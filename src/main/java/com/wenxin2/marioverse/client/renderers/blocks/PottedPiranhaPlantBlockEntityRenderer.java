package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.blocks.entities.PottedPiranhaPlantBlockEntity;
import com.wenxin2.marioverse.client.models.blocks.PottedPiranhaPlantBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PottedPiranhaPlantBlockEntityRenderer extends GeoBlockRenderer<PottedPiranhaPlantBlockEntity> {
    public PottedPiranhaPlantBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new PottedPiranhaPlantBlockModel());
    }

    @Override
    public void render(PottedPiranhaPlantBlockEntity animatable, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        Direction.Axis axis = animatable.getBlockState().getValue(BlockStateProperties.HORIZONTAL_AXIS);
        float yRot = axis == Direction.Axis.X ? 90f : 0f;

        poseStack.pushPose();
            poseStack.translate(0.5, 0.0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
            poseStack.translate(-0.5, 0.0, -0.5);
            super.render(animatable, partialTick, poseStack, bufferSource, packedLight, packedOverlay);
        poseStack.popPose();
    }
}
