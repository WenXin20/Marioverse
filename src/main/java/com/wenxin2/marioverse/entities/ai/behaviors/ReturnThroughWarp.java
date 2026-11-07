package com.wenxin2.marioverse.entities.ai.behaviors;

import com.google.common.collect.ImmutableMap;
import com.wenxin2.marioverse.utils.WarpReturn;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.phys.Vec3;

public class ReturnThroughWarp extends Behavior<Mob> {
    private static final int MAX_DURATION = 400;
    private static final int RETURN_TIMEOUT = 300;
    private static final float RETURN_SPEED = 0.5F;
    private static final double JUMP_ALIGN_DISTANCE = 0.35;
    private int returnTicks;

    public ReturnThroughWarp() {
        super(ImmutableMap.of(), MAX_DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Mob mob) {
        return WarpReturn.needsReturn(mob);
    }

    @Override
    protected void start(ServerLevel level, Mob mob, long gameTime) {
        this.returnTicks = 0;
        WarpReturn.startReturning(mob);
        mob.getNavigation().stop();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
        return WarpReturn.needsReturn(mob) && this.returnTicks < RETURN_TIMEOUT;
    }

    @Override
    protected void tick(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        BlockPos returnBlock = WarpReturn.getReturnBlock(mob);
        this.returnTicks++;

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.PATH);

        if (returnBlock == null)
            return;

        Vec3 trigger = WarpReturn.getTriggerSpot(level, returnBlock);
        mob.getMoveControl().setWantedPosition(trigger.x, trigger.y, trigger.z, RETURN_SPEED);

        if (WarpReturn.needsJump(level, returnBlock) && mob.onGround()
                && mob.position().subtract(trigger).horizontalDistanceSqr() < JUMP_ALIGN_DISTANCE * JUMP_ALIGN_DISTANCE)
            mob.getJumpControl().jump();
    }

    @Override
    protected void stop(ServerLevel level, Mob mob, long gameTime) {
        WarpReturn.clear(mob);
    }
}
