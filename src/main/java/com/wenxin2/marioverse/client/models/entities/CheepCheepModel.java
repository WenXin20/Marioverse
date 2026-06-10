package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.CheepCheepEntity;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

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
        String variant = animatable.getVariant();
        if (animatable.getVariant().equals(CheepCheepVariants.COLD))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/cold_cheep_cheep.png");
        if (animatable.getVariant().equals(CheepCheepVariants.WARM))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/warm_cheep_cheep.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/cheep_cheep/cheep_cheep.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CheepCheepEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/cheep_cheep/cheep_cheep.animation.json");
    }

    @Override
    public void setCustomAnimations(CheepCheepEntity animatable, long instanceId, AnimationState<CheepCheepEntity> animationState) {
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
