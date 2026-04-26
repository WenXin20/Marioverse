package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.EmbeddedPlotLevelAccessor;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
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
            SubLevelContainer container = SubLevelContainer.getContainer(level);

            if (!(entity instanceof Player))
                return null;

            if (access == null)
                access = EntitySubLevelUtil.getTrackingSubLevel(entity);
            if (access == null)
                access = EntitySubLevelUtil.getLastTrackingSubLevel(entity);

            if (!(access instanceof SubLevel)) {
                if (!level.isClientSide && container instanceof ServerSubLevelContainer serverContainer) {
                    for (SubLevel sub : serverContainer.getAllSubLevels()) {
                        SableProvider.SableContext ctx = buildContext(entity, sub);
                        Pose3dc pose = sub.logicalPose();
                        Vec3 entityPos = entity.position();
                        BoundingBox3ic box = sub.getPlot().getBoundingBox();
                        Vector3d localVec = pose.transformPositionInverse(new Vector3d(entityPos.x, entityPos.y, entityPos.z));
                        BlockPos localPos = BlockPos.containing(localVec.x, localVec.y, localVec.z);

                        if (localPos.getX() < box.minX() || localPos.getX() > box.maxX()
                                || localPos.getY() < box.minY() || localPos.getY() > box.maxY()
                                || localPos.getZ() < box.minZ() || localPos.getZ() > box.maxZ()) {
                            continue;
                        }
                        return ctx;
                    }
                    return null;
                }
            }
            if (access instanceof SubLevel sub)
                return buildContext(entity, sub);
            return null;
        });
    }

    private static SableProvider.SableContext buildContext(Entity entity, SubLevel sub) {
        Pose3d pose = sub.logicalPose();
        var plot = sub.getPlot();
        EmbeddedPlotLevelAccessor accessor = plot.getEmbeddedLevelAccessor();

        Vec3 entityPos = entity.position();
        Vector3d localVec = pose.transformPositionInverse(new Vector3d(entityPos.x, entityPos.y, entityPos.z));
        BlockPos embeddedPos = BlockPos.containing(Math.floor(localVec.x), Math.floor(localVec.y - 0.001), Math.floor(localVec.z));
        Vector3d worldVec = pose.transformPosition(new Vector3d(embeddedPos.getX(), embeddedPos.getY(), embeddedPos.getZ()));
        BlockPos worldPos = BlockPos.containing(worldVec.x, worldVec.y, worldVec.z);

        SableProvider.SafeAccessor safeAccessor = new SableProvider.SafeAccessor() {
            @Override
            public BlockState getBlockState(BlockPos pos) {
                if (!accessor.hasChunkAt(pos))
                    return Blocks.AIR.defaultBlockState();
                return accessor.getBlockState(pos);
            }

            @Override
            public BlockEntity getBlockEntity(BlockPos pos) {
                if (!accessor.hasChunkAt(pos))
                    return null;
                return accessor.getBlockEntity(pos);
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

        return new SableProvider.SableContext(embeddedPos, worldPos, new Vec3(localVec.x, localVec.y, localVec.z), safeAccessor, sub);
    }
}