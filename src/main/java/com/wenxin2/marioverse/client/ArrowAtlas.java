package com.wenxin2.marioverse.client;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

/** Holds the "arrow" atlas (assets/marioverse/atlases/arrow.json), stitched via paletted_permutations. */
public class ArrowAtlas extends TextureAtlasHolder {
    public static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/atlas/arrow.png");
    private static final ResourceLocation ATLAS_INFO_LOCATION =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "arrow");

    private static ArrowAtlas instance;

    public ArrowAtlas(TextureManager textureManager) {
        super(textureManager, TEXTURE_LOCATION, ATLAS_INFO_LOCATION);
    }

    public static ArrowAtlas get() {
        if (instance == null)
            instance = new ArrowAtlas(Minecraft.getInstance().getTextureManager());
        return instance;
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location) {
        return super.getSprite(location);
    }
}
