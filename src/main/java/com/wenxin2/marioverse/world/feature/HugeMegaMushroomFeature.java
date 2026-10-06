package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class HugeMegaMushroomFeature extends ShapedHugeMushroomFeature {
    private final boolean twoByTwo;

    public HugeMegaMushroomFeature(Codec<HugeMushroomFeatureConfiguration> codec, boolean twoByTwo) {
        super(codec);
        this.twoByTwo = twoByTwo;
    }

    @Override
    public boolean place(FeaturePlaceContext<HugeMushroomFeatureConfiguration> context) {
        RandomSource random = context.random();
        int extraSize = this.twoByTwo ? 1 : 0;
        int minStemHeight = this.twoByTwo ? 7 : 4;
        int minRadius = 2 + extraSize;
        int maxRadius = Math.max(minRadius, context.config().foliageRadius + extraSize);

        int stemHeight = minStemHeight + random.nextInt(this.twoByTwo ? 5 : 6);
        int radius = Math.min(maxRadius, minRadius + (stemHeight - minStemHeight + random.nextInt(3)) / 3);
        int bodyHeight = radius + extraSize;
        int rimHeight = stemHeight - bodyHeight - 2 >= 2 && random.nextBoolean() ? 2 : 1;
        int capMinY = stemHeight - bodyHeight - rimHeight;
        int bodyMinY = capMinY + rimHeight;

        return this.placeMushroom(context.level(), random, context.origin(), context.config(), this.twoByTwo ? 2 : 1,
                stemHeight, capMinY, stemHeight, radius + 1, (distanceX, y, distanceZ) -> {
                    int layerRadius = getLayerRadius(radius, capMinY, bodyMinY, stemHeight, y);
                    if (!isInLayer(layerRadius, distanceX, distanceZ))
                        return false;
                    if (y == stemHeight)
                        return true;

                    int radiusAbove = getLayerRadius(radius, capMinY, bodyMinY, stemHeight, y + 1);
                    return !isInLayer(radiusAbove, distanceX, distanceZ)
                            || !isInLayer(layerRadius, distanceX + 1, distanceZ)
                            || !isInLayer(layerRadius, distanceX, distanceZ + 1);
                });
    }

    private static int getLayerRadius(int radius, int capMinY, int bodyMinY, int stemHeight, int y) {
        if (y < capMinY || y > stemHeight)
            return -1;
        if (y == stemHeight)
            return radius - 1;
        return y >= bodyMinY ? radius : radius + 1;
    }

    private static boolean isInLayer(int layerRadius, int distanceX, int distanceZ) {
        if (distanceX > layerRadius || distanceZ > layerRadius)
            return false;
        if (layerRadius == 1)
            return distanceX + distanceZ < 2;
        return layerRadius < 2 || distanceX + distanceZ < layerRadius * 2 - 1;
    }
}
