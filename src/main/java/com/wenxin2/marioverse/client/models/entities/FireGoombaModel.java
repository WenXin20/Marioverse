package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.FireGoombaEntity;
import com.wenxin2.marioverse.entities.GoombaEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class FireGoombaModel extends DefaultedEntityGeoModel<FireGoombaEntity> {
    public FireGoombaModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "goomba/fire_goomba"));
    }

    @Override
    public RenderType getRenderType(FireGoombaEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
