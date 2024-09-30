package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.GoombaEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class GoombaModel extends DefaultedEntityGeoModel<GoombaEntity> {
    public GoombaModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "goomba/goomba"));
    }

    @Override
    public RenderType getRenderType(GoombaEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }
}
