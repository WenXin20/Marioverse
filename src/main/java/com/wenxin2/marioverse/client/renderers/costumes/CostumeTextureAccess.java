package com.wenxin2.marioverse.client.renderers.costumes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface CostumeTextureAccess extends CostumeRendererAccess {
    ResourceLocation getWaistTextureLocation();
    ResourceLocation getDressTextureLocation();
    ResourceLocation getDressOverlayTextureLocation();
    ResourceLocation getDressBackTextureLocation();
    @Nullable LivingEntity getCurrentWearer();
    int getDefaultDyeColor();
    int getDyeColor();
}
