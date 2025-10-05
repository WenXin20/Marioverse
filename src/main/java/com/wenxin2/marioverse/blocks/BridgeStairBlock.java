package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.VoxelShapeUtils;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.phys.shapes.CollisionContext;
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
                    Block.box(12, 0, 0, 15, 3, 16)).optimize();;
    protected static final VoxelShape BOTTOM_AABB =
            Shapes.or(Block.box(1, 0, 0, 4, 3, 16),
                    Block.box(6, 6, 0, 10, 10, 16),
                    Block.box(12, 13, 0, 15, 16, 16)).optimize();

    protected static final VoxelShape OCTET_ENN = Block.box(0.0, 0.0, 0.0, 4.0, 3.0, 8.0);
    protected static final VoxelShape OCTET_ENP = Block.box(0.0, 0.0, 8.0, 4.0, 3.0, 16.0);
    protected static final VoxelShape OCTET_MNN = Block.box(6.0, 6.0, 0.0, 10.0, 8.0, 8.0);
    protected static final VoxelShape OCTET_MNP = Block.box(6.0, 6.0, 8.0, 10.0, 8.0, 16.0);
    protected static final VoxelShape OCTET_WNN = Block.box(12.0, 13.0, 0.0, 15.0, 16.0, 8.0);
    protected static final VoxelShape OCTET_WNP = Block.box(12.0, 13.0, 8.0, 15.0, 16.0, 16.0);
    protected static final VoxelShape[] BRIDGE_STAIR_SHAPES = new VoxelShape[] {
            Shapes.or(OCTET_ENN, OCTET_ENP, OCTET_MNN, OCTET_MNP, OCTET_WNN, OCTET_WNP).optimize()
    };
    private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

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

        VoxelShape base = (half == Half.TOP) ? TOP_AABB : BOTTOM_AABB;
        VoxelShape rotated = VoxelShapeUtils.rotateShape(base, facing);
        return applyShapeVariant(rotated, facing, shape);
    }

    private static VoxelShape applyShapeVariant(VoxelShape base, Direction facing, StairsShape shape) {
        switch (shape) {
            case INNER_LEFT -> {
                return Shapes.or(base, VoxelShapeUtils.rotateShape(OCTET_WNN, facing));
            }
            case INNER_RIGHT -> {
                return Shapes.or(base, VoxelShapeUtils.rotateShape(OCTET_MNN, facing));
            }
            case OUTER_LEFT -> {
                return Shapes.or(base, VoxelShapeUtils.rotateShape(OCTET_ENN, facing));
            }
            case OUTER_RIGHT -> {
                return Shapes.or(base, VoxelShapeUtils.rotateShape(OCTET_ENP, facing));
            }
            default -> {
                return base;
            }
        }
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
