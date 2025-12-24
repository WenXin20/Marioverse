package com.wenxin2.marioverse.integration;

import net.mehvahdjukaar.supplementaries.common.entities.goals.UseCannonBoatGoal;
import net.minecraft.world.entity.Mob;

public class SupplementariesCompat {
    public static void addGoals(Mob mob) {
        mob.goalSelector.addGoal(0, new UseCannonBoatGoal(mob));
    }
}