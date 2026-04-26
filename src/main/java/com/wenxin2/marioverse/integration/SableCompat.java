package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SableCompat {
    public static void init() {
        SableProvider.set((level, entity) -> {
            SubLevelAccess access = SableCompanion.INSTANCE.getContaining(entity);
            if (access == null)
                access = EntitySubLevelUtil.getLastTrackingSubLevel(entity);
            if (access == null)
                access = EntitySubLevelUtil.getTrackingSubLevel(entity);

            if (!(access instanceof SubLevel)) {
                SubLevelContainer container = SubLevelContainer.getContainer(level);

                if (!level.isClientSide && container instanceof ServerSubLevelContainer) {
                    for (SubLevel sub : container.getAllSubLevels()) {
                        SableProvider.SableContext context = buildContext(entity, sub);
                        return context;
                    }
                }
                return null;
            }
            return buildContext(entity, (SubLevel) access);
        });
    }

    private static SableProvider.SableContext buildContext(Entity entity, SubLevel sub) {
        Pose3dc pose = sub.logicalPose();
        var plot = sub.getPlot();
        var accessor = plot.getEmbeddedLevelAccessor();

        SableProvider.SafeAccessor safeAccessor = new SableProvider.SafeAccessor() {
            @Override
            public BlockState getBlockState(BlockPos pos) {
                if (!accessor.hasChunkAt(pos))
                    return Blocks.AIR.defaultBlockState();
                return accessor.getBlockState(pos);
            }

            @Override
            public BlockState getServerBlockState(BlockPos pos) {
                if (!accessor.getLevel().hasChunkAt(pos))
                    return Blocks.AIR.defaultBlockState();
                return accessor.getLevel().getBlockState(pos);
            }

            @Override
            public BlockEntity getBlockEntity(BlockPos pos) {
                if (!accessor.hasChunkAt(pos))
                    return null;
                return accessor.getBlockEntity(pos);
            }

            @Override
            public BlockEntity getServerBlockEntity(BlockPos pos) {
                if (!accessor.getLevel().hasChunkAt(pos))
                    return null;
                return accessor.getLevel().getBlockEntity(pos);
            }

            @Override
            public boolean hasChunkAt(BlockPos pos) {
                return accessor.hasChunkAt(pos);
            }

            @Override
            public int getMinY() {
                return accessor.getMinBuildHeight();
            }

            @Override
            public int getMaxY() {
                return accessor.getMaxBuildHeight();
            }
        };

        Vec3 entityPos = entity.position();
        Vector3d localVec = pose.transformPositionInverse(new Vector3d(entityPos.x, entityPos.y + 0.001, entityPos.z));
        BlockPos plotPos = BlockPos.containing(localVec.x, localVec.y, localVec.z);
        BlockPos embeddedPos = plotPos.subtract(plot.getCenterBlock());
        BlockPos worldPos = embeddedPos.offset(plot.getCenterBlock());

        return new SableProvider.SableContext(embeddedPos, worldPos, new Vec3(localVec.x, localVec.y, localVec.z), safeAccessor, sub);
    }
}