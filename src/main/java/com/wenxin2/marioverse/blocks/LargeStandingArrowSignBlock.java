package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class LargeStandingArrowSignBlock extends StandingArrowSignBlock {
    public static final EnumProperty<HalfBlockStates> HALF = BlockStatePropertyRegistry.HALF;

    public LargeStandingArrowSignBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, HalfBlockStates.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == HalfBlockStates.BOTTOM ? super.newBlockEntity(pos, state) : null;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        if (pos.getY() >= context.getLevel().getMaxBuildHeight() - 1
                || !context.getLevel().getBlockState(pos.above()).canBeReplaced(context))
            return null;

        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(HALF, HalfBlockStates.BOTTOM);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, HalfBlockStates.TOP), Block.UPDATE_ALL);
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) == HalfBlockStates.TOP) {
            BlockState below = level.getBlockState(pos.below());
            return below.is(this) && below.getValue(HALF) == HalfBlockStates.BOTTOM;
        }
        return super.canSurvive(state, level, pos);
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        HalfBlockStates half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y && (half == HalfBlockStates.BOTTOM) == (direction == Direction.UP)) {
            if (!(neighborState.is(this) && neighborState.getValue(HALF) != half))
                return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(HALF) == HalfBlockStates.TOP) {
            BlockPos belowPos = pos.below();
            return super.useWithoutItem(level.getBlockState(belowPos), level, belowPos, player, hitResult);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (state.getValue(HALF) == HalfBlockStates.TOP) {
            BlockPos belowPos = pos.below();
            return super.useItemOn(stack, level.getBlockState(belowPos), level, belowPos, player, hand, hitResult);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
