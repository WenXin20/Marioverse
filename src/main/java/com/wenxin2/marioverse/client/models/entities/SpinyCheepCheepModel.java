package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.SpinyCheepCheepEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SpinyCheepCheepModel extends GeoModel<SpinyCheepCheepEntity> {
    public SpinyCheepCheepModel() {
        super();
    }

    @Override
    public RenderType getRenderType(SpinyCheepCheepEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(SpinyCheepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/cheep_cheep/spiny_cheep_cheep.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpinyCheepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/spiny_cheep_cheep.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpinyCheepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/cheep_cheep/spiny_cheep_cheep.animation.json");
    }
}
