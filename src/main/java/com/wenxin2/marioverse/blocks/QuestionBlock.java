package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.items.PlasticBucketItem;
import com.wenxin2.marioverse.items.PowerUpItem;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.PowerUpSpawnEggItem;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.WindCharge;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
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
        return new QuestionBlockEntity(BlockEntityRegistry.QUESTION_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.QUESTION_BLOCK_ENTITY.get(), QuestionBlockEntity::tick);
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
            if (blockEntity instanceof QuestionBlockEntity questionBE)
                Containers.dropContents(world, pos, questionBE);
        }

        if (!isOxidizing && !isScraping && !isUnwaxing || newState.canBeReplaced())
            super.onRemove(oldState, world, pos, newState, isMoving);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof QuestionBlockEntity questionBE)
            questionBE.setBreakingEntityUUID(player.getUUID());
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.defaultBlockState().setValue(EMPTY, Boolean.TRUE);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos neighborPos, boolean notify) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof QuestionBlockEntity questionBE && ConfigRegistry.REDSTONE_OPENS_QUESTION.get()) {
            boolean isPowered = level.hasNeighborSignal(pos);
            if (isPowered && !state.getValue(EMPTY) && !questionBE.isLastPowered()) {
                ItemStack storedItem = questionBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    BlockPos getBlockPos = QuestionBlock.getBlockPos(level, pos, storedItem);

                    MarioverseSoundTypes.playSounds(level, getBlockPos, storedItem, questionBE);
                    QuestionBlock.spawnFromContainer(level, getBlockPos, pos, storedItem, null,
                            ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                            ConfigRegistry.QUESTION_BUCKET_TWEAKS.get(), TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);

                    questionBE.splitTheItem(1);
                    questionBE.setChanged();
                }

                if (storedItem.isEmpty())
                    level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);

                if (questionBE.getLootTable() != null)
                    level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
            }
            questionBE.setLastPowered(isPowered);
        }
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        BlockPos pos = hitResult.getBlockPos();

        if (level.getBlockEntity(pos) instanceof QuestionBlockEntity questionBlockEntity
                && projectile.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && projectile.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0
                && !(projectile instanceof WindCharge))
            QuestionBlock.hitQuestionBlock(level, pos, projectile, questionBlockEntity);

        projectile.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 20);
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> consumer) {
        if (explosion.canTriggerBlocks() && level.getBlockEntity(pos) instanceof QuestionBlockEntity questionBE) {
            if (!state.getValue(EMPTY) ) {
                ItemStack storedItem = questionBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    BlockPos getBlockPos = QuestionBlock.getBlockPos(level, pos, storedItem);

                    MarioverseSoundTypes.playSounds(level, getBlockPos, storedItem, questionBE);
                    QuestionBlock.spawnFromContainer(level, getBlockPos, pos, storedItem, explosion.getDirectSourceEntity(),
                            ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                            ConfigRegistry.QUESTION_BUCKET_TWEAKS.get(), TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);

                    if (state.hasProperty(InvisibleQuestionBlock.INVISIBLE))
                        level.setBlock(pos, state.setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.FALSE), 3);

                    questionBE.splitTheItem(1);
                    questionBE.setChanged();
                }

                if (storedItem.isEmpty())
                    level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);

                if (questionBE.getLootTable() != null) {
                    if (state.hasProperty(InvisibleQuestionBlock.INVISIBLE))
                        level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE)
                                .setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.TRUE), 3);
                    else level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                }
            }
        }
        super.onExplosionHit(state, level, pos, explosion, consumer);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return state.getBlock() == BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK.get()
                || state.getBlock() == BlockRegistry.PRISMARINE_QUESTION_BRICKS.get();
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity blockEntity = level.getBlockEntity(pos);

        if (blockEntity instanceof QuestionBlockEntity questionBE && !heldItem.is(TagRegistry.CANNOT_PLACE_IN_QUESTION_BLOCKS)
                && (questionBE.getRefillCountdown() == -1 || player.isCreative())) {
            ItemStack blockStack = questionBE.getTheItem();

            if (level.isClientSide) {
                return ItemInteractionResult.CONSUME;
            } else {
                if (!heldItem.isEmpty()
                        && (ConfigRegistry.QUESTION_ADD_ITEMS.get() || player.isCreative())
                        && (blockStack.isEmpty() || ItemStack.isSameItemSameComponents(heldItem, blockStack)
                        && blockStack.getCount() < blockStack.getMaxStackSize())) {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    ItemStack itemstack = stack.consumeAndReturn(1, player);

                    float soundPitch;
                    if (questionBE.isEmpty()) {
                        questionBE.setTheItem(itemstack);
                        soundPitch = (float) itemstack.getCount() / (float) itemstack.getMaxStackSize();
                    } else {
                        blockStack.grow(1);
                        soundPitch = (float) blockStack.getCount() / (float) blockStack.getMaxStackSize();
                    }
                    level.playSound(null, pos, SoundRegistry.ITEM_INSERTED.get(),
                            SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * soundPitch);

                    level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
                    questionBE.setChanged();
                    level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                    return ItemInteractionResult.SUCCESS;
                } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
        } else return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());

        if (level.getBlockEntity(pos) instanceof QuestionBlockEntity questionBE) {
            ItemStack blockStack = questionBE.getTheItem();

            if ((heldItem.isEmpty() || !ItemStack.isSameItemSameComponents(heldItem, blockStack))
                    && (ConfigRegistry.QUESTION_REMOVE_ITEMS.get() || player.isCreative())
                    && !state.getValue(EMPTY)) {
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                ItemStack storedItem = questionBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    BlockPos getBlockPos = QuestionBlock.getBlockPos(level, pos, storedItem);

                    MarioverseSoundTypes.playSounds(level, getBlockPos, storedItem, questionBE);
                    QuestionBlock.spawnFromContainer(level, getBlockPos, pos, storedItem, player,
                            ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                            ConfigRegistry.QUESTION_BUCKET_TWEAKS.get(), TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);

                    if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                        PiglinAi.angerNearbyPiglins(player, false);

                    questionBE.splitTheItem(1);
                    questionBE.setChanged();
                }

                if (storedItem.isEmpty())
                    level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);

                return InteractionResult.SUCCESS;
            } else return InteractionResult.PASS;
        } else return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @javax.annotation.Nullable LivingEntity entity, ItemStack stack) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof QuestionBlockEntity questionBE) {
            if (stack.has(DataComponents.CUSTOM_NAME) || stack.has(DataComponentRegistry.PIPE_NAME)) {
                questionBE.setCustomName(stack.getHoverName());
                questionBE.setChanged();
            }
            questionBE.onLoad();
        }
    }

    public static void hitQuestionBlock(Level level, BlockPos pos, Entity entity, QuestionBlockEntity questionBE) {
        BlockState state = level.getBlockState(pos);

        if (ModList.get().isLoaded("sable") && SableProvider.getContext(level, entity) != null) {
            SableProvider.SableContext context = SableProvider.getContext(level, entity);
            state = context.accessor.getBlockState(pos);
            if (level instanceof ServerLevel)
                state = context.accessor.getServerBlockState(pos);
        }

        if (state.getBlock() instanceof QuestionBlock) {
            ItemStack storedItem = questionBE.getTheItem();

            if (!state.getValue(QuestionBlock.EMPTY))
                QuestionBlock.hitEntityAbove(pos, level, entity);

            if (!storedItem.isEmpty() && !state.getValue(QuestionBlock.EMPTY)) {
                BlockState stateAbove = level.getBlockState(pos.above());
                ItemStack coinItem = new ItemStack(stateAbove.getBlock().asItem());
                if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                    StarCoinBlock.collectCoin(starCoin, level, stateAbove, pos.above(), entity, coinItem);
                else if (stateAbove.getBlock() instanceof CoinBlock)
                    CoinBlock.collectCoin(level, stateAbove, pos.above(), entity, coinItem);

                BlockPos getBlockPos = QuestionBlock.getBlockPos(level, pos, storedItem);

                MarioverseSoundTypes.playSounds(level, getBlockPos, storedItem, questionBE);
                QuestionBlock.spawnFromContainer(level, getBlockPos, pos, storedItem, entity,
                        ConfigRegistry.QUESTION_SPAWNS_MOBS.get(), ConfigRegistry.QUESTION_SPAWNS_POWER_UPS.get(),
                        ConfigRegistry.QUESTION_BUCKET_TWEAKS.get(), TagRegistry.QUESTION_BLOCK_CANNOT_SPAWN);

                if (state.is(BlockTags.GUARDED_BY_PIGLINS) && entity instanceof Player player)
                    PiglinAi.angerNearbyPiglins(player, false);

                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticlesOnBlockFace(ParticleTypes.CRIT, serverWorld, pos, Direction.DOWN,
                            UniformInt.of(3, 4), () -> ServerParticleUtils.getRandomSpeedRanges(level.getRandom()), 0.65D);

                entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 2);
                questionBE.splitTheItem(1);
                questionBE.setChanged();
            }

            if (storedItem.isEmpty() && !state.getValue(QuestionBlock.EMPTY)) {
                if (state.getBlock() instanceof QuestionBlock)
                    level.setBlock(pos, state.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }

            if (state.getBlock() instanceof InvisibleQuestionBlock
                    && state.getValue(InvisibleQuestionBlock.INVISIBLE)) {
                level.setBlock(pos, state.setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.FALSE), 3);
                level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    public static void hitQuestionBlockFromSide(Level level, BlockPos pos, Entity entity) {
        if ((level.getBlockEntity(pos) instanceof QuestionBlockEntity questionBE))
            QuestionBlock.hitQuestionBlock(level, pos, entity, questionBE);
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

    public static void spawnItem(Level world, BlockPos pos, ItemStack stack, boolean dropItemsAtPos) {
        if (dropItemsAtPos) {
            ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, stack.copyWithCount(1));
            if (!itemEntity.getType().is(TagRegistry.HAS_NO_DELTA_MOVEMENT))
                itemEntity.setDeltaMovement(0.0D, 0.0D, 0.0D);
            itemEntity.move(MoverType.SELF, itemEntity.getDeltaMovement());
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

    public static boolean useItem(ServerLevel level, BlockPos pos, ItemStack stack, @Nullable Entity entityHitBlock) {
        ItemStack stackCopy = stack.copy();
        FakePlayer fakePlayer = FakePlayerFactory.getMinecraft(level);
        UseAnim anim = stackCopy.getUseAnimation();
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos).relative(Direction.UP, 0.5),
                Direction.UP, pos, false);
        fakePlayer.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 0.0F, 90.0F);
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stackCopy);
        if (entityHitBlock != null)
            fakePlayer.setData(DataAttachmentRegistry.OWNER_UUID, entityHitBlock.getUUID());

        if ((stackCopy.getItem() instanceof Equipable && !(stackCopy.getItem() instanceof PlasticBucketItem))
                || stackCopy.getItem() instanceof EnderpearlItem
                || stackCopy.get(DataComponents.FOOD) != null)
            return false;

        if (stackCopy.getItem() instanceof PotionItem && !(stackCopy.getItem() instanceof ThrowablePotionItem))
            return false;

        if (anim == UseAnim.BOW || anim == UseAnim.CROSSBOW || anim == UseAnim.SPEAR
                || anim == UseAnim.BLOCK || anim == UseAnim.SPYGLASS || anim == UseAnim.TOOT_HORN
                || anim == UseAnim.EAT)
            return false;

        InteractionResultHolder<ItemStack> holder;
        InteractionResult result;
        ItemStack resultStack;
        if (stackCopy.getItem() instanceof BlockItem blockItem) {
            Vec3 hitVec = Vec3.atCenterOf(pos).relative(Direction.UP, 0.5);
            UseOnContext useContext = new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND,
                    new BlockHitResult(hitVec, Direction.UP, pos, false));

            BlockPlaceContext placeContext = new BlockPlaceContext(useContext);
            result = blockItem.place(placeContext);
            BlockState state = level.getBlockState(pos);
            int randomRotation = level.random.nextInt(15);

            if (state.hasProperty(BlockStateProperties.ROTATION_16))
                level.setBlock(pos, state.setValue(BlockStateProperties.ROTATION_16, randomRotation), 3);

            if (state.getBlock() instanceof CoinBlock)
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), level, pos, UniformInt.of(2, 5));
            else level.levelEvent(2001, pos, Block.getId(state));
        } else {
            holder = stackCopy.use(level, fakePlayer, InteractionHand.MAIN_HAND);
            result = holder.getResult();
            resultStack = holder.getObject();

            QuestionBlock.dropResult(level, pos, stackCopy, resultStack, fakePlayer);
        }

        if (!result.consumesAction()) {
            fakePlayer.moveTo(pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D, 0.0F, 90.0F);
            result = stackCopy.useOn(new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND, hit));
            ItemStack heldStack = fakePlayer.getMainHandItem();

            QuestionBlock.dropResult(level, pos, stackCopy, heldStack, fakePlayer);
        }

        fakePlayer.removeData(DataAttachmentRegistry.OWNER_UUID);
        return result.consumesAction();
    }

    public static void spawnFromContainer(Level level, BlockPos pos, BlockPos particlePos, ItemStack stack, @Nullable Entity entityHitBlock, boolean spawnMobs,
                                          boolean spawnPowerUps, boolean canEmptyBuckets, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (stack.getItem() instanceof PowerUpItem) {
            QuestionBlock.spawnItem(level, pos, stack, true);
            return;
        }

        if (!spawnPowerUps && stack.getItem() instanceof PowerUpSpawnEggItem powerUpItem) {
            EntityType<?> entityType = powerUpItem.getType(stack);
            if (entityType.is(cannotSpawn))
                return;
            QuestionBlock.spawnItem(level, pos, stack, true);
            return;
        }

        if (!spawnMobs && stack.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getType(stack);
            if (entityType.is(cannotSpawn))
                return;
            QuestionBlock.spawnItem(level, pos, stack, true);
            return;
        }

        if (!canEmptyBuckets && (stack.getItem() instanceof BucketItem
                || stack.getItem() instanceof SolidBucketItem)) {
            QuestionBlock.spawnItem(level, pos, stack, true);
            return;
        }

        if (QuestionBlock.spawnTNT(level, pos, stack, cannotSpawn))
            return;
        if (QuestionBlock.spawnMinecart(level, pos, stack, cannotSpawn))
            return;
        if (QuestionBlock.spawnEndCrystal(level, pos, stack, cannotSpawn))
            return;
        if (QuestionBlock.spawnBomb(level, pos, stack, cannotSpawn))
            return;
        if (QuestionBlock.spawnCannonball(level, pos, stack, cannotSpawn))
            return;
        QuestionBlock.spawnConfetti(level, pos, stack);

        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof CoinBlock) {
            Player player = null;

            if (entityHitBlock instanceof Player hitPlayer)
                player = hitPlayer;
            else if (entityHitBlock instanceof TraceableEntity traceableEntity
                    && traceableEntity.getOwner() instanceof Player owner)
                player = owner;

            if (player != null) {
                boolean itemAdded = player.addItem(stack);

                if (blockItem.getBlock() instanceof StarCoinBlock)
                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverLevel, particlePos,
                            UniformInt.of(4, 6));
                else ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverLevel, particlePos,
                        UniformInt.of(2, 3));

                if (!itemAdded)
                    QuestionBlock.spawnItem(level, pos, stack, true);
                return;
            }
        }

        if (stack.getItem() instanceof BlockItem blockItem
                && !blockItem.getBlock().defaultBlockState().is(TagRegistry.QUESTION_BLOCKS_CAN_PLACE)
                && !(stack.getItem() instanceof SolidBucketItem)) {
            QuestionBlock.spawnItem(level, pos, stack, true);
            return;
        }

        if (!QuestionBlock.useItem(serverLevel, pos, stack, entityHitBlock))
            QuestionBlock.spawnItem(level, pos, stack, true);

    }

    public static void dropResult(ServerLevel level, BlockPos pos, ItemStack stackCopy, ItemStack resultStack, FakePlayer fakePlayer) {
        if (!ItemStack.isSameItemSameComponents(stackCopy, resultStack)) {
            QuestionBlock.spawnItem(level, pos, resultStack.copy(), true);

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR
                        || slot == EquipmentSlot.OFFHAND
                        || slot == EquipmentSlot.MAINHAND) {
                    ItemStack equipped = fakePlayer.getItemBySlot(slot);

                    if (!equipped.isEmpty() && !ItemStack.isSameItemSameComponents(resultStack, equipped)) {
                        QuestionBlock.spawnItem(level, pos, equipped.copy(), true);
                        fakePlayer.setItemSlot(slot, ItemStack.EMPTY);
                    }
                }
            }
        }
    }

    public static BlockPos getBlockPos(Level level, BlockPos pos, ItemStack stack) {
        Entity entity;

        if (level instanceof ServerLevel serverLevel) {
            if (stack.getItem() instanceof SpawnEggItem spawnEgg)
                entity = spawnEgg.getType(stack).create(serverLevel);
            else entity = new ItemEntity(serverLevel, pos.getX(), pos.getY(), pos.getZ(), stack);

            if (entity != null) {
                if (level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                        || !level.getBlockState(pos.above()).isCollisionShapeFullBlock(level, pos.above())
                        || level.getFluidState(pos.above()).is(FluidTags.WATER)
                            || (level.getBlockState(pos.below()).isCollisionShapeFullBlock(level, pos.below())
                                && !level.getFluidState(pos.below()).is(FluidTags.WATER))) {
                    pos = pos.above();
                } else pos = BlockPos.containing(pos.getX(), pos.getY() - entity.getBbHeight(), pos.getZ());
            }
        }
        return pos;
    }

    public static boolean spawnCannonball(Level level, BlockPos pos, ItemStack stack, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (stack.getItem() == CompatRegistry.CANNONBALL_ITEM.get()) {
            Entity entity = CompatRegistry.CANNONBALL.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                entity.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        level.random.triangle(0.0, 0.3),
                        level.random.triangle(0.5, 0.3),
                        level.random.triangle(0.0, 0.3)));
                level.addFreshEntity(entity);
                stack.copyWithCount(1);
                return true;
            }
        }
        return false;
    }

    public static void spawnConfetti(Level level, BlockPos pos, ItemStack stack) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (stack.getItem() == CompatRegistry.CONFETTI_POPPER_ITEM.get()) {
            Creeper entity = EntityType.CREEPER.create(serverLevel);

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

                entity.setPos(pos.getX() + 0.5D, pos.getY() - 1.0D, pos.getZ() + 0.5D);
                level.broadcastEntityEvent(entity, (byte) 113);
                level.addFreshEntity(entity);
            }
            level.gameEvent(null, GameEvent.EXPLODE, pos);
        }
    }

    public static boolean spawnBomb(Level level, BlockPos pos, ItemStack stack, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (stack.getItem() == CompatRegistry.BOMB_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                if (level.getBlockState(pos.above()).isAir() || level.getFluidState(pos.above()).is(FluidTags.WATER)) {
                    entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                    entity.setDeltaMovement(new Vec3(
                            level.random.triangle(0.0, 0.2),
                            level.random.triangle(0.5, 0.2),
                            level.random.triangle(0.0, 0.2)));
                } else {
                    entity.setPos(pos.getX() + 0.5D, pos.getY() - entity.getBbHeight(), pos.getZ() + 0.5D);
                    entity.setDeltaMovement(new Vec3(0, -0.5, 0));
                }
                level.addFreshEntity(entity);
                return true;
            }
        } else if (stack.getItem() == CompatRegistry.BOMB_BLUE_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putInt("Type", 1);
                entity.load(nbt);

                entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        level.random.triangle(0.0, 0.2),
                        level.random.triangle(0.5, 0.2),
                        level.random.triangle(0.0, 0.2)));
                level.addFreshEntity(entity);
                return true;
            }
        } else if (stack.getItem() == CompatRegistry.BOMB_SPIKY_ITEM.get()) {
            Entity entity = CompatRegistry.BOMB.get().create(serverLevel);

            if (entity != null && !entity.getType().is(cannotSpawn)) {
                CompoundTag nbt = new CompoundTag();
                entity.save(nbt);
                nbt.putInt("Type", 2);
                entity.load(nbt);

                entity.setPos(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D);
                entity.setDeltaMovement(new Vec3(
                        level.random.triangle(0.0, 0.2),
                        level.random.triangle(0.5, 0.2),
                        level.random.triangle(0.0, 0.2)));
                level.addFreshEntity(entity);
                return true;
            }
        }
        return false;
    }

    public static boolean spawnEndCrystal(Level level, BlockPos pos, ItemStack stack, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (stack.getItem() instanceof EndCrystalItem) {
            EndCrystal endCrystal = new EndCrystal(serverLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

            if (!endCrystal.getType().is(cannotSpawn)) {
                endCrystal.setPos(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
                endCrystal.setDeltaMovement(new Vec3(0, -0.5, 0));
                endCrystal.setShowBottom(false);
                level.addFreshEntity(endCrystal);
                level.gameEvent(null, GameEvent.ENTITY_PLACE, pos);
                stack.copyWithCount(1);
                return true;
            }
        }
        return false;
    }

    public static boolean spawnMinecart(Level level, BlockPos pos, ItemStack stack, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (stack.getItem() instanceof MinecartItem cart) {
            AbstractMinecart abstractMinecart =
                    AbstractMinecart.createMinecart(serverLevel, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, cart.type, stack, null);

            if (!abstractMinecart.getType().is(cannotSpawn)) {
                abstractMinecart.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                level.addFreshEntity(abstractMinecart);
                stack.copyWithCount(1);
                return true;
            }
        }
        return false;
    }

    public static boolean spawnTNT(Level level, BlockPos pos, ItemStack stack, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return false;

        if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof TntBlock) {
            PrimedTnt primedtnt = new PrimedTnt(serverLevel, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);

            if (!primedtnt.getType().is(cannotSpawn)) {
                primedtnt.setPos(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
                level.addFreshEntity(primedtnt);
                serverLevel.gameEvent(null, GameEvent.PRIME_FUSE, pos);
                return true;
            }
        }
        return false;
    }
}