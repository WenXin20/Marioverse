package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SableCompat {
    public static void init() {
        SableProvider.set((level, entity) -> {
            AABB box = entity.getBoundingBox().inflate(0.25, 0.5, 0.25)
                    .move(0, -0.25, 0);
            Iterable<SubLevel> subs = Sable.HELPER.getAllIntersecting(level, new BoundingBox3d(box));
            Vec3 entityPos = entity.position();

            for (SubLevel sub : subs) {
                Pose3dc pose = sub.logicalPose();
                var plot = sub.getPlot();
                var accessor = plot.getEmbeddedLevelAccessor();
                Vector3d localVec = pose.transformPositionInverse(new Vector3d(entityPos.x, entityPos.y + 0.001, entityPos.z));
                BlockPos plotPos = BlockPos.containing(localVec.x, localVec.y, localVec.z);
                BlockPos embeddedPos = plotPos.subtract(plot.getCenterBlock());

                if (embeddedPos.getY() < accessor.getMinBuildHeight() ||
                        embeddedPos.getY() >= accessor.getMaxBuildHeight())
                    continue;
                BlockPos localPos = BlockPos.containing(localVec.x, localVec.y, localVec.z);
                BlockPos worldPos = embeddedPos.offset(plot.getCenterBlock());

                if (!accessor.hasChunkAt(embeddedPos))
                    continue;
                if (!accessor.hasChunkAt(worldPos))
                    continue;

                return new SableProvider.SableContext(plotPos, worldPos, new Vec3(localVec.x, localVec.y, localVec.z), accessor, sub);
            }
            return null;
        });
    }
}