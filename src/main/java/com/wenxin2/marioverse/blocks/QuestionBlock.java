package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ArmorStandItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.SpawnEggItem;
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
import net.minecraft.world.phys.BlockHitResult;
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
                        this.spawnEntity(world, pos, storedItem);

                    if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                        this.playCoinSound(world, pos);
                    else if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
                        this.playPrimedTNTSound(world, pos);
                    else if (storedItem.getItem() instanceof BasePowerUpItem)
                        this.playPowerUpSound(world, pos);
                    else if (storedItem.getItem() instanceof SpawnEggItem)
                        this.playMobSound(world, pos);
                    else if (storedItem.getItem() instanceof ArmorStandItem)
                        this.playArmorStandSound(world, pos);
                    else if (storedItem.getItem() instanceof BoatItem)
                        this.playBoatSound(world, pos);
                    else if (storedItem.getItem() instanceof MinecartItem)
                        this.playMinecartSound(world, pos);
                    else this.playItemSound(world, pos);

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
                            this.spawnEntity(world, pos, storedItem);

                        if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                            this.playCoinSound(world, pos);
                        else if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock)
                            this.playPrimedTNTSound(world, pos);
                        else if (storedItem.getItem() instanceof BasePowerUpItem)
                            this.playPowerUpSound(world, pos);
                        else if (storedItem.getItem() instanceof SpawnEggItem)
                            this.playMobSound(world, pos);
                        else if (storedItem.getItem() instanceof ArmorStandItem)
                            this.playArmorStandSound(world, pos);
                        else if (storedItem.getItem() instanceof BoatItem)
                            this.playBoatSound(world, pos);
                        else if (storedItem.getItem() instanceof MinecartItem)
                            this.playMinecartSound(world, pos);
                        else this.playItemSound(world, pos);

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

    public void spawnEntity(Level world, BlockPos pos, ItemStack stack) {
        if (stack.getItem() instanceof BasePowerUpItem powerUpItem && ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get()) {
            EntityType<?> entityType = powerUpItem.getType(stack);

            if (world instanceof ServerLevel serverWorld && !entityType.is(TagRegistry.QUESTION_BLOCK_ENTITY_BLACKLIST)) {
                if (world.getBlockState(pos.above()).isAir()) {
                    Entity entity = entityType.spawn((ServerLevel) world, stack, null, pos.above(1), MobSpawnType.SPAWN_EGG, true, false);
                    if (entity != null) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.3, 0));
                        entity.move(MoverType.SELF, entity.getDeltaMovement());
                    }
                }
                else entityType.spawn(serverWorld, stack, null, pos.below((int) Math.max(1, entityType.getHeight())), MobSpawnType.SPAWN_EGG, true, true);
                stack.copyWithCount(1);
            } else if (world.getBlockState(pos.above()).isAir()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            } else {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            }
        } else if (stack.getItem() instanceof SpawnEggItem spawnEgg && ConfigRegistry.QUESTION_SPAWNS_MOBS.get()) {
            EntityType<?> entityType = spawnEgg.getType(stack);

            if (world instanceof ServerLevel serverWorld && !entityType.is(TagRegistry.QUESTION_BLOCK_ENTITY_BLACKLIST)) {
                if (world.getBlockState(pos.above()).isAir()) {
                    Entity entity = entityType.spawn((ServerLevel) world, stack, null, pos.above(1), MobSpawnType.SPAWN_EGG, true, false);
                    if (entity != null) {
                        entity.setDeltaMovement(entity.getDeltaMovement().add(0, 0.3, 0));
                        entity.move(MoverType.SELF, entity.getDeltaMovement());
                    }
                }
                else entityType.spawn(serverWorld, stack, null, pos.below((int) Math.max(1, entityType.getHeight())), MobSpawnType.SPAWN_EGG, true, true);
                stack.copyWithCount(1);
            } else if (world.getBlockState(pos.above()).isAir()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            } else {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            }
        } else if (stack.getItem() instanceof ArmorStandItem && world instanceof ServerLevel serverWorld) {
            Consumer<ArmorStand> consumer = EntityType.createDefaultStackConfig(serverWorld, stack, null);
            ArmorStand armorStand = EntityType.ARMOR_STAND.create(serverWorld, consumer, pos, MobSpawnType.SPAWN_EGG, true, true);
            if (armorStand != null) {
                if (world.getBlockState(pos.above()).isAir())
                    armorStand.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                else armorStand.setPos(pos.getX() + 0.5D, pos.below((int) Math.max(2, armorStand.getType().getHeight())).getY(), pos.getZ() + 0.5D);
                world.addFreshEntity(armorStand);
                stack.copyWithCount(1);
            }
        } else if (stack.getItem() instanceof MinecartItem cart && world instanceof ServerLevel serverWorld) {
            AbstractMinecart abstractMinecart =
                    AbstractMinecart.createMinecart(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, cart.type, stack, null);
            if (world.getBlockState(pos.above()).isAir())
                abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            else abstractMinecart.setPos(pos.getX() + 0.5D, pos.below((int) Math.max(1, abstractMinecart.getBbHeight())).getY(), pos.getZ() + 0.5D);
            world.addFreshEntity(abstractMinecart);
            stack.copyWithCount(1);
        } else if (stack.getItem() instanceof BoatItem boatItem && world instanceof ServerLevel serverWorld) {
            Boat boat = boatItem.hasChest ? new ChestBoat(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D)
                    : new Boat(serverWorld, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            if (world.getBlockState(pos.above()).isAir())
                boat.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            else boat.setPos(pos.getX() + 0.5D, pos.below((int) Math.max(1, boat.getBbHeight())).getY(), pos.getZ() + 0.5D);
            boat.setVariant(boatItem.type);
            world.addFreshEntity(boat);
            stack.copyWithCount(1);
        } else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock && world instanceof ServerLevel serverWorld) {
            PrimedTnt primedtnt = new PrimedTnt(serverWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
            if (world.getBlockState(pos.above()).isAir())
                primedtnt.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            else primedtnt.setPos(pos.getX() + 0.5D, pos.below((int) Math.max(1, primedtnt.getBbHeight())).getY(), pos.getZ() + 0.5D);
            world.addFreshEntity(primedtnt);
            stack.copyWithCount(1);
            serverWorld.gameEvent(null, GameEvent.PRIME_FUSE, pos);
        } else {
            if (world.getBlockState(pos.above()).isAir()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            } else {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            }
        }
    }

    public void playArmorStandSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playBoatSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.BOAT_PADDLE_WATER, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playCoinSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playItemSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playMobSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.MOB_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playMinecartSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.MINECART_RIDING, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playPowerUpSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playPrimedTNTSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void unpackLootTable(Entity entity, QuestionBlockEntity questionBlockEntity) {
        if (entity instanceof Player player) {
            questionBlockEntity.unpackLootTable(player);
        }
        questionBlockEntity.processLootTable();
        questionBlockEntity.setChanged();
    }
}
