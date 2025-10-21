package com.wenxin2.marioverse.client.models.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.SplunkinEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SplunkinModel extends GeoModel<SplunkinEntity> {
    public SplunkinModel() {
        super();
    }

    @Override
    public RenderType getRenderType(SplunkinEntity animatable, ResourceLocation texture) {
        return RenderType.entityCutout(getTextureResource(animatable));
    }

    @Override
    public ResourceLocation getModelResource(SplunkinEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/splunkin/splunkin.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SplunkinEntity animatable) {
        if (animatable.getData(DataAttachmentRegistry.CRACKED))
            return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/splunkin/splunkin_cracked.png");
        else return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/splunkin/splunkin.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SplunkinEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "animations/entity/splunkin/splunkin.animation.json");
    }
}
