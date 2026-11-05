package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.mixin.MobAccessor;
import com.wenxin2.marioverse.utils.WarpReturn;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.phys.Vec3;

public class ReturnThroughWarpGoal extends Goal {
    private static final int RETURN_TIMEOUT = 300;
    private static final double RETURN_SPEED = 1.0;
    private static final double JUMP_ALIGN_DISTANCE = 0.35;
    private final PathfinderMob mob;
    private int returnTicks;

    public ReturnThroughWarpGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return WarpReturn.needsReturn(this.mob);
    }

    @Override
    public void start() {
        this.returnTicks = 0;
        WarpReturn.startReturning(this.mob);
        this.mob.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return WarpReturn.needsReturn(this.mob) && this.returnTicks < RETURN_TIMEOUT;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        BlockPos returnBlock = WarpReturn.getReturnBlock(this.mob);
        this.returnTicks++;

        if (returnBlock == null)
            return;

        Vec3 trigger = WarpReturn.getTriggerSpot(this.mob.level(), returnBlock);
        this.mob.getMoveControl().setWantedPosition(trigger.x, trigger.y, trigger.z, RETURN_SPEED);

        if (WarpReturn.needsJump(this.mob.level(), returnBlock) && this.mob.onGround()
                && this.mob.position().subtract(trigger).horizontalDistanceSqr() < JUMP_ALIGN_DISTANCE * JUMP_ALIGN_DISTANCE)
            this.mob.getJumpControl().jump();
    }

    @Override
    public void stop() {
        if (WarpReturn.needsReturn(this.mob))
            WarpReturn.clear(this.mob);
    }

    public static void addTo(PathfinderMob mob) {
        GoalSelector goalSelector = ((MobAccessor) mob).mv$getGoalSelector();

        if (goalSelector.getAvailableGoals().stream().noneMatch(wrapped -> wrapped.getGoal() instanceof ReturnThroughWarpGoal))
            goalSelector.addGoal(0, new ReturnThroughWarpGoal(mob));
    }
}
