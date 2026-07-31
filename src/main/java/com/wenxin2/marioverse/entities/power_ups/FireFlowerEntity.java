package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.ai.controls.JumpInPlaceMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.ContinuousJumpGoal;
import com.wenxin2.marioverse.entities.ai.goals.LookAtEntityTagGoal;
import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
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

public class FireFlowerEntity extends AbstractPowerUpEntity implements GeoEntity, PowerUpSource {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.fire_flower.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FireFlowerEntity(EntityType<? extends FireFlowerEntity> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new JumpInPlaceMoveControl(this, 100, null, 1.0F, 1.0F);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.FIRE_FLOWER;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new ContinuousJumpGoal(this));
        this.goalSelector.addGoal(1, new LookAtEntityTagGoal(this, TagRegistry.CAN_CONSUME_FIRE_FLOWERS, 8.0F, 1.0F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 0, this::idleAnimController));
        controllers.add(DefaultAnimations.getSpawnController(this, state -> this, 20));
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
    public void collideWithEntity(Entity entity) {
        LivingEntity rider = entity.getControllingPassenger();

        if (entity.getType().is(TagRegistry.POWERS_UP_RIDER) && entity.hasControllingPassenger()
                && rider instanceof AbilitiesHandler handler)
            handler.applyFireFlowerPowerUp(this.level(), rider, this);
        else if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler)
            handler.applyFireFlowerPowerUp(this.level(), livingEntity, this);
    }
}
