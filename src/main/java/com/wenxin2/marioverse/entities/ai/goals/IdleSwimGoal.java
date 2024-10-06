package com.wenxin2.marioverse.entities.ai.goals;

import java.util.EnumSet;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

public class IdleSwimGoal extends Goal {
    private final PathfinderMob mob;
    private final double idleSpeed;

    public IdleSwimGoal(PathfinderMob mob, double idleSpeed) {
        this.mob = mob;
        this.idleSpeed = idleSpeed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return mob.isInWaterOrBubble() || mob.isInLava();
    }

    @Override
    public void tick() {
        mob.getNavigation().moveTo(mob.getX() + (mob.getRandom().nextDouble()),
                mob.getY() + (mob.getRandom().nextDouble()),
                mob.getZ() + (mob.getRandom().nextDouble()),
                idleSpeed);
    }
}
