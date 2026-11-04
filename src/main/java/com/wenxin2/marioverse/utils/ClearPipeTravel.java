package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ClearPipeTravel {
    public static final String TRAVEL_TARGET = "PipeTravelTarget";
    public static final String TRAVEL_EXIT = "PipeTravelExit";
    private static final String TRAVEL_ENTRANCE = "PipeTravelEntrance";
    private static final String TRAVEL_STARTED = "PipeTravelStarted";
    private static final String TRAVEL_SPEED = "PipeTravelSpeed";
    private static final String SHRUNK = "PipeShrunk";
    private static final int ROUTE_GRACE_TICKS = 5;
    private static final int NETWORK_LIMIT = 2048;
    private static final int CACHE_TICKS = 100;
    private static final int VILLAGER_CHECK_INTERVAL = 20;
    private static final int APPROACH_TIMEOUT = 600;
    private static final double MIN_TRAVEL_DISTANCE = 24.0;
    private static final double ENTRANCE_SEARCH_RADIUS = 16.0;
    private static final double PIPE_COST_FACTOR = 0.2;
    private static final double REQUIRED_SAVING = 0.7;
    private static final double PIPE_WALL_THICKNESS = 3.0 / 16.0;
    private static final int OPENING_CACHE_TICKS = 20;
    private static final Map<Level, Map<BlockPos, Cached<List<BlockPos>>>> EXIT_CACHE = new WeakHashMap<>();
    private static final Map<Level, Map<BlockPos, Cached<Double>>> OPENING_CACHE = new WeakHashMap<>();
    private static final Map<Level, Map<BlockPos, Cached<Map<BlockPos, Integer>>>> DISTANCE_CACHE = new WeakHashMap<>();

    private record Cached<T>(T value, long expiry) {}

    public static boolean isShrunk(Entity entity, Pose pose) {
        return pose == Pose.SWIMMING && entity instanceof Mob && !(entity instanceof WaterAnimal);
    }

    public static EntityDimensions getShrunkDimensions(EntityDimensions dimensions) {
        return EntityDimensions.scalable(dimensions.width(), Math.min(dimensions.height(), dimensions.width()));
    }

    public static boolean fitsEntrance(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!(state.getBlock() instanceof ClearWarpPipeBlock) || !state.getValue(ClearWarpPipeBlock.ENTRANCE))
            return true;
        return entity.getBbWidth() <= ClearPipeTravel.getOpeningSize(level, pos, state.getValue(ClearWarpPipeBlock.FACING)) + 1.0E-4;
    }

    public static boolean canEnter(Level level, BlockPos pos, BlockState state, Entity entity) {
        return entity.getPose() == Pose.SWIMMING || ClearPipeTravel.fitsEntrance(level, pos, state, entity);
    }

    public static void shrink(Mob mob) {
        if (mob.level().isClientSide || mob.getPose() == Pose.SWIMMING || mob instanceof WaterAnimal)
            return;

        mob.getPersistentData().putBoolean(SHRUNK, true);
        mob.setPose(Pose.SWIMMING);
    }

    public static void recordTarget(Entity entity) {
        if (!(entity instanceof Mob mob) || mob.getPersistentData().contains(TRAVEL_TARGET))
            return;

        BlockPos target = mob.getNavigation().getTargetPos();
        if (target != null)
            mob.getPersistentData().putLong(TRAVEL_TARGET, target.asLong());
    }

    public static void tick(Mob mob) {
        CompoundTag tag = mob.getPersistentData();
        Level level = mob.level();
        long gameTime = level.getGameTime();
        boolean inRoute = tag.contains(ClearWarpPipeBlock.PIPE_ROUTE_TIME)
                && gameTime - tag.getLong(ClearWarpPipeBlock.PIPE_ROUTE_TIME) <= ROUTE_GRACE_TICKS;

        if (tag.getBoolean(SHRUNK) && !inRoute) {
            tag.remove(SHRUNK);
            if (mob.getPose() == Pose.SWIMMING)
                mob.setPose(Pose.STANDING);
            ClearPipeTravel.finishTravel(mob);
        }

        if (mob instanceof Villager villager && (villager.tickCount + villager.getId()) % VILLAGER_CHECK_INTERVAL == 0)
            ClearPipeTravel.tickVillager(villager, inRoute);
    }

    @Nullable
    public static Direction steerAtJunction(Entity entity, BlockPos pos, List<Direction> connections, Direction direction, Direction nextDirection) {
        Map<BlockPos, Integer> distances = ClearPipeTravel.getExitDistances(entity);
        if (distances == null || !distances.containsKey(pos))
            return null;

        Direction best = nextDirection;
        int bestDistance = distances.getOrDefault(pos.relative(nextDirection), Integer.MAX_VALUE);

        for (Direction connected : connections) {
            if (connected == direction.getOpposite())
                continue;

            int distance = distances.getOrDefault(pos.relative(connected), Integer.MAX_VALUE);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = connected;
            }
        }
        return bestDistance == Integer.MAX_VALUE ? null : best;
    }

    @Nullable
    public static Direction steerTowardTarget(Entity entity, BlockPos pos, List<Direction> options) {
        Map<BlockPos, Integer> distances = ClearPipeTravel.getExitDistances(entity);
        if (distances != null) {
            Direction best = null;
            int bestDistance = Integer.MAX_VALUE;

            for (Direction option : options) {
                int distance = distances.getOrDefault(pos.relative(option), Integer.MAX_VALUE);
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = option;
                }
            }

            if (best != null)
                return best;
        }

        if (!(entity instanceof Mob) || !entity.getPersistentData().contains(TRAVEL_TARGET))
            return null;

        Vec3 target = Vec3.atCenterOf(BlockPos.of(entity.getPersistentData().getLong(TRAVEL_TARGET)));
        if (SableProvider.isLoaded() && SableProvider.isPlotPos(entity.level(), pos)) {
            target = SableProvider.toLocalVec(entity.level(), pos, target);
            if (target == null)
                return null;
        }

        Direction best = null;
        double bestDistance = Double.MAX_VALUE;

        for (Direction option : options) {
            double distance = Vec3.atCenterOf(pos.relative(option)).distanceToSqr(target);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = option;
            }
        }
        return best;
    }

    private static void tickVillager(Villager villager, boolean inRoute) {
        CompoundTag tag = villager.getPersistentData();
        Level level = villager.level();
        Brain<Villager> brain = villager.getBrain();

        if (inRoute) {
            tag.remove(TRAVEL_ENTRANCE);
            return;
        }

        if (tag.contains(TRAVEL_ENTRANCE)) {
            BlockPos entrance = BlockPos.of(tag.getLong(TRAVEL_ENTRANCE));

            if (level.getGameTime() - tag.getLong(TRAVEL_STARTED) > APPROACH_TIMEOUT
                    || !ClearPipeTravel.isUsableEntrance(level.getBlockState(entrance))) {
                ClearPipeTravel.clearTravel(tag);
                return;
            }

            brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(entrance, tag.getFloat(TRAVEL_SPEED), 0));
            return;
        }

        if (villager.isSleeping() || villager.isPassenger())
            return;

        Optional<WalkTarget> walkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET);
        if (walkTarget.isEmpty())
            return;

        BlockPos target = walkTarget.get().getTarget().currentBlockPosition();
        double directDistance = Math.sqrt(villager.distanceToSqr(Vec3.atCenterOf(target)));
        if (directDistance < MIN_TRAVEL_DISTANCE)
            return;

        Set<BlockPos> warpBlocks = BaseWarpBlockEntity.LOADED_WARP_BLOCKS.get(level);
        if (warpBlocks == null)
            return;

        BlockPos bestEntrance = null;
        BlockPos bestExit = null;
        double bestCost = directDistance * REQUIRED_SAVING;

        for (BlockPos entrance : List.copyOf(warpBlocks)) {
            BlockState entranceState = level.getBlockState(entrance);
            if (villager.distanceToSqr(Vec3.atCenterOf(entrance)) > ENTRANCE_SEARCH_RADIUS * ENTRANCE_SEARCH_RADIUS
                    || !ClearPipeTravel.isUsableEntrance(entranceState)
                    || !ClearPipeTravel.fitsEntrance(level, entrance, entranceState, villager))
                continue;

            double approach = Math.sqrt(villager.distanceToSqr(Vec3.atCenterOf(entrance)));
            for (BlockPos exit : ClearPipeTravel.getExits(level, entrance)) {
                double cost = approach + Math.sqrt(exit.distSqr(target)) + Math.sqrt(entrance.distSqr(exit)) * PIPE_COST_FACTOR;

                if (cost < bestCost) {
                    bestCost = cost;
                    bestEntrance = entrance;
                    bestExit = exit;
                }
            }
        }

        if (bestEntrance == null)
            return;

        float speed = walkTarget.get().getSpeedModifier();
        tag.putLong(TRAVEL_TARGET, target.asLong());
        tag.putLong(TRAVEL_EXIT, bestExit.asLong());
        tag.putLong(TRAVEL_ENTRANCE, bestEntrance.asLong());
        tag.putLong(TRAVEL_STARTED, level.getGameTime());
        tag.putFloat(TRAVEL_SPEED, speed);
        brain.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(bestEntrance, speed, 0));
    }

    private static void finishTravel(Mob mob) {
        CompoundTag tag = mob.getPersistentData();

        if (mob instanceof Villager villager && tag.contains(TRAVEL_TARGET) && tag.contains(TRAVEL_EXIT))
            villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(BlockPos.of(tag.getLong(TRAVEL_TARGET)), tag.getFloat(TRAVEL_SPEED), 1));
        ClearPipeTravel.clearTravel(tag);
    }

    private static void clearTravel(CompoundTag tag) {
        tag.remove(TRAVEL_TARGET);
        tag.remove(TRAVEL_EXIT);
        tag.remove(TRAVEL_ENTRANCE);
        tag.remove(TRAVEL_STARTED);
        tag.remove(TRAVEL_SPEED);
    }

    private static double getOpeningSize(Level level, BlockPos pos, Direction facing) {
        Map<BlockPos, Cached<Double>> cache = OPENING_CACHE.computeIfAbsent(level, key -> new HashMap<>());
        Cached<Double> cached = cache.get(pos);

        if (cached == null || cached.expiry() < level.getGameTime()) {
            Set<BlockPos> group = ClearPipeTravel.getEntranceGroup(level, pos, facing);
            Direction.Axis[] axes = Direction.Axis.values();
            double opening = Double.MAX_VALUE;

            for (Direction.Axis axis : axes) {
                if (axis == facing.getAxis())
                    continue;

                int min = group.stream().mapToInt(groupPos -> groupPos.get(axis)).min().orElse(0);
                int max = group.stream().mapToInt(groupPos -> groupPos.get(axis)).max().orElse(0);
                opening = Math.min(opening, max - min + 1 - PIPE_WALL_THICKNESS * 2.0);
            }

            cached = new Cached<>(opening, level.getGameTime() + OPENING_CACHE_TICKS);
            cache.put(pos.immutable(), cached);
        }
        return cached.value();
    }

    private static boolean isUsableEntrance(BlockState state) {
        return state.getBlock() instanceof ClearWarpPipeBlock && state.getValue(ClearWarpPipeBlock.ENTRANCE)
                && !state.getValue(ClearWarpPipeBlock.CLOSED) && state.getValue(ClearWarpPipeBlock.FACING) != Direction.DOWN;
    }

    @Nullable
    private static Map<BlockPos, Integer> getExitDistances(Entity entity) {
        if (!(entity instanceof Mob) || !entity.getPersistentData().contains(TRAVEL_EXIT))
            return null;

        Level level = entity.level();
        BlockPos exit = BlockPos.of(entity.getPersistentData().getLong(TRAVEL_EXIT));
        Map<BlockPos, Cached<Map<BlockPos, Integer>>> cache = DISTANCE_CACHE.computeIfAbsent(level, key -> new HashMap<>());
        Cached<Map<BlockPos, Integer>> cached = cache.get(exit);

        if (cached == null || cached.expiry() < level.getGameTime()) {
            cached = new Cached<>(ClearPipeTravel.computeDistances(level, exit), level.getGameTime() + CACHE_TICKS);
            cache.put(exit, cached);
        }
        return cached.value();
    }

    private static List<BlockPos> getExits(Level level, BlockPos entrance) {
        Map<BlockPos, Cached<List<BlockPos>>> cache = EXIT_CACHE.computeIfAbsent(level, key -> new HashMap<>());
        Cached<List<BlockPos>> cached = cache.get(entrance);

        if (cached == null || cached.expiry() < level.getGameTime()) {
            cached = new Cached<>(ClearPipeTravel.computeExits(level, entrance), level.getGameTime() + CACHE_TICKS);
            cache.put(entrance, cached);
        }
        return cached.value();
    }

    private static List<BlockPos> computeExits(Level level, BlockPos entrance) {
        BlockState entranceState = level.getBlockState(entrance);
        if (!(entranceState.getBlock() instanceof ClearWarpPipeBlock))
            return List.of();

        Set<BlockPos> entranceGroup = ClearPipeTravel.getEntranceGroup(level, entrance, entranceState.getValue(ClearWarpPipeBlock.FACING));
        List<BlockPos> exits = new ArrayList<>();

        for (BlockPos pos : ClearPipeTravel.computeDistances(level, entrance).keySet()) {
            BlockState state = level.getBlockState(pos);
            if (!entranceGroup.contains(pos) && state.getValue(ClearWarpPipeBlock.ENTRANCE) && !state.getValue(ClearWarpPipeBlock.CLOSED))
                exits.add(pos);
        }
        return exits;
    }

    private static Set<BlockPos> getEntranceGroup(Level level, BlockPos start, Direction facing) {
        Set<BlockPos> group = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        group.add(start);
        queue.add(start);

        while (!queue.isEmpty() && group.size() < NETWORK_LIMIT) {
            BlockPos pos = queue.poll();

            for (Direction direction : Direction.values()) {
                if (direction.getAxis() == facing.getAxis())
                    continue;

                BlockPos neighbor = pos.relative(direction);
                BlockState state = level.getBlockState(neighbor);

                if (!group.contains(neighbor) && state.getBlock() instanceof ClearWarpPipeBlock && state.getValue(ClearWarpPipeBlock.ENTRANCE)
                        && state.getValue(ClearWarpPipeBlock.FACING) == facing) {
                    group.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
        return group;
    }

    private static Map<BlockPos, Integer> computeDistances(Level level, BlockPos start) {
        Map<BlockPos, Integer> distances = new HashMap<>();
        Deque<BlockPos> queue = new ArrayDeque<>();

        if (!(level.getBlockState(start).getBlock() instanceof ClearWarpPipeBlock))
            return distances;

        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty() && distances.size() < NETWORK_LIMIT) {
            BlockPos pos = queue.poll();
            BlockState state = level.getBlockState(pos);
            int distance = distances.get(pos);

            for (Direction direction : Direction.values()) {
                if (!state.getValue(ClearWarpPipeBlock.PROPERTY_BY_DIRECTION.get(direction)))
                    continue;

                BlockPos neighbor = pos.relative(direction);
                if (!distances.containsKey(neighbor) && level.isLoaded(neighbor)
                        && level.getBlockState(neighbor).getBlock() instanceof ClearWarpPipeBlock) {
                    distances.put(neighbor, distance + 1);
                    queue.add(neighbor);
                }
            }
        }
        return distances;
    }
}
