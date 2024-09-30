package com.wenxin2.marioverse.entities.ai;

import com.wenxin2.marioverse.entities.GoombaEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

public class RandomSitGoal extends Goal {
    private final GoombaEntity goomba;
    private int sitTimer;
    private int cooldown;
    private final int chanceToSit; // Chance to sit (higher means less frequent)
    private final int maxSitDuration; // Maximum duration to sit

    public RandomSitGoal(GoombaEntity goomba, int chanceToSit, int maxSitDuration) {
        this.goomba = goomba;
        this.chanceToSit = chanceToSit;
        this.maxSitDuration = maxSitDuration;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        // Only use the sit goal if not already sitting and the cooldown has passed
        if (this.cooldown > 0) {
            this.cooldown--;
            return false;
        }

        // Randomly decide to sit based on the configured chance
        if (this.goomba.getRandom().nextInt(this.chanceToSit) == 0) {
            this.sitTimer = this.goomba.getRandom().nextInt(this.maxSitDuration) + 40; // Sit for 2 to maxSitDuration seconds
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        // Continue sitting as long as the sit timer is active
        return this.sitTimer > 0;
    }

    @Override
    public void start() {
        this.goomba.getNavigation().stop(); // Stop moving
    }

    @Override
    public void stop() {
        this.cooldown = this.goomba.getRandom().nextInt(200) + 100; // Cooldown before sitting again
        this.sitTimer = 0; // Reset sit timer
    }

    @Override
    public void tick() {
        this.sitTimer--;
        this.goomba.getLookControl().setLookAt(goomba.getX(), goomba.getY(), goomba.getZ()); // Look in place
    }
}
