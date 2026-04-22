package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.entity.EntitySubLevelUtil;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SableCompat {

    public static void init() {
        SableProvider.set((entity, level, ignoredPos) -> {

            // --- 1. Start with normal world block ---
            BlockPos globalPos = entity.blockPosition();
            BlockState state = level.getBlockState(globalPos);

            // If not air, return immediately (matches Sable behavior)
            if (!state.isAir())
                return state;

            // --- 2. Find ALL sublevels intersecting this position ---
            Iterable<SubLevel> subs =
                    Sable.HELPER.getAllIntersecting(level, new BoundingBox3d(globalPos));

            Vec3 entityPos = entity.position();

            for (SubLevel sub : subs) {

                Pose3dc pose = sub.logicalPose();
                var plot = sub.getPlot();
                var accessor = plot.getEmbeddedLevelAccessor();

                // --- 3. Transform entity position into sublevel (plot/world space) ---
                Vector3d localVec = pose.transformPositionInverse(
                        new Vector3d(
                                entityPos.x,
                                entityPos.y + 0.001, // small epsilon (important)
                                entityPos.z
                        )
                );

                // This is in plot/world coordinates
                BlockPos plotPos = BlockPos.containing(localVec.x, localVec.y - 0.01, localVec.z);

                // --- 4. Convert to embedded-local coordinates (CRITICAL FIX) ---
                BlockPos embeddedPos = plotPos.subtract(plot.getCenterBlock()
                );

                // --- 5. Prevent out-of-bounds crash ---
                if (!accessor.hasChunkAt(embeddedPos))
                    continue;

                // --- 6. Get block from embedded world ---
                BlockState localState = accessor.getBlockState(embeddedPos);

                if (!localState.isAir()) {
                    return localState;
                }
            }

            // --- 7. Fallback to normal world ---
            return state;
        });
    }
}