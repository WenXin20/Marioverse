package com.wenxin2.marioverse.entities.ai.behaviors;

import com.google.common.collect.ImmutableMap;
import com.wenxin2.marioverse.utils.WarpPipePathing;
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

public class UseWarpPipe extends Behavior<Mob> {
    private static final int MAX_DURATION = 200;
    private static final int WARP_TIMEOUT = 100;
    private static final double START_DISTANCE = 1.5;
    private static final double ARRIVAL_DISTANCE = 3.0;
    private static final double JUMP_ALIGN_DISTANCE = 0.35;
    @Nullable private WarpPipePathing.WarpLink link;
    @Nullable private WalkTarget savedWalkTarget;
    private float speed;
    private int waitTicks;

    public UseWarpPipe() {
        super(ImmutableMap.of(MemoryModuleType.PATH, MemoryStatus.VALUE_PRESENT), MAX_DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Mob mob) {
        if (!WarpPipePathing.canPathThroughWarpPipes(mob))
            return false;

        Path path = mob.getBrain().getMemory(MemoryModuleType.PATH).orElse(null);
        this.link = WarpPipePathing.findWarpStep(level, path, mob);

        return this.link != null && mob.distanceToSqr(Vec3.atBottomCenterOf(this.link.trigger())) <= START_DISTANCE * START_DISTANCE;
    }

    @Override
    protected void start(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();

        this.savedWalkTarget = brain.getMemory(MemoryModuleType.WALK_TARGET).orElse(null);
        this.speed = this.savedWalkTarget != null ? this.savedWalkTarget.getSpeedModifier() : 0.5F;
        this.waitTicks = 0;

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.PATH);
        mob.getNavigation().stop();
    }

    @Override
    protected boolean canStillUse(ServerLevel level, Mob mob, long gameTime) {
        return this.link != null && this.waitTicks < WARP_TIMEOUT && !this.hasArrived(mob);
    }

    @Override
    protected void tick(ServerLevel level, Mob mob, long gameTime) {
        Brain<?> brain = mob.getBrain();
        this.waitTicks++;

        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.eraseMemory(MemoryModuleType.PATH);

        if (this.link != null) {
            Vec3 trigger = Vec3.atBottomCenterOf(this.link.trigger());
            mob.getMoveControl().setWantedPosition(trigger.x, trigger.y, trigger.z, this.speed);

            if (this.link.jump() && mob.onGround() && mob.position().subtract(trigger).horizontalDistanceSqr() < JUMP_ALIGN_DISTANCE * JUMP_ALIGN_DISTANCE)
                mob.getJumpControl().jump();
        }
    }

    @Override
    protected void stop(ServerLevel level, Mob mob, long gameTime) {
        if (this.savedWalkTarget != null && !mob.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET))
            mob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, this.savedWalkTarget);

        this.link = null;
        this.savedWalkTarget = null;
    }

    private boolean hasArrived(Mob mob) {
        return this.link != null
                && mob.distanceToSqr(Vec3.atBottomCenterOf(this.link.destinationStand())) <= ARRIVAL_DISTANCE * ARRIVAL_DISTANCE;
    }
}
