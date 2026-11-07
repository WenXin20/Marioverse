package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShortShroomgrassBlock extends TallGrassBlock {
    protected static final VoxelShape SHAPE = Block
            .box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0);

    public ShortShroomgrassBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockState shroomgrass = BlockRegistry.SHROOMGRASS.get().defaultBlockState();
        if (shroomgrass.canSurvive(serverLevel, pos))
            serverLevel.setBlockAndUpdate(pos, shroomgrass);
    }
}
