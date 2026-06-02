package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.Comparator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.JumpGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class JumpOutOfWaterGoal extends JumpGoal {
    @Nullable private final SoundEvent soundEvent;
    @Nullable private LivingEntity lureTarget;
    private static final int[] STEPS_TO_CHECK = new int[]{0, 1, 4, 5, 6, 7};
    private final Mob mob;
    private final int baseInterval;
    private boolean breached;
    private final TagKey<EntityType<?>> lureEntityTag;
    private final boolean eatTarget;
    private final double lureRadius;

    public JumpOutOfWaterGoal(Mob mob, TagKey<EntityType<?>> lureEntityTag, double lureRadius, int jumpInterval, boolean eatTarget, @Nullable SoundEvent soundEvent) {
        this.baseInterval = reducedTickDelay(jumpInterval);
        this.eatTarget = eatTarget;
        this.lureEntityTag = lureEntityTag;
        this.lureRadius = lureRadius;
        this.mob = mob;
        this.soundEvent = soundEvent;
    }

    @Override
    public boolean canUse() {
        this.lureTarget = this.findLureTarget();
        int interval = (lureTarget != null) ? Math.max(2, baseInterval / 3) : baseInterval;

        if (this.mob.getRandom().nextInt(interval) != 0)
            return false;

        Direction direction = this.mob.getMotionDirection();
        int stepX = direction.getStepX();
        int stepZ = direction.getStepZ();
        BlockPos pos = this.mob.blockPosition();

        for (int stepToCheck : STEPS_TO_CHECK) {
            if (!this.waterIsClear(pos, stepX, stepZ, stepToCheck) || !this.surfaceIsClear(pos, stepX, stepZ, stepToCheck))
                return false;
        }
        return true;
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
        Direction direction = this.getBiasedDirection();
        double upwardBoost = (this.lureTarget != null) ? 1.0 : 0.7;
        double forwardBoost = (this.lureTarget != null) ? 0.9 : 0.6;
        Vec3 jumpVec = new Vec3(direction.getStepX() * forwardBoost, upwardBoost,
                direction.getStepZ() * forwardBoost);
        float yaw = (float) (Mth.atan2(jumpVec.z, jumpVec.x) * (180F / Math.PI)) - 90F;

        this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(jumpVec));
        this.mob.setYRot(yaw);
        this.mob.setYHeadRot(yaw);
        this.mob.yBodyRot = yaw;
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.mob.setXRot(0.0F);
    }

    @Override
    public void tick() {
        boolean hasBreached = this.breached;
        float pitch = 0.9F + this.mob.level().random.nextFloat() * 0.2F;

        if (!hasBreached) {
            FluidState fluidstate = this.mob.level().getFluidState(this.mob.blockPosition());
            this.breached = fluidstate.is(FluidTags.WATER);
        }

        if (this.breached && !hasBreached && this.soundEvent != null)
            this.mob.playSound(this.soundEvent, 1.0F, pitch);

        if (this.breached)
            this.mob.setData(DataAttachmentRegistry.HAS_JUMPED, true);

        Vec3 vec3 = this.mob.getDeltaMovement();
        if (vec3.y * vec3.y < 0.03F && this.mob.getXRot() != 0.0F)
            this.mob.setXRot(Mth.rotLerp(0.2F, this.mob.getXRot(), 0.0F));
        else if (vec3.length() > 1.0E-5F) {
            double d0 = vec3.horizontalDistance();
            double d1 = Math.atan2(-vec3.y, d0) * 180.0F / (float) Math.PI;
            this.mob.setXRot((float) d1);
        }

        if (this.mob.getTarget() instanceof LivingEntity) {
            boolean canSwallow = this.mob.getTarget().getBbWidth() <= 2.0F &&
                    this.mob.getTarget().getBbHeight() <= 2.0F;
            if (this.eatTarget && canSwallow)
                this.mob.setData(DataAttachmentRegistry.IS_MOUTH_OPEN, true);
        }
    }

    private Direction getBiasedDirection() {
        if (this.lureTarget == null)
            return this.mob.getMotionDirection();

        Vec3 toTarget = this.lureTarget.position().subtract(this.mob.position());
        return Direction.getNearest(toTarget.x, 0, toTarget.z);
    }

    @Nullable
    private LivingEntity findLureTarget() {
        List<LivingEntity> list = this.mob.level().getEntitiesOfClass(LivingEntity.class,
                this.mob.getBoundingBox().inflate(this.lureRadius), this::isValidLureTarget);

        return list.stream().min(Comparator.comparingDouble(entity -> entity.distanceToSqr(this.mob)))
                .orElse(null);
    }

    private boolean isValidLureTarget(LivingEntity entity) {
        return entity != this.mob && entity.getType().is(this.lureEntityTag) && !entity.isSpectator()
                && (!(entity instanceof Player player) || !player.isCreative())
                && !entity.isInWaterOrBubble();
    }
}