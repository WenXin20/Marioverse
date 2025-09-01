package com.wenxin2.marioverse.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.sounds.FadeInAndOutSoundInstance;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public ClearWarpPipeBlock(@Nullable DyeColor color, Properties properties) {
        super(color, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP)
                .setValue(ENTRANCE, Boolean.TRUE).setValue(CLOSED, Boolean.FALSE).setValue(POWERED, Boolean.FALSE)
                .setValue(WATERLOGGED, Boolean.FALSE).setValue(WATER_SPOUT, Boolean.FALSE)
                .setValue(UP, Boolean.FALSE).setValue(NORTH, Boolean.FALSE).setValue(SOUTH, Boolean.FALSE)
                .setValue(EAST, Boolean.FALSE).setValue(WEST, Boolean.FALSE).setValue(DOWN, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(BUBBLES, CLOSED, ENTRANCE, FACING, POWERED, WATER_SPOUT, WATERLOGGED, UP, DOWN, NORTH, SOUTH, EAST, WEST);
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
                            || player.getItemInHand(player.getUsedItemHand()).is(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)
                            || player.getItemInHand(player.getUsedItemHand()).getItem() == BlockRegistry.CLEAR_WARP_PIPE.get().asItem()))) {
                        shape = Shapes.or(shape, PIPE_ALL);
                    }
                }

                if (player.getItemInHand(player.getUsedItemHand()).is(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)
                        || player.getItemInHand(player.getUsedItemHand()).getItem() == BlockRegistry.CLEAR_WARP_PIPE.get().asItem())
                    shape = Shapes.block();
            }

            if (!state.getValue(ENTRANCE) && state.getValue(UP) && state.getValue(DOWN) && state.getValue(NORTH)
                    && state.getValue(SOUTH) && state.getValue(EAST) && state.getValue(WEST)) {

                if ((player.isCreative() && ConfigRegistry.DEBUG_SELECTION_BOX_CREATIVE.get() || ConfigRegistry.DEBUG_SELECTION_BOX.get())
                        || ((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                        || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DiggerItem
                        || player.getItemInHand(player.getUsedItemHand()).is(TagRegistry.CAN_SELECT_CLEAR_WARP_PIPES)
                        || player.getItemInHand(player.getUsedItemHand()).getItem() == BlockRegistry.CLEAR_WARP_PIPE.get().asItem()))) {
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

    @NotNull
    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());

        double entityX = player.getX();
        double entityY = player.getY();
        double entityZ = player.getZ();
        double height =  + player.getBbHeight();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (state.getValue(ENTRANCE) && heldItem.isEmpty()) {
            Direction facing = state.getValue(FACING);
            boolean yPosCheck = entityY + height >= blockY && entityY - height < blockY + 0.75;
            
            if (facing == Direction.UP && (entityY + height >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                return InteractionResult.SUCCESS;
            } else if (facing == Direction.DOWN && (entityY + height <= blockY)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                return InteractionResult.SUCCESS;
            } else if (facing == Direction.NORTH
                    && (entityX < blockX + 1 && entityX > blockX) && yPosCheck && (entityZ < blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                return InteractionResult.SUCCESS;
            } else if (facing == Direction.SOUTH
                    && (entityX < blockX + 1 && entityX > blockX) && yPosCheck && (entityZ > blockZ + 0.25)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                player.setSwimming(true);
                return InteractionResult.SUCCESS;
            } else if (facing == Direction.EAST
                    && (entityX > blockX) && yPosCheck && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                player.setSwimming(true);
                return InteractionResult.SUCCESS;
            } else if (facing == Direction.WEST
                    && (entityX < blockX) && yPosCheck && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                player.moveTo(pos.getX() + 0.5, pos.getY() + 0.25, pos.getZ() + 0.5);
                player.setSwimming(true);
                return InteractionResult.SUCCESS;
            } else return super.useWithoutItem(state, world, pos, player, hitResult);
        } else return super.useWithoutItem(state, world, pos, player, hitResult);
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

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        switch (rotation) {
            case CLOCKWISE_180:
                return state.setValue(NORTH, state.getValue(SOUTH))
                        .setValue(EAST, state.getValue(WEST))
                        .setValue(SOUTH, state.getValue(NORTH))
                        .setValue(WEST, state.getValue(EAST))
                        .setValue(FACING, rotation.rotate(state.getValue(FACING)))
                        .setValue(ENTRANCE, false);
            case COUNTERCLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(EAST))
                        .setValue(EAST, state.getValue(SOUTH))
                        .setValue(SOUTH, state.getValue(WEST))
                        .setValue(WEST, state.getValue(NORTH))
                        .setValue(FACING, rotation.rotate(state.getValue(FACING)))
                        .setValue(ENTRANCE, false);
            case CLOCKWISE_90:
                return state.setValue(NORTH, state.getValue(WEST))
                        .setValue(EAST, state.getValue(NORTH))
                        .setValue(SOUTH, state.getValue(EAST))
                        .setValue(WEST, state.getValue(SOUTH))
                        .setValue(FACING, rotation.rotate(state.getValue(FACING)))
                        .setValue(ENTRANCE, false);
            default:
                return super.rotate(state, rotation);
        }
    }

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

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos posNeighbor) {
        Direction facing = state.getValue(FACING);
        BlockPos posRelative = pos.relative(facing);

        if (state.getValue(WATERLOGGED) && !state.getValue(CLOSED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return state.setValue(ENTRANCE, worldAccessor.getBlockState(posRelative).getBlock() != this)
                .setValue(PROPERTY_BY_DIRECTION.get(direction), this.connectsTo(neighborState));
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
        Direction facing = state.getValue(FACING);

        if (!entity.isShiftKeyDown() && ConfigRegistry.ALLOW_FAST_TRAVEL.get() && !entity.getType().is(TagRegistry.CANNOT_QUICK_TRAVEL)) {
            if ((facing == Direction.UP || facing == Direction.DOWN)) {
                if (state.getValue(NORTH) || state.getValue(SOUTH)
                        || state.getValue(EAST) || state.getValue(WEST)) {
                    entity.setSwimming(true);
                }
            } else entity.setSwimming(true);

            if (entity instanceof LivingEntity livingEntity)
                this.spawnTrailParticles(world, livingEntity);

            if (entity instanceof Player player) {
                Direction moveDirection = this.getDirectionFromLook(player);
                movePlayerInPipe(player, moveDirection);
            } else moveEntityInPipe(entity);
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

    private void moveEntityInPipe(Entity entity) {
        Vec3 lookVec = entity.getLookAngle();
        Vec3 moveVec = entity.getDeltaMovement();
        double d0 = Math.min(1.5D, moveVec.y + 0.1D);
        double speed = 1.25D;
        double verticalSpeed = 1.15D;

        if (entity instanceof LivingEntity && !entity.isShiftKeyDown()) {
            Vec3 movement = new Vec3(lookVec.x * speed, moveVec.y * verticalSpeed, lookVec.z * speed);
            entity.setDeltaMovement(movement.x, movement.y, movement.z);

            if (moveVec.y > 0 || moveVec.y < 0)
                entity.setDeltaMovement(moveVec.x, d0, moveVec.z);
        } else if (!entity.isShiftKeyDown()) {
            Vec3 movement = new Vec3(moveVec.x * speed, moveVec.y * verticalSpeed, moveVec.z * speed);
            entity.setDeltaMovement(movement.x, movement.y, movement.z);
            if (moveVec.y > 0 || moveVec.y < 0)
                entity.setDeltaMovement(moveVec.x, d0, moveVec.z);
        }
        entity.resetFallDistance();
    }

    public void movePlayerInPipe(Entity entity, Direction direction) {
        Vec3 motion = switch (direction) {
            case NORTH -> new Vec3(0, 0, -0.75);
            case SOUTH -> new Vec3(0, 0, 0.75);
            case WEST -> new Vec3(-0.75, 0, 0);
            case EAST -> new Vec3(0.75, 0, 0);
            case UP -> new Vec3(0, 0.6, 0);
            case DOWN -> new Vec3(0, -0.25, 0);
        };

        entity.setDeltaMovement(motion);
        entity.resetFallDistance();
    }

    private Direction getDirectionFromLook(Entity entity) {
        Vec3 lookVec = entity.getLookAngle().normalize();
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
}
