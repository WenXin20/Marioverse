package com.wenxin2.marioverse.entities.ai;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MushroomMoveGoal extends WaterAvoidingRandomStrollGoal {
    private Vec3 direction;

    public MushroomMoveGoal(PathfinderMob mob, double speed) {
        super(mob, speed, 0.0F);
        this.direction = this.getRandomDirection();
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        return this.mob.getRandom().nextFloat() >= this.probability ? LandRandomPos.getPos(this.mob, 10, 7) : this.direction;
    }

    private Vec3 getRandomDirection() {
        float x = this.mob.getRandom().nextBoolean() ? 1 : -1; // Either 1 or -1 for X axis
        float z = this.mob.getRandom().nextBoolean() ? 1 : -1; // Either 1 or -1 for Z axis
        return new Vec3(x * 0.5, 0, z * 0.5);
    }
}
