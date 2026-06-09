package com.wenxin2.marioverse.entities.ai.controls;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class UndulatingSwimMoveControl extends MoveControl {
    private final PathfinderMob mob;

    public UndulatingSwimMoveControl(PathfinderMob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public void tick() {
        if (this.operation == Operation.MOVE_TO && !this.mob.getNavigation().isDone()) {
            Vec3 direction = new Vec3(this.wantedX - this.mob.getX(),
                    this.wantedY - this.mob.getY(),
                    this.wantedZ - this.mob.getZ());
            double distance = direction.length();

            if (distance < 1.0E-6D) {
                this.mob.setSpeed(0.0F);
                this.mob.setData(DataAttachmentRegistry.IS_MOVING, false);
                return;
            }

            double xNorm = direction.x / distance;
            double yNorm = direction.y / distance;
            double zNorm = direction.z / distance;
            float targetYaw = (float)(Mth.atan2(direction.z, direction.x) * 180.0D / Math.PI) - 90.0F;

            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), targetYaw, 90.0F));
            this.mob.yBodyRot = this.mob.getYRot();

            float targetSpeed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
            float currentSpeed = Mth.lerp(0.125F, this.mob.getSpeed(), targetSpeed);

            this.mob.setSpeed(currentSpeed);

            double sway = Math.sin((this.mob.tickCount + this.mob.getId()) * 0.5D) * 0.02D;
            double yawCos = Math.cos(this.mob.getYRot() * (Math.PI / 180.0F));
            double yawSin = Math.sin(this.mob.getYRot() * (Math.PI / 180.0F));
            double bob = Math.sin((this.mob.tickCount + this.mob.getId()) * 0.75D) * 0.02D;

            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(sway * yawCos,
                    bob * (yawSin + yawCos) * 0.25D + currentSpeed * yNorm * 0.3D, sway * yawSin));

            LookControl lookControl = this.mob.getLookControl();
            double lookX = this.mob.getX() + xNorm * 2.0D;
            double lookY = this.mob.getEyeY() + yNorm;
            double lookZ = this.mob.getZ() + zNorm * 2.0D;
            double wantedLookX = lookControl.getWantedX();
            double wantedLookY = lookControl.getWantedY();
            double wantedLookZ = lookControl.getWantedZ();

            if (!lookControl.isLookingAtTarget()) {
                wantedLookX = lookX;
                wantedLookY = lookY;
                wantedLookZ = lookZ;
            }

            lookControl.setLookAt(Mth.lerp(0.125D, wantedLookX, lookX),
                    Mth.lerp(0.125D, wantedLookY, lookY),
                    Mth.lerp(0.125D, wantedLookZ, lookZ),
                    10.0F, 40.0F);

            this.mob.setData(DataAttachmentRegistry.IS_MOVING, true);
        } else {
            this.mob.setSpeed(0.0F);
            this.mob.setData(DataAttachmentRegistry.IS_MOVING, false);
        }
    }
}
