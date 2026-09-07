package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class RoseHedgeBlock extends HedgeBlock {
    public static final BooleanProperty FLOWERS = BlockStatePropertyRegistry.FLOWERS;
    public RoseHedgeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FLOWERS, true)
                .setValue(SNOWY, false).setValue(TOP, true).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FLOWERS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return super.getStateForPlacement(context).setValue(FLOWERS, true);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        serverLevel.setBlockAndUpdate(pos, state.setValue(FLOWERS, true));
        super.performBonemeal(serverLevel, random, pos, state);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return HedgeBlock.canSpread(levelReader, pos, state) || !state.getValue(FLOWERS);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return HedgeBlock.canSpread(level, pos, state) || !state.getValue(FLOWERS);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (state.getValue(FLOWERS) && stack.is(Tags.Items.TOOLS_SHEAR)) {
            level.setBlockAndUpdate(pos, state.setValue(FLOWERS, false));
            level.playSound(player, pos, SoundEvents.MOOSHROOM_SHEAR, SoundSource.BLOCKS, 1.0F, pitch);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            if (stack.isDamageableItem())
                stack.hurtAndBreak(1, player, Player.getSlotForHand(player.getUsedItemHand()));

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
