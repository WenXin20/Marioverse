package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class WarpDoorBlockEntity extends BaseWarpBlockEntity {
    public static final String BREAK_DOOR = "BreakDoor";
    public boolean breakDoor;

    public WarpDoorBlockEntity(final BlockPos pos, final BlockState state) {
        this(BlockEntityRegistry.WARP_DOOR_BLOCK_ENTITY.get(), pos, state);
    }

    public WarpDoorBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return this.getType().isValid(state) || state.getBlock() instanceof DoorBlock;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(BREAK_DOOR))
            this.breakDoor = tag.getBoolean(BREAK_DOOR);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean(BREAK_DOOR, this.breakDoor);
    }

    public void setBreakDoor(boolean breakDoor) {
        this.breakDoor = breakDoor;
    }

    public static void breakDoor(BlockPos warpPos, Level world) {
        if (!world.isClientSide) {
            if (world.getBlockState(warpPos).getBlock() instanceof DoorBlock)
                world.destroyBlock(warpPos, true);
        }
    }

    public static void warp(Entity entity, BlockPos warpPos, Level world, BlockState state, DoorBlock doorBlock, BaseWarpBlockEntity warpBE) {
        Entity passengerEntity = entity.getControllingPassenger();

        if (entity instanceof Player player && !player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (state.getBlock() instanceof DoorBlock)
                warpBE.playDoorSounds(null, world, warpPos, state.getValue(DoorBlock.OPEN), doorBlock.type());
            entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
            if (ConfigRegistry.BLINDNESS_EFFECT.get())
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
        } else if (!entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (state.getBlock() instanceof DoorBlock)
                warpBE.playDoorSounds(entity, world, warpPos, state.getValue(DoorBlock.OPEN), doorBlock.type());
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
}
