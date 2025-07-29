package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.GoombaEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.state.BlockState;

public class GoombaRideGoal extends Goal {
    private final GoombaEntity goomba;
    private final float chanceToRide;
    private int cooldown;
    private int rideDuration;
    private int maxRideDuration;
    private int rideCooldown = 0;

    public GoombaRideGoal(GoombaEntity goomba, float chanceToRide) {
        this.goomba = goomba;
        this.chanceToRide = chanceToRide;
    }

    @Override
    public boolean canUse() {
        if (rideCooldown > 0) {
            rideCooldown--;
            return false;
        }

        if (!this.goomba.isPassenger() && this.goomba.getPassengers().isEmpty()
                && !this.goomba.isVehicle() && !this.goomba.isInWaterOrBubble() && this.cooldown == 0 && this.rideCooldown == 0) {
            if (this.goomba.getRandom().nextFloat() < chanceToRide) {
                Entity targetGoomba = findNearbyGoombaToRide();
                rideCooldown = 1200;
                return targetGoomba != null && this.canRide(targetGoomba);
            } else rideCooldown = 1200;
        }
        return false;
    }

    @Override
    public void start() {
        Entity targetGoomba = findNearbyGoombaToRide();
        if (targetGoomba != null && this.canRide(targetGoomba) && !this.goomba.isInWaterOrBubble()) {
            this.goomba.tryToRide();
            this.goomba.startRiding(targetGoomba, true);
            this.rideDuration = 0;
            this.maxRideDuration = 1200 + this.goomba.getRandom().nextInt(1200);
        }
        this.cooldown = 200 + this.goomba.getRandom().nextInt(400);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.goomba.isPassenger() && this.goomba.getVehicle() != null
                && this.goomba.getVehicle().getType().is(TagRegistry.GOOMBA_CAN_RIDE)) {
            Entity vehicle = this.goomba.getVehicle();

            if (vehicle.isInWaterOrBubble() || this.rideDuration >= this.maxRideDuration) {
                this.stop();
                return false;
            }
            this.rideDuration++;
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        this.cooldown = 200;
        this.rideCooldown = 1200;
        this.goomba.ride(false);
        this.goomba.stopRiding();
    }

    private Entity findNearbyGoombaToRide() {
        List<Entity> nearbyGoombas =
                this.goomba.level().getEntities(this.goomba,
                        this.goomba.getBoundingBox().inflate(0.5D, ConfigRegistry.MAX_GOOMBA_STACK.get(), 0.5D), goomba -> !this.goomba.isPassenger());

        for (Entity candidate : nearbyGoombas) {
            if (candidate != this.goomba && canRide(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    private boolean canRide(Entity targetGoomba) {
        if (targetGoomba.getPassengers().isEmpty()) {
            BlockPos targetPos = targetGoomba.blockPosition().above();
            BlockState blockAbove = this.goomba.level().getBlockState(targetPos);

            return blockAbove.isAir() && canStack(targetGoomba);
        }
        return false;
    }

    private boolean canStack(Entity targetGoomba) {
        int stackCount = 0;
        Entity current = targetGoomba;

        while (current.getVehicle() != null && current.getVehicle().getType().is(TagRegistry.GOOMBA_CAN_RIDE)) {
            if (stackCount >= ConfigRegistry.MAX_GOOMBA_STACK.get())
                return false;
            current = current.getVehicle();
            stackCount++;
        }
        return true;
    }
}