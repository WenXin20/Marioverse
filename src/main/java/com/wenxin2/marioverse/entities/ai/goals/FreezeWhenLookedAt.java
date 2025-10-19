package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.BooEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

public class FreezeWhenLookedAt extends Goal {
    private final Mob mob;
    @Nullable private LivingEntity target;
    private final TagKey<EntityType<?>> entityTag;
    private static final double MAX_DISTANCE_SQR = 256;
    private static final double FOV_ANGLE = 160;

    public FreezeWhenLookedAt(Mob mob, TagKey<EntityType<?>> entityTag) {
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        this.entityTag = entityTag;
        this.mob = mob;
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        } else if (this.target.getType().is(entityTag)) {
            Vec3 posEye = this.target.getEyePosition();
            Vec3 posMob = this.mob.position();
            double distance = posEye.distanceToSqr(posMob);
            Vec3 targetView = this.target.getViewVector(1.0F).normalize();
            Vec3 toMob = this.mob.position().subtract(posEye).normalize();
            double dot = targetView.dot(toMob);

            return dot > Math.cos(Math.toRadians(FOV_ANGLE / 2.0)) && distance <= MAX_DISTANCE_SQR;
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse();
    }

    @Override
    public void start() {
        this.mob.getNavigation().stop();
        this.mob.setDeltaMovement(Vec3.ZERO);
        this.mob.setSpeed(0);
        this.mob.getLookControl().setLookAt(this.mob.getX(), this.mob.getY(), this.mob.getZ());
        this.mob.setYHeadRot(this.mob.getYRot());
        this.mob.yBodyRot = this.mob.getYRot();
        if (this.mob instanceof BooEntity)
            this.mob.setData(DataAttachmentRegistry.IS_HIDING.get(), true);
    }

    @Override
    public void tick() {
        if (this.target != null)
            this.mob.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
    }

    @Override
    public void stop() {
        this.target = null;
        if (this.mob instanceof BooEntity)
            this.mob.setData(DataAttachmentRegistry.IS_HIDING.get(), false);
    }
}
