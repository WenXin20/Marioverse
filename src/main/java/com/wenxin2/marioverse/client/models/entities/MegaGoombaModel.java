package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.MegaGoombaEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class MegaGoombaModel extends DefaultedEntityGeoModel<MegaGoombaEntity> {
    public MegaGoombaModel() {
        super(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "goomba/mega_goomba"));
    }

    @Override
    public RenderType getRenderType(MegaGoombaEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }
}
