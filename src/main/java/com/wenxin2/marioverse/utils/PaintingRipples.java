package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.integration.fast_paintings_compat.PaintingBlockWarp;
import com.wenxin2.marioverse.network.client_bound.data.PaintingBlockRipplePayload;
import com.wenxin2.marioverse.network.client_bound.data.PaintingRipplePayload;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class PaintingRipples {
    public static final float RIPPLE_TICKS = 60.0F;
    public static final int MAX_RIPPLES = 3;
    private static final float[] NO_RIPPLES = new float[0];
    private static final Map<Entity, Deque<Long>> RIPPLE_STARTS = Collections.synchronizedMap(new WeakHashMap<>());
    private static final Map<BlockPos, Deque<Long>> BLOCK_RIPPLE_STARTS = Collections.synchronizedMap(new HashMap<>());

    public static boolean isRipplePainting(Entity entity) {
        return entity instanceof Painting || PaintingRipples.isRipplePaintingType(entity.getType());
    }

    public static boolean isRipplePaintingType(EntityType<?> type) {
        return type == EntityType.PAINTING
                || type == CompatRegistry.IMMERSIVE_GLOW_GRAFFITI.get()
                || type == CompatRegistry.IMMERSIVE_GLOW_PAINTING.get()
                || type == CompatRegistry.IMMERSIVE_GRAFFITI.get()
                || type == CompatRegistry.IMMERSIVE_PAINTING.get()
                || type == CompatRegistry.MAGIC_PAINTING.get();
    }

    public static void sendRipple(Entity painting) {
        if (painting != null && painting.isAlive() && painting.level() instanceof ServerLevel
                && PaintingRipples.isRipplePainting(painting))
            PacketDistributor.sendToPlayersTrackingEntity(painting, new PaintingRipplePayload(painting.getId()));
    }

    public static void sendBlockRipple(@Nullable PaintingBlockWarp painting) {
        if (painting != null && painting.getLevel() instanceof ServerLevel serverLevel)
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(painting.getMasterPos()),
                    new PaintingBlockRipplePayload(painting.getMasterPos()));
    }

    public static void start(Entity painting) {
        Deque<Long> starts = RIPPLE_STARTS.computeIfAbsent(painting, entity -> new ArrayDeque<>());
        PaintingRipples.addStart(starts, painting.level().getGameTime());
    }

    public static void start(Level level, BlockPos pos) {
        Deque<Long> starts = BLOCK_RIPPLE_STARTS.computeIfAbsent(pos.immutable(), blockPos -> new ArrayDeque<>());
        PaintingRipples.addStart(starts, level.getGameTime());
    }

    public static float[] getProgresses(Entity painting, float partialTick) {
        if (RIPPLE_STARTS.isEmpty())
            return NO_RIPPLES;

        Deque<Long> starts = RIPPLE_STARTS.get(painting);
        if (starts == null)
            return NO_RIPPLES;

        float[] progresses = PaintingRipples.computeProgresses(starts, painting.level().getGameTime(), partialTick);
        if (starts.isEmpty())
            RIPPLE_STARTS.remove(painting);
        return progresses;
    }

    public static float[] getProgresses(Level level, BlockPos pos, float partialTick) {
        Deque<Long> starts = BLOCK_RIPPLE_STARTS.get(pos);
        if (starts == null)
            return NO_RIPPLES;

        float[] progresses = PaintingRipples.computeProgresses(starts, level.getGameTime(), partialTick);
        if (starts.isEmpty())
            BLOCK_RIPPLE_STARTS.remove(pos);
        return progresses;
    }

    public static List<BlockPos> getRipplingBlocks() {
        if (BLOCK_RIPPLE_STARTS.isEmpty())
            return List.of();

        synchronized (BLOCK_RIPPLE_STARTS) {
            return List.copyOf(BLOCK_RIPPLE_STARTS.keySet());
        }
    }

    private static void addStart(Deque<Long> starts, long gameTime) {
        starts.addLast(gameTime);

        while (starts.size() > MAX_RIPPLES)
            starts.removeFirst();
    }

    private static float[] computeProgresses(Deque<Long> starts, long gameTime, float partialTick) {
        float[] progresses = new float[starts.size()];
        int count = 0;
        Iterator<Long> iterator = starts.iterator();

        while (iterator.hasNext()) {
            float progress = (gameTime - iterator.next() + partialTick) / RIPPLE_TICKS;

            if (progress >= 1.0F || progress < 0.0F)
                iterator.remove();
            else progresses[count++] = progress;
        }
        return count == progresses.length ? progresses : Arrays.copyOf(progresses, count);
    }
}
