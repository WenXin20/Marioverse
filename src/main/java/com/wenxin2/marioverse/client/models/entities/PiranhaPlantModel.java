package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.model.GeoModel;

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
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = getAnimationProcessor().getBone("head");

        if (animatable.isBaby()) {
            if (head != null) {
                head.setScaleX(head.getScaleX() * 1.1f);
                head.setScaleY(head.getScaleX() * 1.1f);
                head.setScaleZ(head.getScaleX() * 1.1f);
            }
        }
    }
}
