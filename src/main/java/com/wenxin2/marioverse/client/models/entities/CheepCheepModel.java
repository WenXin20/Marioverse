package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.CheepCheepEntity;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CheepCheepModel extends GeoModel<CheepCheepEntity> {
    public CheepCheepModel() {
        super();
    }

    @Override
    public RenderType getRenderType(CheepCheepEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(CheepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/cheep_cheep/cheep_cheep.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CheepCheepEntity animatable) {
        ResourceLocation variant = animatable.getVariant();
        if (animatable.getVariant() == CheepCheepVariants.NORMAL)
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/cheep_cheep.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/" + variant.getPath() + "_cheep_cheep.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CheepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/cheep_cheep/cheep_cheep.animation.json");
    }
}
