package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.world.feature.foliage_placers.DomeFoliagePlacer;
import com.wenxin2.marioverse.world.feature.trunk_placers.TaperingTrunkPlacer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TreeRegistry {
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_BLUE_TRAMPOLINE_CAP;
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_RED_TRAMPOLINE_CAP;
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<DomeFoliagePlacer>> DOME_FOLIAGE_PLACER;
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<TaperingTrunkPlacer>> TAPERING_TRUNK_PLACER;

    static  {
        DOME_FOLIAGE_PLACER = Marioverse.FOLIAGE_PLACERS.register("dome_foliage_placer",
                () -> new FoliagePlacerType<>(DomeFoliagePlacer.CODEC));
        TAPERING_TRUNK_PLACER = Marioverse.TRUNK_PLACERS.register("tapering_trunk_placer",
                () -> new TrunkPlacerType<>(TaperingTrunkPlacer.CODEC));

        HUGE_BLUE_TRAMPOLINE_CAP = Marioverse.CONFIGURED_FEATURES.register("huge_blue_trampoline_cap",
                () -> new ConfiguredFeature<>(FeatureRegistry.HUGE_SWITCH_MUSHROOM.get(),
                        new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.get()),
                                BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3)));
        HUGE_RED_TRAMPOLINE_CAP = Marioverse.CONFIGURED_FEATURES.register("huge_red_trampoline_cap",
                () -> new ConfiguredFeature<>(FeatureRegistry.HUGE_SWITCH_MUSHROOM.get(),
                        new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.get()),
                                BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3)));
    }

    public static void init() {
    }
}