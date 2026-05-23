package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.EepCheepEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EepCheepModel extends GeoModel<EepCheepEntity> {
    public EepCheepModel() {
        super();
    }

    @Override
    public RenderType getRenderType(EepCheepEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(EepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/cheep_cheep/eep_cheep.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/eep_cheep.png");
    }

    @Override
    public ResourceLocation getAnimationResource(EepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/cheep_cheep/eep_cheep.animation.json");
    }
}
