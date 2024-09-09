package com.wenxin2.marioverse.entities;

import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BasePowerUpEntity extends Mob implements GeoEntity {
    protected static final RawAnimation SPAWN_ANIM = RawAnimation.begin().thenPlay("animation.mushroom.spawn");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BasePowerUpEntity(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    protected <E extends GeoAnimatable> PlayState deployAnimController(final AnimationState<E> event) {
        event.getController().setAnimation(SPAWN_ANIM);
        return PlayState.CONTINUE;
    }

//    @Override
//    public void tick() {
//        super.tick();
//        checkForCollisions();
//    }

//    @Override
//    public boolean hurt(DamageSource source, float amount) {
//        // Create a "poof" particle effect
//        if (!this.level().isClientSide) {
//            // Only spawn particles on the server side
//            this.level().broadcastEntityEvent(this, (byte) 20); // Particle effect ID
//        }
//
//        // Immediately remove the entity
//        this.remove(RemovalReason.KILLED);
//        return true;
//    }
//
//    private void checkForCollisions() {
//        // Check for collisions with entities
//        AABB boundingBox = this.getBoundingBox().inflate(0.1);
//        List<Entity> entities = this.level().getEntities(this, boundingBox, entity -> entity != this);
//
//        for (Entity entity : entities) {
//            handleCollision(entity);
//            break; // Handle only the first collision
//        }
//    }

//    private void handleCollision(Entity entity) {
//        if (!this.level().isClientSide) {
//
//            // Create a "poof" particle effect
//            this.level().broadcastEntityEvent(this, (byte) 20); // Particle effect ID
//            // Immediately remove the mushroom entity
//            this.remove(Entity.RemovalReason.KILLED);
//        }
//    }
//
//    @Override
//    public void handleEntityEvent(byte id) {
//        if (id == 20) { // Particle effect ID
//            // Spawn "poof" particles at the entity's position
//            if (this.level().isClientSide) {
//                for (int i = 0; i < 10; i++) {
//                    this.level().addParticle(ParticleTypes.POOF,
//                            this.getX(), this.getY(), this.getZ(),
//                            0.0, 0.0, 0.0);
//                }
//            }
//        } else {
//            super.handleEntityEvent(id);
//        }
//    }
}
