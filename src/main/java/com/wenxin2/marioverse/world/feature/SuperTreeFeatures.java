package com.wenxin2.marioverse.world.feature;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.world.feature.foliage_placers.DomeFoliagePlacer;
import com.wenxin2.marioverse.world.feature.tree_decorators.BranchGrowthDecorator;
import com.wenxin2.marioverse.world.feature.tree_decorators.SpookyFaceDecorator;
import com.wenxin2.marioverse.world.feature.trunk_placers.TaperingTrunkPlacer;
import com.wenxin2.marioverse.world.feature.trunk_placers.TwoByTwoTrunkPlacer;
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
import net.minecraft.world.level.levelgen.feature.trunkplacers.DarkOakTrunkPlacer;
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
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPOOKROOT = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "spookroot"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPOOKROOT_BEES_002 = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "spookroot_bees_002"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_SPOOKROOT =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mega_spookroot"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_SPOOKROOT_BEES_002 =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mega_spookroot_bees_002"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPOOKY_SPOOKROOT = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "spooky_spookroot"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> SPOOKY_SPOOKROOT_BEES_002 = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "spooky_spookroot_bees_002"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_SPOOKY_SPOOKROOT =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mega_spooky_spookroot"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> MEGA_SPOOKY_SPOOKROOT_BEES_002 =
            ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mega_spooky_spookroot_bees_002"));

    private static TreeConfiguration.TreeConfigurationBuilder createMushroot() {
        return createMushrootTree().ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createSpookroot() {
        return createSpookrootTree().ignoreVines();
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
                new TaperingTrunkPlacer(true, 7, 5, 0,
                        UniformInt.of(1, 2), ConstantInt.of(2),
                        UniformInt.of(3, 4), UniformInt.of(3, 6),
                        UniformInt.of(2, 3), true, UniformInt.of(4, 5)),
                BlockStateProvider.simple(BlockRegistry.MUSHROOT_LEAVES.get()),
                new DomeFoliagePlacer(UniformInt.of(4, 5), ConstantInt.of(4), UniformInt.of(5, 8)),
                new TwoLayersFeatureSize(1, 0, 2, OptionalInt.of(6))).ignoreVines();
    }

    private static TreeConfiguration.TreeConfigurationBuilder createSpookrootTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(BlockRegistry.SPOOKROOT_LOG.get()),
                new StraightTrunkPlacer(4, 4, 0),
                BlockStateProvider.simple(BlockRegistry.SPOOKROOT_LEAVES.get()),
                new FancyFoliagePlacer(UniformInt.of(2, 3), ConstantInt.of(1), 4),
                new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
        );
    }

    private static TreeConfiguration.TreeConfigurationBuilder createMegaSpookrootTree() {
        return new TreeConfiguration.TreeConfigurationBuilder(
                BlockStateProvider.simple(BlockRegistry.SPOOKROOT_LOG.get()),
                new TwoByTwoTrunkPlacer(5, 3, 2),
                BlockStateProvider.simple(BlockRegistry.SPOOKROOT_LEAVES.get()),
                new DomeFoliagePlacer(UniformInt.of(6, 10), ConstantInt.of(6), UniformInt.of(7, 10)),
                new TwoLayersFeatureSize(1, 0, 2, OptionalInt.of(6))).ignoreVines();
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        BeehiveDecorator beehiveDecorator2 = new BeehiveDecorator(0.02F);
        SpookyFaceDecorator spookyFaceDecorator = new SpookyFaceDecorator(0.05F, BlockRegistry.DARK_SPOOKROOT_LEAVES.get());
        SpookyFaceDecorator guaranteedSpookyFaceDecorator = new SpookyFaceDecorator(1.0F, BlockRegistry.DARK_SPOOKROOT_LEAVES.get());
        BranchGrowthDecorator spookrootBranches = new BranchGrowthDecorator(ConstantInt.of(4),
                ConstantInt.of(4), BlockRegistry.SPOOKROOT_LOG.get());

        FeatureUtils.register(context, MUSHROOT, Feature.TREE, createMushroot().build());
        FeatureUtils.register(context, MUSHROOT_BEES_002, Feature.TREE, createMushroot()
                .decorators(List.of(beehiveDecorator2)).build());
        FeatureUtils.register(context, MEGA_MUSHROOT, Feature.TREE, createMegaMushrootTree().build());
        FeatureUtils.register(context, MEGA_MUSHROOT_BEES_002, Feature.TREE, createMegaMushrootTree()
                .decorators(List.of(beehiveDecorator2)).build());
        FeatureUtils.register(context, SPOOKROOT, Feature.TREE, createSpookroot().build());
        FeatureUtils.register(context, SPOOKROOT_BEES_002, Feature.TREE, createSpookroot()
                .decorators(List.of(beehiveDecorator2)).build());
        FeatureUtils.register(context, MEGA_SPOOKROOT, Feature.TREE, createMegaSpookrootTree()
                .decorators(List.of(spookyFaceDecorator, spookrootBranches)).build());
        FeatureUtils.register(context, MEGA_SPOOKROOT_BEES_002, Feature.TREE, createMegaSpookrootTree()
                .decorators(List.of(beehiveDecorator2, spookyFaceDecorator, spookrootBranches)).build());
        FeatureUtils.register(context, SPOOKY_SPOOKROOT, Feature.TREE, createSpookroot()
                .decorators(List.of(guaranteedSpookyFaceDecorator)).build());
        FeatureUtils.register(context, SPOOKY_SPOOKROOT_BEES_002, Feature.TREE, createSpookroot()
                .decorators(List.of(beehiveDecorator2, guaranteedSpookyFaceDecorator)).build());
        FeatureUtils.register(context, MEGA_SPOOKY_SPOOKROOT, Feature.TREE, createMegaSpookrootTree()
                .decorators(List.of(guaranteedSpookyFaceDecorator, spookrootBranches)).build());
        FeatureUtils.register(context, MEGA_SPOOKY_SPOOKROOT_BEES_002, Feature.TREE, createMegaSpookrootTree()
                .decorators(List.of(beehiveDecorator2, guaranteedSpookyFaceDecorator, spookrootBranches)).build());
    }
}
