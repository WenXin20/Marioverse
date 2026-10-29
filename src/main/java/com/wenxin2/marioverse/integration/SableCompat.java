package com.wenxin2.marioverse.integration;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.integration.sable_compat.SubLevelLocationSavedData;
import com.wenxin2.marioverse.utils.BlockWarpEntityHandler;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.neoforge.event.ForgeSableSubLevelContainerReadyEvent;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForge;
import org.joml.Vector3d;

public class SableCompat {
    private static final Map<SubLevel, SableProvider.SafeAccessor> ACCESSOR_CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static final TicketType<ChunkPos> WARP_TICKET = TicketType.create("marioverse_warp", Comparator.comparingLong(ChunkPos::toLong), 400);
    private static final Map<Integer, PendingWarp> PENDING_WARPS = new HashMap<>();
    private static final Set<UUID> UNKNOWN_LOCATION_WARNINGS = new HashSet<>();
    private static final int PENDING_WARP_TIMEOUT = 300;
    private static final int MAX_TICKETED_CHUNKS = 256;
    private static final int LOCATION_UPDATE_INTERVAL = 100;

    private record PendingWarp(ServerLevel level, UUID subLevel, BlockPos localPos, Entity entity, Predicate<BlockPos> isReady,
                               Runnable onLoaded, long deadline) {}

    private static final SubLevelObserver LOCATION_OBSERVER = new SubLevelObserver() {
        @Override
        public void onSubLevelAdded(SubLevel sub) {
            if (sub.getLevel() instanceof ServerLevel level)
                SubLevelLocationSavedData.get(level).setLocation(sub.getUniqueId(), SableCompat.getChunkBounds(sub));
        }

        @Override
        public void onSubLevelRemoved(SubLevel sub, SubLevelRemovalReason reason) {
            if (sub.getLevel() instanceof ServerLevel level) {
                SubLevelLocationSavedData data = SubLevelLocationSavedData.get(level);

                if (reason == SubLevelRemovalReason.UNLOADED)
                    data.setLocation(sub.getUniqueId(), SableCompat.getChunkBounds(sub));
                else data.removeLocation(sub.getUniqueId());
            }
        }

        @Override
        public void tick(SubLevelContainer container) {
            if (container instanceof ServerSubLevelContainer serverContainer) {
                ServerLevel level = serverContainer.getLevel();

                if (level.getGameTime() % LOCATION_UPDATE_INTERVAL == 0) {
                    SubLevelLocationSavedData data = SubLevelLocationSavedData.get(level);
                    for (SubLevel sub : serverContainer.getAllSubLevels())
                        data.setLocation(sub.getUniqueId(), SableCompat.getChunkBounds(sub));
                }

                SableCompat.processPendingWarps(level);
            }
        }
    };

