package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SubLevelCollision {
    private static final double SUB_LEVEL_TOLERANCE = 0.1;

    public static boolean isAbove(EntityCollisionContext context, VoxelShape shape, BlockPos pos) {
        return SubLevelCollision.isAbove(context, shape, pos, shape, pos);
    }

    public static boolean isAbove(EntityCollisionContext context, VoxelShape shape, BlockPos pos, VoxelShape solidShape, BlockPos solidPos) {
        Entity entity = context.getEntity();
        Vec3 localFeet = entity != null ? SubLevelCollision.getLocalFeet(entity, pos) : null;

        if (localFeet == null)
            return context.isAbove(shape, pos, false);
        if (solidShape.isEmpty())
            return false;
        return localFeet.y > solidPos.getY() + solidShape.max(Direction.Axis.Y) - SUB_LEVEL_TOLERANCE;
    }

    public static double getFeetY(Entity entity, BlockPos pos) {
        Vec3 localFeet = SubLevelCollision.getLocalFeet(entity, pos);
        return localFeet != null ? localFeet.y : entity.getBoundingBox().minY;
    }

    private static Vec3 getLocalFeet(Entity entity, BlockPos pos) {
        if (!SableProvider.isLoaded() || !SableProvider.isPlotPos(entity.level(), pos))
            return null;
        return SableProvider.toLocalVec(entity.level(), pos, entity.position());
    }
}
