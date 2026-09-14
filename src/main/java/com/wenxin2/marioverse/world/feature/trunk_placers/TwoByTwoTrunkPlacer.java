package com.wenxin2.marioverse.world.feature.trunk_placers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.TreeRegistry;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.jetbrains.annotations.NotNull;

public class TwoByTwoTrunkPlacer extends TrunkPlacer {
    private static final int MAX_ROOT_DEPTH = 8;

    public static final MapCodec<TwoByTwoTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance).apply(instance, TwoByTwoTrunkPlacer::new));

    public TwoByTwoTrunkPlacer(int baseHeight, int heightRandA, int heightRandB) {
        super(baseHeight, heightRandA, heightRandB);
    }

    @NotNull
    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistry.TWO_BY_TWO_TRUNK_PLACER.get();
    }

    @NotNull
    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                            RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        this.placeRoots(level, blockSetter, random, config, pos);

        for (int y = 0; y < freeTreeHeight; y++)
            this.placeDoubleLog(level, blockSetter, random, config, pos, y);

        return List.of(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, true));
    }

    private void placeDoubleLog(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                TreeConfiguration config, BlockPos pos, int y) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        mutable.setWithOffset(pos, 0, y, 0);
        placeLog(level, blockSetter, random, mutable, config);
        mutable.setWithOffset(pos, 1, y, 0);
        placeLogIfFree(level, blockSetter, random, mutable, config);
        mutable.setWithOffset(pos, 0, y, 1);
        placeLogIfFree(level, blockSetter, random, mutable, config);
        mutable.setWithOffset(pos, 1, y, 1);
        placeLogIfFree(level, blockSetter, random, mutable, config);
    }

    private void placeRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                            TreeConfiguration config, BlockPos pos) {
        int centerDepth = this.placeRootColumn(level, blockSetter, random, config, pos);
        this.placeRootColumn(level, blockSetter, random, config, pos.offset(1, 0, 0));
        this.placeRootColumn(level, blockSetter, random, config, pos.offset(0, 0, 1));
        this.placeRootColumn(level, blockSetter, random, config, pos.offset(1, 0, 1));

        if (centerDepth == 0)
            setDirtAt(level, blockSetter, random, pos.below(), config);
    }

    private int placeRootColumn(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                TreeConfiguration config, BlockPos columnPos) {
        BlockPos.MutableBlockPos mutable = columnPos.mutable().move(Direction.DOWN);
        int depth = 0;
        while (depth < MAX_ROOT_DEPTH && this.isReplaceable(level, mutable)) {
            BlockState state = config.trunkProvider.getState(random, mutable).trySetValue(RotatedPillarBlock.AXIS, Direction.Axis.Y);
            blockSetter.accept(mutable.immutable(), state);
            mutable.move(Direction.DOWN);
            depth++;
        }
        return depth;
    }

    private boolean isReplaceable(LevelSimulatedReader level, BlockPos pos) {
        return level.isStateAtPosition(pos, state -> state.isAir() || state.canBeReplaced());
    }
}
