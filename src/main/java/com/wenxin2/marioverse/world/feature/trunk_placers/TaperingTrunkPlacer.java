package com.wenxin2.marioverse.world.feature.trunk_placers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.TreeRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import org.jetbrains.annotations.NotNull;

public class TaperingTrunkPlacer extends TrunkPlacer {
    private static final int MAX_ROOT_DEPTH = 8;

    public static final MapCodec<TaperingTrunkPlacer> CODEC = RecordCodecBuilder.mapCodec(instance ->
            trunkPlacerParts(instance)
                    .and(IntProvider.codec(1, 8).fieldOf("taper_rate").forGetter(p -> p.taperRate))
                    .and(Codec.BOOL.fieldOf("square_base").forGetter(p -> p.squareBase))
                    .and(BranchConfig.CODEC.fieldOf("branches").forGetter(p -> p.branchConfig))
                    .apply(instance, (baseHeight1, heightRandA1, heightRandB1, taperRate1, squareBase1, branchConfig1)
                            -> new TaperingTrunkPlacer(squareBase1, baseHeight1, heightRandA1, heightRandB1, taperRate1, branchConfig1)));

    private final IntProvider taperRate;
    private final boolean squareBase;
    private final BranchConfig branchConfig;

    public TaperingTrunkPlacer(boolean squareBase, int baseHeight, int heightRandA, int heightRandB,
                               IntProvider taperRate, BranchConfig branchConfig) {
        super(baseHeight, heightRandA, heightRandB);
        this.taperRate = taperRate;
        this.squareBase = squareBase;
        this.branchConfig = branchConfig;
    }

    @NotNull
    @Override
    protected TrunkPlacerType<?> type() {
        return TreeRegistry.TAPERING_TRUNK_PLACER.get();
    }

