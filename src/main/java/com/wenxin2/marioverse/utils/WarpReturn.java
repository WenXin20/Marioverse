package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class WarpReturn {
    public static final String WARP_INTENT = "WarpLinkIntent";
    private static final String RETURN_FROM = "WarpReturnFrom";
    private static final String RETURN_TARGET = "WarpReturnTarget";
    private static final String RETURNING = "WarpReturning";
    private static final int RETURNED_WARP_COOLDOWN = 100;

    public static void onWarped(Entity entity, BlockPos warpPos, Vec3 startPos) {
        if (!(entity instanceof PathfinderMob mob) || mob.level().isClientSide || mob.position().equals(startPos))
            return;

        CompoundTag tag = mob.getPersistentData();

        if (tag.getBoolean(RETURNING)) {
            BlockPos target = tag.contains(RETURN_TARGET) ? BlockPos.of(tag.getLong(RETURN_TARGET)) : null;
            WarpReturn.clear(mob);
            mob.setData(DataAttachmentRegistry.WARP_COOLDOWN, RETURNED_WARP_COOLDOWN);

            if (target != null && !WarpReturn.usesBrainPath(mob))
                mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0);
            return;
        }

        if (tag.getBoolean(WARP_INTENT) || tag.contains(RETURN_FROM))
            return;

        BlockPos target = WarpReturn.getPathTarget(mob);
        if (target == null)
            return;

        tag.putLong(RETURN_FROM, warpPos.asLong());
        tag.putLong(RETURN_TARGET, target.asLong());
    }

    public static boolean needsReturn(Mob mob) {
        return mob.getPersistentData().contains(RETURN_FROM);
    }

    public static void startReturning(Mob mob) {
        mob.getPersistentData().putBoolean(RETURNING, true);
    }

    public static void clear(Mob mob) {
        CompoundTag tag = mob.getPersistentData();
        tag.remove(RETURN_FROM);
        tag.remove(RETURN_TARGET);
        tag.remove(RETURNING);
    }

    @Nullable
    public static BlockPos getReturnBlock(Mob mob) {
        return WarpReturn.needsReturn(mob) ? BlockPos.of(mob.getPersistentData().getLong(RETURN_FROM)) : null;
    }

    public static Vec3 getTriggerSpot(Level level, BlockPos warpBlock) {
        BlockState state = level.getBlockState(warpBlock);

        if (state.getBlock() instanceof WarpPipeBlock && !(state.getBlock() instanceof ClearWarpPipeBlock)) {
            Direction facing = state.getValue(WarpPipeBlock.FACING);
            if (facing == Direction.DOWN)
                return Vec3.atBottomCenterOf(warpBlock.below(2));
            return Vec3.atBottomCenterOf(warpBlock.relative(facing));
        }
        return Vec3.atBottomCenterOf(warpBlock);
    }

    public static boolean needsJump(Level level, BlockPos warpBlock) {
        BlockState state = level.getBlockState(warpBlock);
        return state.getBlock() instanceof WarpPipeBlock && !(state.getBlock() instanceof ClearWarpPipeBlock)
                && state.getValue(WarpPipeBlock.FACING) == Direction.DOWN;
    }

    private static boolean usesBrainPath(Mob mob) {
        return mob.getBrain().checkMemory(MemoryModuleType.PATH, MemoryStatus.REGISTERED);
    }

    @Nullable
    private static BlockPos getPathTarget(Mob mob) {
        if (mob.getNavigation().isInProgress())
            return mob.getNavigation().getTargetPos();

        if (WarpReturn.usesBrainPath(mob))
            return mob.getBrain().getMemory(MemoryModuleType.WALK_TARGET)
                    .map(walkTarget -> walkTarget.getTarget().currentBlockPosition()).orElse(null);
        return null;
    }
}
