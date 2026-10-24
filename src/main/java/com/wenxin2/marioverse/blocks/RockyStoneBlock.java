package com.wenxin2.marioverse.blocks;

import java.util.function.Supplier;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

public class RockyStoneBlock extends RotatedPillarBlock {
    private final Supplier<? extends Block> floorBlock;

    public RockyStoneBlock(Supplier<? extends Block> floorBlock, Properties properties) {
        super(properties);
        this.floorBlock = floorBlock;
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility == ItemAbilities.PICKAXE_DIG)
            return StoneFloorBlock.getFloorState(this.floorBlock.get(), state.getValue(AXIS), context.getClickedFace());
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
