package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.world.feature.foliage_placers.DomeFoliagePlacer;
import com.wenxin2.marioverse.world.feature.tree_decorators.BranchGrowthDecorator;
import com.wenxin2.marioverse.world.feature.tree_decorators.SpookyFaceDecorator;
import com.wenxin2.marioverse.world.feature.trunk_placers.TaperingTrunkPlacer;
import com.wenxin2.marioverse.world.feature.trunk_placers.TwoByTwoTrunkPlacer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class TreeRegistry {
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_BLUE_TRAMPOLINE_CAP;
    public static final DeferredHolder<ConfiguredFeature<?, ?>, ConfiguredFeature<?, ?>> HUGE_RED_TRAMPOLINE_CAP;
    public static final DeferredHolder<FoliagePlacerType<?>, FoliagePlacerType<DomeFoliagePlacer>> DOME_FOLIAGE_PLACER;
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<TaperingTrunkPlacer>> TAPERING_TRUNK_PLACER;
    public static final DeferredHolder<TrunkPlacerType<?>, TrunkPlacerType<TwoByTwoTrunkPlacer>> TWO_BY_TWO_TRUNK_PLACER;
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<SpookyFaceDecorator>> SPOOKY_FACE_DECORATOR;
    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BranchGrowthDecorator>> BRANCH_GROWTH_DECORATOR;

    static  {
        DOME_FOLIAGE_PLACER = Marioverse.FOLIAGE_PLACERS.register("dome_foliage_placer",
                () -> new FoliagePlacerType<>(DomeFoliagePlacer.CODEC));
        TAPERING_TRUNK_PLACER = Marioverse.TRUNK_PLACERS.register("tapering_trunk_placer",
                () -> new TrunkPlacerType<>(TaperingTrunkPlacer.CODEC));
        TWO_BY_TWO_TRUNK_PLACER = Marioverse.TRUNK_PLACERS.register("two_by_two_trunk_placer",
                () -> new TrunkPlacerType<>(TwoByTwoTrunkPlacer.CODEC));
        SPOOKY_FACE_DECORATOR = Marioverse.TREE_DECORATORS.register("spooky_face_decorator",
                () -> new TreeDecoratorType<>(SpookyFaceDecorator.CODEC));
        BRANCH_GROWTH_DECORATOR = Marioverse.TREE_DECORATORS.register("branch_growth_decorator",
                () -> new TreeDecoratorType<>(BranchGrowthDecorator.CODEC));

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