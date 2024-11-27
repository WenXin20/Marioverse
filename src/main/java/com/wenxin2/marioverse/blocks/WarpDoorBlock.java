package com.wenxin2.marioverse.blocks;

import java.util.Collections;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

public class WarpDoorBlock extends DoorBlock {
    private final Block originalBlock;

    public WarpDoorBlock(BlockSetType setType, Properties properties, Block originalBlock) {
        super(setType, properties);
        this.originalBlock = originalBlock;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return this.getOriginalBlock().defaultBlockState().getRenderShape();
    }

    @NotNull
    @Override
    public MutableComponent getName() {
        String originalName = originalBlock.getName().getString();
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

    @NotNull
    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER)
            return originalBlock.defaultBlockState().getDrops(params);
        else return Collections.singletonList(ItemStack.EMPTY);
    }

    public Block getOriginalBlock() {
        return originalBlock;
    }
}
