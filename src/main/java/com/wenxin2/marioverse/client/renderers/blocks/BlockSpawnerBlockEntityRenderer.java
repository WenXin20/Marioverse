package com.wenxin2.marioverse.client.renderers.blocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class BlockSpawnerBlockEntityRenderer implements BlockEntityRenderer<BlockSpawnerBlockEntity> {
    private static final int CHANGE_INTERVAL = 40;
    private static List<ItemStack> BLOCK_ITEMS;

    public BlockSpawnerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super();
    }

    @Override
    public void render(BlockSpawnerBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Minecraft mc = Minecraft.getInstance();
        BlockState disguiseState = blockEntity.getDisguiseState();
        BlockState state = blockEntity.getBlockState();
        boolean invisible = state.getValue(BlockStatePropertyRegistry.INVISIBLE);
        boolean disguised = state.getValue(BlockStatePropertyRegistry.DISGUISED);

        if (disguiseState != null && !disguiseState.isAir()
                && !disguiseState.is(TagRegistry.CANNOT_USE_AS_DISGUISE)) {
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

        if (!invisible && !disguised)
            this.renderSpinningItem(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
        if (invisible && !disguised && mc.player != null && mc.player.isCreative())
            this.renderSpinningItem(blockEntity, partialTick, poseStack, buffer, packedLight, packedOverlay);
    }

    @NotNull
    @Override
    public AABB getRenderBoundingBox(BlockSpawnerBlockEntity blockEntity) {
        BlockEntity disguisedBE = blockEntity.getDisguiseBlockEntity();
        if (disguisedBE != null && blockEntity.getBlockState().getValue(BlockStatePropertyRegistry.DISGUISED)) {
            BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(disguisedBE);

            if (renderer != null)
                return renderer.getRenderBoundingBox(disguisedBE);
        }
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }


    private void renderSpinningItem(BlockSpawnerBlockEntity blockEntity, float partialTick,
                                    PoseStack poseStack, MultiBufferSource buffer,
                                    int light, int overlay) {
        if (blockEntity.getLevel() == null || BlockSpawnerBlockEntityRenderer.getBlockItems().isEmpty())
            return;
        int lightColor = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos());

        ItemStack stack = getRandomStack(blockEntity, blockEntity.getLevel());
        if (!blockEntity.isEmpty())
            stack = blockEntity.getItem(1);

        float angle = ((blockEntity.getLevel().getGameTime() % 72000L) + partialTick) * 2.5F;
        if (!blockEntity.isEmpty())
            angle = ((blockEntity.getLevel().getGameTime() % 72000L) + partialTick) * 1.25F;

        poseStack.pushPose();
            poseStack.translate(0.5, 0.3, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(angle));
            poseStack.scale(1.25F, 1.25F, 1.25F);
            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.GROUND, lightColor, overlay,
                    poseStack, buffer, blockEntity.getLevel(), 0);
        poseStack.popPose();
    }

    private ItemStack getRandomStack(BlockSpawnerBlockEntity blockEntity, Level level) {
        List<ItemStack> items = getBlockItems();
        if (items.isEmpty())
            return ItemStack.EMPTY;

        long timeWindow = level.getGameTime() / CHANGE_INTERVAL;
        long seed = blockEntity.getBlockPos().asLong() * 31L + timeWindow * 1315423911L;
        Random random = new Random(seed);
        int index = random.nextInt(items.size());

        return items.get(index);
    }

    private static List<ItemStack> getBlockItems() {
        if (BLOCK_ITEMS == null || BLOCK_ITEMS.isEmpty()) {
            BLOCK_ITEMS = collectBlockItems();
            Collections.shuffle(BLOCK_ITEMS, new Random(123456789L));
        }
        return BLOCK_ITEMS;
    }

    private static List<ItemStack> collectBlockItems() {
        List<ItemStack> list = new ArrayList<>();
        Random random = new Random();

        for (Item item : BuiltInRegistries.ITEM) {
            if (item instanceof BlockItem || item instanceof BucketItem) {
                ItemStack stack = new ItemStack(item);
                if (!stack.isEmpty()) {
                    int index = list.isEmpty() ? 0 : random.nextInt(list.size() + 1);
                    list.add(index, stack);
                }
            }
        }
        return list;
    }
}