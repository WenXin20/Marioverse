package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class FireFlowerModel extends DefaultedEntityGeoModel<FireFlowerEntity> {
    public FireFlowerModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "power_up/fire_flower"));
    }

    @Override
    public RenderType getRenderType(FireFlowerEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(this.getTextureResource(animatable));
    }

    @Override
    public void setCustomAnimations(FireFlowerEntity animatable, long instanceId, AnimationState<FireFlowerEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("flower");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            if (entityData != null) {
                head.setRotX(entityData.headPitch() * 0.017453292F);
                head.setRotY(entityData.netHeadYaw() * 0.017453292F);
            }
        }
    }
}
