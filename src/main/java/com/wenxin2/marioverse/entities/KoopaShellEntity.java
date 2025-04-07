package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class KoopaShellEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_HIDE_FLAGS = SynchedEntityData.defineId(KoopaShellEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation ATTACK_SWING_LEFT = RawAnimation.begin().thenPlay("attack.swing.left");
    public static final RawAnimation ATTACK_SWING_RIGHT = RawAnimation.begin().thenPlay("attack.swing.right");
    public static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("move.emerge");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int hideTicks = -1;
    private int emergeAnimationTicks = -1;

    public KoopaShellEntity(EntityType<? extends KoopaShellEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(1);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.GOOMBA_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.GOOMBA_STOMP.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundRegistry.GOOMBA_STEP.get(), 1.0F, 1.0F);
    }

    protected SoundEvent getBumpSound() {
        return SoundRegistry.GOOMBA_BUMP.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_HIDE_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this).transitionLength(0));
        controllers.add(new AnimationController<>(this, "emerge_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("emerge", EMERGE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ID_HIDE_FLAGS, tag.getByte("HideFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("HideFlags", this.entityData.get(DATA_ID_HIDE_FLAGS));
    }

    @Override
    public void tick() {
        super.tick();
        int i = this.getAirSupply();

        if (hideTicks > 0 && this.getDeltaMovement().horizontalDistance() == 0)
            hideTicks--;

        if (hideTicks == 0 && emergeAnimationTicks <= 0) {
            this.triggerAnim("emerge_controller", "emerge");
            this.emergeAnimationTicks = 20;
        }

        if (emergeAnimationTicks > 0)
            emergeAnimationTicks--;

        if (!this.level().isClientSide && emergeAnimationTicks == 0) {
            KoopaTroopaEntity entity = new KoopaTroopaEntity(EntityRegistry.GREEN_KOOPA_TROOPA.get(), this.level());

            entity.setPos(this.getX(), this.getY(), this.getZ());
            entity.setYRot(this.getYRot());
            entity.setXRot(this.getXRot());
            entity.yBodyRot = this.yBodyRot;
            entity.setYHeadRot(this.getYHeadRot());
            entity.setHealth(this.getHealth());
            for (EquipmentSlot slot : EquipmentSlot.values())
                entity.setItemSlot(slot, this.getItemBySlot(slot).copy());

            AccessoriesCapability capability = AccessoriesCapability.get(this);
            if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                    && !this.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
                String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};
                for (String slotType : slotTypes) {
                    AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(this, slotType));
                    AccessoriesContainer containerEntity = capability.getContainer(SlotTypeLoader.getSlotType(entity, slotType));
                    if (container != null) {
                        ItemStack stack = container.getAccessories().getItem(0);
                        if (containerEntity != null)
                            containerEntity.getAccessories().setItem(0, stack);
                    }
                }
            }

            this.level().addFreshEntity(entity);
            this.remove(RemovalReason.DISCARDED);
        }

        this.handleAirSupply(i);
    }

    @Override
    public void aiStep() {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeRegistry.STOMP) || source.is(DamageTypeRegistry.PLAYER_STOMP)) {
            this.stopInPlace();
        }
        return super.hurt(source, amount);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new AmphibiousPathNavigation(this, world);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    protected void handleAirSupply(int airSupplyAmount) {
        if (this.isAlive() && this.isInWaterOrBubble()) {
            this.setAirSupply(airSupplyAmount);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

//    @Override
//    protected BodyRotationControl createBodyControl() {
//        return new BodyRotationControl(this) {
//            @Override
//            public void clientTick() {
//                if (!KoopaShellEntity.this.isHiding()) {
//                    super.clientTick();
//                }
//            }
//        };
//    }

    public void setHideTicks(int hideTicks) {
        this.hideTicks = hideTicks;
    }
}
