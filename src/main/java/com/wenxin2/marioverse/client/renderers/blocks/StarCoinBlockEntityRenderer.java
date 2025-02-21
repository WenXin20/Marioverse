package com.wenxin2.marioverse.client.renderers.blocks;

import com.wenxin2.marioverse.blocks.StarCoinBlock;
import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import com.wenxin2.marioverse.blocks.states.QuadrantBlockStates;
import com.wenxin2.marioverse.client.models.blocks.StarCoinBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class StarCoinBlockEntityRenderer extends GeoBlockRenderer<StarCoinBlockEntity> {
    public StarCoinBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new StarCoinBlockModel());
    }

    @Nullable
    @Override
    public RenderType getRenderType(StarCoinBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        BlockState state = animatable.getBlockState();

        if (state.getValue(StarCoinBlock.HALF) == DoubleBlockHalf.LOWER
                && state.getValue(StarCoinBlock.QUADRANT) == QuadrantBlockStates.NORTH_WEST)
            return super.getRenderType(animatable, texture, bufferSource, partialTick);
        else return null;
    }
}
