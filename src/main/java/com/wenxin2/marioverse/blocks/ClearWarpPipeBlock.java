package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ClearPipeNetwork;
import com.wenxin2.marioverse.utils.SubLevelCollision;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClearWarpPipeBlock extends WarpPipeBlock implements EntityBlock, SimpleWaterloggedBlock {
    private static final String PIPE_DIRECTION = "PipeDirection";
    private static final String PIPE_PREVIOUS_DIRECTION = "PipePreviousDirection";
    private static final String PIPE_ENTRY_DIRECTION = "PipeEntryDirection";
    public static final String PIPE_ROUTE_TIME = "PipeRouteTime";
    private static final String PIPE_SHRUNK = "PipeShrunk";
    private static final float SHRUNK_WIDTH_SCALE = 0.85F;
    private static final float SHRUNK_INTERIOR_CLEARANCE = 0.95F;
    private static final double PIPE_WALL_CENTERING = 0.5;
    private static final int PIPE_ROUTE_TIMEOUT = 10;
    private static final double PIPE_CENTERING = 0.4;
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

    public ClearWarpPipeBlock(@Nullable DyeColor color, Properties properties) {
        super(color, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP)
                .setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE).setValue(POWERED, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE).setValue(NORTH, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(CLOSED, ENTRANCE, FACING, POWERED, WATERLOGGED, UP, DOWN, NORTH, SOUTH, EAST, WEST);
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

                    if ((player.isCreative() && ConfigRegistry.DEBUG_SELECTION_BOX_CREATIVE.get() || ConfigRegistry.DEBUG_SELECTION_BOX.get())
                            || ((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                                || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DiggerItem
                                || player.getItemInHand(player.getUsedItemHand()).is(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)))) {
                        shape = Shapes.or(shape, PIPE_ALL);
                    }
                }

                if (player.getItemInHand(player.getUsedItemHand()).is(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES))
                    shape = Shapes.block();
            }

            if (!state.getValue(ENTRANCE) && state.getValue(UP) && state.getValue(DOWN) && state.getValue(NORTH)
                    && state.getValue(SOUTH) && state.getValue(EAST) && state.getValue(WEST)) {

                if ((player.isCreative() && ConfigRegistry.DEBUG_SELECTION_BOX_CREATIVE.get() || ConfigRegistry.DEBUG_SELECTION_BOX.get())
                        || ((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                            || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DiggerItem
                            || player.getItemInHand(player.getUsedItemHand()).is(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)))) {
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

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (ClearPipeNetwork.isOpenEntrance(state) && SubLevelCollision.getCollisionEntity(collisionContext) instanceof Mob mob
                && ClearPipeNetwork.fitsEntrance(mob.level(), pos, state, mob)) {
            Direction facing = state.getValue(FACING);

            if (facing == Direction.DOWN)
                return this.voxelShape(state.setValue(NORTH, Boolean.TRUE).setValue(SOUTH, Boolean.TRUE)
                        .setValue(EAST, Boolean.TRUE).setValue(WEST, Boolean.TRUE));

            if (facing.getAxis().isHorizontal())
                return this.voxelShape(state.setValue(UP, Boolean.TRUE)
                        .setValue(PROPERTY_BY_DIRECTION.get(facing.getClockWise()), Boolean.TRUE)
                        .setValue(PROPERTY_BY_DIRECTION.get(facing.getCounterClockWise()), Boolean.TRUE));
        }
        return this.voxelShape(state);
    }

    @Override
    public boolean hasDynamicShape() {
        return true;
    }

    @Nullable
    @Override
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        if (state.getValue(ENTRANCE) && !state.getValue(CLOSED) && state.getValue(FACING) != Direction.DOWN)
            return PathType.OPEN;
        return super.getBlockPathType(state, level, pos, mob);
    }

    @NotNull
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return Shapes.empty();
    }

    @NotNull
    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return this.voxelShape(state);
    }

    @Override
    public boolean isPathfindable(BlockState state, PathComputationType pathType) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> list, TooltipFlag options) {
        if (Screen.hasShiftDown()) {
            list.add(Component.literal(""));

            list.add(Component.translatable("block.marioverse.clear_warp_pipe.tooltip.instructions"));
            list.add(Component.translatable("block.marioverse.clear_warp_pipe.tooltip.instructions.wrench"));
            list.add(Component.translatable("block.marioverse.clear_warp_pipe.tooltip.instructions.place"));

            list.add(Component.literal(""));

            Spawner.appendHoverText(stack, list, "SpawnData");

            list.add(Component.literal(""));
        } else list.add(Component.translatable("block.marioverse.clear_warp_pipe.tooltip"));
    }

    @NotNull
    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        double entityX = player.getX();
        double entityY = player.getY();
        double entityZ = player.getZ();
        double height =  + player.getBbHeight();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (state.getValue(ENTRANCE) && (!player.isCreative() || player.getItemInHand(hand).is(TagRegistry.WARP_PIPE_CANNOT_SPAWN_ITEMS))
                && !player.getItemInHand(hand).is(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)) {
            Direction facing = state.getValue(FACING);
            boolean yPosCheck = entityY + height >= blockY && entityY - height < blockY + 0.75;

            if (facing == Direction.UP && (entityY + height >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                return ItemInteractionResult.SUCCESS;
            } else if (facing == Direction.DOWN && (entityY + height <= blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                return ItemInteractionResult.SUCCESS;
            } else if (facing == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && yPosCheck && (entityZ < blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                return ItemInteractionResult.SUCCESS;
            } else if (facing == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && yPosCheck && (entityZ > blockZ + 0.25)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                player.setSwimming(true);
                return ItemInteractionResult.SUCCESS;
            } else if (facing == Direction.EAST
                    && (entityX > blockX) && yPosCheck && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                player.setSwimming(true);
                return ItemInteractionResult.SUCCESS;
            } else if (facing == Direction.WEST
                    && (entityX < blockX) && yPosCheck && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                player.setSwimming(true);
                return ItemInteractionResult.SUCCESS;
            } else return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
        Set<RelativeMovement> flags = EnumSet.noneOf(RelativeMovement.class);
        Vec3 look = player.getViewVector(1.0F);

        double entityX = player.getX();
        double entityY = player.getY();
        double entityZ = player.getZ();
        double height = player.getBbHeight();

        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (SableProvider.isLoaded() && SableProvider.isPlotPos(level, pos)) {
            Vec3 localPos = SableProvider.toLocalVec(level, pos, player.position());
            if (localPos == null)
                return super.useWithoutItem(state, level, pos, player, hitResult);

            look = SableProvider.toLocalDirection(level, pos, look).normalize();
            entityX = localPos.x;
            entityY = localPos.y;
            entityZ = localPos.z;
        }

        if (state.hasProperty(ENTRANCE) && state.getValue(ENTRANCE) && heldItem.isEmpty()) {
            Direction facing = state.getValue(FACING);

            double horizontalLength = Math.sqrt(look.x * look.x + look.z * look.z);
            double flatX = horizontalLength > 0.0001 ? look.x / horizontalLength : 0;
            double flatZ = horizontalLength > 0.0001 ? look.z / horizontalLength : 0;

            double lookDot = flatX * facing.getStepX() + flatZ * facing.getStepZ();
            boolean facingIntoPipe = lookDot < -0.65;

            boolean withinX = entityX >= blockX && entityX < blockX + 1;
            boolean withinY = entityY + height >= blockY && entityY - height < blockY + 0.75;
            boolean withinZ = entityZ >= blockZ && entityZ < blockZ + 1;

            boolean insideFaceBounds;

            switch (facing.getAxis()) {
                case X -> insideFaceBounds = withinY && withinZ;
                case Y -> insideFaceBounds = withinX && withinZ;
                case Z -> insideFaceBounds = withinX && withinY;
                default -> insideFaceBounds = false;
            }
            boolean canEnterPipe;

            if (facing.getAxis().isHorizontal())
                canEnterPipe = facingIntoPipe;
            else if (facing == Direction.UP)
                canEnterPipe = entityY + height >= blockY - 1;
            else
                canEnterPipe = entityY + height <= blockY;

            if (insideFaceBounds && canEnterPipe) {
                Vec3 entryPos = new Vec3(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);

                if (SableProvider.isLoaded() && SableProvider.isPlotPos(level, pos)) {
                    entryPos = SableProvider.toWorldVec(level, Vec3.atCenterOf(pos));
                    if (entryPos == null)
                        return super.useWithoutItem(state, level, pos, player, hitResult);

                    player.setSwimming(true);
                    player.setPose(Pose.SWIMMING);
                    entryPos = entryPos.subtract(0.0, player.getBbHeight() * 0.5, 0.0);
                }

                if (player instanceof ServerPlayer serverPlayer)
                    serverPlayer.teleportTo((ServerLevel) level, entryPos.x, entryPos.y, entryPos.z,
                            flags, serverPlayer.getYRot(), serverPlayer.getXRot());
                else player.moveTo(entryPos.x, entryPos.y, entryPos.z);

                if (facing.getAxis().isHorizontal())
                    player.setSwimming(true);

                return InteractionResult.SUCCESS;
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
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
                .setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @NotNull
    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                return state.setValue(NORTH, state.getValue(SOUTH))
                        .setValue(EAST, state.getValue(WEST))
                        .setValue(SOUTH, state.getValue(NORTH))
                        .setValue(WEST, state.getValue(EAST))
                        .setValue(FACING, rotation.rotate(state.getValue(FACING)));
            case COUNTERCLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(EAST))
                        .setValue(EAST, state.getValue(SOUTH))
                        .setValue(SOUTH, state.getValue(WEST))
                        .setValue(WEST, state.getValue(NORTH))
                        .setValue(FACING, rotation.rotate(state.getValue(FACING)));
            case CLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(WEST))
                        .setValue(EAST, state.getValue(NORTH))
                        .setValue(SOUTH, state.getValue(EAST))
                        .setValue(WEST, state.getValue(SOUTH))
                        .setValue(FACING, rotation.rotate(state.getValue(FACING)));
            default:
                return super.rotate(state, rotation);
        }
    }

    @NotNull
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        switch (mirror) {
            case LEFT_RIGHT:
                return state.setValue(NORTH, state.getValue(SOUTH)).setValue(SOUTH, state.getValue(NORTH))
                        .setValue(FACING, mirror.mirror(state.getValue(FACING))).setValue(ENTRANCE, false);
            case FRONT_BACK:
                return state.setValue(EAST, state.getValue(WEST)).setValue(WEST, state.getValue(EAST))
                        .setValue(FACING, mirror.mirror(state.getValue(FACING))).setValue(ENTRANCE, false);
            default:
                return super.mirror(state, mirror);
        }
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

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos pos, BlockPos posNeighbor) {
        BlockState newState = state;
        BooleanProperty property = PROPERTY_BY_DIRECTION.get(direction);

        if (state.getValue(WATERLOGGED) && !state.getValue(CLOSED))
            levelAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));

        if (property != null) {
            boolean shouldConnect = this.connectsTo(neighborState);

            if (state.getValue(property) != shouldConnect)
                newState = newState.setValue(property, shouldConnect);
        }
        return this.calculateEntrance(newState, levelAccessor, pos);
    }

    @Override
    public BlockState calculateEntrance(BlockState state, LevelAccessor level, BlockPos pos) {
        BlockState newState = super.calculateEntrance(state, level, pos);
        Direction facing = newState.getValue(FACING);
        long connections = PROPERTY_BY_DIRECTION.entrySet().stream()
                .filter(entry -> newState.getValue(entry.getValue())
                        && !ClearWarpPipeBlock.isSameEntranceGroup(level, pos, entry.getKey(), facing))
                .count();

        if (newState.getValue(ENTRANCE) && connections > 1)
            return newState.setValue(ENTRANCE, false);
        return newState;
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        WarpPipeBlockEntity pipeBlockEntity = (WarpPipeBlockEntity) serverWorld.getBlockEntity(pos);

        if (pipeBlockEntity != null && pipeBlockEntity.hasWaterSpout() && state.getValue(WATERLOGGED)) {
            WaterSpoutBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBlockEntity.spoutHeight);
            serverWorld.scheduleTick(pos, this, 3);
        }

        if (pipeBlockEntity != null && pipeBlockEntity.hasBubbles() && state.getValue(FACING) == Direction.UP && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnUp(serverWorld, pos.above(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (pipeBlockEntity != null && pipeBlockEntity.hasBubbles() && state.getValue(FACING) == Direction.DOWN && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnDown(serverWorld, pos.below(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (pipeBlockEntity != null && pipeBlockEntity.hasBubbles() && state.getValue(FACING) == Direction.NORTH && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnNorth(serverWorld, pos.north(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (pipeBlockEntity != null && pipeBlockEntity.hasBubbles() && state.getValue(FACING) == Direction.SOUTH && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnSouth(serverWorld, pos.south(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (pipeBlockEntity != null && pipeBlockEntity.hasBubbles() && state.getValue(FACING) == Direction.EAST && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnEast(serverWorld, pos.east(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        } else if (pipeBlockEntity != null && pipeBlockEntity.hasBubbles() && state.getValue(FACING) == Direction.WEST && state.getValue(WATERLOGGED) && pipeBlockEntity != null) {
            PipeBubblesBlock.repeatColumnWest(serverWorld, pos.west(), state, pipeBlockEntity.bubblesDistance);
            serverWorld.scheduleTick(pos, this, 3);
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        Direction facing = state.getValue(FACING);
        boolean onSubLevel = SableProvider.isLoaded() && SableProvider.isPlotPos(world, pos);
        Vec3 localPos = entity.position();
        Vec3 pipeCenterOffset = Vec3.ZERO;

        if (onSubLevel) {
            Vec3 localCenter = SableProvider.toLocalVec(world, pos, entity.getBoundingBox().getCenter());
            localPos = SableProvider.toLocalVec(world, pos, entity.position());

            if (localCenter == null || localPos == null || !ClearWarpPipeBlock.isInsidePipe(pos, localCenter))
                return;
            pipeCenterOffset = Vec3.atCenterOf(pos).subtract(localCenter);
        }

        if (entity instanceof Mob && !ClearPipeNetwork.canEnter(world, pos, state, entity))
            return;

        if (!entity.isShiftKeyDown() && ConfigRegistry.ALLOW_FAST_TRAVEL.get() && !entity.getType().is(TagRegistry.CANNOT_QUICK_TRAVEL)) {
            if (onSubLevel)
                entity.setSwimming(true);
            else if ((facing == Direction.UP || facing == Direction.DOWN)) {
                if (state.getValue(NORTH) || state.getValue(SOUTH)
                        || state.getValue(EAST) || state.getValue(WEST)) {
                    entity.setSwimming(true);
                }
            } else entity.setSwimming(true);

            if (entity instanceof LivingEntity livingEntity)
                this.spawnTrailParticles(world, livingEntity);

            if (entity instanceof Mob mob)
                ClearWarpPipeBlock.shrinkMob(mob);

            if (entity instanceof Player player) {
                Direction moveDirection = this.getDirectionFromLook(player, world, pos, onSubLevel);
                this.movePlayerInPipe(player, moveDirection, world, pos, onSubLevel, pipeCenterOffset);
            } else this.moveEntityInPipe(entity, state, pos, localPos, onSubLevel, pipeCenterOffset);
            super.entityInside(state, world, pos, entity);
        }
    }

    protected void spawnTrailParticles(Level world, LivingEntity entity) {
        BlockPos posLegacy = entity.getOnPosLegacy();
        BlockState state = world.getBlockState(posLegacy);
        float scale = (float) entity.getAttributeValue(Attributes.SCALE);
        float widthScale = (float) entity.getAttributeValue(AttributesRegistry.WIDTH_SCALE);

        if (!state.addRunningEffects(world, posLegacy, entity)) {
            if (state.getRenderShape() != RenderShape.INVISIBLE) {
                Vec3 vec3 = entity.getDeltaMovement();
                BlockPos pos = entity.blockPosition();
                double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * scale * widthScale;
                double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * scale * widthScale;

                if (pos.getX() != posLegacy.getX())
                    x = Mth.clamp(x, posLegacy.getX(), posLegacy.getX() + 1.0);

                if (pos.getZ() != posLegacy.getZ())
                    z = Mth.clamp(z, posLegacy.getZ(), posLegacy.getZ() + 1.0);

                world.addParticle(ParticleTypes.EFFECT, x, entity.getY(), z, vec3.x * -4.0, 1.5, vec3.z * -4.0);
            }
        }
    }

    private void moveEntityInPipe(Entity entity, BlockState state, BlockPos pos, Vec3 localPos, boolean onSubLevel, Vec3 pipeCenterOffset) {
        double speed = 0.35D;
        double wallStrength = 0.08D;
        List<Direction> connections = new ArrayList<>();

        if (state.getValue(NORTH)) connections.add(Direction.NORTH);
        if (state.getValue(SOUTH)) connections.add(Direction.SOUTH);
        if (state.getValue(EAST)) connections.add(Direction.EAST);
        if (state.getValue(WEST)) connections.add(Direction.WEST);
        if (state.getValue(UP)) connections.add(Direction.UP);
        if (state.getValue(DOWN)) connections.add(Direction.DOWN);

        if (connections.isEmpty())
            return;

        CompoundTag tag = entity.getPersistentData();
        long gameTime = entity.level().getGameTime();
        boolean openEntrance = state.hasProperty(ENTRANCE) && state.getValue(ENTRANCE)
                && state.hasProperty(CLOSED) && !state.getValue(CLOSED);
        boolean continuingRoute = tag.contains(PIPE_DIRECTION) && gameTime - tag.getLong(PIPE_ROUTE_TIME) <= PIPE_ROUTE_TIMEOUT;
        Direction direction;
        Direction previousDirection = null;
        Direction entryDirection;

        if (continuingRoute) {
            direction = Direction.from3DDataValue(tag.getInt(PIPE_DIRECTION));
            if (tag.contains(PIPE_PREVIOUS_DIRECTION))
                previousDirection = Direction.from3DDataValue(tag.getInt(PIPE_PREVIOUS_DIRECTION));
            entryDirection = tag.contains(PIPE_ENTRY_DIRECTION) ? Direction.from3DDataValue(tag.getInt(PIPE_ENTRY_DIRECTION)) : direction;
        } else {
            Direction inward = openEntrance ? state.getValue(FACING).getOpposite()
                    : ClearWarpPipeBlock.getAdjacentEntranceInward(entity.level(), pos);

            if (inward != null && (openEntrance || connections.contains(inward)))
                direction = inward;
            else {
                Vec3 motion = entity.getDeltaMovement();
                if (onSubLevel)
                    motion = SableProvider.toLocalDirection(entity.level(), pos, motion);
                direction = Direction.getNearest(motion.x, motion.y, motion.z);

                if (!connections.contains(direction))
                    direction = connections.getFirst();
            }
            entryDirection = inward != null ? inward : direction;
        }

        if (openEntrance && direction.getAxis() != state.getValue(FACING).getAxis())
            direction = state.getValue(FACING).getOpposite();

        Direction nextDirection = direction;

        if (!openEntrance && !connections.contains(direction))
            nextDirection = ClearWarpPipeBlock.chooseNextDirection(entity, pos, connections, direction, previousDirection, entryDirection);
        else if (!openEntrance) {
            Direction steered = ClearPipeNetwork.steerAtJunction(entity, pos, connections, direction, nextDirection);
            if (steered != null)
                nextDirection = steered;
        }

        if (nextDirection != direction)
            previousDirection = direction;

        tag.putInt(PIPE_DIRECTION, nextDirection.get3DDataValue());
        tag.putInt(PIPE_ENTRY_DIRECTION, entryDirection.get3DDataValue());
        tag.putLong(PIPE_ROUTE_TIME, gameTime);
        if (previousDirection != null)
            tag.putInt(PIPE_PREVIOUS_DIRECTION, previousDirection.get3DDataValue());
        else tag.remove(PIPE_PREVIOUS_DIRECTION);

        Vec3 motion = entity.getDeltaMovement();
        Vec3 forwardMotion = new Vec3(nextDirection.getStepX() * speed,
                nextDirection.getStepY() * speed,
                nextDirection.getStepZ() * speed);

        Direction.Axis travelAxis = nextDirection.getAxis();
        double centerX = localPos.x - pos.getX() - 0.5;
        double centerY = localPos.y + entity.getBbHeight() * 0.5 - pos.getY() - 0.5;
        double centerZ = localPos.z - pos.getZ() - 0.5;
        double pushX = travelAxis == Direction.Axis.X ? 0.0 : Mth.clamp(-centerX * PIPE_WALL_CENTERING, -wallStrength, wallStrength);
        double pushY = travelAxis == Direction.Axis.Y ? 0.0 : Mth.clamp(-centerY * PIPE_WALL_CENTERING, -wallStrength, wallStrength);
        double pushZ = travelAxis == Direction.Axis.Z ? 0.0 : Mth.clamp(-centerZ * PIPE_WALL_CENTERING, -wallStrength, wallStrength);

        Vec3 pipeMotion = forwardMotion.add(pushX, pushY, pushZ);
        if (onSubLevel)
            pipeMotion = ClearWarpPipeBlock.toSubLevelPipeMotion(entity, pos, pipeMotion, nextDirection, pipeCenterOffset);

        entity.setDeltaMovement(motion.x * 0.75 + pipeMotion.x,
                motion.y * 0.75 + pipeMotion.y,
                motion.z * 0.75 + pipeMotion.z);
        entity.resetFallDistance();
    }

    public void movePlayerInPipe(Entity entity, Direction direction, Level level, BlockPos pos, boolean onSubLevel, Vec3 pipeCenterOffset) {
        Vec3 motion = switch (direction) {
            case NORTH -> new Vec3(0, 0, -0.75);
            case SOUTH -> new Vec3(0, 0, 0.75);
            case WEST -> new Vec3(-0.75, 0, 0);
            case EAST -> new Vec3(0.75, 0, 0);
            case UP -> new Vec3(0, 0.6, 0);
            case DOWN -> new Vec3(0, -0.3, 0);
        };

        if (onSubLevel)
            motion = ClearWarpPipeBlock.toSubLevelPipeMotion(entity, pos, motion, direction, pipeCenterOffset);

        entity.setDeltaMovement(motion);
        entity.resetFallDistance();
    }

    private Direction getDirectionFromLook(Entity entity, Level level, BlockPos pos, boolean onSubLevel) {
        Vec3 lookVec = entity.getLookAngle().normalize();
        if (onSubLevel)
            lookVec = SableProvider.toLocalDirection(level, pos, lookVec).normalize();
        Direction bestDirection = null;
        double bestDot = -1; // -1 picks the most aligned direction

        for (Direction dir : Direction.values()) {
            Vec3 pipeVec = Vec3.atLowerCornerOf(dir.getNormal()).normalize();
            double dotProduct = lookVec.dot(pipeVec);

            if (dotProduct > bestDot) {
                bestDot = dotProduct;
                bestDirection = dir;
            }
        }
        return bestDirection;
    }

    private static boolean isInsidePipe(BlockPos pos, Vec3 localPos) {
        return localPos.x >= pos.getX() && localPos.x <= pos.getX() + 1.0
                && localPos.y >= pos.getY() && localPos.y <= pos.getY() + 1.0
                && localPos.z >= pos.getZ() && localPos.z <= pos.getZ() + 1.0;
    }

    private static Vec3 toSubLevelPipeMotion(Entity entity, BlockPos pos, Vec3 localMotion, Direction direction, Vec3 pipeCenterOffset) {
        Vec3 centering = switch (direction.getAxis()) {
            case X -> new Vec3(0.0, pipeCenterOffset.y, pipeCenterOffset.z);
            case Y -> new Vec3(pipeCenterOffset.x, 0.0, pipeCenterOffset.z);
            case Z -> new Vec3(pipeCenterOffset.x, pipeCenterOffset.y, 0.0);
        };
        Vec3 worldMotion = SableProvider.toWorldDirection(entity.level(), pos, localMotion.add(centering.scale(PIPE_CENTERING)));
        return worldMotion.add(0.0, entity.getGravity(), 0.0);
    }

    private static boolean isSameEntranceGroup(LevelAccessor level, BlockPos pos, Direction direction, Direction facing) {
        if (direction.getAxis() == facing.getAxis())
            return false;

        BlockPos neighborPos = pos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);

        if (!(neighborState.getBlock() instanceof ClearWarpPipeBlock) || neighborState.getValue(FACING) != facing)
            return false;

        BlockState neighborFront = level.getBlockState(neighborPos.relative(facing));
        return !(neighborFront.is(TagRegistry.PIPE_JUNCTION_BLOCKS) || neighborFront.getBlock() instanceof WarpPipeBlock);
    }

    private static Direction chooseNextDirection(Entity entity, BlockPos pos, List<Direction> connections, Direction direction,
                                                 @Nullable Direction previousDirection, Direction entryDirection) {
        List<Set<Direction>> exclusions = new ArrayList<>();
        Set<Direction> strict = EnumSet.of(direction.getOpposite(), entryDirection.getOpposite());
        Set<Direction> relaxed = EnumSet.of(direction.getOpposite());

        if (previousDirection != null) {
            strict.add(previousDirection.getOpposite());
            relaxed.add(previousDirection.getOpposite());
        }
        exclusions.add(strict);
        exclusions.add(relaxed);
        exclusions.add(EnumSet.of(direction.getOpposite()));

        for (Set<Direction> excluded : exclusions) {
            List<Direction> possibleDirections = connections.stream().filter(connected -> !excluded.contains(connected)).toList();

            if (!possibleDirections.isEmpty()) {
                Direction steered = ClearPipeNetwork.steerToExit(entity, pos, possibleDirections);
                return steered != null ? steered : possibleDirections.get(entity.level().random.nextInt(possibleDirections.size()));
            }
        }
        return direction.getOpposite();
    }

    @Nullable
    private static Direction getAdjacentEntranceInward(Level level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            BlockState neighborState = level.getBlockState(pos.relative(direction));

            if (neighborState.getBlock() instanceof ClearWarpPipeBlock && neighborState.getValue(ENTRANCE)
                    && !neighborState.getValue(CLOSED) && neighborState.getValue(FACING) == direction)
                return direction.getOpposite();
        }
        return null;
    }

    public static boolean isShrunkPose(Entity entity, Pose pose) {
        return pose == Pose.SWIMMING && entity instanceof Mob && !(entity instanceof WaterAnimal);
    }

    public static EntityDimensions getShrunkDimensions(EntityDimensions dimensions) {
        int pipeSpan = Math.max(1, Mth.ceil(dimensions.width() - 1.0E-4F));
        float interior = (float) (pipeSpan - ClearPipeNetwork.PIPE_WALL_THICKNESS * 2.0) * SHRUNK_INTERIOR_CLEARANCE;
        float width = Math.min(dimensions.width() * SHRUNK_WIDTH_SCALE, interior);
        return EntityDimensions.scalable(width, Math.min(dimensions.height(), width));
    }

    public static void restoreShrunkMob(Mob mob) {
        CompoundTag tag = mob.getPersistentData();

        if (tag.getBoolean(PIPE_SHRUNK) && !ClearPipeNetwork.isTravelingInPipe(mob)) {
            tag.remove(PIPE_SHRUNK);
            if (mob.getPose() == Pose.SWIMMING)
                mob.setPose(Pose.STANDING);
        }
    }

    private static void shrinkMob(Mob mob) {
        if (mob.level().isClientSide || mob.getPose() == Pose.SWIMMING || mob instanceof WaterAnimal)
            return;

        mob.getPersistentData().putBoolean(PIPE_SHRUNK, true);
        mob.setPose(Pose.SWIMMING);
    }
}
