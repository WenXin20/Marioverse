package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.DeepCheepEntity;
import com.wenxin2.marioverse.entities.DeepCheepEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DeepCheepModel extends GeoModel<DeepCheepEntity> {
    public DeepCheepModel() {
        super();
    }

    @Override
    public RenderType getRenderType(DeepCheepEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(DeepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/cheep_cheep/deep_cheep.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DeepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/deep_cheep.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DeepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/cheep_cheep/deep_cheep.animation.json");
    }
}
