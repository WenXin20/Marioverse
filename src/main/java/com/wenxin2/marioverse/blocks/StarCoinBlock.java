package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import com.wenxin2.marioverse.blocks.states.QuadrantBlockStates;
import com.wenxin2.marioverse.init.ParticleRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StarCoinBlock extends CoinBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<QuadrantBlockStates> QUADRANT = EnumProperty.create("quadrant", QuadrantBlockStates.class);

    protected static final VoxelShape LOWER_NORTH_WEST = Block.box(5.0, 3.5, 5.0, 27.0, 25.5, 27.0).optimize();
    protected static final VoxelShape LOWER_NORTH_EAST = Block.box(5.0, 3.5, 5.0, 27.0, 25.5, 27.0).optimize();

    public StarCoinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HALF, QUADRANT, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return LOWER_NORTH_WEST;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StarCoinBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final Level world = context.getLevel();
        final FluidState fluidState = world.getFluidState(pos);
        final boolean waterlogged = fluidState.getType() == Fluids.WATER;

        Direction facing = context.getHorizontalDirection();
        DoubleBlockHalf half = (pos.getY() % 2 == 0) ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;

        if (!canPlaceBlock(world, pos.relative(facing.getClockWise()))
                || !canPlaceBlock(world, pos.relative(facing, 1))
                || !canPlaceBlock(world, pos.relative(facing.getClockWise()).relative(facing))
                || !canPlaceBlock(world, pos.above())
                || !canPlaceBlock(world, pos.relative(facing.getClockWise()).above())
                || !canPlaceBlock(world, pos.relative(facing, 1).above())
                || !canPlaceBlock(world, pos.relative(facing.getClockWise()).relative(facing).above())) {
            return null;
        }

        return this.defaultBlockState().setValue(HALF, half).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST).setValue(WATERLOGGED, waterlogged);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);

        if ((direction == Direction.UP && half == DoubleBlockHalf.LOWER) ||
                (direction == Direction.DOWN && half == DoubleBlockHalf.UPPER)) {
            if (!neighborState.is(this))
                return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
//        if (entity != null) {
//            Direction facing = entity.getDirection();
//
//            if (canPlaceBlock(world, pos.relative(facing.getClockWise()))
//                    && canPlaceBlock(world, pos.relative(facing, 1))
//                    && canPlaceBlock(world, pos.relative(facing.getClockWise()).relative(facing))
//                    && canPlaceBlock(world, pos.above())
//                    && canPlaceBlock(world, pos.relative(facing.getClockWise()).above())
//                    && canPlaceBlock(world, pos.relative(facing, 1).above())
//                    && canPlaceBlock(world, pos.relative(facing.getClockWise()).relative(facing).above())) {
//                world.setBlock(pos, state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
//                        .setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER), 3);
//
//                if (canPlaceBlock(world, pos.relative(facing.getClockWise())))
//                    world.setBlock(pos.relative(facing.getClockWise()), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
//                            .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getClockWise())).getType() == Fluids.WATER), 3);
//
//                if (canPlaceBlock(world, pos.relative(facing, 1)))
//                    world.setBlock(pos.relative(facing, 1), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
//                            .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing, 1)).getType() == Fluids.WATER), 3);
//
//                if (canPlaceBlock(world, pos.relative(facing.getClockWise()).relative(facing)))
//                    world.setBlock(pos.relative(facing.getClockWise()).relative(facing), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
//                            .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getClockWise()).relative(facing)).getType() == Fluids.WATER), 3);
//
//                if (canPlaceBlock(world, pos.above()))
//                    world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
//                            .setValue(WATERLOGGED, world.getFluidState(pos.above()).getType() == Fluids.WATER), 3);
//
//                if (canPlaceBlock(world, pos.relative(facing.getClockWise()).above()))
//                    world.setBlock(pos.relative(facing.getClockWise()).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
//                            .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getClockWise()).above()).getType() == Fluids.WATER), 3);
//
//                if (canPlaceBlock(world, pos.relative(facing, 1).above()))
//                    world.setBlock(pos.relative(facing, 1).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
//                            .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing, 1).above()).getType() == Fluids.WATER), 3);
//
//                if (canPlaceBlock(world, pos.relative(facing.getClockWise()).relative(facing).above()))
//                    world.setBlock(pos.relative(facing.getClockWise()).relative(facing).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
//                            .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getClockWise()).relative(facing).above()).getType() == Fluids.WATER), 3);
//            }
//        }
        if (entity != null) {
            // Always determine NORTH_WEST position based on world coordinates
            BlockPos northWestPos = pos.relative(Direction.NORTH).relative(Direction.WEST);

            if (canPlaceBlock(world, northWestPos.relative(Direction.EAST))
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH))
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST))
                    && canPlaceBlock(world, northWestPos.above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.EAST).above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above())) {

                // Lower half
                world.setBlock(northWestPos, state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.EAST), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.EAST)).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH)).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST)).getType() == Fluids.WATER), 3);

                // Upper half
                world.setBlock(northWestPos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.above()).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.EAST).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.EAST).above()).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH).above()).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above()).getType() == Fluids.WATER), 3);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
        if (nearestPlayer != null) {
            world.addParticle(ParticleRegistry.INVISIBLE_FUNGAL_QUESTION.get(),
                    x + 0.5, y + 0.5, z + 0.5, 0.0, 0.0, 0.0);
        }
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos neighborPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState neighborState = world.getBlockState(neighborPos);
        Direction facing = player.getDirection();
        BlockPos[] offsets = getPlacementOffsets(facing);

        if (!world.isClientSide) {
            if (player.isCreative())
                preventDropFromParts(world, pos, state, player);
            else dropResources(state, world, pos, null, player, player.getMainHandItem());

            for (BlockPos offset : offsets) {
                BlockPos partPos = pos.offset(offset);
                BlockState partState = world.getBlockState(partPos);

                if (partState.is(this)) {
                    boolean isWaterlogged = partState.getValue(WATERLOGGED);
                    world.setBlock(partPos, isWaterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                    world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, partPos, Block.getId(partState));
                }
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    private BlockPos getHalfPos(final BlockPos pos, final DoubleBlockHalf state) {
        if (state == DoubleBlockHalf.UPPER)
            return pos.above();
        else return pos;
    }

    private BlockPos getQuadrantPos(final BlockPos pos, final QuadrantBlockStates state) {
        if (state == QuadrantBlockStates.NORTH_EAST)
            return pos.east();
        else if (state == QuadrantBlockStates.SOUTH_EAST)
            return pos.south().east();
        else if (state == QuadrantBlockStates.SOUTH_WEST)
            return pos.south().west();
        else return pos;
    }

    private BlockPos getPartPos(final BlockPos pos, final DoubleBlockHalf half, final QuadrantBlockStates quadrant) {
        if (half == DoubleBlockHalf.UPPER) {
            if (quadrant == QuadrantBlockStates.NORTH_EAST)
                return pos.above().east();
            else if (quadrant == QuadrantBlockStates.SOUTH_EAST)
                return pos.above().south().east();
            else if (quadrant == QuadrantBlockStates.SOUTH_WEST)
                return pos.above().south().west();
            else return pos.above();
        } else {
            if (quadrant == QuadrantBlockStates.NORTH_EAST)
                return pos.east();
            else if (quadrant == QuadrantBlockStates.SOUTH_EAST)
                return pos.south().east();
            else if (quadrant == QuadrantBlockStates.SOUTH_WEST)
                return pos.south().west();
            else return pos;
        }
    }

    private BlockPos[] getPlacementOffsets(Direction facing) {
        return new BlockPos[]{
                BlockPos.ZERO.relative(facing.getCounterClockWise()), // Lower - left
                BlockPos.ZERO.relative(facing.getOpposite()),         // Lower - back
                BlockPos.ZERO.relative(facing.getCounterClockWise()).relative(facing.getOpposite()), // Lower - back-left
                BlockPos.ZERO.above(),                                // Upper - main
                BlockPos.ZERO.above().relative(facing.getCounterClockWise()), // Upper - left
                BlockPos.ZERO.above().relative(facing.getOpposite()), // Upper - back
                BlockPos.ZERO.above().relative(facing.getCounterClockWise()).relative(facing.getOpposite()) // Upper - back-left
        };
    }

    private QuadrantBlockStates determineQuadrant(Direction direction) {
        return switch (direction) {
            default -> QuadrantBlockStates.NORTH_WEST;
            case EAST -> QuadrantBlockStates.NORTH_EAST;
            case SOUTH -> QuadrantBlockStates.SOUTH_EAST;
            case WEST -> QuadrantBlockStates.SOUTH_WEST;
        };
    }

    protected static void preventDropFromParts(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.LOWER) {
            BlockPos posAbove = pos.above();
            BlockState stateAbove = world.getBlockState(posAbove);

            if (stateAbove.is(state.getBlock()) && stateAbove.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockState stateAboveWaterOrAir = stateAbove.getFluidState().is(Fluids.WATER)
                        ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                world.setBlock(posAbove, stateAboveWaterOrAir, 35);
                world.levelEvent(player, 2001, posAbove, Block.getId(stateAbove));
            }
        }
    }
}
