package com.wenxin2.marioverse.entities;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MushroomEntity extends BasePowerUpEntity implements GeoEntity {
    protected static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlay("animation.mushroom.spawn");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private boolean movingRight = true; // Variable to track direction
    private static final double SPEED = 0.1; // Mushroom movement speed
    private static final double MAX_DISTANCE = 5.0; // Distance before switching direction
    private double distanceTraveled = 0.0; // Track how far the entity has moved

    public MushroomEntity(EntityType<? extends MushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(DefaultAnimations.getSpawnController(this, this::deployAnimController, 1));
    }

    @Override
    protected <E extends GeoAnimatable> PlayState deployAnimController(final AnimationState<E> event) {
        event.getController().setAnimation(SPAWN_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

//    @Override
//    public void tick() {
//        super.tick();
//        moveBackAndForth();
//    }
//
//    private void moveBackAndForth() {
//        // Move the mushroom entity back and forth
//        Vec3 currentVelocity = this.getDeltaMovement();
//
//        if (distanceTraveled >= MAX_DISTANCE) {
//            movingRight = !movingRight; // Reverse direction
//            distanceTraveled = 0.0; // Reset the distance traveled
//        }
//
//        if (movingRight) {
//            this.setDeltaMovement(SPEED, currentVelocity.y, currentVelocity.z); // Move right
//        } else {
//            this.setDeltaMovement(-SPEED, currentVelocity.y, currentVelocity.z); // Move left
//        }
//        distanceTraveled += Math.abs(this.getDeltaMovement().x);
//    }
}
