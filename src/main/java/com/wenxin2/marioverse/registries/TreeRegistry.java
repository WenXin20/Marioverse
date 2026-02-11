package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import java.util.List;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TreeRegistry {
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_BLUE_TRAMPOLINE_CAP;
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_RED_TRAMPOLINE_CAP;
    public static final DeferredHolder<PlacedFeature, PlacedFeature> HUGE_BLUE_TRAMPOLINE_CAP_GROWER;
    public static final DeferredHolder<PlacedFeature, PlacedFeature> HUGE_RED_TRAMPOLINE_CAP_GROWER;

    static  {
        HUGE_BLUE_TRAMPOLINE_CAP = Marioverse.CONFIGURED_FEATURES.register("huge_blue_trampoline_cap",
                () -> new ConfiguredFeature<>(FeatureRegistry.HUGE_BLUE_TRAMPOLINE_CAP.get(),
                        new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.get()),
                                BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3)));
        HUGE_RED_TRAMPOLINE_CAP = Marioverse.CONFIGURED_FEATURES.register("huge_red_trampoline_cap",
                () -> new ConfiguredFeature<>(FeatureRegistry.HUGE_RED_TRAMPOLINE_CAP.get(),
                        new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.get()),
                                BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3)));
        HUGE_BLUE_TRAMPOLINE_CAP_GROWER = Marioverse.PLACED_FEATURES.register("huge_blue_trampoline_cap",
                () -> new PlacedFeature(HUGE_BLUE_TRAMPOLINE_CAP, List.of()));
        HUGE_RED_TRAMPOLINE_CAP_GROWER = Marioverse.PLACED_FEATURES.register("huge_red_trampoline_cap",
                () -> new PlacedFeature(HUGE_RED_TRAMPOLINE_CAP, List.of()));
    }
}
