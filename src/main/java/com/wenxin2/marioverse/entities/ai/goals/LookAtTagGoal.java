package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public class LookAtTagGoal extends Goal {
    public static final float DEFAULT_PROBABILITY = 0.02F;
    @Nullable protected Entity lookAt;
    private final boolean onlyHorizontal;
    private int lookTime;
    protected final Mob mob;
    protected final TagKey<EntityType<?>> lookAtTag;
    protected final TargetingConditions lookAtContext;
    protected final float lookDistance;
    protected final float probability;

    public LookAtTagGoal(Mob mob, TagKey<EntityType<?>> entityTag, float lookDistance) {
        this(mob, entityTag, lookDistance, DEFAULT_PROBABILITY);
    }

    public LookAtTagGoal(Mob mob, TagKey<EntityType<?>> entityTag, float lookDistance, float probability) {
        this(mob, entityTag, lookDistance, probability, false);
    }

    public LookAtTagGoal(Mob mob, TagKey<EntityType<?>> entityTag, float lookDistance, float probability, boolean onlyHorizontal) {
        this.mob = mob;
        this.lookAtTag = entityTag;
        this.lookDistance = lookDistance;
        this.probability = probability;
        this.onlyHorizontal = onlyHorizontal;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        this.lookAtContext = TargetingConditions.forNonCombat().range(lookDistance);
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextFloat() >= this.probability
                || this.mob.getData(DataAttachmentRegistry.IS_HIDING.get())) {
            return false;
        } else {
            if (this.mob.getTarget() != null) {
                this.lookAt = this.mob.getTarget();
            }

            List<LivingEntity> list = this.mob.level().getEntitiesOfClass(LivingEntity.class,
                    this.mob.getBoundingBox().inflate(this.lookDistance, 3.0, this.lookDistance),
                    entity -> entity.getType().is(this.lookAtTag));

            this.lookAt = this.mob.level().getNearestEntity(list,
                    this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());

            return this.lookAt != null;
        }
    }

    @Override
    public boolean canContinueToUse() {
        if (this.mob.getData(DataAttachmentRegistry.IS_HIDING.get()))
            return false;
        else if (this.lookAt == null)
            return false;
        else if (this.lookAt.getData(DataAttachmentRegistry.IS_HIDING.get()))
            return false;
        else if (!this.lookAt.isAlive())
            return false;
        else return !(this.mob.distanceToSqr(this.lookAt) > (double) (this.lookDistance * this.lookDistance)) && this.lookTime > 0;
    }

    @Override
    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.lookAt = null;
    }

    @Override
    public void tick() {
        if (this.lookAt != null && this.lookAt.isAlive()) {
            double d0 = this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();
            this.mob.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
            this.lookTime--;
        }
    }
}