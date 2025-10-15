package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
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
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SplunkinEntity extends Monster implements GeoEntity, NeutralMob {
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable private UUID persistentAngerTarget;

    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("splunkin.idle");
    public static final RawAnimation IDLE_SWIM_ANIM = RawAnimation.begin().thenLoop("splunkin.idle_swim");
    public static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("splunkin.run");
    public static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("splunkin.sit");
    public static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("splunkin.swim");
    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("splunkin.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SplunkinEntity(EntityType<? extends SplunkinEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(1);
    }

    @Nullable    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.GOOMBA_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.GOOMBA_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(1, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Swim", 15, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkAnimController));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
        controllers.add(DefaultAnimations.genericWalkController(this));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if (this.isPassenger()) {
            event.setAndContinue(SIT_ANIM);
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
        } else if (event.isMoving()) {
            event.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean isWalking() {
        return (this.getDeltaMovement().horizontalDistance() >= 0.01
                && this.getDeltaMovement().horizontalDistance() < 0.5)
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof RandomStrollGoal
                || this.walkDist > 0);
    }

    private boolean isRunning() {
        return this.isSprinting() || this.getSpeed() >= 0.5 || this.getDeltaMovement().horizontalDistance() >= 0.5
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof MeleeAttackGoal)
                || this.targetSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof NearestAttackableTargetGoal<?>);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isInWaterOrBubble())
            this.ejectPassengers();

        if (this.getRemainingPersistentAngerTime() <= 0
                && !this.isNoAi() && this.getData(DataAttachmentRegistry.CRACKED))
            this.setData(DataAttachmentRegistry.CRACKED, false);
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        this.handleAirSupply(i);

        if (this.getTarget() != null)
            this.setSpeed(0.8F);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else super.travel(travelVector);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);

        if (wasHurt && !this.isNoAi() && !this.getData(DataAttachmentRegistry.CRACKED))
            this.setData(DataAttachmentRegistry.CRACKED, true);
        return wasHurt;
    }

    @Override
    public void die(DamageSource source) {
        this.playDeathAnimation(this);
        super.die(source);
    }


    @Override
    public boolean canTakeItem(ItemStack stack) {
        EquipmentSlot equipmentslot = this.getEquipmentSlotForItem(stack);
        return this.getItemBySlot(equipmentslot).isEmpty();
    }

    //TODO: Remove
    @NotNull
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && (player.getItemInHand(hand).getItem() instanceof ArmorItem
                || (player.getItemInHand(hand).getItem() instanceof BlockItem blockItem
                && (blockItem.getBlock() instanceof SkullBlock
                || blockItem.getBlock() instanceof EquipableCarvedPumpkinBlock
                || blockItem.getBlock() instanceof CarvedPumpkinBlock)))) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData) {
        RandomSource random = serverWorld.getRandom();

        if (random.nextFloat() < 0.05F && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        else if (random.nextFloat() < 0.15F && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));

        return super.finalizeSpawn(serverWorld, difficulty, spawnType, groupData);
    }

    public static boolean checkSplunkinSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    @NotNull
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float height) {
        return new Vec3(0.0D, this.getBbHeight() - 0.1D, 0.0D);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader worldReader) {
        return worldReader.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    protected void handleAirSupply(int airSupplyAmount) {
        if (this.isAlive() && this.isInWaterOrBubble())
            this.setAirSupply(airSupplyAmount);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.remainingPersistentAngerTime = angerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angerTarget) {
        this.persistentAngerTarget = angerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @NotNull
    public BlockParticleOption getShatterParticle() {
        return new BlockParticleOption(ParticleTypes.BLOCK, Blocks.JACK_O_LANTERN.defaultBlockState()); // TODO
    }

    public void playDeathAnimation(Entity entity) {
        float scale = (float) this.getAttributeValue(Attributes.SCALE);
        float heightScale = (float) this.getAttributeValue(AttributesRegistry.HEIGHT_SCALE);
        float widthScale = (float) this.getAttributeValue(AttributesRegistry.WIDTH_SCALE);

        if (entity.level() instanceof ServerLevel serverWorld) {
            float height = this.getBbHeight() * scale * heightScale;
            float width = this.getBbWidth() * scale * widthScale;

            if (this.getBbHeight() >= this.getBbWidth() * 3)
                width *= 2.0F;

            float scaleFactor = height * width * 1.2F;
            int numParticles = (int) (scaleFactor * 15);
            for (int i = 0; i < numParticles; ++i)
                ServerParticleUtils.spawnEntityBreakParticles(this.getShatterParticle(), serverWorld,
                        entity, height * 1.55F + 0.1F, width * 1.55F);
        }
    }
}
