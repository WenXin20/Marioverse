package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.GoombaEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

public class GoombaSleepGoal extends Goal {
    private final GoombaEntity goomba;
    private final int chanceToSleep;
    private final int ticksBeforeSleepingAgain;
    private final int ticksSleeping;
    private int cooldown;

    public GoombaSleepGoal(GoombaEntity goomba, int chanceToSleep, int ticksSleeping, int ticksBeforeSleepingAgain) {
        this.goomba = goomba;
        this.ticksBeforeSleepingAgain = ticksBeforeSleepingAgain;
        this.ticksSleeping = ticksSleeping;
        this.chanceToSleep = chanceToSleep;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown == 0 && !this.goomba.isInWater()) {
            return this.goomba.getRandom().nextInt(this.chanceToSleep) == 0;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.goomba.isInWater()
                && this.goomba.getRandom().nextInt(ticksSleeping / 2) != 1;
    }


    @Override
    public void tick() {
        if (!this.goomba.isSleeping())
            this.goomba.tryToSleep();
        else this.goomba.checkForCollisionsAndWakeUp();
    }

    @Override
    public void start() {
        this.goomba.tryToSleep();
        this.cooldown = this.goomba.getRandom().nextInt(ticksSleeping) + 100;
    }

    @Override
    public void stop() {
        this.cooldown = this.goomba.getRandom().nextInt(ticksBeforeSleepingAgain);
        this.goomba.sleep(false);
    }
}