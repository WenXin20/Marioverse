package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.PokeyBodyEntity;
import com.wenxin2.marioverse.entities.SnowPokeyBodyEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PokeyBodyModel extends GeoModel<PokeyBodyEntity> {
    public PokeyBodyModel() {
        super();
    }

    @Override
    public RenderType getRenderType(PokeyBodyEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutoutNoCull(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(PokeyBodyEntity animatable) {
        if (animatable instanceof SnowPokeyBodyEntity)
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/pokey/snow_pokey_body.geo.json");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/pokey/pokey_body.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PokeyBodyEntity animatable) {
        if (animatable instanceof SnowPokeyBodyEntity)
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/pokey/snow_pokey.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/pokey/pokey.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PokeyBodyEntity animatable) {
        if (animatable instanceof SnowPokeyBodyEntity)
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/pokey/snow_pokey_body.animation.json");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/pokey/pokey_body.animation.json");
    }

    @Override
    public void setCustomAnimations(PokeyBodyEntity animatable, long instanceId, AnimationState<PokeyBodyEntity> animationState) {
        GeoBone body = this.getAnimationProcessor().getBone("body");
        int rotIndex = Math.floorMod(animatable.getUUID().hashCode(), 4);
        float degrees = rotIndex * 90F;
        float radians = (float) Math.toRadians(degrees);

        if (body != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            if (entityData != null)
                body.setRotY(radians);
        }
    }
}
