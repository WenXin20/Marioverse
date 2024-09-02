package com.wenxin2.warp_pipes.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.init.Config;
import com.wenxin2.warp_pipes.init.ModRegistry;
import com.wenxin2.warp_pipes.init.ModTags;
import com.wenxin2.warp_pipes.items.LinkerItem;
import java.util.Collection;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class ClearWarpPipeBlock extends WarpPipeBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty ENTRANCE = WarpPipeBlock.ENTRANCE;
    public static final BooleanProperty CLOSED = WarpPipeBlock.CLOSED;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), enumMap -> {
        enumMap.put(Direction.UP, UP);
        enumMap.put(Direction.DOWN, DOWN);
        enumMap.put(Direction.NORTH, NORTH);
        enumMap.put(Direction.EAST, EAST);
        enumMap.put(Direction.SOUTH, SOUTH);
        enumMap.put(Direction.WEST, WEST);
    }));
    
    public static final VoxelShape PIPE_UP = Shapes.or(
            Block.box(0, 13, 0, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_NORTH = Shapes.or(
            Block.box(0, 0, 0, 16, 16, 3)).optimize();
    public static final VoxelShape PIPE_SOUTH = Shapes.or(
            Block.box(0, 0, 13, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_EAST = Shapes.or(
            Block.box(13, 0, 0, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_WEST = Shapes.or(
            Block.box(0, 0, 0, 3, 16, 16)).optimize();
    public static final VoxelShape PIPE_DOWN = Shapes.or(
            Block.box(0, 0, 0, 16, 3, 16)).optimize();
    public static final VoxelShape PIPE_FACING_UP = Shapes.or(
            Block.box(0, 13, 0, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_FACING_NORTH = Shapes.or(
            Block.box(0, 0, 0, 16, 16, 3)).optimize();
    public static final VoxelShape PIPE_FACING_SOUTH = Shapes.or(
            Block.box(0, 0, 13, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_FACING_EAST = Shapes.or(
            Block.box(13, 0, 0, 16, 16, 16)).optimize();
    public static final VoxelShape PIPE_FACING_WEST = Shapes.or(
            Block.box(0, 0, 0, 3, 16, 16)).optimize();
    public static final VoxelShape PIPE_FACING_DOWN = Shapes.or(
            Block.box(0, 0, 0, 16, 3, 16)).optimize();
    public static final VoxelShape PIPE_ALL = Shapes.or(
            Block.box(4, 4, 4, 12, 12, 12)).optimize();

    public ClearWarpPipeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP)
                .setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE).setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE).setValue(NORTH, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(DOWN, Boolean.FALSE)
                .setValue(WATER_SPOUT, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(BUBBLES, CLOSED, ENTRANCE, FACING, WATER_SPOUT, WATERLOGGED, UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    public VoxelShape voxelShape(BlockState state) {
        VoxelShape shape = Shapes.empty();
        VoxelShape shapeDown = Shapes.empty();

        if (state.getValue(FACING) == Direction.DOWN && state.getValue(ENTRANCE) && !state.getValue(CLOSED)) {
            if (!state.getValue(DOWN) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.DOWN))) {
                shapeDown = Shapes.or(shapeDown, PIPE_FACING_DOWN);
            }
            if (!state.getValue(UP)) {
                shapeDown = Shapes.or(shapeDown, PIPE_FACING_UP);
            }
            if (!state.getValue(NORTH)) {
                shapeDown = Shapes.or(shapeDown, PIPE_FACING_NORTH);
            }
            if (!state.getValue(EAST)) {
                shapeDown = Shapes.or(shapeDown, PIPE_FACING_EAST);
            }
            if (!state.getValue(SOUTH)) {
                shapeDown = Shapes.or(shapeDown, PIPE_FACING_SOUTH);
            }
            if (!state.getValue(WEST)) {
                shapeDown = Shapes.or(shapeDown, PIPE_FACING_WEST);
            }
            else {
                Shapes.box(2, 2, 2, 14, 14, 14);
            }
            return shapeDown.optimize();
        }

        // Combine shapes based on the directional block states
        if (!(state.getValue(FACING) == Direction.DOWN && state.getValue(ENTRANCE) && !state.getValue(CLOSED))) {
            if (!state.getValue(DOWN) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.DOWN))) {
                shape = Shapes.or(shape, PIPE_DOWN);
            }
            if (!state.getValue(UP) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.UP))) {
                shape = Shapes.or(shape, PIPE_UP);
            }
            if (!state.getValue(NORTH) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.NORTH))) {
                shape = Shapes.or(shape, PIPE_NORTH);
            }
            if (!state.getValue(EAST) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.EAST))) {
                shape = Shapes.or(shape, PIPE_EAST);
            }
            if (!state.getValue(SOUTH) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.SOUTH))) {
                shape = Shapes.or(shape, PIPE_SOUTH);
            }
            if (!state.getValue(WEST) && !(state.getValue(ENTRANCE) && (state.getValue(FACING) == Direction.WEST))) {
                shape = Shapes.or(shape, PIPE_WEST);
            }
        }

        if (state.getValue(ENTRANCE)) {
            if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.UP) {
                shape = Shapes.or(shape, PIPE_UP);
            }
            if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.DOWN) {
                shape = Shapes.or(shape, PIPE_DOWN);
            }
            if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.NORTH) {
                shape = Shapes.or(shape, PIPE_NORTH);
            }
            if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.SOUTH) {
                shape = Shapes.or(shape, PIPE_SOUTH);
            }
            if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.EAST) {
                shape = Shapes.or(shape, PIPE_EAST);
            }
            if (state.getValue(CLOSED) && state.getValue(FACING) == Direction.WEST) {
                shape = Shapes.or(shape, PIPE_WEST);
            }
        }
        return shape.optimize();
    }

    public VoxelShape noCollisionShape(BlockState state, CollisionContext context) {
        VoxelShape shape = Shapes.box(8, 8, 8, 8.00001, 8.00001, 8.00001);

        if (context instanceof EntityCollisionContext && ((EntityCollisionContext)context).getEntity() instanceof Player player) {
            if (!state.getValue(CLOSED)) {
                if ((state.getValue(UP) && state.getValue(NORTH) && state.getValue(SOUTH) &&
                        state.getValue(EAST) && state.getValue(WEST) && state.getValue(ENTRANCE) && state.getValue(FACING) == Direction.DOWN) ||
                        (state.getValue(DOWN) && state.getValue(NORTH) && state.getValue(SOUTH) && state.getValue(EAST) && state.getValue(WEST) &&
                                state.getValue(ENTRANCE) && state.getValue(FACING) == Direction.UP) ||
                        (state.getValue(UP) && state.getValue(DOWN) && state.getValue(SOUTH) && state.getValue(EAST) && state.getValue(WEST) &&
                                state.getValue(ENTRANCE) && state.getValue(FACING) == Direction.NORTH) ||
                        (state.getValue(UP) && state.getValue(DOWN) && state.getValue(NORTH) && state.getValue(EAST) && state.getValue(WEST) &&
                                state.getValue(ENTRANCE) && state.getValue(FACING) == Direction.SOUTH) ||
                        (state.getValue(UP) && state.getValue(DOWN) && state.getValue(NORTH) && state.getValue(SOUTH) && state.getValue(WEST) &&
                                state.getValue(ENTRANCE) && state.getValue(FACING) == Direction.EAST) ||
                        (state.getValue(UP) && state.getValue(DOWN) && state.getValue(NORTH) && state.getValue(SOUTH) && state.getValue(EAST) &&
                                state.getValue(ENTRANCE) && state.getValue(FACING) == Direction.WEST)) {

                    if ((player.isCreative() && Config.DEBUG_SELECTION_BOX_CREATIVE.get() || Config.DEBUG_SELECTION_BOX.get())
                            || ((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                            || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof LinkerItem
                            || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DebugStickItem
                            || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DiggerItem
                            || player.getItemInHand(player.getUsedItemHand()).getItem() == ModRegistry.CLEAR_WARP_PIPE.get().asItem()))) {
                        shape = Shapes.or(shape, PIPE_ALL);
                    }
                }
            }

            if (!state.getValue(ENTRANCE) && state.getValue(UP) && state.getValue(DOWN) && state.getValue(NORTH)
                    && state.getValue(SOUTH) && state.getValue(EAST) && state.getValue(WEST)) {

                if ((player.isCreative() && Config.DEBUG_SELECTION_BOX_CREATIVE.get() || Config.DEBUG_SELECTION_BOX.get())
                        || ((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                        || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof LinkerItem
                        || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DebugStickItem
                        || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DiggerItem
                        || player.getItemInHand(player.getUsedItemHand()).getItem() == ModRegistry.CLEAR_WARP_PIPE.get().asItem()))) {
                    shape = Shapes.or(shape, PIPE_ALL);
                }
            }
        }
        return shape.optimize();
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.or(this.voxelShape(state), this.noCollisionShape(state, context));
        return shape.optimize();
    }


    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return this.voxelShape(state);
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return this.voxelShape(state);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType pathType) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());
        Direction direction = placeContext.getClickedFace();
        BlockGetter blockGetter = placeContext.getLevel();
        BlockPos pos = placeContext.getClickedPos();

        BlockPos posAbove = pos.above();
        BlockPos posBelow = pos.below();
        BlockPos posNorth = pos.north();
        BlockPos posSouth = pos.south();
        BlockPos posEast = pos.east();
        BlockPos posWest = pos.west();

        BlockState stateAbove = blockGetter.getBlockState(posAbove);
        BlockState stateBelow = blockGetter.getBlockState(posBelow);
        BlockState stateNorth = blockGetter.getBlockState(posNorth);
        BlockState stateSouth = blockGetter.getBlockState(posSouth);
        BlockState stateEast = blockGetter.getBlockState(posEast);
        BlockState stateWest = blockGetter.getBlockState(posWest);

        return this.defaultBlockState().setValue(FACING, direction)
                .setValue(UP, this.connectsTo(stateAbove))
                .setValue(DOWN, this.connectsTo(stateBelow))
                .setValue(NORTH, this.connectsTo(stateNorth))
                .setValue(SOUTH, this.connectsTo(stateSouth))
                .setValue(EAST, this.connectsTo(stateEast))
                .setValue(WEST, this.connectsTo(stateWest))
                .setValue(CLOSED, placeContext.getLevel().hasNeighborSignal(placeContext.getClickedPos()))
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public boolean connectsTo(BlockState state) {
        Block block = state.getBlock();
        return block instanceof ClearWarpPipeBlock;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return true;
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        if (state.getValue(CLOSED) && (state.getValue(UP) || state.getValue(DOWN) || state.getValue(NORTH) || state.getValue(SOUTH)
                || state.getValue(EAST) || state.getValue(WEST))) {
            return neighborState.is(this) && neighborState.getValue(CLOSED) || super.skipRendering(state, neighborState, direction);
        }
        return false;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos pos2) {
        Block blockAbove = worldAccessor.getBlockState(pos.above()).getBlock();
        Block blockBelow = worldAccessor.getBlockState(pos.below()).getBlock();
        Block blockNorth = worldAccessor.getBlockState(pos.north()).getBlock();
        Block blockSouth = worldAccessor.getBlockState(pos.south()).getBlock();
        Block blockEast = worldAccessor.getBlockState(pos.east()).getBlock();
        Block blockWest = worldAccessor.getBlockState(pos.west()).getBlock();

        boolean facingUp = state.getValue(FACING) == Direction.UP;
        boolean facingDown = state.getValue(FACING) == Direction.DOWN;
        boolean facingNorth = state.getValue(FACING) == Direction.NORTH;
        boolean facingSouth = state.getValue(FACING) == Direction.SOUTH;
        boolean facingEast = state.getValue(FACING) == Direction.EAST;
        boolean facingWest = state.getValue(FACING) == Direction.WEST;

        if (state.getValue(WATERLOGGED) && !state.getValue(CLOSED)) {
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
        }

        if (facingUp) {
            if (blockAbove instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingDown) {
            if (blockBelow instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingNorth) {
            if (blockNorth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingSouth) {
            if (blockSouth instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingEast) {
            if (blockEast instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
            return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
        }

        if (facingWest) {
            if (blockWest instanceof WarpPipeBlock) {
                return state.setValue(ENTRANCE, Boolean.FALSE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
            }
        }
        return state.setValue(ENTRANCE, Boolean.TRUE).setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        WarpPipeBlockEntity pipeBlockEntity = (WarpPipeBlockEntity) serverWorld.getBlockEntity(pos);

        if (state.getValue(WATER_SPOUT) && pipeBlockEntity != null && state.getValue(WATERLOGGED)) {
            WaterSpoutBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBlockEntity.spoutHeight);
            serverWorld.scheduleTick(pos, this, 3);
        }

        if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.UP && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.DOWN && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnDown(serverWorld, pos.below(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.NORTH && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnNorth(serverWorld, pos.north(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.SOUTH && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnSouth(serverWorld, pos.south(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.EAST && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnEast(serverWorld, pos.east(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (state.getValue(BUBBLES) && state.getValue(FACING) == Direction.WEST && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnWest(serverWorld, pos.west(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        RandomSource random = world.getRandom();
        Vec3 moveVec = entity.getDeltaMovement();

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (!entity.isShiftKeyDown() && Config.ALLOW_FAST_TRAVEL.get() && !entity.getType().is(ModTags.QUICK_TRAVEL_BlACKLIST))
            entity.setSwimming(true);

        if ((entityY < blockY + 0.98 && entityY > blockY + 0.02)
                && (entityX < blockX + 0.98 && entityX > blockX + 0.02)
                && (entityZ < blockZ + 0.98 && entityZ > blockZ + 0.02)
                && !entity.isShiftKeyDown() && Config.ALLOW_FAST_TRAVEL.get()
                && !entity.getType().is(ModTags.QUICK_TRAVEL_BlACKLIST)) {
            this.moveEntityInPipe(entity);

            if (!world.isClientSide) {
                if (moveVec.x > 0 || moveVec.x < 0 || moveVec.y > 0 || moveVec.y < 0 || moveVec.z > 0 || moveVec.z < 0) {
                    if (random.nextInt(10) == 0) {
                        this.spawnParticles(entity);
                    }
                }
            }
        }
        super.entityInside(state, world, pos, entity);
    }

    public void moveEntityInPipe(Entity entity) {
        Vec3 lookVec = entity.getLookAngle();
        Vec3 moveVec = entity.getDeltaMovement();
        double d0 = Math.min(1.5D, moveVec.y + 0.1D);
        double speed = 1.25D;
        double verticalSpeed = 1.15D;

        if (entity instanceof LivingEntity && !entity.isShiftKeyDown()) {
            Vec3 movement = new Vec3(lookVec.x * speed, moveVec.y * verticalSpeed, lookVec.z * speed);
            entity.setDeltaMovement(movement.x, movement.y, movement.z);

            if (moveVec.y > 0 || moveVec.y < 0) {
                entity.setDeltaMovement(moveVec.x, d0, moveVec.z);
            }
        } else if (!entity.isShiftKeyDown()) {

            Vec3 movement = new Vec3(moveVec.x * speed, moveVec.y * verticalSpeed, moveVec.z * speed);
            entity.setDeltaMovement(movement.x, movement.y, movement.z);

            if (moveVec.y > 0 || moveVec.y < 0) {
                entity.setDeltaMovement(moveVec.x, d0, moveVec.z);
            }
        }
        entity.resetFallDistance();
    }

    public void spawnParticles(Entity entity) {
        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();

        Collection<ServerPlayer> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
        for (ServerPlayer player : players) {
            for (int i = 0; i < 2; i++) {
                player.connection.send(new ClientboundLevelParticlesPacket(
                        ParticleTypes.EFFECT, false,
                        entityX, entityY, entityZ,
                        0.25F, 0.15F, 0.25F,
                        0, 2
                ));
            }
        }
    }
}
