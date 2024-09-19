package com.wenxin2.marioverse.client.renderers.entities;

import com.wenxin2.marioverse.client.models.entities.FireFlowerModel;
import com.wenxin2.marioverse.client.models.entities.MushroomModel;
import com.wenxin2.marioverse.entities.FireFlowerEntity;
import com.wenxin2.marioverse.entities.MushroomEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class FireFlowerRenderer extends GeoEntityRenderer<FireFlowerEntity> {
    public FireFlowerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new FireFlowerModel());
    }
}
