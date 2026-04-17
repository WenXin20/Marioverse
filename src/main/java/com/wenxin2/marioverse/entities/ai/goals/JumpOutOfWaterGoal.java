package com.wenxin2.marioverse.entities.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class JumpOutOfWaterGoal extends JumpGoal {
    private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 4, 5, 6, 7};
    private final Mob mob;
    @Nullable private final SoundEvent soundEvent;
    private final int interval;
    private boolean breached;

    public JumpOutOfWaterGoal(Mob mob, int jumpInterval, @Nullable SoundEvent soundEvent) {
        this.mob = mob;
        this.interval = reducedTickDelay(jumpInterval);
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.interval) != 0)
            return false;
        else {
            Direction direction = this.mob.getMotionDirection();
            int i = direction.getStepX();
            int j = direction.getStepZ();
            BlockPos blockpos = this.mob.blockPosition();

            for (int k : STEPS_TO_CHECK) {
                if (!this.waterIsClear(blockpos, i, j, k) || !this.surfaceIsClear(blockpos, i, j, k))
                    return false;
            }
            return true;
        }
    }

    private boolean waterIsClear(BlockPos pos, int dx, int dz, int scale) {
        BlockPos blockpos = pos.offset(dx * scale, 0, dz * scale);
        return this.mob.level().getFluidState(blockpos).is(FluidTags.WATER)
                && !this.mob.level().getBlockState(blockpos).blocksMotion();
    }

    private boolean surfaceIsClear(BlockPos pos, int dx, int dz, int scale) {
        return this.mob.level().getBlockState(pos.offset(dx * scale, 1, dz * scale)).isAir()
                && this.mob.level().getBlockState(pos.offset(dx * scale, 2, dz * scale)).isAir();
    }

    @Override
    public boolean canContinueToUse() {
        double d0 = this.mob.getDeltaMovement().y;
        return (!(d0 * d0 < 0.03F) || this.mob.getXRot() == 0.0F
                || !(Math.abs(this.mob.getXRot()) < 10.0F) || !this.mob.isInWater())
                    && !this.mob.onGround();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public void start() {
        Direction direction = this.mob.getMotionDirection();
        this.mob.setDeltaMovement(this.mob.getDeltaMovement()
                .add((double) direction.getStepX() * 0.6, 0.7, (double) direction.getStepZ() * 0.6));
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setXRot(0.0F);
    }

    @Override
    public void tick() {
        boolean hasBreached = this.breached;
        if (!hasBreached) {
            FluidState fluidstate = this.mob.level().getFluidState(this.mob.blockPosition());
            this.breached = fluidstate.is(FluidTags.WATER);
        }

        if (this.breached && !hasBreached && this.soundEvent != null)
            this.mob.playSound(this.soundEvent, 1.0F, 1.0F);

        Vec3 vec3 = this.mob.getDeltaMovement();
        if (vec3.y * vec3.y < 0.03F && this.mob.getXRot() != 0.0F)
            this.mob.setXRot(Mth.rotLerp(0.2F, this.mob.getXRot(), 0.0F));
        else if (vec3.length() > 1.0E-5F) {
            double d0 = vec3.horizontalDistance();
            double d1 = Math.atan2(-vec3.y, d0) * 180.0F / (float) Math.PI;
            this.mob.setXRot((float) d1);
        }
    }
}