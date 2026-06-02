package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.PorcupufferEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PorcupufferModel extends GeoModel<PorcupufferEntity> {
    public PorcupufferModel() {
        super();
    }

    @Override
    public RenderType getRenderType(PorcupufferEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(PorcupufferEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/porcupuffer/porcupuffer.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PorcupufferEntity animatable) {
        if (animatable.getData(DataAttachmentRegistry.IS_MOUTH_OPEN))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/porcupuffer_biting.png");
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/porcupuffer/porcupuffer.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PorcupufferEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/porcupuffer/porcupuffer.animation.json");
    }
}