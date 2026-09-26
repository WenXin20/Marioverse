package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.HalfBlockStates;
import com.wenxin2.marioverse.utils.VoxelShapeUtils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BridgeBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<HalfBlockStates> HALF = BlockStatePropertyRegistry.HALF;

    protected static final VoxelShape BOTTOM_AABB_Z =
            Shapes.or(Block.box(0, 0, 0, 16, 9, 8),
                    Block.box(0, 2, 8, 16, 9, 16)).optimize();

    protected static final VoxelShape TOP_AABB_Z =
            Shapes.or(Block.box(0, 7, 0, 16, 16, 8),
                    Block.box(0, 9, 8, 16, 16, 16)).optimize();

    protected static final VoxelShape BOTTOM_AABB_X = VoxelShapeUtils.rotateShape(Direction.NORTH, Direction.EAST, BOTTOM_AABB_Z);

    protected static final VoxelShape TOP_AABB_X = VoxelShapeUtils.rotateShape(Direction.NORTH, Direction.EAST, TOP_AABB_Z);

    protected static final VoxelShape BOTTOM_COLLISION = Block.box(0.0, 1.0, 0.0, 16.0, 9.0, 16.0);

    protected static final VoxelShape TOP_COLLISION = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

    public BridgeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X)
                .setValue(HALF, HalfBlockStates.BOTTOM).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(AXIS, HALF, WATERLOGGED);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag options) {
        list.add(Component.translatable("block.marioverse.bridges.tooltip"));
        super.appendHoverText(stack, context, list, options);
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
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        HalfBlockStates stateValue = state.getValue(HALF);

        if (collisionContext instanceof EntityCollisionContext context && context.getEntity() != null) {
            if (stateValue == HalfBlockStates.TOP) {
                if (!context.isAbove(BOTTOM_COLLISION, pos, false))
                    return Shapes.empty();
            } else if (stateValue == HalfBlockStates.BOTTOM) {
                if (!context.isAbove(TOP_COLLISION, pos.below(), false))
                    return Shapes.empty();
            }
        }
        return super.getCollisionShape(state, blockGetter, pos, collisionContext);
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
}
