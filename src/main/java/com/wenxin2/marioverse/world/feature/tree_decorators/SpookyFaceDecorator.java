package com.wenxin2.marioverse.world.feature.tree_decorators;

import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import org.jetbrains.annotations.NotNull;

public class SpookyFaceDecorator extends TreeDecorator {
    private static final Direction[] HORIZONTAL_DIRECTIONS =
            {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    private static final int LEAF_DEPTH = 2;
    private static final int SMALL_LEAF_DEPTH = 1;
    private static final int SMALL_CANOPY_RADIUS = 4;
    private static final float CONNECTOR_CHANCE = 0.3F;
    private static final int REFERENCE_RADIUS = 5;
    private static final int MAX_EYE_MOUTH_GAP = 3;

    public static final MapCodec<SpookyFaceDecorator> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(p -> p.probability),
                    BuiltInRegistries.BLOCK.byNameCodec().fieldOf("dark_leaves").forGetter(p -> p.leavesBlock))
            .apply(instance, SpookyFaceDecorator::new));

    private final float probability;
    private final Block leavesBlock;

    public SpookyFaceDecorator(float probability, Block leavesBlock) {
        this.probability = probability;
        this.leavesBlock = leavesBlock;
    }

    @NotNull
    @Override
    protected TreeDecoratorType<?> type() {
        return TreeRegistry.SPOOKY_FACE_DECORATOR.get();
    }

    @Override
    public void place(@NotNull Context context) {
        RandomSource random = context.random();
        if (random.nextFloat() >= this.probability)
            return;

        ObjectArrayList<BlockPos> leaves = context.leaves();
        ObjectArrayList<BlockPos> logs = context.logs();
        if (leaves.isEmpty() || logs.isEmpty())
            return;

        Set<BlockPos> leafPositions = new HashSet<>(leaves);

        BlockPos trunkBase = logs.getFirst();
        int centerX = trunkBase.getX();
        int centerZ = trunkBase.getZ();
        int minY = leaves.getFirst().getY();
        int maxY = leaves.getLast().getY();
        int centerY = Math.round((minY + maxY) / 2.0F);

        int radius = this.canopyRadius(leaves);
        float scale = (float) radius / REFERENCE_RADIUS;
        boolean smallCanopy = radius < SMALL_CANOPY_RADIUS;
        int leafDepth = smallCanopy ? SMALL_LEAF_DEPTH : LEAF_DEPTH;

        Direction direction = HORIZONTAL_DIRECTIONS[random.nextInt(HORIZONTAL_DIRECTIONS.length)];
        Direction side = direction.getClockWise();
        BlockState darkLeaves = this.leavesBlock.defaultBlockState();

        int eyeOffset = Math.max(1, Math.round(1 * scale));
        int mouthOffset = Math.min(Math.max(1, Math.round(2 * scale)), Math.max(1, MAX_EYE_MOUTH_GAP - eyeOffset));
        int eyeY = centerY + eyeOffset;
        int eyeGap = Math.min(2, Math.max(1, Math.round(2 * scale)));

        int searchDistance = radius + 2;

        this.darkenColumnAtSurface(context, leafPositions, darkLeaves, new BlockPos(centerX, eyeY, centerZ).relative(side, eyeGap), direction, leafDepth, searchDistance);
        this.darkenColumnAtSurface(context, leafPositions, darkLeaves, new BlockPos(centerX, eyeY + 1, centerZ).relative(side, eyeGap), direction, leafDepth, searchDistance);
        this.darkenColumnAtSurface(context, leafPositions, darkLeaves, new BlockPos(centerX, eyeY, centerZ).relative(side, -eyeGap), direction, leafDepth, searchDistance);
        this.darkenColumnAtSurface(context, leafPositions, darkLeaves, new BlockPos(centerX, eyeY + 1, centerZ).relative(side, -eyeGap), direction, leafDepth, searchDistance);

        int mouthY = centerY - mouthOffset;
        int mouthWidthMin = Math.max(3, Math.round(5 * scale));
        int mouthWidthRange = Math.max(1, Math.round(4 * scale));
        int mouthWidth = mouthWidthMin + random.nextInt(mouthWidthRange);
        int startOffset = -(mouthWidth / 2);

        if (smallCanopy) {
            for (int index = 0; index < mouthWidth; index++) {
                int offset = startOffset + index;
                this.darkenColumnAtSurface(context, leafPositions, darkLeaves,
                        new BlockPos(centerX, mouthY, centerZ).relative(side, offset), direction, leafDepth, searchDistance);
            }
            return;
        }

        boolean startHigh = random.nextBoolean();
        int previousHeight = -1;

        for (int index = 0; index < mouthWidth; index++) {
            int offset = startOffset + index;
            int height = ((index % 2 == 0) == startHigh) ? 1 : 0;

            this.darkenColumnAtSurface(context, leafPositions, darkLeaves,
                    new BlockPos(centerX, mouthY + height, centerZ).relative(side, offset), direction, leafDepth, searchDistance);

            if (previousHeight >= 0 && previousHeight != height && random.nextFloat() < CONNECTOR_CHANCE)
                this.darkenColumnAtSurface(context, leafPositions, darkLeaves,
                        new BlockPos(centerX, mouthY + previousHeight, centerZ).relative(side, offset), direction, leafDepth, searchDistance);

            previousHeight = height;
        }
    }

    private void darkenColumnAtSurface(Context context, Set<BlockPos> leafPositions, BlockState darkLeaves, BlockPos rayOrigin, Direction direction, int depth, int searchDistance) {
        int surface = this.surfaceOffset(leafPositions, rayOrigin, direction, searchDistance);
        if (surface <= 0)
            return;

        BlockPos pos = rayOrigin.relative(direction, surface);
        for (int step = 0; step < depth; step++) {
            this.darken(context, leafPositions, darkLeaves, pos);
            pos = pos.relative(direction, -1);
        }
    }

    private int canopyRadius(ObjectArrayList<BlockPos> leaves) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : leaves) {
            minX = Math.min(minX, pos.getX());
            maxX = Math.max(maxX, pos.getX());
            minZ = Math.min(minZ, pos.getZ());
            maxZ = Math.max(maxZ, pos.getZ());
        }
        return Math.max(1, ((maxX - minX) + (maxZ - minZ)) / 4);
    }

    private int surfaceOffset(Set<BlockPos> leafPositions, BlockPos center, Direction direction, int maxDistance) {
        int surface = 0;

        for (int distance = 1; distance <= maxDistance; distance++) {
            if (leafPositions.contains(center.relative(direction, distance)))
                surface = distance;
        }
        return surface;
    }

    private void darken(Context context, Set<BlockPos> leafPositions, BlockState darkLeaves, BlockPos pos) {
        if (leafPositions.contains(pos))
            context.setBlock(pos, darkLeaves);
    }
}
