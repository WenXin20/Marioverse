package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.ai.controls.BounceMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.ContinuousJumpGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.sounds.FadingSoundInstance;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.super_star.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public SuperStarEntity(EntityType<? extends SuperStarEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new BounceMoveControl(this, 1, getJumpSound(), 1.0F, 1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ContinuousJumpGoal(this));
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

    protected SoundEvent getJumpSound() {
        return SoundRegistry.SUPER_STAR_BOUNCE.get();
    }

    @Override
    public void tick() {
        super.tick();
        this.checkForCollisions();

        if (this.level().getRandom().nextBoolean()) {
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.COIN_GLINT.get(), serverWorld, this);
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
        if (!this.level().isClientSide) {
            if (entity instanceof Player player && !player.isSpectator()
                    && !player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                    && player.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS)
                    && entity instanceof AbilitiesHandler handler) {

                handler.mv$setSuperStar(true);
                handler.mv$setSuperStarCooldown(ConfigRegistry.SUPER_STAR_DURATION.get());
                this.level().broadcastEntityEvent(player, (byte) 119); // Super Star Powered Up particle
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));

                this.level().playSound(null, this.blockPosition(), SoundRegistry.POWERS_UP_SUPER_STAR.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                if (!handler.mv$playedSuperStarTheme())
                    Minecraft.getInstance().getSoundManager().play(new FadingSoundInstance(player, SoundRegistry.SUPER_STAR_THEME.get(),
                            SoundSource.AMBIENT, entity.getRandom(), handler.mv$getSuperStarCooldown(), 100));
                handler.mv$setPlayedSuperStarTheme(true);
                this.remove(RemovalReason.KILLED);

            } else if (entity instanceof LivingEntity livingEntity
                    && !livingEntity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                    && (livingEntity.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS)
                        || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get())
                    && !(livingEntity instanceof Player)
                    && entity instanceof AbilitiesHandler handler) {

                handler.mv$setSuperStar(true);
                handler.mv$setSuperStarCooldown(ConfigRegistry.SUPER_STAR_DURATION.get());
                this.level().broadcastEntityEvent(livingEntity, (byte) 119); // Super Star Powered Up particle
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));

                this.level().playSound(null, this.blockPosition(), SoundRegistry.POWERS_UP_SUPER_STAR.get(),
                        SoundSource.AMBIENT, 1.0F, 1.0F);
                if (!handler.mv$playedSuperStarTheme())
                    Minecraft.getInstance().getSoundManager().play(new FadingSoundInstance(livingEntity, SoundRegistry.SUPER_STAR_THEME.get(),
                            SoundSource.AMBIENT, entity.getRandom(), handler.mv$getSuperStarCooldown(), 100));
                handler.mv$setPlayedSuperStarTheme(true);
                this.remove(RemovalReason.KILLED);
            }
        }
    }
}
