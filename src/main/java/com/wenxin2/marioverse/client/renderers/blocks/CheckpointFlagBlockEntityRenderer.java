package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import com.wenxin2.marioverse.client.models.blocks.CheckpointFlagBlockModel;
import com.wenxin2.marioverse.client.models.blocks.GoalPoleBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CheckpointFlagBlockEntityRenderer extends GeoBlockRenderer<CheckpointFlagBlockEntity> {
    public CheckpointFlagBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new CheckpointFlagBlockModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

//    @Override
//    public void render(CheckpointFlagBlockEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
//        BlockState state = animatable.getBlockState();
//
//        if (state.getValue(GoalPoleBlock.FLAG) && animatable.getLevel() != null)
//            super.render(animatable, partialTicks, stack, buffer, packedLight, packedOverlay);
//    }


    @Override
    public boolean shouldRender(CheckpointFlagBlockEntity blockEntity, Vec3 vec3) {
        if (blockEntity.getBlockState().getValue(CheckpointFlagBlock.PART) == TripleBlockStates.BOTTOM)
            return super.shouldRender(blockEntity, vec3);
        else return false;
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(CheckpointFlagBlockEntity animatable) {

        return new AABB(
                animatable.getBlockPos().getX() - 0.8,
                animatable.getBlockPos().getY(),
                animatable.getBlockPos().getZ() - 0.8,
                animatable.getBlockPos().getX() + 1.8,
                animatable.getBlockPos().getY() + 1.0,
                animatable.getBlockPos().getZ() + 1.8
        );
    }
}
