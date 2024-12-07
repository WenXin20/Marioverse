package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.init.BlockEntityRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WarpDoorBlockEntity extends BaseWarpBlockEntity implements GeoBlockEntity {
    protected static final RawAnimation APPEAR_ANIM = RawAnimation.begin().thenPlayAndHold("animation.warp_door.appear");
    protected static final RawAnimation DISAPPEAR_ANIM = RawAnimation.begin().thenPlayAndHold("animation.warp_door.disappear");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public boolean breakDoor = Boolean.FALSE;

    public WarpDoorBlockEntity(final BlockPos pos, final BlockState state) {
        this(BlockEntityRegistry.WARP_DOOR_BLOCK_ENTITY.get(), pos, state);
    }

    public WarpDoorBlockEntity(final BlockEntityType<?> tileEntity, BlockPos pos, BlockState state) {
        super(tileEntity, pos, state);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "switch", 5, this::switchAnimController));
    }

    protected <E extends GeoAnimatable> PlayState switchAnimController(final AnimationState<E> event) {
        BlockState state = this.getBlockState();

        if (state.getValue(DoorBlock.OPEN)) {
            event.setAndContinue(APPEAR_ANIM);
        } else {
            event.setAndContinue(DISAPPEAR_ANIM);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(BREAK_DOOR))
            this.breakDoor = tag.getBoolean(BREAK_DOOR);
        if (this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
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
