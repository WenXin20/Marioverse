package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.SoundRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class PanelBlock extends FaceAttachedHorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape FLOOR = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    protected static final VoxelShape CEILING = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape NORTH = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    protected static final VoxelShape SOUTH = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    protected static final VoxelShape EAST = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    protected static final VoxelShape WEST = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    public static final MapCodec<PanelBlock> CODEC = simpleCodec(PanelBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @NotNull
    @Override
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public PanelBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACE, AttachFace.FLOOR).setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACE, FACING, WATERLOGGED);
    }

    @NotNull
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
}
