package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.registries.ConfigRegistry;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class BaseWarpBlockEntity extends BlockEntity implements WarpLinkableBlock {
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

    public static void warp(Entity entity, BlockPos warpPos, Level world) {
        Entity passengerEntity = entity.getControllingPassenger();

        if (entity instanceof Player player && !player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
            if (ConfigRegistry.BLINDNESS_EFFECT.get())
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
        } else if (!entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
            if (passengerEntity instanceof Player player && !player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                if (ConfigRegistry.BLINDNESS_EFFECT.get())
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                entity.unRide();
            }
        }

        markEntityTeleported(entity);
        world.gameEvent(GameEvent.TELEPORT, warpPos, GameEvent.Context.of(entity));
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

    @Override
    public BaseWarpBlockEntity self() {
        return this;
    }

    @Nullable
    @Override
    public BlockPos getDestinationPos() {
        if (this.destinationPos != null) {
            return this.destinationPos;
        }
        return null;
    }

    @Override
    public String getDimensionTag() {
        return dimensionTag;
    }

    @Override
    public void setDimensionTag(String tag) {
        dimensionTag = tag;
    }

    @Nullable
    @Override
    public ResourceKey<Level> getDestinationDim() {
        String dimTag = getDimensionTag();
        if (dimTag != null) {
            ResourceLocation location = ResourceLocation.tryParse(dimTag);
            if (location != null) {
                return ResourceKey.create(Registries.DIMENSION, location);
            }
        }
        return null;
    }

    @Override
    public void setDestinationDim(@Nullable ResourceKey<Level> dimension) {
        if (dimension != null)
            setDimensionTag(dimension.location().toString());
        this.setChanged();
        if (this.level != null)
            this.level.setBlock(self().getBlockPos(), self().getBlockState(), 4);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean isPreventWarp() {
        return this.preventWarp;
    }

    @Override
    public void setPreventWarp(boolean preventWarp) {
        this.preventWarp = preventWarp;
    }

    @Override
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
    public void setRemoved() {
        super.setRemoved();
        WARP_LOCATIONS.remove(this.getWarpUuid());
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
