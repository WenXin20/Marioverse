package com.wenxin2.marioverse.integration.sable_compat;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SableProvider {
    private static ContextGetter CONTEXT_GETTER = (l, e) -> null;

    public interface SafeAccessor {
        BlockState getBlockState(BlockPos pos);
        BlockState getServerBlockState(BlockPos pos);
        BlockEntity getBlockEntity(BlockPos pos);
        BlockEntity getServerBlockEntity(BlockPos pos);
        BlockHitResult clip(Level level, Vec3 start, Vec3 end, Entity entity);
        BlockHitResult clipServer(Level level, Vec3 start, Vec3 end, Entity entity);
        boolean hasChunkAt(BlockPos pos);
        int getMinY();
        int getMaxY();
    }

    public static class SableContext {
        public final SafeAccessor accessor;
        public final BlockPos posEmbedded;
        public final BlockPos posWorld;
        public final Pose3dc pose3dc;
        public final SubLevel subLevel;
        public final Vec3 posLocal;

        public SableContext(BlockPos posEmbedded, BlockPos posWorld, Vec3 posLocal, Pose3dc pose, SafeAccessor accessor, SubLevel sub) {
            this.accessor = accessor;
            this.posEmbedded = posEmbedded;
            this.posLocal = posLocal;
            this.posWorld = posWorld;
            this.pose3dc = pose;
            this.subLevel = sub;
        }

        public BlockPos toWorld(BlockPos embedded) {
            BlockPos plot = embedded.offset(this.subLevel.getPlot().getCenterBlock());
            Vector3d vec = this.subLevel.logicalPose().transformPosition(new Vector3d(plot.getX(), plot.getY(), plot.getZ()));

            return BlockPos.containing(vec.x, vec.y, vec.z);
        }

        public BlockPos toEmbedded(BlockPos worldPos) {
            Vector3d vec = this.subLevel.logicalPose().transformPositionInverse(new Vector3d(worldPos.getX(), worldPos.getY(), worldPos.getZ()));

            BlockPos plotPos = BlockPos.containing(vec.x, vec.y, vec.z);
            return plotPos.subtract(this.subLevel.getPlot().getCenterBlock()).offset(this.subLevel.getPlot().getCenterBlock());
        }
    }

    public interface ContextGetter {
        SableContext get(Level level, Entity entity);
    }

    public static SableContext getContext(Level level, Entity entity) {
        return CONTEXT_GETTER.get(level, entity);
    }

    public static void set(ContextGetter getter) {
        CONTEXT_GETTER = getter;
    }
}