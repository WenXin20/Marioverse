package com.wenxin2.marioverse.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class CheckpointFlagBlockItem extends BlockItem {
    public CheckpointFlagBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @NotNull
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (pos.above(2).getY() >= world.getMaxBuildHeight()) {
            if (player != null && world.isClientSide)
                player.displayClientMessage(Component.translatable("build.tooHigh", world.getMaxBuildHeight() - 1)
                                .withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        return super.place(context);
    }
}
