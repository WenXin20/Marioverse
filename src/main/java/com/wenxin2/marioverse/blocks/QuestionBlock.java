package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuestionBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(EMPTY);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.defaultBlockState().setValue(EMPTY, Boolean.TRUE);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos posNeighbor) {
        QuestionBlockEntity questionBlockEntity = (QuestionBlockEntity) worldAccessor.getBlockEntity(pos);

        if (questionBlockEntity != null && questionBlockEntity.hasItems()) {
            return state.setValue(EMPTY, Boolean.FALSE);
        }
        else return state.setValue(EMPTY, Boolean.TRUE);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, 
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
            ItemStack heldItem = player.getItemInHand(hand);
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof QuestionBlockEntity questionBlockEntity) {
                ItemStack blockStack = questionBlockEntity.getStackInSlot();
                if (!heldItem.isEmpty() && (ConfigRegistry.QUESTION_ADD_ITEMS.get() || player.isCreative())
                        && (blockStack.isEmpty() || ItemStack.isSameItemSameComponents(heldItem, blockStack))) {
                    world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                    questionBlockEntity.addItem(heldItem);
                    questionBlockEntity.setChanged();
                    if(!player.isCreative())
                        stack.shrink(heldItem.getCount());
                    return ItemInteractionResult.SUCCESS;
                } else if (heldItem.isEmpty() && (ConfigRegistry.QUESTION_REMOVE_ITEMS.get() || player.isCreative())
                        && !state.getValue(EMPTY)) {
                    ItemStack droppedItem = questionBlockEntity.getItems().getStackInSlot(0);

                    if (!droppedItem.isEmpty()) {
                        if (!world.isClientSide)
                            this.spawnEntity(world, player, pos, droppedItem, false);


                        this.playSoundEffect(world, player, pos, droppedItem);
                        questionBlockEntity.removeItems();
                        questionBlockEntity.setChanged();
                        world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    if (droppedItem.isEmpty()) {
                        world.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                    }
                    return ItemInteractionResult.SUCCESS;
                } else return ItemInteractionResult.CONSUME;
            }
        return ItemInteractionResult.CONSUME;
    }

    public void spawnEntity(Level world, Entity entity, BlockPos pos, ItemStack stack, Boolean dropEntireStack) {
        if (stack.getItem() instanceof SpawnEggItem spawnEgg && ConfigRegistry.QUESTION_SPAWNS_MOBS.get()) {
            EntityType<?> entityType = spawnEgg.getType(stack);

            if (world instanceof ServerLevel serverWorld && !entityType.is(TagRegistry.QUESTION_BLOCK_BLACKLIST)) { // Check this
                if (world.getBlockState(pos.above()).isAir())
                    entityType.spawn(serverWorld, stack, null, pos.above(2), MobSpawnType.SPAWN_EGG, true, true);
                else entityType.spawn(serverWorld, stack, null, pos.below((int) entityType.getHeight()), MobSpawnType.SPAWN_EGG, true, true);
                stack.split(1);
            } else if (world.getBlockState(pos.above()).isAir()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.split(1));
                world.addFreshEntity(itemEntity);
            } else {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.split(1));
                world.addFreshEntity(itemEntity);
            }
        } else if (stack.getItem() == Items.ARMOR_STAND) {
            ArmorStand armorStand = new ArmorStand(EntityType.ARMOR_STAND, world);
            armorStand.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
            world.addFreshEntity(armorStand);
            stack.split(1);
        } else {
            if (world.getBlockState(pos.above()).isAir()) {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, stack.split(1));
                world.addFreshEntity(itemEntity);
            } else {
                ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY() - 0.5D, pos.getZ() + 0.5D, stack.split(1));
                world.addFreshEntity(itemEntity);
            }
        }
    }

    public void playSoundEffect(Level world, Entity entity, BlockPos pos, ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock)
            world.playSound(entity, pos, SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        else {
            world.playSound(entity, pos, SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }
}
