package com.wenxin2.marioverse.entities.ai.goals;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class AvoidEntityTagGoal extends Goal {
    protected final PathfinderMob mob;
    private final double walkSpeed;
    private final double sprintSpeed;
    @Nullable protected LivingEntity entityToAvoid;
    protected final float detectionRadius;
    @Nullable protected Path escapePath;
    protected final PathNavigation navigation;
    protected final TagKey<EntityType<?>> entityTag;
    protected final Predicate<LivingEntity> avoidPredicate;
    protected final Predicate<LivingEntity> targetSelectPredicate;
    private final TargetingConditions targetingConditions;

    public AvoidEntityTagGoal(PathfinderMob mob, TagKey<EntityType<?>> entityTag, float detectionRadius, double walkSpeed, double sprintSpeed) {
        this(mob, entityTag, entity -> true, detectionRadius, walkSpeed, sprintSpeed, EntitySelector.NO_CREATIVE_OR_SPECTATOR::test);
    }

    public AvoidEntityTagGoal(PathfinderMob mob, TagKey<EntityType<?>> entityTag, Predicate<LivingEntity> avoidPredicate,
            float detectionRadius, double walkSpeed, double sprintSpeed, Predicate<LivingEntity> targetSelectPredicate) {
        this.mob = mob;
        this.entityTag = entityTag;
        this.avoidPredicate = avoidPredicate;
        this.detectionRadius = detectionRadius;
        this.walkSpeed = walkSpeed;
        this.sprintSpeed = sprintSpeed;
        this.targetSelectPredicate = targetSelectPredicate;
        this.navigation = mob.getNavigation();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        this.targetingConditions = TargetingConditions.forCombat().range(detectionRadius)
                .selector(targetSelectPredicate.and(avoidPredicate));
    }

    public AvoidEntityTagGoal(PathfinderMob mob, TagKey<EntityType<?>> entityTag, float detectionRadius, double walkSpeed, double sprintSpeed, Predicate<LivingEntity> targetSelectPredicate) {
        this(mob, entityTag, entity -> true, detectionRadius, walkSpeed, sprintSpeed, targetSelectPredicate);
    }

    @Override
    public boolean canUse() {
        Level level = this.mob.level();

        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class,
                this.mob.getBoundingBox().inflate(this.detectionRadius, 3.0, this.detectionRadius),
                entity -> entity.getType().is(this.entityTag));

        this.entityToAvoid = level.getNearestEntity(nearbyEntities, this.targetingConditions,
                this.mob, this.mob.getX(), this.mob.getY(), this.mob.getZ());

        if (this.entityToAvoid == null)
            return false;

        Vec3 escapePosition = DefaultRandomPos.getPosAway(this.mob, 16, 7, this.entityToAvoid.position());
        if (escapePosition == null)
            return false;

        if (this.entityToAvoid.distanceToSqr(escapePosition.x, escapePosition.y, escapePosition.z)
                < this.entityToAvoid.distanceToSqr(this.mob))
            return false;

        this.escapePath = this.navigation.createPath(escapePosition.x, escapePosition.y, escapePosition.z, 0);
        return this.escapePath != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.navigation.isDone();
    }

    @Override
    public void start() {
        this.navigation.moveTo(this.escapePath, this.walkSpeed);
    }

    @Override
    public void stop() {
        this.entityToAvoid = null;
    }

    @Override
    public void tick() {
        if (this.entityToAvoid == null)
            return;
        double distanceSqrToThreat = this.mob.distanceToSqr(this.entityToAvoid);
        double speed = distanceSqrToThreat < 49.0 ? this.sprintSpeed : this.walkSpeed;
        this.mob.getNavigation().setSpeedModifier(speed);
    }
}