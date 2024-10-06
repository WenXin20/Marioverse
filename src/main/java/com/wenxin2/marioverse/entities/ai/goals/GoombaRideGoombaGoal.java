package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.GoombaEntity;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

public class GoombaRideGoombaGoal extends Goal {
    private final GoombaEntity goomba;
    private final float chanceToRide;
    private int cooldown;
    private static final int MAX_STACK_SIZE = 5;

    public GoombaRideGoombaGoal(GoombaEntity goomba, float chanceToRide) {
        this.goomba = goomba;
        this.chanceToRide = chanceToRide;
    }

    @Override
    public boolean canUse() {
        if (!this.goomba.isPassenger() && this.goomba.getPassengers().isEmpty()
                && !this.goomba.isVehicle() && !this.goomba.isSwimming() && this.cooldown == 0) {
            if (this.goomba.getRandom().nextFloat() < chanceToRide) {
                GoombaEntity targetGoomba = findNearbyGoombaToRide();
                return targetGoomba != null && canRide(targetGoomba);
            }
        }
        return false;
    }

    @Override
    public void start() {
        GoombaEntity targetGoomba = findNearbyGoombaToRide();
        if (targetGoomba != null && canRide(targetGoomba)) {
            this.goomba.tryToRide();
            this.goomba.startRiding(targetGoomba, true);
        }
        this.cooldown = 200 + this.goomba.getRandom().nextInt(400);
    }

    @Override
    public boolean canContinueToUse() {
        return this.goomba.isPassenger() && this.goomba.getVehicle() instanceof GoombaEntity;
    }

    @Override
    public void stop() {
        this.cooldown = 200;
        this.goomba.ride(false);
    }

    private GoombaEntity findNearbyGoombaToRide() {
        // Search for nearby Goombas within a certain radius that are not passengers
        List<GoombaEntity> nearbyGoombas =
                this.goomba.level().getEntitiesOfClass(GoombaEntity.class,
                        this.goomba.getBoundingBox().inflate(0.5D), goomba -> !this.goomba.isPassenger());

        for (GoombaEntity candidate : nearbyGoombas) {
            if (candidate != this.goomba && canRide(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean canRide(GoombaEntity targetGoomba) {
        if (targetGoomba.getPassengers().isEmpty()) {
            BlockPos targetPos = targetGoomba.blockPosition().above();
            BlockState blockAbove = this.goomba.level().getBlockState(targetPos);

            return blockAbove.isAir() && canStack(targetGoomba);
        }
        return false;
    }

    private boolean canStack(GoombaEntity targetGoomba) {
        int stackCount = 0;
        Entity current = targetGoomba;

        while (current.getVehicle() instanceof GoombaEntity) {
            current = current.getVehicle();
            stackCount++;
            if (stackCount >= MAX_STACK_SIZE) {
                return false;
            }
        }
        return true;
    }
}