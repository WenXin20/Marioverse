package com.wenxin2.marioverse.world.feature.tree_decorators;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.TreeRegistry;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.HashSet;
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
    private static final int START_SEARCH_DEPTH = 2;
    private static final int TOP_MARGIN = 2;

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
        int maxLeafY = Integer.MIN_VALUE;
        for (BlockPos log : logs)
            topY = Math.max(topY, log.getY());
        for (BlockPos leaf : leaves)
            maxLeafY = Math.max(maxLeafY, leaf.getY());
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

        BlockPos.MutableBlockPos centerColumn = new BlockPos.MutableBlockPos(centerX, topY, centerZ);
        while (centerColumn.getY() < growthCeiling && leafPositions.contains(centerColumn.above())) {
            centerColumn.move(Direction.UP);
            context.setBlock(centerColumn.immutable(), this.logState(Direction.Axis.Y));
        }

        int branches = this.branchCount.sample(random);
        if (branches <= 0)
            return;

        int domeOriginY = topY + 1;
        int drop = Math.max(1, this.branchDrop.sample(random));
        int splitY = domeOriginY - drop;

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

            BlockPos start = this.findBranchStart(leafPositions, edgeX, splitY, edgeZ);
            if (start == null)
                continue;

            this.growBranch(context, leafPositions, start, dir, growthCeiling);
        }
    }

    private BlockPos findBranchStart(Set<BlockPos> leafPositions, int x, int baseY, int z) {
        for (int dy = 0; dy <= START_SEARCH_DEPTH; dy++) {
            BlockPos candidate = new BlockPos(x, baseY - dy, z);
            if (leafPositions.contains(candidate))
                return candidate;
        }
        return null;
    }

    private void growBranch(Context context, Set<BlockPos> leafPositions, BlockPos start, Direction dir, int growthCeiling) {
        context.setBlock(start, this.logState(dir.getAxis()));
        BlockPos.MutableBlockPos current = start.mutable();
        int outSteps = 0;
        int upSteps = 0;

        for (int step = 0; step < MAX_BRANCH_STEPS; step++) {
            boolean outOk = leafPositions.contains(current.relative(dir));
            boolean upOk = current.getY() < growthCeiling && leafPositions.contains(current.above());
            if (!outOk && !upOk)
                break;

            boolean moveOut = outOk && (!upOk || outSteps <= upSteps);
            if (moveOut) {
                current.move(dir);
                outSteps++;
                context.setBlock(current.immutable(), this.logState(dir.getAxis()));
            } else {
                current.move(Direction.UP);
                upSteps++;
                context.setBlock(current.immutable(), this.logState(Direction.Axis.Y));
            }
        }
    }

    private BlockState logState(Direction.Axis axis) {
        return this.logBlock.defaultBlockState().trySetValue(RotatedPillarBlock.AXIS, axis);
    }
}
