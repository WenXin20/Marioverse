package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.CoinBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CoinBlockModel extends GeoModel<CoinBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/coin.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/coin/coin.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/coin.animation.json");

    @Override
    public ResourceLocation getModelResource(CoinBlockEntity coinBlock) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(CoinBlockEntity coinBlock) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(CoinBlockEntity coinBlock) {
        return this.animations;
    }
}
