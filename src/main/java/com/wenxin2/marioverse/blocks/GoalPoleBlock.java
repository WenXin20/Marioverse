package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoalPoleBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<GoalPoleBlock> CODEC = simpleCodec(GoalPoleBlock::new);
    public static final EnumProperty<ColumnBlockStates> COLUMN = EnumProperty.create("column", ColumnBlockStates.class);
    public static final BooleanProperty FLAG = BooleanProperty.create("flag");
    public static final BooleanProperty LOWERED = BooleanProperty.create("lowered");
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;

    protected static final VoxelShape GOAL_POLE_MIDDLE = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0).optimize();
    protected static final VoxelShape GOAL_POLE_TOP = Shapes.or(
            Block.box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0),
            Block.box(4.0, 8.0, 4.0, 12.0, 16.0, 12.0)).optimize();
    protected static final VoxelShape GOAL_POLE_NONE = Shapes.or(
            Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0),
            Block.box(5.0, 10.0, 5.0, 11.0, 16.0, 11.0)).optimize();

    public GoalPoleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(COLUMN, ColumnBlockStates.NONE)
                .setValue(FLAG, Boolean.TRUE).setValue(LOWERED, Boolean.FALSE).setValue(ROTATION, 0).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @NotNull
    @Override
    public MapCodec<GoalPoleBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(COLUMN, FLAG, LOWERED, ROTATION, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(COLUMN) == ColumnBlockStates.TOP)
            return GOAL_POLE_TOP;
        if (state.getValue(COLUMN) == ColumnBlockStates.NONE)
            return GOAL_POLE_NONE;
        else return GOAL_POLE_MIDDLE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(FLAG))
            return new GoalPoleBlockEntity(pos, state);
        else return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8)
                .setValue(ROTATION, RotationSegment.convertToSegment(placeContext.getRotation()));
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        Block blockAbove = worldAccessor.getBlockState(pos.above()).getBlock();
        Block blockBelow = worldAccessor.getBlockState(pos.below()).getBlock();

        if (!state.getValue(FLAG) && worldAccessor instanceof ServerLevel serverWorld) {
            BlockEntity blockEntity = worldAccessor.getBlockEntity(pos);
            if (blockEntity != null) {
                serverWorld.removeBlockEntity(pos);
            }
        }

        if (blockAbove instanceof GoalPoleBlock) {
            if (blockBelow instanceof GoalPoleBlock)
                return state.setValue(COLUMN, ColumnBlockStates.MIDDLE).setValue(FLAG, Boolean.FALSE);
            return state.setValue(COLUMN, ColumnBlockStates.BOTTOM).setValue(FLAG, Boolean.FALSE);
        }

        if (blockBelow instanceof GoalPoleBlock)
            return state.setValue(COLUMN, ColumnBlockStates.TOP).setValue(FLAG, Boolean.TRUE);

        if (state.getValue(WATERLOGGED)) {
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
        }

        return state.setValue(COLUMN, ColumnBlockStates.NONE).setValue(FLAG, Boolean.TRUE);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), ROTATIONS));
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide() && entity.getType().is(TagRegistry.CAN_LOWER_FLAGS)) {
            if (!state.getValue(LOWERED)) {

                if (state.getValue(COLUMN) == ColumnBlockStates.TOP) {
                    world.setBlock(pos, state.setValue(LOWERED, Boolean.TRUE), 3);
                }

                if (state.getValue(COLUMN) == ColumnBlockStates.NONE) {
                    world.setBlock(pos, state.setValue(LOWERED, Boolean.TRUE), 3);
                }
                world.scheduleTick(pos, this, 3);
                world.setBlock(pos, state.setValue(LOWERED, Boolean.TRUE), 3);
                world.playSound(null, entity.blockPosition(), SoundRegistry.GOAL_POLE_FINISH.get(), SoundSource.BLOCKS);
            }

            entity.setDeltaMovement(entity.getDeltaMovement().add(0, -0.01, 0));
            entity.resetFallDistance();
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        super.tick(state, serverWorld, pos, random);
        updateConnectedFlags(serverWorld, pos);
    }

    private void updateConnectedFlags(Level world, BlockPos pos) {
        BlockPos posAbove = pos.above();
        while (world.getBlockState(posAbove).getBlock() instanceof GoalPoleBlock) {
            world.setBlock(posAbove, world.getBlockState(posAbove).setValue(LOWERED, Boolean.TRUE), 3);
            posAbove = posAbove.above();
        }

        BlockPos posBelow = pos.below();
        while (world.getBlockState(posBelow).getBlock() instanceof GoalPoleBlock) {
            world.setBlock(posBelow, world.getBlockState(posBelow).setValue(LOWERED, Boolean.TRUE), 3);
            posBelow = posBelow.below();
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        if (state.getValue(LOWERED) && (state.getValue(COLUMN) == ColumnBlockStates.BOTTOM
                || state.getValue(COLUMN) == ColumnBlockStates.NONE))
            return 16;
        else return super.getAnalogOutputSignal(state, world, pos);
    }
}
