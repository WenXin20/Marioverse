package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.CoinBlockEntity;
import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class StarCoinBlockModel extends GeoModel<StarCoinBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/star_coin.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/coin/star_coin.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/star_coin.animation.json");

    @Override
    public ResourceLocation getModelResource(StarCoinBlockEntity coinBlock) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(StarCoinBlockEntity coinBlock) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(StarCoinBlockEntity coinBlock) {
        return this.animations;
    }
}
