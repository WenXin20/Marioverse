package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.network.client_bound.data.PaintingRipplePayload;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.neoforged.neoforge.network.PacketDistributor;

public class PaintingRipples {
    public static final float RIPPLE_TICKS = 60.0F;
    private static final Map<Entity, Long> RIPPLE_STARTS = Collections.synchronizedMap(new WeakHashMap<>());

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
        RIPPLE_STARTS.put(painting, painting.level().getGameTime());
    }

    public static float getProgress(Entity painting, float partialTick) {
        if (RIPPLE_STARTS.isEmpty())
            return -1.0F;

        Long start = RIPPLE_STARTS.get(painting);
        if (start == null)
            return -1.0F;

        float progress = (painting.level().getGameTime() - start + partialTick) / RIPPLE_TICKS;
        if (progress >= 1.0F || progress < 0.0F) {
            RIPPLE_STARTS.remove(painting);
            return -1.0F;
        }
        return progress;
    }
}
