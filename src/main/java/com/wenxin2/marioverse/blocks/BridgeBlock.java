package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BridgeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<HalfBlockStates> HALF = EnumProperty.create("half", HalfBlockStates.class);
    public Block logBlock;

    protected static final VoxelShape BOTTOM_AABB_X =
            Shapes.or(Block.box(1, 0, 0, 4, 3, 16),
                    Block.box(6, 0, 0, 10, 4, 16),
                    Block.box(12, 0, 0, 15, 3, 16)).optimize();

    protected static final VoxelShape TOP_AABB_X =
            Shapes.or(Block.box(1, 13, 0, 4, 16, 16),
                    Block.box(6, 12, 0, 10, 16, 16),
                    Block.box(12, 13, 0, 15, 16, 16)).optimize();

    protected static final VoxelShape BOTTOM_AABB_Z =
            Shapes.or(Block.box(0, 0, 1, 16, 3, 4),
                    Block.box(0, 0, 6, 16, 4, 10),
                    Block.box(0, 0, 12, 16, 3, 15)).optimize();

    protected static final VoxelShape TOP_AABB_Z =
            Shapes.or(Block.box(0, 13, 1, 16, 16, 4),
                    Block.box(0, 12, 6, 16, 16, 10),
                    Block.box(0, 13, 12, 16, 16, 15)).optimize();

    public BridgeBlock(Block logBlock, Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X)
                .setValue(HALF, HalfBlockStates.BOTTOM).setValue(WATERLOGGED, false));
        this.logBlock = logBlock;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(AXIS, HALF, WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction.Axis axis = state.getValue(AXIS);
        HalfBlockStates stateValue = state.getValue(HALF);

        if (axis == Direction.Axis.X) {
            if (stateValue == HalfBlockStates.TOP)
                return TOP_AABB_X;
            return BOTTOM_AABB_X;
        } else {
            if (stateValue == HalfBlockStates.TOP)
                return TOP_AABB_Z;
            return BOTTOM_AABB_Z;
        }
    }

    @NotNull
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                return switch (state.getValue(AXIS)) {
                    case X -> state.setValue(AXIS, Direction.Axis.Z);
                    case Z -> state.setValue(AXIS, Direction.Axis.X);
                    default -> state;
                };
            default: return state;
        }
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        BlockPos pos = placeContext.getClickedPos();
        FluidState fluidState = placeContext.getLevel().getFluidState(pos);
        Direction direction = placeContext.getHorizontalDirection();

        BlockState state = this.defaultBlockState()
                .setValue(AXIS, direction.getAxis())
                .setValue(HALF, HalfBlockStates.BOTTOM)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

        return direction != Direction.DOWN && (direction == Direction.UP || !(placeContext.getClickLocation().y - (double)pos.getY() > 0.5))
                ? state : state.setValue(HALF, HalfBlockStates.TOP);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean canBeReplaced(BlockState state, BlockPlaceContext placeContext) {
        ItemStack stack = placeContext.getItemInHand();
        HalfBlockStates stateValue = state.getValue(HALF);

        if (!stack.is(this.asItem()))
            return false;
        else if (placeContext.replacingClickedOnBlock()) {
            boolean flag = placeContext.getClickLocation().y - (double) placeContext.getClickedPos().getY() > 0.5;
            Direction direction = placeContext.getClickedFace();

            return stateValue == HalfBlockStates.BOTTOM
                    ? direction == Direction.UP || flag && direction.getAxis().isHorizontal()
                    : direction == Direction.DOWN || !flag && direction.getAxis().isHorizontal();
        } else return true;
    }

    @Override
    public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility itemAbility, boolean simulate) {
        if (itemAbility.equals(ItemAbilities.AXE_STRIP)) {
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
            String path = id.getPath();
            String logName = BuiltInRegistries.BLOCK.getKey(logBlock).getPath();

            if (!path.contains("stripped_") && path.contains("_log_bridge")) {
                String strippedPath = path.replace(logName, "stripped_" + logName);
                ResourceLocation strippedId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), strippedPath);

                Block strippedBlock = BuiltInRegistries.BLOCK.get(strippedId);
                if (strippedBlock != Blocks.AIR) {
                    return strippedBlock.defaultBlockState()
                            .setValue(AXIS, state.getValue(AXIS))
                            .setValue(HALF, state.getValue(HALF));
                }

            }
        }
        return super.getToolModifiedState(state, context, itemAbility, simulate);
    }
}
