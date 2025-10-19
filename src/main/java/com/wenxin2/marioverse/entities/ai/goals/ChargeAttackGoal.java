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
        LivingEntity entity = mob.getTarget();
        return entity != null
                && entity.isAlive()
                && !mob.getMoveControl().hasWanted()
                && mob.getRandom().nextInt(reducedTickDelay(7)) == 0
                && mob.distanceToSqr(entity) > 4.0;
    }

    @Override
    public boolean canContinueToUse() {
        return mob.getMoveControl().hasWanted() && mob.getData(DataAttachmentRegistry.IS_CHARGING.get())
                && !this.mob.getData(DataAttachmentRegistry.IS_HIDING.get())
                && mob.getTarget() != null && mob.getTarget().isAlive();
    }

    @Override
    public void start() {
        LivingEntity livingentity = mob.getTarget();
        if (livingentity != null) {
            Vec3 vec3 = livingentity.getEyePosition();
            mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
        }

        mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), true);
        mob.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
    }

    @Override
    public void stop() {
        mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingentity = mob.getTarget();
        if (livingentity != null) {
            if (mob.getBoundingBox().intersects(livingentity.getBoundingBox())) {
                mob.doHurtTarget(livingentity);
                mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), false);
            } else {
                double d0 = mob.distanceToSqr(livingentity);
                if (d0 < 9.0) {
                    Vec3 vec3 = livingentity.getEyePosition();
                    mob.getMoveControl().setWantedPosition(vec3.x, vec3.y, vec3.z, 1.0);
                }
            }
        }
    }
}