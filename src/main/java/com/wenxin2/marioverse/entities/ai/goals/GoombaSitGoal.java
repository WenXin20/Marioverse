package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.GoombaEntity;
import java.util.EnumSet;
import net.minecraft.world.entity.ai.goal.Goal;

public class GoombaSitGoal extends Goal {
    private final GoombaEntity goomba;
    private final float chanceToSit;
    private final int ticksBeforeSittingAgain;
    private final int ticksBeforeSleeping;
    private int ticksSitting;
    private int cooldown;
    private int sittingTime;

    public GoombaSitGoal(GoombaEntity goomba, float chanceToSit, int ticksSitting, int ticksBeforeSittingAgain, int ticksBeforeSleeping) {
        this.goomba = goomba;
        this.chanceToSit = chanceToSit;
        this.ticksSitting = ticksSitting;
        this.ticksBeforeSittingAgain = ticksBeforeSittingAgain;
        this.ticksBeforeSleeping = ticksBeforeSleeping;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.cooldown == 0 && !this.goomba.isInWater() && !this.goomba.isSitting()) {
            if (this.goomba.getRandom().nextInt() < chanceToSit) {
                this.cooldown = ticksBeforeSittingAgain;
                return true;
            } else this.cooldown = ticksBeforeSittingAgain / 2;
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
    }

    @Override
    public void stop() {
        if (this.ticksSitting == 0)
            this.goomba.sit(false);
    }

    @Override
    public void tick() {
        if (this.cooldown > 0) {
            this.cooldown--;
            this.goomba.getNavigation().stop();
            this.goomba.setXxa(0.0F);
            this.goomba.setSpeed(0.0F);
        }

        if (this.sittingTime >= this.ticksSitting) {
            this.goomba.sit(false);
            this.goomba.sleep(false);
            this.ticksSitting = 0;
        } else {
            this.sittingTime++;
            if (this.sittingTime >= this.ticksBeforeSleeping)
                this.goomba.sleep(true);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}