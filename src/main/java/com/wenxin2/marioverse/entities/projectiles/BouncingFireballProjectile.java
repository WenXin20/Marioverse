package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
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
    public static final int MAX_FIREBALLS = 2;
    public static final int FIREBALL_DELAY = 5;
    public static int fireballCooldown = 0;
    public static int fireballCount = 0; // Move to player mixin

    public BouncingFireballProjectile(EntityType<? extends BouncingFireballProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, this::idleAnimController));
    }

    protected <E extends GeoAnimatable> PlayState idleAnimController(final AnimationState<E> event) {
        if (this.isMoving()) {
            event.setAndContinue(IDLE_ANIM);
            return PlayState.CONTINUE;
        } else return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    private boolean isMoving() {
        return this.getDeltaMovement().lengthSqr() >= 0.0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void tick() {
        super.tick();
        this.checkForCollisions();
        Vec3 motion = this.getDeltaMovement();

        if (BouncingFireballProjectile.fireballCooldown > 0) {
            BouncingFireballProjectile.fireballCooldown--;
        }

        if (!this.isInWater()) {
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity
        } else this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity

        if (motion.length() > 0) {
            this.setYRot((float) Math.toDegrees(Math.atan2(motion.z, motion.x)) + 90);
            this.setXRot((float) Math.toDegrees(Math.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        }

        if (this.onGround() || this.tickCount > 400) {
            this.discard(); // Despawn
            fireballCount--;
            for (int i = 0; i < 5; i++) {
                double x = this.getX() + this.getBbWidth() / 2;
                double y = this.getY() + this.getBbHeight() / 2;
                double z = this.getZ() + this.getBbWidth() / 2;
                this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
            }
        }

        for (int i = 0; i < 1; i++) {
            double x = this.getX() + this.getBbWidth() / 2;
            double y = this.getY() + this.getBbHeight() / 2;
            double z = this.getZ() + this.getBbWidth() / 2;
            this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
        }
    }

    @Override
    public void onHitBlock(BlockHitResult hit) {
        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z) {
            this.discard(); // Despawn on side hit
            fireballCount--;
            for (int i = 0; i < 5; i++) {
                double x = this.getX() + this.getBbWidth() / 2;
                double y = this.getY() + this.getBbHeight() / 2;
                double z = this.getZ() + this.getBbWidth() / 2;
                this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
            }
        } else {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, 0.4, motion.z); // Bounce
            for (int i = 0; i < 5; i++) {
                double x = this.getX() + this.getBbWidth() / 2;
                double y = this.getY() - this.getBbHeight();
                double z = this.getZ() + this.getBbWidth() / 2;
                this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0, 0, 0);
            }
        }
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
        if (!this.level().isClientSide) {
            if (entity instanceof Player player && !player.isSpectator() && !player.fireImmune() && player != this.getOwner()
                    && !player.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
                player.igniteForSeconds(2.0F);
                player.hurt(this.level().damageSources().onFire(), 2.0F);
                this.remove(RemovalReason.KILLED);
                fireballCount--;
            } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune() && livingEntity != this.getOwner()
                    && !livingEntity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
                livingEntity.igniteForSeconds(2.0F);
                livingEntity.hurt(this.level().damageSources().onFire(), 2.0F);
                this.remove(RemovalReason.KILLED);
                fireballCount--;
            }
        }

        if (entity instanceof Player player && !player.isSpectator() && !player.fireImmune()
                && !player.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            for (int i = 0; i < 10; i++) {
                player.level().addParticle(ParticleTypes.FLAME,
                        player.getX() + player.getBbWidth() / 2.0,
                        player.getY() + player.getBbHeight() / 2.0,
                        player.getZ() + player.getBbWidth() / 2.0,
                        0.0, 0.0, 0.0);
            }
        } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune()
                && !livingEntity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            for (int i = 0; i < 10; i++) {
                livingEntity.level().addParticle(ParticleTypes.FLAME,
                        livingEntity.getX() + livingEntity.getBbWidth() / 2.0,
                        livingEntity.getY() + livingEntity.getBbHeight() / 2.0,
                        livingEntity.getZ() + livingEntity.getBbWidth() / 2.0,
                        0.0, 0.0, 0.0);
            }
        }
    }
}
