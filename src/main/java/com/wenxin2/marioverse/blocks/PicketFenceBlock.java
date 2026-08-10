package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PicketFenceBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<PicketFenceBlock> CODEC = simpleCodec(PicketFenceBlock::new);
    public static final EnumProperty<StairsShape> SHAPE = BlockStateProperties.STAIRS_SHAPE;
    public static final BooleanProperty TALL = BlockStatePropertyRegistry.TALL;

    private static final VoxelShape SHAPE_STRAIGHT = Shapes
            .or(Block.box(9, 0, 7, 15, 16, 9),
                    Block.box(1, 0, 7, 7, 15, 9),
                    Block.box(0, 6, 9, 16, 10, 10)).optimize();
    private static final VoxelShape SHAPE_OUTER = Shapes
            .or(Block.box(10, 0, 7, 15, 16, 9),
                    Block.box(7, 0, 1, 9, 15, 6),
                    Block.box(6, 6, 9, 16, 10, 10),
                    Block.box(6, 6, 0, 7, 10, 9)).optimize();
    private static final VoxelShape SHAPE_INNER = Shapes
            .or(Block.box(1, 0, 7, 6, 16, 9),
                    Block.box(7, 0, 10, 9, 15, 15),
                    Block.box(0, 6, 9, 7, 10, 10),
                    Block.box(6, 6, 10, 7, 10, 16)).optimize();
    private static final VoxelShape SHAPE_STRAIGHT_TALL = Shapes
            .or(Block.box(9, 0, 7, 15, 16, 9),
                    Block.box(1, 0, 7, 7, 16, 9),
                    Block.box(0, 6, 9, 16, 10, 10)).optimize();
    private static final VoxelShape SHAPE_OUTER_TALL = Shapes
            .or(Block.box(10, 0, 7, 15, 16, 9),
                    Block.box(7, 0, 1, 9, 16, 6),
                    Block.box(6, 6, 9, 16, 10, 10),
                    Block.box(6, 6, 0, 7, 10, 9)).optimize();
    private static final VoxelShape SHAPE_INNER_TALL = Shapes
            .or(Block.box(1, 0, 7, 6, 16, 9),
                    Block.box(7, 0, 10, 9, 16, 15),
                    Block.box(0, 6, 9, 7, 10, 10),
                    Block.box(6, 6, 10, 7, 10, 16)).optimize();

    public PicketFenceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(SHAPE, StairsShape.STRAIGHT)
                .setValue(TALL, false));
    }

    @Override
    protected MapCodec<PicketFenceBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SHAPE, TALL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();

        BlockState state = this.defaultBlockState().setValue(FACING, facing);
        state = state.setValue(SHAPE, this.computeShape(state, level, pos));
        state = state.setValue(TALL, level.getBlockState(pos.above()).is(this));
        return state;
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.UP)
            return state.setValue(TALL, neighborState.is(this));
        if (direction.getAxis().isHorizontal())
            return state.setValue(SHAPE, this.computeShape(state, level, pos));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        StairsShape shape = state.getValue(SHAPE);
        boolean tall = state.getValue(TALL);

        VoxelShape base = switch (shape) {
            case STRAIGHT -> tall ? SHAPE_STRAIGHT_TALL : SHAPE_STRAIGHT;
            case OUTER_LEFT, OUTER_RIGHT -> tall ? SHAPE_OUTER_TALL : SHAPE_OUTER;
            case INNER_LEFT, INNER_RIGHT -> tall ? SHAPE_INNER_TALL : SHAPE_INNER;
        };
        Direction target = (shape == StairsShape.OUTER_LEFT || shape == StairsShape.INNER_LEFT)
                ? state.getValue(FACING).getCounterClockWise()
                : state.getValue(FACING);

        return this.rotateShape(Direction.NORTH, target, base);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    private VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int rotations = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < rotations; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    private StairsShape computeShape(BlockState state, LevelReader level, BlockPos pos) {
        Direction facing = state.getValue(FACING);

        BlockState aheadState = level.getBlockState(pos.relative(facing));
        if (aheadState.is(this)) {
            Direction aheadFacing = aheadState.getValue(FACING);
            if (aheadFacing.getAxis() != facing.getAxis()
                    && this.canTakeShape(state, level, pos, aheadFacing.getOpposite())) {
                return aheadFacing == facing.getCounterClockWise()
                        ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT;
            }
        }

        BlockState behindState = level.getBlockState(pos.relative(facing.getOpposite()));
        if (behindState.is(this)) {
            Direction behindFacing = behindState.getValue(FACING);
            if (behindFacing.getAxis() != facing.getAxis()
                    && this.canTakeShape(state, level, pos, behindFacing)) {
                return behindFacing == facing.getCounterClockWise()
                        ? StairsShape.INNER_LEFT : StairsShape.INNER_RIGHT;
            }
        }

        return StairsShape.STRAIGHT;
    }

    private boolean canTakeShape(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
        BlockState neighbor = level.getBlockState(pos.relative(direction));
        return !neighbor.is(this) || neighbor.getValue(FACING) != state.getValue(FACING);
    }
}