package com.wenxin2.marioverse.integration;

import net.conczin.immersive_paintings.entity.ImmersivePaintingEntity;
import net.mehvahdjukaar.supplementaries.common.entities.goals.UseCannonBoatGoal;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

public class ImmersivePaintingEntityCompat {
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