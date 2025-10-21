package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.EnumSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class ChargeAttackGoal extends Goal {
    private final Mob mob;
    
    public ChargeAttackGoal(Mob mob) {
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        LivingEntity entity = this.mob.getTarget();
        return entity != null
                && entity.isAlive()
                && !this.mob.getMoveControl().hasWanted()
                && this.mob.getRandom().nextInt(reducedTickDelay(7)) == 0
                && this.mob.distanceToSqr(entity) > 4.0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.getMoveControl().hasWanted() && this.mob.getData(DataAttachmentRegistry.IS_CHARGING.get())
                && !this.mob.getData(DataAttachmentRegistry.IS_HIDING.get())
                && this.mob.getTarget() != null && this.mob.getTarget().isAlive();
    }

    @Override
    public void start() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            Vec3 vec3 = livingentity.getEyePosition();
            this.mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
        }
        this.mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), true);
    }

    @Override
    public void stop() {
        this.mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null) {
            if (this.mob.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                this.mob.doHurtTarget(livingentity);
                this.mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), false);
            } else {
                double d0 = this.mob.distanceToSqr(livingentity);
                if (d0 < 9.0) {
                    Vec3 vec3 = livingentity.getEyePosition();
                    this.mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
                }
            }
        }
    }
}