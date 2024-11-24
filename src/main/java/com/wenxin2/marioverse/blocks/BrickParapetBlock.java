package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BrickParapetBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape BRICK_PARAPET_SHAPE = Shapes.or(
            Block.box(2, 0, 2, 14, 8, 14),
            Block.box(0, 8, 0, 16, 12, 16),
            Block.box(5, 12, 5, 11, 16, 11),
            Block.box(13, 12, 0, 16, 16, 6),
            Block.box(13, 12, 10, 16, 16, 16),
            Block.box(0, 12, 10, 3, 16, 16),
            Block.box(0, 12, 0, 3, 16, 6),
            Block.box(3, 12, 13, 6, 16, 16),
            Block.box(3, 12, 0, 6, 16, 3),
            Block.box(10, 12, 0, 13, 16, 3),
            Block.box(10, 12, 13, 13, 16, 16)).optimize();

    public BrickParapetBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return BRICK_PARAPET_SHAPE;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockState stateAbove = world.getBlockState(pos.above());

        if (stateAbove.getBlock() instanceof GoalPoleBlock) {
            if (stateAbove.getValue(GoalPoleBlock.LOWERED))
                return this.calculateFlagPoleLoweredHeight(world, pos.above());
            else return super.getAnalogOutputSignal(state, world, pos);
        } else return super.getAnalogOutputSignal(state, world, pos);
    }

    private int calculateFlagPoleLoweredHeight(Level world, BlockPos pos) {
        int height = 0;
        BlockPos checkPos = pos;

        // Check upward to count the flag pole's height
        while (world.getBlockState(checkPos).getBlock() instanceof GoalPoleBlock
                && world.getBlockState(checkPos).getValue(GoalPoleBlock.LOWERED) && height <= 15) {
            height++;
            checkPos = checkPos.above();
        }
        return height;
    }
}
