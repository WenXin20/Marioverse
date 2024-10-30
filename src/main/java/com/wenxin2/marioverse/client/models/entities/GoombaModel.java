package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.GoombaEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class GoombaModel extends GeoModel<GoombaEntity> {
    public GoombaModel() {
        super();
    }

    @Override
    public RenderType getRenderType(GoombaEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(GoombaEntity animatable) {
        if (animatable.hasCustomName() && "Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/goomba/goombella.geo.json");
        } else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/goomba/goomba.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GoombaEntity animatable) {
        if (animatable.hasCustomName() && "Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goomba/goombella.png");
        } else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/goomba/goomba.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GoombaEntity animatable) {
        if (animatable.hasCustomName() && "Goombella".equals(ChatFormatting.stripFormatting(animatable.getName().getString()))) {
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/goomba/goombella.animation.json");
        } else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/goomba/goomba.animation.json");
    }
}
