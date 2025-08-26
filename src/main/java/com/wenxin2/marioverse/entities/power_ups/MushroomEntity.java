package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.ai.goals.ContinuousStrollGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
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

public class MushroomEntity extends BaseMushroomEntity implements GeoEntity {
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.super_mushroom.walk");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MushroomEntity(EntityType<? extends MushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ContinuousStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 0, this::walkAnimController));
    }

    @Override
    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if (this.isMoving()) {
            event.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        } else return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public void tick() {
        super.tick();
        this.checkForCollisions();
    }

    private boolean isMoving() {
        return this.getDeltaMovement().lengthSqr() > 0.01;
    }

    @Override
    public void checkForCollisions() {
        AABB boundingBox = this.getBoundingBox().inflate(0.1);
        List<Entity> entities = this.level().getEntities(this, boundingBox, entity -> entity != this);

        if (!entities.isEmpty()) {
            for (Entity entity : entities) {
                collideWithEntity(entity);
                break;
            }
        }
    }

    @Override
    public void collideWithEntity(Entity entity) {
        if (!this.level().isClientSide) {
            powerUp(this.level(), entity, this, ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
        }
    }

    public static void powerUp(Level world, Entity entity, @Nullable Entity mushroom, float healthHealed) {
        if (entity instanceof Player player && !player.isSpectator()
                && !player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (player.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS)
                    || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get())
                && player instanceof AbilitiesHandler handler) {
            handler.mv$setSuperMushroom(true);
            if (entity.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 25);

            if (!world.isClientSide) {
                if (player.getHealth() < player.getMaxHealth())
                    player.heal(healthHealed);
                if (mushroom != null) {
                    world.playSound(null, mushroom.blockPosition(), SoundRegistry.POWERS_UP.get(),
                            SoundSource.PLAYERS, 1.0F, 1.0F);
                    mushroom.remove(RemovalReason.KILLED);
                } else world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        } else if (entity instanceof LivingEntity livingEntity
                && !livingEntity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (livingEntity.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS)
                    || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get())
                && entity instanceof AbilitiesHandler handler) {
            handler.mv$setSuperMushroom(true);
            if (entity.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 25);

            if (!world.isClientSide) {
                if (livingEntity.getHealth() < livingEntity.getMaxHealth())
                    livingEntity.heal(healthHealed);
                if (mushroom != null) {
                    world.playSound(null, mushroom.blockPosition(), SoundRegistry.POWERS_UP.get(),
                            SoundSource.NEUTRAL, 1.0F, 1.0F);
                    mushroom.remove(RemovalReason.KILLED);
                } else world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(),
                        SoundSource.NEUTRAL, 1.0F, 1.0F);
            }
        }
    }
}
