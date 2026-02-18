package com.wenxin2.marioverse.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class PottedTrampolineCapBlock extends FlowerPotBlock implements ToggleableBlock {
    public PottedTrampolineCapBlock(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties properties) {
        super(emptyPot, block, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(OnBlock.ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OnBlock.ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.getStateForPlacementSavedData(this.defaultBlockState(), placeContext);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        this.onPlaceSavedData(level, pos);
        super.onPlace(state, level, pos, oldState, moved);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        this.onRemoveSavedData(level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }
}
