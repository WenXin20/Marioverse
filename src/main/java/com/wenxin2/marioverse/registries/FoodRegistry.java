package com.wenxin2.marioverse.registries;

import net.minecraft.world.food.FoodProperties;

public class FoodRegistry {
    public static final FoodProperties MUSHROOM = new FoodProperties.Builder()
            .fast().alwaysEdible().nutrition(4).saturationModifier(0.3F).build();
}
