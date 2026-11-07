package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class HugeMegaMushroomFeature extends ShapedHugeMushroomFeature {
    private static final int MIN_CAP_HEIGHT = 3;
    private static final int TALL_RIM_CAP_HEIGHT = 5;
    private static final int EXTRA_ROUNDED_RADIUS = 4;

    private final boolean twoByTwo;

    public HugeMegaMushroomFeature(Codec<ShapedHugeMushroomConfiguration> codec, boolean twoByTwo) {
        super(codec);
        this.twoByTwo = twoByTwo;
    }

    @Override
    public boolean place(FeaturePlaceContext<ShapedHugeMushroomConfiguration> context) {
        RandomSource random = context.random();
        ShapedHugeMushroomConfiguration config = context.config();
        int stemHeight = Math.max(MIN_CAP_HEIGHT, config.stemHeight().sample(random));
        int radius = scaleWithStem(random, config.radius(), config.stemHeight(), stemHeight);
        int capHeight = Mth.clamp(scaleWithStem(random, config.capHeight(), config.stemHeight(), stemHeight),
                MIN_CAP_HEIGHT, stemHeight);
        int rimHeight = capHeight >= TALL_RIM_CAP_HEIGHT && random.nextBoolean() ? 2 : 1;
        int capMinY = stemHeight + 1 - capHeight;
        int bodyMinY = capMinY + rimHeight;

        return this.placeMushroom(context.level(), random, context.origin(), config, this.twoByTwo ? 2 : 1,
                stemHeight, capMinY, stemHeight, radius + 1, (distanceX, y, distanceZ) -> {
                    int layerRadius = getLayerRadius(radius, capMinY, bodyMinY, stemHeight, y);
                    if (!isInLayer(layerRadius, distanceX, distanceZ))
                        return false;
                    if (y == stemHeight)
                        return true;

                    int radiusAbove = getLayerRadius(radius, capMinY, bodyMinY, stemHeight, y + 1);
                    return !isInLayer(radiusAbove, distanceX, distanceZ)
                            || isLayerEdge(radiusAbove, distanceX, distanceZ)
                            || isLayerEdge(layerRadius, distanceX, distanceZ);
                });
    }

    private static int getLayerRadius(int radius, int capMinY, int bodyMinY, int stemHeight, int y) {
        if (y < capMinY || y > stemHeight)
            return -1;
        if (y == stemHeight)
            return radius - 1;
        return y >= bodyMinY ? radius : radius + 1;
    }

    private static boolean isLayerEdge(int layerRadius, int distanceX, int distanceZ) {
        return !isInLayer(layerRadius, distanceX + 1, distanceZ) || !isInLayer(layerRadius, distanceX, distanceZ + 1);
    }

    private static boolean isInLayer(int layerRadius, int distanceX, int distanceZ) {
        if (distanceX > layerRadius || distanceZ > layerRadius)
            return false;
        if (layerRadius == 1)
            return distanceX + distanceZ < 2;
        if (layerRadius >= EXTRA_ROUNDED_RADIUS)
            return distanceX + distanceZ < layerRadius * 2 - 2;
        return layerRadius < 2 || distanceX + distanceZ < layerRadius * 2 - 1;
    }
}
