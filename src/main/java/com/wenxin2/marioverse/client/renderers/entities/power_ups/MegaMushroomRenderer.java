package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.wenxin2.marioverse.client.models.entities.MegaMushroomModel;
import com.wenxin2.marioverse.entities.power_ups.MegaMushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MegaMushroomRenderer extends GeoEntityRenderer<MegaMushroomEntity> {
    public MegaMushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MegaMushroomModel());
        this.shadowRadius = 1.25F;
    }
}