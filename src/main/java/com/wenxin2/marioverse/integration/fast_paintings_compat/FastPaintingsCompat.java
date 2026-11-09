package com.wenxin2.marioverse.integration.fast_paintings_compat;

import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.items.LinkerItem;
import com.wenxin2.marioverse.items.WarpDisruptorItem;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.EntityWarpEntityHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import com.wenxin2.marioverse.utils.WarpLinkParticles;
import com.wenxin2.marioverse.world.PendingWarpLinkSavedData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.mehvahdjukaar.fastpaintings.PaintingBlock;
import net.mehvahdjukaar.fastpaintings.PaintingBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FastPaintingsCompat {
    public static final String PAINTING_BLOCK_TYPE = "painting";
    private static final double ENTRY_REACH = 0.1;

    public static boolean isPaintingBlock(BlockState state) {
        return state.getBlock() instanceof PaintingBlock;
    }

    @Nullable
    public static PaintingBlockWarp getWarp(Level level, BlockPos pos) {
        if (!level.isLoaded(pos))
            return null;

        BlockState state = level.getBlockState(pos);
        if (!FastPaintingsCompat.isPaintingBlock(state))
            return null;

        PaintingBlockEntity master = PaintingBlock.getMaster(state, pos, level);
        if (master == null || master.getVariant() == null)
            return null;

        Holder<PaintingVariant> variant = master.getVariant();
        int width = variant.value().width();
        int height = variant.value().height();
        Direction facing = master.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos basePos = master.getBlockPos().below(height / 2).relative(facing.getCounterClockWise(), (width - 1) / 2);

        return new PaintingBlockWarp(level, master, basePos, facing, variant);
    }

    @Nullable
    public static PaintingBlockWarp findWarp(Level level, BlockPos pos, @Nullable UUID uuid) {
        if (uuid == null)
            return null;

        PaintingBlockWarp warp = FastPaintingsCompat.getWarp(level, pos);
        return warp != null && uuid.equals(warp.getUUID()) ? warp : null;
    }

    @Nullable
    public static PaintingBlockWarp getTouchedWarp(Entity entity, Level level) {
        if (level.isClientSide)
            return null;

        AABB reach = entity.getBoundingBox().inflate(ENTRY_REACH, 0.0, ENTRY_REACH);
        PaintingBlockWarp warp = FastPaintingsCompat.getTouchedWarp(level, reach);
        if (warp != null || !SableProvider.isLoaded())
            return warp;

        for (AABB localReach : SableProvider.toLocalBoxes(level, reach)) {
            warp = FastPaintingsCompat.getTouchedWarp(level, localReach);
            if (warp != null)
                return warp;
        }
        return null;
    }

    public static List<PaintingBlockWarp> getLinkedWarpsIn(Level level, AABB bounds) {
        int minX = Mth.floor(bounds.minX);
        int minY = Math.max(Mth.floor(bounds.minY), level.getMinBuildHeight());
        int minZ = Mth.floor(bounds.minZ);
        int maxX = Mth.floor(bounds.maxX);
        int maxY = Math.min(Mth.floor(bounds.maxY), level.getMaxBuildHeight() - 1);
        int maxZ = Mth.floor(bounds.maxZ);
        Map<BlockPos, PaintingBlockWarp> warps = new LinkedHashMap<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int chunkX = minX >> 4; chunkX <= maxX >> 4; chunkX++) {
            for (int chunkZ = minZ >> 4; chunkZ <= maxZ >> 4; chunkZ++) {
                if (!level.hasChunk(chunkX, chunkZ))
                    continue;

                LevelChunk chunk = level.getChunk(chunkX, chunkZ);

                for (int sectionY = minY >> 4; sectionY <= maxY >> 4; sectionY++) {
                    int index = chunk.getSectionIndexFromSectionY(sectionY);
                    if (index < 0 || index >= chunk.getSectionsCount())
                        continue;

                    LevelChunkSection section = chunk.getSection(index);
                    if (section.hasOnlyAir() || !section.maybeHas(FastPaintingsCompat::isPaintingBlock))
                        continue;

                    for (int x = Math.max(minX, chunkX << 4); x <= Math.min(maxX, (chunkX << 4) + 15); x++) {
                        for (int y = Math.max(minY, sectionY << 4); y <= Math.min(maxY, (sectionY << 4) + 15); y++) {
                            for (int z = Math.max(minZ, chunkZ << 4); z <= Math.min(maxZ, (chunkZ << 4) + 15); z++) {
                                BlockState state = section.getBlockState(x & 15, y & 15, z & 15);
                                if (!FastPaintingsCompat.isPaintingBlock(state))
                                    continue;

                                PaintingBlockEntity master = PaintingBlock.getMaster(state, pos.set(x, y, z), level);
                                if (master == null || warps.containsKey(master.getBlockPos()))
                                    continue;

                                PaintingBlockWarp warp = FastPaintingsCompat.getWarp(level, master.getBlockPos());
                                if (warp != null && warp.mv$getWarpUUID() != null && !warp.isPreventWarp())
                                    warps.put(master.getBlockPos(), warp);
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(warps.values());
    }

    @Nullable
    public static AABB getPaintingBounds(Level level, BlockPos pos) {
        PaintingBlockWarp warp = FastPaintingsCompat.getWarp(level, pos);
        if (warp == null)
            return null;

        AABB bounds = null;
        Direction alongPainting = warp.getDirection().getCounterClockWise();

        for (int down = 0; down < warp.getHeight(); down++) {
            for (int right = 0; right < warp.getWidth(); right++) {
                BlockPos part = warp.getMasterPos().below(down).relative(alongPainting, right);
                BlockState state = level.getBlockState(part);
                if (!FastPaintingsCompat.isPaintingBlock(state))
                    continue;

                VoxelShape shape = state.getShape(level, part);
                if (shape.isEmpty())
                    continue;

                AABB partBounds = shape.bounds().move(part);
                bounds = bounds == null ? partBounds : bounds.minmax(partBounds);
            }
        }
        return bounds;
    }

    public static void transferWarpData(Painting painting) {
        Level level = painting.level();
        if (level.isClientSide || !(painting instanceof WarpLinkableEntity linkable))
            return;

        boolean hasWarpData = linkable.mv$getWarpUUID() != null || linkable.mv$hasDestinationPos()
                || painting.getData(DataAttachmentRegistry.PREVENT_WARP) || painting.getData(DataAttachmentRegistry.BREAK)
                || painting.getData(DataAttachmentRegistry.IS_WAXED) || painting.getData(DataAttachmentRegistry.WARP_FUEL_COUNT) > 0;
        if (!hasWarpData)
            return;

        PaintingBlockWarp warp = FastPaintingsCompat.getWarp(level, painting.getPos());
        if (warp == null)
            return;

        ResourceKey<Level> dimension = linkable.mv$getDestinationDim();
        warp.setWarpData(new PaintingWarpData(painting.getUUID(), linkable.mv$getWarpUUID(), linkable.mv$getDestinationPos(),
                dimension != null ? dimension.location().toString() : null, linkable.mv$getWarpTarget(),
                linkable.mv$getWarpSubLevel(), linkable.mv$getWarpSubLevelPos()));
        warp.setPreventWarp(painting.getData(DataAttachmentRegistry.PREVENT_WARP));
        warp.setBreak(painting.getData(DataAttachmentRegistry.BREAK));
        warp.setWaxed(painting.getData(DataAttachmentRegistry.IS_WAXED));
        warp.setFuelCount(painting.getData(DataAttachmentRegistry.WARP_FUEL_COUNT));
        WarpLinkableEntity.setWarpPos(painting.getUUID(), warp.getBasePos(), warp.getDirection(), warp.getWidth());
    }

    public static void removePainting(PaintingBlockWarp warp) {
        Level level = warp.getLevel();
        BlockPos masterPos = warp.getMasterPos();
        Direction alongPainting = warp.getDirection().getCounterClockWise();

        level.removeBlockEntity(masterPos);
        for (int down = 0; down < warp.getHeight(); down++) {
            for (int right = 0; right < warp.getWidth(); right++) {
                BlockPos part = masterPos.below(down).relative(alongPainting, right);
                BlockState state = level.getBlockState(part);

                if (FastPaintingsCompat.isPaintingBlock(state))
                    level.setBlock(part, state.getFluidState().createLegacyBlock(), Block.UPDATE_ALL);
            }
        }
    }

    public static boolean isBoundToPainting(ItemStack stack) {
        return stack.has(DataComponentRegistry.WARP_BLOCK.get())
                && FastPaintingsCompat.PAINTING_BLOCK_TYPE.equals(LinkerItem.getWarpBlockType(stack));
    }

    public static boolean useLinker(LinkerItem linker, ItemStack stack, Level level, Player player, BlockPos pos, BlockState state,
                                    InteractionHand hand) {
        boolean clickedPainting = FastPaintingsCompat.isPaintingBlock(state);
        boolean boundToPainting = LinkerItem.getIsBound(stack) && FastPaintingsCompat.isBoundToPainting(stack);

        if (!clickedPainting && !(boundToPainting && level.getBlockEntity(pos) instanceof BaseWarpBlockEntity
                && LinkerItem.getLinkableBlock(state)))
            return false;
        if (!(level instanceof ServerLevel serverLevel))
            return true;

        if (clickedPainting)
            FastPaintingsCompat.useLinkerOnPainting(linker, stack, serverLevel, player, pos, hand);
        else FastPaintingsCompat.linkBoundPaintingToBlock(linker, stack, serverLevel, player, pos, state, hand);
        return true;
    }

    public static void linkBoundPaintingToEntity(LinkerItem linker, ItemStack stack, ServerLevel level, Player player, Entity target) {
        PaintingBlockWarp painting = FastPaintingsCompat.getBoundPainting(level, stack);

        if (painting != null) {
            FastPaintingsCompat.linkPaintingAndEntity(painting, target);
            FastPaintingsCompat.sendLinkedMessage(linker, player, FastPaintingsCompat.getEntityTitle(target), target.getName(),
                    painting.getTitle(), painting.getName());
            WarpLinkParticles.outlineEntity(level, target, null);
            WarpLinkParticles.outlineWarp(level, painting.getMasterPos(), null);
        } else if (target instanceof WarpLinkableEntity linkable) {
            UUID paintingUuid = LinkerItem.getWarpUUID(stack);
            WarpLinkableEntity.WarpTarget paintingTarget = paintingUuid != null ? WarpLinkableEntity.getWarpPos(paintingUuid) : null;

            linkable.mv$setDestinationPos(paintingTarget != null ? paintingTarget.pos() : LinkerItem.getWarpPos(stack));
            linkable.mv$setDestinationDim(FastPaintingsCompat.parseDimension(LinkerItem.getWarpDimension(stack)));
            linkable.mv$setWarpUuid(paintingUuid);
            linkable.mv$setWarpTarget(paintingTarget);
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_unloaded_painting",
                    target.getName()).withStyle(ChatFormatting.GOLD), true);
        }

        linker.playSound(level, target.blockPosition(), SoundRegistry.WRENCH_WARP_CREATED.get(), SoundSource.BLOCKS, 1.0F,
                0.9F + level.random.nextFloat() * 0.2F);
        linker.clearItemComponents(stack);
        LinkerItem.setBound(stack, false);
    }

    public static boolean disrupt(WarpDisruptorItem disruptor, Level level, BlockPos pos, BlockState state, @Nullable Player player,
                                  ItemStack stack, InteractionHand hand) {
        if (!FastPaintingsCompat.isPaintingBlock(state))
            return false;
        if (!(level instanceof ServerLevel serverLevel))
            return true;

        PaintingBlockWarp warp = FastPaintingsCompat.getWarp(level, pos);
        boolean canDisrupt = !ConfigRegistry.DISABLE_WARP_PAINTINGS.get() || (player != null && player.isCreative());
        if (warp == null || !canDisrupt || (warp.isPreventWarp() && warp.isBreak()))
            return true;

        Component title = warp.getTitle();

        if (warp.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
            if (player != null)
                player.displayClientMessage((title != null
                        ? Component.translatable(disruptor.getDescriptionId() + ".message.waxed_painting", title, warp.getName())
                        : Component.translatable(disruptor.getDescriptionId() + ".message.waxed", warp.getName()))
                        .withStyle(ChatFormatting.GOLD), true);
        } else if (warp.isPreventWarp()) {
            ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.WARPED_SPORE, serverLevel, null, pos, 16);
            if (player != null)
                player.displayClientMessage((title != null
                        ? Component.translatable(disruptor.getDescriptionId() + ".message.break_painting", title, warp.getName())
                        : Component.translatable(disruptor.getDescriptionId() + ".message.despawn_entity", warp.getName()))
                        .withStyle(ChatFormatting.DARK_AQUA), true);
            warp.setBreak(true);
        } else {
            ServerParticleUtils.spawnThreeLayerBlockParticles(ParticleTypes.CRIMSON_SPORE, serverLevel, null, pos, 16);
            if (player != null)
                player.displayClientMessage((title != null
                        ? Component.translatable(disruptor.getDescriptionId() + ".message.prevent_painting_warp", title, warp.getName())
                        : Component.translatable(disruptor.getDescriptionId() + ".message.prevent_entity_warp", warp.getName()))
                        .withStyle(ChatFormatting.RED), true);
            warp.setPreventWarp(true);
        }

        if (player != null && !player.isCreative())
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
        return true;
    }

    public static boolean onRightClickPainting(Level level, BlockPos pos, BlockState state, Player player, ItemStack stack) {
        if (!FastPaintingsCompat.isPaintingBlock(state))
            return false;

        PaintingBlockWarp warp = FastPaintingsCompat.getWarp(level, pos);
        if (warp == null)
            return false;

        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (player.isSpectator()) {
            if (level instanceof ServerLevel && warp.mv$getWarpUUID() != null && !warp.isPreventWarp()
                    && player.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                ((EntityWarpEntityHandler) player).warp(player, level, warp);
            return true;
        }

        if ((!ConfigRegistry.DISABLE_WARP_PAINTINGS.get() || player.isCreative()) && stack.is(TagRegistry.CRAFTS_WARP_PAINTING)
                && (level.isClientSide || warp.getFuelCount() < ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt())) {
            if (level instanceof ServerLevel serverLevel) {
                int fuelCount = warp.getFuelCount() + 1;
                warp.setFuelCount(fuelCount);

                if (fuelCount < ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt())
                    level.playSound(null, pos, SoundRegistry.WARP_FUEL_FILLS.get(), SoundSource.BLOCKS, 1.0F, pitch);
                else level.playSound(null, pos, SoundRegistry.WARP_COMPLETED.get(), SoundSource.BLOCKS, 1.0F, pitch);

                ServerParticleUtils.spawnOneLayerBlockParticles(ParticleTypes.PORTAL, serverLevel, null, pos, 16);
                stack.consume(1, player);
            }
            return true;
        }

        if (stack.getItem() instanceof HoneycombItem && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()
                && (level.isClientSide || !warp.isWaxed())) {
            if (level instanceof ServerLevel serverLevel) {
                warp.setWaxed(true);
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleTypes.WAX_ON, serverLevel, pos, UniformInt.of(3, 5));
                level.playSound(null, pos, SoundEvents.HONEYCOMB_WAX_ON, SoundSource.BLOCKS, 1.0F, pitch);
                stack.consume(1, player);
            }
            return true;
        }
        return false;
    }

    private static void useLinkerOnPainting(LinkerItem linker, ItemStack stack, ServerLevel level, Player player, BlockPos pos,
                                            InteractionHand hand) {
        PaintingBlockWarp painting = FastPaintingsCompat.getWarp(level, pos);
        if (painting == null || (ConfigRegistry.DISABLE_WARP_PAINTINGS.get() && !stack.is(ItemRegistry.CREATIVE_WRENCH)))
            return;

        Component title = painting.getTitle();

        if (painting.isWaxed() && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.waxed",
                    painting.getName()).withStyle(ChatFormatting.GOLD), true);
            return;
        }

        int fuelRequired = ConfigRegistry.WARP_PAINTING_FUEL_AMT.getAsInt() - painting.getFuelCount();
        if (!player.isCreative() && !stack.is(ItemRegistry.CREATIVE_WRENCH) && fuelRequired > 0) {
            player.displayClientMessage((title != null
                    ? Component.translatable(linker.getDescriptionId() + ".message.painting_fuel_required", title, painting.getName(), fuelRequired)
                    : Component.translatable(linker.getDescriptionId() + ".message.entity_fuel_required", painting.getName(), fuelRequired))
                    .withStyle(ChatFormatting.RED).withStyle(ChatFormatting.BOLD), true);
            return;
        }

        if (!LinkerItem.getIsBound(stack)) {
            FastPaintingsCompat.bindPainting(linker, stack, level, player, painting);
            return;
        }

        if (FastPaintingsCompat.isBoundToPainting(stack))
            FastPaintingsCompat.linkBoundPaintingToPainting(linker, stack, level, player, painting);
        else if (stack.has(DataComponentRegistry.WARP_BLOCK.get()))
            FastPaintingsCompat.linkBoundBlockToPainting(linker, stack, level, player, painting, hand);
        else FastPaintingsCompat.linkBoundEntityToPainting(linker, stack, level, player, painting);

        linker.playSound(level, pos, SoundRegistry.WRENCH_WARP_CREATED.get(), SoundSource.BLOCKS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        linker.clearItemComponents(stack);
        LinkerItem.setBound(stack, false);
    }

    private static void bindPainting(LinkerItem linker, ItemStack stack, ServerLevel level, Player player, PaintingBlockWarp painting) {
        UUID uuid = painting.getOrCreateUUID();
        BlockPos masterPos = painting.getMasterPos();
        Component title = painting.getTitle();

        WarpLinkableEntity.setWarpPos(uuid, painting.getBasePos(), painting.getDirection(), painting.getWidth());
        LinkerItem.setWarpBlock(stack, masterPos, level.getBlockState(masterPos));
        stack.set(DataComponentRegistry.WARP_BLOCK_TYPE.get(), FastPaintingsCompat.PAINTING_BLOCK_TYPE);
        stack.remove(DataComponentRegistry.WARP_ENTITY.get());
        stack.remove(DataComponentRegistry.WARP_PAINTING.get());
        LinkerItem.setWarpPos(stack, masterPos);
        LinkerItem.setWarpDimension(stack, level.dimension().location().toString());
        LinkerItem.setWarpUUID(stack, uuid);
        LinkerItem.setBound(stack, true);

        player.displayClientMessage((title != null
                ? Component.translatable(linker.getDescriptionId() + ".message.bound_painting", title, painting.getName())
                : Component.translatable(linker.getDescriptionId() + ".message.bound", painting.getName()))
                .withStyle(ChatFormatting.GREEN), true);
        WarpLinkParticles.outlineWarp(level, masterPos, null);
        linker.playSound(level, masterPos, SoundRegistry.WRENCH_WARP_LINKED.get(), SoundSource.PLAYERS, 1.0F,
                0.1F + level.random.nextFloat() * 0.2F);
    }

    private static void linkBoundPaintingToPainting(LinkerItem linker, ItemStack stack, ServerLevel level, Player player,
                                                    PaintingBlockWarp painting) {
        PaintingBlockWarp first = FastPaintingsCompat.getBoundPainting(level, stack);

        if (first != null && first.getMasterPos().equals(painting.getMasterPos()))
            return;

        if (first != null) {
            FastPaintingsCompat.linkPaintings(level, first, painting);
            FastPaintingsCompat.sendLinkedMessage(linker, player, painting.getTitle(), painting.getName(), first.getTitle(), first.getName());
            WarpLinkParticles.outlineWarp(level, first.getMasterPos(), null);
        } else {
            UUID firstUuid = LinkerItem.getWarpUUID(stack);
            WarpLinkableEntity.WarpTarget firstTarget = firstUuid != null ? WarpLinkableEntity.getWarpPos(firstUuid) : null;

            painting.getOrCreateUUID();
            FastPaintingsCompat.setPaintingLink(painting, firstTarget != null ? firstTarget.pos() : LinkerItem.getWarpPos(stack),
                    FastPaintingsCompat.parseDimension(LinkerItem.getWarpDimension(stack)), firstUuid, firstTarget);
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_unloaded_painting",
                    painting.getName()).withStyle(ChatFormatting.GOLD), true);
        }
        WarpLinkParticles.outlineWarp(level, painting.getMasterPos(), null);
    }

    private static void linkBoundBlockToPainting(LinkerItem linker, ItemStack stack, ServerLevel level, Player player,
                                                 PaintingBlockWarp painting, InteractionHand hand) {
        BlockPos blockPos = LinkerItem.getWarpPos(stack);

        if (blockPos != null && level.getBlockEntity(blockPos) instanceof BaseWarpBlockEntity) {
            BlockState blockState = level.getBlockState(blockPos);
            FastPaintingsCompat.linkPaintingAndBlock(level, painting, blockPos, blockState);
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_warp_block",
                    painting.getName(), blockState.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
            WarpLinkParticles.outlineWarp(level, blockPos, null);
            stack.hurtAndBreak(2, player, LivingEntity.getSlotForHand(hand));
        } else {
            String blockType = LinkerItem.getWarpBlockType(stack).isEmpty() ? "warp" : LinkerItem.getWarpBlockType(stack);

            painting.getOrCreateUUID();
            FastPaintingsCompat.setPaintingLink(painting, blockPos, FastPaintingsCompat.parseDimension(LinkerItem.getWarpDimension(stack)),
                    LinkerItem.getWarpUUID(stack), null);
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_unloaded_" + blockType,
                    painting.getName()).withStyle(ChatFormatting.GOLD), true);
        }
        WarpLinkParticles.outlineWarp(level, painting.getMasterPos(), null);
    }

    private static void linkBoundEntityToPainting(LinkerItem linker, ItemStack stack, ServerLevel level, Player player,
                                                  PaintingBlockWarp painting) {
        UUID entityUuid = LinkerItem.getWarpUUID(stack);
        Entity entity = LinkerItem.findEntity(level, entityUuid);

        if (entity != null) {
            FastPaintingsCompat.linkPaintingAndEntity(painting, entity);
            FastPaintingsCompat.sendLinkedMessage(linker, player, painting.getTitle(), painting.getName(),
                    FastPaintingsCompat.getEntityTitle(entity), entity.getName());
            WarpLinkParticles.outlineEntity(level, entity, null);
        } else {
            WarpLinkableEntity.WarpTarget entityTarget = entityUuid != null ? WarpLinkableEntity.getWarpPos(entityUuid) : null;
            UUID paintingUuid = painting.getOrCreateUUID();

            FastPaintingsCompat.setPaintingLink(painting, entityTarget != null ? entityTarget.pos() : LinkerItem.getWarpPos(stack),
                    FastPaintingsCompat.parseDimension(LinkerItem.getWarpDimension(stack)), entityUuid, entityTarget);
            if (entityUuid != null)
                PendingWarpLinkSavedData.get(level.getServer()).addLink(entityUuid, new PendingWarpLinkSavedData.PendingLink(
                        painting.getBasePos(), level.dimension(), paintingUuid, painting.getTarget()));
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_unloaded_painting",
                    painting.getName()).withStyle(ChatFormatting.GOLD), true);
        }
        WarpLinkParticles.outlineWarp(level, painting.getMasterPos(), null);
    }

    private static void linkBoundPaintingToBlock(LinkerItem linker, ItemStack stack, ServerLevel level, Player player, BlockPos pos,
                                                 BlockState state, InteractionHand hand) {
        if (level.getBlockEntity(pos) instanceof BaseWarpBlockEntity warpBE && warpBE.isWaxed()
                && ConfigRegistry.WAX_DISABLES_WARP_LINKING.get()) {
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.waxed",
                    state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
            return;
        }

        PaintingBlockWarp painting = FastPaintingsCompat.getBoundPainting(level, stack);

        if (painting != null) {
            FastPaintingsCompat.linkPaintingAndBlock(level, painting, pos, state);
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_warp_block",
                    state.getBlock().getName(), painting.getName()).withStyle(ChatFormatting.GOLD), true);
            WarpLinkParticles.outlineWarp(level, painting.getMasterPos(), null);
        } else {
            UUID paintingUuid = LinkerItem.getWarpUUID(stack);
            WarpLinkableEntity.WarpTarget paintingTarget = paintingUuid != null ? WarpLinkableEntity.getWarpPos(paintingUuid) : null;

            if (paintingUuid != null && paintingTarget != null)
                LinkerItem.linkBlocksToEntity(level, paintingUuid, paintingTarget.pos(), paintingTarget.pos().getCenter(),
                        paintingTarget.direction(), paintingTarget.width(), pos, state);
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_unloaded_painting",
                    state.getBlock().getName()).withStyle(ChatFormatting.GOLD), true);
        }

        WarpLinkParticles.outlineWarp(level, pos, null);
        linker.playSound(level, pos, SoundRegistry.WRENCH_WARP_CREATED.get(), SoundSource.BLOCKS, 1.0F, 0.9F + level.random.nextFloat() * 0.2F);
        stack.hurtAndBreak(2, player, LivingEntity.getSlotForHand(hand));
        linker.clearItemComponents(stack);
        LinkerItem.setBound(stack, false);
    }

    private static void linkPaintings(ServerLevel level, PaintingBlockWarp first, PaintingBlockWarp second) {
        UUID firstUuid = first.getOrCreateUUID();
        UUID secondUuid = second.getOrCreateUUID();

        FastPaintingsCompat.setPaintingLink(first, second.getBasePos(), second.getLevel().dimension(), secondUuid, second.getTarget());
        FastPaintingsCompat.setPaintingLink(second, first.getBasePos(), first.getLevel().dimension(), firstUuid, first.getTarget());
        WarpLinkableEntity.setWarpPos(firstUuid, first.getBasePos(), first.getDirection(), first.getWidth());
        WarpLinkableEntity.setWarpPos(secondUuid, second.getBasePos(), second.getDirection(), second.getWidth());
        WarpLinkParticles.sendLinkVibration(level, first.getCenter(), second.getCenter());
    }

    private static void linkPaintingAndBlock(ServerLevel level, PaintingBlockWarp painting, BlockPos blockPos, BlockState blockState) {
        UUID paintingUuid = painting.getOrCreateUUID();

        LinkerItem.linkBlocksToEntity(level, paintingUuid, painting.getBasePos(), painting.getCenter(), painting.getDirection(),
                painting.getWidth(), blockPos, blockState);
        if (!(level.getBlockEntity(blockPos) instanceof BaseWarpBlockEntity warpBE))
            return;

        FastPaintingsCompat.setPaintingLink(painting, blockPos, level.dimension(), warpBE.getUUID(), null);
        WarpLinkableEntity.setWarpPos(paintingUuid, painting.getBasePos(), painting.getDirection(), painting.getWidth());
    }

    private static void linkPaintingAndEntity(PaintingBlockWarp painting, Entity entity) {
        if (!(entity instanceof WarpLinkableEntity linkable))
            return;

        UUID paintingUuid = painting.getOrCreateUUID();
        int width = EntityWarpEntityHandler.getPaintingWidth(entity);
        BlockPos entityPos = width > 0 && entity instanceof BlockAttachedEntity attached ? attached.getPos() : entity.blockPosition();
        WarpLinkableEntity.WarpTarget entityTarget = width > 0
                ? new WarpLinkableEntity.WarpTarget(entityPos, entity.getDirection(), width)
                : new WarpLinkableEntity.WarpTarget(entityPos, Direction.NORTH, 1);

        FastPaintingsCompat.setPaintingLink(painting, entityPos, entity.level().dimension(), entity.getUUID(), entityTarget);
        linkable.mv$setDestinationPos(painting.getBasePos());
        linkable.mv$setDestinationDim(painting.getLevel().dimension());
        linkable.mv$setWarpUuid(paintingUuid);
        linkable.mv$setWarpTarget(painting.getTarget());
        WarpLinkableEntity.updateWarpSubLevel(entity.level(), linkable, painting.getBasePos());
        WarpLinkableEntity.setWarpPos(entity.getUUID(), entityPos, entityTarget.direction(), entityTarget.width());
        WarpLinkableEntity.setWarpPos(paintingUuid, painting.getBasePos(), painting.getDirection(), painting.getWidth());

        if (SableProvider.isLoaded())
            SableProvider.recordEntityAnchor(entity);
        if (painting.getLevel() instanceof ServerLevel serverLevel)
            WarpLinkParticles.sendLinkVibration(serverLevel, painting.getCenter(), entity.position());
    }

    private static void setPaintingLink(PaintingBlockWarp painting, @Nullable BlockPos destination, @Nullable ResourceKey<Level> dimension,
                                        @Nullable UUID warpUuid, @Nullable WarpLinkableEntity.WarpTarget target) {
        painting.mv$setDestinationPos(destination);
        painting.mv$setDestinationDim(dimension);
        painting.mv$setWarpUuid(warpUuid);
        painting.mv$setWarpTarget(target);
        if (destination != null)
            WarpLinkableEntity.updateWarpSubLevel(painting.getLevel(), painting, destination);
    }

    @Nullable
    private static PaintingBlockWarp getBoundPainting(ServerLevel level, ItemStack stack) {
        BlockPos pos = LinkerItem.getWarpPos(stack);

        if (pos == null || !level.isInWorldBounds(pos)
                || !level.dimension().location().toString().equals(LinkerItem.getWarpDimension(stack)))
            return null;

        level.getChunkAt(pos);
        return FastPaintingsCompat.findWarp(level, pos, LinkerItem.getWarpUUID(stack));
    }

    private static void sendLinkedMessage(LinkerItem linker, Player player, @Nullable Component firstTitle, Component firstName,
                                          @Nullable Component secondTitle, Component secondName) {
        if (firstTitle != null && secondTitle != null)
            player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_warp_painting",
                    firstTitle, firstName, secondTitle, secondName).withStyle(ChatFormatting.GOLD), true);
        else player.displayClientMessage(Component.translatable(linker.getDescriptionId() + ".message.linked_warp_block",
                firstName, secondName).withStyle(ChatFormatting.GOLD), true);
    }

    @Nullable
    private static Component getEntityTitle(Entity entity) {
        if (entity instanceof Painting painting && painting.getVariant().getKey() != null)
            return Component.translatable(painting.getVariant().getKey().location().toLanguageKey("painting", "title"));
        return null;
    }

    @Nullable
    private static ResourceKey<Level> parseDimension(String dimension) {
        ResourceLocation location = dimension.isEmpty() ? null : ResourceLocation.tryParse(dimension);
        return location != null ? ResourceKey.create(Registries.DIMENSION, location) : null;
    }

    @Nullable
    private static PaintingBlockWarp getTouchedWarp(Level level, AABB reach) {
        BlockPos min = BlockPos.containing(reach.minX, reach.minY, reach.minZ);
        BlockPos max = BlockPos.containing(reach.maxX, reach.maxY, reach.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (!level.isLoaded(pos))
                continue;

            BlockState state = level.getBlockState(pos);
            if (!FastPaintingsCompat.isPaintingBlock(state))
                continue;

            VoxelShape shape = state.getShape(level, pos);
            if (shape.isEmpty() || !shape.bounds().move(pos).intersects(reach))
                continue;

            PaintingBlockWarp warp = FastPaintingsCompat.getWarp(level, pos.immutable());
            if (warp != null && warp.mv$getWarpUUID() != null && !warp.isPreventWarp())
                return warp;
        }
        return null;
    }
}
