package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import com.wenxin2.marioverse.items.LargeSnowballItem;
import com.wenxin2.marioverse.items.PiranhaPlantPodItem;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.Projectile;
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
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestionBlock extends BaseEntityBlock {
    public static final MapCodec<QuestionBlock> CODEC = simpleCodec(QuestionBlock::new);
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    public QuestionBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(EMPTY, Boolean.TRUE));
    }

    @NotNull
    @Override
    protected MapCodec<? extends QuestionBlock> codec() {
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

    @NotNull
    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        boolean isOxidizing = WeatheringCopper.getNext(oldState.getBlock()).isPresent();
        boolean isScraping = WeatheringCopper.getPrevious(oldState.getBlock()).isPresent();
        boolean isUnwaxing = HoneycombItem.getWaxed(newState).isPresent() && HoneycombItem.getWaxed(oldState).isEmpty();

        if (!(newState.getBlock() instanceof WeatheringCopperQuestionBlock)
                && !(newState.getBlock() instanceof QuestionBlock)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof QuestionBlockEntity questionBlock)
                Containers.dropContents(world, pos, questionBlock);
        }

        if (!isOxidizing && !isScraping && !isUnwaxing || newState.canBeReplaced())
            super.onRemove(oldState, world, pos, newState, isMoving);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.defaultBlockState().setValue(EMPTY, Boolean.TRUE);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof QuestionBlockEntity questionBE && ConfigRegistry.REDSTONE_OPENS_QUESTION.get()) {
            boolean isPowered = world.hasNeighborSignal(pos);
            if (isPowered && !state.getValue(EMPTY) && !questionBE.isLastPowered()) {
                ItemStack storedItem = questionBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                    MarioverseSoundTypes.playSounds(world, pos, storedItem);
                    questionBE.splitTheItem(1);
                    questionBE.setChanged();
                }

                if (storedItem.isEmpty())
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);

                if (questionBE.getLootTable() != null)
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
            }
            questionBE.setLastPowered(isPowered);
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        BlockPos pos = hitResult.getBlockPos();

        if (level.getBlockEntity(pos) instanceof QuestionBlockEntity questionBlockEntity
            && projectile.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
            && projectile.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0)
            QuestionBlock.hitQuestionBlock(level, pos, projectile, questionBlockEntity);

        projectile.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 20);
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

        if (blockEntity instanceof QuestionBlockEntity questionBE && !heldItem.is(TagRegistry.CANNOT_PLACE_IN_QUESTION_BLOCKS)) {
            ItemStack blockStack = questionBE.getTheItem();

            if (world.isClientSide) {
                return ItemInteractionResult.CONSUME;
            } else {
                if (!heldItem.isEmpty()
                        && (ConfigRegistry.QUESTION_ADD_ITEMS.get() || player.isCreative())
                        && (blockStack.isEmpty() || ItemStack.isSameItemSameComponents(heldItem, blockStack)
                        && blockStack.getCount() < blockStack.getMaxStackSize())) {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    ItemStack itemstack = stack.consumeAndReturn(1, player);

                    float f;
                    if (questionBE.isEmpty()) {
                        questionBE.setTheItem(itemstack);
                        f = (float) itemstack.getCount() / (float) itemstack.getMaxStackSize();
                    } else {
                        blockStack.grow(1);
                        f = (float) blockStack.getCount() / (float) blockStack.getMaxStackSize();
                    }
                    world.playSound(null, pos, SoundEvents.DECORATED_POT_INSERT, SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * f);

                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                    questionBE.setChanged();
                    world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                    return ItemInteractionResult.SUCCESS;
                } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());

        if (world.getBlockEntity(pos) instanceof QuestionBlockEntity questionBE) {
            ItemStack blockStack = questionBE.getTheItem();

            if ((heldItem.isEmpty() || !ItemStack.isSameItemSameComponents(heldItem, blockStack))
                    && (ConfigRegistry.QUESTION_REMOVE_ITEMS.get() || player.isCreative())
                    && !state.getValue(EMPTY)) {
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                ItemStack storedItem = questionBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnFromQuestionBlock(world, pos, storedItem, null, Boolean.FALSE, Boolean.TRUE);

                    if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                        PiglinAi.angerNearbyPiglins(player, false);

                    MarioverseSoundTypes.playSounds(world, pos, storedItem);
                    questionBE.splitTheItem(1);
                    questionBE.setChanged();
                }

                if (storedItem.isEmpty())
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);

                return InteractionResult.SUCCESS;
            } else return InteractionResult.PASS;
        } else return InteractionResult.PASS;
    }

    public static void hitQuestionBlock(Level world, BlockPos pos, Entity entity, QuestionBlockEntity questionBlockEntity) {
        if (world.getBlockState(pos).getBlock() instanceof QuestionBlock questionBlock) {
            ItemStack storedItem = questionBlockEntity.getTheItem();

            if (!world.getBlockState(pos).getValue(QuestionBlock.EMPTY))
                QuestionBlock.hitEntityAbove(pos, world, entity);

            if (!storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                BlockState stateAbove = world.getBlockState(pos.above());
                ItemStack coinItem = new ItemStack(stateAbove.getBlock().asItem());
                if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                    StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
                else if (stateAbove.getBlock() instanceof CoinBlock)
                    CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);

                if (!world.isClientSide)
                    questionBlock.spawnFromQuestionBlock(world, pos, storedItem, entity, Boolean.FALSE, Boolean.TRUE);

                if (world.getBlockState(pos).is(BlockTags.GUARDED_BY_PIGLINS) && entity instanceof Player player)
                    PiglinAi.angerNearbyPiglins(player, false);

                if (world instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticlesOnBlockFace(ParticleTypes.CRIT, serverWorld, pos, Direction.DOWN,
                            UniformInt.of(3, 4), () -> ServerParticleUtils.getRandomSpeedRanges(world.getRandom()), 0.65D);

                entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 2);
                MarioverseSoundTypes.playSounds(world, pos, storedItem);
                questionBlockEntity.splitTheItem(1);
                questionBlockEntity.setChanged();
            }

            if (storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                BlockState currentState = world.getBlockState(pos);
                if (currentState.getBlock() instanceof QuestionBlock)
                    world.setBlock(pos, currentState.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }

            if (world.getBlockState(pos).getBlock() instanceof InvisibleQuestionBlock
                    && world.getBlockState(pos).getValue(InvisibleQuestionBlock.INVISIBLE)) {
                BlockState currentState = world.getBlockState(pos);
                world.setBlock(pos, currentState.setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.FALSE), 3);
                world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    public static void hitQuestionBlockFromSide(Level world, BlockPos posNorth, Entity entity, BlockPos posSouth, BlockPos posEast, BlockPos posWest) {
        if (world.getBlockEntity(posNorth) instanceof QuestionBlockEntity questionBlockEntity)
            QuestionBlock.hitQuestionBlock(world, posNorth, entity, questionBlockEntity);

        if (world.getBlockEntity(posSouth) instanceof QuestionBlockEntity questionBlockEntity)
            QuestionBlock.hitQuestionBlock(world, posSouth, entity, questionBlockEntity);

        if (world.getBlockEntity(posEast) instanceof QuestionBlockEntity questionBlockEntity)
            QuestionBlock.hitQuestionBlock(world, posEast, entity, questionBlockEntity);

        if (world.getBlockEntity(posWest) instanceof QuestionBlockEntity questionBlockEntity)
            QuestionBlock.hitQuestionBlock(world, posWest, entity, questionBlockEntity);
    }

    public static void hitEntityAbove(BlockPos pos, Level world, Entity attackingEntity) {
        AABB boundingBox = new AABB(pos.above()).inflate(0.01);
        List<Entity> entitiesAbove = world.getEntities(null, boundingBox);

        if (!entitiesAbove.isEmpty()) {
            for (Entity entityAbove : entitiesAbove) {
                if (entityAbove instanceof LivingEntity livingEntity && livingEntity.onGround()) {
                    entityAbove.setDeltaMovement(entityAbove.getDeltaMovement().add(0, 0.5, 0));
                    if (world.getBlockState(pos).getBlock() instanceof QuestionBlock) {
                        if (livingEntity instanceof KoopaShellEntity)
                            livingEntity.hurt(DamageSourceRegistry.bonked(livingEntity, attackingEntity), 0.0F);
                        else livingEntity.hurt(DamageSourceRegistry.bonked(livingEntity, attackingEntity), 4.0F);
                    } else {
                        if (livingEntity instanceof KoopaShellEntity)
                            livingEntity.hurt(DamageSourceRegistry.shrapnel(livingEntity, attackingEntity), 0.0F);
                        else livingEntity.hurt(DamageSourceRegistry.shrapnel(livingEntity, attackingEntity), 4.0F);
                    }
                }
            }
        }
    }

    public void spawnFromQuestionBlock(Level world, BlockPos pos, ItemStack stack, Entity entityHitBlock, boolean dropItemsAtPos, boolean applyUpMotion) {
        if (world instanceof ServerLevel serverWorld) {
            if (stack.getItem() instanceof BasePowerUpItem powerUpItem && ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get()) {
                EntityType<?> entityType = powerUpItem.getType(stack);

                if (!entityType.is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        Entity entity = entityType.spawn((ServerLevel) world, stack, null, pos.above(1), MobSpawnType.SPAWN_EGG, true, false);
                        if (entity != null && applyUpMotion) {
                            if (!entity.getType().is(TagRegistry.HAS_NO_DELTA_MOVEMENT))
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
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof PiranhaPlantPodItem pod && ConfigRegistry.QUESTION_SPAWNS_MOBS.get()) {
                EntityType<?> entityType = pod.getType(stack);

                if (!entityType.is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    Entity entity = entityType.spawn(serverWorld, stack, null, pos, MobSpawnType.SPAWN_EGG, true, false);

                    if (entity instanceof PiranhaPlantEntity piranhaPlant) {
                        piranhaPlant.setAge(-24000);
                        piranhaPlant.setOwner(entityHitBlock);
                    }

                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof SpawnEggItem spawnEgg && ConfigRegistry.QUESTION_SPAWNS_MOBS.get()
                    && !(stack.getItem() instanceof BasePowerUpItem)) {
                EntityType<?> entityType = spawnEgg.getType(stack);

                if (!entityType.is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        Entity entity = entityType.spawn((ServerLevel) world, stack, null, pos.above(1), MobSpawnType.SPAWN_EGG, true, false);
                        if (entity != null && applyUpMotion) {
                            if (!entity.getType().is(TagRegistry.HAS_NO_DELTA_MOVEMENT))
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
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof LargeSnowballItem) {
                LargeSnowballProjectile snowball = new LargeSnowballProjectile(serverWorld, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);

                if (!snowball.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        snowball.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else snowball.setPos(pos.getX() + 0.5D, pos.getY() - snowball.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(snowball);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof ArmorStandItem) {
                Consumer<ArmorStand> consumer = EntityType.createDefaultStackConfig(serverWorld, stack, null);
                ArmorStand armorStand = EntityType.ARMOR_STAND.create(serverWorld, consumer, pos, MobSpawnType.SPAWN_EGG, true, true);

                if (armorStand != null && !armorStand.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        armorStand.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else armorStand.setPos(pos.getX() + 0.5D, pos.getY() - armorStand.getType().getHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(armorStand);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof MinecartItem cart) {
                AbstractMinecart abstractMinecart =
                        AbstractMinecart.createMinecart(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, cart.type, stack, null);

                if (!abstractMinecart.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY() - abstractMinecart.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(abstractMinecart);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BoatItem boatItem) {
                Boat boat = boatItem.hasChest ? new ChestBoat(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D)
                        : new Boat(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);

                if (!boat.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        boat.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else boat.setPos(pos.getX() + 0.5D, pos.getY() - boat.getBbHeight(), pos.getZ() + 0.5D);
                    boat.setVariant(boatItem.type);
                    world.addFreshEntity(boat);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock) {
                PrimedTnt primedtnt = new PrimedTnt(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);

                if (!primedtnt.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        primedtnt.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else primedtnt.setPos(pos.getX() + 0.5D, pos.getY() - primedtnt.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(primedtnt);
                    stack.copyWithCount(1);
                    serverWorld.gameEvent(null, GameEvent.PRIME_FUSE, pos);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock
                    && entityHitBlock instanceof Player player) {
                boolean itemAdded = player.addItem(stack.copyWithCount(1));

                if (blockItem.getBlock() instanceof StarCoinBlock)
                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, pos, UniformInt.of(4, 6));
                else ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, pos, UniformInt.of(2, 3));

                if (!itemAdded)
                    player.drop(stack.copyWithCount(1), false);

            } else if (stack.getItem() instanceof WindChargeItem) {
                WindCharge windCharge = new WindCharge(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new Vec3(0, -0.5, 0));

                if (!windCharge.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        windCharge.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else windCharge.setPos(pos.getX() + 0.5D, pos.getY() - windCharge.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(windCharge);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof FireChargeItem) {
                SmallFireball fireball = new SmallFireball(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                        new Vec3(0, -0.5, 0));

                if (!fireball.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        fireball.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else fireball.setPos(pos.getX() + 0.5D, pos.getY() - fireball.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(fireball);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof ThrowablePotionItem) {
                ThrownPotion potion = new ThrownPotion(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!potion.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        potion.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else potion.setPos(pos.getX() + 0.5D, pos.getY() - potion.getBbHeight(), pos.getZ() + 0.5D);
                    potion.setItem(stack);
                    world.addFreshEntity(potion);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof ExperienceBottleItem) {
                ThrownExperienceBottle xpBottle = new ThrownExperienceBottle(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!xpBottle.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        xpBottle.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else xpBottle.setPos(pos.getX() + 0.5D, pos.getY() - xpBottle.getBbHeight(), pos.getZ() + 0.5D);
                    xpBottle.setItem(stack);
                    world.addFreshEntity(xpBottle);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof EndCrystalItem) {
                EndCrystal endCrystal = new EndCrystal(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!endCrystal.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        endCrystal.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else {
                        endCrystal.setPos(pos.getX() + 0.5D, pos.getY() - endCrystal.getBbHeight(), pos.getZ() + 0.5D);
                        endCrystal.setDeltaMovement(new Vec3(0, -0.5, 0));
                    }
                    endCrystal.setShowBottom(false);
                    world.addFreshEntity(endCrystal);
                    world.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof FireworkRocketItem) {
                FireworkRocketEntity firework = new FireworkRocketEntity(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);

                if (!firework.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        firework.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else firework.setPos(pos.getX() + 0.5D, pos.getY() - firework.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(firework);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof EggItem) {
                ThrownEgg egg = new ThrownEgg(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

                if (!egg.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        egg.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else egg.setPos(pos.getX() + 0.5D, pos.getY() - egg.getBbHeight(), pos.getZ() + 0.5D);
                    egg.setItem(stack);
                    world.addFreshEntity(egg);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() instanceof BucketItem bucket && bucket.content != Fluids.EMPTY
                    && ConfigRegistry.QUESTION_BUCKET_TWEAKS.get()) {
                if (world.getBlockState(pos.above()).canBeReplaced()) {
                    if (bucket.emptyContents(null, world, pos.above(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.above());
                    this.spawnItem(world, pos.above(), new ItemStack(Items.BUCKET), dropItemsAtPos);
                } else if (world.getBlockState(pos.below()).canBeReplaced()) {
                    if (bucket.emptyContents(null, world, pos.below(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.below());
                    this.spawnItem(world, pos.below(), new ItemStack(Items.BUCKET), dropItemsAtPos);
                } else if (!world.getBlockState(pos.below()).canBeReplaced())
                    this.spawnItem(world, pos.below(), stack, dropItemsAtPos);

            } else if (stack.getItem() instanceof SolidBucketItem bucket && ConfigRegistry.QUESTION_BUCKET_TWEAKS.get()) {
                if (world.getBlockState(pos.above()).canBeReplaced()) {
                    if (bucket.emptyContents(null, world, pos.above(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.above());
                    this.spawnItem(world, pos.above(), new ItemStack(Items.BUCKET), dropItemsAtPos);
                } else if (world.getBlockState(pos.below()).canBeReplaced()) {
                    if (bucket.emptyContents(null, world, pos.below(), null, stack))
                        bucket.checkExtraContent(null, world, stack, pos.below());
                    this.spawnItem(world, pos.below(), new ItemStack(Items.BUCKET), dropItemsAtPos);
                } else this.spawnItem(world, pos.below(), stack, dropItemsAtPos);

            } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CheckpointFlagBlock block) {
                int randomRotation = world.random.nextInt(17);

                if (world.getBlockState(pos.above()).canBeReplaced()
                        && world.getBlockState(pos.above(2)).canBeReplaced()
                        && world.getBlockState(pos.above(3)).canBeReplaced()) {

                    world.setBlock(pos.above(), block.defaultBlockState().setValue(CheckpointFlagBlock.ROTATION, randomRotation)
                            .setValue(CheckpointFlagBlock.WATERLOGGED, world.getFluidState(pos.above()).is(FluidTags.WATER)), 3);
                    world.setBlock(pos.above(2), block.defaultBlockState().setValue(CheckpointFlagBlock.PART, TripleBlockStates.MIDDLE)
                            .setValue(CheckpointFlagBlock.WATERLOGGED, world.getFluidState(pos.above(2)).is(FluidTags.WATER)), 3);
                    world.setBlock(pos.above(3), block.defaultBlockState().setValue(CheckpointFlagBlock.PART, TripleBlockStates.TOP)
                            .setValue(CheckpointFlagBlock.WATERLOGGED, world.getFluidState(pos.above(3)).is(FluidTags.WATER)), 3);

                    CheckpointFlagBlockEntity flagBE = (CheckpointFlagBlockEntity) world.getBlockEntity(pos.above());
                    checkpointFlagNBT(world, pos.above(), stack, Direction.UP, flagBE, entityHitBlock);

                } else if (world.getBlockState(pos.below()).canBeReplaced()
                        && world.getBlockState(pos.below(2)).canBeReplaced()
                        && world.getBlockState(pos.below(3)).canBeReplaced()) {

                    world.setBlock(pos.below(3), block.defaultBlockState().setValue(CheckpointFlagBlock.ROTATION, randomRotation)
                            .setValue(CheckpointFlagBlock.WATERLOGGED, world.getFluidState(pos.below(3)).is(FluidTags.WATER)), 3);
                    world.setBlock(pos.below(2), block.defaultBlockState().setValue(CheckpointFlagBlock.PART, TripleBlockStates.MIDDLE)
                            .setValue(CheckpointFlagBlock.WATERLOGGED, world.getFluidState(pos.below(2)).is(FluidTags.WATER)), 3);
                    world.setBlock(pos.below(), block.defaultBlockState().setValue(CheckpointFlagBlock.PART, TripleBlockStates.TOP)
                            .setValue(CheckpointFlagBlock.WATERLOGGED, world.getFluidState(pos.below()).is(FluidTags.WATER)), 3);

                    CheckpointFlagBlockEntity flagBE = (CheckpointFlagBlockEntity) world.getBlockEntity(pos.below());
                    checkpointFlagNBT(world, pos.below(), stack, Direction.DOWN, flagBE, entityHitBlock);

                } else this.spawnItem(world, pos, stack, dropItemsAtPos);

            } else if (stack.getItem() == CompatRegistry.HAT_STAND_ITEM.get()) {
                Entity entity = CompatRegistry.HAT_STAND.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    else entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get()) {
                Entity entity = CompatRegistry.CANNONBALL.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
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
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()) {
                Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
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
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()) {
                Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    CompoundTag nbt = new CompoundTag();
                    entity.save(nbt);
                    nbt.putInt("Type", 1);
                    entity.load(nbt);

                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
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
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else if (stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get()) {
                Entity entity = CompatRegistry.BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    CompoundTag nbt = new CompoundTag();
                    entity.save(nbt);
                    nbt.putInt("Type", 2);
                    entity.load(nbt);

                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
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
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
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

                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER)))
                        entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                    else entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(entity);
                }
                world.gameEvent(null, GameEvent.EXPLODE, pos);
            } else if (stack.getItem() == CompatRegistry.ICE_BOMB_ITEM.get()) {
                Entity entity = CompatRegistry.ICE_BOMB.get().create(serverWorld);

                if (entity != null && !entity.getType().is(TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN)) {
                    if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
                        entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                        entity.setDeltaMovement(new Vec3(
                                world.random.triangle(0.0, 0.2),
                                world.random.triangle(0.5, 0.2),
                                world.random.triangle(0.0, 0.2)));
                    }
                    else entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    world.addFreshEntity(entity);
                    stack.copyWithCount(1);
                } else this.spawnItem(world, pos, stack, dropItemsAtPos);
            } else this.spawnItem(world, pos, stack, dropItemsAtPos);
        }
    }

    private static void checkpointFlagNBT(Level world, BlockPos pos, ItemStack stack, Direction direction, CheckpointFlagBlockEntity flagBE, Entity entityHitBlock) {
        if (stack.has(DataComponents.CUSTOM_NAME)) {
            flagBE.setCustomName(stack.getHoverName());
            flagBE.markUpdated();

            if (flagBE.isWonderFlag()) {
                flagBE.setWonderFlag(Boolean.TRUE);
                if (!world.isClientSide())
                    PacketDistributor.sendToAllPlayers(new WonderNamePayload(pos, flagBE.hasWonderFlag()));
            } else if (flagBE.isAmericanFlag()) {
                flagBE.setAmericanFlag(Boolean.TRUE);
                if (!world.isClientSide())
                    PacketDistributor.sendToAllPlayers(new AmericaNamePayload(pos, flagBE.hasAmericanFlag()));
            }

            BlockEntity middleBlockEntity = world.getBlockEntity(pos.relative(direction));
            if (middleBlockEntity instanceof CheckpointFlagBlockEntity middleFlagBE) {
                if (middleFlagBE.getCustomName() == null) {
                    middleFlagBE.setCustomName(stack.getHoverName());
                    middleFlagBE.markUpdated();

                    if (middleFlagBE.isWonderFlag()) {
                        middleFlagBE.setWonderFlag(Boolean.TRUE);
                        if (!world.isClientSide())
                            PacketDistributor.sendToAllPlayers(new WonderNamePayload(pos.relative(direction), middleFlagBE.hasWonderFlag()));
                    } else if (middleFlagBE.isAmericanFlag()) {
                        middleFlagBE.setAmericanFlag(Boolean.TRUE);
                        if (!world.isClientSide())
                            PacketDistributor.sendToAllPlayers(new AmericaNamePayload(pos.relative(direction), middleFlagBE.hasAmericanFlag()));
                    }
                }
            }

            BlockEntity topBlockEntity = world.getBlockEntity(pos.relative(direction, 2));
            if (topBlockEntity instanceof CheckpointFlagBlockEntity topFlagBE) {
                if (topFlagBE.getCustomName() == null) {
                    topFlagBE.setCustomName(stack.getHoverName());
                    topFlagBE.markUpdated();

                    if (topFlagBE.isWonderFlag()) {
                        topFlagBE.setWonderFlag(Boolean.TRUE);
                        if (!world.isClientSide())
                            PacketDistributor.sendToAllPlayers(new WonderNamePayload(pos.relative(direction, 2), topFlagBE.hasWonderFlag()));
                    } else if (topFlagBE.isAmericanFlag()) {
                        topFlagBE.setAmericanFlag(Boolean.TRUE);
                        if (!world.isClientSide())
                            PacketDistributor.sendToAllPlayers(new AmericaNamePayload(pos.relative(direction, 2), topFlagBE.hasAmericanFlag()));
                    }
                }
            }
        }

        if (stack.has(DataComponents.CONTAINER_LOOT)) {
            SeededContainerLoot lootTableReference = stack.get(DataComponents.CONTAINER_LOOT);

            if (lootTableReference != null) {
                flagBE.setLootTable(lootTableReference.lootTable(), lootTableReference.seed());
                flagBE.markUpdated();

                BlockEntity middleBlockEntity = world.getBlockEntity(pos.relative(direction));
                if (middleBlockEntity instanceof CheckpointFlagBlockEntity middleFlagBE) {
                    middleFlagBE.setLootTable(lootTableReference.lootTable(), lootTableReference.seed());
                    middleFlagBE.markUpdated();
                }

                BlockEntity topBlockEntity = world.getBlockEntity(pos.relative(direction, 2));
                if (topBlockEntity instanceof CheckpointFlagBlockEntity topFlagBE) {
                    topFlagBE.setLootTable(lootTableReference.lootTable(), lootTableReference.seed());
                    topFlagBE.markUpdated();
                }
            }
        }

        if (stack.has(DataComponents.CONTAINER)) {
            ItemContainerContents containerContents = stack.get(DataComponents.CONTAINER);

            if (containerContents != null) {
                flagBE.setTheItem(containerContents.copyOne());
                flagBE.markUpdated();

                BlockEntity middleBlockEntity = world.getBlockEntity(pos.relative(direction));
                if (middleBlockEntity instanceof CheckpointFlagBlockEntity middleFlagBE) {
                    middleFlagBE.setTheItem(containerContents.copyOne());
                    middleFlagBE.markUpdated();
                }

                BlockEntity topBlockEntity = world.getBlockEntity(pos.relative(direction, 2));
                if (topBlockEntity instanceof CheckpointFlagBlockEntity topFlagBE) {
                    topFlagBE.setTheItem(containerContents.copyOne());
                    topFlagBE.markUpdated();
                }
            }
        }
    }

    public void spawnItem(Level world, BlockPos pos, ItemStack stack, boolean dropItemsAtPos) {
        if (dropItemsAtPos) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, stack.copyWithCount(1));
            world.addFreshEntity(itemEntity);
        } else if (world.getBlockState(pos.above()).canBeReplaced() || world.getFluidState(pos.above()).is(FluidTags.WATER)
                || (!world.getBlockState(pos.below()).canBeReplaced() && !world.getFluidState(pos.below()).is(FluidTags.WATER))) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.copyWithCount(1));
            world.addFreshEntity(itemEntity);
        } else {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.copyWithCount(1));
            world.addFreshEntity(itemEntity);
        }
    }
}
