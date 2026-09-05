package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ShrubroomBlock extends TallGrassBlock {
    protected static final VoxelShape SHAPE = Block
            .box(3, 0, 3, 13, 3, 13);

    public ShrubroomBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return ShrubroomBlock.findSpreadPos(level, random, pos, state) != null;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos targetPos = ShrubroomBlock.findSpreadPos(serverLevel, random, pos, state);
        if (targetPos != null)
            serverLevel.setBlockAndUpdate(targetPos, state);
    }

    @Nullable
    private static BlockPos findSpreadPos(LevelReader levelReader, RandomSource random, BlockPos pos, BlockState state) {
        for (Direction direction : Direction.Plane.HORIZONTAL.shuffledCopy(random)) {
            BlockPos targetPos = pos.relative(direction);
            
            if (levelReader.isEmptyBlock(targetPos) && state.canSurvive(levelReader, targetPos))
                return targetPos;
        }
        return null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        final Vec3 offset = state.getOffset(blockGetter, pos);

        return SHAPE.move(offset.x, offset.y, offset.z);
    }
}
