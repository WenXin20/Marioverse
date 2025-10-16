package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class SplunkinCarvedPumpkinBlock extends CarvedPumpkinBlock {
    public static final BooleanProperty CRACKED = BooleanProperty.create("cracked");

    public SplunkinCarvedPumpkinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CRACKED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CRACKED);
        super.createBlockStateDefinition(stateBuilder);
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return world.getBlockState(pos).getValue(CRACKED) ? 15 : 10;
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.getItemInHand(hand).is(Items.PUMPKIN_PIE) && state.getValue(CRACKED)) {
            world.setBlock(pos, state.setValue(CRACKED, false), 3);
            player.getItemInHand(hand).consume(1, player);
            world.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS);
            return ItemInteractionResult.SUCCESS;
        }

        if (player.isCreative() && !state.getValue(CRACKED)) {
            world.setBlock(pos, state.setValue(CRACKED, true), 3);
            world.levelEvent(player, 2001, pos, getId(state));
        }
        return super.useItemOn(stack, state, world, pos, player, hand, hitResult);
    }

    @Override
    protected void attack(BlockState state, Level world, BlockPos pos, Player player) {
        if (!state.getValue(CRACKED)) {
            world.setBlock(pos, state.setValue(CRACKED, true), 3);
            world.levelEvent(player, 2001, pos, getId(state));
        }
        super.attack(state, world, pos, player);
    }
}
