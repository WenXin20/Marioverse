package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ConnectionBlockStates;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.VoxelShapeUtils;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
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

public class LogBridgeStairBlock extends StairBlock implements SimpleWaterloggedBlock {
    public static final EnumProperty<ConnectionBlockStates> ABOVE = BlockStatePropertyRegistry.ABOVE;
    public static final EnumProperty<ConnectionBlockStates> BELOW = BlockStatePropertyRegistry.BELOW;
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

    private static final EnumMap<Direction, EnumMap<Half, EnumMap<StairsShape, VoxelShape>>> SHAPES =
            new EnumMap<>(Direction.class);

    static {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            EnumMap<Half, EnumMap<StairsShape, VoxelShape>> halfMap = new EnumMap<>(Half.class);
            for (Half half : Half.values()) {
                EnumMap<StairsShape, VoxelShape> shapeMap = new EnumMap<>(StairsShape.class);
                for (StairsShape stairsShape : StairsShape.values()) {
                    VoxelShape base = switch (stairsShape) {
                        case STRAIGHT -> (half == Half.TOP ? TOP_AABB : BOTTOM_AABB);
                        case INNER_LEFT, INNER_RIGHT -> (half == Half.TOP ? TOP_INNER_AABB : BOTTOM_INNER_AABB);
                        case OUTER_LEFT, OUTER_RIGHT -> (half == Half.TOP ? TOP_OUTER_AABB : BOTTOM_OUTER_AABB);
                    };

                    int rotSteps = switch (dir) {
                        case EAST -> 0;
                        case SOUTH -> 1;
                        case WEST -> 2;
                        default -> 3;
                    };

                    if (stairsShape == StairsShape.INNER_RIGHT || stairsShape == StairsShape.OUTER_RIGHT)
                        rotSteps = (rotSteps + 4) % 4;
                    else if (stairsShape == StairsShape.INNER_LEFT || stairsShape == StairsShape.OUTER_LEFT)
                        rotSteps = (rotSteps + 3) % 4;
                    if (half == Half.TOP && stairsShape != StairsShape.STRAIGHT)
                        rotSteps = (rotSteps + 1) % 4;

                    int rotation = rotSteps * 90;

                    shapeMap.put(stairsShape, VoxelShapeUtils.rotateShapeAxis(base, Direction.Axis.Y, rotation));
                }
                halfMap.put(half, shapeMap);
            }
            SHAPES.put(dir, halfMap);
        }
    }

    public LogBridgeStairBlock(BlockState state, Properties properties) {
        super(state, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(HALF, Half.BOTTOM).setValue(SHAPE, StairsShape.STRAIGHT)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(ABOVE, ConnectionBlockStates.NONE).setValue(BELOW, ConnectionBlockStates.NONE));
        this.logBlock = state.getBlock();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ABOVE, BELOW);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return LogBridgeStairBlock.updateConnections(super.getStateForPlacement(context), context.getLevel(), context.getClickedPos());
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return LogBridgeStairBlock.updateConnections(super.updateShape(state, direction, neighborState, level, pos, neighborPos), level, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        LogBridgeStairBlock.refreshDiagonalNeighbors(level, pos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (!newState.is(state.getBlock()))
            LogBridgeStairBlock.refreshDiagonalNeighbors(level, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag options) {
        list.add(Component.translatable("block.marioverse.bridge_stairs.tooltip"));
        super.appendHoverText(stack, context, list, options);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext ctx) {
        return SHAPES.get(state.getValue(FACING)).get(state.getValue(HALF)).get(state.getValue(SHAPE));
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

                Block strippedBlock = BuiltInRegistries.BLOCK.get(strippedId);

                if (strippedBlock != Blocks.AIR && strippedId.getPath().contains("stripped_")) {
                    return strippedBlock.defaultBlockState()
                            .setValue(FACING, state.getValue(FACING))
                            .setValue(HALF, state.getValue(HALF))
                            .setValue(SHAPE, state.getValue(SHAPE))
                            .setValue(WATERLOGGED, state.getValue(WATERLOGGED))
                            .setValue(ABOVE, state.getValue(ABOVE))
                            .setValue(BELOW, state.getValue(BELOW));
                }
            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.is(TagRegistry.FLAMMABLE_BRIDGE_STAIR_BLOCKS);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    public static BlockState updateConnections(BlockState state, LevelAccessor level, BlockPos pos) {
        if (state == null || !(state.getBlock() instanceof LogBridgeStairBlock))
            return state;

        return state.setValue(ABOVE, LogBridgeStairBlock.ropeEnd(state, level, pos,
                        LogBridgeStairBlock.ropeEndDirection(state, true), LogBridgeStairBlock.ropeEndDy(state, true)))
                .setValue(BELOW, LogBridgeStairBlock.ropeEnd(state, level, pos,
                        LogBridgeStairBlock.ropeEndDirection(state, false), LogBridgeStairBlock.ropeEndDy(state, false)));
    }

    public static ConnectionBlockStates ropeEnd(BlockState state, LevelAccessor level, BlockPos pos, Direction direction, int dy) {
        BlockPos sidePos = pos.relative(direction);
        BlockState sideState = level.getBlockState(sidePos);
        if (LogBridgeBlock.alignsWith(sideState, direction, dy > 0 ? HalfBlockStates.TOP : HalfBlockStates.BOTTOM)
                || LogBridgeStairBlock.ropeDy(sideState, direction.getOpposite()) == dy
                || Block.canSupportCenter(level, sidePos, direction.getOpposite()))
            return ConnectionBlockStates.FLAT;

        BlockState diagonalState = level.getBlockState(sidePos.above(dy));
        if (LogBridgeStairBlock.ropeDy(diagonalState, direction.getOpposite()) == -dy
                || LogBridgeBlock.alignsWith(diagonalState, direction, dy > 0 ? HalfBlockStates.BOTTOM : HalfBlockStates.TOP))
            return ConnectionBlockStates.DIAGONAL;
        return ConnectionBlockStates.NONE;
    }

    public static boolean ropeReaches(BlockState state, LevelAccessor level, BlockPos pos, BlockPos targetPos) {
        if (!(state.getBlock() instanceof LogBridgeStairBlock))
            return false;

        return LogBridgeStairBlock.ropeEndReaches(state, level, pos, true, targetPos)
                || LogBridgeStairBlock.ropeEndReaches(state, level, pos, false, targetPos);
    }

    public static boolean ropeEndReaches(BlockState state, LevelAccessor level, BlockPos pos, boolean above, BlockPos targetPos) {
        Direction direction = LogBridgeStairBlock.ropeEndDirection(state, above);
        int dy = LogBridgeStairBlock.ropeEndDy(state, above);

        return switch (LogBridgeStairBlock.ropeEnd(state, level, pos, direction, dy)) {
            case FLAT -> pos.relative(direction).equals(targetPos);
            case DIAGONAL -> pos.relative(direction).above(dy).equals(targetPos);
            case NONE -> false;
        };
    }

    public static int ropeDy(BlockState state, Direction direction) {
        if (!(state.getBlock() instanceof LogBridgeStairBlock))
            return 0;
        if (LogBridgeStairBlock.ropeEndDirection(state, true) == direction)
            return LogBridgeStairBlock.ropeEndDy(state, true);
        if (LogBridgeStairBlock.ropeEndDirection(state, false) == direction)
            return LogBridgeStairBlock.ropeEndDy(state, false);
        return 0;
    }

    public static Direction ropeEndDirection(BlockState state, boolean above) {
        StairsShape shape = state.getValue(SHAPE);
        Half half = state.getValue(HALF);
        Direction direction = switch (shape) {
            case STRAIGHT -> above ? Direction.EAST : Direction.WEST;
            case OUTER_LEFT, OUTER_RIGHT -> above ? Direction.NORTH : Direction.WEST;
            case INNER_LEFT, INNER_RIGHT -> above ? Direction.EAST : Direction.SOUTH;
        };

        if (half == Half.TOP && direction.getAxis() == Direction.Axis.Z)
            direction = direction.getOpposite();

        int yRot = (int) state.getValue(FACING).getClockWise().toYRot();
        if (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT)
            yRot += 270;
        if (shape != StairsShape.STRAIGHT && half == Half.TOP)
            yRot += 90;

        for (int i = 0; i < (yRot % 360) / 90; i++)
            direction = direction.getClockWise();
        return direction;
    }

    public static int ropeEndDy(BlockState state, boolean above) {
        StairsShape shape = state.getValue(SHAPE);
        boolean rises = shape == StairsShape.STRAIGHT ? above : shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT;
        int dy = rises ? 1 : -1;

        return state.getValue(HALF) == Half.BOTTOM ? dy : -dy;
    }

    public static void refreshDiagonalNeighbors(Level level, BlockPos pos) {
        if (level.isClientSide)
            return;

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            for (int dy = -1; dy <= 1; dy += 2) {
                BlockPos neighborPos = pos.relative(direction).above(dy);
                BlockState neighborState = level.getBlockState(neighborPos);
                BlockState updatedState = neighborState;

                if (neighborState.getBlock() instanceof LogBridgeStairBlock)
                    updatedState = LogBridgeStairBlock.updateConnections(neighborState, level, neighborPos);
                else if (neighborState.getBlock() instanceof LogBridgeBlock)
                    updatedState = LogBridgeBlock.updateConnections(neighborState, level, neighborPos);

                if (updatedState != neighborState)
                    level.setBlock(neighborPos, updatedState, Block.UPDATE_CLIENTS);
            }
        }
    }
}
