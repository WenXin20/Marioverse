package com.wenxin2.marioverse.blocks.behaviors;

import com.wenxin2.marioverse.blocks.SplunkinCarvedPumpkinBlock;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class DispenserBehaviors {
    public static void register() {
        DispenseItemBehavior dispenseBucketBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem)stack.getItem();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();

                if (dispensibleContainerItem.emptyContents(null, level, pos, null, stack)) {
                    dispensibleContainerItem.checkExtraContent(null, level, stack, pos);
                    return this.consumeWithRemainder(blockSource, stack, new ItemStack(Items.BUCKET));
                } else return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
            }
        };

        DispenseItemBehavior dispensePlasticBucketBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem)stack.getItem();
                BlockPos pos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
                Level level = blockSource.level();

                if (dispensibleContainerItem.emptyContents(null, level, pos, null, stack)) {
                    dispensibleContainerItem.checkExtraContent(null, level, stack, pos);
                    return this.consumeWithRemainder(blockSource, stack, new ItemStack(ItemRegistry.PLASTIC_BUCKET.get()));
                } else return this.defaultDispenseItemBehavior.dispense(blockSource, stack);
            }
        };


        DispenserBlock.registerProjectileBehavior(ItemRegistry.LARGE_SNOWBALL);
        DispenserBlock.registerBehavior(ItemRegistry.QUICKSAND_BUCKET, dispenseBucketBehavior);
        DispenserBlock.registerBehavior(ItemRegistry.QUICKSAND_PLASTIC_BUCKET, dispensePlasticBucketBehavior);
        DispenserBlock.registerBehavior(ItemRegistry.POWDER_SNOW_PLASTIC_BUCKET, dispensePlasticBucketBehavior);

        DispenserBlock.registerBehavior(ItemRegistry.PLASTIC_BUCKET, new DefaultDispenseItemBehavior() {
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                LevelAccessor level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(
                        blockSource.state().getValue(DispenserBlock.FACING));
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof BucketPickup bucketPickup))
                    return super.execute(blockSource, stack);

                ItemStack newStack;
                if (state.is(BlockRegistry.QUICKSAND.get()))
                    newStack = new ItemStack(ItemRegistry.QUICKSAND_PLASTIC_BUCKET.get());
                else if (state.is(Blocks.POWDER_SNOW))
                    newStack = new ItemStack(ItemRegistry.POWDER_SNOW_PLASTIC_BUCKET.get());
                else return super.execute(blockSource, stack);
                newStack.applyComponents(stack.getComponents());

                ItemStack vanillaResult =
                        bucketPickup.pickupBlock(null, level, pos, state);

                if (vanillaResult.isEmpty())
                    return super.execute(blockSource, stack);

                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);

                return this.consumeWithRemainder(blockSource, stack, newStack);
            }
        });

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
