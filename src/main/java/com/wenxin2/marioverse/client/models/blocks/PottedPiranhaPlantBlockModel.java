package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.PottedPiranhaPlantBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PottedPiranhaPlantBlockModel extends GeoModel<PottedPiranhaPlantBlockEntity> {
    private final ResourceLocation model = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/potted_piranha_plant.geo.json");
    private final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/piranha_plant/potted_piranha_plant.png");
    private final ResourceLocation animations = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/potted_piranha_plant.animation.json");

    @Override
    public ResourceLocation getModelResource(PottedPiranhaPlantBlockEntity blockEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(PottedPiranhaPlantBlockEntity blockEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(PottedPiranhaPlantBlockEntity blockEntity) {
        return this.animations;
    }
}