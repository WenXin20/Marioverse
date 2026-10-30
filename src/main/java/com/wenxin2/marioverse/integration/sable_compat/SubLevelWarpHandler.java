package com.wenxin2.marioverse.integration.sable_compat;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.utils.WarpCost;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

public class SubLevelWarpHandler {
    private static final double MIN_ENTRY_SPEED = 0.25;
    private static final double EXIT_CLEARANCE = 1.0;
    private static final Map<UUID, Long> WARP_COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Vector3d> LAST_VELOCITIES = new HashMap<>();
    private static final Map<UUID, PendingLoad> PENDING_LOADS = new HashMap<>();
    private static final int PENDING_LOAD_TIMEOUT = 300;

    private record WarpPoint(Vec3 center, Vec3 normal) {}

    private record EntryMotion(Vec3 direction, Vector3d velocity) {}

    private record PendingLoad(BlockPos entrancePos, Vector3d velocity, long deadline) {}

    public static void tick(ServerSubLevelContainer container) {
        ServerLevel level = container.getLevel();

        if (!ConfigRegistry.TELEPORT_CONTRAPTIONS.get() || level.getGameTime() % 2 != 0 || container.getLoadedCount() == 0)
            return;

        Set<BlockPos> loadedWarpBlocks = BaseWarpBlockEntity.LOADED_WARP_BLOCKS.get(level);
        List<BlockPos> warpBlocks = loadedWarpBlocks == null ? List.of() : new ArrayList<>(loadedWarpBlocks);

        if (level.getGameTime() % 100 == 0) {
            Set<UUID> loadedSubLevels = new HashSet<>();
            for (ServerSubLevel sub : container.getAllSubLevels())
                loadedSubLevels.add(sub.getUniqueId());
            LAST_VELOCITIES.keySet().retainAll(loadedSubLevels);
            PENDING_LOADS.keySet().retainAll(loadedSubLevels);
        }

        for (ServerSubLevel sub : new ArrayList<>(container.getAllSubLevels())) {
            if (sub.isRemoved())
                continue;

            RigidBodyHandle handle = RigidBodyHandle.of(sub);
            if (handle == null || !handle.isValid())
                continue;

            Vector3d currentVelocity = handle.getLinearVelocity(new Vector3d());
            Vector3d previousVelocity = LAST_VELOCITIES.put(sub.getUniqueId(), new Vector3d(currentVelocity));
            List<Vector3d> velocities = new ArrayList<>(List.of(currentVelocity));
            if (previousVelocity != null)
                velocities.add(previousVelocity);

            Long cooldownEnd = WARP_COOLDOWNS.get(sub.getUniqueId());
            if (cooldownEnd != null) {
                if (level.getGameTime() < cooldownEnd)
                    continue;
                WARP_COOLDOWNS.remove(sub.getUniqueId());
            }

            AABB bounds = SubLevelWarpHandler.toAabb(sub.boundingBox()).inflate(1.0);

            if (!SubLevelWarpHandler.tryWarpThroughBlocks(level, sub, bounds, warpBlocks, velocities))
                SubLevelWarpHandler.tryWarpThroughPaintings(level, sub, bounds, velocities);
        }
    }

