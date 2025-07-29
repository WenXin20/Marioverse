package com.wenxin2.marioverse.integration;

import net.minecraft.world.entity.Mob;
import net.mehvahdjukaar.supplementaries.common.entities.goals.ManeuverAndShootCannonGoal;

public class SupplementariesCompat {
    public static void addGoals(Mob mob) {
        mob.goalSelector.addGoal(0, new ManeuverAndShootCannonGoal(mob, 1, 5));
    }
}