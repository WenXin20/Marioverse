package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.ai.goals.ContinuousStrollGoal;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
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
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.mushroom.walk");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MushroomEntity(EntityType<? extends MushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ContinuousStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
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

        for (Entity entity : entities) {
            handleCollision(entity);
            break;
        }
    }

    @Override
    public void handleCollision(Entity entity) {
        if (!this.level().isClientSide) {
            if (entity instanceof Player player && !player.isSpectator()
                    && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                    && !player.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)) {
                if (!player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                    if (player.getHealth() > ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
                        this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
                        player.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                    } else {
                        player.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                        this.level().broadcastEntityEvent(player, (byte) 125); // Mushroom Transform particle
                    }
                }
                if (!this.level().isClientSide) {
                    if (player.getHealth() < player.getMaxHealth())
                        player.heal(ConfigRegistry.MUSHROOM_HEAL_AMT.get().floatValue());
                    if (!player.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                        this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                        this.remove(Entity.RemovalReason.KILLED);
                    }
                }
            } else if (entity instanceof LivingEntity livingEntity && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                    && !livingEntity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)) {
                if (!livingEntity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                    if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get()) {
                        this.level().broadcastEntityEvent(this, (byte) 20); // Poof particle
                        livingEntity.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                    } else {
                        livingEntity.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                        this.level().broadcastEntityEvent(livingEntity, (byte) 125); // Mushroom Transform particle
                    }
                }

                if (!this.level().isClientSide) {
                    if (livingEntity.getHealth() < livingEntity.getMaxHealth())
                        livingEntity.heal(ConfigRegistry.MUSHROOM_HEAL_AMT.get().floatValue());
                    if (!livingEntity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                        this.level().playSound(null, this.blockPosition(), SoundRegistry.PLAYER_POWERS_UP.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                        this.remove(Entity.RemovalReason.KILLED);
                    }
                }
            }
        }
    }
}
