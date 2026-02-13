package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.world.feature.HugeSwitchMushroomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FeatureRegistry {
    public static final DeferredHolder<Feature<?>, Feature<HugeMushroomFeatureConfiguration>> HUGE_SWITCH_MUSHROOM;

    static {
        HUGE_SWITCH_MUSHROOM = Marioverse.FEATURES.register("huge_switch_mushroom",
                () -> new HugeSwitchMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
    }

    public static void init() {
    }
}