package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class LightAvoidingChargeAttackGoal extends ChargeAttackGoal {
    private final Mob mob;
    private final int maxLightLevel;

    public LightAvoidingChargeAttackGoal(Mob mob, int maxLightLevel) {
        super(mob);
        this.mob = mob;
        this.maxLightLevel = maxLightLevel;
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        if (target != null) {
            BlockPos targetPos = target.blockPosition();
            Level world = mob.level();
            int light = Math.max(world.getBrightness(LightLayer.BLOCK, targetPos),
                    world.getBrightness(LightLayer.SKY, targetPos));
            if (light >= this.maxLightLevel - 1)
                return false;
        }
        if (this.isInDeadlyLight())
            return false;
        return super.canUse();
    }

    @Override
    public boolean canContinueToUse() {
        if (this.isInDeadlyLight()) {
            this.mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), false);
            return false;
        }
        return super.canContinueToUse();
    }

    @Override
    public void tick() {
        if (this.isInDeadlyLight()) {
            this.moveToDarkerPos();
            this.mob.setData(DataAttachmentRegistry.IS_CHARGING.get(), false);
            return;
        }
        super.tick();
    }

    private boolean isInDeadlyLight() {
        Level world = this.mob.level();
        BlockPos pos = this.mob.blockPosition();
        BlockPos posEye = BlockPos.containing(this.mob.getEyePosition());

        boolean isBlockTooBright = world.getBrightness(LightLayer.BLOCK, pos) >= this.maxLightLevel - 1 && this.mob.isAlive();

        boolean isSkyTooBright = world.getBrightness(LightLayer.SKY, pos) >= this.maxLightLevel - 1
                && world.canSeeSky(posEye) && this.mob.isAlive() && world.isDay()
                && this.mob.getItemBySlot(EquipmentSlot.HEAD).isEmpty();

        return isSkyTooBright || isBlockTooBright;
    }

    private void moveToDarkerPos() {
        Level world = this.mob.level();
        BlockPos posMob = this.mob.blockPosition();
        RandomSource random = this.mob.getRandom();
        BlockPos bestPos = null;
        int bestLight = Integer.MAX_VALUE;

        for (int i = 0; i < 20; i++) {
            BlockPos candidate = posMob.offset(
                    random.nextIntBetweenInclusive(-6, 6),
                    random.nextIntBetweenInclusive(-3, 3),
                    random.nextIntBetweenInclusive(-6, 6)
            );

            if (!world.getBlockState(candidate).isAir())
                continue;
            if (isPathThroughLight(world, posMob, candidate))
                continue;

            int light = Math.max(
                    world.getBrightness(LightLayer.BLOCK, candidate),
                    world.getBrightness(LightLayer.SKY, candidate)
            );

            if (light < bestLight && light < this.maxLightLevel)
            {
                bestLight = light;
                bestPos = candidate;
            }
        }

        if (bestPos != null) {
            this.mob.getMoveControl().setWantedPosition(bestPos.getX() + 0.5, bestPos.getY() + 0.5, bestPos.getZ() + 0.5, 0.25);
            if (this.mob.getTarget() == null)
                this.mob.getLookControl().setLookAt(bestPos.getX() + 0.5, bestPos.getY() + 0.5, bestPos.getZ() + 0.5, 180.0F, 20.0F);
        }
    }

    private boolean isPathThroughLight(Level world, BlockPos start, BlockPos end) {
        int steps = start.distManhattan(end);
        Vec3 vecStart = Vec3.atCenterOf(start);
        Vec3 vecEnd = Vec3.atCenterOf(end);
        Vec3 step = vecEnd.subtract(vecStart).scale(1.0 / steps);

        for (int i = 0; i <= steps; i++) {
            Vec3 vecPos = vecStart.add(step.scale(i));
            BlockPos pos = BlockPos.containing(vecPos);
            int blockLight = world.getBrightness(LightLayer.BLOCK, pos);
            int skyLight = world.getBrightness(LightLayer.SKY, pos);

            if (Math.max(blockLight, skyLight) >= this.maxLightLevel)
                return true;
        }

        return false;
    }
}