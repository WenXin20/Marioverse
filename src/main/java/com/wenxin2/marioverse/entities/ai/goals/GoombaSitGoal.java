package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.GoombaEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class GoombaSitGoal extends Goal {
    private final GoombaEntity goomba;
    private final int chanceToSit;
    private final int ticksBeforeSittingAgain;
    private final int ticksBeforeSleeping;
    private final int ticksSitting;
    private int cooldown;
    private int sittingTime;

    public GoombaSitGoal(GoombaEntity goomba, int chanceToSit, int ticksSitting, int ticksBeforeSittingAgain, int ticksBeforeSleeping) {
        this.goomba = goomba;
        this.chanceToSit = chanceToSit;
        this.ticksSitting = ticksSitting;
        this.ticksBeforeSittingAgain = ticksBeforeSittingAgain;
        this.ticksBeforeSleeping = ticksBeforeSleeping;
    }

    @Override
    public boolean canUse() {
        if (this.cooldown == 0 && !this.goomba.isInWater() && !this.goomba.isSitting()) {
            return this.goomba.getRandom().nextInt(this.chanceToSit) == 0;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.goomba.isSitting() && this.sittingTime < this.ticksSitting;
    }

    @Override
    public void start() {
        this.goomba.tryToSit();
        this.sittingTime = 0;
        this.cooldown = ticksBeforeSittingAgain;
    }

    @Override
    public void stop() {
        this.goomba.sit(false);
        this.cooldown = this.ticksBeforeSittingAgain;
        this.sittingTime = 0;
    }

    @Override
    public void tick() {
        if (this.sittingTime >= this.ticksSitting) {
            this.goomba.sit(false);
            this.goomba.sleep(false);
        } else {
            this.sittingTime++;
            if (this.sittingTime >= this.ticksBeforeSleeping) {
                this.goomba.sleep(true);
            }
        }
    }

//    @Override
//    public boolean requiresUpdateEveryTick() {
//        return true;
//    }
}