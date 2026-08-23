package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.blocks.states.SideBlockStates;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class LargeWallArrowSignBlock extends WallArrowSignBlock {
    public static final EnumProperty<HalfBlockStates> HALF = BlockStatePropertyRegistry.HALF;
    public static final EnumProperty<SideBlockStates> SIDE = BlockStatePropertyRegistry.SIDE;

    // Derived from the board cubes in large_wall_arrow_sign.geo.json, mirrored across the width
    // axis (x at FACING=NORTH), not the depth axis, so the board's depth stays hugging the wall
    // as authored (matching the standing sign's correction) while each plank's width-axis span
    // still pairs with its own y-range. Each of the 4 blocks returns the full, uninterrupted shape
    // of the whole board (translated into its own local frame) rather than being clipped to its
    // own quadrant, so there's no seam at the block boundaries. RIGHT is a pure translation of
    // LEFT (no further mirroring) by the same offset used to place the RIGHT block
    // (clockwise from FACING).
    private static final Map<Direction, VoxelShape> BOTTOM_LEFT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(2, 6, 14, 28, 16, 16), Block.box(4, 16, 14, 30, 26, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, 6, 2, 2, 16, 28), Block.box(0, 16, 4, 2, 26, 30)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(-12, 6, 0, 14, 16, 2), Block.box(-14, 16, 0, 12, 26, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, 6, -12, 16, 16, 14), Block.box(14, 16, -14, 16, 26, 12)).optimize()));
    private static final Map<Direction, VoxelShape> BOTTOM_RIGHT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(-14, 6, 14, 12, 16, 16), Block.box(-12, 16, 14, 14, 26, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, 6, -14, 2, 16, 12), Block.box(0, 16, -12, 2, 26, 14)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(4, 6, 0, 30, 16, 2), Block.box(2, 16, 0, 28, 26, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, 6, 4, 16, 16, 30), Block.box(14, 16, 2, 16, 26, 28)).optimize()));
    private static final Map<Direction, VoxelShape> TOP_LEFT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(2, -10, 14, 28, 0, 16), Block.box(4, 0, 14, 30, 10, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, -10, 2, 2, 0, 28), Block.box(0, 0, 4, 2, 10, 30)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(-12, -10, 0, 14, 0, 2), Block.box(-14, 0, 0, 12, 10, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, -10, -12, 16, 0, 14), Block.box(14, 0, -14, 16, 10, 12)).optimize()));
    private static final Map<Direction, VoxelShape> TOP_RIGHT_SHAPE = Maps.newEnumMap(ImmutableMap
            .of(Direction.NORTH, Shapes.or(Block.box(-14, -10, 14, 12, 0, 16), Block.box(-12, 0, 14, 14, 10, 16)).optimize(),
                    Direction.EAST, Shapes.or(Block.box(0, -10, -14, 2, 0, 12), Block.box(0, 0, -12, 2, 10, 14)).optimize(),
                    Direction.SOUTH, Shapes.or(Block.box(4, -10, 0, 30, 0, 2), Block.box(2, 0, 0, 28, 10, 2)).optimize(),
                    Direction.WEST, Shapes.or(Block.box(14, -10, 4, 16, 0, 30), Block.box(14, 0, 2, 16, 10, 28)).optimize()));

    public LargeWallArrowSignBlock(WoodType woodType, BlockBehaviour.Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(HALF, HalfBlockStates.BOTTOM).setValue(SIDE, SideBlockStates.LEFT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF, SIDE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == HalfBlockStates.BOTTOM && state.getValue(SIDE) == SideBlockStates.LEFT
                ? super.newBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state == null)
            return null;

        Direction rightDir = state.getValue(FACING).getClockWise();
        BlockPos pos = context.getClickedPos();
        LevelReader level = context.getLevel();

        BlockPos rightPos = pos.relative(rightDir);
        BlockPos abovePos = pos.above();
        BlockPos aboveRightPos = rightPos.above();

        if (!level.getBlockState(rightPos).canBeReplaced(context)
                || !level.getBlockState(abovePos).canBeReplaced(context)
                || !level.getBlockState(aboveRightPos).canBeReplaced(context))
            return null;

        return state.setValue(HALF, HalfBlockStates.BOTTOM).setValue(SIDE, SideBlockStates.LEFT);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        Direction rightDir = state.getValue(FACING).getClockWise();
        BlockPos rightPos = pos.relative(rightDir);
        BlockPos abovePos = pos.above();
        BlockPos aboveRightPos = rightPos.above();

        level.setBlock(rightPos, state.setValue(SIDE, SideBlockStates.RIGHT)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(rightPos).getType() == Fluids.WATER), Block.UPDATE_ALL);
        level.setBlock(abovePos, state.setValue(HALF, HalfBlockStates.TOP)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(abovePos).getType() == Fluids.WATER), Block.UPDATE_ALL);
        level.setBlock(aboveRightPos, state.setValue(HALF, HalfBlockStates.TOP).setValue(SIDE, SideBlockStates.RIGHT)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(aboveRightPos).getType() == Fluids.WATER), Block.UPDATE_ALL);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    // SIDE (LEFT/RIGHT) means "clockwise from FACING", a relationship that's rotation-invariant, so
    // a pure rotation only needs to carry FACING along. A mirror reverses chirality though: the
    // part that was clockwise-of-primary becomes counter-clockwise-of-primary, so SIDE has to flip
    // too, or the mirrored copy's own getShape/primaryPos logic would point at the wrong block.
    @NotNull
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @NotNull
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        BlockState rotated = this.rotate(state, mirror.getRotation(state.getValue(FACING)));
        SideBlockStates flippedSide = rotated.getValue(SIDE) == SideBlockStates.LEFT
                ? SideBlockStates.RIGHT : SideBlockStates.LEFT;
        return rotated.setValue(SIDE, flippedSide);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean isBottom = state.getValue(HALF) == HalfBlockStates.BOTTOM;
        boolean isLeft = state.getValue(SIDE) == SideBlockStates.LEFT;

        Map<Direction, VoxelShape> shapeMap = isBottom
                ? (isLeft ? BOTTOM_LEFT_SHAPE : BOTTOM_RIGHT_SHAPE)
                : (isLeft ? TOP_LEFT_SHAPE : TOP_RIGHT_SHAPE);
        return shapeMap.get(facing);
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        HalfBlockStates half = state.getValue(HALF);
        SideBlockStates side = state.getValue(SIDE);
        Direction towardPartner = side == SideBlockStates.LEFT
                ? state.getValue(FACING).getClockWise() : state.getValue(FACING).getCounterClockWise();

        if (direction.getAxis() == Direction.Axis.Y && (half == HalfBlockStates.BOTTOM) == (direction == Direction.UP)) {
            if (!(neighborState.is(this) && neighborState.getValue(HALF) != half && neighborState.getValue(SIDE) == side))
                return Blocks.AIR.defaultBlockState();
        } else if (direction == towardPartner) {
            if (!(neighborState.is(this) && neighborState.getValue(SIDE) != side && neighborState.getValue(HALF) == half))
                return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    private BlockPos primaryPos(BlockState state, BlockPos pos) {
        BlockPos basePos = state.getValue(SIDE) == SideBlockStates.RIGHT
                ? pos.relative(state.getValue(FACING).getCounterClockWise()) : pos;
        return state.getValue(HALF) == HalfBlockStates.TOP ? basePos.below() : basePos;
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockPos primaryPos = this.primaryPos(state, pos);
        if (!primaryPos.equals(pos))
            return super.useWithoutItem(level.getBlockState(primaryPos), level, primaryPos, player, hitResult);
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        BlockPos primaryPos = this.primaryPos(state, pos);
        if (!primaryPos.equals(pos))
            return super.useItemOn(stack, level.getBlockState(primaryPos), level, primaryPos, player, hand, hitResult);
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    // The interaction methods above always redirect to the BOTTOM+LEFT part's pos/state before
    // calling super, so `pos` here is always the primary block, and the other 3 parts can be found
    // relative to it. ARROW_DIRECTION is a per-block property, so rotating/erasing must be mirrored
    // onto all 4 parts or it desyncs.
    private BlockPos[] otherPartPositions(BlockState state, BlockPos primaryPos) {
        Direction rightDir = state.getValue(FACING).getClockWise();
        BlockPos rightPos = primaryPos.relative(rightDir);
        return new BlockPos[] { rightPos, primaryPos.above(), rightPos.above() };
    }

    @Override
    protected boolean rotateArrow(Level level, BlockState state, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!state.getValue(BOARD))
            return false;
        if (level.isClientSide)
            return true;

        ArrowDirection direction = state.getValue(ARROW_DIRECTION).next();
        level.setBlock(pos, state.setValue(ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
        signBlockEntity.setArrowDirection(direction);

        for (BlockPos otherPos : this.otherPartPositions(state, pos)) {
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this))
                level.setBlock(otherPos, otherState.setValue(ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
        }

        level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS);
        return true;
    }

    @Override
    protected boolean removeArrow(Level level, BlockState state, BlockPos pos, ItemStack stack) {
        if (!stack.is(TagRegistry.ARROW_ERASERS))
            return false;
        if (state.getValue(ARROW_DIRECTION) == ArrowDirection.NONE)
            return false;

        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ARROW_DIRECTION, ArrowDirection.NONE), Block.UPDATE_CLIENTS);
            if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                signBlockEntity.setArrowDirection(ArrowDirection.NONE);

            for (BlockPos otherPos : this.otherPartPositions(state, pos)) {
                BlockState otherState = level.getBlockState(otherPos);
                if (otherState.is(this) && otherState.getValue(ARROW_DIRECTION) != ArrowDirection.NONE)
                    level.setBlock(otherPos, otherState.setValue(ARROW_DIRECTION, ArrowDirection.NONE), Block.UPDATE_CLIENTS);
            }
        }
        return true;
    }

    // Breaking any one of the 4 parts should take the whole sign down. The updateShape cascade
    // above handles orphaned parts eventually, but explicitly removing all 4 here is immediate and
    // doesn't depend on neighbor-update propagation reaching the diagonally-opposite corner.
    private void removeOtherParts(Level level, BlockState state, BlockPos pos, boolean dropResources) {
        BlockPos primaryPos = this.primaryPos(state, pos);
        for (BlockPos otherPos : this.otherPartPositions(state, primaryPos)) {
            if (otherPos.equals(pos))
                continue;
            if (level.getBlockState(otherPos).is(this))
                level.destroyBlock(otherPos, dropResources);
        }
        if (!primaryPos.equals(pos) && level.getBlockState(primaryPos).is(this))
            level.destroyBlock(primaryPos, dropResources);
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        if (!level.isClientSide() && level instanceof Level realLevel)
            this.removeOtherParts(realLevel, state, pos, true);
        super.destroy(level, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide)
            this.removeOtherParts(level, state, pos, !player.isCreative());
        return super.playerWillDestroy(level, pos, state, player);
    }
}
