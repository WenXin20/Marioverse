package com.wenxin2.marioverse.world.feature;

import com.mojang.serialization.Codec;
import com.wenxin2.marioverse.blocks.SuperMushroomCapBlock;
import com.wenxin2.marioverse.blocks.SuperMushroomStemBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;

public abstract class ShapedHugeMushroomFeature extends Feature<HugeMushroomFeatureConfiguration> {
    public ShapedHugeMushroomFeature(Codec<HugeMushroomFeatureConfiguration> codec) {
        super(codec);
    }

    protected boolean placeMushroom(WorldGenLevel level, RandomSource random, BlockPos origin,
                                    HugeMushroomFeatureConfiguration config, int stemWidth, int stemHeight,
                                    int capMinY, int capMaxY, int capRadius, CapShape shape) {
        if (!this.isValidPosition(level, origin, stemWidth, stemHeight, capMinY, capMaxY, capRadius, shape))
            return false;

        BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();
        for (int y = capMinY; y <= capMaxY; y++) {
            for (int x = -capRadius; x < capRadius + stemWidth; x++) {
                for (int z = -capRadius; z < capRadius + stemWidth; z++) {
                    if (!isCap(shape, stemWidth, x, y, z))
                        continue;

                    posMutable.setWithOffset(origin, x, y, z);
                    if (level.getBlockState(posMutable).isSolidRender(level, posMutable))
                        continue;

                    BlockState state = config.capProvider.getState(random, origin);
                    state = with(state, HugeMushroomBlock.WEST, isExposed(shape, stemWidth, x, y, z, -1, 0));
                    state = with(state, HugeMushroomBlock.EAST, isExposed(shape, stemWidth, x, y, z, 1, 0));
                    state = with(state, HugeMushroomBlock.NORTH, isExposed(shape, stemWidth, x, y, z, 0, -1));
                    state = with(state, HugeMushroomBlock.SOUTH, isExposed(shape, stemWidth, x, y, z, 0, 1));
                    state = with(state, HugeMushroomBlock.UP, !isCap(shape, stemWidth, x, y + 1, z));
                    state = with(state, HugeMushroomBlock.DOWN, false);
                    state = with(state, SuperMushroomCapBlock.BOTTOM, y == capMinY);
                    this.setBlock(level, posMutable, state);
                }
            }
        }

        for (int y = 0; y < stemHeight; y++) {
            for (int x = 0; x < stemWidth; x++) {
                for (int z = 0; z < stemWidth; z++) {
                    posMutable.setWithOffset(origin, x, y, z);
                    if (level.getBlockState(posMutable).isSolidRender(level, posMutable))
                        continue;

                    this.setBlock(level, posMutable, with(config.stemProvider.getState(random, origin),
                            SuperMushroomStemBlock.END, y == stemHeight - 1));
                }
            }
        }
        return true;
    }

    private boolean isValidPosition(WorldGenLevel level, BlockPos origin, int stemWidth, int stemHeight,
                                    int capMinY, int capMaxY, int capRadius, CapShape shape) {
        if (origin.getY() < level.getMinBuildHeight() + 1 || origin.getY() + capMaxY + 1 >= level.getMaxBuildHeight())
            return false;

        BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();
        for (int x = 0; x < stemWidth; x++) {
            for (int z = 0; z < stemWidth; z++) {
                BlockState stateBelow = level.getBlockState(posMutable.setWithOffset(origin, x, -1, z));
                if (!isDirt(stateBelow) && !stateBelow.is(BlockTags.MUSHROOM_GROW_BLOCK))
                    return false;
            }
        }

        for (int y = 0; y <= capMaxY; y++) {
            for (int x = -capRadius; x < capRadius + stemWidth; x++) {
                for (int z = -capRadius; z < capRadius + stemWidth; z++) {
                    boolean isStem = y < stemHeight && x >= 0 && x < stemWidth && z >= 0 && z < stemWidth;
                    if (!isStem && (y < capMinY || !isCap(shape, stemWidth, x, y, z)))
                        continue;

                    BlockState state = level.getBlockState(posMutable.setWithOffset(origin, x, y, z));
                    if (!state.isAir() && !state.is(BlockTags.LEAVES))
                        return false;
                }
            }
        }
        return true;
    }

    private static boolean isCap(CapShape shape, int stemWidth, int x, int y, int z) {
        return shape.isCap(distance(x, stemWidth), y, distance(z, stemWidth));
    }

    private static boolean isExposed(CapShape shape, int stemWidth, int x, int y, int z, int stepX, int stepZ) {
        return !isCap(shape, stemWidth, x + stepX, y, z + stepZ)
                && distance(x + stepX, stemWidth) + distance(z + stepZ, stemWidth)
                > distance(x, stemWidth) + distance(z, stemWidth);
    }

    private static int distance(int offset, int stemWidth) {
        return offset < 0 ? -offset : Math.max(0, offset - stemWidth + 1);
    }

    private static BlockState with(BlockState state, BooleanProperty property, boolean value) {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }

    @FunctionalInterface
    protected interface CapShape {
        boolean isCap(int distanceX, int y, int distanceZ);
    }
}
