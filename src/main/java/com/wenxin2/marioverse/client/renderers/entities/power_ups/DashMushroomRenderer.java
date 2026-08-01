package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.wenxin2.marioverse.client.models.entities.DashMushroomModel;
import com.wenxin2.marioverse.entities.power_ups.DashMushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DashMushroomRenderer extends GeoEntityRenderer<DashMushroomEntity> {
    public DashMushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DashMushroomModel());
        this.shadowRadius = 0.5F;
    }
}