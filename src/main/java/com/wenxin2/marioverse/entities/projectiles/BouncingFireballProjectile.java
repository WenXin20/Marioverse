package com.wenxin2.marioverse.entities.projectiles;

import net.minecraft.core.Direction;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BouncingFireballProjectile extends ThrowableProjectile implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.fireball.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public BouncingFireballProjectile(EntityType<? extends BouncingFireballProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 0, this::idleAnimController));
    }

    protected <E extends GeoAnimatable> PlayState idleAnimController(final AnimationState<E> event) {
        event.setAndContinue(IDLE_ANIM);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();

        if (!this.isInWater()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity
        }

        if (this.onGround() || this.tickCount > 200) {
            this.discard(); // Despawn logic
        }
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z) {
            this.discard(); // Despawn on side hit
        } else {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, -motion.y * 0.8, motion.z); // Bounce
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hit) {
        Entity entity = hit.getEntity();
        if (entity instanceof LivingEntity && !entity.fireImmune()) {
            entity.igniteForSeconds(2.0F);
            entity.hurt(this.level().damageSources().onFire(), 2.0F);
        }

        super.onHitEntity(hit);
    }
}
