package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.VoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.AABB;
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
        VoxelShape shape = this.getShape(state, blockGetter, pos, collisionContext);

        if (collisionContext instanceof EntityCollisionContext context && context.getEntity() instanceof LivingEntity livingEntity) {
            double stepHeight = livingEntity.getAttributeValue(Attributes.STEP_HEIGHT);
            double feetY = livingEntity.getBoundingBox().minY;

            for (AABB box : shape.toAabbs()) {
                double boxTopWorld = pos.getY() + box.maxY;
                double boxHeight = box.maxY - box.minY;

                if (boxHeight <= stepHeight + 1e-3 && feetY + stepHeight >= boxTopWorld - 1e-3)
                    return shape;
            }
        }
        return Shapes.empty();
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
