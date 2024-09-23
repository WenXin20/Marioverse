package com.wenxin2.marioverse.entities.projectiles;

import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.bouncing_fireball.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public BouncingFireballProjectile(EntityType<? extends BouncingFireballProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, this::walkAnimController));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        event.setAndContinue(WALK_ANIM);
        return PlayState.CONTINUE;
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

        if (!this.isInWater()) {
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity
        } else this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04D, 0)); // Gravity

        if (motion.length() > 0) {
            this.setYRot((float) Math.toDegrees(Math.atan2(motion.z, motion.x)) + 270);
            this.setXRot((float) Math.toDegrees(Math.atan2(-motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z))));
        }

        if (this.onGround() || this.tickCount > 400) {
            if (!this.level().isClientSide) {
                this.level().broadcastEntityEvent(this, (byte) 60); // Smoke particle
            }
            this.level().playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            this.discard(); // Despawn
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
        Level world = this.level();
        BlockPos hitPos = hit.getBlockPos();
        BlockState state = this.level().getBlockState(hitPos);
        BlockState stateAbove = this.level().getBlockState(hitPos.above());

        if (hit.getDirection().getAxis() == Direction.Axis.X || hit.getDirection().getAxis() == Direction.Axis.Z) {
            if (!world.isClientSide) {
                world.broadcastEntityEvent(this, (byte) 60); // Smoke particle
            }
            world.playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
            this.discard(); // Despawn on side hit
        } else {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, 0.4, motion.z); // Bounce
            world.broadcastEntityEvent(this, (byte) 61); // Smoke particle
            world.playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_SIZZLES.get(),
                    SoundSource.AMBIENT, 1.0F, 1.0F);
        }

        if (state.is(TagRegistry.MELTS))
            world.removeBlock(hitPos, false);
        else if (stateAbove.is(Blocks.SNOW))
            world.removeBlock(hitPos.above(), false);
        else if (state.is(TagRegistry.MELTS_INTO_WATER))
            world.setBlock(hitPos, Blocks.WATER.defaultBlockState(), 3);
        else if (state.is(TagRegistry.MELTS_INTO_ICE))
            world.setBlock(hitPos, Blocks.ICE.defaultBlockState(), 3);
        else if (state.is(TagRegistry.MELTS_INTO_PACKED_ICE))
            world.setBlock(hitPos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        else if (state.is(Blocks.WET_SPONGE))
            world.setBlock(hitPos, Blocks.SPONGE.defaultBlockState(), 3);
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
                this.doKnockback(player, this.level().damageSources().onFire());
                this.level().playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                this.remove(RemovalReason.KILLED);
            } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune() && livingEntity != this.getOwner()
                    && !livingEntity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
                livingEntity.igniteForSeconds(2.0F);
                livingEntity.hurt(this.level().damageSources().onFire(), 2.0F);
                this.doKnockback(livingEntity, this.level().damageSources().onFire());
                this.level().playSound(null, this.blockPosition(), SoundRegistry.FIREBALL_EXTINGUISHED.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                this.remove(RemovalReason.KILLED);
            }
        }

        if (entity instanceof Player player && !player.isSpectator() && !player.fireImmune() && player != this.getOwner()
                && !player.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            this.level().broadcastEntityEvent(this, (byte) 60); // Smoke particle
        } else if (entity instanceof LivingEntity livingEntity && !livingEntity.fireImmune() && livingEntity != this.getOwner()
                && !livingEntity.getType().is(TagRegistry.FIREBALL_IMMUNE)) {
            this.level().broadcastEntityEvent(this, (byte) 60); // Smoke particle
        }
    }

    public void doKnockback(LivingEntity entity, DamageSource source) {
        double d1 = Math.max(0.0, 1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        Vec3 vec3 = this.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize().scale(0.6 * d1);
        if (vec3.lengthSqr() > 0.0) {
            entity.push(vec3.x, 0.2, vec3.z);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 60) {
            if (this.level().isClientSide) {
                int numParticles = 10; // Number of particles to spawn in the circle
                double radius = 0.2;  // Radius of the circle around the fireball

                for (int i = 0; i < numParticles; i++) {
                    // Calculate angle for each particle
                    double angle = 2 * Math.PI * i / numParticles;

                    // Calculate the X and Z offset using sine and cosine to spread in a circle
                    double offsetX = Math.cos(angle) * radius;
                    double offsetY = Math.sin(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;

                    double x = this.getX() + offsetX;
                    double y = this.getY() + offsetY;
                    double z = this.getZ() + offsetZ;

                    this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
                }
            }
        } else if (id == 61) {
            if (this.level().isClientSide) {
                int numParticles = 10; // Number of particles to spawn in the circle
                double radius = 0.15;  // Radius of the circle around the fireball

                for (int i = 0; i < numParticles; i++) {
                    // Calculate angle for each particle
                    double angle = 2 * Math.PI * i / numParticles;

                    // Calculate the X and Z offset using sine and cosine to spread in a circle
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;

                    double x = this.getX() + offsetX;
                    double y = this.getY() - this.getBbHeight();
                    double z = this.getZ() + offsetZ;

                    this.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
                }
            }
        } else super.handleEntityEvent(id);
    }
}
