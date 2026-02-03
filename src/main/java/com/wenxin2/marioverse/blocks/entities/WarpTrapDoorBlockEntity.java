package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.utils.BlockWarpEntityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class WarpTrapDoorBlockEntity extends BaseWarpBlockEntity {
    public static final String BREAK_TRAPDOOR = "BreakTrapdoor";
    public boolean breakTrapdoor;

    public WarpTrapDoorBlockEntity(final BlockPos pos, final BlockState state) {
        this(BlockEntityRegistry.WARP_TRAPDOOR_BLOCK_ENTITY.get(), pos, state);
    }

    public WarpTrapDoorBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return this.getType().isValid(state) || state.getBlock() instanceof TrapDoorBlock;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(BREAK_TRAPDOOR))
            this.breakTrapdoor = tag.getBoolean(BREAK_TRAPDOOR);
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putBoolean(BREAK_TRAPDOOR, this.breakTrapdoor);
    }

    public void setBreakTrapdoor(boolean breakTrapdoor) {
        this.breakTrapdoor = breakTrapdoor;
    }

    public static void breakTrapdoor(BlockPos warpPos, Level world) {
        if (!world.isClientSide) {
            if (world.getBlockState(warpPos).getBlock() instanceof TrapDoorBlock)
                world.destroyBlock(warpPos, true);
        }
    }

    public static void warp(Entity entity, BlockPos warpPos, Level world, BlockState state, TrapDoorBlock trapdoorBlock, BaseWarpBlockEntity warpBE) {
        Entity passengerEntity = entity.getControllingPassenger();

        if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (!(world.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity)
                    && entity instanceof Player player)
                BlockWarpEntityHandler.displayDestinationMissingMessage(player);

            if (entity instanceof Player player) {
                if (state.getBlock() instanceof TrapDoorBlock)
                    warpBE.playTrapdoorSounds(null, world, warpPos, state.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
                entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_TRAPDOOR_COOLDOWN.get());
                if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
            } else {
                if (state.getBlock() instanceof TrapDoorBlock)
                    warpBE.playTrapdoorSounds(entity, world, warpPos, state.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
                entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
                entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, ConfigRegistry.WARP_TRAPDOOR_COOLDOWN.get());
                if (passengerEntity instanceof Player player) {
                    if (ConfigRegistry.BLINDNESS_EFFECT.get() && !world.isClientSide())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    entity.unRide();
                }
            }
        }

        markEntityTeleported(entity);
        world.gameEvent(GameEvent.TELEPORT, warpPos, GameEvent.Context.of(entity));
    }
}
