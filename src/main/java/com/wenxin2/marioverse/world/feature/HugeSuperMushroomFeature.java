package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class HugeSuperMushroomFeature extends ShapedHugeMushroomFeature {
    private static final int MIN_STEM_HEIGHT = 2;
    private static final int MAX_STEM_HEIGHT = 8;
    private static final int MIN_RADIUS = 1;
    private static final int ROUNDED_RADIUS = 3;

    private final boolean twoByTwo;

    public HugeSuperMushroomFeature(Codec<HugeMushroomFeatureConfiguration> codec, boolean twoByTwo) {
        super(codec);
        this.twoByTwo = twoByTwo;
    }

    @Override
    public boolean place(FeaturePlaceContext<HugeMushroomFeatureConfiguration> context) {
        RandomSource random = context.random();
        int extraSize = this.twoByTwo ? 1 : 0;
        int minRadius = MIN_RADIUS + extraSize;
        int maxRadius = Math.max(minRadius, context.config().foliageRadius + extraSize);
        int stemHeight = MIN_STEM_HEIGHT + random.nextInt(MAX_STEM_HEIGHT - MIN_STEM_HEIGHT + 1);
        int radius = Math.min(maxRadius, minRadius + (stemHeight - MIN_STEM_HEIGHT + random.nextInt(3)) / 3);

        return this.placeMushroom(context.level(), random, context.origin(), context.config(), this.twoByTwo ? 2 : 1,
                stemHeight, stemHeight, stemHeight + 1, radius, (distanceX, y, distanceZ) -> {
                    if (y < stemHeight || y > stemHeight + 1)
                        return false;

                    int layerRadius = radius - (y - stemHeight);
                    if (distanceX > layerRadius || distanceZ > layerRadius)
                        return false;
                    if (layerRadius > 0 && distanceX == layerRadius && distanceZ == layerRadius)
                        return false;
                    return layerRadius < ROUNDED_RADIUS || distanceX + distanceZ != layerRadius * 2 - 1;
                });
    }
}
