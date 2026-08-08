package com.wenxin2.marioverse.client.models.blocks;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.PottedPiranhaPlantBlockEntity;
import com.wenxin2.marioverse.entities.variants.PiranhaPlantVariants;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PottedPiranhaPlantBlockModel extends GeoModel<PottedPiranhaPlantBlockEntity> {
    private final ResourceLocation model = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "geo/block/potted_piranha_plant.geo.json");
    private final ResourceLocation animations = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "animations/block/potted_piranha_plant.animation.json");

    @Override
    public ResourceLocation getModelResource(PottedPiranhaPlantBlockEntity blockEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(PottedPiranhaPlantBlockEntity blockEntity) {
        String variant = blockEntity.getData(DataAttachmentRegistry.VARIANT);
        if (variant.equals(PiranhaPlantVariants.CHOMPER))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/piranha_plant/potted_chomper.png");
        if (variant.equals(PiranhaPlantVariants.CAVE))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/piranha_plant/potted_cave_piranha_plant.png");
        if (variant.equals(PiranhaPlantVariants.DEEP_CAVE))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/piranha_plant/potted_deep_cave_piranha_plant.png");
        if (variant.equals(PiranhaPlantVariants.TROPICAL))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/piranha_plant/potted_tropical_piranha_plant.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/piranha_plant/potted_piranha_plant.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PottedPiranhaPlantBlockEntity blockEntity) {
        return this.animations;
    }
}