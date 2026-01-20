package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.utils.AbilitiesHandler;
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
import software.bernie.geckolib.util.GeckoLibUtil;

public class MiniMushroomEntity extends MushroomEntity implements GeoEntity {
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("animation.mini_mushroom.walk");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MiniMushroomEntity(EntityType<? extends MiniMushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, this::walkAnimController));
    }

    @Override
    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
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
        if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler)
            handler.applyMiniMushroomPowerUp(this.level(), livingEntity, this);
    }
}
