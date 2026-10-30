package com.wenxin2.marioverse.integration.sable_compat;

import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

public class SableProvider {
    private static ContextGetter CONTEXT_GETTER = (l, e) -> null;
    private static Boolean sableLoaded;

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

    public static boolean isLoaded() {
        if (sableLoaded == null)
            sableLoaded = ModList.get().isLoaded("sable");
        return sableLoaded;
    }

    public static SableContext getContext(Level level, Entity entity) {
        return CONTEXT_GETTER.get(level, entity);
    }

    public static void set(ContextGetter getter) {
        CONTEXT_GETTER = getter;
    }

    public record SubLevelAnchor(UUID subLevel, BlockPos localPos) {}

    public interface WarpResolver {
        @Nullable SubLevelAnchor getAnchor(Level level, BlockPos pos);
        boolean isPlotPos(Level level, BlockPos pos);
        @Nullable BlockPos resolve(Level level, UUID subLevel, BlockPos localPos);
        boolean queueWarp(ServerLevel level, UUID subLevel, BlockPos localPos, Entity entity, Predicate<BlockPos> isReady, Runnable onLoaded);
        boolean requestSubLevelLoad(ServerLevel level, UUID subLevel);
        @Nullable BlockPos toWorldPos(Level level, BlockPos pos);
        @Nullable Vec3 toWorldVec(Level level, Vec3 pos);
        void recordEntityAnchor(Entity entity);
        @Nullable SubLevelAnchor getEntityAnchor(Level level, UUID entity);
    }

    private static WarpResolver WARP_RESOLVER = new WarpResolver() {
        @Override
        public SubLevelAnchor getAnchor(Level level, BlockPos pos) {
            return null;
        }

        @Override
        public boolean isPlotPos(Level level, BlockPos pos) {
            return false;
        }

        @Override
        public BlockPos resolve(Level level, UUID subLevel, BlockPos localPos) {
            return null;
        }

        @Override
        public boolean queueWarp(ServerLevel level, UUID subLevel, BlockPos localPos, Entity entity, Predicate<BlockPos> isReady, Runnable onLoaded) {
            return false;
        }

        @Override
        public boolean requestSubLevelLoad(ServerLevel level, UUID subLevel) {
            return false;
        }

        @Override
        public BlockPos toWorldPos(Level level, BlockPos pos) {
            return pos;
        }

        @Override
        public Vec3 toWorldVec(Level level, Vec3 pos) {
            return pos;
        }

        @Override
        public void recordEntityAnchor(Entity entity) {
        }

        @Override
        public SubLevelAnchor getEntityAnchor(Level level, UUID entity) {
            return null;
        }
    };

    public static void setWarpResolver(WarpResolver resolver) {
        WARP_RESOLVER = resolver;
    }

    @Nullable
    public static SubLevelAnchor getAnchor(Level level, BlockPos pos) {
        return WARP_RESOLVER.getAnchor(level, pos);
    }

    public static boolean isPlotPos(Level level, BlockPos pos) {
        return WARP_RESOLVER.isPlotPos(level, pos);
    }

    @Nullable
    public static BlockPos resolveSubLevelPos(Level level, UUID subLevel, BlockPos localPos) {
        return WARP_RESOLVER.resolve(level, subLevel, localPos);
    }

    @Nullable
    public static BlockPos toWorldPos(Level level, BlockPos pos) {
        return WARP_RESOLVER.toWorldPos(level, pos);
    }

    @Nullable
    public static Vec3 toWorldVec(Level level, Vec3 pos) {
        return WARP_RESOLVER.toWorldVec(level, pos);
    }

    public static void recordEntityAnchor(Entity entity) {
        WARP_RESOLVER.recordEntityAnchor(entity);
    }

    @Nullable
    public static SubLevelAnchor getEntityAnchor(Level level, UUID entity) {
        return WARP_RESOLVER.getEntityAnchor(level, entity);
    }

    public static boolean requestSubLevelLoad(ServerLevel level, UUID subLevel) {
        return WARP_RESOLVER.requestSubLevelLoad(level, subLevel);
    }

    public static boolean queueSubLevelWarp(ServerLevel level, UUID subLevel, BlockPos localPos, Entity entity,
                                            Predicate<BlockPos> isReady, Runnable onLoaded) {
        return WARP_RESOLVER.queueWarp(level, subLevel, localPos, entity, isReady, onLoaded);
    }
}