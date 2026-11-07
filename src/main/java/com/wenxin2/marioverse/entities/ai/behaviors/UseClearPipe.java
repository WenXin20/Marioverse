package com.wenxin2.marioverse.entities.ai.behaviors;

import com.google.common.collect.ImmutableMap;
import com.wenxin2.marioverse.utils.ClearPipeNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class UseClearPipe extends Behavior<Mob> {
    private static final int MAX_DURATION = 1200;
    private static final int ENTER_TIMEOUT = 60;
    private static final double START_DISTANCE = 2.0;
    private static final double JUMP_ALIGN_DISTANCE = 0.35;
    @Nullable private ClearPipeNetwork.PipeStep step;
    @Nullable private WalkTarget savedWalkTarget;
    private float speed;
    private int enterTicks;
    private boolean entered;

    public UseClearPipe() {
        super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT), MAX_DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Mob mob) {
        Path path = mob.getBrain().getMemory(MemoryModuleType.PATH).orElse(null);
        this.step = ClearPipeNetwork.findPipeStep(level, path, mob);

        return this.step != null
                && (ClearPipeNetwork.isTravelingInPipe(mob)
                    || ClearPipeNetwork.isNearApproach(mob, this.step, START_DISTANCE))
                && ClearPipeNetwork.fitsEntrance(level, this.step.entrance(), level.getBlockState(this.step.entrance()), mob);
    }

    @Override
    protected void start(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();

        this.savedWalkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        this.speed = this.savedWalkTarget != null ? this.savedWalkTarget.getSpeedModifier() : 0.5F;
        this.enterTicks = 0;
        this.entered = ClearPipeNetwork.isTravelingInPipe(mob);

        if (this.step != null)
            mob.getPersistentData().putLong(ClearPipeNetwork.TRAVEL_EXIT, this.step.link().exit().asLong());

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.PATH);
        mob.getNavigation().stop();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
        if (ClearPipeNetwork.isTravelingInPipe(mob)) {
            this.entered = true;
            return true;
        }
        return !this.entered && this.enterTicks < ENTER_TIMEOUT;
    }

    @Override
    protected void tick(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        this.enterTicks++;

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.PATH);

        if (ClearPipeNetwork.isTravelingInPipe(mob)) {
            mob.getNavigation().stop();
            return;
        }

        if (this.step != null) {
            Vec3 entry = ClearPipeNetwork.getStepTarget(level, mob, this.step);
            mob.getMoveControl().setWantedPosition(entry.x, entry.y, entry.z, this.speed);

            boolean alignedUnderPipe = this.step.jump() && mob.position().subtract(entry).horizontalDistanceSqr() < JUMP_ALIGN_DISTANCE * JUMP_ALIGN_DISTANCE;
            if (mob.onGround() && (alignedUnderPipe || mob.horizontalCollision))
                mob.getJumpControl().jump();
        }
    }

    @Override
    protected void stop(ServerLevel level, Mob mob, long gameTime) {
        mob.getPersistentData().remove(ClearPipeNetwork.TRAVEL_EXIT);

        if (this.savedWalkTarget != null && !mob.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET))
            mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, this.savedWalkTarget);

        this.step = null;
        this.savedWalkTarget = null;
    }
}
