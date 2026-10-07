package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record ShapedHugeMushroomConfiguration(BlockStateProvider capProvider, BlockStateProvider stemProvider,
                                              IntProvider stemHeight, IntProvider radius,
                                              IntProvider capHeight) implements FeatureConfiguration {
    public static final Codec<ShapedHugeMushroomConfiguration> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter(ShapedHugeMushroomConfiguration::capProvider),
            BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter(ShapedHugeMushroomConfiguration::stemProvider),
            IntProvider.codec(1, 32).fieldOf("stem_height").forGetter(ShapedHugeMushroomConfiguration::stemHeight),
            IntProvider.codec(1, 16).fieldOf("radius").forGetter(ShapedHugeMushroomConfiguration::radius),
            IntProvider.codec(1, 16).fieldOf("cap_height").forGetter(ShapedHugeMushroomConfiguration::capHeight)
    ).apply(instance, ShapedHugeMushroomConfiguration::new));
}
