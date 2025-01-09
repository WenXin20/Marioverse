package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.DamageSourceRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PiranhaPlantEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_HIDE_FLAGS = SynchedEntityData.defineId(PiranhaPlantEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation CONSTANT_BITES_ANIM = RawAnimation.begin().thenLoop("piranha_plant.constant_bite");
    public static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("piranha_plant.death");
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("piranha_plant.idle");
    public static final RawAnimation SQUASH_ANIM = RawAnimation.begin().thenPlayAndHold("piranha_plant.squash");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PiranhaPlantEntity(EntityType<? extends PiranhaPlantEntity> type, Level world) {
        super(type, world);
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
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundRegistry.GOOMBA_STEP.get(), 1.0F, 1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_HIDE_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6D, true));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.PIRANHA_PLANT_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Death", 5, this::squashAnimController));
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Squash", 5, this::squashAnimController));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(5.0D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (!(collidingEntity instanceof PiranhaPlantEntity)
                        || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK))
                    event.setAndContinue(CONSTANT_BITES_ANIM);
            }
        } else event.setAndContinue(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    protected <E extends GeoAnimatable> PlayState squashAnimController(final AnimationState<E> event) {
        if (this.dead) {
            if (this.getLastDamageSource() != null
                && (this.getLastDamageSource().is(DamageSourceRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageSourceRegistry.PLAYER_STOMP))) {
                event.setAndContinue(SQUASH_ANIM);
                return PlayState.CONTINUE;
            } else {
                event.setAndContinue(DEATH_ANIM);
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
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
        this.checkForCollisions();
        this.hideInBlock();

        if (this.isInWaterOrBubble())
            this.ejectPassengers();
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        this.handleAirSupply(i);
    }

    public static boolean checkPiranhaPlantSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                      MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isInWall() {
        if (isHiding()) {
            return false;
        } else return super.isInWall();
    }

    @Override
    protected boolean wouldNotSuffocateAtTargetPose(Pose pose) {
        AABB aabb = this.getDimensions(pose).makeBoundingBox(this.position());
        return this.level().noBlockCollision(this, aabb) || this.isHiding();
    }

    @NotNull
    @Override
    protected AABB makeBoundingBox() {
        AABB originalBoundingBox = super.makeBoundingBox();

        if (isHiding()) {
            double height = originalBoundingBox.getYsize() * 0.5;
            return new AABB(originalBoundingBox.minX, originalBoundingBox.minY, originalBoundingBox.minZ,
                    originalBoundingBox.maxX, originalBoundingBox.maxY * 0.5, originalBoundingBox.maxZ);
        } else return super.makeBoundingBox();
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @NotNull
    @Override
    protected Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight() - 0.5D, this.getBbWidth() * 0.4F);
    }

    private float currentScale = 1.0F;
    private static final float SCALING_SPEED = 0.1F;

    public void hideInBlock() {
        AttributeInstance scale = this.getAttribute(Attributes.SCALE);
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockPos posAbove = pos.above();
        BlockPos posBelow = pos.below();

        double speed = 0.02;
        float targetScale = this.isHiding() ? 0.3F : 1.0F;
        currentScale = Mth.lerp(SCALING_SPEED, currentScale, targetScale);

        if (scale != null && scale.getBaseValue() != currentScale)
            scale.setBaseValue(currentScale);

        if (this.isHiding() && !world.getBlockState(pos).is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                && !world.getBlockState(posBelow).is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {
            this.stopHiding();
            this.setNoGravity(false);
            this.noPhysics = false;
            return;
        }

        if (this.isHiding()) {
            if (world.getGameTime() % ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get() == 0L &&
                    world.getBlockState(pos).is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE) &&
                    !world.getBlockState(posAbove).isSolid()) {

                double deltaYAbove = posAbove.getY() - this.getY();
                double distanceAbove = Math.abs(deltaYAbove);

                this.setNoGravity(false);
                this.noPhysics = false;
                if (distanceAbove > 0) {
                    this.setDeltaMovement(0, 0.8, 0);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                }
                this.stopHiding();
            }
        } else if (world.getGameTime() % ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get() == 0L &&
                world.getBlockState(posBelow).is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {

            double deltaYBelow = posBelow.getY() - this.getY();
            double distanceBelow = Math.abs(deltaYBelow);

            if (distanceBelow > 0)
                this.setDeltaMovement(0, (deltaYBelow / distanceBelow) * speed, 0);

            this.setNoGravity(true);
            this.noPhysics = true;
            this.tryToHide();
        } else this.setDeltaMovement(0, 0, 0);
    }

    public void checkForCollisions() {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.15D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && this.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || !(collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    return;

                this.swing(InteractionHand.MAIN_HAND);
                this.doHurtTarget(collidingEntity);
                break;
            }
        }
    }

    protected void handleAirSupply(int airSupplyAmount) {
        if (this.isAlive() && this.isInWaterOrBubble()) {
            this.setAirSupply(airSupplyAmount);
        }
    }

    public boolean isHiding() {
        return this.getHideFlag(8);
    }

    public void hide(boolean isHiding) {
        this.setHideFlag(8, isHiding);
    }

    private boolean getHideFlag(int i) {
        return (this.entityData.get(DATA_ID_HIDE_FLAGS) & i) != 0;
    }

    public void tryToHide() {
        this.hide(Boolean.TRUE);
        this.stopInPlace();
    }

    public void stopHiding() {
        this.hide(Boolean.FALSE);
        this.isHideStopping();
    }

    public void isHideStopping() {
        this.getHideFlag(8);
    }

    private void setHideFlag(int i, boolean b) {
        byte b0 = this.entityData.get(DATA_ID_HIDE_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_HIDE_FLAGS, (byte)(b0 | i));
        } else {
            this.entityData.set(DATA_ID_HIDE_FLAGS, (byte)(b0 & ~i));
        }
    }
}
