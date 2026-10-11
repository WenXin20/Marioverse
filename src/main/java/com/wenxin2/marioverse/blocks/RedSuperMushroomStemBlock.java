package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class RedSuperMushroomStemBlock extends SuperMushroomStemBlock implements ToggleableBlock {
    public static final BooleanProperty ACTIVE = BlockStatePropertyRegistry.ACTIVE;

    public RedSuperMushroomStemBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getStateForPlacementSavedData(super.getStateForPlacement(context), context);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.onPlaceSavedData(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        this.onRemoveSavedData(level, pos);
    }
}
