package com.wenxin2.marioverse.blocks.behaviors;

import com.wenxin2.marioverse.blocks.SplunkinCarvedPumpkinBlock;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class DispenserBehaviors {
    public static void register() {
        DispenserBlock.registerProjectileBehavior(ItemRegistry.LARGE_SNOWBALL);

        DispenserBlock.registerBehavior(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get(), new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level world = source.level();
                BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get();
                if (world.isEmptyBlock(blockpos)) {
                    if (!world.isClientSide) {
                        world.setBlock(blockpos, carvedPumpkinBlock.defaultBlockState(), 3);
                        world.gameEvent(null, GameEvent.BLOCK_PLACE, blockpos);
                    }

                    stack.shrink(1);
                    this.setSuccess(true);
                } else this.setSuccess(ArmorItem.dispenseArmor(source, stack));
                return stack;
            }
        });

        DispenserBlock.registerBehavior(Items.PUMPKIN_PIE, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Level world = source.level();
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(facing);
                BlockState targetState = world.getBlockState(targetPos);

                if (targetState.hasProperty(SplunkinCarvedPumpkinBlock.CRACKED)) {
                    if (targetState.getValue(SplunkinCarvedPumpkinBlock.CRACKED)) {
                        world.setBlock(targetPos, targetState.setValue(SplunkinCarvedPumpkinBlock.CRACKED, false), 3);
                        world.playSound(null, targetPos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS);
                        stack.shrink(1);
                    }
                }
                return stack;
            }
        });

        DispenserBlock.registerBehavior(Items.SHEARS, new OptionalDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel world = source.level();
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(facing);
                BlockState targetState = world.getBlockState(targetPos);

                if (targetState.hasProperty(SplunkinCarvedPumpkinBlock.CRACKED)) {
                    if (!targetState.getValue(SplunkinCarvedPumpkinBlock.CRACKED)) {
                        world.setBlock(targetPos, targetState.setValue(SplunkinCarvedPumpkinBlock.CRACKED, true), 3);
                        world.levelEvent(null, 2001, targetPos, Block.getId(targetState));
                        world.playSound(null, targetPos, SoundRegistry.SPLUNKIN_CRACKS.get(), SoundSource.BLOCKS);
                        stack.hurtAndBreak(1, world, null, item -> {});
                    }
                }
                return stack;
            }
        });
    }
}
