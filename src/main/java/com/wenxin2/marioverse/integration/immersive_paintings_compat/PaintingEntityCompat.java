package com.wenxin2.marioverse.integration.immersive_paintings_compat;

import net.conczin.immersive_paintings.entity.ImmersivePaintingEntity;
import net.minecraft.world.entity.Entity;

public class PaintingEntityCompat {
    public static int getPaintingHeight(Entity entity) {
        if (entity instanceof ImmersivePaintingEntity painting)
            return painting.getPaintingHeight();
        return (int) entity.getBbHeight();
    }

    public static int getPaintingWidth(Entity entity) {
        if (entity instanceof ImmersivePaintingEntity painting)
            return painting.getPaintingWidth();
        return (int) entity.getBbWidth();
    }
}