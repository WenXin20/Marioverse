package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SuperMushroomStemBlock extends DirectionalBlock implements BonemealableBlock {
    public static final MapCodec<SuperMushroomStemBlock> CODEC = simpleCodec(SuperMushroomStemBlock::new);
    public static final BooleanProperty END = BlockStatePropertyRegistry.END;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;

    @NotNull
    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public SuperMushroomStemBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(END, true)
                .setValue(FACING, Direction.UP).setValue(UP, true).setValue(DOWN, true)
                .setValue(NORTH, true).setValue(EAST, true).setValue(SOUTH, true).setValue(WEST, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        BlockPos pos = context.getClickedPos();
        BlockState neighborState = context.getLevel().getBlockState(pos.relative(facing));
        BlockState state = this.defaultBlockState().setValue(FACING, facing)
                .setValue(END, !context.isSecondaryUseActive() && !(neighborState.getBlock() instanceof SuperMushroomStemBlock));

        for (Direction direction : Direction.values())
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction),
                    !context.getLevel().getBlockState(pos.relative(direction)).is(this));
        return state;
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        BlockState newState = neighborState.is(this)
                ? state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction), false) : state;

        if (direction == state.getValue(FACING)) {
            if (neighborState.getBlock() instanceof SuperMushroomStemBlock)
                return newState.setValue(END, false);
            if (neighborState.isAir())
                return newState.setValue(END, true);
        }
        return newState;
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!stack.is(Items.BONE_MEAL))
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);

        BooleanProperty faceProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(hitResult.getDirection());
        if (state.getValue(faceProperty))
            return ItemInteractionResult.CONSUME;

        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(faceProperty, true), Block.UPDATE_ALL);
            level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 15);
            stack.consume(1, player);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        BooleanProperty faceProperty = PipeBlock.PROPERTY_BY_DIRECTION.get(context.getClickedFace());
        if (itemAbility == ItemAbilities.AXE_STRIP && state.getValue(faceProperty)) {
            if (!simulate && context.getLevel() instanceof ServerLevel serverLevel)
                SuperMushroomCapBlock.spawnScrapeParticles(serverLevel, context.getClickedPos(), state, context.getClickedFace());
            return state.setValue(faceProperty, false);
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return PipeBlock.PROPERTY_BY_DIRECTION.values().stream().anyMatch(property -> !state.getValue(property));
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        List<BooleanProperty> faceProperties = PipeBlock.PROPERTY_BY_DIRECTION.values().stream()
                .filter(property -> !state.getValue(property)).toList();
        if (!faceProperties.isEmpty())
            serverLevel.setBlock(pos, state.setValue(Util.getRandom(faceProperties, random), true), Block.UPDATE_ALL);
    }

    @NotNull
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState newState = state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
        for (Direction direction : Direction.values())
            newState = newState.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(rotation.rotate(direction)),
                    state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction)));
        return newState;
    }

    @NotNull
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState newState = state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
        for (Direction direction : Direction.values())
            newState = newState.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(mirror.mirror(direction)),
                    state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction)));
        return newState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(END, FACING, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}
