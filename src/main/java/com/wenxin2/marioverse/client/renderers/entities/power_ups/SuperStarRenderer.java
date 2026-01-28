package com.wenxin2.marioverse.client.renderers.entities.power_ups;

import com.wenxin2.marioverse.client.models.entities.SuperStarModel;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SuperStarRenderer extends GeoEntityRenderer<SuperStarEntity> {
    public SuperStarRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SuperStarModel());
        this.shadowRadius = 0.35F;
    }
}
