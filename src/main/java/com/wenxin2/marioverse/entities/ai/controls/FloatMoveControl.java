package com.wenxin2.marioverse.entities.ai.controls;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;

public class FloatMoveControl extends MoveControl {
    private final Mob mob;
    
    public FloatMoveControl(Mob mob) {
        super(mob);
        this.mob = mob;
    }

    @Override
    public void tick() {
        if (this.operation == MoveControl.Operation.MOVE_TO) {
            Vec3 vec3 = new Vec3(this.wantedX - mob.getX(), this.wantedY - mob.getY(), this.wantedZ - mob.getZ());
            double d0 = vec3.length();
            if (d0 < mob.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                mob.setDeltaMovement(mob.getDeltaMovement().scale(0.5));
            } else {
                mob.setDeltaMovement(mob.getDeltaMovement().add(vec3.scale(this.speedModifier * 0.05 / d0)));
                if (mob.getTarget() == null) {
                    Vec3 vec31 = mob.getDeltaMovement();
                    mob.setYRot(-((float) Mth.atan2(vec31.x, vec31.z)) * (180.0F / (float)Math.PI));
                    mob.yBodyRot = mob.getYRot();
                } else {
                    double d2 = mob.getTarget().getX() - mob.getX();
                    double d1 = mob.getTarget().getZ() - mob.getZ();
                    mob.setYRot(-((float)Mth.atan2(d2, d1)) * (180.0F / (float)Math.PI));
                    mob.yBodyRot = mob.getYRot();
                }
            }
        }
    }
}
