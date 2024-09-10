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
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
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

public class MushroomEntity extends BaseMushroomEntity implements GeoEntity {
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.mushroom.walking");
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MushroomEntity(EntityType<? extends MushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walking", 5, this::walkAnimController));
    }

    @Override
    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if (event.isMoving())
            return event.setAndContinue(WALK_ANIM);
        return PlayState.STOP;
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
            this.level().playSound(null, this.blockPosition(), SoundRegistry.POWER_UP_SPAWNS.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

            if (entity instanceof Player player && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                if (player.getHealth() > ConfigRegistry.HEALTH_SHRINK_PLAYERS.get()) {
                    if (ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()) {
                        ScaleTypes.HEIGHT.getScaleData(player).setTargetScale(1.0F);
                        ScaleTypes.WIDTH.getScaleData(player).setTargetScale(1.0F);
                    }
                    player.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                }
                if (player.getHealth() < player.getMaxHealth())
                    player.heal(ConfigRegistry.MUSHROOM_HEAL_AMT.get().floatValue());
                // Poof particle
                this.level().broadcastEntityEvent(this, (byte) 20);
                this.remove(Entity.RemovalReason.KILLED);
            } else if (entity instanceof LivingEntity livingEntity && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                    && !livingEntity.getType().is(TagRegistry.DAMAGE_SHRINKS_ENTITY_BLACKLIST)) {
                if (livingEntity.getHealth() > livingEntity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get()) {
                    if (ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()) {
                        ScaleTypes.HEIGHT.getScaleData(livingEntity).setTargetScale(1.0F);
                        ScaleTypes.WIDTH.getScaleData(livingEntity).setTargetScale(1.0F);
                    }
                    livingEntity.getPersistentData().putBoolean("marioverse:has_mushroom", Boolean.TRUE);
                }
                if (livingEntity.getHealth() < livingEntity.getMaxHealth())
                    livingEntity.heal(ConfigRegistry.MUSHROOM_HEAL_AMT.get().floatValue());
                // Poof particle
                this.level().broadcastEntityEvent(this, (byte) 20);
                this.remove(Entity.RemovalReason.KILLED);
            }
        }
    }
}
