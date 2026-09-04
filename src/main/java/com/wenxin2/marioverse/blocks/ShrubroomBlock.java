package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShrubroomBlock extends TallGrassBlock {
    public ShrubroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos targetPos = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));

        if (serverLevel.isEmptyBlock(targetPos) && state.canSurvive(serverLevel, targetPos))
            serverLevel.setBlockAndUpdate(targetPos, state);
    }
}
