package com.wenxin2.marioverse.client.renderers.blocks;

import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.client.models.blocks.ArrowSignBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ArrowSignBlockEntityRenderer extends GeoBlockRenderer<ArrowSignBlockEntity> {
    public ArrowSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ArrowSignBlockModel());
    }
}