    @NotNull
    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                            RandomSource random, int freeTreeHeight, BlockPos pos, TreeConfiguration config) {
        int rate = Math.max(1, this.taperRate.sample(random));
        boolean useSquareBase = this.squareBase && freeTreeHeight >= rate * 2 + 3;

        this.placeRoots(level, blockSetter, random, config, pos, useSquareBase);

        int squareEnd = useSquareBase ? Math.min(rate, freeTreeHeight) : 0;
        int taperEnd = Math.min(squareEnd + rate, freeTreeHeight);
        int flareStart = Math.max(taperEnd, freeTreeHeight - 2);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        int minRadius = Math.max(1, this.branchConfig.minBranchRadius().sample(random));

        for (int y = 0; y < freeTreeHeight; y++) {
            if (y < squareEnd)
                this.placeSquare(level, blockSetter, random, config, pos, y, mutable);
            else if (y < taperEnd)
                this.placeCross(level, blockSetter, random, config, pos, y, mutable, 1);
            else if (y < flareStart) {
                if (minRadius <= 1) {
                    mutable.setWithOffset(pos, 0, y, 0);
                    placeLog(level, blockSetter, random, mutable, config);
                } else this.placeCross(level, blockSetter, random, config, pos, y, mutable, minRadius - 1);
            } else this.placeCross(level, blockSetter, random, config, pos, y, mutable, 1);
        }

        List<FoliagePlacer.FoliageAttachment> attachments = new ArrayList<>();
        attachments.add(new FoliagePlacer.FoliageAttachment(pos.above(freeTreeHeight), 0, false));
        attachments.addAll(this.placeBranches(level, blockSetter, random, config, pos, freeTreeHeight));

        return attachments;
    }

    private void placeRoots(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                            TreeConfiguration config, BlockPos pos, boolean useSquareBase) {
        int centerDepth = this.placeRootColumn(level, blockSetter, random, config, pos);

        if (useSquareBase) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dz == 0)
                        continue;
                    this.placeRootColumn(level, blockSetter, random, config, pos.offset(dx, 0, dz));
                }
            }
        }

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

    private void placeSquare(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                             TreeConfiguration config, BlockPos pos, int y, BlockPos.MutableBlockPos mutable) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                mutable.setWithOffset(pos, dx, y, dz);

                if (dx == 0 && dz == 0)
                    placeLog(level, blockSetter, random, mutable, config);
                else placeLogIfFree(level, blockSetter, random, mutable, config);
            }
        }
    }

    private void placeCross(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                            TreeConfiguration config, BlockPos pos, int y, BlockPos.MutableBlockPos mutable, int armLength) {
        mutable.setWithOffset(pos, 0, y, 0);
        placeLog(level, blockSetter, random, mutable, config);
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            for (int step = 1; step <= armLength; step++) {
                mutable.setWithOffset(pos, dir.getStepX() * step, y, dir.getStepZ() * step);
                placeLogIfFree(level, blockSetter, random, mutable, config);
            }
        }
    }

    private List<FoliagePlacer.FoliageAttachment> placeBranches(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter,
                                                                RandomSource random, TreeConfiguration config, BlockPos pos, int freeTreeHeight) {
        List<FoliagePlacer.FoliageAttachment> branchAttachments = new ArrayList<>();

        int drop = Math.max(1, this.branchConfig.branchDrop().sample(random));
        int splitY = Math.max(0, freeTreeHeight - drop);
        BlockPos trunkAttach = pos.above(splitY);

        int centerRise = this.branchConfig.branchLength().sample(random) + 1 + random.nextInt(4);
        BlockPos centerEnd = trunkAttach.above(centerRise);
        this.placeLimb(level, blockSetter, random, config, trunkAttach, centerEnd);
        branchAttachments.add(new FoliagePlacer.FoliageAttachment(centerEnd, 0, false));

        int branches = this.branchConfig.branchCount().sample(random);
        if (branches <= 0)
            return branchAttachments;

        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
        for (int i = directions.length - 1; i > 0; i--) { // Fisher-Yates so branches pick specific sides
            int j = random.nextInt(i + 1);
            Direction tmp = directions[i];
            directions[i] = directions[j];
            directions[j] = tmp;
        }

        int maxSideRise = Math.max(1, centerRise - 1); // side branches stay below the center leader's peak
        for (int i = 0; i < Math.min(branches, directions.length); i++) {
            Direction dir = directions[i];
            int rise = Math.min(this.branchConfig.branchLength().sample(random), maxSideRise);
            int reach = 2 + random.nextInt(2); // one block further out than before

            BlockPos tip = this.placeStaircaseBranch(blockSetter, random, config, trunkAttach, dir, reach, rise);
            branchAttachments.add(new FoliagePlacer.FoliageAttachment(tip, 0, false));
        }
        return branchAttachments;
    }

    private BlockPos placeStaircaseBranch(BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                                          TreeConfiguration config, BlockPos start, Direction dir, int reach, int rise) {
        BlockPos.MutableBlockPos current = start.mutable();
        this.placeBranchLog(blockSetter, random, config, current, dir.getAxis());

        int outSteps = 0;
        int upSteps = 0;

        while (outSteps < reach || upSteps < rise) {
            boolean moveOut = outSteps < reach && (upSteps >= rise || (outSteps + 1) * rise <= (upSteps + 1) * reach);
            if (moveOut) {
                current.move(dir);
                outSteps++;
                this.placeBranchLog(blockSetter, random, config, current, dir.getAxis());
            } else {
                current.move(Direction.UP);
                upSteps++;
                this.placeBranchLog(blockSetter, random, config, current, Direction.Axis.Y);
            }
        }
        return current.immutable();
    }

    private void placeBranchLog(BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random, TreeConfiguration config,
                                BlockPos pos, Direction.Axis axis) {
        BlockState state = config.trunkProvider.getState(random, pos).trySetValue(RotatedPillarBlock.AXIS, axis);
        blockSetter.accept(pos.immutable(), state);
    }

    private void placeLimb(LevelSimulatedReader level, BiConsumer<BlockPos, BlockState> blockSetter, RandomSource random,
                           TreeConfiguration config, BlockPos from, BlockPos to) {
        BlockPos delta = to.offset(-from.getX(), -from.getY(), -from.getZ());
        int steps = Math.max(Math.abs(delta.getX()), Math.max(Math.abs(delta.getY()), Math.abs(delta.getZ())));
        if (steps == 0)
            return;

        float stepX = (float) delta.getX() / steps;
        float stepY = (float) delta.getY() / steps;
        float stepZ = (float) delta.getZ() / steps;
        Direction.Axis axis = this.limbAxis(delta);

        for (int i = 0; i <= steps; i++) {
            BlockPos logPos = from.offset(Mth.floor(0.5F + i * stepX), Mth.floor(0.5F + i * stepY),
                    Mth.floor(0.5F + i * stepZ));
            BlockState state = config.trunkProvider.getState(random, logPos).trySetValue(RotatedPillarBlock.AXIS, axis);
            blockSetter.accept(logPos, state);
        }
    }

    private Direction.Axis limbAxis(BlockPos delta) {
        int dx = Math.abs(delta.getX());
        int dz = Math.abs(delta.getZ());
        int horizontal = Math.max(dx, dz);
        if (horizontal == 0)
            return Direction.Axis.Y;
        return dx >= dz ? Direction.Axis.X : Direction.Axis.Z;
    }

    public record BranchConfig(IntProvider minBranchRadius, IntProvider branchCount, IntProvider branchLength, IntProvider branchDrop) {
        public static final Codec<BranchConfig> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(IntProvider.codec(1, 4).fieldOf("min_branch_radius").forGetter(BranchConfig::minBranchRadius),
                        IntProvider.codec(0, 4).fieldOf("branch_count").forGetter(BranchConfig::branchCount),
                        IntProvider.codec(1, 10).fieldOf("branch_length").forGetter(BranchConfig::branchLength),
                        IntProvider.codec(1, 10).fieldOf("branch_drop").forGetter(BranchConfig::branchDrop))
                .apply(instance, BranchConfig::new));
    }
}