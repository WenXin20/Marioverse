package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

public class WarpDoorBlockEntity extends BlockEntity {
    public static final String WARP_POS = "WarpPos";
    public static final String WARP_DIMENSION = "Dimension";
    public static final String WARP_UUID = "WarpUUID";
    public static final String UUID = "UUID";
    public static final String PREVENT_WARP = "PreventWarp";
    public static final String BREAK_DOOR = "BreakDoor";
    public static final String IS_WAXED = "IsWaxed";
    public BlockPos destinationPos;
    public String dimensionTag;
    public boolean preventWarp = Boolean.FALSE;
    public boolean breakDoor = Boolean.FALSE;
    public boolean isWaxed;
    public UUID uuid;
    public UUID warpUuid;

    public WarpDoorBlockEntity(final BlockPos pos, final BlockState state) {
        this(BlockEntityRegistry.WARP_DOOR_BLOCK_ENTITY.get(), pos, state);
    }

    public WarpDoorBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
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

    public UUID getUuid() {
        return this.uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public void setPreventWarp(boolean preventWarp) {
        this.preventWarp = preventWarp;
    }

    public void setBreakDoor(boolean breakDoor) {
        this.breakDoor = breakDoor;
    }

    public UUID getWarpUuid() {
        return this.warpUuid;
    }

    public void setWarpUuid(UUID uuid) {
        this.warpUuid = uuid;
        this.setChanged();
    }

    public void markUpdated() {
        this.setChanged();
        if (this.level != null)
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
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

        if (tag.contains(BREAK_DOOR))
            this.breakDoor = tag.getBoolean(BREAK_DOOR);

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
        tag.putBoolean(BREAK_DOOR, this.breakDoor);
        tag.putBoolean(PREVENT_WARP, this.preventWarp);
        tag.putBoolean(IS_WAXED, this.isWaxed);

        if (this.hasDestinationPos() && this.destinationPos != null)
            tag.put(WARP_POS, NbtUtils.writeBlockPos(this.destinationPos));

        if (this.dimensionTag != null)
            tag.putString(WARP_DIMENSION, this.dimensionTag);

        if (this.uuid != null)
            tag.putUUID(UUID, this.getUuid());

        if (this.warpUuid != null)
            tag.putUUID(WARP_UUID, this.getWarpUuid());
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

    // Store a map to track whether entities have teleported or not
    public static final Map<Integer, Boolean> teleportedEntities = new HashMap<>();

    // Method to mark an entity as teleported
    public static void markEntityTeleported(Entity entity) {
        if (entity != null) {
            teleportedEntities.put(entity.getId(), true);
        }
    }

    public static void warp(Entity entity, BlockPos warpPos, Level world, BlockState state) {
        if (world.getBlockState(warpPos).getBlock() instanceof DoorBlock doorBlock
                && world.getBlockEntity(warpPos) instanceof WarpDoorBlockEntity doorBlockEntity) {
            Entity passengerEntity = entity.getControllingPassenger();

            if (entity instanceof Player) {
                doorBlockEntity.playDoorSounds(null, world, warpPos, state.getValue(DoorBlock.OPEN), doorBlock.type());
                entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
                if (ConfigRegistry.BLINDNESS_EFFECT.get())
                    ((Player) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
            } else {
                doorBlockEntity.playDoorSounds(entity, world, warpPos, state.getValue(DoorBlock.OPEN), doorBlock.type());
                entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
                if (passengerEntity instanceof Player) {
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        ((Player) passengerEntity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    entity.unRide();
                }
            }
            if (doorBlockEntity.breakDoor)
                world.destroyBlock(warpPos.below(), Boolean.TRUE);
            markEntityTeleported(entity);
        }
        world.gameEvent(GameEvent.TELEPORT, warpPos, GameEvent.Context.of(entity));
    }

    public static BlockPos findMatchingUUID(UUID uuid, Level world, BlockPos pos) {
        BlockPos closestPos = null;
        double closestDistanceSq = Double.MAX_VALUE;
        int maxDistance = 64; // How far it searches for warp pipes with a matching UUID

        for (int x = -maxDistance; x <= maxDistance; x++) {
            for (int y = Math.max(-maxDistance, world.getMinBuildHeight() - pos.getY()); y <= Math.min(maxDistance, world.getMaxBuildHeight() - pos.getY()); y++) {
                for (int z = -maxDistance; z <= maxDistance; z++) {
                    BlockPos checkingPos = pos.offset(x, y, z);
                    BlockState blockState = world.getBlockState(checkingPos);
                    Block block = blockState.getBlock();

                    if (block instanceof DoorBlock && world.getBlockEntity(checkingPos) != null
                            && world.getBlockEntity(checkingPos) instanceof WarpDoorBlockEntity) {
                        BlockEntity blockEntity = world.getBlockEntity(checkingPos);

                        if (blockEntity instanceof WarpDoorBlockEntity warpDoorBlockEntity) {
                            UUID warpUUID = warpDoorBlockEntity.getWarpUuid();

                            if (uuid.equals(warpUUID)) {
                                double distanceSq = pos.distToCenterSqr(checkingPos.getX(), checkingPos.getY(), checkingPos.getZ());
                                if (distanceSq < closestDistanceSq) {
                                    closestPos = checkingPos.immutable();
                                    closestDistanceSq = distanceSq;
                                }
                            }
                        }
                    }
                }
            }
        }
        return closestPos;
    }
}
