package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.FishSwimGoal;
import com.wenxin2.marioverse.entities.ai.goals.JumpOutOfWaterGoal;
import com.wenxin2.marioverse.entities.ai.goals.MeleeAttackTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.StopFollowFlockLeaderGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
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
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PorcupufferEntity extends AbstractSchoolingFish implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation FLOP = RawAnimation.begin().thenLoop("move.flop");
    public static final RawAnimation JUMP = RawAnimation.begin().thenLoop("move.jump");
    public static final RawAnimation SWIM = RawAnimation.begin().thenLoop("move.swim");
    private static final float MAX_INTERNAL_DAMAGE = 8.0F;
    public int attackCooldown = 0;
    private float internalDamage;

    public PorcupufferEntity(EntityType<? extends PorcupufferEntity> type, Level world) {
        super(type, world);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.xpReward = 2;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 360;
    }

    @NotNull
    @Override
    protected SoundEvent getFlopSound() {
        return SoundRegistry.CHEEP_CHEEP_FLOP.get();
    } // TODO

    @Nullable
    public SoundEvent getJumpSound() {
        return SoundRegistry.CHEEP_CHEEP_JUMP.get();
    } // TODO

    @NotNull
    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundRegistry.CHEEP_CHEEP_SPLASH.get();
    } // TODO

    @NotNull
    @Override
    protected SoundEvent getSwimSound() {
        return SoundRegistry.CHEEP_CHEEP_SWIM.get();
    } // TODO

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.CHEEP_CHEEP_HURT.get();
    } // TODO

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.CHEEP_CHEEP_DEATH.get();
    } // TODO

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
        return TagRegistry.CHEEP_CHEEP_CAN_ATTACK; // TODO
    }

    public double getLureRadius() {
        return ConfigRegistry.CHEEP_CHEEP_LURE_RADIUS.get(); // TODO
     }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new JumpOutOfWaterGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 10, true, this.getJumpSound()));
        this.goalSelector.addGoal(3, new FishSwimGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 1.0, 20, true));
        this.goalSelector.addGoal(4, new StopFollowFlockLeaderGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackTagGoal(this, this.getCanAttackTag(), 1.2F,
                false, true, false));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, this.getCanAttackTag(), true));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "swim", 5, this::swimAnimation));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
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

        if (this.attackCooldown > 0)
            this.attackCooldown--;

        if (this.getData(DataAttachmentRegistry.HAS_JUMPED) && this.isInWaterOrBubble())
            this.setData(DataAttachmentRegistry.HAS_JUMPED, false);

        if (this.getData(DataAttachmentRegistry.IS_BITING)
                && this.getData(DataAttachmentRegistry.IS_EATING) && this.getFirstPassenger() == null)
            this.setData(DataAttachmentRegistry.IS_BITING, false);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        Entity attacker = source.getEntity();

        if (hurt && this.getData(DataAttachmentRegistry.IS_EATING)) {
            if (attacker != null && attacker.isPassenger()) {
                this.internalDamage += amount;

                if (this.internalDamage >= MAX_INTERNAL_DAMAGE) {
                    this.spitOutPassenger(attacker);
                    this.internalDamage = 0.0F;
                }
            }
        }

        return hurt;
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
        Vec3 look = this.getLookAngle();
        Vec3 toTarget = entity.position().subtract(this.position()).normalize();

        if (this.isAlive() && !this.isAlliedTo(entity) && this.attackCooldown == 0
                && entity.getType().is(this.getCanAttackTag()) && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if (entity instanceof Creeper)
                entity.hurt(this.getDamageSource(entity), attackDamage);
            else entity.hurt(this.getDamageSource(this), attackDamage);

            if (!this.isNoAi() && canSwallow && look.dot(toTarget) > 0.5D) {
                entity.startRiding(this, true);
                this.setData(DataAttachmentRegistry.IS_EATING, true);
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
            pos = new Vec3(this.getX(), this.getY() + 0.1D, this.getZ() - 0.3D);
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
        return true;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        if (!this.getPassengers().isEmpty())
            return false;
        return super.removeWhenFarAway(distanceToClosestPlayer);
    }

    @Override // TODO remove
    public boolean canTakeItem(ItemStack stack) {
        EquipmentSlot equipmentslot = this.getEquipmentSlotForItem(stack);
        return this.getItemBySlot(equipmentslot).isEmpty();
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && player.isCreative()
                && (player.getItemInHand(hand).getItem() instanceof ArmorItem
                || (player.getItemInHand(hand).getItem() instanceof BlockItem blockItem
                && (blockItem.getBlock() instanceof SkullBlock
                || blockItem.getBlock() instanceof EquipableCarvedPumpkinBlock)))) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
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
        double offset = this.getBbWidth() * 0.9D;
        double x = this.getX() + look.x * offset;
        double y = this.getEyeY() - 0.2D;
        double z = this.getZ() + look.z * offset;
        double launchStrength = 1.8D;

        passenger.stopRiding();
        passenger.teleportTo(x, y, z);
        passenger.setDeltaMovement(look.x * launchStrength, 0.3D, look.z * launchStrength);
        passenger.hurtMarked = true;
        this.setData(DataAttachmentRegistry.IS_BITING, false);
        this.setData(DataAttachmentRegistry.IS_EATING, false);
    }
}