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
import org.jetbrains.annotations.NotNull;
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

    @NotNull
    @Override
    public AABB getRenderBoundingBox(GoalPoleBlockEntity animatable) {
        BlockState state = animatable.getBlockState();

        if (state.getValue(GoalPoleBlock.FLAG)) {
            if (state.getValue(GoalPoleBlock.COLUMN) == ColumnBlockStates.NONE)
                return new AABB(
                        animatable.getBlockPos().getX() - 0.8,
                        animatable.getBlockPos().getY(),
                        animatable.getBlockPos().getZ() - 0.8,
                        animatable.getBlockPos().getX() + 1.8,
                        animatable.getBlockPos().getY() + 0.6,
                        animatable.getBlockPos().getZ() + 1.8
                );
            else return new AABB(
                    animatable.getBlockPos().getX() - 0.8,
                    animatable.getBlockPos().getY() - 0.8,
                    animatable.getBlockPos().getZ() - 0.8,
                    animatable.getBlockPos().getX() + 1.8,
                    animatable.getBlockPos().getY() + 0.6,
                    animatable.getBlockPos().getZ() + 1.8
            );
        } else return new AABB(0, 0, 0, 0, 0, 0);
    }
}
