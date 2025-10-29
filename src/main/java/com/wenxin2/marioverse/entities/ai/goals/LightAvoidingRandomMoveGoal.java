package com.wenxin2.marioverse.entities.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

public class LightAvoidingRandomMoveGoal extends RandomMoveGoal {
    private final Mob mob;
    private final int maxLightLevel;

    public LightAvoidingRandomMoveGoal(Mob mob, int maxLightLevel) {
        super(mob);
        this.mob = mob;
        this.maxLightLevel = maxLightLevel;
    }

    @Override
    public boolean canUse() {
        return super.canUse();
    }

    @Override
    public void tick() {
        if (this.isInDeadlyLight()) {
            this.moveToDarkerPos();
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

        if (bestPos != null)
            this.mob.getMoveControl().setWantedPosition(bestPos.getX() + 0.5, bestPos.getY() + 0.5, bestPos.getZ() + 0.5, 0.25);
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