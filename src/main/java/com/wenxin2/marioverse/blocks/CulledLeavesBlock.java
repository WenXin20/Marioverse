package com.wenxin2.marioverse.blocks;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CulledLeavesBlock extends LeavesBlock {
    public CulledLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean skipRendering(@NotNull BlockState state, @NotNull BlockState neighborState, @NotNull Direction direction) {
        return neighborState.is(this) || super.skipRendering(state, neighborState, direction);
    }
}
