package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.OnBlock;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TreeRegistry {
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_BLUE_TRAMPOLINE_CAP;
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_RED_TRAMPOLINE_CAP;
    public static final DeferredHolder<PlacedFeature, PlacedFeature> HUGE_BLUE_TRAMPOLINE_CAP_GROWER;
    public static final DeferredHolder<PlacedFeature, PlacedFeature> HUGE_RED_TRAMPOLINE_CAP_GROWER;

    static  {
        HUGE_BLUE_TRAMPOLINE_CAP = Marioverse.CONFIGURED_FEATURES.register("huge_blue_trampoline_cap",
                () -> new ConfiguredFeature<>(Feature.HUGE_BROWN_MUSHROOM,
                        new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(BlockRegistry.BLUE_TRAMPOLINE_CAP.get()),
                                BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3)));
        HUGE_RED_TRAMPOLINE_CAP = Marioverse.CONFIGURED_FEATURES.register("huge_red_trampoline_cap",
                () -> new ConfiguredFeature<>(Feature.HUGE_BROWN_MUSHROOM,
                        new HugeMushroomFeatureConfiguration(BlockStateProvider.simple(BlockRegistry.RED_TRAMPOLINE_CAP.get()),
                                BlockStateProvider.simple(Blocks.MUSHROOM_STEM), 3)));
        HUGE_BLUE_TRAMPOLINE_CAP_GROWER = Marioverse.PLACED_FEATURES.register("huge_blue_trampoline_cap",
                () -> new PlacedFeature(HUGE_BLUE_TRAMPOLINE_CAP,
                        List.of(PlacementUtils.filteredByBlockSurvival(BlockRegistry.BLUE_TRAMPOLINE_CAP.get()))));
        HUGE_RED_TRAMPOLINE_CAP_GROWER = Marioverse.PLACED_FEATURES.register("huge_red_trampoline_cap",
                () -> new PlacedFeature(HUGE_RED_TRAMPOLINE_CAP,
                        List.of(PlacementUtils.filteredByBlockSurvival(BlockRegistry.RED_TRAMPOLINE_CAP.get()))));
    }
}
