package com.wenxin2.marioverse.client.renderers.entities;

import com.wenxin2.marioverse.client.models.entities.PiranhaPlantModel;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PiranhaPlantRenderer extends GeoEntityRenderer<PiranhaPlantEntity> {
    public PiranhaPlantRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PiranhaPlantModel());
    }

    @Override
    protected float getDeathMaxRotation(PiranhaPlantEntity animatable) {
        return 0.0F;
    }
}
