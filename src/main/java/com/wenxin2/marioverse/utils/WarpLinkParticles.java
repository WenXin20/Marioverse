package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.particles.WarpLinkParticleOption;
import com.wenxin2.marioverse.particles.WarpStarParticleOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WarpLinkParticles {
    private static final double[] EDGE_STEPS = {-0.5, -0.25, 0.0, 0.25, 0.5};
    private static final double FACE_OFFSET = 0.51;
    private static final int MAX_VIBRATION_TICKS = 40;
    private static final int SWIRL_PARTICLES = 16;
    private static final int MAX_SWIRL_PARTICLES = 64;
    private static final int PUFF_PARTICLES = 14;
    private static final int MAX_PUFF_PARTICLES = 48;
    private static final double MIN_SWIRL_RADIUS = 0.8;
    private static final float SWIRL_SPIN = 0.45F;
    private static final float PUFF_SPIN = 0.12F;

    private record WarpFace(Vec3 center, Vec3 normal, double radius) {}

    public static void outlineWarp(ServerLevel level, BlockPos pos, @Nullable ServerPlayer viewer) {
        BlockState state = level.getBlockState(pos);
        List<BlockPos> blocks;
        Direction facing;

        if (state.getBlock() instanceof WarpPipeBlock) {
            facing = state.getValue(WarpPipeBlock.FACING);
            blocks = state.getBlock() instanceof ClearWarpPipeBlock ? List.of(pos) : WarpPipeBlock.getEntranceGroup(level, pos, state);
            if (blocks.isEmpty())
                blocks = List.of(pos);
        } else if (state.getBlock() instanceof DoorBlock) {
            facing = state.getValue(DoorBlock.FACING);
            BlockPos lowerPos = state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
            blocks = List.of(lowerPos, lowerPos.above());
        } else {
            facing = Direction.UP;
            blocks = List.of(pos);
        }

        Set<BlockPos> group = new HashSet<>(blocks);

        for (BlockPos block : blocks) {
            Vec3 faceCenter = Vec3.atCenterOf(block).add(facing.getStepX() * FACE_OFFSET, facing.getStepY() * FACE_OFFSET,
                    facing.getStepZ() * FACE_OFFSET);

            for (Direction side : Direction.values()) {
                if (side.getAxis() == facing.getAxis() || group.contains(block.relative(side)))
                    continue;

                Direction.Axis edgeAxis = WarpLinkParticles.getRemainingAxis(facing.getAxis(), side.getAxis());
                Vec3 edgeCenter = faceCenter.add(side.getStepX() * 0.5, side.getStepY() * 0.5, side.getStepZ() * 0.5);

                for (double step : EDGE_STEPS)
                    WarpLinkParticles.sendOutlineParticle(level, viewer, edgeCenter.relative(Direction.fromAxisAndDirection(edgeAxis,
                            Direction.AxisDirection.POSITIVE), step));
            }
        }
    }

    public static void outlineEntity(ServerLevel level, Entity entity, @Nullable ServerPlayer viewer) {
        AABB bounds = entity.getBoundingBox();
        double[] xs = {bounds.minX, bounds.maxX};
        double[] ys = {bounds.minY, bounds.maxY};
        double[] zs = {bounds.minZ, bounds.maxZ};

        for (double step = 0.0; step <= 1.0; step += 0.25) {
            double x = bounds.minX + (bounds.maxX - bounds.minX) * step;
            double y = bounds.minY + (bounds.maxY - bounds.minY) * step;
            double z = bounds.minZ + (bounds.maxZ - bounds.minZ) * step;

            for (double edgeA : ys)
                for (double edgeB : zs)
                    WarpLinkParticles.sendOutlineParticle(level, viewer, new Vec3(x, edgeA, edgeB));
            for (double edgeA : xs)
                for (double edgeB : zs)
                    WarpLinkParticles.sendOutlineParticle(level, viewer, new Vec3(edgeA, y, edgeB));
            for (double edgeA : xs)
                for (double edgeB : ys)
                    WarpLinkParticles.sendOutlineParticle(level, viewer, new Vec3(edgeA, edgeB, z));
        }
    }

    public static void sendLinkVibration(ServerLevel level, Vec3 from, Vec3 to) {
        Vec3 worldFrom = WarpLinkParticles.toWorld(level, from);
        Vec3 worldTo = WarpLinkParticles.toWorld(level, to);

        if (worldFrom == null || worldTo == null)
            return;

        int arrivalTicks = Math.max(1, Math.min(MAX_VIBRATION_TICKS, (int) worldFrom.distanceTo(worldTo)));
        level.sendParticles(new WarpLinkParticleOption(new BlockPositionSource(BlockPos.containing(worldTo)), arrivalTicks),
                worldFrom.x, worldFrom.y, worldFrom.z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    public static void spawnWarpTransition(ServerLevel level, BlockPos from, BlockState fromState, BlockPos to, BlockState toState) {
        WarpFace entrance = WarpLinkParticles.getWarpFace(level, from, fromState);
        WarpFace exit = WarpLinkParticles.getWarpFace(level, to, toState);

        if (entrance != null)
            WarpLinkParticles.spawnSwirl(level, entrance);
        if (exit != null)
            WarpLinkParticles.spawnPuff(level, exit);
    }

    public static void spawnEntityWarpTransition(ServerLevel level, Entity entered, Entity traveler) {
        Direction facing = entered.getDirection();
        AABB bounds = entered.getBoundingBox();
        Vec3 center = bounds.getCenter();
        Vec3 worldCenter = WarpLinkParticles.toWorld(level, center);
        Vec3 worldFacing = WarpLinkParticles.toWorld(level, center.add(facing.getStepX(), facing.getStepY(), facing.getStepZ()));

        if (worldCenter != null && worldFacing != null)
            WarpLinkParticles.spawnSwirl(level, new WarpFace(worldCenter, worldFacing.subtract(worldCenter).normalize(),
                    Math.max(bounds.getXsize(), Math.max(bounds.getYsize(), bounds.getZsize())) * 0.5));

        WarpLinkParticles.spawnPuff(level, new WarpFace(traveler.position().add(0.0, traveler.getBbHeight() * 0.5, 0.0),
                Vec3.directionFromRotation(0.0F, traveler.getYRot()), Math.max(traveler.getBbWidth(), 0.5)));
    }

    private static void spawnSwirl(ServerLevel level, WarpFace face) {
        Vec3[] basis = WarpLinkParticles.getFaceBasis(face.normal());
        double ringRadius = Math.max(MIN_SWIRL_RADIUS, face.radius() * 1.1);
        double scale = ringRadius / MIN_SWIRL_RADIUS;
        int count = Math.min(MAX_SWIRL_PARTICLES, (int) Math.round(SWIRL_PARTICLES * scale));
        WarpStarParticleOption option = WarpLinkParticles.getStarOption(face.normal(), SWIRL_SPIN);

        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2.0 * i / count;
            Vec3 radial = basis[0].scale(Math.cos(angle)).add(basis[1].scale(Math.sin(angle)));
            Vec3 tangent = face.normal().cross(radial);
            Vec3 pos = face.center().add(radial.scale(ringRadius)).add(face.normal().scale(0.3));
            Vec3 velocity = radial.scale(-0.12 * scale).add(tangent.scale(0.1 * scale)).add(face.normal().scale(-0.03));

            level.sendParticles(option, pos.x, pos.y, pos.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
        }
    }

    private static void spawnPuff(ServerLevel level, WarpFace face) {
        Vec3[] basis = WarpLinkParticles.getFaceBasis(face.normal());
        RandomSource random = level.getRandom();
        double scale = Math.max(1.0, face.radius() / 0.5);
        int count = Math.min(MAX_PUFF_PARTICLES, (int) Math.round(PUFF_PARTICLES * scale));
        WarpStarParticleOption option = WarpLinkParticles.getStarOption(face.normal(), PUFF_SPIN);

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = Math.sqrt(random.nextDouble()) * face.radius() * 0.7;
            Vec3 radial = basis[0].scale(Math.cos(angle)).add(basis[1].scale(Math.sin(angle)));
            Vec3 pos = face.center().add(radial.scale(distance)).add(face.normal().scale(0.3));
            Vec3 velocity = face.normal().scale(0.25 + random.nextDouble() * 0.15).add(radial.scale(0.06 * scale));

            level.sendParticles(option, pos.x, pos.y, pos.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
        }
    }

    private static WarpStarParticleOption getStarOption(Vec3 normal, float spin) {
        return new WarpStarParticleOption(new Vector3f((float) normal.x, (float) normal.y, (float) normal.z), spin);
    }

    @Nullable
    private static WarpFace getWarpFace(ServerLevel level, BlockPos pos, BlockState state) {
        Vec3 center = Vec3.atCenterOf(pos);
        Direction facing = Direction.UP;
        double radius = 0.5;

        if (state.getBlock() instanceof WarpPipeBlock) {
            facing = state.getValue(WarpPipeBlock.FACING);
            List<BlockPos> group = state.getBlock() instanceof ClearWarpPipeBlock ? List.of(pos) : WarpPipeBlock.getEntranceGroup(level, pos, state);

            if (group.size() > 1) {
                center = new Vec3(group.stream().mapToInt(BlockPos::getX).average().orElse(pos.getX()) + 0.5,
                        group.stream().mapToInt(BlockPos::getY).average().orElse(pos.getY()) + 0.5,
                        group.stream().mapToInt(BlockPos::getZ).average().orElse(pos.getZ()) + 0.5);

                for (BlockPos block : group)
                    radius = Math.max(radius, Vec3.atCenterOf(block).distanceTo(center) + 0.5);
            }
            center = center.add(facing.getStepX() * 0.5, facing.getStepY() * 0.5, facing.getStepZ() * 0.5);
        } else if (state.getBlock() instanceof DoorBlock) {
            facing = state.getValue(DoorBlock.FACING);
            radius = 1.0;
            if (state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
                center = center.add(0.0, 0.5, 0.0);
            else center = center.add(0.0, -0.5, 0.0);
        } else if (state.getBlock() instanceof TrapDoorBlock)
            center = center.add(0.0, 0.5, 0.0);

        Vec3 worldCenter = WarpLinkParticles.toWorld(level, center);
        Vec3 worldFacing = WarpLinkParticles.toWorld(level, center.add(facing.getStepX(), facing.getStepY(), facing.getStepZ()));

        if (worldCenter == null || worldFacing == null)
            return null;
        return new WarpFace(worldCenter, worldFacing.subtract(worldCenter).normalize(), radius);
    }

    private static Vec3[] getFaceBasis(Vec3 normal) {
        Vec3 reference = Math.abs(normal.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
        Vec3 first = reference.cross(normal).normalize();
        return new Vec3[] {first, normal.cross(first).normalize()};
    }

    private static void sendOutlineParticle(ServerLevel level, @Nullable ServerPlayer viewer, Vec3 pos) {
        Vec3 worldPos = WarpLinkParticles.toWorld(level, pos);
        if (worldPos == null)
            return;

        if (viewer != null)
            level.sendParticles(viewer, ParticleTypes.END_ROD, false, worldPos.x, worldPos.y, worldPos.z, 1, 0.0, 0.0, 0.0, 0.0);
        else level.sendParticles(ParticleTypes.END_ROD, worldPos.x, worldPos.y, worldPos.z, 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Nullable
    private static Vec3 toWorld(ServerLevel level, Vec3 pos) {
        return SableProvider.isLoaded() ? SableProvider.toWorldVec(level, pos) : pos;
    }

    private static Direction.Axis getRemainingAxis(Direction.Axis first, Direction.Axis second) {
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != first && axis != second)
                return axis;
        }
        return Direction.Axis.Y;
    }
}
