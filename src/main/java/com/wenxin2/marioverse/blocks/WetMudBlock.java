package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.MudBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class WetMudBlock extends MudBlock {
    public WetMudBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (context.getClickedFace() != Direction.DOWN
                && context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
            if (itemAbility.equals(ItemAbilities.HOE_TILL))
                return BlockRegistry.WET_MUD_FARMLAND.get().defaultBlockState();
            else if (itemAbility.equals(ItemAbilities.SHOVEL_FLATTEN))
                return BlockRegistry.DEEP_WET_MUD.get().defaultBlockState();
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
