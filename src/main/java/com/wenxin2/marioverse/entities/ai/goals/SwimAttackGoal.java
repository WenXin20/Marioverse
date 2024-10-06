package com.wenxin2.marioverse.entities.ai.goals;

import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class SwimAttackGoal<T extends LivingEntity> extends TargetGoal {
    private final PathfinderMob pathfinderMob;
    private final double attackSpeed;
    private final Class<T> targetType;
    private LivingEntity target;
    protected TargetingConditions targetConditions;

    public SwimAttackGoal(PathfinderMob pathfinderMob, Class<T> targetType, double attackSpeed) {
        super(pathfinderMob, true);
        this.pathfinderMob = pathfinderMob;
        this.attackSpeed = attackSpeed;
        this.targetType = targetType;
    }

    @Override
    public boolean canUse() {
        List<T> potentialTargets = this.pathfinderMob.level().getEntitiesOfClass(this.targetType, this.pathfinderMob.getBoundingBox().inflate(this.getFollowDistance(), 4.0, this.getFollowDistance()),
                entity -> entity != null && entity.isAlive() && this.canAttack(entity, TargetingConditions.DEFAULT));

        if (!potentialTargets.isEmpty()) {
            this.target = potentialTargets.getFirst();
            return true;
        }

        return false;
    }

    @Override
    public void start() {
        if (this.target != null) {
            this.pathfinderMob.setTarget(this.target);
            this.pathfinderMob.getNavigation().moveTo(this.target, this.attackSpeed);
        }
    }

    @Override
    public void tick() {
        if (this.target != null && this.target.isAlive()) {
            this.pathfinderMob.getNavigation().moveTo(this.target.getX(), this.target.getY(), this.target.getZ(), this.attackSpeed);
            this.pathfinderMob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
        } else {
            this.stop();
        }
    }
}
