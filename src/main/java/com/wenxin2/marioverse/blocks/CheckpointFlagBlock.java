package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.states.TripleBlockStates;
import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.MegaMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.MiniMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.items.PiranhaPlantPodItem;
import com.wenxin2.marioverse.items.PlasticBucketItem;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.GameEventRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.items.BasePowerUpItem;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.decoration.ArmorStand;
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
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.EndCrystalItem;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MinecartItem;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ThrowablePotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.WindChargeItem;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CheckpointFlagBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<CheckpointFlagBlock> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(DyeColor.CODEC.optionalFieldOf("color")
                            .forGetter(flagBlock -> Optional.ofNullable(flagBlock.color)), propertiesCodec())
                    .apply(instance, (dyeColor, properties) -> new CheckpointFlagBlock(3, dyeColor.orElse(null), properties)));

    public static final EnumProperty<TripleBlockStates> PART = EnumProperty.create("part", TripleBlockStates.class);
    public static final BooleanProperty CLAIMED = BooleanProperty.create("claimed");
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Component UNKNOWN_CONTENTS = Component.translatable("container.marioverse.checkpoint_flag.unknownContents");
    public static final ResourceLocation CONTENTS = ResourceLocation.withDefaultNamespace("contents");
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;
    @Nullable private final DyeColor color;
    int tooltipLineAmt;

    protected static final VoxelShape CHECKPOINT_FLAG_TOP =
            Shapes.or(Block.box(7, 0, 7, 9, 4, 9),
            Block.box(6, 4, 6, 10, 8, 10)).optimize();
    protected static final VoxelShape CHECKPOINT_FLAG_MIDDLE =
            Block.box(7, 0, 7, 9, 16, 9).optimize();
    protected static final VoxelShape CHECKPOINT_FLAG_BOTTOM =
            Shapes.or(Block.box(4, 0, 4, 12, 2, 12),
            Block.box(7, 2, 7, 9, 16, 9)).optimize();

    public CheckpointFlagBlock(int tooltipLineAmt, @Nullable DyeColor color, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(CLAIMED, Boolean.FALSE)
                .setValue(PART, TripleBlockStates.BOTTOM).setValue(ROTATION, 0).setValue(WATERLOGGED, Boolean.FALSE));
        this.color = color;
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @NotNull
    @Override
    public MapCodec<CheckpointFlagBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CLAIMED, PART, ROTATION, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(PART) == TripleBlockStates.TOP)
            return CHECKPOINT_FLAG_TOP;
        if (state.getValue(PART) == TripleBlockStates.MIDDLE)
            return CHECKPOINT_FLAG_MIDDLE;
        else return CHECKPOINT_FLAG_BOTTOM;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CheckpointFlagBlockEntity(pos, state);
    }

    @NotNull
    @Override
    public ItemStack getCloneItemStack(LevelReader worldReader, BlockPos pos, BlockState state) {
        if (worldReader.getBlockEntity(pos) instanceof CheckpointFlagBlockEntity flagBE) {
            ItemStack stack = new ItemStack(this.asItem());
            DataComponentMap.Builder builder = DataComponentMap.builder();
            flagBE.collectImplicitComponents(builder);
            DataComponentMap components = builder.build();

            stack.applyComponents(components);
            return stack;
        } else return super.getCloneItemStack(worldReader, pos, state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag options) {
        super.appendHoverText(stack, tooltipContext, list, options);
        list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));

        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable("block.marioverse.checkpoint_flag.tooltip.line" + lineAmt));
            list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable("block.marioverse.checkpoint_flag.tooltip"));

        if (stack.has(DataComponents.CONTAINER_LOOT))
            list.add(UNKNOWN_CONTENTS);

        int i = 0;
        int j = 0;

        for (ItemStack itemstack : stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).nonEmptyItems()) {
            j++;
            if (i <= 4) {
                i++;
                list.add(Component.translatable("container.marioverse.checkpoint_flag.itemCount",
                        itemstack.getHoverName(), itemstack.getCount()).withStyle(ChatFormatting.ITALIC));
            }
        }

        if (j - i > 0)
            list.add(Component.translatable("container.marioverse.checkpoint_flag.more", j - i));
    }

    @Override
    public boolean canSurvive(BlockState state, @NotNull LevelReader worldReader, BlockPos pos) {
        return worldReader instanceof Level world && this.canPlaceBlock(world, pos) && this.canPlaceBlock(world, pos.above())
                && this.canPlaceBlock(world, pos.above(2));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final Level world = context.getLevel();
        final FluidState fluidState = world.getFluidState(pos);

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8)
                .setValue(ROTATION, RotationSegment.convertToSegment(context.getRotation()));
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
        super.setPlacedBy(world, pos, state, entity, stack);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (entity != null && this.canPlaceBlock(world, pos) && this.canPlaceBlock(world, pos.above())
                && this.canPlaceBlock(world, pos.above(2))) {
            world.setBlock(pos.above(), state.setValue(PART, TripleBlockStates.MIDDLE)
                    .setValue(WATERLOGGED, world.getFluidState(pos.above()).getType() == Fluids.WATER), 3);
            world.setBlock(pos.above(2), state.setValue(PART, TripleBlockStates.TOP)
                    .setValue(WATERLOGGED, world.getFluidState(pos.above(2)).getType() == Fluids.WATER), 3);
        }

        if (blockEntity instanceof CheckpointFlagBlockEntity flagBE) {
            BlockEntity middleBlockEntity = world.getBlockEntity(pos.above());
            BlockEntity topBlockEntity = world.getBlockEntity(pos.above(2));

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

                if (middleBlockEntity instanceof CheckpointFlagBlockEntity middleFlagBE) {
                    if (middleFlagBE.getCustomName() == null) {
                        middleFlagBE.setCustomName(stack.getHoverName());
                        middleFlagBE.markUpdated();

                        if (middleFlagBE.isWonderFlag()) {
                            middleFlagBE.setWonderFlag(Boolean.TRUE);
                            if (!world.isClientSide())
                                PacketDistributor.sendToAllPlayers(new WonderNamePayload(pos.above(), middleFlagBE.hasWonderFlag()));
                        } else if (middleFlagBE.isAmericanFlag()) {
                            middleFlagBE.setAmericanFlag(Boolean.TRUE);
                            if (!world.isClientSide())
                                PacketDistributor.sendToAllPlayers(new AmericaNamePayload(pos.above(), middleFlagBE.hasAmericanFlag()));
                        }
                    }
                }

                if (topBlockEntity instanceof CheckpointFlagBlockEntity topFlagBE) {
                    if (topFlagBE.getCustomName() == null) {
                        topFlagBE.setCustomName(stack.getHoverName());
                        topFlagBE.markUpdated();

                        if (topFlagBE.isWonderFlag()) {
                            topFlagBE.setWonderFlag(Boolean.TRUE);
                            if (!world.isClientSide())
                                PacketDistributor.sendToAllPlayers(new WonderNamePayload(pos.above(2), topFlagBE.hasWonderFlag()));
                        } else if (topFlagBE.isAmericanFlag()) {
                            topFlagBE.setAmericanFlag(Boolean.TRUE);
                            if (!world.isClientSide())
                                PacketDistributor.sendToAllPlayers(new AmericaNamePayload(pos.above(2), topFlagBE.hasAmericanFlag()));
                        }
                    }
                }
            }

            if (stack.has(DataComponents.CONTAINER_LOOT)) {
                SeededContainerLoot lootTableReference = stack.get(DataComponents.CONTAINER_LOOT);

                if (lootTableReference != null) {
                    flagBE.setLootTable(lootTableReference.lootTable(), lootTableReference.seed());
                    flagBE.markUpdated();

                    if (middleBlockEntity instanceof CheckpointFlagBlockEntity middleFlagBE) {
                        middleFlagBE.setLootTable(lootTableReference.lootTable(), lootTableReference.seed());
                        middleFlagBE.markUpdated();
                    }

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

                    if (middleBlockEntity instanceof CheckpointFlagBlockEntity middleFlagBE) {
                        middleFlagBE.setTheItem(containerContents.copyOne());
                        middleFlagBE.markUpdated();
                    }

                    if (topBlockEntity instanceof CheckpointFlagBlockEntity topFlagBE) {
                        topFlagBE.setTheItem(containerContents.copyOne());
                        topFlagBE.markUpdated();
                    }
                }
            }
        }
    }

    @Override
    public void destroy(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        if (!worldAccessor.isClientSide()) {
            if (state.getValue(PART) == TripleBlockStates.BOTTOM) {
                worldAccessor.destroyBlock(pos.above(), true);
                worldAccessor.destroyBlock(pos.above(2), true);
                worldAccessor.levelEvent(2001, pos.above(), Block.getId(worldAccessor.getBlockState(pos.above())));
                worldAccessor.levelEvent(2001, pos.above(2), Block.getId(worldAccessor.getBlockState(pos.above(2))));
            } else if (state.getValue(PART) == TripleBlockStates.MIDDLE) {
                worldAccessor.destroyBlock(pos.below(), true);
                worldAccessor.destroyBlock(pos.above(), true);
                worldAccessor.levelEvent(2001, pos.below(), Block.getId(worldAccessor.getBlockState(pos.below())));
                worldAccessor.levelEvent(2001, pos.above(), Block.getId(worldAccessor.getBlockState(pos.above())));
            } else if (state.getValue(PART) == TripleBlockStates.TOP) {
                worldAccessor.destroyBlock(pos.below(), true);
                worldAccessor.destroyBlock(pos.below(2), true);
                worldAccessor.levelEvent(2001, pos.below(), Block.getId(worldAccessor.getBlockState(pos.below())));
                worldAccessor.levelEvent(2001, pos.below(2), Block.getId(worldAccessor.getBlockState(pos.below(2))));
            }
        }
        super.destroy(worldAccessor, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide) {
            if (player.isCreative() || !player.hasCorrectToolForDrops(state, world, pos)) {
                if (state.getValue(PART) == TripleBlockStates.BOTTOM) {
                    world.destroyBlock(pos.above(), false);
                    world.destroyBlock(pos.above(2), false);
                    this.spawnDestroyParticles(world, player, pos.above(), world.getBlockState(pos.above()));
                    this.spawnDestroyParticles(world, player, pos.above(2), world.getBlockState(pos.above(2)));
                } else if (state.getValue(PART) == TripleBlockStates.MIDDLE) {
                    world.destroyBlock(pos.below(), false);
                    world.destroyBlock(pos.above(), false);
                    this.spawnDestroyParticles(world, player, pos.below(), world.getBlockState(pos.below()));
                    this.spawnDestroyParticles(world, player, pos.above(), world.getBlockState(pos.above()));
                } else if (state.getValue(PART) == TripleBlockStates.TOP) {
                    world.destroyBlock(pos.below(), false);
                    world.destroyBlock(pos.below(2), false);
                    this.spawnDestroyParticles(world, player, pos.below(), world.getBlockState(pos.below()));
                    this.spawnDestroyParticles(world, player, pos.below(2), world.getBlockState(pos.below(2)));
                }
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        BlockEntity blockentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (blockentity instanceof CheckpointFlagBlockEntity flagBE) {
            builder = builder.withDynamicDrop(CONTENTS, stack -> {
                for (int i = 0; i < flagBE.getContainerSize(); i++) {
                    stack.accept(flagBE.getItem(i));
                }
            });
        }

        return super.getDrops(state, builder);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        if (serverWorld.getBlockEntity(pos) instanceof CheckpointFlagBlockEntity checkpointFlagBE)
            checkpointFlagBE.stopTriggeredAnim("claim_controller", "claim");
        super.tick(state, serverWorld, pos, random);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(hand);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CheckpointFlagBlockEntity flagBE && !heldItem.is(TagRegistry.CANNOT_PLACE_IN_CHECKPOINT_FLAGS)) {
            ItemStack blockStack = flagBE.getTheItem();

            if (world.isClientSide) {
                return ItemInteractionResult.CONSUME;
            } else {
                if (!heldItem.isEmpty()
                        && (ConfigRegistry.CHECKPOINT_FLAG_ADD_ITEMS.get() || player.isCreative())
                        && (blockStack.isEmpty() || ItemStack.isSameItemSameComponents(heldItem, blockStack)
                        && blockStack.getCount() < blockStack.getMaxStackSize())) {
                    player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
                    ItemStack stackConsumed = stack.consumeAndReturn(1, player);

                    float soundPitch;
                    if (flagBE.isEmpty()) {
                        soundPitch = (float) stackConsumed.getCount() / (float) stackConsumed.getMaxStackSize();
                        flagBE.setTheItem(stackConsumed);

                        if (state.getValue(PART) == TripleBlockStates.TOP) {
                            if (world.getBlockEntity(pos.below()) instanceof CheckpointFlagBlockEntity topFlagBE)
                                topFlagBE.setTheItem(stackConsumed);
                            if (world.getBlockEntity(pos.below(2)) instanceof CheckpointFlagBlockEntity topFlagBE)
                                topFlagBE.setTheItem(stackConsumed);
                        } else if (state.getValue(PART) == TripleBlockStates.MIDDLE) {
                            if (world.getBlockEntity(pos.above()) instanceof CheckpointFlagBlockEntity middleFlagBE)
                                middleFlagBE.setTheItem(stackConsumed);
                            if (world.getBlockEntity(pos.below()) instanceof CheckpointFlagBlockEntity middleFlagBE)
                                middleFlagBE.setTheItem(stackConsumed);
                        } else {
                            if (world.getBlockEntity(pos.above()) instanceof CheckpointFlagBlockEntity bottomFlagBE)
                                bottomFlagBE.setTheItem(stackConsumed);
                            if (world.getBlockEntity(pos.above(2)) instanceof CheckpointFlagBlockEntity bottomFlagBE)
                                bottomFlagBE.setTheItem(stackConsumed);
                        }
                    } else {
                        soundPitch = (float) blockStack.getCount() / (float) blockStack.getMaxStackSize();
                        blockStack.grow(1);
                    }
                    world.playSound(null, pos, SoundRegistry.ITEM_INSERTED.get(), SoundSource.BLOCKS, 1.0F, 0.7F + 0.5F * soundPitch);
                    flagBE.setChanged();
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

        if (world.getBlockEntity(pos) instanceof CheckpointFlagBlockEntity flagBE) {
            ItemStack blockStack = flagBE.getTheItem();

            if ((heldItem.isEmpty() || !ItemStack.isSameItemSameComponents(heldItem, blockStack))
                    && (ConfigRegistry.CHECKPOINT_FLAG_REMOVE_ITEMS.get() || player.isCreative())) {
                world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

                ItemStack storedItem = flagBE.getTheItem();

                if (!storedItem.isEmpty()) {
                    if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                        PiglinAi.angerNearbyPiglins(player, false);

                    boolean itemAdded = player.addItem(storedItem.copyWithCount(1));
                    if (!itemAdded)
                        player.drop(storedItem.copyWithCount(1), false);

                    world.playSound(null, pos, SoundRegistry.ITEM_SPAWNS.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    flagBE.splitTheItem(1);
                    flagBE.setChanged();

                    if (state.getValue(PART) == TripleBlockStates.TOP) {
                        if (world.getBlockEntity(pos.below()) instanceof CheckpointFlagBlockEntity topFlagBE)
                            topFlagBE.splitTheItem(1);
                        if (world.getBlockEntity(pos.below(2)) instanceof CheckpointFlagBlockEntity topFlagBE)
                            topFlagBE.splitTheItem(1);
                    } else if (state.getValue(PART) == TripleBlockStates.MIDDLE) {
                        if (world.getBlockEntity(pos.above()) instanceof CheckpointFlagBlockEntity middleFlagBE)
                            middleFlagBE.splitTheItem(1);
                        if (world.getBlockEntity(pos.below()) instanceof CheckpointFlagBlockEntity middleFlagBE)
                            middleFlagBE.splitTheItem(1);
                    } else {
                        if (world.getBlockEntity(pos.above()) instanceof CheckpointFlagBlockEntity bottomFlagBE)
                            bottomFlagBE.splitTheItem(1);
                        if (world.getBlockEntity(pos.above(2)) instanceof CheckpointFlagBlockEntity bottomFlagBE)
                            bottomFlagBE.splitTheItem(1);
                    }
                    return InteractionResult.SUCCESS;
                } else return InteractionResult.PASS;
            } else return InteractionResult.PASS;
        } else return InteractionResult.PASS;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockPos respawnPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        BlockPos statePos = switch (state.getValue(PART)) {
            case TOP -> pos.below(2);
            case MIDDLE -> pos.below();
            default -> pos;
        };
        BlockState statePart = world.getBlockState(statePos);

        if (entity.getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS)
                && entity.getData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN) <= 0)
            this.claimCheckpoint(state, world, pos, entity, statePart, statePos, respawnPos);

        if (entity instanceof Projectile projectile && projectile.getOwner() != null
                && projectile.getOwner().getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS)
                && projectile.getOwner().getData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN) <= 0)
            this.claimCheckpoint(state, world, pos, projectile.getOwner(), statePart, statePos, respawnPos);
    }

    @Override
    protected void onExplosionHit(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> consumer) {
        Entity entity = explosion.getDirectSourceEntity();
        BlockPos respawnPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ());
        BlockPos statePos = switch (state.getValue(PART)) {
            case TOP -> pos.below(2);
            case MIDDLE -> pos.below();
            default -> pos;
        };
        BlockState statePart = level.getBlockState(statePos);

        if (explosion.canTriggerBlocks()) {
            if (entity != null && entity.getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS)
                    && entity.getData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN) <= 0)
                this.claimCheckpoint(state, level, pos, entity, statePart, statePos, respawnPos);
        }

        super.onExplosionHit(state, level, pos, explosion, consumer);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getColoredItemStack(@Nullable DyeColor color) {
        return new ItemStack(BlockRegistry.CHECKPOINT_FLAGS.get(color));
    }

    private boolean canPlaceBlock(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return (state.isAir() || state.canBeReplaced() || state.is(this));
    }

    private void claimCheckpoint(BlockState state, Level level, BlockPos pos, Entity entity, BlockState statePart, BlockPos statePos, BlockPos respawnPos) {
        if (statePart.hasProperty(CLAIMED) && !statePart.getValue(CLAIMED)) {
            if (level.getBlockEntity(statePos) instanceof CheckpointFlagBlockEntity checkpointFlagBE) {
                checkpointFlagBE.markUpdated();

                if (!(entity instanceof Player)) {
                    entity.level().broadcastEntityEvent(entity, (byte) 112);
                    level.playSound(null, pos, SoundRegistry.CHECKPOINT_FLAG_CLAIMED.get(), SoundSource.BLOCKS);
                } else ParticleUtils.spawnParticlesOnBlockFaces(level, statePos, ParticleRegistry.GLOWING_STAR.get(), UniformInt.of(1, 1));

                if (!checkpointFlagBE.isAmericanFlag() && statePart.getBlock() != BlockRegistry.CLASSIC_GOAL_POLE.get())
                    checkpointFlagBE.triggerAnim("switch_controller", "switch");

                level.scheduleTick(statePos, this, 40);
                level.gameEvent(entity, GameEventRegistry.CHECKPOINT_ACTIVATED, statePos);
                checkpointFlagBE.triggerAnim("claim_controller", "claim");
            }

            level.scheduleTick(statePos, this, 3);
            level.setBlock(pos, state.setValue(CLAIMED, Boolean.TRUE), 3);

            if (state.getValue(PART) == TripleBlockStates.BOTTOM) {
                if (level.getBlockState(pos.above()).hasProperty(CLAIMED)
                        && level.getBlockState(pos.above(2)).hasProperty(CLAIMED)) {
                    level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(CLAIMED, Boolean.TRUE), 3);
                    level.setBlock(pos.above(2), level.getBlockState(pos.above(2)).setValue(CLAIMED, Boolean.TRUE), 3);
                }
            } else if (state.getValue(PART) == TripleBlockStates.MIDDLE) {
                if (level.getBlockState(pos.above()).hasProperty(CLAIMED)
                        && level.getBlockState(pos.below()).hasProperty(CLAIMED)) {
                    level.setBlock(pos.above(), level.getBlockState(pos.above()).setValue(CLAIMED, Boolean.TRUE), 3);
                    level.setBlock(pos.below(), level.getBlockState(pos.below()).setValue(CLAIMED, Boolean.TRUE), 3);
                }
            } else {
                if (level.getBlockState(pos.below()).hasProperty(CLAIMED)
                        && level.getBlockState(pos.below(2)).hasProperty(CLAIMED)) {
                    level.setBlock(pos.below(), level.getBlockState(pos.below()).setValue(CLAIMED, Boolean.TRUE), 3);
                    level.setBlock(pos.below(2), level.getBlockState(pos.below(2)).setValue(CLAIMED, Boolean.TRUE), 3);
                }
            }

            if (level.getBlockEntity(respawnPos) instanceof CheckpointFlagBlockEntity flagBE
                    && ConfigRegistry.CHECKPOINT_FLAG_CLAIM_USES_ITEMS.get()) {
                ItemStack storedItem = flagBE.getTheItem();

                if (!storedItem.isEmpty() && entity instanceof LivingEntity livingEntity) {
                    MarioverseSoundTypes.playSounds(level, respawnPos, storedItem, flagBE);
                    CheckpointFlagBlock.spawnFromContainer(level, respawnPos, storedItem, livingEntity,
                            ConfigRegistry.CHECKPOINT_FLAG_SPAWNS_MOBS.get(), ConfigRegistry.CHECKPOINT_FLAG_APPLIES_POWER_UPS.get(),
                            ConfigRegistry.CHECKPOINT_FLAG_BUCKET_TWEAKS.get(), TagRegistry.CHECKPOINT_FLAG_CANNOT_SPAWN);
                    flagBE.splitTheItem(1);
                }
            }
        }

        if (entity instanceof ServerPlayer player && !pos.equals(player.getRespawnPosition())) {
            BlockPos playerRespawnPos = player.getRespawnPosition();
            BlockPos newRespawnPos = switch (state.getValue(PART)) {
                case TOP -> respawnPos.below(2);
                case MIDDLE -> respawnPos.below();
                default -> respawnPos;
            };

            if (level.getBlockEntity(newRespawnPos) instanceof CheckpointFlagBlockEntity checkpointFlagBE
                    && !(newRespawnPos.equals(playerRespawnPos))) {
                level.scheduleTick(newRespawnPos, this, 40);
                level.gameEvent(entity, GameEventRegistry.CHECKPOINT_ACTIVATED, statePos);
                checkpointFlagBE.triggerAnim("claim_controller", "claim");

                level.playSound(null, newRespawnPos, SoundRegistry.CHECKPOINT_FLAG_CLAIMED.get(), SoundSource.BLOCKS);
                ParticleUtils.spawnParticlesOnBlockFaces(level, newRespawnPos, ParticleRegistry.GLOWING_STAR.get(), UniformInt.of(1, 1));
                player.setRespawnPosition(level.dimension(), newRespawnPos, player.getYRot(), false, true);
                entity.setData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN, 40);

                if (level instanceof ServerLevel serverWorld)
                    serverWorld.sendParticles(ParticleRegistry.GLOWING_STAR.get(),
                            newRespawnPos.getX() + 0.5, newRespawnPos.getY() + 0.5, newRespawnPos.getZ() + 0.5,
                            10, 0.4, 0.5, 0.4, 0.6);
            }
        }
    }

    public static void spawnFromContainer(Level level, BlockPos pos, ItemStack stack, LivingEntity livingEntity, boolean spawnMobs,
                                          boolean applyPowerUps, boolean canEmptyBuckets, TagKey<EntityType<?>> cannotSpawn) {
        if (!(level instanceof ServerLevel serverLevel)) return;

        if (!spawnMobs && stack.getItem() instanceof SpawnEggItem spawnEgg) {
            EntityType<?> entityType = spawnEgg.getType(stack);
            if (entityType.is(cannotSpawn))
                return;
            QuestionBlock.spawnItem(level, pos, stack, true);
            return;
        }

        if (applyPowerUps && stack.getItem() instanceof BasePowerUpItem powerUpItem) {
            CheckpointFlagBlock.applyPowerUps(level, pos, stack, livingEntity, powerUpItem);
            return;
        }

        if ((!canEmptyBuckets || !level.getBlockState(pos.above(3)).canBeReplaced() || stack.getItem() instanceof SolidBucketItem)
                && (stack.getItem() instanceof BucketItem || stack.getItem() instanceof SolidBucketItem)) {
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

            if (livingEntity instanceof Player hitPlayer)
                player = hitPlayer;
            else if (livingEntity instanceof TraceableEntity traceableEntity
                    && traceableEntity.getOwner() instanceof Player owner)
                player = owner;

            if (player != null) {
                boolean itemAdded = player.addItem(stack);

                if (blockItem.getBlock() instanceof StarCoinBlock)
                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverLevel, pos,
                            UniformInt.of(4, 6));
                else ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverLevel, pos,
                        UniformInt.of(2, 3));

                if (!itemAdded)
                    QuestionBlock.spawnItem(level, pos, stack, true);
                return;
            }
        }

        if (stack.getItem() instanceof BlockItem) {
            QuestionBlock.spawnItem(level, pos, stack, true);
            return;
        }

        if (!QuestionBlock.useItem(serverLevel, pos, stack, livingEntity))
            QuestionBlock.spawnItem(level, pos, stack, true);

    }

    public static void applyPowerUps(Level level, BlockPos pos, ItemStack stack, LivingEntity entity, BasePowerUpItem powerUpItem) {
        EntityType<?> entityType = powerUpItem.getType(stack);

        if (level instanceof ServerLevel serverLevel) {
            if (!entityType.is(TagRegistry.CHECKPOINT_FLAG_CANNOT_SPAWN)) {
                Entity spawnedEntity = entityType.create(serverLevel);
                if (spawnedEntity != null && entity instanceof AbilitiesHandler handler) {
                    if (stack.getItem() == ItemRegistry.SUPER_MUSHROOM.get()
                            && spawnedEntity instanceof SuperMushroomEntity powerUp) {
                        handler.applySuperMushroomPowerUp(level, entity, powerUp, ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
                    } else if (stack.getItem() == ItemRegistry.MEGA_MUSHROOM.get()
                            && spawnedEntity instanceof MegaMushroomEntity powerUp) {
                        handler.applyMegaMushroomPowerUp(level, entity, powerUp);
                    } else if (stack.getItem() == ItemRegistry.MINI_MUSHROOM.get()
                            && spawnedEntity instanceof MiniMushroomEntity powerUp) {
                        handler.applyMiniMushroomPowerUp(level, entity, powerUp);
                    } else if (stack.getItem() == ItemRegistry.ONE_UP_MUSHROOM.get()
                            && spawnedEntity instanceof OneUpMushroomEntity powerUp) {
                        handler.applyOneUpMushroomPowerUp(level, stack, entity, powerUp);
                    } else if (stack.getItem() == ItemRegistry.FIRE_FLOWER.get()
                            && spawnedEntity instanceof FireFlowerEntity powerUp) {
                        handler.applyFireFlowerPowerUp(level, entity, powerUp);
                    } else if (stack.getItem() == ItemRegistry.ICE_FLOWER.get()
                            && spawnedEntity instanceof IceFlowerEntity powerUp) {
                        handler.applyIceFlowerPowerUp(level, entity, powerUp);
                    } else if (stack.getItem() == ItemRegistry.SUPER_STAR.get()
                            && spawnedEntity instanceof SuperStarEntity powerUp) {
                        handler.applySuperStarPowerUp(level, entity, powerUp);
                    } else {
                        entityType.spawn(serverLevel, stack, null,
                                BlockPos.containing(pos.getX(), pos.getY(), pos.getZ()),
                                MobSpawnType.SPAWN_EGG, true, false);
                    }
                }
                stack.copyWithCount(1);
            } else QuestionBlock.spawnItem(level, pos, stack, true);
        }
    }
}
