package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.GoombaEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

public class GoombaSleepGoal extends Goal {
    private final GoombaEntity goomba;
    private final float chanceToSleep;
    private final int ticksBeforeSleepingAgain;
    private int ticksSleeping;
    private int cooldown;
    private int sleepingTime;

    public GoombaSleepGoal(GoombaEntity goomba, float chanceToSleep, int ticksSleeping, int ticksBeforeSleepingAgain) {
        this.goomba = goomba;
        this.ticksBeforeSleepingAgain = ticksBeforeSleepingAgain;
        this.ticksSleeping = ticksSleeping;
        this.chanceToSleep = chanceToSleep;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown == 0 && !this.goomba.isInWater()) {
            if (this.goomba.getRandom().nextInt() < this.chanceToSleep) {
                this.cooldown = ticksBeforeSleepingAgain;
                return true;
            } else this.cooldown = ticksBeforeSleepingAgain / 2;
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
        if (this.cooldown > 0) {
            this.cooldown--;
            this.goomba.getNavigation().stop();
            this.goomba.setXxa(0.0F);
            this.goomba.setSpeed(0.0F);
        }

        if (!this.goomba.isSleeping())
            this.goomba.tryToSleep();
        else this.goomba.checkForCollisionsAndWakeUp();

        if (this.sleepingTime >= this.ticksSleeping) {
            this.goomba.sit(false);
            this.goomba.sleep(false);
            this.ticksSleeping = 0;
        } else this.sleepingTime++;
    }

    @Override
    public void start() {
        this.goomba.tryToSleep();
    }

    @Override
    public void stop() {
        if (this.ticksSleeping == 0)
            this.goomba.sleep(false);
    }
}