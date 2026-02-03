package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.wenxin2.marioverse.client.models.entities.MiniMushroomModel;
import com.wenxin2.marioverse.entities.power_ups.MiniMushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MiniMushroomRenderer extends GeoEntityRenderer<MiniMushroomEntity> {
    public MiniMushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MiniMushroomModel());
        this.shadowRadius = 0.25F;
    }
}