package com.wenxin2.marioverse.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;

public class WarpDoorBlockItem extends BlockItem {
    private final Block source;

    public WarpDoorBlockItem(Block block, Block source, Properties properties) {
        super(block, properties);
        this.source = source;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("block.marioverse.warp_door", Component.translatable(source.getDescriptionId()));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        ResourceLocation blockID = BuiltInRegistries.BLOCK.getKey(this.source);
        Component modName = ModList.get().getModContainerById(blockID.getNamespace())
                .map(c -> Component.literal(c.getModInfo().getDisplayName()))
                .orElse(Component.literal(blockID.getNamespace()));

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable("block.marioverse.warp_door.tooltip.ability"));
            list.add(Component.translatable("block.marioverse.warp_door.tooltip.description"));
            list.add(Component.translatable("block.marioverse.warp_door.tooltip.guide"));
            list.add(Component.translatable("block.marioverse.warp_door.tooltip.mod"));
            list.add(Component.translatable("block.marioverse.warp_door.tooltip.source_mod", modName).withStyle(ChatFormatting.BLUE));

            list.add(Component.literal(""));
        } else list.add(Component.translatable("block.marioverse.warp_door.tooltip"));
    }
}