    private static boolean tryWarpThroughBlocks(ServerLevel level, ServerSubLevel sub, AABB bounds, List<BlockPos> warpBlocks,
                                                List<Vector3d> velocities) {
        for (BlockPos pos : warpBlocks) {
            if (!(level.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE) || warpBE.preventWarp || !warpBE.hasDestinationPos()
                    || SubLevelWarpHandler.isOnSubLevel(level, pos, sub))
                continue;

            BlockState state = level.getBlockState(pos);
            WarpPoint entrance = SubLevelWarpHandler.getBlockWarpPoint(level, pos, state, true);

            if (entrance == null || !bounds.contains(entrance.center()))
                continue;

            boolean isPipe = state.getBlock() instanceof WarpPipeBlock;
            if (!SubLevelWarpHandler.isTouching(sub, entrance, !isPipe))
                continue;

            List<Vector3d> entryVelocities = new ArrayList<>(velocities);
            PendingLoad pendingLoad = PENDING_LOADS.get(sub.getUniqueId());
            if (pendingLoad != null && pendingLoad.entrancePos().equals(pos) && level.getGameTime() <= pendingLoad.deadline())
                entryVelocities.add(pendingLoad.velocity());

            EntryMotion motion = SubLevelWarpHandler.getEntryMotion(level, pos, entrance, isPipe, entryVelocities);
            if (motion == null)
                continue;

            WarpPoint exit = SubLevelWarpHandler.getBlockDestination(level, warpBE, sub);
            if (exit == null && warpBE.destinationSubLevel != null && warpBE.destinationSubLevelPos != null
                    && !warpBE.destinationSubLevel.equals(sub.getUniqueId())
                    && SableProvider.resolveSubLevelPos(level, warpBE.destinationSubLevel, warpBE.destinationSubLevelPos) == null
                    && SableProvider.requestSubLevelLoad(level, warpBE.destinationSubLevel)) {
                if (pendingLoad == null || !pendingLoad.entrancePos().equals(pos))
                    PENDING_LOADS.put(sub.getUniqueId(), new PendingLoad(pos.immutable(), new Vector3d(motion.velocity()),
                            level.getGameTime() + PENDING_LOAD_TIMEOUT));
                continue;
            }

            if (exit == null)
                continue;

            List<Player> passengers = SubLevelWarpHandler.getPassengers(level, bounds);
            WarpCost.Cost warpCost = WarpCost.getBlockCost(entrance.center().distanceTo(exit.center()), state);

            if (!WarpCost.canAfford(passengers, warpCost))
                continue;

            if (!SubLevelWarpHandler.warp(sub, entrance, motion, exit))
                continue;

            WarpCost.charge(passengers, warpCost);
            PENDING_LOADS.remove(sub.getUniqueId());

            int cooldown = isPipe ? ConfigRegistry.WARP_PIPE_COOLDOWN.get()
                    : state.getBlock() instanceof DoorBlock ? ConfigRegistry.WARP_DOOR_COOLDOWN.get()
                    : ConfigRegistry.WARP_TRAPDOOR_COOLDOWN.get();
            WARP_COOLDOWNS.put(sub.getUniqueId(), level.getGameTime() + cooldown);
            return true;
        }
        return false;
    }

    private static void tryWarpThroughPaintings(ServerLevel level, ServerSubLevel sub, AABB bounds, List<Vector3d> velocities) {
        List<Entity> warpEntities = level.getEntities((Entity) null, bounds, entity -> entity instanceof BlockAttachedEntity
                && entity instanceof WarpLinkableEntity linkable && linkable.mv$getWarpUUID() != null
                && !entity.getData(DataAttachmentRegistry.PREVENT_WARP));

        for (Entity warpEntity : warpEntities) {
            if (SubLevelWarpHandler.isOnSubLevel(level, warpEntity.blockPosition(), sub))
                continue;

            WarpPoint entrance = SubLevelWarpHandler.getEntityWarpPoint(level, warpEntity);
            if (entrance == null || !SubLevelWarpHandler.isTouching(sub, entrance, false))
                continue;

            EntryMotion motion = SubLevelWarpHandler.getEntryMotion(level, warpEntity.blockPosition(), entrance, true, velocities);
            if (motion == null)
                continue;

            Entity destination = level.getEntity(((WarpLinkableEntity) warpEntity).mv$getWarpUUID());
            if (destination == null || SubLevelWarpHandler.isOnSubLevel(level, destination.blockPosition(), sub))
                continue;

            WarpPoint exit = SubLevelWarpHandler.getEntityWarpPoint(level, destination);
            if (exit == null)
                continue;

            List<Player> passengers = SubLevelWarpHandler.getPassengers(level, bounds);
            WarpCost.Cost warpCost = WarpCost.getEntityCost(entrance.center().distanceTo(exit.center()), true);

            if (!WarpCost.canAfford(passengers, warpCost) || !SubLevelWarpHandler.warp(sub, entrance, motion, exit))
                continue;

            WarpCost.charge(passengers, warpCost);

            WARP_COOLDOWNS.put(sub.getUniqueId(), level.getGameTime() + ConfigRegistry.WARP_PAINTING_COOLDOWN.get());
            return;
        }
    }

    @Nullable
    private static WarpPoint getBlockDestination(ServerLevel level, BaseWarpBlockEntity warpBE, ServerSubLevel sub) {
        BlockPos destination = warpBE.destinationPos;

        if (warpBE.destinationSubLevel != null && warpBE.destinationSubLevelPos != null) {
            if (warpBE.destinationSubLevel.equals(sub.getUniqueId()))
                return null;
            destination = SableProvider.resolveSubLevelPos(level, warpBE.destinationSubLevel, warpBE.destinationSubLevelPos);
        }

        if ((destination == null || !(level.getBlockEntity(destination) instanceof BaseWarpBlockEntity)) && warpBE.getUUID() != null) {
            destination = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID());

            if (destination != null && level.getBlockEntity(destination) instanceof BaseWarpBlockEntity) {
                warpBE.destinationPos = destination;
                warpBE.updateDestinationSubLevel(destination);
                warpBE.setChanged();
            }
        }

