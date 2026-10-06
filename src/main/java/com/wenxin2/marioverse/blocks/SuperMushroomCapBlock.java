package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class SuperMushroomCapBlock extends HugeMushroomBlock {
    public static final BooleanProperty BOTTOM = BlockStatePropertyRegistry.BOTTOM;

    public SuperMushroomCapBlock( Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BOTTOM, true));
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateBelow = context.getLevel().getBlockState(context.getClickedPos().below());
        return super.getStateForPlacement(context)
                .setValue(BOTTOM, !(stateBelow.getBlock() instanceof SuperMushroomCapBlock));
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        BlockState newState = super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
        if (direction == Direction.DOWN)
            return newState.setValue(BOTTOM, !(neighborState.getBlock() instanceof SuperMushroomCapBlock));
        return newState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BOTTOM, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}
