package com.wenxin2.marioverse.entities.ai.goals;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class NearestAttackableTagGoal extends TargetGoal {
    private static final int DEFAULT_RANDOM_INTERVAL = 10;
    protected final int randomInterval;
    @Nullable
    protected LivingEntity target;
    protected TargetingConditions targetConditions;
    private final TagKey<EntityType<?>> entityTag;

    public NearestAttackableTagGoal(Mob mob, TagKey<EntityType<?>> entityTag, boolean mustSee) {
        this(mob, entityTag, 10, mustSee, false, null);
    }

    public NearestAttackableTagGoal(Mob mob, TagKey<EntityType<?>> entityTag, int randomInterval, boolean mustSee, boolean mustReach, @Nullable Predicate<LivingEntity> selector) {
        super(mob, mustSee, mustReach);
        this.randomInterval = reducedTickDelay(randomInterval);
        this.entityTag = entityTag;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        this.targetConditions = TargetingConditions.forCombat().range(this.getFollowDistance()).selector(selector);
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        } else {
            this.findTarget();
            return this.target != null;
        }
    }

    protected AABB getTargetSearchArea(double followDistance) {
        return this.mob.getBoundingBox().inflate(followDistance, 4.0, followDistance);
    }

    protected void findTarget() {
        List<LivingEntity> potentialTargets = this.mob.level()
                .getEntitiesOfClass(LivingEntity.class,
                        this.getTargetSearchArea(this.getFollowDistance()), entity -> entity.getType().is(this.entityTag));

        this.target = this.mob.level()
                .getNearestEntity(potentialTargets, this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
    }
}