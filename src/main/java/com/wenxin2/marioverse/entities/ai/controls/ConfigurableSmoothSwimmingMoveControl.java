package com.wenxin2.marioverse.entities.ai.controls;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class ConfigurableSmoothSwimmingMoveControl extends MoveControl {
    private static final float FULL_SPEED_TURN_THRESHOLD = 10.0F;
    private static final float STOP_TURN_THRESHOLD = 60.0F;

    private final int maxTurnX;
    private final int maxTurnY;
    private final float inWaterSpeedModifier;
    private final float outsideWaterSpeedModifier;
    private final float speedMultiplier;
    private final boolean applyGravity;

    public ConfigurableSmoothSwimmingMoveControl(Mob mob, int maxTurnX, int maxTurnY, float inWaterSpeedModifier,
                                                 float outsideWaterSpeedModifier, float speedMultiplier, boolean applyGravity) {
        super(mob);
        this.applyGravity = applyGravity;
        this.maxTurnX = maxTurnX;
        this.maxTurnY = maxTurnY;
        this.inWaterSpeedModifier = inWaterSpeedModifier;
        this.outsideWaterSpeedModifier = outsideWaterSpeedModifier;
        this.speedMultiplier = speedMultiplier;
    }

    @Override
    public void tick() {
        if (this.applyGravity && this.mob.isInWater())
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(0.0D, 0.005D, 0.0D));

        if (this.operation == Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            double dx = this.wantedX - this.mob.getX();
            double dy = this.wantedY - this.mob.getY();
            double dz = this.wantedZ - this.mob.getZ();

            double distanceSqr = dx * dx + dy * dy + dz * dz;

            if (distanceSqr < 2.5000003E-7F) {
                this.mob.setData(DataAttachmentRegistry.IS_MOVING, false);
                this.mob.setZza(0.0F);
                return;
            }

            float targetYaw = (float)(Mth.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;

            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, this.maxTurnY));
            this.mob.yBodyRot = this.mob.getYRot();
            this.mob.yHeadRot = this.mob.getYRot();

            float speed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * this.speedMultiplier);

            if (this.mob.isInWater()) {
                this.mob.setSpeed(speed * this.inWaterSpeedModifier);

                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);

                if (Math.abs(dy) > 1.0E-5F || Math.abs(horizontalDistance) > 1.0E-5F) {
                    float targetPitch = -((float)(Mth.atan2(dy, horizontalDistance) * 180.0D / Math.PI));
                    targetPitch = Mth.clamp(Mth.wrapDegrees(targetPitch), -this.maxTurnX, this.maxTurnX);

                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), targetPitch, 5.0F));
                }

                float cosPitch = Mth.cos(this.mob.getXRot() * ((float)Math.PI / 180.0F));
                float sinPitch = Mth.sin(this.mob.getXRot() * ((float)Math.PI / 180.0F));

                this.mob.zza = cosPitch * speed;
                this.mob.yya = -sinPitch * speed;
                this.mob.setData(DataAttachmentRegistry.IS_MOVING, true);
            } else {
                float turnAmount = Math.abs(Mth.wrapDegrees(this.mob.getYRot() - targetYaw));
                float turnFactor = getTurningSpeedFactor(turnAmount);

                this.mob.setSpeed(speed * this.outsideWaterSpeedModifier * turnFactor);
                this.mob.setData(DataAttachmentRegistry.IS_MOVING, false);
            }
        } else {
            this.mob.setData(DataAttachmentRegistry.IS_MOVING, false);
            this.mob.setSpeed(0.0F);
            this.mob.setXxa(0.0F);
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }
    }

    private static float getTurningSpeedFactor(float turnAmount) {
        return 1.0F - Mth.clamp((turnAmount - FULL_SPEED_TURN_THRESHOLD) / (STOP_TURN_THRESHOLD - FULL_SPEED_TURN_THRESHOLD), 0.0F, 1.0F);
    }
}