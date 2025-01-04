package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.entity.projectile.ThrownExperienceBottle;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LingeringPotionItem;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestionBlock extends Block implements EntityBlock {
    public static final MapCodec<QuestionBlock> CODEC = simpleCodec(QuestionBlock::new);
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    public QuestionBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(EMPTY, Boolean.TRUE));
    }

    @NotNull
    @Override
    public MapCodec<QuestionBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(EMPTY);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuestionBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean moved) {
        Containers.dropContentsOnDestroy(oldState, newState, world, pos);
        super.onRemove(oldState, world, pos, newState, moved);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.defaultBlockState().setValue(EMPTY, Boolean.TRUE);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof QuestionBlockEntity questionBlockEntity && ConfigRegistry.REDSTONE_OPENS_QUESTION.get()) {
            boolean isPowered = world.hasNeighborSignal(pos);
            if (isPowered && !state.getValue(EMPTY) && !questionBlockEntity.isLastPowered()) {
                ItemStack storedItem = questionBlockEntity.getItems().getFirst();

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                    this.playSounds(world, pos, storedItem);
                    questionBlockEntity.removeItems();
                    questionBlockEntity.setChanged();
                }

                if (storedItem.isEmpty()) {
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                }

                Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);
                if (questionBlockEntity.getLootTable() != null && nearestPlayer != null) {
                    this.unpackLootTable(nearestPlayer, questionBlockEntity);
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                }
            }
            questionBlockEntity.setLastPowered(isPowered);
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, 
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
            ItemStack heldItem = player.getItemInHand(hand);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof QuestionBlockEntity questionBlockEntity && !heldItem.is(TagRegistry.QUESTION_BLOCK_ITEM_BLACKLIST)) {
                ItemStack blockStack = questionBlockEntity.getStackInSlot();

                if (questionBlockEntity.getLootTable() != null)
                    this.unpackLootTable(player, questionBlockEntity);

                if (!heldItem.isEmpty() && questionBlockEntity.getLootTable() == null
                        && (ConfigRegistry.QUESTION_ADD_ITEMS.get() || player.isCreative())
                        && (!questionBlockEntity.hasItems() || ItemStack.isSameItemSameComponents(heldItem, blockStack))) {
                    questionBlockEntity.addItem(heldItem);
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                    questionBlockEntity.setChanged();
                    if(!player.isCreative())
                        stack.shrink(heldItem.getCount());
                    return ItemInteractionResult.SUCCESS;
                } else if (heldItem.isEmpty() && (ConfigRegistry.QUESTION_REMOVE_ITEMS.get() || player.isCreative())
                        && !state.getValue(EMPTY)) {
                    ItemStack storedItem = questionBlockEntity.getItems().getFirst();

                    if (!storedItem.isEmpty()) {
                        if (!world.isClientSide)
                            this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                        this.playSounds(world, pos, storedItem);
                        questionBlockEntity.removeItems();
                        questionBlockEntity.setChanged();
                    }

                    if (storedItem.isEmpty()) {
                        world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                    }
                    return ItemInteractionResult.SUCCESS;
                } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void spawnFromQuestionBlock(Level world, BlockPos pos, ItemStack stack, Entity entityHitBlock, boolean dropItemsAtPos, boolean applyUpMotion) {
        if (world instanceof ServerLevel serverWorld) {
            if (stack.getItem() instanceof BasePowerUpItem powerUpItem && ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get()) {
                EntityType<?> entityType = powerUpItem.getType(stack);

                if (!entityType.is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        Entity entity = entityType.spawn((ServerLevel) world, stack, null, pos.above(1), MobSpawnType.SPAWN_EGG, true, false);
                        if (entity != null && applyUpMotion) {
                            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.3, 0));
                            entity.move(MoverType.SELF, entity.getDeltaMovement());
                        }
                    } else {
                        Entity entity = entityType.create(serverWorld);
                        if (entity != null)
                            entityType.spawn(serverWorld, stack, null,
                                    BlockPos.containing(pos.getX(), pos.getY() - entity.getBbHeight(), pos.getZ()),
                                    MobSpawnType.SPAWN_EGG, true, false);
                    }
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof SpawnEggItem spawnEgg && ConfigRegistry.QUESTION_SPAWNS_MOBS.get()
                    && !(stack.getItem() instanceof BasePowerUpItem)) {
                EntityType<?> entityType = spawnEgg.getType(stack);

                if (!entityType.is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        Entity entity = entityType.spawn((ServerLevel) world, stack, null, pos.above(1), MobSpawnType.SPAWN_EGG, true, false);
                        if (entity != null && applyUpMotion) {
                            entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.3, 0));
                            entity.move(MoverType.SELF, entity.getDeltaMovement());
                        }
                    } else {
                        Entity entity = entityType.create(serverWorld);
                        if (entity != null)
                            entityType.spawn(serverWorld, stack, null,
                                    BlockPos.containing(pos.getX(), pos.getY() - entity.getBbHeight(), pos.getZ()),
                                    MobSpawnType.SPAWN_EGG, true, false);
                    }
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof ArmorStandItem) {
                Consumer<ArmorStand> consumer = EntityType.createDefaultStackConfig(serverWorld, stack, null);
                ArmorStand armorStand = EntityType.ARMOR_STAND.create(serverWorld, consumer, pos, MobSpawnType.SPAWN_EGG, true, true);

                if (armorStand != null && !armorStand.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        armorStand.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else armorStand.setPos(pos.getX() + 0.5D, pos.getY() - armorStand.getType().getHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(armorStand);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof MinecartItem cart) {
                AbstractMinecart abstractMinecart =
                        AbstractMinecart.createMinecart(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, cart.type, stack, null);

                if (!abstractMinecart.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY() - abstractMinecart.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(abstractMinecart);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BoatItem boatItem) {
                Boat boat = boatItem.hasChest ? new ChestBoat(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D)
                        : new Boat(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);

                if (!boat.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        boat.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else boat.setPos(pos.getX() + 0.5D, pos.getY() - boat.getBbHeight(), pos.getZ() + 0.5D);
                    boat.setVariant(boatItem.type);
                    world.addFreshEntity(boat);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock) {
                PrimedTnt primedtnt = new PrimedTnt(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);

                if (!primedtnt.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        primedtnt.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else primedtnt.setPos(pos.getX() + 0.5D, pos.getY() - primedtnt.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(primedtnt);
                    stack.copyWithCount(1);
                    serverWorld.gameEvent(null, GameEvent.PRIME_FUSE, pos);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock
                    && entityHitBlock instanceof Player player) {
                player.addItem(stack.copyWithCount(1));
            } else if (stack.getItem() instanceof WindChargeItem) {
                WindCharge windCharge = new WindCharge(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new Vec3(0, -0.5, 0));

                if (!windCharge.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        windCharge.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else windCharge.setPos(pos.getX() + 0.5D, pos.getY() - windCharge.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(windCharge);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof FireChargeItem) {
                SmallFireball fireball = new SmallFireball(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new Vec3(0, -0.5, 0));

                if (!fireball.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        fireball.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else fireball.setPos(pos.getX() + 0.5D, pos.getY() - fireball.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(fireball);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof ThrowablePotionItem) {
                ThrownPotion potion = new ThrownPotion(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!potion.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        potion.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else potion.setPos(pos.getX() + 0.5D, pos.getY() - potion.getBbHeight(), pos.getZ() + 0.5D);
                    potion.setItem(stack);
                    world.addFreshEntity(potion);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof ExperienceBottleItem) {
                ThrownExperienceBottle xpBottle = new ThrownExperienceBottle(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!xpBottle.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        xpBottle.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else xpBottle.setPos(pos.getX() + 0.5D, pos.getY() - xpBottle.getBbHeight(), pos.getZ() + 0.5D);
                    xpBottle.setItem(stack);
                    world.addFreshEntity(xpBottle);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof EndCrystalItem) {
                EndCrystal endCrystal = new EndCrystal(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!endCrystal.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        endCrystal.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else {
                        endCrystal.setPos(pos.getX() + 0.5D, pos.getY() - endCrystal.getBbHeight(), pos.getZ() + 0.5D);
                        endCrystal.setDeltaMovement(new Vec3(0, -0.5, 0));
                    }
                    endCrystal.setShowBottom(false);
                    world.addFreshEntity(endCrystal);
                    world.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof FireworkRocketItem) {
                FireworkRocketEntity firework = new FireworkRocketEntity(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);

                if (!firework.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        firework.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else firework.setPos(pos.getX() + 0.5D, pos.getY() - firework.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(firework);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof EggItem) {
                ThrownEgg egg = new ThrownEgg(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!egg.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        egg.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else egg.setPos(pos.getX() + 0.5D, pos.getY() - egg.getBbHeight(), pos.getZ() + 0.5D);
                    egg.setItem(stack);
                    world.addFreshEntity(egg);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BucketItem bucket && bucket.content != Fluids.EMPTY
                    && ConfigRegistry.QUESTION_BUCKET_TWEAKS.get()) {
                if (world.getBlockState(pos.above()).isAir() || !world.getFluidState(pos.above()).isEmpty()
                        || (!world.getBlockState(pos.below()).isAir() && world.getFluidState(pos.below()).isEmpty())) {
                    if (bucket.emptyContents(null, world, pos.above(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.above());
                } else {
                    if (bucket.emptyContents(null, world, pos.below(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.below());
                }
                spawnItem(world, pos, new ItemStack(Items.BUCKET), dropItemsAtPos);
            } else if (stack.getItem() instanceof SolidBucketItem bucket && ConfigRegistry.QUESTION_BUCKET_TWEAKS.get()) {
                if (world.getBlockState(pos.above()).isAir() || !world.getFluidState(pos.above()).isEmpty()
                        || (!world.getBlockState(pos.below()).isAir() && world.getFluidState(pos.below()).isEmpty())) {
                    if (bucket.emptyContents(null, world, pos.above(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.above());
                } else {
                    if (bucket.emptyContents(null, world, pos.below(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.below());
                }
                spawnItem(world, pos, new ItemStack(Items.BUCKET), dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.HAT_STAND_ITEM.get()) {
                Entity entity = CompatRegistry.HAT_STAND.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get()) {
                Entity entity = CompatRegistry.CANNONBALL.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(
                                world.random.triangle(0.0, 0.3),
                                world.random.triangle(0.5, 0.3),
                                world.random.triangle(0.0, 0.3)));
                    } else {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                    }
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()) {
                Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(
                                world.random.triangle(0.0, 0.2),
                                world.random.triangle(0.5, 0.2),
                                world.random.triangle(0.0, 0.2)));
                    } else {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                    }
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()) {
                Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    CompoundTag nbt = new CompoundTag();
                    entity.save(nbt);
                    nbt.putInt("Type", 1);
                    entity.load(nbt);

                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(
                                world.random.triangle(0.0, 0.2),
                                world.random.triangle(0.5, 0.2),
                                world.random.triangle(0.0, 0.2)));
                    } else {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                    }
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get()) {
                Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    CompoundTag nbt = new CompoundTag();
                    entity.save(nbt);
                    nbt.putInt("Type", 2);
                    entity.load(nbt);

                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(
                                world.random.triangle(0.0, 0.2),
                                world.random.triangle(0.5, 0.2),
                                world.random.triangle(0.0, 0.2)));
                    } else {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                    }
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get()) {
                Creeper entity = EntityType.CREEPER.create(serverWorld);

                if (entity != null) {
                    CompoundTag nbt = new CompoundTag();
                    entity.save(nbt);
                    nbt.putBoolean("Party", true);
                    nbt.putInt("Fuse", 0);

                    entity.setNoAi(true);
                    entity.ignite();
                    entity.setInvisible(true);
                    entity.setSilent(true);
                    entity.load(nbt);

                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    else entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(entity);
                }
                world.gameEvent(null, GameEvent.EXPLODE, pos);
            } else if (stack.getItem() == CompatRegistry.ICE_BOMB_ITEM.get()) {
                Entity entity = CompatRegistry.ICE_BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(
                                world.random.triangle(0.0, 0.2),
                                world.random.triangle(0.5, 0.2),
                                world.random.triangle(0.0, 0.2)));
                    }
                    else entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else spawnItem(world, pos, stack, dropItemsAtPos);
            } else spawnItem(world, pos, stack, dropItemsAtPos);
        }
    }

    public void spawnItem(Level world, BlockPos pos, ItemStack stack, boolean dropItemsAtPos) {
        if (dropItemsAtPos) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, stack.copyWithCount(1));
            world.addFreshEntity(itemEntity);
        } else if (world.getBlockState(pos.above()).isAir() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                || (!world.getBlockState(pos.below()).isAir() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.copyWithCount(1));
            world.addFreshEntity(itemEntity);
        } else {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.copyWithCount(1));
            world.addFreshEntity(itemEntity);
        }
    }

    public void playSounds(Level world, BlockPos pos, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
            world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
            world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ArmorStandItem)
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BasePowerUpItem)
            world.playSound(null, pos, SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof BoatItem)
            world.playSound(null, pos, SoundEvents.BOAT_PADDLE_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof EggItem)
            world.playSound(null, pos, SoundEvents.EGG_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof ExperienceBottleItem)
            world.playSound(null, pos, SoundEvents.EXPERIENCE_BOTTLE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof FireChargeItem)
            world.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof LingeringPotionItem)
            world.playSound(null, pos, SoundEvents.LINGERING_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof MinecartItem)
            world.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof PotionItem)
            world.playSound(null, pos, SoundEvents.SPLASH_POTION_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof SpawnEggItem)
            world.playSound(null, pos, SoundRegistry.MOB_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() instanceof WindChargeItem)
            world.playSound(null, pos, SoundEvents.WIND_CHARGE_THROW, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()
                || stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()
                || stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get())
            world.playSound(null, pos, CompatRegistry.BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get())
            world.playSound(null, pos, CompatRegistry.CANNON_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get())
            world.playSound(null, pos, CompatRegistry.CONFETTI_POPPER_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.HAT_STAND_ITEM.get())
            world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
        else if (stack.getItem() == CompatRegistry.ICE_BOMB_ITEM.get())
            world.playSound(null, pos, CompatRegistry.ICE_BOMB_SOUND.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void unpackLootTable(Entity entity, QuestionBlockEntity questionBlockEntity) {
        if (entity instanceof Player player) {
            questionBlockEntity.unpackLootTable(player);
        }
        questionBlockEntity.processLootTable();
        questionBlockEntity.setChanged();
    }
}
