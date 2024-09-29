package com.wenxin2.marioverse.client.renderers.entities;

import com.wenxin2.marioverse.client.models.entities.OneUpMushroomModel;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class OneUpMushroomRenderer extends GeoEntityRenderer<OneUpMushroomEntity> {
    public OneUpMushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new OneUpMushroomModel());
    }
}