    public static void init() {
        NeoForge.EVENT_BUS.addListener(SableCompat::onContainerReady);

        SableProvider.set((level, entity) -> {
            if (entity == null)
                return null;

            SubLevelContainer container = SubLevelContainer.getContainer(level);
            if (container == null || container.getLoadedCount() == 0)
                return null;

            SubLevelAccess access = SableCompanion.INSTANCE.getContaining(entity);
            if (!(access instanceof SubLevel))
                access = SableCompanion.INSTANCE.getLastTrackingSubLevel(entity);
            if (!(access instanceof SubLevel))
                access = SableCompanion.INSTANCE.getTrackingSubLevel(entity);

            if (access instanceof SubLevel sub) {
                if (isInsideSublevel(entity, sub))
                    return buildContext(entity, sub);
            }

            if (!level.isClientSide && container instanceof ServerSubLevelContainer) {
                for (SubLevel sub : container.queryIntersecting(new BoundingBox3d(entity.getBoundingBox()))) {
                    if (isInsideSublevel(entity, sub))
                        return buildContext(entity, sub);
                }
            }

            return null;
        });

        SableProvider.setWarpResolver(new SableProvider.WarpResolver() {
            @Override
            public SableProvider.SubLevelAnchor getAnchor(Level level, BlockPos pos) {
                SubLevelContainer container = SubLevelContainer.getContainer(level);

                if (container == null || !container.inBounds(pos))
                    return null;

                LevelPlot plot = container.getPlot(new ChunkPos(pos));
                if (plot == null || plot.getSubLevel() == null)
                    return null;
                return new SableProvider.SubLevelAnchor(plot.getSubLevel().getUniqueId(), pos.subtract(plot.getCenterBlock()));
            }

            @Override
            public boolean isPlotPos(Level level, BlockPos pos) {
                SubLevelContainer container = SubLevelContainer.getContainer(level);
                return container != null && container.inBounds(pos);
            }

            @Override
            public BlockPos resolve(Level level, UUID subLevel, BlockPos localPos) {
                return SableCompat.resolveSubLevelPos(level, subLevel, localPos);
            }

            @Override
            public boolean queueWarp(ServerLevel level, UUID subLevel, BlockPos localPos, Entity entity, Predicate<BlockPos> isReady, Runnable onLoaded) {
                if (PENDING_WARPS.containsKey(entity.getId()))
                    return true;

                SubLevelLocationSavedData.ChunkBounds bounds = SubLevelLocationSavedData.get(level).getLocation(subLevel);
                if (bounds == null) {
                    if (UNKNOWN_LOCATION_WARNINGS.add(subLevel))
                        Marioverse.LOGGER.warn("Cannot load warp destination sub-level {}: its last location is unknown", subLevel);
                    return false;
                }

                int ticketedChunks = 0;
                for (int chunkX = bounds.minX(); chunkX <= bounds.maxX(); chunkX++) {
                    for (int chunkZ = bounds.minZ(); chunkZ <= bounds.maxZ() && ticketedChunks < MAX_TICKETED_CHUNKS; chunkZ++) {
                        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
                        level.getChunkSource().addRegionTicket(WARP_TICKET, chunkPos, 2, chunkPos);
                        ticketedChunks++;
                    }
                }
                PENDING_WARPS.put(entity.getId(), new PendingWarp(level, subLevel, localPos, entity, isReady, onLoaded,
                        level.getGameTime() + PENDING_WARP_TIMEOUT));
                return true;
            }

            @Override
            public BlockPos toWorldPos(Level level, BlockPos pos) {
                SubLevelContainer container = SubLevelContainer.getContainer(level);

                if (container == null || !container.inBounds(pos))
                    return pos;

                LevelPlot plot = container.getPlot(new ChunkPos(pos));
                if (plot == null || plot.getSubLevel() == null)
                    return null;

                Vector3d worldPos = plot.getSubLevel().logicalPose().transformPosition(new Vector3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
                return BlockPos.containing(worldPos.x, worldPos.y, worldPos.z);
            }

            @Override
            public Vec3 toWorldVec(Level level, Vec3 pos) {
                SubLevelContainer container = SubLevelContainer.getContainer(level);
                BlockPos blockPos = BlockPos.containing(pos);

                if (container == null || !container.inBounds(blockPos))
                    return pos;

                LevelPlot plot = container.getPlot(new ChunkPos(blockPos));
                if (plot == null || plot.getSubLevel() == null)
                    return null;

                Vector3d worldPos = plot.getSubLevel().logicalPose().transformPosition(new Vector3d(pos.x, pos.y, pos.z));
                return new Vec3(worldPos.x, worldPos.y, worldPos.z);
            }
        });
    }

    private static void onContainerReady(ForgeSableSubLevelContainerReadyEvent event) {
        if (event.getContainer() instanceof ServerSubLevelContainer container)
            container.addObserver(LOCATION_OBSERVER);
    }

    private static SableProvider.SableContext buildContext(Entity entity, SubLevel sub) {
        Pose3dc pose = sub.logicalPose();
        var plot = sub.getPlot();
        var accessor = plot.getEmbeddedLevelAccessor();

        SableProvider.SafeAccessor safeAccessor = ACCESSOR_CACHE.computeIfAbsent(sub, s -> new SableProvider.SafeAccessor() {
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
            public BlockHitResult clip(Level level, Vec3 start, Vec3 end, Entity entity) {
                return accessor.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
            }

            @Override
            public BlockHitResult clipServer(Level level, Vec3 start, Vec3 end, Entity entity) {
                return accessor.getLevel().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
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
        });

        Vec3 entityPos = entity.position();
        Vector3d localVec = pose.transformPositionInverse(new Vector3d(entityPos.x, entityPos.y + 0.001, entityPos.z));
        BlockPos plotPos = BlockPos.containing(localVec.x, localVec.y, localVec.z);
        BlockPos embeddedPos = plotPos.subtract(plot.getCenterBlock());
        BlockPos worldPos = embeddedPos.offset(plot.getCenterBlock());
        Vec3 posLocal = new Vec3(localVec.x, localVec.y, localVec.z);

        return new SableProvider.SableContext(embeddedPos, worldPos, posLocal, pose, safeAccessor, sub);
    }

    private static boolean isInsideSublevel(Entity entity, SubLevel sub) {
        if (entity == null)
            return false;

        BoundingBox3dc bb = sub.boundingBox();
        AABB entityBox = entity.getBoundingBox();

        return entityBox.maxX > bb.minX() && entityBox.minX < bb.maxX()
                && entityBox.maxY > bb.minY() && entityBox.minY < bb.maxY()
                && entityBox.maxZ > bb.minZ() && entityBox.minZ < bb.maxZ();
    }

    private static SubLevelLocationSavedData.ChunkBounds getChunkBounds(SubLevel sub) {
        BoundingBox3dc bb = sub.boundingBox();
        return new SubLevelLocationSavedData.ChunkBounds(Mth.floor(bb.minX() - 1.0) >> 4, Mth.floor(bb.minZ() - 1.0) >> 4,
                Mth.floor(bb.maxX() + 1.0) >> 4, Mth.floor(bb.maxZ() + 1.0) >> 4);
    }

    private static BlockPos resolveSubLevelPos(Level level, UUID subLevel, BlockPos localPos) {
        SubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null)
            return null;

        SubLevel sub = container.getSubLevel(subLevel);
        if (sub == null || sub.isRemoved())
            return null;
        return sub.getPlot().getCenterBlock().offset(localPos);
    }

    private static void processPendingWarps(ServerLevel level) {
        if (PENDING_WARPS.isEmpty())
            return;

        List<PendingWarp> readyWarps = new ArrayList<>();
        Iterator<PendingWarp> iterator = PENDING_WARPS.values().iterator();

        while (iterator.hasNext()) {
            PendingWarp warp = iterator.next();

            if (warp.level() != level)
                continue;

            if (warp.entity().isRemoved() || warp.entity().level() != level) {
                iterator.remove();
                continue;
            }

            BlockPos resolvedPos = SableCompat.resolveSubLevelPos(level, warp.subLevel(), warp.localPos());

            if (resolvedPos != null && warp.isReady().test(resolvedPos)) {
                iterator.remove();
                readyWarps.add(warp);
            } else if (level.getGameTime() > warp.deadline()) {
                iterator.remove();
                Marioverse.LOGGER.warn("Timed out loading warp destination sub-level {} (loaded: {}, bounds: {})", warp.subLevel(),
                        resolvedPos != null, SubLevelLocationSavedData.get(level).getLocation(warp.subLevel()));
                if (warp.entity() instanceof Player player)
                    BlockWarpEntityHandler.displayDestinationMissingMessage(player);
            }
        }

        for (PendingWarp warp : readyWarps)
            warp.onLoaded().run();
    }
}
