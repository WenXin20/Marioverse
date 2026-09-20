package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PicketFenceGateBlock extends FenceGateBlock implements SimpleWaterloggedBlock, DyeColumnBlock {
    public static final MapCodec<FenceGateBlock> CODEC = RecordCodecBuilder
            .mapCodec(instance -> instance.group(WoodType.CODEC.fieldOf("wood_type")
                    .forGetter(block -> ((PicketFenceGateBlock) block).woodType), propertiesCodec())
                    .apply(instance, PicketFenceGateBlock::new));
    public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
    public static final BooleanProperty TALL = BlockStatePropertyRegistry.TALL;
    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final int MAX_CONNECTED_GATES = 64;

    private static final VoxelShape POST_LEFT = Block.box(0, 0, 6, 3, 16, 10);
    private static final VoxelShape POST_RIGHT = Block.box(13, 0, 6, 16, 16, 10);
    private static final VoxelShape POST_LEFT_COLLISION = Block.box(0, 0, 6, 2, 24, 10);
    private static final VoxelShape POST_RIGHT_COLLISION = Block.box(14, 0, 6, 16, 24, 10);

    private static final VoxelShape SHAPE_CLOSED = Shapes
            .or(Block.box(5, 14, 7, 11, 15, 9),
                    Block.box(3, 0, 7, 13, 14, 9),
                    POST_LEFT, POST_RIGHT).optimize();
    private static final VoxelShape SHAPE_TALL = Shapes
            .or(Block.box(3, 0, 7, 13, 16, 9),
                    POST_LEFT, POST_RIGHT).optimize();

    private static final VoxelShape SHAPE_EXTENDED = Shapes
            .or(Block.box(0, 14, 7, 10, 15, 9),
                    Block.box(0, 0, 7, 13, 14, 9),
                    POST_RIGHT).optimize();
    private static final VoxelShape SHAPE_TALL_EXTENDED = Shapes
            .or(Block.box(0, 0, 7, 13, 16, 9),
                    POST_RIGHT).optimize();

    private static final VoxelShape SHAPE_OPEN = Shapes
            .or(Block.box(11, 0, -2, 13, 14, 8),
                    Block.box(11, 14, 0, 13, 15, 6),
                    POST_LEFT, POST_RIGHT).optimize();
    private static final VoxelShape SHAPE_TALL_OPEN = Shapes
            .or(Block.box(11, 0, -2, 13, 16, 8),
                    POST_LEFT, POST_RIGHT).optimize();
    private static final VoxelShape SHAPE_OPEN_EXTENDED = Shapes
            .or(Block.box(11, 0, -5, 13, 14, 8),
                    Block.box(11, 14, -5, 13, 15, 5),
                    POST_RIGHT).optimize();
    private static final VoxelShape SHAPE_TALL_OPEN_EXTENDED = Shapes
            .or(Block.box(11, 0, -5, 13, 16, 8),
                    POST_RIGHT).optimize();

    private static final VoxelShape COLLISION_CLOSED = Shapes
            .or(Block.box(3, 0, 7, 13, 24, 9),
                    POST_LEFT_COLLISION, POST_RIGHT_COLLISION).optimize();
    private static final VoxelShape COLLISION_CLOSED_EXTENDED = Shapes
            .or(Block.box(0, 0, 7, 13, 24, 9),
                    POST_RIGHT_COLLISION).optimize();
    private static final VoxelShape COLLISION_OPEN = Shapes
            .or(POST_LEFT_COLLISION, POST_RIGHT_COLLISION).optimize();
    private static final VoxelShape COLLISION_OPEN_EXTENDED = POST_RIGHT_COLLISION;

    private static final VoxelShape OCCLUSION_CLOSED = Shapes
            .or(Block.box(3, 0, 7, 13, 14, 9),
                    POST_LEFT, POST_RIGHT).optimize();
    private static final VoxelShape OCCLUSION_CLOSED_EXTENDED = Shapes
            .or(Block.box(0, 0, 7, 13, 14, 9),
                    POST_RIGHT).optimize();
    private static final VoxelShape OCCLUSION_OPEN = Shapes
            .or(POST_LEFT, POST_RIGHT).optimize();
    private static final VoxelShape OCCLUSION_OPEN_EXTENDED = POST_RIGHT;

    private final WoodType woodType;

    public PicketFenceGateBlock(WoodType woodType, BlockBehaviour.Properties properties) {
        super(woodType, properties);
        this.woodType = woodType;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(HINGE, DoorHingeSide.RIGHT)
                .setValue(TALL, false)
                .setValue(EXTENDED, false)
                .setValue(WATERLOGGED, false));
    }

    @NotNull
    @Override
    public MapCodec<FenceGateBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HINGE, TALL, EXTENDED, WATERLOGGED);
    }

    @NotNull
    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean tall = state.getValue(TALL);
        boolean extended = state.getValue(EXTENDED);

        VoxelShape base = state.getValue(OPEN)
                ? (extended ? (tall ? SHAPE_TALL_OPEN_EXTENDED : SHAPE_OPEN_EXTENDED) : (tall ? SHAPE_TALL_OPEN : SHAPE_OPEN))
                : (extended ? (tall ? SHAPE_TALL_EXTENDED : SHAPE_EXTENDED) : (tall ? SHAPE_TALL : SHAPE_CLOSED));
        return this.orientShape(state, base);
    }

    @NotNull
    @Override
    protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return this.getShape(state, level, pos, CollisionContext.empty());
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean extended = state.getValue(EXTENDED);

        VoxelShape base = state.getValue(OPEN)
                ? (extended ? COLLISION_OPEN_EXTENDED : COLLISION_OPEN)
                : (extended ? COLLISION_CLOSED_EXTENDED : COLLISION_CLOSED);
        return this.orientShape(state, base);
    }

    @NotNull
    @Override
    protected VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
        boolean extended = state.getValue(EXTENDED);

        VoxelShape base = state.getValue(OPEN)
                ? (extended ? OCCLUSION_OPEN_EXTENDED : OCCLUSION_OPEN)
                : (extended ? OCCLUSION_CLOSED_EXTENDED : OCCLUSION_CLOSED);
        return this.orientShape(state, base);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction facing = state.getValue(FACING);
        DoorHingeSide hinge = this.getHinge(context, facing);
        Direction latch = this.latchSide(facing, hinge);

        state = state.setValue(HINGE, hinge);
        state = state.setValue(EXTENDED, this.isPartner(level.getBlockState(pos.relative(latch)), facing, latch)
                || this.isExtendedBelow(level.getBlockState(pos.below()), facing, latch));
        state = state.setValue(TALL, this.isTallWith(level.getBlockState(pos.above()), facing));
        return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    @NotNull
    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        if (direction == Direction.UP)
            return state.setValue(TALL, this.isTallWith(neighborState, state.getValue(FACING)));

        state = super.updateShape(state, direction, neighborState, level, pos, neighborPos);

        Direction facing = state.getValue(FACING);
        Direction latch = this.latchSide(facing, state.getValue(HINGE));
        if (direction == latch || direction == Direction.DOWN) {
            BlockState latchState = direction == latch ? neighborState : level.getBlockState(pos.relative(latch));
            BlockState belowState = direction == Direction.DOWN ? neighborState : level.getBlockState(pos.below());
            state = state.setValue(EXTENDED, this.isPartner(latchState, facing, latch)
                    || this.isExtendedBelow(belowState, facing, latch));
        }
        return state;
    }

    @NotNull
    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide)
            return;

        boolean powered = level.hasNeighborSignal(pos);
        if (state.getValue(POWERED) == powered)
            return;

        boolean wasOpen = state.getValue(OPEN);
        level.setBlock(pos, state.setValue(POWERED, powered).setValue(OPEN, powered), Block.UPDATE_CLIENTS);

        for (BlockPos gatePos : this.findConnectedGates(level, pos)) {
            if (gatePos.equals(pos))
                continue;

            BlockState gateState = level.getBlockState(gatePos);
            if (gateState.getValue(OPEN) != powered && (powered || !gateState.getValue(POWERED)))
                level.setBlock(gatePos, gateState.setValue(OPEN, powered), Block.UPDATE_CLIENTS);
        }

        if (wasOpen != powered) {
            level.playSound(null, pos, powered ? this.openSound : this.closeSound, SoundSource.BLOCKS,
                    1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
            level.gameEvent(null, powered ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        }
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        boolean open = !state.getValue(OPEN);
        Direction playerDirection = player.getDirection();

        for (BlockPos gatePos : this.findConnectedGates(level, pos)) {
            BlockState gateState = level.getBlockState(gatePos).setValue(OPEN, open);
            if (open && gateState.getValue(FACING) == playerDirection.getOpposite()) {
                DoorHingeSide flippedHinge = gateState.getValue(HINGE) == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT;
                gateState = gateState.setValue(FACING, playerDirection).setValue(HINGE, flippedHinge);
            }
            level.setBlock(gatePos, gateState, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        }

        level.playSound(player, pos, open ? this.openSound : this.closeSound, SoundSource.BLOCKS,
                1.0F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        this.tickDye(level, pos, state);
    }

    @NotNull
    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        return this.useDyeItem(stack, state, level, pos, player, hitResult);
    }

    @Override
    public Block getColoredBlock(DyeColor color) {
        return BlockRegistry.PICKET_FENCE_GATES.get(color).get();
    }

    @Override
    public int getPaintRange() {
        return ConfigRegistry.PICKET_FENCE_GATE_PAINT_RANGE.get();
    }

    @Override
    public int getPaintRate() {
        return ConfigRegistry.PICKET_FENCE_GATE_PAINT_RATE.get();
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag options) {
        super.appendHoverText(stack, tooltipContext, list, options);

        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable("block.marioverse.picket_fence.tooltip.instructions"));
            list.add(Component.translatable("block.marioverse.picket_fence.tooltip.instructions.dye"));
            list.add(Component.translatable("block.marioverse.picket_fence.tooltip.instructions.dye_one"));

            list.add(Component.literal(""));
        } else list.add(Component.translatable("block.marioverse.picket_fence.tooltip"));
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return state.is(TagRegistry.FLAMMABLE_PICKET_FENCE_GATES);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

    private VoxelShape orientShape(BlockState state, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};

        if (state.getValue(HINGE) == DoorHingeSide.LEFT) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxX, minY, minZ, 1 - minX, maxY, maxZ)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        int rotations = (state.getValue(FACING).get2DDataValue() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < rotations; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    private List<BlockPos> findConnectedGates(Level level, BlockPos origin) {
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        visited.add(origin);
        queue.add(origin);

        while (!queue.isEmpty() && visited.size() < MAX_CONNECTED_GATES) {
            BlockPos current = queue.poll();
            BlockState currentState = level.getBlockState(current);
            Direction facing = currentState.getValue(FACING);
            Direction latch = this.latchSide(facing, currentState.getValue(HINGE));

            for (Direction direction : new Direction[]{Direction.UP, Direction.DOWN, latch}) {
                BlockPos neighborPos = current.relative(direction);
                BlockState neighborState = level.getBlockState(neighborPos);
                if (visited.contains(neighborPos))
                    continue;

                boolean connected = direction.getAxis().isVertical()
                        ? this.isStackedGate(neighborState, facing, latch)
                        : this.isPartner(neighborState, facing, latch);
                if (!connected)
                    continue;

                visited.add(neighborPos);
                queue.add(neighborPos);
            }
        }
        return new ArrayList<>(visited);
    }

    private DoorHingeSide getHinge(BlockPlaceContext context, Direction facing) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        Direction left = facing.getCounterClockWise();
        Direction right = facing.getClockWise();
        boolean partnerOnLeft = this.isPartner(level.getBlockState(pos.relative(left)), facing, left);
        boolean partnerOnRight = this.isPartner(level.getBlockState(pos.relative(right)), facing, right);
        if (partnerOnLeft && !partnerOnRight)
            return DoorHingeSide.RIGHT;
        if (partnerOnRight && !partnerOnLeft)
            return DoorHingeSide.LEFT;

        int stepX = facing.getStepX();
        int stepZ = facing.getStepZ();
        Vec3 click = context.getClickLocation();
        double x = click.x - pos.getX();
        double z = click.z - pos.getZ();
        return (stepX >= 0 || !(z < 0.5)) && (stepX <= 0 || !(z > 0.5))
                && (stepZ >= 0 || !(x > 0.5)) && (stepZ <= 0 || !(x < 0.5))
                ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
    }

    private Direction latchSide(Direction facing, DoorHingeSide hinge) {
        return hinge == DoorHingeSide.RIGHT ? facing.getCounterClockWise() : facing.getClockWise();
    }

    private boolean isPartner(BlockState neighbor, Direction facing, Direction directionToNeighbor) {
        if (!(neighbor.getBlock() instanceof PicketFenceGateBlock) || neighbor.getValue(FACING).getAxis() != facing.getAxis())
            return false;
        return this.latchSide(neighbor.getValue(FACING), neighbor.getValue(HINGE)) == directionToNeighbor.getOpposite();
    }

    private boolean isTallWith(BlockState above, Direction facing) {
        return above.getBlock() instanceof PicketFenceGateBlock && above.getValue(FACING).getAxis() == facing.getAxis();
    }

    private boolean isExtendedBelow(BlockState below, Direction facing, Direction latch) {
        return this.isStackedGate(below, facing, latch) && below.getValue(EXTENDED);
    }

    private boolean isStackedGate(BlockState neighbor, Direction facing, Direction latch) {
        return neighbor.getBlock() instanceof PicketFenceGateBlock
                && neighbor.getValue(FACING).getAxis() == facing.getAxis()
                && this.latchSide(neighbor.getValue(FACING), neighbor.getValue(HINGE)) == latch;
    }
}
