package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpDoorBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.integration.fast_paintings_compat.FastPaintingsCompat;
import com.wenxin2.marioverse.integration.fast_paintings_compat.FastPaintingsProvider;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class WarpLinkParticles {
    private static final double[] EDGE_STEPS = {-0.5, -0.25, 0.0, 0.25, 0.5};
    private static final double FACE_OFFSET = 0.51;
    private static final double OUTLINE_SPACING = 0.25;
    private static final int MAX_VIBRATION_TICKS = 40;
    private static final int SWIRL_PARTICLES = 16;
    private static final int MAX_SWIRL_PARTICLES = 64;
    private static final int PUFF_PARTICLES = 14;
    private static final int MAX_PUFF_PARTICLES = 48;
    private static final double MIN_SWIRL_RADIUS = 0.8;
    private static final float SWIRL_SPIN = 0.45F;
    private static final float PUFF_SPIN = 0.12F;

    private static final double FACE_HEIGHT = 0.1;

    private record WarpFace(Vec3 center, Vec3 normal, Vec3 axisA, Vec3 axisB, double radiusA, double radiusB) {}

    public static void outlineWarp(ServerLevel level, BlockPos pos, @Nullable ServerPlayer viewer) {
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof WarpPipeBlock)) {
            AABB panel = WarpLinkParticles.getShapeBounds(level, pos);

            if (WarpDoorBlock.isDoor(state)) {
                panel = null;
                for (BlockPos part : WarpDoorBlock.getParts(state, pos)) {
                    AABB partPanel = WarpLinkParticles.getShapeBounds(level, part);
                    if (partPanel != null)
                        panel = panel == null ? partPanel : panel.minmax(partPanel);
                }
            } else if (FastPaintingsProvider.isLoaded() && FastPaintingsCompat.isPaintingBlock(state))
                panel = FastPaintingsCompat.getPaintingBounds(level, pos);

            if (panel != null)
                WarpLinkParticles.outlineFace(level, viewer, panel);
            return;
        }

        Direction facing = state.getValue(WarpPipeBlock.FACING);
        List<BlockPos> blocks = state.getBlock() instanceof ClearWarpPipeBlock ? List.of(pos) : WarpPipeBlock.getEntranceGroup(level, pos, state);
        if (blocks.isEmpty())
            blocks = List.of(pos);

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
        WarpLinkParticles.outlineBox(level, viewer, entity.getBoundingBox());
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

        if (entrance != null && fromState.getBlock() instanceof WarpPipeBlock)
            WarpLinkParticles.spawnSwirl(level, entrance);
        if (exit != null && toState.getBlock() instanceof WarpPipeBlock)
            WarpLinkParticles.spawnPuff(level, exit);
    }

    private static void spawnSwirl(ServerLevel level, WarpFace face) {
        double ringA = Math.max(MIN_SWIRL_RADIUS, face.radiusA() * 1.1);
        double ringB = Math.max(MIN_SWIRL_RADIUS, face.radiusB() * 1.1);
        int count = Math.min(MAX_SWIRL_PARTICLES, (int) Math.round(SWIRL_PARTICLES * (ringA + ringB) * 0.5 / MIN_SWIRL_RADIUS));
        Vec3 semiAxis = face.axisA().scale(ringA);
        Vec3 center = face.center().add(face.normal().scale(FACE_HEIGHT));
        WarpStarParticleOption option = new WarpStarParticleOption(WarpLinkParticles.toVector(face.normal()), SWIRL_SPIN, true,
                WarpLinkParticles.toVector(semiAxis), (float) (ringB / ringA));

        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2.0 * i / count;
            Vec3 pos = center.add(semiAxis.scale(Math.cos(angle))).add(face.axisB().scale(ringB * Math.sin(angle)));
            Vec3 toCenter = center.subtract(pos);

            level.sendParticles(option, pos.x, pos.y, pos.z, 0, toCenter.x, toCenter.y, toCenter.z, 1.0);
        }
    }

    private static void spawnPuff(ServerLevel level, WarpFace face) {
        RandomSource random = level.getRandom();
        double scale = Math.max(1.0, (face.radiusA() + face.radiusB()) / 1.0);
        int count = Math.min(MAX_PUFF_PARTICLES, (int) Math.round(PUFF_PARTICLES * scale));
        Vec3 center = face.center().add(face.normal().scale(FACE_HEIGHT));
        WarpStarParticleOption option = new WarpStarParticleOption(WarpLinkParticles.toVector(face.normal()), PUFF_SPIN, false,
                new Vector3f(), 1.0F);

        for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = Math.sqrt(random.nextDouble()) * 0.7;
            Vec3 radial = face.axisA().scale(face.radiusA() * Math.cos(angle)).add(face.axisB().scale(face.radiusB() * Math.sin(angle)));
            Vec3 pos = center.add(radial.scale(distance));
            Vec3 velocity = face.normal().scale(0.125 + random.nextDouble() * 0.075).add(radial.scale(0.03));

            level.sendParticles(option, pos.x, pos.y, pos.z, 0, velocity.x, velocity.y, velocity.z, 1.0);
        }
    }

    @Nullable
    private static WarpFace getWarpFace(ServerLevel level, BlockPos pos, BlockState state) {
        Vec3 center = Vec3.atCenterOf(pos);
        Direction facing = Direction.UP;
        List<BlockPos> blocks = List.of(pos);

        if (state.getBlock() instanceof WarpPipeBlock) {
            facing = state.getValue(WarpPipeBlock.FACING);
            List<BlockPos> group = state.getBlock() instanceof ClearWarpPipeBlock ? List.of(pos) : WarpPipeBlock.getEntranceGroup(level, pos, state);

            if (!group.isEmpty())
                blocks = group;
            center = new Vec3(blocks.stream().mapToInt(BlockPos::getX).average().orElse(pos.getX()) + 0.5,
                    blocks.stream().mapToInt(BlockPos::getY).average().orElse(pos.getY()) + 0.5,
                    blocks.stream().mapToInt(BlockPos::getZ).average().orElse(pos.getZ()) + 0.5);
        } else if (WarpDoorBlock.isDoor(state)) {
            facing = WarpDoorBlock.getFacing(state);
            blocks = WarpDoorBlock.getParts(state, pos);
            center = Vec3.atCenterOf(blocks.getFirst()).add(0.0, (blocks.size() - 1) * 0.5, 0.0);
        }

        Vec3 normal = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        Vec3[] basis = WarpLinkParticles.getFaceBasis(normal);
        double radiusA = 0.5;
        double radiusB = 0.5;

        for (BlockPos block : blocks) {
            Vec3 offset = Vec3.atCenterOf(block).subtract(center);
            radiusA = Math.max(radiusA, Math.abs(offset.dot(basis[0])) + 0.5);
            radiusB = Math.max(radiusB, Math.abs(offset.dot(basis[1])) + 0.5);
        }

        return WarpLinkParticles.toWorldFace(level, center.add(normal.scale(0.5)), normal, radiusA, radiusB);
    }

    @Nullable
    private static WarpFace toWorldFace(ServerLevel level, Vec3 center, Vec3 normal, double radiusA, double radiusB) {
        Vec3[] basis = WarpLinkParticles.getFaceBasis(normal);
        Vec3 worldCenter = WarpLinkParticles.toWorld(level, center);
        Vec3 worldNormal = WarpLinkParticles.toWorld(level, center.add(normal));
        Vec3 worldAxisA = WarpLinkParticles.toWorld(level, center.add(basis[0]));
        Vec3 worldAxisB = WarpLinkParticles.toWorld(level, center.add(basis[1]));

        if (worldCenter == null || worldNormal == null || worldAxisA == null || worldAxisB == null)
            return null;
        return new WarpFace(worldCenter, worldNormal.subtract(worldCenter).normalize(), worldAxisA.subtract(worldCenter).normalize(),
                worldAxisB.subtract(worldCenter).normalize(), radiusA, radiusB);
    }

    private static double getBoxExtent(AABB box, Vec3 axis) {
        return (box.getXsize() * Math.abs(axis.x) + box.getYsize() * Math.abs(axis.y) + box.getZsize() * Math.abs(axis.z)) * 0.5;
    }

    private static Vector3f toVector(Vec3 vec) {
        return new Vector3f((float) vec.x, (float) vec.y, (float) vec.z);
    }

    private static Vec3[] getFaceBasis(Vec3 normal) {
        Vec3 reference = Math.abs(normal.y) > 0.9 ? new Vec3(1.0, 0.0, 0.0) : new Vec3(0.0, 1.0, 0.0);
        Vec3 first = reference.cross(normal).normalize();
        return new Vec3[] {first, normal.cross(first).normalize()};
    }

    @Nullable
    private static AABB getShapeBounds(ServerLevel level, BlockPos pos) {
        VoxelShape shape = level.getBlockState(pos).getShape(level, pos);
        return shape.isEmpty() ? null : shape.bounds().move(pos);
    }

    private static void outlineFace(ServerLevel level, @Nullable ServerPlayer viewer, AABB box) {
        Direction.Axis thinAxis = Direction.Axis.X;
        if (box.getYsize() < box.max(thinAxis) - box.min(thinAxis))
            thinAxis = Direction.Axis.Y;
        if (box.getZsize() < box.max(thinAxis) - box.min(thinAxis))
            thinAxis = Direction.Axis.Z;

        double middle = (box.min(thinAxis) + box.max(thinAxis)) * 0.5;
        AABB face = switch (thinAxis) {
            case X -> new AABB(middle, box.minY, box.minZ, middle, box.maxY, box.maxZ);
            case Y -> new AABB(box.minX, middle, box.minZ, box.maxX, middle, box.maxZ);
            case Z -> new AABB(box.minX, box.minY, middle, box.maxX, box.maxY, middle);
        };
        WarpLinkParticles.outlineBox(level, viewer, face);
    }

    private static void outlineBox(ServerLevel level, @Nullable ServerPlayer viewer, AABB box) {
        double[] xs = {box.minX, box.maxX};
        double[] ys = {box.minY, box.maxY};
        double[] zs = {box.minZ, box.maxZ};
        int xSteps = Math.max(1, (int) Math.ceil(box.getXsize() / OUTLINE_SPACING));
        int ySteps = Math.max(1, (int) Math.ceil(box.getYsize() / OUTLINE_SPACING));
        int zSteps = Math.max(1, (int) Math.ceil(box.getZsize() / OUTLINE_SPACING));

        for (int i = 0; i <= xSteps; i++)
            for (double y : ys)
                for (double z : zs)
                    WarpLinkParticles.sendOutlineParticle(level, viewer, new Vec3(box.minX + box.getXsize() * i / xSteps, y, z));
        for (int i = 0; i <= ySteps; i++)
            for (double x : xs)
                for (double z : zs)
                    WarpLinkParticles.sendOutlineParticle(level, viewer, new Vec3(x, box.minY + box.getYsize() * i / ySteps, z));
        for (int i = 0; i <= zSteps; i++)
            for (double x : xs)
                for (double y : ys)
                    WarpLinkParticles.sendOutlineParticle(level, viewer, new Vec3(x, y, box.minZ + box.getZsize() * i / zSteps));
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
