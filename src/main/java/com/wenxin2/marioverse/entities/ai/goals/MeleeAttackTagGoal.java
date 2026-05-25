package com.wenxin2.marioverse.entities.ai.goals;

import java.util.EnumSet;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;

public class MeleeAttackTagGoal extends Goal {
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
    private Path path;
    private boolean canPenalize = false;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private final TagKey<EntityType<?>> entityTag;
    private final boolean doHurtTarget;
    private final boolean followingTargetEvenIfNotSeen;
    private final double speedModifier;
    private final int attackInterval = 20;
    private int failedPathFindingPenalty = 0;
    private int ticksUntilNextAttack;
    private int ticksUntilNextPathRecalculation;
    private long lastCanUseCheck;
    protected final PathfinderMob mob;

    public MeleeAttackTagGoal(PathfinderMob mob, TagKey<EntityType<?>> entityTag, double speedModifier, boolean doHurtTarget, boolean followingTargetEvenIfNotSeen) {
        this.mob = mob;
        this.entityTag = entityTag;
        this.followingTargetEvenIfNotSeen = followingTargetEvenIfNotSeen;
        this.speedModifier = speedModifier;
        this.doHurtTarget = doHurtTarget;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        long gameTime = this.mob.level().getGameTime();
        if (gameTime - this.lastCanUseCheck < 20L)
            return false;
        else {
            this.lastCanUseCheck = gameTime;
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null)
                return false;
            else if (!livingEntity.isAlive())
                return false;
            else if (!livingEntity.getType().is(this.entityTag))
                return false;
            else {
                if (canPenalize) {
                    if (--this.ticksUntilNextPathRecalculation <= 0) {
                        this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                        this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                        return this.path != null;
                    } else return true;
                }
                this.path = this.mob.getNavigation().createPath(livingEntity, 0);
                return this.path != null ? true : this.mob.isWithinMeleeAttackRange(livingEntity);
            }
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null)
            return false;
        else if (!livingEntity.isAlive())
            return false;
        else if (!livingEntity.getType().is(this.entityTag))
            return false;
        else if (!this.followingTargetEvenIfNotSeen)
            return !this.mob.getNavigation().isDone();
        else
            return !this.mob.isWithinRestriction(livingEntity.blockPosition()) ? false
                    : !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player)livingEntity).isCreative();
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.path, this.speedModifier);
        this.mob.setAggressive(true);
        this.ticksUntilNextPathRecalculation = 0;
        this.ticksUntilNextAttack = 0;
    }

    @Override
    public void stop() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingEntity))
            this.mob.setTarget(null);

        this.mob.setAggressive(false);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity livingEntity = this.mob.getTarget();

        if (livingEntity != null) {
            this.mob.getLookControl().setLookAt(livingEntity, 30.0F, 30.0F);
            this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
            if ((this.followingTargetEvenIfNotSeen || this.mob.getSensing().hasLineOfSight(livingEntity))
                    && this.ticksUntilNextPathRecalculation <= 0
                    && (this.pathedTargetX == 0.0 && this.pathedTargetY == 0.0 && this.pathedTargetZ == 0.0
                            || livingEntity.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0
                            || this.mob.getRandom().nextFloat() < 0.05F)) {
                this.pathedTargetX = livingEntity.getX();
                this.pathedTargetY = livingEntity.getY();
                this.pathedTargetZ = livingEntity.getZ();
                this.ticksUntilNextPathRecalculation = 4 + this.mob.getRandom().nextInt(7);
                double d0 = this.mob.distanceToSqr(livingEntity);
                if (this.canPenalize) {
                    this.ticksUntilNextPathRecalculation += failedPathFindingPenalty;
                    if (this.mob.getNavigation().getPath() != null) {
                        net.minecraft.world.level.pathfinder.Node finalPathPoint = this.mob.getNavigation().getPath().getEndNode();
                        if (finalPathPoint != null && livingEntity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                            failedPathFindingPenalty = 0;
                        else failedPathFindingPenalty += 10;
                    } else failedPathFindingPenalty += 10;
                }
                if (d0 > 1024.0)
                    this.ticksUntilNextPathRecalculation += 10;
                else if (d0 > 256.0)
                    this.ticksUntilNextPathRecalculation += 5;

                if (!this.mob.getNavigation().moveTo(livingEntity, this.speedModifier))
                    this.ticksUntilNextPathRecalculation += 15;
                this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
            }
            this.ticksUntilNextAttack = Math.max(this.ticksUntilNextAttack - 1, 0);
            this.checkAndPerformAttack(livingEntity);
        }
    }

    protected void checkAndPerformAttack(LivingEntity entity) {
        if (this.canPerformAttack(entity)) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
            this.mob.doHurtTarget(entity);
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(20);
    }

    protected boolean isTimeToAttack() {
        return this.ticksUntilNextAttack <= 0;
    }

    protected boolean canPerformAttack(LivingEntity entity) {
        return this.isTimeToAttack() && this.doHurtTarget
                && entity.getType().is(this.entityTag)
                && this.mob.isWithinMeleeAttackRange(entity)
                && this.mob.getSensing().hasLineOfSight(entity);
    }

    protected int getTicksUntilNextAttack() {
        return this.ticksUntilNextAttack;
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(20);
    }
}