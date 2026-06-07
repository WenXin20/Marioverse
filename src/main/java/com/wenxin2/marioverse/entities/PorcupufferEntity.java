package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.FishSwimGoal;
import com.wenxin2.marioverse.entities.ai.goals.JumpOutOfWaterGoal;
import com.wenxin2.marioverse.entities.ai.goals.MeleeAttackTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.PickupItemGoal;
import com.wenxin2.marioverse.entities.ai.goals.StopFollowFlockLeaderGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PorcupufferEntity extends AbstractSchoolingFish implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation FLOP = RawAnimation.begin().thenLoop("move.flop");
    public static final RawAnimation JUMP = RawAnimation.begin().thenLoop("move.jump");
    public static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    public int attackCooldown = 0;
    public int eatCooldown = 0;
    private float internalDamage;

    public PorcupufferEntity(EntityType<? extends PorcupufferEntity> type, Level world) {
        super(type, world);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 1.5F, 0.1F, true);
        this.xpReward = 4;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 360;
    }

    @NotNull
    @Override
    protected SoundEvent getFlopSound() {
        return SoundRegistry.PORCUPUFFER_FLOP.get();
    }

    @Nullable
    public SoundEvent getJumpSound() {
        return SoundRegistry.PORCUPUFFER_JUMP.get();
    }

    @NotNull
    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundRegistry.PORCUPUFFER_SPLASH.get();
    }

    @NotNull
    @Override
    protected SoundEvent getSwimSound() {
        return SoundRegistry.PORCUPUFFER_SWIM.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.PORCUPUFFER_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.PORCUPUFFER_DEATH.get();
    }

    @NotNull
    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(Items.WATER_BUCKET);
    }

    @NotNull
    public DamageSource getDamageSource(Entity collidingEntity) {
        if (this.getData(DataAttachmentRegistry.IS_EATING))
            return DamageSourceRegistry.swallowed(collidingEntity);
        return DamageSourceRegistry.porcupufferSpikes(collidingEntity);
    }

    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.PORCUPUFFER_CAN_ATTACK;
    }

    public double getLureRadius() {
        return ConfigRegistry.PORCUPUFFER_LURE_RADIUS.get();
     }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new JumpOutOfWaterGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 10, true, this.getJumpSound()));
        this.goalSelector.addGoal(3, new MeleeAttackTagGoal(this, this.getCanAttackTag(), 1.2F,
                false, true, true));
        this.goalSelector.addGoal(4, new PickupItemGoal(this, ItemTags.FISHES, this.getLureRadius(), 1.2F, true));
        this.goalSelector.addGoal(5, new FishSwimGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 20.0, 20, true));
        this.goalSelector.addGoal(6, new StopFollowFlockLeaderGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, this.getCanAttackTag(), false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "swim", 5, this::swimAnimation));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected <E extends GeoAnimatable> PlayState swimAnimation(final AnimationState<E> event) {
        if (this.getData(DataAttachmentRegistry.HAS_JUMPED)) {
            event.setAndContinue(JUMP);
            return PlayState.CONTINUE;
        } else if (this.isInWaterOrBubble()) {
            event.setAndContinue(SWIM);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(FLOP);
            return PlayState.CONTINUE;
        }
    }

    @Override
    public void tick() {
        super.tick();

        this.entitySwallowed();

        if (this.attackCooldown > 0)
            this.attackCooldown--;

        if (this.eatCooldown > 0)
            this.eatCooldown--;

        if (this.getData(DataAttachmentRegistry.HAS_JUMPED) && this.isInWaterOrBubble())
            this.setData(DataAttachmentRegistry.HAS_JUMPED, false);

        if (this.getData(DataAttachmentRegistry.IS_MOUTH_OPEN)
                && this.getData(DataAttachmentRegistry.IS_EATING) && this.getFirstPassenger() == null)
            this.setData(DataAttachmentRegistry.IS_MOUTH_OPEN, false);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean isHurt = super.hurt(source, amount);
        Entity attacker = source.getEntity();
        Entity passenger = this.getFirstPassenger();

        if (isHurt && passenger != null && this.getData(DataAttachmentRegistry.IS_EATING)) {
            if (attacker != null) {
                this.internalDamage += amount;
                this.setData(DataAttachmentRegistry.IS_MOUTH_OPEN, true);

                if (this.internalDamage >= ConfigRegistry.PORCUPUFFER_DAMAGE_THRESHOLD.get()) {
                    this.spitOutPassenger(passenger);
                    this.internalDamage = 0.0F;
                }
            }
        }
        return isHurt;
    }

