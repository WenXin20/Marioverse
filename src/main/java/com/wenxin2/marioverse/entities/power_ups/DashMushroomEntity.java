package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.items.DashMushroomItem;
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
import net.minecraft.world.entity.player.Player;
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

public class DashMushroomEntity extends MushroomEntity implements GeoEntity, PowerUpSource {
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public DashMushroomEntity(EntityType<? extends DashMushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public Holder<PowerUpType> getPowerUpType() {
        return PowerUpTypeRegistry.DASH_MUSHROOM;
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
        Level level = entity.level();

        if (!entity.getType().is(TagRegistry.DASH_MUSHROOM_CANNOT_BOOST)) {
            if (entity.isVehicle())
                DashMushroomItem.mushroomAbilities(null, level, entity, ConfigRegistry.VEHICLE_MUSHROOM_BOOST_STRENGTH.get(), true, false);
            else DashMushroomItem.mushroomAbilities(null, level, entity, ConfigRegistry.DASH_MUSHROOM_BOOST_STRENGTH.get(), true, false);
            this.discard();
        }
    }
}
