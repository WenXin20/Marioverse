package com.wenxin2.marioverse.integration.sable_compat;

import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SableProvider {
    private static ContextGetter CONTEXT_GETTER = (l, e) -> null;

    public interface SafeAccessor {
        BlockState getBlockState(BlockPos pos);
        BlockEntity getBlockEntity(BlockPos pos);
        boolean hasChunkAt(BlockPos pos);
        int getMinY();
        int getMaxY();
    }

    public static class SableContext {
        public final SafeAccessor accessor;
        public final BlockPos posEmbedded;
        public final BlockPos posWorld;
        public final SubLevel sub;
        public final Vec3 posLocal;

        public SableContext(BlockPos posEmbedded, BlockPos posWorld, Vec3 posLocal, SafeAccessor accessor, SubLevel sub) {
            this.accessor = accessor;
            this.posEmbedded = posEmbedded;
            this.posLocal = posLocal;
            this.posWorld = posWorld;
            this.sub = sub;
        }

        public BlockPos toWorld(BlockPos embedded) {
            BlockPos plot = embedded.offset(this.sub.getPlot().getCenterBlock());
            Vector3d vec = this.sub.logicalPose().transformPosition(new Vector3d(plot.getX(), plot.getY(), plot.getZ()));

            return BlockPos.containing(vec.x, vec.y, vec.z);
        }

        public BlockPos toEmbedded(BlockPos worldPos) {
            Vector3d vec = this.sub.logicalPose().transformPositionInverse(new Vector3d(worldPos.getX(), worldPos.getY(), worldPos.getZ()));

            BlockPos plotPos = BlockPos.containing(vec.x, vec.y, vec.z);
            return plotPos.subtract(this.sub.getPlot().getCenterBlock()).offset(this.sub.getPlot().getCenterBlock());
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