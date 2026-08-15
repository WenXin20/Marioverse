package com.wenxin2.marioverse.world.feature.foliage_placers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.TreeRegistry;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import org.jetbrains.annotations.NotNull;

public class DomeFoliagePlacer extends FoliagePlacer {
    public static final MapCodec<DomeFoliagePlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            foliagePlacerParts(instance).and(IntProvider.codec(1, 16).fieldOf("height")
                            .forGetter(placer -> placer.height)).apply(instance, DomeFoliagePlacer::new));

    private final IntProvider height;

    public DomeFoliagePlacer(IntProvider radius, IntProvider offset, IntProvider height) {
        super(radius, offset);
        this.height = height;
    }

    @NotNull
    @Override
    protected FoliagePlacerType<?> type() {
        return TreeRegistry.DOME_FOLIAGE_PLACER.get();
    }

    @Override
    protected void createFoliage(LevelSimulatedReader level, FoliageSetter foliageSetter, RandomSource random, TreeConfiguration config,
                                 int maxFreeTreeHeight, FoliageAttachment attachment, int foliageHeight, int radius, int offset) {
        boolean doubleTrunk = attachment.doubleTrunk();
        int bottomY = offset - foliageHeight;

        for (int y = offset; y >= bottomY; y--) {
            int rowFromBottom = y - bottomY;
            int layerRadius = this.hemisphereRadius(radius, foliageHeight, rowFromBottom);
            this.placeLeavesRow(level, foliageSetter, random, config, attachment.pos(), layerRadius, y, doubleTrunk);
        }
    }

    private int hemisphereRadius(int baseRadius, int foliageHeight, int rowFromBottom) {
        float t = foliageHeight <= 0 ? 0.0F : (float) rowFromBottom / (float) foliageHeight;
        t = Math.min(t, 1.0F);
        int radius = Math.round(baseRadius * Mth.sqrt(Math.max(0.0F, 1.0F - t * t)));

        if (rowFromBottom == 0)
            radius = Math.max(0, radius - 1);
        return radius;
    }

    @Override
    public int foliageHeight(RandomSource random, int height, TreeConfiguration config) {
        return this.height.sample(random);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource random, int localX, int localY, int localZ, int range, boolean large) {
        return Mth.square((float) localX + 0.5F) + Mth.square((float) localZ + 0.5F) > (float) (range * range);
    }
}