package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShortShroomgrassBlock extends TallGrassBlock {
    public ShortShroomgrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockState shroomgrass = BlockRegistry.SHROOMGRASS.get().defaultBlockState();
        if (shroomgrass.canSurvive(serverLevel, pos))
            serverLevel.setBlockAndUpdate(pos, shroomgrass);
    }
}
