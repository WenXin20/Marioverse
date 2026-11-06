package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SubLevelCollision {
    private static final double SUB_LEVEL_TOLERANCE = 0.1;
    private static final ThreadLocal<Entity> COLLIDING_ENTITY = new ThreadLocal<>();

    @Nullable
    public static Entity pushCollidingEntity(Entity entity) {
        Entity previous = COLLIDING_ENTITY.get();
        COLLIDING_ENTITY.set(entity);
        return previous;
    }

    public static void popCollidingEntity(@Nullable Entity previous) {
        COLLIDING_ENTITY.set(previous);
    }

    @Nullable
    public static Entity getCollisionEntity(CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null)
            return entityContext.getEntity();
        return COLLIDING_ENTITY.get();
    }

    public static boolean isAbove(CollisionContext context, VoxelShape shape, BlockPos pos) {
        return SubLevelCollision.isAbove(context, shape, pos, shape, pos);
    }

    public static boolean isAbove(CollisionContext context, VoxelShape shape, BlockPos pos, VoxelShape solidShape, BlockPos solidPos) {
        Entity entity = SubLevelCollision.getCollisionEntity(context);
        if (entity == null)
            return false;

        Vec3 localFeet = SubLevelCollision.getLocalFeet(entity, pos);
        if (localFeet == null) {
            if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() != null)
                return entityContext.isAbove(shape, pos, false);
            return entity.getY() > pos.getY() + shape.max(Direction.Axis.Y) - 1.0E-5;
        }

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
