package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BaseMushroomEntity extends PathfinderMob implements GeoEntity {
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.super_mushroom.walk");
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
    public boolean isPushable() {
        return false;
    }

    @Override
    public void jumpInFluid(FluidType type) {
        this.jumpInLiquidInternal(() -> super.jumpInFluid(type));
    }

    private void jumpInLiquidInternal(Runnable onSuper) {
        if (this.getNavigation().canFloat()) {
            onSuper.run();
        } else this.setDeltaMovement(this.getDeltaMovement()
                .add(0.0, this.getAttributeValue(Attributes.JUMP_STRENGTH), 0.0));
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isControlledByLocalInstance()) {
            double d9 = this.getY();
            double d0 = this.getGravity();
            boolean flag = this.getDeltaMovement().y <= 0.0;
            FluidState fluidstate = this.level().getFluidState(this.blockPosition());
            if (flag && this.hasEffect(MobEffects.SLOW_FALLING))
                d0 = Math.min(d0, 0.01);

            if ((this.isInWaterOrBubble() || (this.isInFluidType(fluidstate)
                    && fluidstate.getFluidType() != NeoForgeMod.LAVA_TYPE.value()))
                    && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)) {
                if (this.isInWaterOrBubble() || (this.isInFluidType(fluidstate)
                        && !this.moveInFluid(fluidstate, vec3, d0))) {
                    float f4 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                    float f5 = 0.02F;
                    float f6 = (float) this.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);

                    if (!this.onGround())
                        f6 *= 1.0F;

                    if (f6 > 0.0F) {
                        f4 += (0.54600006F - f4) * f6;
                        f5 += (this.getSpeed() - f5) * f6;
                    }

                    if (this.hasEffect(MobEffects.DOLPHINS_GRACE))
                        f4 = 0.96F;

                    f5 *= (float) this.getAttributeValue(NeoForgeMod.SWIM_SPEED);
                    this.moveRelative(f5, vec3);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    Vec3 vec36 = this.getDeltaMovement();
                    if (this.horizontalCollision && this.onClimbable())
                        vec36 = new Vec3(vec36.x, 0.5, vec36.z);

                    this.setDeltaMovement(vec36.multiply(f4, 0.7F, f4));
                    Vec3 vec32 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                    this.setDeltaMovement(vec32);
                    if (this.horizontalCollision
                            && this.isFree(vec32.x, vec32.y + 0.6F - this.getY() + d9, vec32.z))
                        this.setDeltaMovement(vec32.x, 0.5F, vec32.z);
                }
            } else super.travel(vec3);
        }
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

    @Override
    public boolean isColliding(BlockPos pos, BlockState state) {
        return super.isColliding(pos, state);
    }

    public void checkForCollisions() {
        AABB boundingBox = this.getBoundingBox().inflate(0.25);
        List<Entity> entities = this.level().getEntities(this, boundingBox, entity -> entity != this);

        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                this.collideWithEntity(entity);
                break;
            }
        }
    }

    public void collideWithEntity(Entity entity) {
        if (!this.level().isClientSide && entity instanceof Player player && !player.isSpectator()
                && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK))
            this.remove(RemovalReason.DISCARDED);
    }
}
