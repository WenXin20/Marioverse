package com.wenxin2.marioverse.entities.ai.goals;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ContinuousStrollGoal extends WaterAvoidingRandomStrollGoal {
    private Vec3 direction;

    public ContinuousStrollGoal(PathfinderMob mob, double speed) {
        super(mob, speed, 0.0F);
        this.direction = this.getRandomDirection();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        if (this.mob.isInWaterOrBubble()) {
            return LandRandomPos.getPos(this.mob, 15, 7);
        }
        if (this.hasHitBlock()) {
            this.direction = this.getRandomDirection();
        }
        return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : this.getRandomDirection();
    }

    @Override
    public boolean canUse() {
        if (this.mob.hasControllingPassenger()) {
            return false;
        } else {
            Vec3 vec3 = this.getPosition();
            if (vec3 == null) {
                return false;
            } else {
                this.wantedX = vec3.x;
                this.wantedY = vec3.y;
                this.wantedZ = vec3.z;
                this.forceTrigger = false;
                return true;
            }
        }
    }

    private Vec3 getRandomDirection() {
        float x = this.mob.getRandom().nextBoolean() ? 1 : -1; // Either 1 or -1 for X axis
        float z = this.mob.getRandom().nextBoolean() ? 1 : -1; // Either 1 or -1 for Z axis
        return new Vec3(x * 0.5, 0, z * 0.5);
    }

    private boolean hasHitBlock() {
        BlockPos posInFront = this.mob.blockPosition().offset((int) this.direction.x, 0, (int) this.direction.z);
        return !this.mob.level().getBlockState(posInFront).isAir();
    }
}
