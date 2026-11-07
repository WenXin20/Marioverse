package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.mixin.MobAccessor;
import com.wenxin2.marioverse.mixin.PathNavigationAccessor;
import com.wenxin2.marioverse.utils.WarpLinkPathing;
import com.wenxin2.marioverse.utils.WarpReturn;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class UseWarpLinkGoal extends Goal {
    private static final int WARP_TIMEOUT = 100;
    private static final double START_DISTANCE = 1.5;
    private static final double ARRIVAL_DISTANCE = 3.0;
    private static final double JUMP_ALIGN_DISTANCE = 0.35;
    private final PathfinderMob mob;
    @Nullable private WarpLinkPathing.WarpLink link;
    @Nullable private BlockPos destination;
    private double speed;
    private int waitTicks;

    public UseWarpLinkGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (!WarpLinkPathing.canPathThroughWarps(this.mob))
            return false;

        this.link = WarpLinkPathing.findWarpStep(this.mob.level(), this.mob.getNavigation().getPath(), this.mob);
        return this.link != null && this.mob.distanceToSqr(Vec3.atBottomCenterOf(this.link.trigger())) <= START_DISTANCE * START_DISTANCE;
    }

    @Override
    public void start() {
        PathNavigation navigation = this.mob.getNavigation();
        Path path = navigation.getPath();

        this.destination = path != null ? path.getTarget() : null;
        this.speed = ((PathNavigationAccessor) navigation).mv$getSpeedModifier();
        this.waitTicks = 0;
        this.mob.getPersistentData().putBoolean(WarpReturn.WARP_INTENT, true);
        navigation.stop();
    }

    @Override
    public boolean canContinueToUse() {
        return this.link != null && this.waitTicks < WARP_TIMEOUT && !this.hasArrived();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        this.waitTicks++;

        if (this.link == null)
            return;

        WarpLinkPathing.openDoor(this.mob.level(), this.mob, this.link);

        Vec3 trigger = Vec3.atBottomCenterOf(this.link.trigger());
        this.mob.getMoveControl().setWantedPosition(trigger.x, trigger.y, trigger.z, this.speed);

        if (this.link.jump() && this.mob.onGround() && this.mob.position().subtract(trigger).horizontalDistanceSqr() < JUMP_ALIGN_DISTANCE * JUMP_ALIGN_DISTANCE)
            this.mob.getJumpControl().jump();
    }

    @Override
    public void stop() {
        this.mob.getPersistentData().remove(WarpReturn.WARP_INTENT);

        if (this.hasArrived() && this.destination != null)
            this.mob.getNavigation().moveTo(this.destination.getX() + 0.5, this.destination.getY(), this.destination.getZ() + 0.5, this.speed);

        this.link = null;
        this.destination = null;
    }

    public static void addTo(PathfinderMob mob) {
        GoalSelector goalSelector = ((MobAccessor) mob).mv$getGoalSelector();

        if (goalSelector.getAvailableGoals().stream().noneMatch(wrapped -> wrapped.getGoal() instanceof UseWarpLinkGoal))
            goalSelector.addGoal(0, new UseWarpLinkGoal(mob));
    }

    private boolean hasArrived() {
        return this.link != null
                && this.mob.distanceToSqr(Vec3.atBottomCenterOf(this.link.destinationStand())) <= ARRIVAL_DISTANCE * ARRIVAL_DISTANCE;
    }
}
