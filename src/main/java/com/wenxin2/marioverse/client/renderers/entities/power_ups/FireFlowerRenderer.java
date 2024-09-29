package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.wenxin2.marioverse.client.models.entities.FireFlowerModel;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class FireFlowerRenderer extends GeoEntityRenderer<FireFlowerEntity> {
    public FireFlowerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireFlowerModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
