package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.GoombaRideGoombaGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSitGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSleepGoal;
import com.wenxin2.marioverse.init.ItemRegistry;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
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
import software.bernie.geckolib.util.GeckoLibUtil;

public class GoombaEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_SIT_FLAGS = SynchedEntityData.defineId(GoombaEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_ID_SLEEP_FLAGS = SynchedEntityData.defineId(GoombaEntity.class, EntityDataSerializers.BYTE);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.goomba.idle");
    protected static final RawAnimation IDLE_SWIM_ANIM = RawAnimation.begin().thenLoop("animation.goomba.idle_swim");
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.goomba.run");
    protected static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("animation.goomba.sit");
    protected static final RawAnimation SLEEP_ANIM = RawAnimation.begin().thenLoop("animation.goomba.sleep");
    protected static final RawAnimation SQUASH_ANIM = RawAnimation.begin().thenPlayAndHold("animation.goomba.squash");
    protected static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("animation.goomba.swim");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.goomba.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GoombaEntity(EntityType<? extends Monster> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_SIT_FLAGS, (byte)0);
        builder.define(DATA_ID_SLEEP_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new GoombaSitGoal(this, 100, 1200, 3000, 300));
        this.goalSelector.addGoal(3, new GoombaSleepGoal(this, 25, 2400, 6000));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.6D, true));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(7, new GoombaRideGoombaGoal(this, 0.001F));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Swim", 10, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Death", 5, this::squashAnimController));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if (this.isSitting()) {
            event.setAndContinue(SIT_ANIM);
            return PlayState.CONTINUE;
        }

        if (this.isSleeping()) {
            event.setAndContinue(SLEEP_ANIM);
            return PlayState.CONTINUE;
        }

        if (this.isInWaterOrBubble()) {
            if (!this.isRunning() && !this.isWalking())
                event.setAndContinue(IDLE_SWIM_ANIM);
            else event.setAndContinue(SWIM_ANIM);
            return PlayState.CONTINUE;
        } else if (this.isRunning()) {
            event.setAndContinue(RUN_ANIM);
            return PlayState.CONTINUE;
        } else if (this.isWalking()) {
            event.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
    }

    protected <E extends GeoAnimatable> PlayState squashAnimController(final AnimationState<E> event) {
        if (this.dead) {
            event.setAndContinue(SQUASH_ANIM);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean isSitting() {
        return this.getSitFlag(8);
    }

    public boolean isSleeping() {
        return this.getSleepFlag(12);
    }

    private boolean isWalking() {
        return (this.getDeltaMovement().horizontalDistance() >= 0.01
                && this.getDeltaMovement().horizontalDistance() < 0.5)
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof RandomStrollGoal);
    }

    private boolean isRunning() {
        return this.getSpeed() >= 0.5 ||this.getDeltaMovement().horizontalDistance() >= 0.5
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof MeleeAttackGoal)
                || this.targetSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof NearestAttackableTargetGoal<?>);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSpeed() > 0.4) {
            for (int i = 0; i < 1; i++) {
                double x = this.getX() + this.getBbWidth() / 2;
                double y = this.getY() + this.getBbHeight() / 2;
                double z = this.getZ() + this.getBbWidth() / 2;
                this.level().addParticle(ParticleTypes.POOF, x, y, z, 0, 0, 0);
            }
        }
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        this.handleAirSupply(i);

        if (this.getTarget() != null) {
            this.setSpeed(0.8F);
        }
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
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int i = random.nextInt(6);
            if (i == 0) {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.FIRE_HAT.get()));
            } else {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            }
        }
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && player.getItemInHand(hand).getItem() instanceof ArmorItem) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            player.swing(hand);
        }
        return super.mobInteract(player, hand);
    }

    @NotNull
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float height) {
        return new Vec3(0.0D, this.getBbHeight() - 0.1D, 0.0D);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);

        if (wasHurt) {
            this.sit(false);
            this.sleep(false);
        }
        return wasHurt;
    }


    @Override
    public boolean checkSpawnObstruction(LevelReader worldReader) {
        return worldReader.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(3);
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

    public void checkForCollisionsAndWakeUp() {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.25D), entity -> !entity.isSpectator());

        for (Entity collidingEntity : nearbyEntities) {
            if ((!this.isSleeping() && !this.isSitting()) || collidingEntity instanceof GoombaEntity
                    || collidingEntity.getY() >= this.getY() + this.getEyeHeight())
                return;

            // Apply knockback to both the Goomba and the bumping collidingEntity
            Vec3 knockbackDirection = new Vec3(collidingEntity.getX() - this.getX(), 0.4D,
                    collidingEntity.getZ() - this.getZ()).normalize();
            double knockbackStrength = 1.0D;

            // Knock back the Goomba
            this.setDeltaMovement(
                    -knockbackDirection.x * knockbackStrength, 0.4D,
                    -knockbackDirection.z * knockbackStrength);
            this.hurtMarked = true; // Mark as hurt to apply knockback
            // Knock back the other collidingEntity
            collidingEntity.setDeltaMovement(knockbackDirection.x * knockbackStrength, 0.4D,
                    knockbackDirection.z * knockbackStrength);
            collidingEntity.hurtMarked = true;

            this.sit(Boolean.FALSE);
            this.sleep(Boolean.FALSE);
            break;
        }
    }

    private void setSitFlag(int i, boolean b) {
        byte b0 = this.entityData.get(DATA_ID_SIT_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_SIT_FLAGS, (byte)(b0 | i));
        } else {
            this.entityData.set(DATA_ID_SIT_FLAGS, (byte)(b0 & ~i));
        }
    }

    private void setSleepFlag(int i, boolean b) {
        byte b1 = this.entityData.get(DATA_ID_SLEEP_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_SLEEP_FLAGS, (byte)(b1 | i));
        } else {
            this.entityData.set(DATA_ID_SLEEP_FLAGS, (byte)(b1 & ~i));
        }
    }

    private boolean getSitFlag(int i) {
        return (this.entityData.get(DATA_ID_SIT_FLAGS) & i) != 0;
    }

    private boolean getSleepFlag(int i) {
        return (this.entityData.get(DATA_ID_SLEEP_FLAGS) & i) != 0;
    }

    public void sit(boolean isSitting) {
        this.setSitFlag(8, isSitting);
    }

    public void tryToSit() {
        if (!this.isInWater()) {
            this.stopInPlace();
            this.sit(true);
        }
    }

    public void sleep(boolean isSleeping) {
        this.setSleepFlag(12, isSleeping);
    }

    public void tryToSleep() {
        if (!this.isInWater()) {
            this.stopInPlace();
            this.sit(false);
            this.sleep(true);
        }
    }

    public void ride(boolean isRiding) {
        this.setSitFlag(10, isRiding);
    }

    public void tryToRide() {
        if (!this.isInWater() && !this.isPassenger()) {
            this.stopInPlace();
            this.sit(true);
        }
    }
}
