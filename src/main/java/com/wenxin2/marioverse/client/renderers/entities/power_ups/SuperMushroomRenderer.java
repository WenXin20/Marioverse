package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.wenxin2.marioverse.client.models.entities.SuperMushroomModel;
import com.wenxin2.marioverse.entities.power_ups.SuperMushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SuperMushroomRenderer extends GeoEntityRenderer<SuperMushroomEntity> {
    public SuperMushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SuperMushroomModel());
        this.shadowRadius = 0.5F;
    }
}