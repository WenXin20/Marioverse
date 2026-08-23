package com.wenxin2.marioverse.client.renderers.blocks;

import com.wenxin2.marioverse.blocks.LargeStandingArrowSignBlock;
import com.wenxin2.marioverse.blocks.LargeWallArrowSignBlock;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.blocks.states.SideBlockStates;
import com.wenxin2.marioverse.client.models.blocks.ArrowSignBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ArrowSignBlockEntityRenderer extends GeoBlockRenderer<ArrowSignBlockEntity> {
    public ArrowSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ArrowSignBlockModel());
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(ArrowSignBlockEntity animatable) {
        Block block = animatable.getBlockState().getBlock();
        if (!(block instanceof LargeStandingArrowSignBlock) && !(block instanceof LargeWallArrowSignBlock))
            return super.getRenderBoundingBox(animatable);

        BlockPos pos = animatable.getBlockPos();
        return new AABB(pos.getX() - 1.0, pos.getY(), pos.getZ() - 1.0,
                pos.getX() + 2.0, pos.getY() + 2.0, pos.getZ() + 2.0);
    }

    // The large signs now give every part (both halves / all 4 wall quadrants) a real block
    // entity so the block-breaking overlay has something to hook into wherever the player is
    // mining (see the newBlockEntity comments on both blocks). That means every part would
    // otherwise render its own full copy of the sign; suppress the normal render for everything
    // but the primary part here, the same way StarCoinBlockEntityRenderer does for its 7 extra
    // quadrants.
    @Nullable
    @Override
    public RenderType getRenderType(ArrowSignBlockEntity animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        BlockState state = animatable.getBlockState();
        Block block = state.getBlock();

        if (block instanceof LargeStandingArrowSignBlock
                && state.getValue(LargeStandingArrowSignBlock.HALF) != HalfBlockStates.BOTTOM)
            return null;
        if (block instanceof LargeWallArrowSignBlock
                && (state.getValue(LargeWallArrowSignBlock.HALF) != HalfBlockStates.BOTTOM
                        || state.getValue(LargeWallArrowSignBlock.SIDE) != SideBlockStates.LEFT))
            return null;

        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}