package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ClearPipeNetwork {
    public static final String TRAVEL_EXIT = "PipeTravelExit";
    public static final double PIPE_WALL_THICKNESS = 3.0 / 16.0;
    private static final int ROUTE_GRACE_TICKS = 5;
    private static final int NETWORK_LIMIT = 2048;
    private static final int CACHE_TICKS = 100;
    private static final int SPAN_CACHE_TICKS = 20;
    private static final double JUMP_REACH = 1.2;
    private static final int MAX_EXIT_DROP = 4;
    private static final Map<Level, Map<BlockPos, Cached<List<ExitLink>>>> EXIT_CACHE = new WeakHashMap<>();
    private static final Map<Level, Map<BlockPos, Cached<Map<BlockPos, Integer>>>> DISTANCE_CACHE = new WeakHashMap<>();
    private static final Map<Level, Map<BlockPos, Cached<Double>>> SPAN_CACHE = new WeakHashMap<>();

    private record Cached<T>(T value, long expiry) {}

    public record ExitLink(BlockPos exit, BlockPos stand, int distance) {}

    public record PipeStep(BlockPos approach, BlockPos entrance, ExitLink link, boolean jump) {}

    public record PipeEntry(BlockPos entrance, boolean jump) {}

    public static boolean isTravelingInPipe(Entity entity) {
        return entity.getPersistentData().contains(ClearWarpPipeBlock.PIPE_ROUTE_TIME)
                && entity.level().getGameTime() - entity.getPersistentData().getLong(ClearWarpPipeBlock.PIPE_ROUTE_TIME) <= ROUTE_GRACE_TICKS;
    }

    public static boolean isUsableEntrance(BlockState state) {
        return state.getBlock() instanceof ClearWarpPipeBlock && state.getValue(ClearWarpPipeBlock.ENTRANCE)
                && !state.getValue(ClearWarpPipeBlock.CLOSED) && state.getValue(ClearWarpPipeBlock.FACING) != Direction.DOWN;
    }

    public static boolean isOpenEntrance(BlockState state) {
        return state.getBlock() instanceof ClearWarpPipeBlock && state.getValue(ClearWarpPipeBlock.ENTRANCE)
                && !state.getValue(ClearWarpPipeBlock.CLOSED);
    }

    public static boolean isOpenDownEntrance(BlockState state) {
        return ClearPipeNetwork.isOpenEntrance(state) && state.getValue(ClearWarpPipeBlock.FACING) == Direction.DOWN;
    }

    @Nullable
    public static BlockPos findReachableDownPipe(Level level, BlockPos stand, Entity entity, Predicate<BlockState> isPipe) {
        double headTop = stand.getY() + entity.getBbHeight();
        int maxRise = Mth.ceil(entity.getBbHeight() + JUMP_REACH);

        for (int rise = 1; rise <= maxRise; rise++) {
            BlockPos pos = stand.above(rise);
            BlockState state = level.getBlockState(pos);

            if (isPipe.test(state))
                return pos.getY() - headTop <= JUMP_REACH ? pos.immutable() : null;
            if (!state.getCollisionShape(level, pos).isEmpty())
                return null;
        }
        return null;
    }

    @Nullable
    public static PipeEntry resolveEntrance(Level level, BlockPos approach, Entity entity) {
        if (ClearPipeNetwork.isUsableEntrance(level.getBlockState(approach)))
            return new PipeEntry(approach.immutable(), false);

        BlockPos downPipe = ClearPipeNetwork.findReachableDownPipe(level, approach, entity, ClearPipeNetwork::isOpenDownEntrance);
        return downPipe != null ? new PipeEntry(downPipe, true) : null;
    }

    public static boolean fitsEntrance(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!(state.getBlock() instanceof ClearWarpPipeBlock) || !state.getValue(ClearWarpPipeBlock.ENTRANCE))
            return true;
        return entity.getBbWidth() <= ClearPipeNetwork.getEntranceSpan(level, pos, state.getValue(ClearWarpPipeBlock.FACING)) + 1.0E-4;
    }

    public static boolean canEnter(Level level, BlockPos pos, BlockState state, Entity entity) {
        return ClearWarpPipeBlock.isShrunkPose(entity, entity.getPose()) || ClearPipeNetwork.fitsEntrance(level, pos, state, entity);
    }

    public static List<ExitLink> getExitLinks(Level level, BlockPos entrance) {
        Map<BlockPos, Cached<List<ExitLink>>> cache = EXIT_CACHE.computeIfAbsent(level, key -> new HashMap<>());
        Cached<List<ExitLink>> cached = cache.get(entrance);

        if (cached == null || cached.expiry() < level.getGameTime()) {
            cached = new Cached<>(ClearPipeNetwork.computeExitLinks(level, entrance), level.getGameTime() + CACHE_TICKS);
            cache.put(entrance.immutable(), cached);
        }
        return cached.value();
    }

    @Nullable
    public static PipeStep findPipeStep(Level level, @Nullable Path path, Entity entity) {
        if (path == null || path.isDone())
            return null;

        int next = path.getNextNodeIndex();
        for (int index = Math.max(0, next - 2); index <= next + 1 && index + 1 < path.getNodeCount(); index++) {
            Node approachNode = path.getNode(index);
            BlockPos approach = approachNode.asBlockPos();
            PipeEntry entry = ClearPipeNetwork.resolveEntrance(level, approach, entity);

            if (entry == null)
                continue;

            BlockPos stand = path.getNode(index + 1).asBlockPos();
            for (ExitLink link : ClearPipeNetwork.getExitLinks(level, entry.entrance())) {
                if (link.stand().equals(stand))
                    return new PipeStep(approach.immutable(), entry.entrance(), link, entry.jump());
            }
        }
        return null;
    }

    public static void assignTravelExit(Mob mob) {
        if (mob.getPersistentData().contains(TRAVEL_EXIT))
            return;

        PipeStep step = ClearPipeNetwork.findPipeStep(mob.level(), mob.getNavigation().getPath(), mob);
        if (step != null)
            mob.getPersistentData().putLong(TRAVEL_EXIT, step.link().exit().asLong());
    }

    @Nullable
    public static Direction steerAtJunction(Entity entity, BlockPos pos, List<Direction> connections, Direction direction, Direction nextDirection) {
        Map<BlockPos, Integer> distances = ClearPipeNetwork.getExitDistances(entity);
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
    public static Direction steerToExit(Entity entity, BlockPos pos, List<Direction> options) {
        Map<BlockPos, Integer> distances = ClearPipeNetwork.getExitDistances(entity);
        if (distances == null)
            return null;

        Direction best = null;
        int bestDistance = Integer.MAX_VALUE;

        for (Direction option : options) {
            int distance = distances.getOrDefault(pos.relative(option), Integer.MAX_VALUE);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = option;
            }
        }
        return best;
    }

    public static BlockPos getStandPos(Level level, BlockPos exit, BlockState state) {
        Direction facing = state.getValue(ClearWarpPipeBlock.FACING);
        BlockPos stand = exit.relative(facing);

        if (facing == Direction.UP) {
            BlockPos landing = ClearPipeNetwork.findLandingSide(level, exit);
            if (landing != null)
                return landing;
        }

        if (facing == Direction.DOWN) {
            for (int drop = 0; drop < MAX_EXIT_DROP; drop++) {
                BlockPos below = stand.below();
                if (!level.getBlockState(below).getCollisionShape(level, below).isEmpty())
                    break;
                stand = below;
            }
        }
        return stand.immutable();
    }

    @Nullable
    public static BlockPos findLandingSide(Level level, BlockPos exit) {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos beside = exit.relative(direction);

            if (ClearPipeNetwork.isStandable(level, beside.above()))
                return beside.above().immutable();
            if (ClearPipeNetwork.isStandable(level, beside))
                return beside.immutable();
        }
        return null;
    }

    public static Vec3 getEntryPoint(Level level, BlockPos entrance) {
        BlockState state = level.getBlockState(entrance);
        Vec3 center = Vec3.atBottomCenterOf(entrance);

        if (!(state.getBlock() instanceof ClearWarpPipeBlock))
            return center;

        Direction facing = state.getValue(ClearWarpPipeBlock.FACING);
        return facing.getAxis().isHorizontal()
                ? center.add(facing.getStepX() * -0.25, 0.0, facing.getStepZ() * -0.25)
                : center;
    }

    @Nullable
    private static Map<BlockPos, Integer> getExitDistances(Entity entity) {
        if (!entity.getPersistentData().contains(TRAVEL_EXIT))
            return null;

        Level level = entity.level();
        BlockPos exit = BlockPos.of(entity.getPersistentData().getLong(TRAVEL_EXIT));
        Map<BlockPos, Cached<Map<BlockPos, Integer>>> cache = DISTANCE_CACHE.computeIfAbsent(level, key -> new HashMap<>());
        Cached<Map<BlockPos, Integer>> cached = cache.get(exit);

        if (cached == null || cached.expiry() < level.getGameTime()) {
            cached = new Cached<>(ClearPipeNetwork.computeDistances(level, exit), level.getGameTime() + CACHE_TICKS);
            cache.put(exit, cached);
        }
        return cached.value();
    }

    private static List<ExitLink> computeExitLinks(Level level, BlockPos entrance) {
        BlockState entranceState = level.getBlockState(entrance);
        if (!(entranceState.getBlock() instanceof ClearWarpPipeBlock))
            return List.of();

        Set<BlockPos> entranceGroup = ClearPipeNetwork.getEntranceGroup(level, entrance, entranceState.getValue(ClearWarpPipeBlock.FACING));
        List<ExitLink> links = new ArrayList<>();

        for (Map.Entry<BlockPos, Integer> entry : ClearPipeNetwork.computeDistances(level, entrance).entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = level.getBlockState(pos);

            if (!entranceGroup.contains(pos) && ClearPipeNetwork.isOpenEntrance(state))
                links.add(new ExitLink(pos, ClearPipeNetwork.getStandPos(level, pos, state), entry.getValue()));
        }
        return links;
    }

    private static double getEntranceSpan(Level level, BlockPos pos, Direction facing) {
        Map<BlockPos, Cached<Double>> cache = SPAN_CACHE.computeIfAbsent(level, key -> new HashMap<>());
        Cached<Double> cached = cache.get(pos);

        if (cached == null || cached.expiry() < level.getGameTime()) {
            Set<BlockPos> group = ClearPipeNetwork.getEntranceGroup(level, pos, facing);
            double span = Double.MAX_VALUE;

            for (Direction.Axis axis : Direction.Axis.values()) {
                if (axis == facing.getAxis())
                    continue;

                int min = group.stream().mapToInt(groupPos -> groupPos.get(axis)).min().orElse(0);
                int max = group.stream().mapToInt(groupPos -> groupPos.get(axis)).max().orElse(0);
                span = Math.min(span, max - min + 1);
            }

            cached = new Cached<>(span, level.getGameTime() + SPAN_CACHE_TICKS);
            cache.put(pos.immutable(), cached);
        }
        return cached.value();
    }

    private static Set<BlockPos> getEntranceGroup(Level level, BlockPos start, Direction facing) {
        Set<BlockPos> group = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        group.add(start.immutable());
        queue.add(start.immutable());

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

        distances.put(start.immutable(), 0);
        queue.add(start.immutable());

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

    private static boolean isStandable(Level level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && !(level.getBlockState(below).getBlock() instanceof ClearWarpPipeBlock)
                && !level.getBlockState(below).getCollisionShape(level, below).isEmpty();
    }
}
