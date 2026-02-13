package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeBrownMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FeatureRegistry {

    public static final DeferredHolder<Feature<?>, Feature<HugeMushroomFeatureConfiguration>> HUGE_BLUE_TRAMPOLINE_CAP;
    public static final DeferredHolder<Feature<?>, Feature<HugeMushroomFeatureConfiguration>> HUGE_RED_TRAMPOLINE_CAP;

    static {
        HUGE_BLUE_TRAMPOLINE_CAP = Marioverse.FEATURES.register("huge_blue_trampoline_cap",
                () -> new HugeBrownMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
        HUGE_RED_TRAMPOLINE_CAP = Marioverse.FEATURES.register("huge_red_trampoline_cap",
                () -> new HugeBrownMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
    }

    public static void init() {
    }
}
