package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.blocks.OnBlock;
import com.wenxin2.marioverse.world.SwitchSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;

public class SwitchRandomPatchFeature extends Feature<RandomPatchConfiguration> {
    public SwitchRandomPatchFeature(Codec<RandomPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
        RandomPatchConfiguration patchConfig = context.config();
        RandomSource random = context.random();
        BlockPos pos = context.origin();
        WorldGenLevel worldGenLevel = context.level();
        int placedAmt = 0;
        BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();
        int j = patchConfig.xzSpread() + 1;
        int k = patchConfig.ySpread() + 1;

        for (int l = 0; l < patchConfig.tries(); l++) {
            posMutable.setWithOffset(pos,
                    random.nextInt(j) - random.nextInt(j),
                    random.nextInt(k) - random.nextInt(k),
                    random.nextInt(j) - random.nextInt(j));
            if (patchConfig.feature().value().place(worldGenLevel, context.chunkGenerator(), random, posMutable)) {
                BlockState placedState = worldGenLevel.getBlockState(posMutable);
                SwitchSavedData data = SwitchSavedData.get(worldGenLevel.getLevel());

                if (placedState.hasProperty(OnBlock.ACTIVE))
                    worldGenLevel.setBlock(posMutable, placedState.setValue(OnBlock.ACTIVE, data.isActive()), 2);

                placedAmt++;
            }
        }

        return placedAmt > 0;
    }
}
