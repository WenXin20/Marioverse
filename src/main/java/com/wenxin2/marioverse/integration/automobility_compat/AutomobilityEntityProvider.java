package com.wenxin2.marioverse.integration.automobility_compat;

import io.github.foundationgames.automobility.entity.AutomobileEntity;
import net.minecraft.world.entity.Entity;

public class AutomobilityEntityProvider {
    public static float getHSpeed(Entity entity) {
        if (entity instanceof AutomobileEntity automobile)
            return automobile.getHSpeed();
        return 1.0F;
    }
}