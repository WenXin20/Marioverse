package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.GoombaEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

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
        if (this.cooldown > 0 && !this.goomba.isSleeping())
            this.cooldown--;

        if (this.cooldown == 0 && !this.goomba.isInWater() && !this.goomba.isSleeping()) {
            if (this.goomba.getRandom().nextInt() < this.chanceToSleep) {
                this.cooldown = ticksBeforeSleepingAgain;
                return true;
            } else this.cooldown = ticksBeforeSleepingAgain / 2;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.goomba.isInWater() && ticksSleeping > 0;
    }

    @Override
    public void start() {
        this.goomba.tryToSleep();
    }

    @Override
    public void stop() {
        this.cooldown = ticksBeforeSleepingAgain;
        this.goomba.sleep(false);
    }

    @Override
    public void tick() {
        if (this.cooldown > 0) {
            this.goomba.getNavigation().stop();
            this.goomba.setXxa(0.0F);
            this.goomba.setSpeed(0.0F);
        }

        if (!this.goomba.isSleeping())
            this.goomba.tryToSleep();
        else this.goomba.checkForCollisionsAndWakeUp();

        if (this.sleepingTime >= this.ticksSleeping)
            this.stop();
        else this.sleepingTime++;
    }
}