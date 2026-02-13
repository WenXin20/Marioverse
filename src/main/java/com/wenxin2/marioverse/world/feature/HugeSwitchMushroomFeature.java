package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.blocks.OnBlock;
import com.wenxin2.marioverse.world.SwitchSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public class HugeSwitchMushroomFeature extends AbstractHugeMushroomFeature {
    public HugeSwitchMushroomFeature(Codec<HugeMushroomFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    protected void makeCap(LevelAccessor levelAccessor, RandomSource random, BlockPos pos, int treeHeight,
                           BlockPos.MutableBlockPos posMutable, HugeMushroomFeatureConfiguration config) {
        int foliageRadius = config.foliageRadius;

        for (int j = -foliageRadius; j <= foliageRadius; j++) {
            for (int k = -foliageRadius; k <= foliageRadius; k++) {
                boolean flag = j == -foliageRadius;
                boolean flag1 = j == foliageRadius;
                boolean flag2 = k == -foliageRadius;
                boolean flag3 = k == foliageRadius;
                boolean flag4 = flag || flag1;
                boolean flag5 = flag2 || flag3;
                if (!flag4 || !flag5) {
                    posMutable.setWithOffset(pos, j, treeHeight, k);
                    if (!levelAccessor.getBlockState(posMutable).isSolidRender(levelAccessor, posMutable)) {
                        boolean flag6 = flag || flag5 && j == 1 - foliageRadius;
                        boolean flag7 = flag1 || flag5 && j == foliageRadius - 1;
                        boolean flag8 = flag2 || flag4 && k == 1 - foliageRadius;
                        boolean flag9 = flag3 || flag4 && k == foliageRadius - 1;
                        BlockState blockstate = config.capProvider.getState(random, pos);

                        if (blockstate.hasProperty(HugeMushroomBlock.WEST)
                                && blockstate.hasProperty(HugeMushroomBlock.EAST)
                                && blockstate.hasProperty(HugeMushroomBlock.NORTH)
                                && blockstate.hasProperty(HugeMushroomBlock.SOUTH)) {
                            blockstate = blockstate.setValue(HugeMushroomBlock.WEST, flag6)
                                    .setValue(HugeMushroomBlock.EAST, flag7)
                                    .setValue(HugeMushroomBlock.NORTH, flag8)
                                    .setValue(HugeMushroomBlock.SOUTH, flag9);
                        }

                        if (blockstate.hasProperty(OnBlock.ACTIVE) && levelAccessor instanceof ServerLevelAccessor serverLevelAccessor) {
                            SwitchSavedData data = SwitchSavedData.get(serverLevelAccessor.getLevel());
                            data.add(posMutable);
                            blockstate = blockstate.setValue(OnBlock.ACTIVE, data.isActive());
                        }

                        this.setBlock(levelAccessor, posMutable, blockstate);
                    }
                }
            }
        }
    }

    @Override
    protected int getTreeRadiusForHeight(int height, int y, int foliageRadius, int topY) {
        return topY <= 3 ? 0 : foliageRadius;
    }
}
