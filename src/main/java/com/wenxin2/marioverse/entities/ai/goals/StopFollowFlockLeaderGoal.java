package com.wenxin2.marioverse.entities.ai.goals;

import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;

public class StopFollowFlockLeaderGoal extends FollowFlockLeaderGoal {
    private final AbstractSchoolingFish mob;

    public StopFollowFlockLeaderGoal(AbstractSchoolingFish mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public void stop() {
        if (this.mob instanceof AbstractSchoolingFish fish && fish.isFollower())
            this.mob.stopFollowing();
    }
}