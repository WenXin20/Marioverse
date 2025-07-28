package com.wenxin2.marioverse.client.renderers.blocks;

import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import com.wenxin2.marioverse.client.models.blocks.CheckpointFlagBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CheckpointFlagBlockEntityRenderer extends GeoBlockRenderer<CheckpointFlagBlockEntity> {
    public CheckpointFlagBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new CheckpointFlagBlockModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this){
            @Override
            protected @Nullable RenderType getRenderType(CheckpointFlagBlockEntity animatable, @Nullable MultiBufferSource bufferSource) {
                BlockState state = animatable.getBlockState();

                if (state.getValue(CheckpointFlagBlock.PART) == TripleBlockStates.BOTTOM)
                    return super.getRenderType(animatable, bufferSource);
                else return null;
            }
        });
    }

    @Nullable
    @Override
    public RenderType getRenderType(CheckpointFlagBlockEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        BlockState state = animatable.getBlockState();

        if (state.getValue(CheckpointFlagBlock.PART) == TripleBlockStates.BOTTOM)
            return super.getRenderType(animatable, texture, bufferSource, partialTick);
        else return null;
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(CheckpointFlagBlockEntity animatable) {

        if (animatable.getBlockState().getValue(CheckpointFlagBlock.PART) == TripleBlockStates.BOTTOM) {
            return new AABB(
                    animatable.getBlockPos().getX() - 0.5,
                    animatable.getBlockPos().getY(),
                    animatable.getBlockPos().getZ() - 0.5,
                    animatable.getBlockPos().getX() + 1.5,
                    animatable.getBlockPos().getY() + 2.5,
                    animatable.getBlockPos().getZ() + 1.5

            );
        } else return new AABB(0, 0, 0, 0, 0, 0);
    }
}
