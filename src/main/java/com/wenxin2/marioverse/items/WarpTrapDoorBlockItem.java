package com.wenxin2.marioverse.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class WarpTrapDoorBlockItem extends BlockItem {
    private final Block source;

    public WarpTrapDoorBlockItem(Block block, Block source, Properties properties) {
        super(block, properties);
        this.source = source;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("block.marioverse.warp_trapdoor", Component.translatable(source.getDescriptionId()));
    }
}

