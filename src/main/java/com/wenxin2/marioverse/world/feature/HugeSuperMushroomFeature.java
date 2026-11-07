package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class HugeSuperMushroomFeature extends ShapedHugeMushroomFeature {
    private static final int ROUNDED_RADIUS = 3;
    private static final int EXTRA_ROUNDED_RADIUS = 4;

    private final boolean twoByTwo;

    public HugeSuperMushroomFeature(Codec<ShapedHugeMushroomConfiguration> codec, boolean twoByTwo) {
        super(codec);
        this.twoByTwo = twoByTwo;
    }

    @Override
    public boolean place(FeaturePlaceContext<ShapedHugeMushroomConfiguration> context) {
        RandomSource random = context.random();
        ShapedHugeMushroomConfiguration config = context.config();
        int stemHeight = config.stemHeight().sample(random);
        int radius = scaleWithStem(random, config.radius(), config.stemHeight(), stemHeight);
        int capHeight = Math.min(radius + 1, scaleWithStem(random, config.capHeight(), config.stemHeight(), stemHeight));
        int capMaxY = stemHeight + capHeight - 1;

        return this.placeMushroom(context.level(), random, context.origin(), config, this.twoByTwo ? 2 : 1,
                stemHeight, stemHeight, capMaxY, radius, (distanceX, y, distanceZ) -> {
                    if (y < stemHeight || y > capMaxY)
                        return false;

                    int layerRadius = radius - (y - stemHeight);
                    if (distanceX > layerRadius || distanceZ > layerRadius)
                        return false;
                    if (layerRadius > 0 && distanceX == layerRadius && distanceZ == layerRadius)
                        return false;
                    if (layerRadius >= EXTRA_ROUNDED_RADIUS)
                        return distanceX + distanceZ < layerRadius * 2 - 2;
                    return layerRadius < ROUNDED_RADIUS || distanceX + distanceZ != layerRadius * 2 - 1;
                });
    }
}
