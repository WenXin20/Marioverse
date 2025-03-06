package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.wenxin2.marioverse.client.models.entities.IceFlowerModel;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class IceFlowerRenderer extends GeoEntityRenderer<IceFlowerEntity> {
    public IceFlowerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceFlowerModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
