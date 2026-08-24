package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import javax.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

public class LargeStandingArrowSignBlock extends StandingArrowSignBlock {
    public static final EnumProperty<HalfBlockStates> HALF = BlockStatePropertyRegistry.HALF;

    protected static final VoxelShape BOTTOM_DEFAULT_SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 32.0, 14.0);
    protected static final VoxelShape TOP_DEFAULT_SHAPE = Block.box(2.0, -16.0, 2.0, 14.0, 16.0, 14.0);
    private static final Map<Integer, VoxelShape> BOTTOM_POST_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Block.box(5.0, 0.0, 6.5, 11.0, 32.0, 9.5),
                    4, Block.box(6.5, 0.0, 5.0, 9.5, 32.0, 11.0),
                    8, Block.box(5.0, 0.0, 6.5, 11.0, 32.0, 9.5),
                    12, Block.box(6.5, 0.0, 5.0, 9.5, 32.0, 11.0)));
    private static final Map<Integer, VoxelShape> TOP_POST_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Block.box(5.0, -16.0, 6.5, 11.0, 16.0, 9.5),
                    4, Block.box(6.5, -16.0, 5.0, 9.5, 16.0, 11.0),
                    8, Block.box(5.0, -16.0, 6.5, 11.0, 16.0, 9.5),
                    12, Block.box(6.5, -16.0, 5.0, 9.5, 16.0, 11.0)));
    private static final Map<Integer, VoxelShape> BOTTOM_BOARD_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Shapes.or(Block.box(-6.0, 9.0, 4.5, 20.0, 19.0, 6.5),
                            Block.box(-4.0, 19.0, 4.5, 22.0, 29.0, 6.5)).optimize(),
                    4, Shapes.or(Block.box(9.5, 9.0, -6.0, 11.5, 19.0, 20.0),
                            Block.box(9.5, 19.0, -4.0, 11.5, 29.0, 22.0)).optimize(),
                    8, Shapes.or(Block.box(-4.0, 9.0, 9.5, 22.0, 19.0, 11.5),
                            Block.box(-6.0, 19.0, 9.5, 20.0, 29.0, 11.5)).optimize(),
                    12, Shapes.or(Block.box(4.5, 9.0, -4.0, 6.5, 19.0, 22.0),
                            Block.box(4.5, 19.0, -6.0, 6.5, 29.0, 20.0)).optimize()));
    private static final Map<Integer, VoxelShape> TOP_BOARD_ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Shapes.or(Block.box(-6.0, -7.0, 4.5, 20.0, 3.0, 6.5),
                            Block.box(-4.0, 3.0, 4.5, 22.0, 13.0, 6.5)).optimize(),
                    4, Shapes.or(Block.box(9.5, -7.0, -6.0, 11.5, 3.0, 20.0),
                            Block.box(9.5, 3.0, -4.0, 11.5, 13.0, 22.0)).optimize(),
                    8, Shapes.or(Block.box(-4.0, -7.0, 9.5, 22.0, 3.0, 11.5),
                            Block.box(-6.0, 3.0, 9.5, 20.0, 13.0, 11.5)).optimize(),
                    12, Shapes.or(Block.box(4.5, -7.0, -4.0, 6.5, 3.0, 22.0),
                            Block.box(4.5, 3.0, -6.0, 6.5, 13.0, 20.0)).optimize()));

    public LargeStandingArrowSignBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(HALF, HalfBlockStates.BOTTOM));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state.setValue(HALF, HalfBlockStates.BOTTOM);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockPos abovePos = pos.above();
        level.setBlock(abovePos, state.setValue(HALF, HalfBlockStates.TOP)
                .setValue(BlockStateProperties.WATERLOGGED, level.getFluidState(abovePos).getType() == Fluids.WATER), Block.UPDATE_ALL);
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
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        boolean isBottom = state.getValue(HALF) == HalfBlockStates.BOTTOM;
        VoxelShape defaultShape = isBottom ? BOTTOM_DEFAULT_SHAPE : TOP_DEFAULT_SHAPE;
        int rotation = state.getValue(BlockStateProperties.ROTATION_16);
        VoxelShape postShape = (isBottom ? BOTTOM_POST_ROTATION_SHAPE : TOP_POST_ROTATION_SHAPE).get(rotation);

        if (!state.getValue(BOARD))
            return postShape != null ? postShape : defaultShape;

        VoxelShape boardShape = (isBottom ? BOTTOM_BOARD_ROTATION_SHAPE : TOP_BOARD_ROTATION_SHAPE).get(rotation);

        if (!state.getValue(POST))
            return boardShape != null ? boardShape : defaultShape;

        return boardShape != null && postShape != null
                ? Shapes.or(boardShape, postShape).optimize() : defaultShape;
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

    @Override
    protected boolean wax(Level level, BlockPos pos, ItemStack stack, Player player) {
        boolean result = super.wax(level, pos, stack, player);

        if (result && !level.isClientSide) {
            BlockPos posOther = this.otherHalfPos(level.getBlockState(pos), pos);
            if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherEntity) {
                otherEntity.setWaxed(true);
                level.levelEvent(null, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, posOther, 0);
            }
        }
        return result;
    }

    @Override
    protected boolean toggleBoard(Level level, BlockState state, BlockPos pos, ItemStack stack) {
        if (!stack.is(ItemTags.AXES))
            return false;

        if (!level.isClientSide) {
            level.setBlock(pos, nextToggleBoardState(state), Block.UPDATE_CLIENTS);

            BlockPos otherPos = this.otherHalfPos(state, pos);
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this))
                level.setBlock(otherPos, nextToggleBoardState(otherState), Block.UPDATE_CLIENTS);

            level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS);
        }
        return true;
    }

    private static BlockState nextToggleBoardState(BlockState state) {
        boolean hasBoard = state.getValue(BOARD);
        boolean hasPost = state.getValue(POST);

        if (hasBoard && hasPost)
            return state.setValue(POST, false);
        else if (hasBoard)
            return state.setValue(BOARD, false).setValue(POST, true);
        else return state.setValue(BOARD, true);
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

        BlockPos posOther = this.otherHalfPos(state, pos);
        BlockState stateOther = level.getBlockState(posOther);
        if (stateOther.is(this))
            level.setBlock(posOther, stateOther.setValue(ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
        if (level.getBlockEntity(posOther) instanceof ArrowSignBlockEntity otherSignBE)
            otherSignBE.setArrowDirection(direction);

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

            BlockPos otherPos = this.otherHalfPos(state, pos);
            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this) && otherState.getValue(ARROW_DIRECTION) != ArrowDirection.NONE)
                level.setBlock(otherPos, otherState.setValue(ARROW_DIRECTION, ArrowDirection.NONE), Block.UPDATE_CLIENTS);
        }
        return true;
    }

    @Override
    public void destroy(LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (!levelAccessor.isClientSide() && levelAccessor instanceof Level level) {
            BlockPos posOther = this.otherHalfPos(state, pos);
            if (level.getBlockState(posOther).is(this)) {
                level.destroyBlock(posOther, false);
                levelAccessor.levelEvent(2001, posOther, Block.getId(levelAccessor.getBlockState(posOther)));
            }
        }
        super.destroy(levelAccessor, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && (player.isCreative() || !player.hasCorrectToolForDrops(state, level, pos))) {
            BlockPos posOther = this.otherHalfPos(state, pos);
            if (level.getBlockState(posOther).is(this)) {
                level.destroyBlock(posOther, false);
                level.levelEvent(2001, posOther, Block.getId(level.getBlockState(posOther)));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        BlockPos posOther = this.otherHalfPos(state, pos);
        if (level.getBlockState(posOther).is(this)) {
            level.destroyBlock(posOther, false);
            level.levelEvent(2001, posOther, Block.getId(level.getBlockState(posOther)));
        }
        super.onBlockExploded(state, level, pos, explosion);
    }

    private BlockPos otherHalfPos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == HalfBlockStates.BOTTOM ? pos.above() : pos.below();
    }
}
