package com.wenxin2.marioverse.client.renderers.entities;

import com.wenxin2.marioverse.client.models.entities.PokeyBodyModel;
import com.wenxin2.marioverse.entities.PokeyBodyEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PokeyBodyRenderer extends GeoEntityRenderer<PokeyBodyEntity> {
    public PokeyBodyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PokeyBodyModel());
        this.shadowRadius = 0.5F;
    }
}