        if (destination == null || !(level.getBlockEntity(destination) instanceof BaseWarpBlockEntity)
                || SubLevelWarpHandler.isOnSubLevel(level, destination, sub))
            return null;
        return SubLevelWarpHandler.getBlockWarpPoint(level, destination, level.getBlockState(destination), false);
    }

    @Nullable
    private static WarpPoint getBlockWarpPoint(Level level, BlockPos pos, BlockState state, boolean requireEntrance) {
        Direction facing;
        Vec3 center = Vec3.atCenterOf(pos);

        if (state.getBlock() instanceof WarpPipeBlock) {
            if (requireEntrance && (!state.getValue(WarpPipeBlock.ENTRANCE) || state.getValue(WarpPipeBlock.CLOSED)))
                return null;
            facing = state.getValue(WarpPipeBlock.FACING);
            if (!requireEntrance) {
                List<BlockPos> entranceGroup = WarpPipeBlock.getEntranceGroup(level, pos, state);
                if (entranceGroup.size() > 1)
                    center = new Vec3(entranceGroup.stream().mapToInt(BlockPos::getX).average().orElse(pos.getX()) + 0.5, center.y,
                            entranceGroup.stream().mapToInt(BlockPos::getZ).average().orElse(pos.getZ()) + 0.5);
            }
            center = center.add(facing.getStepX() * 0.5, facing.getStepY() * 0.5, facing.getStepZ() * 0.5);
        } else if (state.getBlock() instanceof DoorBlock)
            facing = state.getValue(DoorBlock.FACING);
        else if (state.getBlock() instanceof TrapDoorBlock)
            facing = state.getValue(TrapDoorBlock.FACING);
        else return null;

        return SubLevelWarpHandler.toWorldWarpPoint(level, center, facing);
    }

    @Nullable
    private static WarpPoint getEntityWarpPoint(Level level, Entity entity) {
        return SubLevelWarpHandler.toWorldWarpPoint(level, entity.position(), entity.getDirection());
    }

    @Nullable
    private static WarpPoint toWorldWarpPoint(Level level, Vec3 center, Direction facing) {
        Vec3 worldCenter = SableProvider.toWorldVec(level, center);
        Vec3 worldFacing = SableProvider.toWorldVec(level, center.add(facing.getStepX(), facing.getStepY(), facing.getStepZ()));

        if (worldCenter == null || worldFacing == null)
            return null;
        return new WarpPoint(worldCenter, worldFacing.subtract(worldCenter).normalize());
    }

    private static List<Player> getPassengers(ServerLevel level, AABB bounds) {
        List<Player> passengers = new ArrayList<>();

        for (Player player : level.players()) {
            if (!player.isSpectator() && bounds.intersects(player.getBoundingBox()))
                passengers.add(player);
        }
        return passengers;
    }

    private static boolean isOnSubLevel(Level level, BlockPos pos, ServerSubLevel sub) {
        SableProvider.SubLevelAnchor anchor = SableProvider.getAnchor(level, pos);
        return anchor != null && anchor.subLevel().equals(sub.getUniqueId());
    }

    private static boolean isTouching(ServerSubLevel sub, WarpPoint point, boolean checkBothSides) {
        double[] offsets = checkBothSides ? new double[] {0.3, 0.8, -0.3, -0.8} : new double[] {0.3, 0.8};

        for (double offset : offsets) {
            Vec3 sample = point.center().add(point.normal().scale(offset));
            Vector3d plotPos = sub.logicalPose().transformPositionInverse(new Vector3d(sample.x, sample.y, sample.z));

            if (sub.getPlot().contains(plotPos)
                    && !sub.getLevel().getBlockState(BlockPos.containing(plotPos.x, plotPos.y, plotPos.z)).isAir())
                return true;
        }
        return false;
    }

    @Nullable
    private static EntryMotion getEntryMotion(ServerLevel level, BlockPos entrancePos, WarpPoint entrance, boolean requireMotion,
                                              List<Vector3d> velocities) {
        Vector3d entranceVelocity = new Vector3d();
        SableProvider.SubLevelAnchor entranceAnchor = SableProvider.getAnchor(level, entrancePos);

        if (entranceAnchor != null && SubLevelContainer.getContainer(level).getSubLevel(entranceAnchor.subLevel()) instanceof ServerSubLevel entranceSub) {
            RigidBodyHandle entranceHandle = RigidBodyHandle.of(entranceSub);
            if (entranceHandle != null && entranceHandle.isValid())
                entranceHandle.getLinearVelocity(entranceVelocity);
        }

        Vector3d bestVelocity = null;
        double bestSpeed = -Double.MAX_VALUE;

        for (Vector3d velocity : velocities) {
            Vector3d relativeVelocity = new Vector3d(velocity).sub(entranceVelocity);
            double speedIntoEntrance = -(relativeVelocity.x * entrance.normal().x + relativeVelocity.y * entrance.normal().y
                    + relativeVelocity.z * entrance.normal().z);

            if (speedIntoEntrance > bestSpeed) {
                bestSpeed = speedIntoEntrance;
                bestVelocity = velocity;
            }
        }

        if (bestVelocity == null)
            return null;
        if (requireMotion)
            return bestSpeed >= MIN_ENTRY_SPEED ? new EntryMotion(entrance.normal().scale(-1.0), bestVelocity) : null;
        if (bestVelocity.lengthSquared() > MIN_ENTRY_SPEED * MIN_ENTRY_SPEED)
            return new EntryMotion(new Vec3(bestVelocity.x, bestVelocity.y, bestVelocity.z).normalize(), bestVelocity);
        return new EntryMotion(entrance.normal().scale(-1.0), bestVelocity);
    }

    private static boolean warp(ServerSubLevel sub, WarpPoint entrance, EntryMotion motion, WarpPoint exit) {
        RigidBodyHandle handle = RigidBodyHandle.of(sub);
        if (handle == null || !handle.isValid())
            return false;

        Vector3d velocity = new Vector3d(motion.velocity());
        Vector3d angularVelocity = handle.getAngularVelocity(new Vector3d());
        Vector3d in = new Vector3d(motion.direction().x, motion.direction().y, motion.direction().z).normalize();
        Vector3d out = new Vector3d(exit.normal().x, exit.normal().y, exit.normal().z).normalize();
        Vector3d entranceCenter = new Vector3d(entrance.center().x, entrance.center().y, entrance.center().z);
        boolean isHorizontalWarp = Math.abs(in.y) < 0.5 && Math.abs(out.y) < 0.5;
        Quaterniond rotation = isHorizontalWarp
                ? new Quaterniond().rotationY(Math.atan2(out.x, out.z) - Math.atan2(in.x, in.z)) : new Quaterniond();
        Pose3d pose = sub.logicalPose();

        double minExitProjection = Double.MAX_VALUE;
        BoundingBox3dc bounds = sub.boundingBox();
        for (int corner = 0; corner < 8; corner++) {
            Vector3d cornerPos = new Vector3d((corner & 1) == 0 ? bounds.minX() : bounds.maxX(),
                    (corner & 2) == 0 ? bounds.minY() : bounds.maxY(), (corner & 4) == 0 ? bounds.minZ() : bounds.maxZ());
            minExitProjection = Math.min(minExitProjection, rotation.transform(cornerPos.sub(entranceCenter)).dot(out));
        }

        Vector3d offset = rotation.transform(new Vector3d(pose.position()).sub(entranceCenter));
        Vector3d newPosition = new Vector3d(exit.center().x, exit.center().y, exit.center().z)
                .add(new Vector3d(out).mul(EXIT_CLEARANCE - minExitProjection)).add(offset);
        Quaterniond newOrientation = new Quaterniond(rotation).mul(pose.orientation());

        handle.teleport(newPosition, newOrientation);

        if (isHorizontalWarp) {
            rotation.transform(velocity);
            rotation.transform(angularVelocity);
        } else velocity = new Vector3d(out).mul(Math.max(velocity.length(), MIN_ENTRY_SPEED));

        handle.addLinearAndAngularVelocity(velocity.sub(handle.getLinearVelocity(new Vector3d())),
                angularVelocity.sub(handle.getAngularVelocity(new Vector3d())));
        return true;
    }

    private static AABB toAabb(BoundingBox3dc bounds) {
        return new AABB(bounds.minX(), bounds.minY(), bounds.minZ(), bounds.maxX(), bounds.maxY(), bounds.maxZ());
    }
}
