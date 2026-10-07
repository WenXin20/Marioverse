package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.world.feature.HugeMegaMushroomFeature;
import com.wenxin2.marioverse.world.feature.HugeSuperMushroomFeature;
import com.wenxin2.marioverse.world.feature.HugeSwitchMushroomFeature;
import com.wenxin2.marioverse.world.feature.ShapedHugeMushroomConfiguration;
import com.wenxin2.marioverse.world.feature.SwitchRandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FeatureRegistry {
    public static final DeferredHolder<Feature<?>, Feature<ShapedHugeMushroomConfiguration>> HUGE_MEGA_MUSHROOM;
    public static final DeferredHolder<Feature<?>, Feature<ShapedHugeMushroomConfiguration>> HUGE_SUPER_MUSHROOM;
    public static final DeferredHolder<Feature<?>, Feature<HugeMushroomFeatureConfiguration>> HUGE_SWITCH_MUSHROOM;
    public static final DeferredHolder<Feature<?>, Feature<ShapedHugeMushroomConfiguration>> WIDE_MEGA_MUSHROOM;
    public static final DeferredHolder<Feature<?>, Feature<ShapedHugeMushroomConfiguration>> WIDE_SUPER_MUSHROOM;

    public static final DeferredHolder<Feature<?>, Feature<RandomPatchConfiguration>> SWITCH_RANDOM_PATCH;

    static {
        WIDE_MEGA_MUSHROOM = Marioverse.FEATURES.register("wide_mega_mushroom",
                () -> new HugeMegaMushroomFeature(ShapedHugeMushroomConfiguration.CODEC, true));
        HUGE_MEGA_MUSHROOM = Marioverse.FEATURES.register("huge_mega_mushroom",
                () -> new HugeMegaMushroomFeature(ShapedHugeMushroomConfiguration.CODEC, false));
        HUGE_SUPER_MUSHROOM = Marioverse.FEATURES.register("huge_super_mushroom",
                () -> new HugeSuperMushroomFeature(ShapedHugeMushroomConfiguration.CODEC, false));
        WIDE_SUPER_MUSHROOM = Marioverse.FEATURES.register("wide_super_mushroom",
                () -> new HugeSuperMushroomFeature(ShapedHugeMushroomConfiguration.CODEC, true));
        HUGE_SWITCH_MUSHROOM = Marioverse.FEATURES.register("huge_switch_mushroom",
                () -> new HugeSwitchMushroomFeature(HugeMushroomFeatureConfiguration.CODEC));
        SWITCH_RANDOM_PATCH = Marioverse.FEATURES.register("switch_random_patch",
                () -> new SwitchRandomPatchFeature(RandomPatchConfiguration.CODEC));
    }

    public static void init() {
    }
}
