package com.wenxin2.marioverse.integration.twilightforest_compat;

import net.minecraft.world.entity.Entity;
import twilightforest.entity.MagicPainting;

public class MagicPaintingEntityProvider {
    public static int getPaintingHeight(Entity entity) {
        if (entity instanceof MagicPainting painting)
            return painting.getVariant().value().height();
        return (int) entity.getBbHeight();
    }

    public static int getPaintingWidth(Entity entity) {
        if (entity instanceof MagicPainting painting)
            return painting.getVariant().value().width();
        return (int) entity.getBbWidth();
    }

    public static String getVariantString(Entity entity) {
        if (entity instanceof MagicPainting painting && painting.getVariant().getKey() != null)
            return painting.getVariant().getKey().location().toLanguageKey("magic_painting", "title");
        return null;
    }
}