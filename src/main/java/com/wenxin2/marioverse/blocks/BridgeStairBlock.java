package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.VoxelShapeUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BridgeStairBlock extends StairBlock implements SimpleWaterloggedBlock {
    public Block logBlock;

    protected static final VoxelShape TOP_AABB =
            Shapes.or(Block.box(1, 13, 0, 4, 16, 16),
                    Block.box(6, 6, 0, 10, 10, 16),
                    Block.box(12, 0, 0, 15, 3, 16)).optimize();
    protected static final VoxelShape BOTTOM_AABB =
            Shapes.or(Block.box(1, 0, 0, 4, 3, 16),
                    Block.box(6, 6, 0, 10, 10, 16),
                    Block.box(12, 13, 0, 15, 16, 16)).optimize();
    protected static final VoxelShape TOP_INNER_AABB =
            Shapes.or(Block.box(1, 13, 12, 4, 16, 16),
                    Block.box(6, 6, 6, 10, 10, 16),
                    Block.box(12, 0, 1, 15, 3, 16),
                    Block.box(0, 13, 12, 1, 16, 15),
                    Block.box(0, 6, 6, 6, 10, 10),
                    Block.box(0, 0, 1, 12, 3, 4)).optimize();
    protected static final VoxelShape BOTTOM_INNER_AABB =
            Shapes.or(Block.box(1, 0, 0, 4, 3, 4),
                    Block.box(6, 6, 0, 10, 10, 10),
                    Block.box(12, 13, 0, 15, 16, 15),
                    Block.box(0, 0, 1, 1, 3, 4),
                    Block.box(0, 6, 6, 6, 10, 10),
                    Block.box(0, 13, 12, 12, 16, 15)).optimize();
    protected static final VoxelShape TOP_OUTER_AABB =
            Shapes.or(Block.box(1, 13, 0, 4, 16, 15),
                    Block.box(6, 6, 0, 10, 10, 10),
                    Block.box(12, 0, 0, 15, 3, 4),
                    Block.box(4, 13, 12, 16, 16, 15),
                    Block.box(10, 6, 6, 16, 10, 10),
                    Block.box(15, 0, 1, 16, 3, 4)).optimize();
    protected static final VoxelShape BOTTOM_OUTER_AABB =
            Shapes.or(Block.box(1, 0, 1, 4, 3, 16),
                    Block.box(6, 6, 6, 10, 10, 16),
                    Block.box(12, 13, 12, 15, 16, 16),
                    Block.box(4, 0, 1, 16, 3, 4),
                    Block.box(10, 6, 6, 16, 10, 10),
                    Block.box(15, 13, 12, 16, 16, 15)).optimize();

    protected static final VoxelShape BOTTOM_COLLISION = Block.box(0.0, 1.0, 0.0, 16.0, 8.0, 16.0);

    protected static final VoxelShape TOP_COLLISION = Block.box(0.0, 9.0, 0.0, 16.0, 16.0, 16.0);

    public BridgeStairBlock(BlockState state, Properties properties) {
        super(state, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT)
                .setValue(WATERLOGGED, Boolean.FALSE)
        );
        this.logBlock = state.getBlock();
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext ctx) {
        Direction facing = state.getValue(FACING);
        Half half = state.getValue(HALF);
        StairsShape shape = state.getValue(SHAPE);

        VoxelShape base;
        switch (shape) {
            case STRAIGHT -> base = (half == Half.TOP) ? TOP_AABB : BOTTOM_AABB;
            case INNER_LEFT, INNER_RIGHT -> base = (half == Half.TOP) ? TOP_INNER_AABB : BOTTOM_INNER_AABB;
            case OUTER_LEFT, OUTER_RIGHT -> base = (half == Half.TOP) ? TOP_OUTER_AABB : BOTTOM_OUTER_AABB;
            default -> base = Shapes.block();
        }

        int baseIdx = Direction.EAST.get2DDataValue();
        int targetIdx = facing.get2DDataValue();
        int rotSteps = (targetIdx - baseIdx) % 4;
        if (rotSteps < 0) rotSteps += 4;

        if (shape == StairsShape.INNER_RIGHT || shape == StairsShape.OUTER_RIGHT)
            rotSteps = (rotSteps + 4) % 4;
        else if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
            rotSteps = (rotSteps + 3) % 4;
        if (half == Half.TOP && (shape != StairsShape.STRAIGHT))
            rotSteps = (rotSteps + 1) % 4;

        int degrees = rotSteps * 90;
        return VoxelShapeUtils.rotateShapeAxis(base, Direction.Axis.Y, degrees).optimize();
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        VoxelShape original = this.getShape(state, blockGetter, pos, collisionContext);

        if (!(collisionContext instanceof EntityCollisionContext context) || context.getEntity() == null)
            return Shapes.empty();

        Entity entity = context.getEntity();
        double stepHeight = 0.6D;
        if (entity instanceof LivingEntity living)
            stepHeight = living.getAttributeValue(Attributes.STEP_HEIGHT);

        double feetY = entity.getBoundingBox().minY;

        List<AABB> tallBoxes = new ArrayList<>();
        List<AABB> lowBoxes  = new ArrayList<>();
        for (AABB aabb : original.toAabbs()) {
            double h = aabb.maxY - aabb.minY;
            if (h > stepHeight + 1e-5) tallBoxes.add(aabb);
            else lowBoxes.add(aabb);
        }

        VoxelShape result = Shapes.empty();

        // For each low box: include it only if entity can step on / is standing on it,
        // and subtract any horizontal overlap with tall boxes so the low box loses side faces that cause sticking.
        for (AABB low : lowBoxes) {
            double lowMinY = low.minY;
            double lowMaxY = low.maxY;
            double lowBottomWorld = pos.getY() + lowMinY;
            double lowTopWorld = pos.getY() + lowMaxY;

            // entity must be able to step onto OR already standing on that low box
            if (!(feetY + stepHeight + 1e-5 >= lowTopWorld || feetY >= lowBottomWorld - 1e-5)) continue;

            VoxelShape lowShape = Shapes.box(low.minX, lowMinY, low.minZ, low.maxX, lowMaxY, low.maxZ);

            // subtract overlapping horizontal footprints from any tall box
            for (AABB tall : tallBoxes) {
                double overlapMinX = Math.max(low.minX, tall.minX);
                double overlapMaxX = Math.min(low.maxX, tall.maxX);
                double overlapMinZ = Math.max(low.minZ, tall.minZ);
                double overlapMaxZ = Math.min(low.maxZ, tall.maxZ);

                if (overlapMinX < overlapMaxX && overlapMinZ < overlapMaxZ) {
                    // overlap shape uses the low box Y-range so we remove only the vertical face portion at that Y
                    VoxelShape overlap = Shapes.box(overlapMinX, lowMinY, overlapMinZ, overlapMaxX, lowMaxY, overlapMaxZ);
                    lowShape = Shapes.join(lowShape, overlap, BooleanOp.ONLY_FIRST);
                    if (lowShape.isEmpty()) break;
                }
            }

            if (!lowShape.isEmpty()) result = Shapes.or(result, lowShape);
        }

        return result.optimize();
    }

    @Nullable
    @Override
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.AXE_STRIP)) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            String path = id.getPath();
            String logName = BuiltInRegistries.BLOCK.getKey(logBlock).getPath();

            if (!path.contains("stripped_")
                    && (path.contains("bamboo_bridge_stairs") || path.contains("log_bridge_stairs") || path.contains("stem_bridge_stairs"))) {
                String removeBlockName = logName.replace("_block", "");
                String strippedPath = path.replace(removeBlockName, "stripped_" + removeBlockName);
                ResourceLocation strippedId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), strippedPath);

                Block strippedBlock = BuiltInRegistries.BLOCK.get(strippedId);if (strippedBlock != Blocks.AIR && strippedId.getPath().contains("stripped_")) {
                    return strippedBlock.defaultBlockState()
                            .setValue(FACING, state.getValue(FACING))
                            .setValue(HALF, state.getValue(HALF))
                            .setValue(SHAPE, state.getValue(SHAPE))
                            .setValue(WATERLOGGED, state.getValue(WATERLOGGED));
                }
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.is(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_BLOCKS);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }
}
