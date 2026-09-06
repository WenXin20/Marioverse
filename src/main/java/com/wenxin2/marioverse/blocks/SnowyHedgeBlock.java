package com.wenxin2.marioverse.blocks;

import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public class SnowyHedgeBlock extends HedgeBlock {
    public SnowyHedgeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(TOP, true).setValue(SNOWY, true).setValue(WATERLOGGED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(SNOWY, true);
    }

    @Override
    protected boolean alwaysSnowy() {
        return true;
    }
}
