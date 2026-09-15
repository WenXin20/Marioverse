package com.wenxin2.marioverse.world.feature.tree_decorators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.TreeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class BranchGrowthDecorator extends TreeDecorator {
    private static final Direction[] HORIZONTAL_DIRECTIONS =
            {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int MAX_BRANCH_STEPS = 32;
    private static final int MAX_APPROACH_STEPS = 6;
    private static final int TAIL_TRIM = 3;
    private static final int MIN_PLACED_STEPS = 2;
    private static final int TOP_MARGIN = 2;
    private static final int FINAL_ASCENT_STEPS = 2;
    private static final int FORK_TAIL_TRIM = 2;
    private static final int FORK_MIN_NEW_STEPS = 1;

    public static final MapCodec<BranchGrowthDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    IntProvider.codec(0, 8).fieldOf("branch_count").forGetter(p -> p.branchCount),
                    IntProvider.codec(1, 12).fieldOf("branch_drop").forGetter(p -> p.branchDrop),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("log").forGetter(p -> p.logBlock))
            .apply(instance, BranchGrowthDecorator::new));

    private final IntProvider branchCount;
    private final IntProvider branchDrop;
    private final Block logBlock;

    public BranchGrowthDecorator(IntProvider branchCount, IntProvider branchDrop, Block logBlock) {
        this.branchCount = branchCount;
        this.branchDrop = branchDrop;
        this.logBlock = logBlock;
    }

    @NotNull
    @Override
    protected TreeDecoratorType<?> type() {
        return TreeRegistry.BRANCH_GROWTH_DECORATOR.get();
    }

    @Override
    public void place(@NotNull Context context) {
        RandomSource random = context.random();
        ObjectArrayList<BlockPos> leaves = context.leaves();
        ObjectArrayList<BlockPos> logs = context.logs();
        if (leaves.isEmpty() || logs.isEmpty())
            return;

        Set<BlockPos> leafPositions = new HashSet<>(leaves);

        int topY = Integer.MIN_VALUE;
        int minLeafY = Integer.MAX_VALUE;
        int maxLeafY = Integer.MIN_VALUE;
        for (BlockPos log : logs)
            topY = Math.max(topY, log.getY());
        for (BlockPos leaf : leaves) {
            minLeafY = Math.min(minLeafY, leaf.getY());
            maxLeafY = Math.max(maxLeafY, leaf.getY());
        }
        int growthCeiling = maxLeafY - TOP_MARGIN;

        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
        for (BlockPos log : logs) {
            if (log.getY() != topY)
                continue;
            minX = Math.min(minX, log.getX());
            maxX = Math.max(maxX, log.getX());
            minZ = Math.min(minZ, log.getZ());
            maxZ = Math.max(maxZ, log.getZ());
        }
        int centerX = Math.round((minX + maxX) / 2.0F);
        int centerZ = Math.round((minZ + maxZ) / 2.0F);

        BlockPos.MutableBlockPos centerColumn = new BlockPos.MutableBlockPos(minX, topY, minZ);
        while (centerColumn.getY() < growthCeiling && leafPositions.contains(centerColumn.above())) {
            centerColumn.move(Direction.UP);
            this.placeSquareLayer(context, leafPositions, minX, centerColumn.getY(), minZ);
        }

        int branches = this.branchCount.sample(random);
        if (branches <= 0)
            return;

        int domeOriginY = topY + 1;
        int drop = Math.max(1, this.branchDrop.sample(random));
        int splitY = Math.max(minLeafY - MAX_APPROACH_STEPS, domeOriginY - drop);

        Direction[] directions = HORIZONTAL_DIRECTIONS.clone();
        for (int i = directions.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            Direction tmp = directions[i];
            directions[i] = directions[j];
            directions[j] = tmp;
        }

        for (int i = 0; i < Math.min(branches, directions.length); i++) {
            Direction dir = directions[i];
            int edgeX = dir == Direction.EAST ? maxX + 1 : dir == Direction.WEST ? minX - 1 : centerX;
            int edgeZ = dir == Direction.SOUTH ? maxZ + 1 : dir == Direction.NORTH ? minZ - 1 : centerZ;

            this.growBranch(context, leafPositions, new BlockPos(edgeX, splitY, edgeZ), dir, growthCeiling);
        }
    }

    private void growBranch(Context context, Set<BlockPos> leafPositions, BlockPos start, Direction dir, int growthCeiling) {
        List<BlockPos> path = new ArrayList<>();
        List<Direction.Axis> axes = new ArrayList<>();
        path.add(start.below());
        axes.add(Direction.Axis.Y);
        path.add(start);
        axes.add(dir.getAxis());

        BlockPos.MutableBlockPos current = start.mutable();
        int outSteps = 0;
        int upSteps = 0;
        boolean reachedLeaves = leafPositions.contains(start);

        for (int step = 0; step < MAX_BRANCH_STEPS; step++) {
            boolean outLeaf = leafPositions.contains(current.relative(dir));
            boolean upLeaf = current.getY() < growthCeiling && leafPositions.contains(current.above());

            if (reachedLeaves) {
                if (!outLeaf && !upLeaf)
                    break;
            } else {
                if (outLeaf || upLeaf)
                    reachedLeaves = true;
                else if (step >= MAX_APPROACH_STEPS)
                    return;
            }

            boolean moveOut = reachedLeaves ? (outLeaf && (!upLeaf || outSteps <= upSteps)) : outSteps <= upSteps;
            if (moveOut) {
                current.move(dir);
                outSteps++;
                axes.add(dir.getAxis());
            } else {
                current.move(Direction.UP);
                upSteps++;
                axes.add(Direction.Axis.Y);
            }
            path.add(current.immutable());
        }

        if (!reachedLeaves)
            return;

        int placeCount = path.size() - TAIL_TRIM;
        if (placeCount < MIN_PLACED_STEPS)
            return;

        for (int i = 0; i < placeCount; i++)
            context.setBlock(path.get(i), this.logState(axes.get(i)));

        BlockPos.MutableBlockPos ascent = path.get(placeCount - 1).mutable();
        for (int extra = 0; extra < FINAL_ASCENT_STEPS; extra++) {
            if (ascent.getY() >= growthCeiling || !leafPositions.contains(ascent.above()))
                break;
            ascent.move(Direction.UP);
            context.setBlock(ascent.immutable(), this.logState(Direction.Axis.Y));
        }

        BlockPos tip = ascent.immutable();
        this.growForkSpur(context, leafPositions, tip, dir.getClockWise());
        this.growForkSpur(context, leafPositions, tip, dir.getCounterClockWise());

        int lowerAngle = this.findSecondToLastAngle(axes, placeCount);
        if (lowerAngle > 0) {
            BlockPos lowerForkOrigin = path.get(lowerAngle);
            this.growLowerFork(context, leafPositions, lowerForkOrigin, dir.getClockWise());
            this.growLowerFork(context, leafPositions, lowerForkOrigin, dir.getCounterClockWise());
        }
    }

    private void growLowerFork(Context context, Set<BlockPos> leafPositions, BlockPos origin, Direction dir) {
        if (!this.growForkSpur(context, leafPositions, origin, dir))
            this.growForkSpur(context, leafPositions, origin.above(), dir);
    }

    private int findSecondToLastAngle(List<Direction.Axis> axes, int limit) {
        int lastChange = -1;
        int secondLastChange = -1;
        for (int i = 1; i < limit; i++) {
            if (axes.get(i) != axes.get(i - 1)) {
                secondLastChange = lastChange;
                lastChange = i;
            }
        }
        return secondLastChange;
    }

    private boolean growForkSpur(Context context, Set<BlockPos> leafPositions, BlockPos origin, Direction dir) {
        List<BlockPos> path = new ArrayList<>();
        path.add(origin);
        BlockPos.MutableBlockPos current = origin.mutable();

        for (int step = 0; step < MAX_BRANCH_STEPS; step++) {
            if (!leafPositions.contains(current.relative(dir)))
                break;
            current.move(dir);
            path.add(current.immutable());
        }

        int placeCount = path.size() - FORK_TAIL_TRIM;
        if (placeCount - 1 < FORK_MIN_NEW_STEPS)
            return false;

        for (int i = 1; i < placeCount; i++)
            context.setBlock(path.get(i), this.logState(dir.getAxis()));
        return true;
    }

    private void placeSquareLayer(Context context, Set<BlockPos> leafPositions, int minX, int y, int minZ) {
        for (int dx = 0; dx <= 1; dx++) {
            for (int dz = 0; dz <= 1; dz++) {
                BlockPos pos = new BlockPos(minX + dx, y, minZ + dz);
                if (leafPositions.contains(pos))
                    context.setBlock(pos, this.logState(Direction.Axis.Y));
            }
        }
    }

    private BlockState logState(Direction.Axis axis) {
        return this.logBlock.defaultBlockState().trySetValue(RotatedPillarBlock.AXIS, axis);
    }
}
