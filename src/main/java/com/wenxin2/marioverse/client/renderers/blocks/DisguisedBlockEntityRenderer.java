package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wenxin2.marioverse.blocks.entities.DisguisedBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

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
            if (disguiseState.getBlock() instanceof EntityBlock && blockEntity.getLevel() != null) {
                BlockEntity disguisedBE = blockEntity.getDisguiseBlockEntity();
                int light = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos());

                if (disguisedBE != null) {
                    BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(disguisedBE);

                    if (renderer != null)
                        renderer.render(disguisedBE, partialTick, poseStack, buffer, light, packedOverlay);
                }
            }
        poseStack.popPose();
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(DisguisedBlockEntity blockEntity) {
        BlockEntity disguisedBE = blockEntity.getDisguiseBlockEntity();
        if (disguisedBE != null && blockEntity.getBlockState().getValue(BlockStatePropertyRegistry.DISGUISED)) {
            BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(disguisedBE);

            if (renderer != null)
                return renderer.getRenderBoundingBox(disguisedBE);
        }
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }
}