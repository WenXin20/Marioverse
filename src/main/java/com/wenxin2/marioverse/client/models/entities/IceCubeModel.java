package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.IceCubeEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class IceCubeModel extends GeoModel<IceCubeEntity> {
    @Override
    public ResourceLocation getModelResource(IceCubeEntity object) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/ice_cube.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(IceCubeEntity object) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/ice_cube.png");
    }

    @Override
    public ResourceLocation getAnimationResource(IceCubeEntity object) {
        return null;
    }

    @Override
    public RenderType getRenderType(IceCubeEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(this.getTextureResource(animatable));
    }
}