package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.PokeyEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PokeyModel extends GeoModel<PokeyEntity> {
    public PokeyModel() {
        super();
    }

    @Override
    public RenderType getRenderType(PokeyEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(PokeyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/pokey/pokey.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PokeyEntity animatable) {
//        if (animatable.getData(DataAttachmentRegistry.IS_HIDING.get()))
//            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/pokey/pokey_hide.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/pokey/pokey.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PokeyEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/pokey/pokey.animation.json");
    }

    @Override
    public void setCustomAnimations(PokeyEntity animatable, long instanceId, AnimationState<PokeyEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            if (entityData != null) {
                head.setRotX(entityData.headPitch() * 0.017453292F);
                head.setRotY(entityData.netHeadYaw() * 0.017453292F);
            }
        }
    }
}
