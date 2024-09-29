package com.wenxin2.marioverse.client.renderers.entities;

import com.wenxin2.marioverse.client.models.entities.MushroomModel;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MushroomRenderer extends GeoEntityRenderer<MushroomEntity> {
    public MushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MushroomModel());
    }
}
