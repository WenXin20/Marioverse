package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.EepCheepEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

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

    @Override
    public void setCustomAnimations(EepCheepEntity animatable, long instanceId, AnimationState<EepCheepEntity> animationState) {
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
