package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.DryBonesEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class DryBonesModel extends GeoModel<DryBonesEntity> {
    public DryBonesModel() {
        super();
    }

    @Override
    public RenderType getRenderType(DryBonesEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(DryBonesEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/dry_bones.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DryBonesEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/dry_bones/dry_bones.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DryBonesEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/dry_bones/dry_bones.animation.json");
    }

    @Override
    public void setCustomAnimations(DryBonesEntity animatable, long instanceId, AnimationState<DryBonesEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("bipedHead");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            if (entityData != null) {
                head.setRotX(entityData.headPitch() * 0.017453292F);
                head.setRotY(entityData.netHeadYaw() * 0.017453292F);
            }
        }
    }
}
