package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.blocks.states.ColumnBlockStates;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import com.wenxin2.marioverse.utils.VoxelShapeUtils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LoopholeBlock extends Block implements SimpleWaterloggedBlock {
    public static final MapCodec<LoopholeBlock> CODEC = simpleCodec(LoopholeBlock::new);
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<ColumnBlockStates> COLUMN = BlockStatePropertyRegistry.COLUMN;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape[] SHAPES = LoopholeBlock.makeShapes(5.0, 11.0);

    public LoopholeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X)
                .setValue(COLUMN, ColumnBlockStates.NONE).setValue(WATERLOGGED, false));
    }

    @NotNull
    @Override
    public MapCodec<? extends LoopholeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(AXIS, COLUMN, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        int axisIndex = state.getValue(AXIS) == Direction.Axis.X ? 1 : 0;
        return this.getShapes()[state.getValue(COLUMN).ordinal() * 2 + axisIndex];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        FluidState fluidState = level.getFluidState(pos);
        Direction.Axis axis = context.getHorizontalDirection().getAxis();
        ColumnBlockStates column = context.isSecondaryUseActive() ? ColumnBlockStates.NONE
                : LoopholeBlock.getColumnState(LoopholeBlock.canConnect(level.getBlockState(pos.above()), axis),
                        LoopholeBlock.canConnect(level.getBlockState(pos.below()), axis));

        return this.defaultBlockState().setValue(AXIS, axis).setValue(COLUMN, column)
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        if (direction.getAxis() == Direction.Axis.Y) {
            Direction.Axis axis = state.getValue(AXIS);
            boolean connectsUp = direction == Direction.UP
                    ? LoopholeBlock.canConnect(neighborState, axis) && LoopholeBlock.connectsDown(neighborState)
                    : LoopholeBlock.connectsUp(state);
            boolean connectsDown = direction == Direction.DOWN
                    ? LoopholeBlock.canConnect(neighborState, axis) && LoopholeBlock.connectsUp(neighborState)
                    : LoopholeBlock.connectsDown(state);

            return state.setValue(COLUMN, LoopholeBlock.getColumnState(connectsUp, connectsDown));
        }
        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
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

    public static ColumnBlockStates getColumnState(boolean connectsUp, boolean connectsDown) {
        if (connectsUp && connectsDown)
            return ColumnBlockStates.MIDDLE;
        if (connectsUp)
            return ColumnBlockStates.BOTTOM;
        if (connectsDown)
            return ColumnBlockStates.TOP;
        return ColumnBlockStates.NONE;
    }

    public static boolean canConnect(BlockState state, Direction.Axis axis) {
        return state.getBlock() instanceof LoopholeBlock && state.getValue(AXIS) == axis;
    }

    public static boolean connectsUp(BlockState state) {
        return state.getValue(COLUMN) == ColumnBlockStates.BOTTOM || state.getValue(COLUMN) == ColumnBlockStates.MIDDLE;
    }

    public static boolean connectsDown(BlockState state) {
        return state.getValue(COLUMN) == ColumnBlockStates.TOP || state.getValue(COLUMN) == ColumnBlockStates.MIDDLE;
    }

    protected VoxelShape[] getShapes() {
        return SHAPES;
    }

    public static VoxelShape[] makeShapes(double lowerBarTop, double upperBarBottom) {
        VoxelShape pillars = Shapes.or(
                Block.box(0.0, 0.0, 0.0, 5.0, 16.0, 16.0),
                Block.box(11.0, 0.0, 0.0, 16.0, 16.0, 16.0));
        VoxelShape lowerBar = Block.box(5.0, 0.0, 0.0, 11.0, lowerBarTop, 16.0);
        VoxelShape upperBar = Block.box(5.0, upperBarBottom, 0.0, 11.0, 16.0, 16.0);
        VoxelShape[] shapes = new VoxelShape[ColumnBlockStates.values().length * 2];

        for (ColumnBlockStates column : ColumnBlockStates.values()) {
            VoxelShape shape = switch (column) {
                case TOP -> Shapes.or(pillars, upperBar);
                case MIDDLE -> pillars;
                case BOTTOM -> Shapes.or(pillars, lowerBar);
                default -> Shapes.or(pillars, lowerBar, upperBar);
            };
            shapes[column.ordinal() * 2] = shape.optimize();
            shapes[column.ordinal() * 2 + 1] = VoxelShapeUtils.rotateShape(Direction.NORTH, Direction.EAST, shape).optimize();
        }
        return shapes;
    }
}
