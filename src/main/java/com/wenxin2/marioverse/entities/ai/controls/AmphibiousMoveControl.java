package com.wenxin2.marioverse.entities.ai.controls;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class AmphibiousMoveControl extends MoveControl {
    private static final float FULL_SPEED_TURN_THRESHOLD = 10.0F;
    private static final float STOP_TURN_THRESHOLD = 60.0F;
    private final int maxTurnX;
    private final int maxTurnY;
    private final float inWaterSpeedModifier;
    private final float outsideWaterSpeedModifier;
    private final boolean applyGravity;
    private final PathfinderMob mob;

    public AmphibiousMoveControl(PathfinderMob mob, int maxTurnX, int maxTurnY, float inWaterSpeedModifier,
                                 float outsideWaterSpeedModifier, boolean applyGravity) {
        super(mob);
        this.mob = mob;
        this.maxTurnX = maxTurnX;
        this.maxTurnY = maxTurnY;
        this.inWaterSpeedModifier = inWaterSpeedModifier;
        this.outsideWaterSpeedModifier = outsideWaterSpeedModifier;
        this.applyGravity = applyGravity;
    }

    @Override
    public void tick() {
        if (this.mob.isInWaterOrBubble()) {
            this.moveInWater(this.mob.isInWaterOrBubble());
        } else {
            this.moveOnLand();
        }
    }

    private void moveOnLand() {
        super.tick();
    }

    private void moveInWater(boolean inWater) {
        if (this.applyGravity && this.mob.isInWater()) {
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0, 0.005, 0.0));
        }

        if (this.operation != MoveControl.Operation.MOVE_TO || this.mob.getNavigation().isDone()) {
            this.resetMovement();
            return;
        }

        double d0 = this.wantedX - this.mob.getX();
        double d1 = this.wantedY - this.mob.getY();
        double d2 = this.wantedZ - this.mob.getZ();
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;

        if (d3 < 2.5E-7F) {
            this.resetMovement();
        } else {
            float targetYaw = (float) (Mth.atan2(d2, d0) * 180.0F / Math.PI) - 90.0F;
            this.updateRotation(targetYaw);

            float speed = (float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));

            if (inWater) {
                this.handleWaterMovement(d0, d1, d2, speed);
            } else {
                this.handleLandMovement(targetYaw, speed);
            }
        }
    }

    private void handleWaterMovement(double d0, double d1, double d2, float speed) {
        this.mob.setSpeed(speed * this.inWaterSpeedModifier);

        double horizontalDistance = Math.sqrt(d0 * d0 + d2 * d2);
        if (Math.abs(d1) > 1.0E-5F || Math.abs(horizontalDistance) > 1.0E-5F) {
            float pitch = -((float) (Mth.atan2(d1, horizontalDistance) * 180.0F / Math.PI));
            pitch = Mth.clamp(Mth.wrapDegrees(pitch), -this.maxTurnX, this.maxTurnX);
            this.mob.setXRot(this.rotlerp(this.mob.getXRot(), pitch, 5.0F));
        }

        float cosPitch = Mth.cos(this.mob.getXRot() * (float) (Math.PI / 180.0));
        float sinPitch = Mth.sin(this.mob.getXRot() * (float) (Math.PI / 180.0));
        this.mob.zza = cosPitch * speed;
        this.mob.yya = -sinPitch * speed;
    }

    private void handleLandMovement(float targetYaw, float speed) {
        float turnSpeed = Math.abs(Mth.wrapDegrees(this.mob.getYRot() - targetYaw));
        float speedModifier = getTurningSpeedFactor(turnSpeed);
        this.mob.setSpeed(speed * this.outsideWaterSpeedModifier * speedModifier);
    }

    private void resetMovement() {
        this.mob.setSpeed(0.0F);
        this.mob.setXxa(0.0F);
        this.mob.setYya(0.0F);
        this.mob.setZza(0.0F);
    }

    private void updateRotation(float targetYaw) {
        float rotation = this.rotlerp(this.mob.getYRot(), targetYaw, this.maxTurnY);
        this.mob.setYRot(rotation);
        this.mob.yBodyRot = rotation;
        this.mob.yHeadRot = rotation;
    }

    private static float getTurningSpeedFactor(float turnSpeed) {
        return 1.0F - Mth.clamp((turnSpeed - 10.0F) / 50.0F, 0.0F, 1.0F);
    }
}