package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.init.ItemRegistry;
import java.util.EnumSet;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
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
    private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(GoombaEntity.class, EntityDataSerializers.BYTE);
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.goomba.idle");
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.goomba.run");
    protected static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("animation.goomba.sit");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.goomba.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public GoombaEntity(EntityType<? extends Monster> type, Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6D, true));
        this.goalSelector.addGoal(2, new RandomSwimmingGoal(this, 1.0, 1));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(3, new GoombaEntity.GoombaSitGoal(100));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new GoombaEntity.GoombaRideGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkAnimController));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if (this.isSitting()) {
            event.setAndContinue(SIT_ANIM);
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public boolean isSitting() {
        return this.getFlag(8);
    }

    private boolean isWalking() {
        return (this.getDeltaMovement().horizontalDistance() >= 0.01
                && this.getDeltaMovement().horizontalDistance() < 0.5)
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof RandomStrollGoal);
    }

    private boolean isRunning() {
        return this.getTarget() != null || this.getDeltaMovement().horizontalDistance() >= 0.3
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof MeleeAttackGoal)
                || this.targetSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof NearestAttackableTargetGoal<?>);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isRunning()) {
            for (int i = 0; i < 1; i++) {
                double x = this.getX() + this.getBbWidth() / 2;
                double y = this.getY() + this.getBbHeight() / 2;
                double z = this.getZ() + this.getBbWidth() / 2;
                this.level().addParticle(ParticleTypes.POOF, x, y, z, 0, 0, 0);
            }
        }
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

    private void setFlag(int i, boolean b) {
        byte b0 = this.entityData.get(DATA_ID_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 | i));
        } else {
            this.entityData.set(DATA_ID_FLAGS, (byte)(b0 & ~i));
        }
    }

    private boolean getFlag(int i) {
        return (this.entityData.get(DATA_ID_FLAGS) & i) != 0;
    }

    public void sit(boolean isSitting) {
        this.setFlag(8, isSitting);
    }

    void tryToSit() {
        if (!this.isInWater()) {
            this.setZza(0.0F);
            this.getNavigation().stop();
            this.sit(true);
        }
    }

    class GoombaSitGoal extends Goal {
        private final int chanceToSit;
        private int cooldown;

        public GoombaSitGoal(int chanceToSit) {
            this.chanceToSit = chanceToSit;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.cooldown == 0
                    && !GoombaEntity.this.isBaby()
                    && !GoombaEntity.this.isInWater()) {

                if (GoombaEntity.this.getRandom().nextInt(this.chanceToSit) == 0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            return !GoombaEntity.this.isInWater()
                    && GoombaEntity.this.isSitting()
                    && GoombaEntity.this.getRandom().nextInt(100) != 1;
        }

        @Override
        public void tick() {
            if (!GoombaEntity.this.isSitting()) {
                GoombaEntity.this.tryToSit();
            }
        }

        @Override
        public void start() {
            GoombaEntity.this.tryToSit();
            this.cooldown = GoombaEntity.this.getRandom().nextInt(100) + 100;
        }

        @Override
        public void stop() {
            this.cooldown = GoombaEntity.this.getRandom().nextInt(1000);
            GoombaEntity.this.sit(false);
        }
    }

    public void ride(boolean isRiding) {
        this.setFlag(9, isRiding);
    }

    void tryToRide() {
        if (!this.isInWater() && !this.isPassenger()) {
            this.setZza(0.0F);
            this.getNavigation().stop();
            this.sit(true);
        }
    }

    class GoombaRideGoal extends Goal {
        private final GoombaEntity goomba;
        private int cooldown;
        private static final int MAX_STACK_SIZE = 5;

        public GoombaRideGoal(GoombaEntity goomba) {
            this.goomba = goomba;
        }

        @Override
        public boolean canUse() {
            // Check if the Goomba has no passengers, isn't riding another Goomba, and stacking limit not exceeded
            if (!goomba.isPassenger() && goomba.getPassengers().isEmpty() && !goomba.isVehicle() && this.cooldown == 0) {
                // Very rare chance to attempt stacking
                if (goomba.random.nextFloat() < 0.001F) {
                    GoombaEntity nearbyGoomba = findNearbyGoombaToRide();
                    return nearbyGoomba != null && nearbyGoomba.getPassengers().size() <= MAX_STACK_SIZE;
                }
            }
            return false;
        }

        @Override
        public void start() {
            GoombaEntity nearbyGoomba = findNearbyGoombaToRide();
            if (nearbyGoomba != null && nearbyGoomba.getPassengers().size() <= MAX_STACK_SIZE) {
                goomba.tryToRide(); // Try to ride instead of sit
                goomba.startRiding(nearbyGoomba, true);
            }
            this.cooldown = 200 + goomba.random.nextInt(400); // Cooldown for rare stacking attempts
        }

        @Override
        public boolean canContinueToUse() {
            return goomba.isPassenger() && goomba.getVehicle() instanceof GoombaEntity;
        }

        @Override
        public void stop() {
            this.cooldown = 200; // Cooldown after stopping ride
            goomba.ride(false); // Stop riding
        }

        private GoombaEntity findNearbyGoombaToRide() {
            // Find nearby Goomba within a small radius to ride
            return goomba.level().getEntitiesOfClass(GoombaEntity.class, goomba.getBoundingBox().inflate(0.5D)).stream()
                    .filter(otherGoomba -> otherGoomba != goomba && otherGoomba.getPassengers().isEmpty())
                    .findFirst()
                    .orElse(null);
        }
    }
}