//    @NotNull
//    @Override
//    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
//        return InteractionResult.PASS;
//    }

    @Override
    public void doPush(Entity entity) {
        super.doPush(entity);
        boolean canSwallow = entity.getBbWidth() <= 2.0F &&
                entity.getBbHeight() <= 2.0F;
        Vec3 look = new Vec3(this.getLookAngle().x, 0.0D, this.getLookAngle().z).normalize();
        Vec3 toTarget = entity.position().subtract(this.position());
        toTarget = new Vec3(toTarget.x, 0.0D, toTarget.z).normalize();

        if (this.isAlive() && !this.isAlliedTo(entity) && this.attackCooldown == 0
                && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if (!entity.isInvulnerableTo(level().damageSources().thorns(this))) {
                if (entity instanceof Creeper)
                    entity.hurt(this.getDamageSource(entity), attackDamage);
                else entity.hurt(this.getDamageSource(this), attackDamage);
            }

            if (!entity.level().isClientSide && !this.isNoAi() && this.eatCooldown == 0
                    && canSwallow && look.dot(toTarget) > 0.5D
                    && entity.getType().is(TagRegistry.PORCUPUFFER_CAN_EAT)
                    && this.getData(DataAttachmentRegistry.IS_MOUTH_OPEN)) {
                entity.startRiding(this, true);
                this.setData(DataAttachmentRegistry.IS_EATING, true);
                this.setData(DataAttachmentRegistry.IS_MOUTH_OPEN, false);
                this.eatCooldown = 120;
            }

            this.swing(this.getUsedItemHand());
            this.attackCooldown = 20;
        }
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        if (!this.hasPassenger(passenger))
            return;

        Vec3 pos;
        if (this.getData(DataAttachmentRegistry.IS_EATING))
            pos = new Vec3(this.getX(), this.getY() + 0.025D, this.getZ() - 0.3D);
        else pos = new Vec3(this.getX(), this.getY() + 1.0D, this.getZ());

        moveFunction.accept(passenger, pos.x, pos.y, pos.z);
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity riderEntity) {
        return true;
    }

    @Override
    public boolean canControlVehicle() {
        return false;
    }

    @Override
    public boolean canRiderInteract() {
        return this.getData(DataAttachmentRegistry.IS_EATING);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        if (!this.getPassengers().isEmpty())
            return false;
        return super.removeWhenFarAway(distanceToClosestPlayer);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        RandomSource random = level.getRandom();

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            if (random.nextFloat() < 0.01F)
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.TURTLE_HELMET));
            else if (random.nextFloat() < 0.015F)
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            else if (random.nextFloat() < 0.05F)
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
        }

        return data;
    }

    public static boolean checkPorcupufferSpawnRules(EntityType<PorcupufferEntity> entityType, LevelAccessor levelAccessor,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getFluidState(pos.below()).is(FluidTags.WATER)
                && levelAccessor.getBlockState(pos.above()).is(Blocks.WATER)
                && WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, spawnType, pos, random);
    }

    private void spitOutPassenger(Entity passenger) {
        if (passenger == null)
            return;
        Vec3 look = this.getLookAngle().normalize();
        double launchStrength = 1.8D;

        passenger.stopRiding();
        passenger.setDeltaMovement(look.x * launchStrength, 0.3D, look.z * launchStrength);
        passenger.hurtMarked = true;
        this.setData(DataAttachmentRegistry.IS_MOUTH_OPEN, false);
        this.setData(DataAttachmentRegistry.IS_EATING, false);
    }

    private void entitySwallowed() {
        Entity passenger = this.getFirstPassenger();
        if (this.getData(DataAttachmentRegistry.IS_EATING)) {
            if (!(passenger instanceof LivingEntity livingPassenger)
                    || !livingPassenger.isAlive()) {
                if (passenger != null)
                    passenger.stopRiding();

                if (ConfigRegistry.PORCUPUFFER_HEALTH_HEALED.get().floatValue() > 0)
                    this.level().addAlwaysVisibleParticle(ParticleTypes.HEART, this.getX() + 0.5,
                            this.getY() + this.getBbHeight(), this.getZ() + 0.5, 0.0, 0.2, 0.0);

                this.heal(ConfigRegistry.PORCUPUFFER_HEALTH_HEALED.get().floatValue());
                this.setData(DataAttachmentRegistry.IS_EATING, false);
                this.internalDamage = 0.0F;
            }
        }
    }

    public boolean isMrsPuff() {
        return this.getName().getString().toLowerCase(Locale.ROOT).equals("mrs puff")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("mrs. puff")
                || this.getName().getString().toLowerCase(Locale.ROOT).equals("mrs_puff");
    }

    public boolean isQwilfish() {
        return this.getName().getString().toLowerCase(Locale.ROOT).equals("qwilfish");
    }
}