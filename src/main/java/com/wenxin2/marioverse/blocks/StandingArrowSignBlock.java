package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.ArrowSignBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ArrowDirection;
import com.wenxin2.marioverse.items.WrenchItem;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BrushItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class StandingArrowSignBlock extends StandingSignBlock {
    public static final EnumProperty<ArrowDirection> ARROW_DIRECTION = BlockStatePropertyRegistry.ARROW_DIRECTION;
    public static final BooleanProperty BOARD = BlockStatePropertyRegistry.BOARD;
    public static final BooleanProperty POST = BlockStatePropertyRegistry.POST;

    protected static final VoxelShape DEFAULT = Block
            .box(3.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    protected static final VoxelShape BOARD_SHAPE = Block
            .box(3.0, 4.0, 3.0, 13.0, 14.0, 13.0);
    protected static final VoxelShape POST_SHAPE = Block
            .box(6.5, 0.0, 6.5, 9.5, 16.0, 9.5);
    private static final Map<Integer, VoxelShape> ROTATION_SHAPE = Maps.newHashMap(ImmutableMap
            .of(0, Shapes.or(Block.box(1, 4, 4.5, 17, 9, 6.5),
                            Block.box(-1, 9, 4.5, 15, 14, 6.5)).optimize(),
                    4, Shapes.or(Block.box(9.5, 4, 1, 11.5, 9, 17),
                            Block.box(9.5, 9, -1, 11.5, 14, 15)).optimize(),
                    8, Shapes.or(Block.box(-1, 4, 9.5, 15, 9, 11.5),
                            Block.box(1, 9, 9.5, 17, 14, 11.5)).optimize(),
                    12, Shapes.or(Block.box(4.5, 4, -1, 6.5, 9, 15),
                            Block.box(4.5, 9, 1, 6.5, 14, 17)).optimize()));

    public StandingArrowSignBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(ARROW_DIRECTION, ArrowDirection.UP)
                .setValue(BOARD, true)
                .setValue(POST, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ARROW_DIRECTION, BOARD, POST);
    }

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArrowSignBlockEntity(BlockEntityRegistry.ARROW_SIGN.get(), pos, state);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (!state.getValue(BOARD))
            return POST_SHAPE;

        VoxelShape rotationShape = ROTATION_SHAPE.get(state.getValue(BlockStateProperties.ROTATION_16));

        if (!state.getValue(POST))
            return rotationShape != null ? rotationShape : BOARD_SHAPE;

        return rotationShape != null ? Shapes.or(rotationShape, POST_SHAPE).optimize() : DEFAULT;
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(BlockStateProperties.WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!player.getMainHandItem().isEmpty())
            return InteractionResult.PASS;
        return this.rotateArrow(level, state, pos)
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                              InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity
                && signBlockEntity.isWaxed())
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        if (this.wax(level, pos, stack, player))
            return ItemInteractionResult.SUCCESS;
        if (this.removeArrow(level, state, pos, stack))
            return ItemInteractionResult.SUCCESS;
        if (this.toggleBoard(level, state, pos, stack))
            return ItemInteractionResult.SUCCESS;

        if (stack.is(TagRegistry.WRENCHES))
            return this.rotateArrow(level, state, pos)
                    ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    protected boolean wax(Level level, BlockPos pos, ItemStack stack, Player player) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!stack.is(Items.HONEYCOMB))
            return false;

        if (!level.isClientSide) {
            signBlockEntity.setWaxed(true);
            stack.consume(1, player);
            level.levelEvent(null, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
        return true;
    }

    protected boolean rotateArrow(Level level, BlockState state, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!state.getValue(BOARD))
            return false;
        if (level.isClientSide)
            return true;

        var direction = state.getValue(ARROW_DIRECTION).next();
        level.setBlock(pos, state.setValue(ARROW_DIRECTION, direction), Block.UPDATE_CLIENTS);
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        level.playSound(null, pos, SoundRegistry.ARROW_ROTATES.get(), SoundSource.BLOCKS);
        signBlockEntity.setArrowDirection(direction);
        return true;
    }

    protected boolean removeArrow(Level level, BlockState state, BlockPos pos, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!stack.is(TagRegistry.ARROW_ERASERS))
            return false;
        if (state.getValue(ARROW_DIRECTION) == ArrowDirection.NONE)
            return false;

        if (stack.getItem() instanceof BrushItem)
            level.playSound(null, pos, SoundEvents.BRUSH_GENERIC, SoundSource.BLOCKS);
        if (stack.getItem() instanceof ShearsItem)
            level.playSound(null, pos, SoundEvents.BEEHIVE_SHEAR, SoundSource.BLOCKS);

        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(ARROW_DIRECTION, ArrowDirection.NONE), Block.UPDATE_CLIENTS);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
            signBlockEntity.setArrowDirection(ArrowDirection.NONE);
        }
        return true;
    }

    protected boolean toggleBoard(Level level, BlockState state, BlockPos pos, ItemStack stack) {
        if (!(level.getBlockEntity(pos) instanceof ArrowSignBlockEntity signBlockEntity)
                || signBlockEntity.isWaxed())
            return false;
        if (!stack.is(ItemTags.AXES))
            return false;

        boolean hasBoard = state.getValue(BlockStatePropertyRegistry.BOARD);
        boolean hasPost = state.getValue(BlockStatePropertyRegistry.POST);

        BlockState newState;
        if (hasBoard && hasPost)
            newState = state.setValue(BlockStatePropertyRegistry.POST, false);
        else if (hasBoard)
            newState = state.setValue(BlockStatePropertyRegistry.BOARD, false).setValue(BlockStatePropertyRegistry.POST, true);
        else newState = state.setValue(BlockStatePropertyRegistry.BOARD, true);

        if (!level.isClientSide) {
            level.setBlock(pos, newState, Block.UPDATE_CLIENTS);
            level.playSound(null, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS);
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        }
        return true;
    }
}