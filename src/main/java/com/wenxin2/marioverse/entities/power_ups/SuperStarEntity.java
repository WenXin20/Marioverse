package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.ai.controls.BounceMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.ContinuousJumpGoal;
import com.wenxin2.marioverse.entities.ai.goals.LookAtEntityTagGoal;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.fluids.FluidType;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SuperStarEntity extends BasePowerUpEntity implements GeoEntity {
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.super_star.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public SuperStarEntity(EntityType<? extends SuperStarEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new BounceMoveControl(this, 1, this.getJumpSound(), 1.0F, 1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ContinuousJumpGoal(this));
        this.goalSelector.addGoal(1, new LookAtEntityTagGoal(this, TagRegistry.CAN_CONSUME_SUPER_STARS, 8.0F, 1.0F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, this::walkAnimation));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimation(final AnimationState<E> event) {
        if (this.isMoving()) {
            event.setAndContinue(WALK);
            return PlayState.CONTINUE;
        } else return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    protected SoundEvent getJumpSound() {
        return SoundRegistry.SUPER_STAR_BOUNCE.get();
    }

    @Override
    public void tick() {
        super.tick();
        RandomSource rand = RandomSource.create();

        if (this.level().getRandom().nextBoolean()) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.COIN_GLINT.get(), serverWorld, this);
            ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.COIN_GLINT.get(), this, true, 5, rand.nextDouble() * this.getBbHeight(), 0.1);
        }
    }

    @Override
    public void jumpInFluid(FluidType type) {
        if (this.onGround())
            this.setDeltaMovement(this.getDeltaMovement()
                    .add(0.0, this.getAttributeValue(Attributes.JUMP_STRENGTH), 0.0));
    }

    @Override
    public void collideWithEntity(Entity entity) {
        Level world = this.level();
        Entity vehicle = entity.getVehicle();
        LivingEntity rider = entity.getControllingPassenger();

        if (entity.getType().is(TagRegistry.POWERS_UP_RIDER) && entity.hasControllingPassenger()
                && rider instanceof AbilitiesHandler handler)
            handler.applySuperStarPowerUp(world, rider, this);
        else if (entity instanceof LivingEntity livingEntity
                && entity instanceof AbilitiesHandler handler) {
            handler.applySuperStarPowerUp(world, livingEntity, this);

            while (vehicle instanceof AbilitiesHandler vehicleHandler
                    && vehicle instanceof LivingEntity livingVehicle) {
                vehicleHandler.applySuperStarPowerUp(world, livingVehicle, this);
                vehicle = vehicle.getVehicle();
            }

            for (Entity passenger : entity.getPassengers())
                this.applyToRiders(world, passenger);
        }
    }

    private void applyToRiders(Level world, Entity firstEntity) {
        Entity currentEntity = firstEntity;

        while (currentEntity instanceof LivingEntity livingEntity) {
            if (currentEntity instanceof AbilitiesHandler riderHandler)
                riderHandler.applySuperStarPowerUp(world, livingEntity, this);

            currentEntity = currentEntity.getFirstPassenger();
        }
    }
}
