package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestionBlock extends Block implements EntityBlock {
    public static final BooleanProperty EMPTY = BooleanProperty.create("empty");

    public QuestionBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(EMPTY, Boolean.TRUE));
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
                Player nearestPlayer = world.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 16.0D, false);

                if (questionBlockEntity.getLootTable() != null)
                    this.unpackLootTable(nearestPlayer, questionBlockEntity);

                if (!storedItem.isEmpty()) {
                    if (!world.isClientSide)
                        this.spawnEntity(world, pos, storedItem);

                    if (storedItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
                        this.playCoinSound(world, pos);
                    else if (storedItem.getItem() instanceof SpawnEggItem)
                        this.playMobSound(world, pos);
                    else this.playItemSound(world, pos);

                    questionBlockEntity.removeItems();
                    questionBlockEntity.setChanged();
                }

                if (storedItem.isEmpty()) {
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
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
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                    questionBlockEntity.addItem(heldItem);
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
                        else if (storedItem.getItem() instanceof SpawnEggItem)
                            this.playMobSound(world, pos);
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
        if (stack.getItem() instanceof SpawnEggItem spawnEgg && ConfigRegistry.QUESTION_SPAWNS_MOBS.get()) {
            EntityType<?> entityType = spawnEgg.getType(stack);

            if (world instanceof ServerLevel serverWorld && !entityType.is(TagRegistry.QUESTION_BLOCK_ENTITY_BLACKLIST)) {
                if (world.getBlockState(pos.above()).isAir())
                    entityType.spawn(serverWorld, stack, null, pos.above(2), MobSpawnType.SPAWN_EGG, true, true);
                else entityType.spawn(serverWorld, stack, null, pos.below(Math.round(entityType.getHeight())), MobSpawnType.SPAWN_EGG, true, true);
                stack.copyWithCount(1);
            } else if (world.getBlockState(pos.above()).isAir()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            } else {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.copyWithCount(1));
                world.addFreshEntity(itemEntity);
            }
        } else if (stack.getItem() == Items.ARMOR_STAND) {
            ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, world);
            armorStand.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            world.addFreshEntity(armorStand);
            stack.copyWithCount(1);
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

    public void playMobSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.MOB_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playItemSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playPowerUpSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void playCoinSound(Level world, BlockPos pos) {
        world.playSound(null, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void unpackLootTable(Entity entity, QuestionBlockEntity questionBlockEntity) {
        if (entity instanceof Player player) {
            questionBlockEntity.unpackLootTable(player);
            questionBlockEntity.processLootTable();
            questionBlockEntity.setChanged();
        }
    }
}
