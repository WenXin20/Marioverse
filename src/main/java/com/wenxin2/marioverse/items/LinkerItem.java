package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.integration.immersive_paintings_compat.PaintingEntityProvider;
import com.wenxin2.marioverse.integration.twilightforest_compat.MagicPaintingEntityProvider;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.NotNull;

public class LinkerItem extends TieredItem {
    public LinkerItem(final Properties properties, Tier tier) {
        super(tier, properties);
    }

    private static boolean getLinkableBlock(BlockState state) {
        if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.ENTRANCE))
            return true;
        else if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.ENTRANCE))
            return false;
        else if (state.getBlock() instanceof ClearWarpPipeBlock)
            return true;
        else return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (getIsBound(stack) && stack.has(DataComponentRegistry.WARP_POS)) {
            list.add(Component.literal(""));

            list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound", true)
                    .withStyle(ChatFormatting.GOLD));

            if (stack.has(DataComponentRegistry.WARP_BLOCK.get()))
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.block",
                        getWarpBlock(stack).name(), true).withStyle(ChatFormatting.GRAY));
            if (stack.has(DataComponentRegistry.WARP_ENTITY.get()) && stack.has(DataComponentRegistry.WARP_PAINTING.get()))
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.painting",
                        Component.translatable(getWarpPainting(stack).name()), getWarpEntity(stack).name(), true).withStyle(ChatFormatting.GRAY));
            if (stack.has(DataComponentRegistry.WARP_ENTITY.get()))
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.entity",
                        getWarpEntity(stack).name(), true).withStyle(ChatFormatting.GRAY));

            if (stack.has(DataComponentRegistry.WARP_POS)) {
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.x",
                        getWarpPos(stack).getX(), true).withStyle(ChatFormatting.GRAY));
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.y",
                        getWarpPos(stack).getY(), true).withStyle(ChatFormatting.GRAY));
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.bound.z",
                        getWarpPos(stack).getZ(), true).withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @NotNull
    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Player player = useOnContext.getPlayer();
        Level world = useOnContext.getLevel();
        BlockPos pos = useOnContext.getClickedPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        ItemStack stack = useOnContext.getItemInHand();
        String dimension = world.dimension().location().toString();
        float pitch = 0.9F + world.random.nextFloat() * 0.2F;

        if (player != null && !player.isCreative() && ConfigRegistry.CREATIVE_WRENCH_LINKING.get()) {
            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.requires_creative"), true);
            return InteractionResult.SUCCESS;
        } else if (player != null) {
            if (player.isShiftKeyDown() && blockEntity instanceof BaseWarpBlockEntity warpBE
                    && getLinkableBlock(state)) {
                UUID uuid = warpBE.getUUID();

                if (warpBE.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.waxed",
                            state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
                    return InteractionResult.SUCCESS;
                } else if (!getIsBound(stack) || !stack.has(DataComponentRegistry.WARP_POS)) {

                    if (!world.isClientSide && uuid == null) {
                        uuid = UUID.randomUUID();
                        warpBE.setUUID(uuid);
                        warpBE.setChanged();
                    }
                    // First interaction: Bind the first block
                    setWarpBlock(stack, pos, state);
                    setWarpPos(stack, pos);
                    setWarpDimension(stack, dimension);
                    setWarpUUID(stack, uuid);
                    setBound(stack, true);  // Mark the item as bound
                    stack.remove(DataComponentRegistry.WARP_ENTITY.get());
                    stack.remove(DataComponentRegistry.WARP_PAINTING.get());

                    player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.bound",
                                    state.getBlock().getName()).withStyle(ChatFormatting.GREEN), true);

                    this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                    this.playSound(world, pos, SoundRegistry.WRENCH_WARP_LINKED.get(), SoundSource.BLOCKS, 1.0F, pitch);
                } else if (getIsBound(stack) && stack.has(DataComponentRegistry.WARP_POS)) {

                    if (!world.isClientSide && uuid == null) {
                        uuid = UUID.randomUUID();
                        warpBE.setUUID(uuid);
                        warpBE.setChanged();
                    }

                    // Second interaction: Link the blocks
                    BlockPos firstPos = getWarpPos(stack);
                    BlockState firstState = world.getBlockState(firstPos);
                    String firstDim = getWarpDimension(stack);

                  //  if (dimension.equals(getWarpDimension(stack))) {
                        BlockEntity firstBE = world.getBlockEntity(firstPos);
                        if (firstBE instanceof BaseWarpBlockEntity firstWarpBE) {

                            // Perform the linking logic
                            this.link(stack, firstWarpBE, warpBE);

                            if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                                BlockEntity firstBEAbove = world.getBlockEntity(firstPos.above());
                                BlockEntity BEAbove = world.getBlockEntity(pos.above());
                                if (firstBEAbove instanceof WarpDoorBlockEntity firstWarpBEAbove && BEAbove instanceof BaseWarpBlockEntity warpBEAbove)
                                    this.link(stack, firstWarpBEAbove, warpBEAbove);
                            }

                            if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
                                BlockEntity firstBEBelow = world.getBlockEntity(firstPos.below());
                                BlockEntity BEBelow = world.getBlockEntity(pos.below());
                                if (firstBEBelow instanceof WarpDoorBlockEntity firstWarpBEAbove && BEBelow instanceof BaseWarpBlockEntity warpBEBelow)
                                    this.link(stack, firstWarpBEAbove, warpBEBelow);
                            }

                            player.displayClientMessage(Component.translatable(this.getDescriptionId() + ".message.linked_warp_block",
                                            state.getBlock().getName(), firstState.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);

                            this.spawnParticles(world, pos, ParticleTypes.ENCHANT);
                            this.playSound(world, pos, SoundRegistry.WRENCH_WARP_CREATED.get(), SoundSource.BLOCKS, 1.0F, pitch);
                        }
                  //  }
                    setBound(stack, false);  // Reset binding
                }
                return InteractionResult.sidedSuccess(true);
            }
        }
        return super.useOn(useOnContext);
    }

    public void link(ItemStack stack, BaseWarpBlockEntity firstPipeBlockEntity, BaseWarpBlockEntity secondPipeBlockEntity) {
        UUID firstUuid = firstPipeBlockEntity.getUUID();
        UUID secondUuid = secondPipeBlockEntity.getUUID();

        BlockPos firstPos = firstPipeBlockEntity.getBlockPos();
        BlockPos secondPos = secondPipeBlockEntity.getBlockPos();
        ResourceKey<Level> firstDim = firstPipeBlockEntity.getDestinationDim();
        ResourceKey<Level> secondDim = secondPipeBlockEntity.getDestinationDim();

        // Linking logic
        firstPipeBlockEntity.setDestinationPos(secondPos);
        secondPipeBlockEntity.setDestinationPos(firstPos);

        if (secondDim != null)
            firstPipeBlockEntity.setDestinationDim(secondDim);
        if (firstDim != null)
            secondPipeBlockEntity.setDestinationDim(firstDim);

        if (firstUuid != null)
            secondPipeBlockEntity.setWarpUuid(firstUuid);
        if (secondUuid != null)
            firstPipeBlockEntity.setWarpUuid(secondUuid);

        firstPipeBlockEntity.markUpdated();
        secondPipeBlockEntity.markUpdated();
        clearItemComponents(stack);
    }

    public void link(ItemStack stack, Entity firstEntity, Entity secondEntity, BlockPos firstPos) {
        if (secondEntity instanceof WarpLinkableEntity secondWarpEntity) {
            UUID secondUuid = secondEntity.getUUID();
            BlockPos secondPos = secondEntity.blockPosition();

            // Try to cast firstEntity to WarpLinkableEntity if available
            if (firstEntity instanceof WarpLinkableEntity firstWarpEntity) {
                UUID firstUuid = firstEntity.getUUID();
                ResourceKey<Level> firstDim = firstWarpEntity.mv$getDestinationDim();
                ResourceKey<Level> secondDim = secondWarpEntity.mv$getDestinationDim();

                // Linking both ways
                firstWarpEntity.mv$setDestinationPos(secondPos);
                secondWarpEntity.mv$setDestinationPos(firstEntity.blockPosition());

                if (secondDim != null)
                    firstWarpEntity.mv$setDestinationDim(secondDim);
                if (firstDim != null)
                    secondWarpEntity.mv$setDestinationDim(firstDim);

                firstWarpEntity.mv$setWarpUuid(secondUuid);
                secondWarpEntity.mv$setWarpUuid(firstUuid);
                WarpLinkableEntity.WARP_ENTITY_LOCATIONS.put(firstPos, firstEntity);
                WarpLinkableEntity.WARP_ENTITY_LOCATIONS.put(secondPos, secondEntity);

                if (firstEntity instanceof Painting firstPainting) {
                    int width = firstPainting.getVariant().value().width();
                    Direction dir = firstPainting.getDirection();
                    WarpLinkableEntity.setWarpPos(firstUuid, firstPos, dir, width);
                } else WarpLinkableEntity.setWarpPos(firstUuid, firstPos, Direction.NORTH, 1);
            } else {
                UUID firstUuid = LinkerItem.getWarpUUID(stack);
                secondWarpEntity.mv$setDestinationPos(firstPos);
                secondWarpEntity.mv$setWarpUuid(firstUuid);
                WarpLinkableEntity.WARP_ENTITY_LOCATIONS.put(firstPos, firstEntity);
                WarpLinkableEntity.WARP_ENTITY_LOCATIONS.put(secondPos, secondEntity);
            }

            if (secondEntity instanceof Painting secondPainting) {
                int width = secondPainting.getVariant().value().width();
                Direction dir = secondPainting.getDirection();
                WarpLinkableEntity.setWarpPos(secondUuid, secondPos, dir, width);
            } else WarpLinkableEntity.setWarpPos(secondUuid, secondPos, Direction.NORTH, 1);

            clearItemComponents(stack);
        }
    }

    public void clearItemComponents(ItemStack stack) {
        setWarpPos(stack, null);
        setWarpDimension(stack, "");
        setWarpUUID(stack, null);
    }

    public static boolean getIsBound(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.IS_BOUND.get(), Boolean.FALSE);
    }

    public static void setBound(ItemStack stack, boolean isBound) {
        stack.set(DataComponentRegistry.IS_BOUND.get(), isBound);
    }

    public static BlockPos getWarpPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_POS, null);
    }

    public static void setWarpPos(ItemStack stack, BlockPos warpPos) {
        stack.set(DataComponentRegistry.WARP_POS, warpPos);
    }

    public static DataComponentRegistry.WarpTarget getWarpBlock(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_BLOCK, new DataComponentRegistry.WarpTarget(null, null));
    }

    public static void setWarpBlock(ItemStack stack, BlockPos warpPos, BlockState blockState) {
        String blockName = blockState.getBlock().getName().getString();
        stack.set(DataComponentRegistry.WARP_BLOCK.get(), new DataComponentRegistry.WarpTarget(warpPos, blockName));
    }

    public static DataComponentRegistry.WarpTarget getWarpEntity(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_ENTITY, new DataComponentRegistry.WarpTarget(null, null));
    }

    public static void setWarpEntity(ItemStack stack, Entity entity) {
        stack.set(DataComponentRegistry.WARP_ENTITY.get(),
                new DataComponentRegistry.WarpTarget(entity.blockPosition(), entity.getName().getString()));
    }

    public static DataComponentRegistry.WarpTarget getWarpPainting(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_PAINTING, new DataComponentRegistry.WarpTarget(null, null));
    }

    public static void setWarpPainting(ItemStack stack, Painting painting) {
        if (painting.getVariant().getKey() != null)
            stack.set(DataComponentRegistry.WARP_PAINTING.get(),
                    new DataComponentRegistry.WarpTarget(painting.blockPosition(),
                            painting.getVariant().getKey().location().toLanguageKey("painting", "title")));
    }

    public static void setWarpMagicPainting(ItemStack stack, Entity painting) {
        if (MagicPaintingEntityProvider.getVariantString(painting) != null)
            stack.set(DataComponentRegistry.WARP_PAINTING.get(),
                    new DataComponentRegistry.WarpTarget(painting.blockPosition(),
                            MagicPaintingEntityProvider.getVariantString(painting)));
    }

    public static String getWarpDimension(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.WARP_DIMENSION.get(), "");
    }

    public static void setWarpDimension(ItemStack stack, String dimension) {
        stack.set(DataComponentRegistry.WARP_DIMENSION.get(), dimension);
    }

    public static UUID getWarpUUID(ItemStack stack) {
        UUID uuid = UUID.randomUUID();
        return stack.getOrDefault(DataComponentRegistry.WARP_UUID.get(), null);
    }

    public static UUID setWarpUUID(ItemStack stack, UUID warpUUID) {
        stack.set(DataComponentRegistry.WARP_UUID.get(), warpUUID);
        return warpUUID;
    }

    public static GlobalPos getGlobalWarpPos(ItemStack stack) {
        return stack.getOrDefault(DataComponentRegistry.GLOBAL_WARP_POS.get(), null);
    }

    public static void setGlobalWarpPos(ItemStack stack, GlobalPos globalPos) {
        stack.set(DataComponentRegistry.GLOBAL_WARP_POS.get(), globalPos);
    }


    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    public void spawnParticles(Level world, BlockPos pos, ParticleOptions particleOptions) {
        if (world.isClientSide()) {
            RandomSource random = world.getRandom();

            for (int i = 0; i < 40; ++i) {
                world.addParticle(particleOptions,
                        pos.getX() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)), pos.getY() + 1.5D,
                        pos.getZ() + 0.5D + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    public void spawnParticles(Level world, Entity entity, BlockPos pos, ParticleOptions particleOptions) {
        if (world.isClientSide()) {
            RandomSource random = world.getRandom();

            for (int i = 0; i < 40; ++i) {
                world.addParticle(particleOptions,
                        pos.getX() + entity.getBbWidth() + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        pos.getY() + entity.getBbHeight(),
                        pos.getZ() + entity.getBbWidth() + (0.5D * (random.nextBoolean() ? 1 : -1)),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        }
    }

    public void linkEntity(ItemStack stack, Player player, Entity target, Level world, BlockPos pos) {
        float pitch = 0.9F + world.random.nextFloat() * 0.2F;

        boolean isImmersivePainting = target.getType() == CompatRegistry.IMMERSIVE_GLOW_GRAFFITI.get()
                || target.getType() == CompatRegistry.IMMERSIVE_GLOW_PAINTING.get()
                || target.getType() == CompatRegistry.IMMERSIVE_GRAFFITI.get()
                || target.getType() == CompatRegistry.IMMERSIVE_PAINTING.get();

        boolean isMagicPainting = target.getType() == CompatRegistry.MAGIC_PAINTING.get();

        if (stack.getItem() instanceof LinkerItem linker) {
            if (!player.isCreative() && ConfigRegistry.CREATIVE_WRENCH_LINKING.get()
                    && !ConfigRegistry.DISABLE_WARP_PAINTINGS.get()) {
                player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.requires_creative"), true);
                player.swing(player.getUsedItemHand());
            } else if (!ConfigRegistry.DISABLE_WARP_PAINTINGS.get() || stack.is(ItemRegistry.CREATIVE_WRENCH)) {
                if (player.isShiftKeyDown()) {
                    UUID uuid = target.getUUID();

                    if (world instanceof ServerLevel serverWorld) {
                        if (target.getData(DataAttachmentRegistry.IS_WAXED.get()) && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
                            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.waxed",
                                    target.getName()).withStyle(ChatFormatting.GOLD), true);
                        } else if (!player.isCreative() && !stack.is(ItemRegistry.CREATIVE_WRENCH)
                                && target.getData(DataAttachmentRegistry.WARP_FUEL_COUNT.get()) < ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt()) {
                            if (target instanceof Painting painting && painting.getVariant().getKey() != null)
                                player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.painting_fuel_required",
                                                Component.translatable(painting.getVariant().getKey().location().toLanguageKey("painting", "title")),
                                                target.getName(), ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt() - target.getData(DataAttachmentRegistry.WARP_FUEL_COUNT.get()))
                                        .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), true);
                            else player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.entity_fuel_required",
                                            target.getName(), ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt() - target.getData(DataAttachmentRegistry.WARP_FUEL_COUNT.get()))
                                    .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), true);
                        } else if (!getIsBound(stack)) {
                            if (target instanceof Painting painting) {
                                int width = painting.getVariant().value().width();
                                Direction direction = target.getDirection();
                                WarpLinkableEntity.setWarpPos(uuid, pos, direction, width);
                                setWarpPos(stack, pos);
                                setWarpEntity(stack, painting);

                                if (painting.getVariant().getKey() != null) {
                                    player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.bound_painting",
                                            Component.translatable(painting.getVariant().getKey().location().toLanguageKey("painting", "title")),
                                            target.getName()).withStyle(ChatFormatting.GREEN), true);
                                    setWarpPainting(stack, painting);
                                }
                            } else if (ModList.get().isLoaded("immersive_paintings") && isImmersivePainting) {
                                int width = PaintingEntityProvider.getPaintingWidth(target);
                                Direction direction = target.getDirection();
                                WarpLinkableEntity.setWarpPos(uuid, pos, direction, width);
                                setWarpPos(stack, pos);
                                setWarpEntity(stack, target);

                                player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.bound",
                                        target.getName()).withStyle(ChatFormatting.GREEN), true);
                            } else if (ModList.get().isLoaded("twilightforest") && isMagicPainting) {
                                int width = PaintingEntityProvider.getPaintingWidth(target);
                                Direction direction = target.getDirection();
                                WarpLinkableEntity.setWarpPos(uuid, pos, direction, width);
                                setWarpPos(stack, pos);
                                setWarpEntity(stack, target);

                                if (MagicPaintingEntityProvider.getVariantString(target) != null) {
                                    player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.bound_painting",
                                            Component.translatable(MagicPaintingEntityProvider.getVariantString(target)),
                                            target.getName()).withStyle(ChatFormatting.GREEN), true);
                                    setWarpMagicPainting(stack, target);
                                }
                            } else {
                                WarpLinkableEntity.setWarpPos(uuid, pos, Direction.NORTH, 1);
                                setWarpPos(stack, pos);
                                player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.bound",
                                        target.getName()).withStyle(ChatFormatting.GREEN), true);
                            }

                            setWarpDimension(stack, target.level().dimension().toString());
                            setWarpUUID(stack, uuid);
                            setBound(stack, true);
                            stack.remove(DataComponentRegistry.WARP_BLOCK.get());

                            WarpLinkableEntity.WARP_ENTITY_LOCATIONS.put(pos, target);

                            ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.ENCHANT, serverWorld, target, 0.5, 128);
                            linker.playSound(world, pos, SoundRegistry.WRENCH_WARP_LINKED.get(), SoundSource.PLAYERS, 1.0F, 0.1F);
                        } else {
                            BlockPos warpPos = getWarpPos(stack);
                            UUID warpUUID = getWarpUUID(stack);
                            //  if (dimension.equals(getWarpDimension(stack))) {
                            Entity warpEntity = serverWorld.getEntity(warpUUID);
                            if (warpEntity == null) {
                                WarpLinkableEntity.WarpTarget warpTarget = WarpLinkableEntity.WARP_LOCATIONS.get(warpUUID);
                                if (warpTarget != null)
                                    warpPos = warpTarget.pos();
                            }

                            if (target instanceof Painting painting) {
                                int width = painting.getVariant().value().width();
                                WarpLinkableEntity.setWarpPos(warpUUID, warpPos, target.getDirection(), width);

                                if (warpEntity instanceof Painting warpPainting && warpPainting.getVariant().getKey() != null
                                        && painting.getVariant().getKey() != null)
                                    player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.linked_warp_painting",
                                            Component.translatable(painting.getVariant().getKey().location().toLanguageKey("painting", "title")),
                                            target.getName(), Component.translatable(warpPainting.getVariant().getKey().location().toLanguageKey("painting", "title")),
                                            warpPainting.getName()).withStyle(ChatFormatting.GOLD), true);
                            } else if (ModList.get().isLoaded("immersive_paintings") && isImmersivePainting) {
                                int width = PaintingEntityProvider.getPaintingWidth(target);
                                WarpLinkableEntity.setWarpPos(warpUUID, warpPos, target.getDirection(), width);

                                if (warpEntity != null)
                                    player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.linked_warp_block",
                                            target.getName(), warpEntity.getName()).withStyle(ChatFormatting.GOLD), true);
                            } else if (ModList.get().isLoaded("twilightforest") && isMagicPainting) {
                                int width = MagicPaintingEntityProvider.getPaintingWidth(target);
                                WarpLinkableEntity.setWarpPos(warpUUID, warpPos, target.getDirection(), width);

                                if (MagicPaintingEntityProvider.getVariantString(warpEntity) != null)
                                    player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.linked_warp_painting",
                                            Component.translatable(MagicPaintingEntityProvider.getVariantString(warpEntity)),
                                            target.getName(), Component.translatable(MagicPaintingEntityProvider.getVariantString(warpEntity)),
                                            warpEntity.getName()).withStyle(ChatFormatting.GOLD), true);
                            } else if (warpEntity != null)
                                player.displayClientMessage(Component.translatable(stack.getDescriptionId() + ".message.linked_warp_block",
                                    target.getName(), warpEntity.getName()).withStyle(ChatFormatting.GOLD), true);

                            linker.link(stack, warpEntity, target, warpPos);

                            // TODO: Fix paintings not linking in unloaded chunks
//                            if (target.level() instanceof ServerLevel && target.getServer() != null) {
//                                final ServerLevel serverLevel = target.getServer().getLevel(target.level().dimension());
//                                if (serverLevel != null) {
//                                    ChunkAccess chunk = serverLevel.getChunk(warpPos.getX() >> 4, warpPos.getZ() >> 4, ChunkStatus.FULL, true);
//                                    serverLevel.getChunk(warpPos);
//
//                                    final AABB box = new AABB(warpPos).inflate(1);
//                                    final List<Painting> list = serverLevel.getEntitiesOfClass(Painting.class, box);
//                                    for (final Painting warpPainting : list) {
//                                        linker.link(stack, warpPainting, target, warpPos);
//                                    }
//                                }
//                            }

                            ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.ENCHANT, serverWorld, target, 0.5, 128); // TODO: fix pos
                            linker.playSound(world, pos, SoundRegistry.WRENCH_WARP_CREATED.get(), SoundSource.BLOCKS, 1.0F, pitch);
                            //  }
                            setBound(stack, false);
                        }
                    }
                    player.swing(player.getUsedItemHand());
                }
            }
        }
    }
}