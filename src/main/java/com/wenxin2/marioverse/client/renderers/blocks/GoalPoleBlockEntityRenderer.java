package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.client.models.blocks.GoalPoleBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GoalPoleBlockEntityRenderer extends GeoBlockRenderer<GoalPoleBlockEntity> {
    public GoalPoleBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new GoalPoleBlockModel());
    }

    @Override
    public void render(GoalPoleBlockEntity animatable, float partialTicks, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState state = animatable.getBlockState();

        if (state.getValue(GoalPoleBlock.FLAG) && animatable.getLevel() != null) {
            super.render(animatable, partialTicks, stack, buffer, packedLight, packedOverlay);
        }
    }

    @Override
    public AABB getRenderBoundingBox(GoalPoleBlockEntity blockEntity) {
        BlockState state = blockEntity.getBlockState();
        int rotationState = state.getValue(GoalPoleBlock.ROTATION);
        double centerX = blockEntity.getBlockPos().getX() + 0.325;
        double centerY = blockEntity.getBlockPos().getY();
        double centerZ = blockEntity.getBlockPos().getZ() + 0.325;
        double[][] offsets = {
                {-1.25, 0},       // 0 degrees (East)
                {-1.17, -0.48},   // 22.5 degrees
                {-0.88, -0.88},   // 45 degrees
                {-0.48, -1.17},   // 67.5 degrees
                {0, -1.25},       // 90 degrees (South)
                {0.48, -1.17},    // 112.5 degrees
                {0.88, -0.88},    // 135 degrees
                {1.17, -0.48},    // 157.5 degrees
                {1.25, 0},        // 180 degrees (West)
                {1.17, 0.48},     // 202.5 degrees
                {0.88, 0.88},     // 225 degrees
                {0.48, 1.17},     // 247.5 degrees
                {0, 1.25},        // 270 degrees (North)
                {-0.48, 1.17},    // 292.5 degrees
                {-0.88, 0.88},    // 315 degrees
                {-1.17, 0.48}     // 337.5 degrees
        };
        double offsetX = offsets[rotationState][0];
        double offsetZ = offsets[rotationState][1];

        if (state.getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.NONE)
            return new AABB(blockEntity.getBlockPos());
        else return new AABB(
                centerX + offsetX - 0.625,
                centerY - 0.5,
                centerZ + offsetZ - 0.625,
                centerX + offsetX + 0.625,
                centerY + 0.6,
                centerZ + offsetZ + 0.625
        );
    }
}
