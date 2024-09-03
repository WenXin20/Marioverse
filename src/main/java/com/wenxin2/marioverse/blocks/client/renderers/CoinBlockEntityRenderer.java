package com.wenxin2.marioverse.blocks.client.renderers;

import com.wenxin2.marioverse.blocks.client.models.CoinBlockModel;
import com.wenxin2.marioverse.blocks.entities.CoinBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CoinBlockEntityRenderer extends GeoBlockRenderer<CoinBlockEntity> {
    public CoinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new CoinBlockModel());
    }
}
