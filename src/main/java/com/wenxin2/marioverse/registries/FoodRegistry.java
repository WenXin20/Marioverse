package com.wenxin2.marioverse.registries;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class FoodRegistry {
    public static final FoodProperties COOKED_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.8F).build();
    public static final FoodProperties COOKED_SPINY_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(1.0F).build();
    public static final FoodProperties DASH_MUSHROOM = new FoodProperties.Builder()
            .fast().alwaysEdible().nutrition(4).saturationModifier(0.3F).build();
    public static final FoodProperties RAW_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.1F).build();
    public static final FoodProperties RAW_SPINY_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 40, 0), 1.0F).build(); // TODO Config
}
