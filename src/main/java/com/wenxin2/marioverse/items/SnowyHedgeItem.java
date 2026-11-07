package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.HedgeBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class SnowyHedgeItem extends BlockItem {
    public SnowyHedgeItem(Properties properties, Block block) {
        super(block, properties);
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        BlockItemStateProperties properties = stack.get(DataComponents.BLOCK_STATE);
        if (properties != null && Boolean.FALSE.equals(properties.get(HedgeBlock.SNOWY)))
            return Component.translatable("block.marioverse.snowy_hedge.snowless");
        return super.getName(stack);
    }
}
