package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import virtuoel.pehkui.api.ScaleTypes;

public class FireFlowerEntity extends BasePowerUpEntity implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.fire_flower.idle");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public FireFlowerEntity(EntityType<? extends FireFlowerEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
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
    public void tick() {
        super.tick();
        checkForCollisions();
    }

    @Override
    public void handleCollision(Entity entity) {
        if (!this.level().isClientSide) {

            if (entity instanceof Player player && !player.isSpectator()
                    && entity.getType().is(TagRegistry.FIRE_FLOWER_WHITELIST)) {
                if (player.getHealth() > ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
                    player.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
                    this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
                } else this.level().broadcastEntityEvent(this, (byte) 60); // Mushroom Transform particle
                if (!player.getType().is(TagRegistry.CONSUME_POWER_UPS_ENTITY_BLACKLIST)) {
                    this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    this.remove(RemovalReason.KILLED);
                }
            } else if (entity instanceof LivingEntity livingEntity
                    && entity.getType().is(TagRegistry.FIRE_FLOWER_WHITELIST)
                    && !(entity instanceof Player)) {
                if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get()) {
                    livingEntity.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
                    this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
                } else this.level().broadcastEntityEvent(this, (byte) 60); // Mushroom Transform particle
                if (!livingEntity.getType().is(TagRegistry.CONSUME_POWER_UPS_ENTITY_BLACKLIST)) {
                    this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    this.remove(RemovalReason.KILLED);
                }
            }
        }
    }
}
