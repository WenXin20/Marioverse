package com.wenxin2.marioverse.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class WarpDoorItem extends DoubleHighBlockItem {
    private final Item originalItem;

    public WarpDoorItem(Block block, Properties properties, Item originalItem) {
        super(block, properties);
        this.originalItem = originalItem;
    }

    @NotNull
    @Override
    public MutableComponent getName(ItemStack stack) {
        String originalName = originalItem.getDescription().getString();
        MutableComponent warpDoorName = Component.translatable("block.marioverse.warp_door");

        if (originalName.endsWith(" Door")) {
            int splitIndex = originalName.lastIndexOf(" Door");
            String baseName = originalName.substring(0, splitIndex);

            return Component.literal(baseName).append(warpDoorName);
        } else {
            // Fallback if it doesn't end with " Door"
            return Component.literal(originalName).append(warpDoorName);
        }
    }

    public Item getOriginalItem() {
        return originalItem;
    }
}
