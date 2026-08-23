package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.blocks.states.SideBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class LargeWallArrowSignBlock extends WallArrowSignBlock {
    public static final EnumProperty<HalfBlockStates> HALF = BlockStatePropertyRegistry.HALF;
    public static final EnumProperty<SideBlockStates> SIDE = BlockStatePropertyRegistry.SIDE;

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

        level.setBlock(rightPos, state.setValue(SIDE, SideBlockStates.RIGHT), Block.UPDATE_ALL);
        level.setBlock(pos.above(), state.setValue(HALF, HalfBlockStates.TOP), Block.UPDATE_ALL);
        level.setBlock(rightPos.above(), state.setValue(HALF, HalfBlockStates.TOP).setValue(SIDE, SideBlockStates.RIGHT), Block.UPDATE_ALL);
        super.setPlacedBy(level, pos, state, placer, stack);
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
}
