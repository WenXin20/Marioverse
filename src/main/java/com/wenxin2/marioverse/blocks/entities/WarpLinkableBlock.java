package com.wenxin2.marioverse.blocks.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.Nullable;

public interface WarpLinkableBlock {
    String WARP_POS = "WarpPos";
    String WARP_DIMENSION = "Dimension";
    String WARP_UUID = "WarpUUID";
    String UUID = "UUID";
    String PREVENT_WARP = "PreventWarp";
    String IS_WAXED = "IsWaxed";

    Map<UUID, BlockPos> WARP_LOCATIONS = new HashMap<>();
    Map<Integer, Boolean> WARPED_ENTITIES = new HashMap<>();

    BaseWarpBlockEntity self();

    BlockPos getDestinationPos();
    void setDestinationPos(@Nullable BlockPos pos);
    boolean hasDestinationPos();

    String getDimensionTag();
    void setDimensionTag(String tag);

    boolean isPreventWarp();
    void setPreventWarp(boolean val);

    boolean isWaxed();
    void setWaxed(boolean waxed);

    UUID getWarpUuid();
    void setWarpUuid(UUID uuid);

    UUID getUUID();
    void setUUID(UUID uuid);

    default ResourceKey<Level> getDestinationDim() {
        String dimTag = getDimensionTag();
        if (dimTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(dimTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }

    default void setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null)
            setDimensionTag(dimension.location().toString());
        self().setChanged();
        if (self().getLevel() != null)
            self().getLevel().setBlock(self().getBlockPos(), self().getBlockState(), 4);
    }

    default void markUpdated() {
        if (self().getLevel() != null) {
            self().getLevel().sendBlockUpdated(self().getBlockPos(), self().getBlockState(), self().getBlockState(), Block.UPDATE_ALL);
        }
        self().setChanged();
    }

    default void onRemoved() {
        WARP_LOCATIONS.remove(getWarpUuid());
    }

    default void onWarpLoad(BlockPos pos) {
        if (getWarpUuid() != null) {
            BaseWarpBlockEntity.WARP_LOCATIONS.put(getWarpUuid(), pos);
        }
    }

    default void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    default void playDoorSounds(@Nullable Entity entity, Level world, BlockPos pos, boolean isOpen, BlockSetType type) {
        world.playSound(entity, pos, isOpen ? type.doorOpen() : type.doorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    default void playTrapdoorSounds(@Nullable Entity entity, Level world, BlockPos pos, boolean isOpen, BlockSetType type) {
        world.playSound(entity, pos, isOpen ? type.doorOpen() : type.doorClose(), SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    static void markEntityTeleported(Entity entity) {
        if (entity != null)
            WARPED_ENTITIES.put(entity.getId(), true);
    }

    static BlockPos findMatchingUUID(UUID uuid) {
        return WARP_LOCATIONS.getOrDefault(uuid, null);
    }
}