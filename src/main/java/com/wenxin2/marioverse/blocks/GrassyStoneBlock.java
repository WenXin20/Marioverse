package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.PlacedFeatureRegistry;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.lighting.LightEngine;
import org.jetbrains.annotations.NotNull;

public class GrassyStoneBlock extends GrassBlock {
    public GrassyStoneBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (!canBeGrassy(state, serverLevel, pos)) {
            if (state.is(BlockRegistry.GRASSY_DEEP_FUNGAL_STONE))
                serverLevel.setBlockAndUpdate(pos, BlockRegistry.DEEP_FUNGAL_STONE.get().defaultBlockState());
            else if (state.is(BlockRegistry.GRASSY_FUNGAL_STONE))
                serverLevel.setBlockAndUpdate(pos, BlockRegistry.FUNGAL_STONE.get().defaultBlockState());
        }
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos posAbove = pos.above();
        Optional<Holder.Reference<PlacedFeature>> placedFeature = serverLevel.registryAccess()
                .registryOrThrow(Registries.PLACED_FEATURE)
                .getHolder(PlacedFeatureRegistry.SHROOMGRASS_BONEMEAL);

        label49:
        for (int i = 0; i < 128; i++) {
            BlockPos posAboveOffset = posAbove;

            for (int j = 0; j < i / 16; j++) {
                posAboveOffset = posAboveOffset.offset(random.nextInt(3) - 1,
                        (random.nextInt(3) - 1) * random.nextInt(3) / 2,
                        random.nextInt(3) - 1);
                if (!serverLevel.getBlockState(posAboveOffset.below()).is(this)
                        || serverLevel.getBlockState(posAboveOffset).isCollisionShapeFullBlock(serverLevel, posAboveOffset))
                    continue label49;
            }

            BlockState stateAbove = serverLevel.getBlockState(posAboveOffset);
            if ((stateAbove.is(BlockRegistry.SHORT_SHROOMGRASS.get()) || stateAbove.is(BlockRegistry.SHROOMGRASS.get()))
                    && random.nextInt(10) == 0)
                ((BonemealableBlock) stateAbove.getBlock()).performBonemeal(serverLevel, random, posAboveOffset, stateAbove);

            if (stateAbove.isAir()) {
                if (placedFeature.isEmpty())
                    continue;
                placedFeature.get().value().place(serverLevel, serverLevel.getChunkSource().getGenerator(), random, posAboveOffset);
            }
        }
    }

    @NotNull
    @Override
    public BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.NEIGHBOR_SPREADER;
    }

    private static boolean canBeGrassy(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos posAbove = pos.above();
        BlockState stateAbove = levelReader.getBlockState(posAbove);
        int i = LightEngine.getLightBlockInto(levelReader, state, pos, stateAbove, posAbove,
                Direction.UP, stateAbove.getLightBlock(levelReader, posAbove));

        return i < levelReader.getMaxLightLevel();
    }
}
