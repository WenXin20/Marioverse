package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.init.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

public class BrickPedestalBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    protected static final VoxelShape BRICK_PEDESTAL_COLUMN = Shapes.or(
            Block.box(2, 0, 2, 14, 16, 14)).optimize();

    protected static final VoxelShape BRICK_PEDESTAL_TOP = Shapes.or(
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

    protected static final VoxelShape FUNGAL_BRICK_PEDESTAL_TOP = Shapes.or(
            Block.box(2, 0, 2, 14, 6, 14),
            Block.box(0, 6, 0, 16, 12, 16),
            Block.box(5, 12, 5, 11, 16, 11),
            Block.box(13, 12, 0, 16, 16, 6),
            Block.box(13, 12, 10, 16, 16, 16),
            Block.box(0, 12, 10, 3, 16, 16),
            Block.box(0, 12, 0, 3, 16, 6),
            Block.box(3, 12, 13, 6, 16, 16),
            Block.box(3, 12, 0, 6, 16, 3),
            Block.box(10, 12, 0, 13, 16, 3),
            Block.box(10, 12, 13, 13, 16, 16)).optimize();

    public BrickPedestalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, Boolean.TRUE).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(TOP, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(TOP)) {
            if (state.getBlock() == BlockRegistry.FUNGAL_BRICK_PEDESTAL.get())
                return FUNGAL_BRICK_PEDESTAL_TOP;
            else return BRICK_PEDESTAL_TOP;
        } else return BRICK_PEDESTAL_COLUMN;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        Block blockAbove = worldAccessor.getBlockState(pos.above()).getBlock();

        if (!worldAccessor.isClientSide()) {
            if (state.getValue(WATERLOGGED))
                worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

            if (!(blockAbove instanceof BrickPedestalBlock))
                return state.setValue(TOP, Boolean.TRUE);
            else return state.setValue(TOP, Boolean.FALSE);
        } else return Blocks.AIR.defaultBlockState();
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
