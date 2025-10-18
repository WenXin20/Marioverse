package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.BooEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class BooModel extends GeoModel<BooEntity> {
    public BooModel() {
        super();
    }

    @Override
    public RenderType getRenderType(BooEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(BooEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/boo/boo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BooEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/boo/boo.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BooEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/boo/boo.animation.json");
    }
}
