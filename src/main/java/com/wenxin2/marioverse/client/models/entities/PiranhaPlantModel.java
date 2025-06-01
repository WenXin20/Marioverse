package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PiranhaPlantModel extends GeoModel<PiranhaPlantEntity> {
    public PiranhaPlantModel() {
        super();
    }

    @Override
    public RenderType getRenderType(PiranhaPlantEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(PiranhaPlantEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/piranha_plant/piranha_plant.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PiranhaPlantEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/piranha_plant/piranha_plant.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PiranhaPlantEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/piranha_plant/piranha_plant.animation.json");
    }

    @Override
    public void setCustomAnimations(PiranhaPlantEntity animatable, long instanceId, AnimationState<PiranhaPlantEntity> animationState) {
        GeoBone head = this.getAnimationProcessor().getBone("head_rotator");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotZ(entityData.headPitch() * 0.030F);
            head.setRotY(entityData.netHeadYaw() * 0.025F);
        }
    }
}
