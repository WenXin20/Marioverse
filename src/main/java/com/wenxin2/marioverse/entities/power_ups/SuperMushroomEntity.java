package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.PowerUpTypeRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
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

public class SuperMushroomEntity extends MushroomEntity implements GeoEntity, PowerUpSource {
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.super_mushroom.walk");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public SuperMushroomEntity(EntityType<? extends SuperMushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.SUPER_MUSHROOM;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, this::walkAnimation));
        controllers.add(DefaultAnimations.getSpawnController(this, state -> this, this.getSpawnDuration()));
    }

    @Override
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

    @Override
    public void collideWithEntity(Entity entity) {
        LivingEntity rider = entity.getControllingPassenger();

        if (entity.getType().is(TagRegistry.POWERS_UP_RIDER) && entity.hasControllingPassenger()
                && rider instanceof AbilitiesHandler handler)
            handler.applySuperMushroomPowerUp(this.level(), rider, this,
                    ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
        else if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler)
            handler.applySuperMushroomPowerUp(this.level(), livingEntity, this,
                    ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
    }
}
