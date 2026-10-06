package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class SuperMushroomStemBlock extends DirectionalBlock {
    public static final MapCodec<SuperMushroomStemBlock> CODEC = simpleCodec(SuperMushroomStemBlock::new);
    public static final BooleanProperty END = BlockStatePropertyRegistry.END;

    @NotNull
    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public SuperMushroomStemBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(END, true)
                .setValue(FACING, Direction.UP));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockState neighborState = context.getLevel().getBlockState(context.getClickedPos().relative(facing));
        return this.defaultBlockState().setValue(FACING, facing)
                .setValue(END, !context.isSecondaryUseActive() && !(neighborState.getBlock() instanceof SuperMushroomStemBlock));
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        if (direction == state.getValue(FACING)) {
            if (neighborState.getBlock() instanceof SuperMushroomStemBlock)
                return state.setValue(END, false);
            if (neighborState.isAir())
                return state.setValue(END, true);
        }
        return super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(END, FACING);
    }
}
