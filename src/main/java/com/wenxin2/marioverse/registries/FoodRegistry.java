package com.wenxin2.marioverse.registries;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class FoodRegistry {
    public static final FoodProperties COOKED_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(6).saturationModifier(0.6F).build();
    public static final FoodProperties COOKED_SPINY_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(8).saturationModifier(0.8F).build();
    public static final FoodProperties DASH_MUSHROOM = new FoodProperties.Builder()
            .fast().alwaysEdible().nutrition(4).saturationModifier(0.3F).build();
    public static final FoodProperties RAW_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(2).saturationModifier(0.1F).build();
    public static final FoodProperties RAW_SPINY_CHEEP_CHEEP = new FoodProperties.Builder()
            .nutrition(1).saturationModifier(0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON,
                    ConfigRegistry.SPINY_CHEEP_CHEEP_FOOD_POISON_DURATION.get(), 0), 0.5F).build();
    public static final FoodProperties RAW_PORCUPUFFER = new FoodProperties.Builder()
            .nutrition(3).saturationModifier(0.1F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON,
                    ConfigRegistry.SPINY_CHEEP_CHEEP_FOOD_POISON_DURATION.get(), 3), 1.0F).build(); //TODO
    public static final FoodProperties COOKED_PORCUPUFFER = new FoodProperties.Builder()
            .nutrition(10).saturationModifier(1.2F)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 40, 0), 0.1F).build();
}
