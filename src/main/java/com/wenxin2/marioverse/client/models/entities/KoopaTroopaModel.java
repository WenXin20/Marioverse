package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class KoopaTroopaModel extends GeoModel<KoopaTroopaEntity> {
    public KoopaTroopaModel() {
        super();
    }

    @Override
    public RenderType getRenderType(KoopaTroopaEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(KoopaTroopaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/koopa_troopa/koopa_troopa.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KoopaTroopaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/koopa_troopa/green_koopa_troopa.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KoopaTroopaEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/koopa_troopa/koopa_troopa.animation.json");
    }
}
