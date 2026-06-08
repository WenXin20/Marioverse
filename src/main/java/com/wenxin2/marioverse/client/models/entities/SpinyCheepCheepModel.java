package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.SpinyCheepCheepEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

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

    @Override
    public void setCustomAnimations(SpinyCheepCheepEntity animatable, long instanceId, AnimationState<SpinyCheepCheepEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("bipedBody");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            if (entityData != null) {
                head.setRotX(entityData.headPitch() * 0.017453292F);
                head.setRotY(entityData.netHeadYaw() * 0.017453292F);
            }
        }
    }
}
