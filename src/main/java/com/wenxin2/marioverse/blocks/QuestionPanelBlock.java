package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class QuestionPanelBlock extends FaceAttachedHorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape FLOOR = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    protected static final VoxelShape CEILING = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape EAST = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    protected static final VoxelShape WEST = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public static final MapCodec<QuestionPanelBlock> CODEC = simpleCodec(QuestionPanelBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    @NotNull
    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public QuestionPanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACE, AttachFace.FLOOR).setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACE, FACING, POWERED, WATERLOGGED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        Direction facing = state.getValue(FACING);
        AttachFace face = state.getValue(FACE);

        switch (face) {
            case FLOOR:
                return FLOOR;
            case CEILING:
                return CEILING;
            case WALL:
                switch (facing) {
                    case NORTH:
                        return NORTH;
                    case SOUTH:
                        return SOUTH;
                    case EAST:
                        return EAST;
                    case WEST:
                        return WEST;
                }
                break;
        }
        return FLOOR;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        BlockPos posBelow = pos.below();
        return canAttach(worldReader, pos, getConnectedDirection(state).getOpposite()) || canSupportCenter(worldReader, posBelow, Direction.UP);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        if (getConnectedDirection(state).getOpposite() == direction && !state.canSurvive(worldAccessor, pos))
            return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        boolean isWater = fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8;

        for (Direction direction : placeContext.getNearestLookingDirections()) {
            BlockState state;
            if (direction.getAxis() == Direction.Axis.Y) {
                state = this.defaultBlockState()
                        .setValue(FACE, direction == Direction.UP ? AttachFace.CEILING : AttachFace.FLOOR)
                        .setValue(FACING, placeContext.getHorizontalDirection())
                        .setValue(WATERLOGGED, isWater);
            } else state = this.defaultBlockState().setValue(FACE, AttachFace.WALL)
                    .setValue(FACING, direction.getOpposite())
                    .setValue(WATERLOGGED, isWater);

            if (state.canSurvive(placeContext.getLevel(), placeContext.getClickedPos()))
                return state.setValue(WATERLOGGED, isWater);
        }

        return this.defaultBlockState().setValue(WATERLOGGED, isWater);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide) {
            int power = this.getSignalForState(state);
            if (power == 0)
                this.checkPressed(entity, world, pos, state, power);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        int power = this.getSignalForState(state);
        if (power > 0) {
            this.checkPressed(null, serverWorld, pos, state, power);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState neighborState, boolean isMoving) {
        if (!isMoving && !state.is(neighborState.getBlock())) {
            if (this.getSignalForState(state) > 0)
                this.updateNeighbours(world, pos);

            super.onRemove(state, world, pos, neighborState, isMoving);
        }
    }

    @Override
    public boolean isPossibleToRespawnInThis(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        return this.getSignalForState(state);
    }

    @Override
    protected int getDirectSignal(BlockState state, BlockGetter blockGetter, BlockPos pos, Direction direction) {
        return direction == Direction.UP ? this.getSignalForState(state) : 0;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    protected int getPressedTime() {
        return 20;
    }

    protected int getSignalForState(BlockState state) {
        return state.getValue(POWERED) ? 15 : 0;
    }

    protected BlockState setSignalForState(BlockState state, int signalStrength) {
        return state.setValue(POWERED, signalStrength > 0);
    }

    protected int getSignalStrength(Level world, BlockPos pos) {
        return 15;
    }

    protected void updateNeighbours(Level world, BlockPos pos) {
        world.updateNeighborsAt(pos, this);
        world.updateNeighborsAt(pos.below(), this);
    }

    private void checkPressed(@Nullable Entity entity, Level world, BlockPos pos, BlockState state, int power) {
        int signalStrength = this.getSignalStrength(world, pos);
        boolean isPowered = power > 0;
        boolean isSignaled = signalStrength > 0;
        if (power != signalStrength) {
            BlockState blockstate = this.setSignalForState(state, signalStrength);
            world.setBlock(pos, blockstate, 2);
            this.updateNeighbours(world, pos);
            world.setBlocksDirty(pos, state, blockstate);
        }

        if (!isSignaled && isPowered) {
//            world.playSound(null, pos, this.type.pressurePlateClickOff(), SoundSource.BLOCKS);
            world.gameEvent(entity, GameEvent.BLOCK_DEACTIVATE, pos);
        } else if (isSignaled && !isPowered) {
//            world.playSound(null, pos, this.type.pressurePlateClickOn(), SoundSource.BLOCKS);
            world.gameEvent(entity, GameEvent.BLOCK_ACTIVATE, pos);
        }

        if (isSignaled)
            world.scheduleTick(new BlockPos(pos), this, this.getPressedTime());
    }
}
