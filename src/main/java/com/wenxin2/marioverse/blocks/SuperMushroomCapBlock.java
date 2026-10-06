package com.wenxin2.marioverse.blocks;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class SuperMushroomCapBlock extends HugeMushroomBlock implements BonemealableBlock {
    public static final BooleanProperty BOTTOM = BlockStatePropertyRegistry.BOTTOM;

    public SuperMushroomCapBlock( Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BOTTOM, true));
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateBelow = context.getLevel().getBlockState(context.getClickedPos().below());
        return super.getStateForPlacement(context)
                .setValue(BOTTOM, !(stateBelow.getBlock() instanceof SuperMushroomCapBlock));
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor levelAccessor, BlockPos pos, BlockPos neighborPos) {
        BlockState newState = super.updateShape(state, direction, neighborState, levelAccessor, pos, neighborPos);
        if (direction == Direction.DOWN)
            return newState.setValue(BOTTOM, !(neighborState.getBlock() instanceof SuperMushroomCapBlock));
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BOTTOM, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}
