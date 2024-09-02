package com.wenxin2.marioverse.blocks.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.PipeText;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.Config;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
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

public class WarpPipeBlockEntityRenderer implements BlockEntityRenderer<WarpPipeBlockEntity> {
    private static final float TEXT_RENDER_SCALE = 0.0125F;
    private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
    private final Font font;

    public WarpPipeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.font = context.getFont();
    }

    @Override
    public void render(WarpPipeBlockEntity blockEntity, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!Config.DISABLE_TEXT.get()) this.renderPipeWithText(blockEntity, stack, buffer, packedLight);
    }

    void renderPipeWithText(WarpPipeBlockEntity pipeBlockEntity, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        BlockState state = pipeBlockEntity.getBlockState();

        stack.pushPose();
        if (state.getValue(WarpPipeBlock.ENTRANCE)
                && !(state.getValue(WarpPipeBlock.FACING) == Direction.NORTH || state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH)) {
            if (pipeBlockEntity.hasTextNorth())
                this.renderPipeTextNorth(pipeBlockEntity, pipeBlockEntity.getBlockPos(), pipeBlockEntity.getPipeText(), stack, buffer, packedLight,
                        pipeBlockEntity.getTextLineHeight(), pipeBlockEntity.getMaxTextLineWidth());
            if (pipeBlockEntity.hasTextSouth())
                this.renderPipeTextSouth(pipeBlockEntity, pipeBlockEntity.getBlockPos(), pipeBlockEntity.getPipeText(), stack, buffer, packedLight,
                        pipeBlockEntity.getTextLineHeight(), pipeBlockEntity.getMaxTextLineWidth());
        }

        if (state.getValue(WarpPipeBlock.ENTRANCE)
                && !(state.getValue(WarpPipeBlock.FACING) == Direction.EAST || state.getValue(WarpPipeBlock.FACING) == Direction.WEST)) {
            if (pipeBlockEntity.hasTextEast())
                this.renderPipeTextEast(pipeBlockEntity, pipeBlockEntity.getBlockPos(), pipeBlockEntity.getPipeText(), stack, buffer, packedLight,
                    pipeBlockEntity.getTextLineHeight(), pipeBlockEntity.getMaxTextLineWidth());
            if (pipeBlockEntity.hasTextWest())
                this.renderPipeTextWest(pipeBlockEntity, pipeBlockEntity.getBlockPos(), pipeBlockEntity.getPipeText(), stack, buffer, packedLight,
                    pipeBlockEntity.getTextLineHeight(), pipeBlockEntity.getMaxTextLineWidth());
        }

        if (state.getValue(WarpPipeBlock.ENTRANCE)
                && !(state.getValue(WarpPipeBlock.FACING) == Direction.UP || state.getValue(WarpPipeBlock.FACING) == Direction.DOWN)) {
            if (pipeBlockEntity.hasTextAbove())
                this.renderPipeTextAbove(pipeBlockEntity, pipeBlockEntity.getBlockPos(), pipeBlockEntity.getPipeText(), stack, buffer, packedLight,
                    pipeBlockEntity.getTextLineHeight(), pipeBlockEntity.getMaxTextLineWidth());
            if (pipeBlockEntity.hasTextBelow())
                this.renderPipeTextBelow(pipeBlockEntity, pipeBlockEntity.getBlockPos(), pipeBlockEntity.getPipeText(), stack, buffer, packedLight,
                    pipeBlockEntity.getTextLineHeight(), pipeBlockEntity.getMaxTextLineWidth());
        }
        stack.popPose();
    }

    void renderPipeTextNorth(WarpPipeBlockEntity pipeBlockEntity, BlockPos pos, PipeText pipeText, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth) {
        BlockState state = pipeBlockEntity.getBlockState();
        Level world = pipeBlockEntity.getLevel();

        FormattedCharSequence[] pipeNameArray = pipeText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        stack.pushPose();

        int textColor;
        int packedLightL;
        boolean hasGlowingText;
        if (pipeText.hasGlowingText()) {
            textColor = pipeText.getColor().getTextColor();
            hasGlowingText = isOutlineVisible(pos, textColor);
            packedLightL = 15728880;
        } else {
            textColor = getDarkColor(pipeText);
            packedLightL = 0xFFFFFF;
            hasGlowingText = false;
        }

        if (world != null) {
            stack.pushPose();

            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP) {
                stack.translate(0.5, 0.825, -0.001);
                stack.mulPose(Axis.YP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN){
                stack.translate(0.5, 0.175, -0.001);
                stack.mulPose(Axis.YP.rotationDegrees(180F));
                stack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST){
                stack.translate(0.825, 0.5, -0.001);
                stack.mulPose(Axis.YP.rotationDegrees(180F));
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST){
                stack.translate(0.175, 0.5, -0.001);
                stack.mulPose(Axis.YP.rotationDegrees(180F));
                stack.mulPose(Axis.ZP.rotationDegrees(270F));
            }

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(TEXT_RENDER_SCALE, TEXT_RENDER_SCALE, TEXT_RENDER_SCALE);
            stack.translate(0.0, -(this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < 1; j++) {
                BlockState stateNorth = world.getBlockState(pos.north());
                FormattedCharSequence pipeName = pipeNameArray[j];
                if (state.getBlock() instanceof ClearWarpPipeBlock)
                    stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 7, 0.0);
                else stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 2, 0.0);

                if (!(stateNorth.isSolid() && stateNorth.isSolidRender(world, pos.north()))
                        && !(state.getBlock() instanceof ClearWarpPipeBlock && stateNorth.getBlock() instanceof ClearWarpPipeBlock)) {
                    if (hasGlowingText) {
                        this.font.drawInBatch8xOutline(pipeName, 0, 0, textColor, getDarkColor(pipeText),
                                stack.last().pose(), buffer, packedLightL);
                    } else {
                        this.font.drawInBatch(pipeName, 0, 0, textColor, false,
                                stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                    }
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }

    void renderPipeTextSouth(WarpPipeBlockEntity pipeBlockEntity, BlockPos pos, PipeText pipeText, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth) {
        BlockState state = pipeBlockEntity.getBlockState();
        Level world = pipeBlockEntity.getLevel();

        FormattedCharSequence[] pipeNameArray = pipeText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        stack.pushPose();

        int textColor;
        int packedLightL;
        boolean hasGlowingText;
        if (pipeText.hasGlowingText()) {
            textColor = pipeText.getColor().getTextColor();
            hasGlowingText = isOutlineVisible(pos, textColor);
            packedLightL = 15728880;
        } else {
            textColor = getDarkColor(pipeText);
            packedLightL = 0xFFFFFF;
            hasGlowingText = false;
        }

        if (world != null) {
            stack.pushPose();

            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP) {
                stack.translate(0.5, 0.825, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN){
                stack.translate(0.5, 0.175, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
                stack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST){
                stack.translate(0.825, 0.5, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
                stack.mulPose(Axis.ZP.rotationDegrees(270F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST){
                stack.translate(0.175, 0.5, 1.001);
                stack.mulPose(Axis.YP.rotationDegrees(0F));
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
            }

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(TEXT_RENDER_SCALE, TEXT_RENDER_SCALE, TEXT_RENDER_SCALE);
            stack.translate(0.0, -(this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < 1; j++) {
                BlockState stateSouth = world.getBlockState(pos.south());
                FormattedCharSequence pipeName = pipeNameArray[j];
                if (state.getBlock() instanceof ClearWarpPipeBlock)
                    stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 7, 0.0);
                else stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 2, 0.0);

                if (!(stateSouth.isSolid() && stateSouth.isSolidRender(world, pos.south()))
                        && !(state.getBlock() instanceof ClearWarpPipeBlock && stateSouth.getBlock() instanceof ClearWarpPipeBlock)) {
                    if (hasGlowingText) {
                        this.font.drawInBatch8xOutline(pipeName, 0, 0, textColor, getDarkColor(pipeText),
                                stack.last().pose(), buffer, packedLightL);
                    } else {
                        this.font.drawInBatch(pipeName, 0, 0, textColor, false,
                                stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                    }
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }

    void renderPipeTextEast(WarpPipeBlockEntity pipeBlockEntity, BlockPos pos, PipeText pipeText, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth) {
        BlockState state = pipeBlockEntity.getBlockState();
        Level world = pipeBlockEntity.getLevel();

        FormattedCharSequence[] pipeNameArray = pipeText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        stack.pushPose();

        int textColor;
        int packedLightL;
        boolean hasGlowingText;
        if (pipeText.hasGlowingText()) {
            textColor = pipeText.getColor().getTextColor();
            hasGlowingText = isOutlineVisible(pos, textColor);
            packedLightL = 15728880;
        } else {
            textColor = getDarkColor(pipeText);
            packedLightL = 0xFFFFFF;
            hasGlowingText = false;
        }

        if (world != null) {
            stack.pushPose();

            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP) {
                stack.translate(1.001, 0.825, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(90F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN){
                stack.translate(1.001, 0.175, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(90F));
                stack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH){
                stack.translate(1.001, 0.5, 0.175);
                stack.mulPose(Axis.YP.rotationDegrees(90F));
                stack.mulPose(Axis.ZP.rotationDegrees(270F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH){
                stack.translate(1.001, 0.5, 0.825);
                stack.mulPose(Axis.YP.rotationDegrees(90F));
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
            }

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(TEXT_RENDER_SCALE, TEXT_RENDER_SCALE, TEXT_RENDER_SCALE);
            stack.translate(0.0, -(this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < 1; j++) {
                BlockState stateEast = world.getBlockState(pos.east());
                FormattedCharSequence pipeName = pipeNameArray[j];
                if (state.getBlock() instanceof ClearWarpPipeBlock)
                    stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 7, 0.0);
                else stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 2, 0.0);

                if (!(stateEast.isSolid() && stateEast.isSolidRender(world, pos.east()))
                        && !(state.getBlock() instanceof ClearWarpPipeBlock && stateEast.getBlock() instanceof ClearWarpPipeBlock)) {
                    if (hasGlowingText) {
                        this.font.drawInBatch8xOutline(pipeName, 0, 0, textColor, getDarkColor(pipeText),
                                stack.last().pose(), buffer, packedLightL);
                    } else {
                        this.font.drawInBatch(pipeName, 0, 0, textColor, false,
                                stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                    }
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }

    void renderPipeTextWest(WarpPipeBlockEntity pipeBlockEntity, BlockPos pos, PipeText pipeText, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth) {
        BlockState state = pipeBlockEntity.getBlockState();
        Level world = pipeBlockEntity.getLevel();

        FormattedCharSequence[] pipeNameArray = pipeText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        stack.pushPose();

        int textColor;
        int packedLightL;
        boolean hasGlowingText;
        if (pipeText.hasGlowingText()) {
            textColor = pipeText.getColor().getTextColor();
            hasGlowingText = isOutlineVisible(pos, textColor);
            packedLightL = 15728880;
        } else {
            textColor = getDarkColor(pipeText);
            packedLightL = 0xFFFFFF;
            hasGlowingText = false;
        }

        if (world != null) {
            stack.pushPose();

            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP) {
                stack.translate(-0.001, 0.825, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(270F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.DOWN){
                stack.translate(-0.001, 0.175, 0.5);
                stack.mulPose(Axis.YP.rotationDegrees(270F));
                stack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH){
                stack.translate(-0.001, 0.5, 0.175);
                stack.mulPose(Axis.YP.rotationDegrees(270F));
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH){
                stack.translate(-0.001, 0.5, 0.825);
                stack.mulPose(Axis.YP.rotationDegrees(270F));
                stack.mulPose(Axis.ZP.rotationDegrees(270F));
            }

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(TEXT_RENDER_SCALE, TEXT_RENDER_SCALE, TEXT_RENDER_SCALE);
            stack.translate(0.0, -(this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < 1; j++) {
                BlockState stateWest = world.getBlockState(pos.west());
                FormattedCharSequence pipeName = pipeNameArray[j];
                if (state.getBlock() instanceof ClearWarpPipeBlock)
                    stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 7, 0.0);
                else stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 2, 0.0);

                if (!(stateWest.isSolid() && stateWest.isSolidRender(world, pos.west()))
                        && !(state.getBlock() instanceof ClearWarpPipeBlock && stateWest.getBlock() instanceof ClearWarpPipeBlock)) {
                    if (hasGlowingText) {
                        this.font.drawInBatch8xOutline(pipeName, 0, 0, textColor, getDarkColor(pipeText),
                                stack.last().pose(), buffer, packedLightL);
                    } else {
                        this.font.drawInBatch(pipeName, 0, 0, textColor, false,
                                stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                    }
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }

    void renderPipeTextAbove(WarpPipeBlockEntity pipeBlockEntity, BlockPos pos, PipeText pipeText, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth) {
        BlockState state = pipeBlockEntity.getBlockState();
        Level world = pipeBlockEntity.getLevel();

        FormattedCharSequence[] pipeNameArray = pipeText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        stack.pushPose();

        int textColor;
        int packedLightL;
        boolean hasGlowingText;
        if (pipeText.hasGlowingText()) {
            textColor = pipeText.getColor().getTextColor();
            hasGlowingText = isOutlineVisible(pos, textColor);
            packedLightL = 15728880;
        } else {
            textColor = getDarkColor(pipeText);
            packedLightL = 0xFFFFFF;
            hasGlowingText = false;
        }

        if (world != null) {
            stack.pushPose();

            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH){
                stack.translate(0.5, 1.001, 0.175);
                stack.mulPose(Axis.XP.rotationDegrees(270F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH){
                stack.translate(0.5, 1.001, 0.825);
                stack.mulPose(Axis.XP.rotationDegrees(270F));
                stack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST){
                stack.translate(0.825, 1.001, 0.5);
                stack.mulPose(Axis.XP.rotationDegrees(270F));
                stack.mulPose(Axis.ZP.rotationDegrees(270F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST){
                stack.translate(0.175, 1.001, 0.5);
                stack.mulPose(Axis.XP.rotationDegrees(270F));
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
            }

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(TEXT_RENDER_SCALE, TEXT_RENDER_SCALE, TEXT_RENDER_SCALE);
            stack.translate(0.0, -(this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < 1; j++) {
                BlockState stateAbove = world.getBlockState(pos.above());
                FormattedCharSequence pipeName = pipeNameArray[j];
                if (state.getBlock() instanceof ClearWarpPipeBlock)
                    stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 7, 0.0);
                else stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 2, 0.0);

                if (!(stateAbove.isSolid() && stateAbove.isSolidRender(world, pos.above()))
                        && !(state.getBlock() instanceof ClearWarpPipeBlock && stateAbove.getBlock() instanceof ClearWarpPipeBlock)) {
                    if (hasGlowingText) {
                        this.font.drawInBatch8xOutline(pipeName, 0, 0, textColor, getDarkColor(pipeText),
                                stack.last().pose(), buffer, packedLightL);
                    } else {
                        this.font.drawInBatch(pipeName, 0, 0, textColor, false,
                                stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                    }
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }

    void renderPipeTextBelow(WarpPipeBlockEntity pipeBlockEntity, BlockPos pos, PipeText pipeText, PoseStack stack, MultiBufferSource buffer, int packedLight, int lineHeight, int maxWidth) {
        BlockState state = pipeBlockEntity.getBlockState();
        Level world = pipeBlockEntity.getLevel();

        FormattedCharSequence[] pipeNameArray = pipeText.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (text) -> {
            List<FormattedCharSequence> list = this.font.split(text, maxWidth);
            return list.isEmpty() ? FormattedCharSequence.EMPTY : list.get(0);
        });

        stack.pushPose();

        int textColor;
        int packedLightL;
        boolean hasGlowingText;
        if (pipeText.hasGlowingText()) {
            textColor = pipeText.getColor().getTextColor();
            hasGlowingText = isOutlineVisible(pos, textColor);
            packedLightL = 15728880;
        } else {
            textColor = getDarkColor(pipeText);
            packedLightL = 0xFFFFFF;
            hasGlowingText = false;
        }

        if (world != null) {
            stack.pushPose();

            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH){
                stack.translate(0.5, -0.001, 0.175);
                stack.mulPose(Axis.XP.rotationDegrees(90F));
                stack.mulPose(Axis.ZP.rotationDegrees(180F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH){
                stack.translate(0.5, -0.001, 0.825);
                stack.mulPose(Axis.XP.rotationDegrees(90F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST){
                stack.translate(0.825, -0.001, 0.5);
                stack.mulPose(Axis.XP.rotationDegrees(90F));
                stack.mulPose(Axis.ZP.rotationDegrees(270F));
            } else if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST){
                stack.translate(0.175, -0.001, 0.5);
                stack.mulPose(Axis.XP.rotationDegrees(90F));
                stack.mulPose(Axis.ZP.rotationDegrees(90F));
            }

            stack.scale(1.0F, -1.0F, -1.0F);
            stack.scale(TEXT_RENDER_SCALE, TEXT_RENDER_SCALE, TEXT_RENDER_SCALE);
            stack.translate(0.0, -(this.font.lineHeight - 1.0) / 2.0, 0);

            for (int j = 0; j < 1; j++) {
                BlockState stateBelow = world.getBlockState(pos.below());
                FormattedCharSequence pipeName = pipeNameArray[j];
                if (state.getBlock() instanceof ClearWarpPipeBlock)
                    stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 7, 0.0);
                else stack.translate((-this.font.width(pipeName) / 2.0) + 0.5, 2, 0.0);

                if (!(stateBelow.isSolid() && stateBelow.isSolidRender(world, pos.below()))
                        && !(state.getBlock() instanceof ClearWarpPipeBlock && stateBelow.getBlock() instanceof ClearWarpPipeBlock)) {
                    if (hasGlowingText) {
                        this.font.drawInBatch8xOutline(pipeName, 0, 0, textColor, getDarkColor(pipeText),
                                stack.last().pose(), buffer, packedLightL);
                    } else {
                        this.font.drawInBatch(pipeName, 0, 0, textColor, false,
                                stack.last().pose(), buffer, Font.DisplayMode.POLYGON_OFFSET, 0, packedLightL);
                    }
                }
            }
            stack.popPose();
        }
        stack.popPose();
    }

    static boolean isOutlineVisible(BlockPos pos, int textColor) {
        if (textColor == DyeColor.BLACK.getTextColor()) {
            return true;
        } else {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer localplayer = minecraft.player;
            if (localplayer != null && minecraft.options.getCameraType().isFirstPerson() && localplayer.isScoping()) {
                return true;
            } else {
                Entity entity = minecraft.getCameraEntity();
                return entity != null && entity.distanceToSqr(Vec3.atCenterOf(pos)) < (double) OUTLINE_RENDER_DISTANCE;
            }
        }
    }

    static int getDarkColor(PipeText pipeText) {
        int textColor = pipeText.getColor().getTextColor();
        if (textColor == DyeColor.BLACK.getTextColor() && pipeText.hasGlowingText()) {
            return -988212;
        } else {
            double d0 = 0.4D;
            int j = (int)((double) FastColor.ARGB32.red(textColor) * 0.4D);
            int k = (int)((double) FastColor.ARGB32.green(textColor) * 0.4D);
            int l = (int)((double) FastColor.ARGB32.blue(textColor) * 0.4D);
            return FastColor.ARGB32.color(0, j, k, l);
        }
    }
}
