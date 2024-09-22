package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BaseMushroomEntity extends PathfinderMob implements GeoEntity {
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.mushroom.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BaseMushroomEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if (event.isMoving())
            return event.setAndContinue(WALK_ANIM);
        return PlayState.STOP;
    }

    @Override
    public void tick() {
        super.tick();
        this.checkForCollisions();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // Poof particle effect
        if (this.level().isClientSide) {
            for (int i = 0; i < 10; i++) {
                this.level().addParticle(ParticleTypes.POOF,
                        this.getX() + this.getBbWidth() / 2.0, this.getY() + this.getBbHeight() / 2.0, this.getZ() + this.getBbWidth() / 2.0,
                        0.0, 0.0, 0.0);
            }
        }
        this.remove(RemovalReason.KILLED);
        return true;
    }

    public void checkForCollisions() {
        AABB boundingBox = this.getBoundingBox().inflate(0.1);
        List<Entity> entities = this.level().getEntities(this, boundingBox, entity -> entity != this);

        for (Entity entity : entities) {
            handleCollision(entity);
            break;
        }
    }

    public void handleCollision(Entity entity) {
        if (!this.level().isClientSide && entity instanceof Player player && !player.isSpectator()
                && !entity.getType().is(TagRegistry.DAMAGE_SHRINKS_ENTITY_BLACKLIST)) {
            this.level().broadcastEntityEvent(this, (byte) 60);
            this.level().broadcastEntityEvent(this, (byte) 20);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 60) {
            if (this.level().isClientSide) {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.blockPosition(),
                        ParticleRegistry.MUSHROOM_TRANSFORM.get(), UniformInt.of(1, 3));
            }
        } else if (id == 61) {
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleRegistry.ONE_UP.get(),
                        this.getX(), this.getY() + 2.0, this.getZ(),
                        0.0, 1.0, 0.0);
            }
        } else if (id == 62) {
            if (this.level().isClientSide) {
                for (int i = 0; i < 10; i++) {
                    this.level().addParticle(ParticleTypes.POOF,
                            this.getX(), this.getY() + 0.5, this.getZ(),
                            0.0, 0.0, 0.0);
                }
            }
        } else super.handleEntityEvent(id);
    }
}
