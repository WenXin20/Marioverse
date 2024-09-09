package com.wenxin2.marioverse.client.renderer.entities;

import com.wenxin2.marioverse.client.model.entities.MushroomModel;
import com.wenxin2.marioverse.entities.MushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MushroomRenderer extends GeoEntityRenderer<MushroomEntity> {
    public MushroomRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MushroomModel());
    }
}
