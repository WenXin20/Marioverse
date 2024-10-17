package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.entities.MiniGoombaEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MiniGoombaModel extends DefaultedEntityGeoModel<MiniGoombaEntity> {
    public MiniGoombaModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "goomba/mini_goomba"));
    }

    @Override
    public RenderType getRenderType(MiniGoombaEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
