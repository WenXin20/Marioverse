package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.HeftyGoombaEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class HeftyGoombaModel extends DefaultedEntityGeoModel<HeftyGoombaEntity> {
    public HeftyGoombaModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "goomba/hefty_goomba"));
    }

    @Override
    public RenderType getRenderType(HeftyGoombaEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
