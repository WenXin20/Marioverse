package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class FungalStone extends Block implements BonemealableBlock {
    public static final MapCodec<FungalStone> CODEC = simpleCodec(FungalStone::new);

    @NotNull
    @Override
    public MapCodec<FungalStone> codec() {
        return CODEC;
    }

    public FungalStone(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        if (!levelReader.getBlockState(pos.above()).propagatesSkylightDown(levelReader, pos))
            return false;
        else {
            for (BlockPos posOffset : BlockPos.betweenClosed(pos.offset(-1, -1, -1),
                    pos.offset(1, 1, 1))) {
                if (levelReader.getBlockState(posOffset).is(TagRegistry.GRASSY_STONES))
                    return true;
            }
            return false;
        }
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        boolean isFungalStone = false;
        boolean isDeepFungalStone = false;

        for (BlockPos posOffset : BlockPos.betweenClosed(pos.offset(-1, -1, -1),
                pos.offset(1, 1, 1))) {
            BlockState stateOffset = serverLevel.getBlockState(posOffset);

            if (stateOffset.is(BlockRegistry.GRASSY_FUNGAL_STONE))
                isDeepFungalStone = true;

            if (stateOffset.is(BlockRegistry.GRASSY_DEEP_FUNGAL_STONE))
                isFungalStone = true;

            if (isDeepFungalStone && isFungalStone)
                break;
        }

        if (isDeepFungalStone && isFungalStone)
            serverLevel.setBlock(pos, random.nextBoolean() ? BlockRegistry.GRASSY_FUNGAL_STONE.get().defaultBlockState()
                    : BlockRegistry.GRASSY_DEEP_FUNGAL_STONE.get().defaultBlockState(), 3);
        else if (isDeepFungalStone)
            serverLevel.setBlock(pos, BlockRegistry.GRASSY_DEEP_FUNGAL_STONE.get().defaultBlockState(), 3);
        else if (isFungalStone)
            serverLevel.setBlock(pos, BlockRegistry.GRASSY_FUNGAL_STONE.get().defaultBlockState(), 3);
    }

    @NotNull
    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }
}
