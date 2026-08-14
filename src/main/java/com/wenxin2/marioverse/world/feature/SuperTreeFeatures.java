package com.wenxin2.marioverse.world.feature;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.world.feature.foliage_placers.DomeFoliagePlacer;
import com.wenxin2.marioverse.world.feature.trunk_placers.TaperingTrunkPlacer;
import java.util.List;
import java.util.OptionalInt;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.BeehiveDecorator;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;

public class SuperTreeFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOT = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mushroot"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MUSHROOT_BEES_002 = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mushroot_bees_002"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_MUSHROOT =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mega_mushroot"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_MUSHROOT_BEES_002 =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mega_mushroot_bees_002"));

    private static TreeConfiguration.TreeConfigurationBuilder createMushroot() {
        return createMushrootTree().ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createMushrootTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(BlockRegistry.MUSHROOT_LOG.get()),
                new StraightTrunkPlacer(4, 4, 0),
                BlockStateProvider.simple(BlockRegistry.MUSHROOT_LEAVES.get()),
                new FancyFoliagePlacer(UniformInt.of(2, 3), ConstantInt.of(1), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
        );
    }

    private static TreeConfiguration.TreeConfigurationBuilder createMegaMushrootTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(BlockRegistry.MUSHROOT_LOG.get()),
                new TaperingTrunkPlacer(true, 6, 3, 0, UniformInt.of(1, 2),
                        new TaperingTrunkPlacer.BranchConfig(ConstantInt.of(2), UniformInt.of(3, 4),
                                UniformInt.of(3, 5), ConstantInt.of(4))),
                BlockStateProvider.simple(BlockRegistry.MUSHROOT_LEAVES.get()),
                new DomeFoliagePlacer(UniformInt.of(4, 5), ConstantInt.of(4), UniformInt.of(5, 6)),
                new TwoLayersFeatureSize(1, 0, 2, OptionalInt.of(6))
        ).ignoreVines();
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        BeehiveDecorator beehiveDecorator2 = new BeehiveDecorator(0.02F);
        FeatureUtils.register(context, MUSHROOT, Feature.TREE, createMushroot().build());
        FeatureUtils.register(context, MUSHROOT_BEES_002, Feature.TREE, createMushroot()
                .decorators(List.of(beehiveDecorator2)).build());
        FeatureUtils.register(context, MEGA_MUSHROOT, Feature.TREE, createMegaMushrootTree().build());
        FeatureUtils.register(context, MEGA_MUSHROOT_BEES_002, Feature.TREE, createMegaMushrootTree()
                .decorators(List.of(beehiveDecorator2)).build());
    }
}
