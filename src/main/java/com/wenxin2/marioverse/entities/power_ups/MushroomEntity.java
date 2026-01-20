package com.wenxin2.marioverse.entities.power_ups;

import com.wenxin2.marioverse.entities.ai.goals.ContinuousStrollGoal;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

    public MushroomEntity(EntityType<? extends MushroomEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new ContinuousStrollGoal(this, 0.7D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public void tick() {
        super.tick();
        this.checkForCollisions();
    }

    public boolean isMoving() {
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
    }
}
