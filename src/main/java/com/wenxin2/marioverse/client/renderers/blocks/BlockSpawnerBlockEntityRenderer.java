package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.entities.PipeText;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockSpawnerBlockEntityRenderer implements BlockEntityRenderer<BlockSpawnerBlockEntity> {
    public BlockSpawnerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BlockSpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        BlockState disguiseState = blockEntity.getDisguise();

        if (!disguiseState.isAir()) {
            Minecraft mc = Minecraft.getInstance();

            BlockRenderDispatcher dispatcher = mc.getBlockRenderer();

            poseStack.pushPose();

            dispatcher.renderSingleBlock(disguiseState, poseStack, buffer, packedLight, packedOverlay);

            poseStack.popPose();
        }
    }
}
