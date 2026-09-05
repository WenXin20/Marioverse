package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShrubroomBlock extends TallGrassBlock {
    protected static final VoxelShape SHAPE = Block
            .box(3, 0, 3, 13, 3, 13);

    public ShrubroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos targetPos = pos.relative(Direction.Plane.HORIZONTAL.getRandomDirection(random));

        if (serverLevel.isEmptyBlock(targetPos) && state.canSurvive(serverLevel, targetPos))
            serverLevel.setBlockAndUpdate(targetPos, state);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        final Vec3 offset = state.getOffset(blockGetter, pos);

        return SHAPE.move(offset.x, offset.y, offset.z);
    }
}
