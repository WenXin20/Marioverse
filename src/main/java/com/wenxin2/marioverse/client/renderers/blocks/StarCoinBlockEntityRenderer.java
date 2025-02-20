package com.wenxin2.marioverse.client.renderers.blocks;

import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import com.wenxin2.marioverse.client.models.blocks.StarCoinBlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StarCoinBlockEntityRenderer extends GeoBlockRenderer<StarCoinBlockEntity> {
    public StarCoinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new StarCoinBlockModel());
    }
}
