package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.blocks.entities.DisguisedBlockEntity;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class DisguisedBlockEntityRenderer implements BlockEntityRenderer<DisguisedBlockEntity> {
    public DisguisedBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(DisguisedBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        BlockState disguiseState = blockEntity.getDisguiseState();

        if (disguiseState == null || disguiseState.isAir()
                || disguiseState.is(TagRegistry.BLOCK_SPAWNER_CANNOT_DISGUISE))
            return;

        poseStack.pushPose();
            if (disguiseState.getBlock() instanceof EntityBlock) {
                BlockEntity disguisedBE = blockEntity.getDisguiseBlockEntity();

                if (disguisedBE != null) {
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(disguiseState, poseStack, buffer, packedLight, packedOverlay);
                    BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(disguisedBE);

                    if (renderer != null)
                        renderer.render(disguisedBE, partialTick, poseStack, buffer, packedLight, packedOverlay);
                }
            }
        poseStack.popPose();
    }
}