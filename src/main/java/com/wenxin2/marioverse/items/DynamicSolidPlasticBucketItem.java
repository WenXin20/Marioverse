package com.wenxin2.marioverse.items;

import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class DynamicSolidPlasticBucketItem extends SolidPlasticBucketItem {
    public DynamicSolidPlasticBucketItem(int tooltipLineAmt, Block block, SoundEvent soundEvent, Item.Properties properties) {
        super(tooltipLineAmt, block, soundEvent, properties);
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
    }

    @NotNull
    @Override
    public String getDescriptionId() {
        return "item.marioverse.plastic_solid_bucket";
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(), this.getBlock().getName());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        Component blockName = this.getBlock().getName().copy().withStyle(ChatFormatting.GRAY);

        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt, blockName));
            list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}