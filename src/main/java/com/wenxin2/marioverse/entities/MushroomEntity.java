package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.init.SoundRegistry;
import java.util.List;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
import virtuoel.pehkui.api.ScaleTypes;

public class MushroomEntity extends BasePowerUpEntity implements GeoEntity {
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.mushroom.walk");
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
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericWalkIdleController(this));
        controllers.add(new AnimationController<>(this, "Walking", 5, this::walkAnimController));
    }

    @Override
    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        event.getController().setAnimation(WALK_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void tick() {
        super.tick();
//        moveBackAndForth();
        checkForCollisions();
    }

    @Override
    public void checkForCollisions() {
        // Check for collisions with entities
        AABB boundingBox = this.getBoundingBox().inflate(0.1);
        List<Entity> entities = this.level().getEntities(this, boundingBox, entity -> entity != this);

        for (Entity entity : entities) {
            handleCollision(entity);
            break; // Handle only the first collision
        }
    }

    @Override
    public void handleCollision(Entity entity) {
        if (!this.level().isClientSide && !(entity instanceof BasePowerUpEntity)) {
            if (entity instanceof Player player && player.getHealth() <= 10) {
                ScaleTypes.HEIGHT.getScaleData(player).setTargetScale(1.0F);
                ScaleTypes.WIDTH.getScaleData(player).setTargetScale(1.0F);
                player.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                player.setHealth(20F);
            }
            // Create a "poof" particle effect
            this.level().broadcastEntityEvent(this, (byte) 20);
            this.level().playSound(null, this.blockPosition(), SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void moveBackAndForth() {
        // Move the mushroom entity back and forth
        Vec3 currentVelocity = this.getDeltaMovement();

        if (distanceTraveled >= MAX_DISTANCE) {
            movingRight = !movingRight; // Reverse direction
            distanceTraveled = 0.0; // Reset the distance traveled
        }

        if (movingRight) {
            this.setDeltaMovement(SPEED, currentVelocity.y, currentVelocity.z); // Move right
        } else {
            this.setDeltaMovement(-SPEED, currentVelocity.y, currentVelocity.z); // Move left
        }
        distanceTraveled += Math.abs(this.getDeltaMovement().x);
    }
}
