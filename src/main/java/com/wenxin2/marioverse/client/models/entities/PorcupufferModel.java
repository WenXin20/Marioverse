package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.PorcupufferEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PorcupufferModel extends GeoModel<PorcupufferEntity> {
    public PorcupufferModel() {
        super();
    }

    @Override
    public RenderType getRenderType(PorcupufferEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(PorcupufferEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/porcupuffer/porcupuffer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PorcupufferEntity animatable) {
        if (animatable.getData(DataAttachmentRegistry.IS_MOUTH_OPEN)) {
            if (animatable.hasCustomName() && animatable.isMrsPuff())
                return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/mrs_puff_open_mouth.png");
            if (animatable.hasCustomName() && animatable.isQwilfish())
                return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/qwilfish_open_mouth.png");
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/porcupuffer_open_mouth.png");
        }
        if (animatable.hasCustomName() && animatable.isMrsPuff())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/mrs_puff.png");
        if (animatable.hasCustomName() && animatable.isQwilfish())
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/qwilfish.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/porcupuffer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PorcupufferEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/porcupuffer/porcupuffer.animation.json");
    }

    @Override
    public void setCustomAnimations(PorcupufferEntity animatable, long instanceId, AnimationState<PorcupufferEntity> animationState) {
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