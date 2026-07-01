package com.wenxin2.marioverse.blocks.behaviors;

import com.wenxin2.marioverse.blocks.SplunkinCarvedPumpkinBlock;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.IShearable;
import org.jetbrains.annotations.NotNull;

public class DispenserBehaviors {
    public static void register() {
        DispenseItemBehavior dispenseBucketBehavior = new DefaultDispenseItemBehavior() {
            private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

            @NotNull
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) stack.getItem();
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

            @NotNull
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                DispensibleContainerItem dispensibleContainerItem = (DispensibleContainerItem) stack.getItem();
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
        DispenserBlock.registerBehavior(ItemRegistry.PLASTIC_QUICKSAND_BUCKET, dispensePlasticBucketBehavior);
        DispenserBlock.registerBehavior(ItemRegistry.RED_QUICKSAND_BUCKET, dispenseBucketBehavior);
        DispenserBlock.registerBehavior(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET, dispensePlasticBucketBehavior);
        DispenserBlock.registerBehavior(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET, dispensePlasticBucketBehavior);
        DispenserBlock.registerBehavior(ItemRegistry.PLASTIC_WATER_BUCKET, dispensePlasticBucketBehavior);

        DispenseItemBehavior plasticBucketBehavior = DispenserBlock.DISPENSER_REGISTRY.get(ItemRegistry.PLASTIC_BUCKET.get());
        DispenserBlock.registerBehavior(ItemRegistry.PLASTIC_BUCKET.get(), new DefaultDispenseItemBehavior() {
            @NotNull
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                LevelAccessor level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(
                        blockSource.state().getValue(DispenserBlock.FACING));
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof BucketPickup bucketPickup))
                    return plasticBucketBehavior.dispense(blockSource, stack);

                ItemStack newStack;
                if (state.is(BlockRegistry.QUICKSAND.get()))
                    newStack = new ItemStack(ItemRegistry.PLASTIC_QUICKSAND_BUCKET.get());
                else if (state.is(BlockRegistry.RED_QUICKSAND.get()))
                    newStack = new ItemStack(ItemRegistry.PLASTIC_RED_QUICKSAND_BUCKET.get());
                else if (state.is(Blocks.POWDER_SNOW))
                    newStack = new ItemStack(ItemRegistry.PLASTIC_POWDER_SNOW_BUCKET.get());
                else if (state.is(Blocks.WATER))
                    newStack = new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());
                else if (state.is(BlockRegistry.PIPE_BUBBLES))
                    newStack = new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());
                else if (state.is(BlockRegistry.WATER_SPOUT))
                    newStack = new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());
                else return super.execute(blockSource, stack);
                newStack.applyComponents(stack.getComponents());

                ItemStack vanillaResult =
                        bucketPickup.pickupBlock(null, level, pos, state);

                if (vanillaResult.isEmpty())
                    return plasticBucketBehavior.dispense(blockSource, stack);

                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);

                return this.consumeWithRemainder(blockSource, stack, newStack);
            }
        });

        DispenseItemBehavior bucketBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.BUCKET);
        DispenserBlock.registerBehavior(Items.BUCKET, new DefaultDispenseItemBehavior() {
            @NotNull
            @Override
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                LevelAccessor level = blockSource.level();
                BlockPos pos = blockSource.pos().relative(
                        blockSource.state().getValue(DispenserBlock.FACING));
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof BucketPickup bucketPickup))
                    return bucketBehavior.dispense(blockSource, stack);

                ItemStack newStack;
                if (state.is(BlockRegistry.PIPE_BUBBLES))
                    newStack = new ItemStack(Items.WATER_BUCKET);
                else if (state.is(BlockRegistry.WATER_SPOUT))
                    newStack = new ItemStack(Items.WATER_BUCKET);
                else return super.execute(blockSource, stack);

                newStack.applyComponents(stack.getComponents());

                ItemStack vanillaResult =
                        bucketPickup.pickupBlock(null, level, pos, state);

                if (vanillaResult.isEmpty())
                    return bucketBehavior.dispense(blockSource, stack);

                level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);

                return this.consumeWithRemainder(blockSource, stack, newStack);
            }
        });

        DispenserBlock.registerBehavior(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get(), new OptionalDispenseItemBehavior() {
            @NotNull
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();
                BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                CarvedPumpkinBlock carvedPumpkinBlock = (CarvedPumpkinBlock)BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get();
                if (level.isEmptyBlock(pos)) {
                    if (!level.isClientSide) {
                        level.setBlock(pos, carvedPumpkinBlock.defaultBlockState(), 3);
                        level.gameEvent(null, GameEvent.BLOCK_PLACE, pos);
                    }

                    stack.shrink(1);
                    this.setSuccess(true);
                } else this.setSuccess(ArmorItem.dispenseArmor(source, stack));
                return stack;
            }
        });

        DispenseItemBehavior shearsBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.SHEARS);
        DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenseItemBehavior() {
            @NotNull
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(facing);
                BlockState targetState = level.getBlockState(targetPos);
                float pitch = 0.9F + level.random.nextFloat() * 0.2F;

                if (targetState.hasProperty(SplunkinCarvedPumpkinBlock.CRACKED)) {
                    if (!targetState.getValue(SplunkinCarvedPumpkinBlock.CRACKED)) {
                        level.setBlock(targetPos, targetState.setValue(SplunkinCarvedPumpkinBlock.CRACKED, true), 3);
                        level.levelEvent(null, 2001, targetPos, Block.getId(targetState));
                        level.playSound(null, targetPos, SoundRegistry.SPLUNKIN_CRACKS.get(), SoundSource.BLOCKS, 1.0F, pitch);
                        stack.hurtAndBreak(1, level, null, item -> {});
                        return stack;
                    }
                }
                return shearsBehavior.dispense(source, stack);
            }
        });

        DispenseItemBehavior pieBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.PUMPKIN_PIE);
        DispenserBlock.registerBehavior(Items.PUMPKIN_PIE, new OptionalDispenseItemBehavior() {
            @NotNull
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(facing);
                BlockState targetState = level.getBlockState(targetPos);
                float pitch = 0.9F + level.random.nextFloat() * 0.2F;

                if (targetState.hasProperty(SplunkinCarvedPumpkinBlock.CRACKED)) {
                    if (targetState.getValue(SplunkinCarvedPumpkinBlock.CRACKED)) {
                        level.setBlock(targetPos, targetState.setValue(SplunkinCarvedPumpkinBlock.CRACKED, false), 3);
                        level.playSound(null, targetPos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, pitch);
                        stack.shrink(1);
                        return stack;
                    }
                }

                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(targetPos), EntitySelector.NO_SPECTATORS)) {
                    if (entity.getData(DataAttachmentRegistry.CRACKED)) {
                        entity.setData(DataAttachmentRegistry.CRACKED, false);
                        level.playSound(null, targetPos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, pitch);
                        stack.shrink(1);
                        return stack;
                    }
                }
                return pieBehavior.dispense(source, stack);
            }
        });

        DispenseItemBehavior carrotBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.CARROT);
        DispenserBlock.registerBehavior(Items.CARROT, new OptionalDispenseItemBehavior() {
            @NotNull
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(facing);

                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(targetPos), EntitySelector.NO_SPECTATORS)) {
                    if (!entity.getData(DataAttachmentRegistry.HAS_CARROT)
                            && !entity.getData(DataAttachmentRegistry.HAS_GOLDEN_CARROT)) {
                        entity.setData(DataAttachmentRegistry.HAS_CARROT, true);
                        stack.shrink(1);
                        return stack;
                    }
                }
                return carrotBehavior.dispense(source, stack);
            }
        });

        DispenseItemBehavior goldenCarrotBehavior = DispenserBlock.DISPENSER_REGISTRY.get(Items.GOLDEN_CARROT);
        DispenserBlock.registerBehavior(Items.GOLDEN_CARROT, new OptionalDispenseItemBehavior() {
            @NotNull
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                ServerLevel level = source.level();
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos targetPos = source.pos().relative(facing);

                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(targetPos), EntitySelector.NO_SPECTATORS)) {
                    if (!entity.getData(DataAttachmentRegistry.HAS_GOLDEN_CARROT)
                            && !entity.getData(DataAttachmentRegistry.HAS_CARROT)) {
                        entity.setData(DataAttachmentRegistry.HAS_GOLDEN_CARROT, true);
                        stack.shrink(1);
                        return stack;
                    }
                }
                return goldenCarrotBehavior.dispense(source, stack);
            }
        });
    }
}