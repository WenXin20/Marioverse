package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.mixin.MobAccessor;
import com.wenxin2.marioverse.mixin.PathNavigationAccessor;
import com.wenxin2.marioverse.utils.ClearPipeNetwork;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class UseClearPipeGoal extends Goal {
    private static final int ENTER_TIMEOUT = 60;
    private static final double START_DISTANCE = 2.0;
    private static final double JUMP_ALIGN_DISTANCE = 0.35;
    private final PathfinderMob mob;
    @Nullable private ClearPipeNetwork.PipeStep step;
    @Nullable private BlockPos destination;
    private double speed;
    private int enterTicks;
    private boolean entered;

    public UseClearPipeGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        Path path = this.mob.getNavigation().getPath();
        this.step = ClearPipeNetwork.findPipeStep(this.mob.level(), path, this.mob);

        return this.step != null
                && (ClearPipeNetwork.isTravelingInPipe(this.mob)
                    || ClearPipeNetwork.isNearApproach(this.mob, this.step, START_DISTANCE))
                && ClearPipeNetwork.fitsEntrance(this.mob.level(), this.step.entrance(), this.mob.level().getBlockState(this.step.entrance()), this.mob);
    }

    @Override
    public void start() {
        PathNavigation navigation = this.mob.getNavigation();
        Path path = navigation.getPath();

        this.destination = path != null ? path.getTarget() : null;
        this.speed = ((PathNavigationAccessor) navigation).mv$getSpeedModifier();
        this.enterTicks = 0;
        this.entered = ClearPipeNetwork.isTravelingInPipe(this.mob);

        if (this.step != null)
            this.mob.getPersistentData().putLong(ClearPipeNetwork.TRAVEL_EXIT, this.step.link().exit().asLong());
        navigation.stop();
    }

    @Override
    public boolean canContinueToUse() {
        if (ClearPipeNetwork.isTravelingInPipe(this.mob)) {
            this.entered = true;
            return true;
        }
        return !this.entered && this.enterTicks < ENTER_TIMEOUT;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.enterTicks++;

        if (ClearPipeNetwork.isTravelingInPipe(this.mob)) {
            this.mob.getNavigation().stop();
            return;
        }

        if (this.step != null) {
            Vec3 entry = ClearPipeNetwork.getStepTarget(this.mob.level(), this.mob, this.step);
            this.mob.getMoveControl().setWantedPosition(entry.x, entry.y, entry.z, this.speed);

            boolean alignedUnderPipe = this.step.jump() && this.mob.position().subtract(entry).horizontalDistanceSqr() < JUMP_ALIGN_DISTANCE * JUMP_ALIGN_DISTANCE;
            if (this.mob.onGround() && (alignedUnderPipe || this.mob.horizontalCollision))
                this.mob.getJumpControl().jump();
        }
    }

    @Override
    public void stop() {
        this.mob.getPersistentData().remove(ClearPipeNetwork.TRAVEL_EXIT);

        if (this.entered && this.destination != null)
            this.mob.getNavigation().moveTo(this.destination.getX() + 0.5, this.destination.getY(), this.destination.getZ() + 0.5, this.speed);

        this.step = null;
        this.destination = null;
    }

    public static void addTo(PathfinderMob mob) {
        GoalSelector goalSelector = ((MobAccessor) mob).mv$getGoalSelector();

        if (goalSelector.getAvailableGoals().stream().noneMatch(wrapped -> wrapped.getGoal() instanceof UseClearPipeGoal))
            goalSelector.addGoal(0, new UseClearPipeGoal(mob));
    }
}
