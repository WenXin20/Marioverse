package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.network.client_bound.data.PaintingRipplePayload;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.neoforged.neoforge.network.PacketDistributor;

public class PaintingRipples {
    public static final float RIPPLE_TICKS = 60.0F;
    public static final int MAX_RIPPLES = 3;
    private static final float[] NO_RIPPLES = new float[0];
    private static final Map<Entity, Deque<Long>> RIPPLE_STARTS = Collections.synchronizedMap(new WeakHashMap<>());

    public static boolean isRipplePainting(Entity entity) {
        if (entity instanceof Painting)
            return true;

        EntityType<?> type = entity.getType();
        return type == CompatRegistry.IMMERSIVE_GLOW_GRAFFITI.get()
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

    public static void start(Entity painting) {
        Deque<Long> starts = RIPPLE_STARTS.computeIfAbsent(painting, entity -> new ArrayDeque<>());
        starts.addLast(painting.level().getGameTime());

        while (starts.size() > MAX_RIPPLES)
            starts.removeFirst();
    }

    public static float[] getProgresses(Entity painting, float partialTick) {
        if (RIPPLE_STARTS.isEmpty())
            return NO_RIPPLES;

        Deque<Long> starts = RIPPLE_STARTS.get(painting);
        if (starts == null)
            return NO_RIPPLES;

        float[] progresses = new float[starts.size()];
        int count = 0;
        Iterator<Long> iterator = starts.iterator();

        while (iterator.hasNext()) {
            float progress = (painting.level().getGameTime() - iterator.next() + partialTick) / RIPPLE_TICKS;

            if (progress >= 1.0F || progress < 0.0F)
                iterator.remove();
            else progresses[count++] = progress;
        }

        if (starts.isEmpty())
            RIPPLE_STARTS.remove(painting);
        return count == progresses.length ? progresses : Arrays.copyOf(progresses, count);
    }
}
