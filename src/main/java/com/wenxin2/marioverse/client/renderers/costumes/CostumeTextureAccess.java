package com.wenxin2.marioverse.client.renderers.costumes;

import net.minecraft.resources.ResourceLocation;

public interface CostumeTextureAccess extends CostumeRendererAccess {
    ResourceLocation getWaistTextureLocation();
    ResourceLocation getDressTextureLocation();
    ResourceLocation getDressOverlayTextureLocation();
    ResourceLocation getDressBackTextureLocation();
    int getDefaultDyeColor();
    int getDyeColor();
}
