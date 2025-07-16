package com.wenxin2.marioverse.blocks.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.jetbrains.annotations.NotNull;

public class BaseWarpBlockEntity extends BlockEntity {
    public static final String WARP_POS = "WarpPos";
    public static final String WARP_DIMENSION = "Dimension";
    public static final String WARP_UUID = "WarpUUID";
    public static final String UUID = "UUID";
    public static final String PREVENT_WARP = "PreventWarp";
    public static final String IS_WAXED = "IsWaxed";
    public BlockPos destinationPos;
    public String dimensionTag;
    public boolean preventWarp = Boolean.FALSE;
    public boolean isWaxed;
    public UUID uuid;
    public UUID warpUuid;
    public static final Map<UUID, BlockPos> WARP_LOCATIONS = new HashMap<>();
    // Store a map to track whether entities have teleported or not
    public static final Map<Integer, Boolean> WARPED_ENTITIES = new HashMap<>();

    public BaseWarpBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
    }

    public boolean isWaxed() {
        return this.isWaxed;
    }

    public void setWaxed(boolean isWaxed) {
        if (this.isWaxed != isWaxed) {
            this.isWaxed = isWaxed;
            this.markUpdated();
            this.getUpdatePacket();
        }
    }

    public boolean hasDestinationPos() {
        return this.destinationPos != null;
    }

    public void setDestinationPos(@Nullable BlockPos pos) {
        this.destinationPos = pos;
        this.setChanged();
        if (this.level != null && pos != null) {
            BlockState state = this.getBlockState();
            this.level.setBlock(this.getBlockPos(), state, 4);
        }
    }

    @Nullable
    public BlockPos getDestinationPos() {
        if (this.destinationPos != null) {
            return this.destinationPos;
        }
        return null;
    }

    @Nullable
    public ResourceKey<Level> getDestinationDim() {
        if (dimensionTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(dimensionTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }


    public void setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null)
            this.dimensionTag = dimension.location().toString();

        if (this.level != null)
            this.level.setBlock(this.getBlockPos(), this.getBlockState(), 4);
        this.setChanged();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public void setPreventWarp(boolean preventWarp) {
        this.preventWarp = preventWarp;
    }

    public UUID getWarpUuid() {
        return this.warpUuid;
    }

    public void setWarpUuid(UUID uuid) {
        this.warpUuid = uuid;
        this.setChanged();
        this.onLoad();
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.isWaxed = tag.getBoolean(IS_WAXED);

        if (tag.contains(WARP_POS)) {
            this.destinationPos = NbtUtils.readBlockPos(tag, WARP_POS).orElse(null);
            this.setDestinationPos(this.destinationPos);
        }

        if (tag.contains(WARP_DIMENSION))
            this.dimensionTag = tag.getString(WARP_DIMENSION);

        if (tag.contains(PREVENT_WARP))
            this.preventWarp = tag.getBoolean(PREVENT_WARP);

        if (tag.contains(UUID))
            this.uuid = tag.getUUID(UUID);

        if (tag.contains(WARP_UUID))
            this.warpUuid = tag.getUUID(WARP_UUID);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean(PREVENT_WARP, this.preventWarp);
        tag.putBoolean(IS_WAXED, this.isWaxed);

        if (this.hasDestinationPos() && this.destinationPos != null)
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.destinationPos));

        if (this.dimensionTag != null)
            tag.putString(WARP_DIMENSION, this.dimensionTag);

        if (this.uuid != null)
            tag.putUUID(UUID, this.getUUID());

        if (this.warpUuid != null)
            tag.putUUID(WARP_UUID, this.getWarpUuid());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        WARP_LOCATIONS.put(this.getWarpUuid(), this.getBlockPos());
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);

        this.saveAdditional(tag, provider);
        return tag;
    }

    public void playSound(Level world, BlockPos pos, SoundEvent soundEvent, SoundSource source, float volume, float pitch) {
        world.playSound(null, pos, soundEvent, source, volume, pitch);
    }

    public void playDoorSounds(@Nullable Entity entity, Level world, BlockPos pos, boolean isOpen, BlockSetType type) {
        world.playSound(entity, pos, isOpen ? type.doorOpen() : type.doorClose(), SoundSource.BLOCKS, 1.0F,
                world.getRandom().nextFloat() * 0.1F + 0.9F
        );
    }

    public void playTrapdoorSounds(@Nullable Entity entity, Level world, BlockPos pos, boolean isOpen, BlockSetType type) {
        world.playSound(entity, pos, isOpen ? type.doorOpen() : type.doorClose(), SoundSource.BLOCKS, 1.0F,
                world.getRandom().nextFloat() * 0.1F + 0.9F
        );
    }

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null)
            WARPED_ENTITIES.put(entity.getId(), true);
    }

    public static BlockPos findMatchingUUID(UUID uuid) {
        return WARP_LOCATIONS.getOrDefault(uuid, null);
    }
}
