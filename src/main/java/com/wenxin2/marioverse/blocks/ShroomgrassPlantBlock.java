package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShroomgrassPlantBlock extends TallGrassBlock {
    public ShroomgrassPlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockState tallShroomgrass = BlockRegistry.TALL_SHROOMGRASS.get().defaultBlockState();
        if (tallShroomgrass.canSurvive(serverLevel, pos) && serverLevel.isEmptyBlock(pos.above()))
            DoublePlantBlock.placeAt(serverLevel, tallShroomgrass, pos, 2);
    }
}
