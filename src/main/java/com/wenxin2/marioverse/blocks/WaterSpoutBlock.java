package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import com.wenxin2.marioverse.items.PlasticBucketItem;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.SubLevelCollision;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterSpoutBlock extends Block implements BucketPickup {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final int MAX_CONNECTED_SPOUTS = 256;
    public static final int MAX_COLUMN_LENGTH = 256;
    private static final ThreadLocal<Set<BlockPos>> CACHED_GROUP = new ThreadLocal<>();
    private static final ThreadLocal<Direction> CACHED_FACING = new ThreadLocal<>();
    private static final ThreadLocal<Map<BlockPos, Integer>> CACHED_PARTITION = new ThreadLocal<>();
    private final Map<BlockState, VoxelShape> shapes;

    public WaterSpoutBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP)
                .setValue(TOP, Boolean.FALSE).setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE));
        this.shapes = this.getShapeForEachState(WaterSpoutBlock::createShape);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, TOP, NORTH, EAST, SOUTH, WEST);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() instanceof Player player) {
            if ((player.hasPermissions(1) && player.isCreative() && ConfigRegistry.DEBUG_WATER_SPOUT_SELECTION_BOX.get())
                    || (((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                            && ConfigRegistry.WATER_SPOUTS_BUCKETABLE.get())
                        || (player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PlasticBucketItem
                            && ConfigRegistry.WATER_SPOUTS_BUCKETABLE.get())
                        || player.getItemInHand(player.getUsedItemHand()).is(TagRegistry.CAN_SELECT_WATER_SPOUTS)))) {
                return this.shapes.get(state);
            }
        }
        // Shapes.empty() causes a crash, use a tiny bounding box instead
        return Shapes.box(8, 8, 8, 8.00001, 8.00001, 8.00001);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
        Direction facing = state.getValue(FACING);

        if (!WaterSpoutBlock.isValidSource(worldAccessor, pos.relative(facing.getOpposite()), facing)) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        return WaterSpoutBlock.updateConnections(state.setValue(TOP, !WaterSpoutBlock.isSpoutFacing(worldAccessor, pos.relative(facing), facing)),
                worldAccessor, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        BlockPos sourcePos = pos.relative(facing.getOpposite());
        BlockState sourceState = worldReader.getBlockState(sourcePos);

        if (facing == Direction.UP && sourceState.is(Blocks.WATER) && sourceState.getFluidState().getAmount() >= 8
                && sourceState.getFluidState().isSource())
            return true;
        return WaterSpoutBlock.isValidSource(worldReader, sourcePos, facing);
    }

    public static boolean isValidSource(LevelReader worldReader, BlockPos sourcePos, Direction facing) {
        BlockState sourceState = worldReader.getBlockState(sourcePos);

        if (sourceState.is(BlockRegistry.WATER_SPOUT.get()))
            return sourceState.getValue(FACING) == facing;
        else if (sourceState.getBlock() instanceof ClearWarpPipeBlock)
            return sourceState.getValue(WarpPipeBlock.FACING) == facing && !sourceState.getValue(WarpPipeBlock.CLOSED)
                    && sourceState.getValue(ClearWarpPipeBlock.WATERLOGGED) && WarpPipeBlock.hasWaterSpout(worldReader, sourcePos);
        else if (sourceState.getBlock() instanceof WarpPipeBlock)
            return sourceState.getValue(WarpPipeBlock.FACING) == facing && !sourceState.getValue(WarpPipeBlock.CLOSED)
                    && WarpPipeBlock.hasWaterSpout(worldReader, sourcePos);
        return false;
    }

    public static boolean canExistIn(LevelAccessor worldAccessor, BlockPos pos, Direction facing) {
        BlockState state = worldAccessor.getBlockState(pos);
        return state.isAir() || (state.is(BlockRegistry.WATER_SPOUT.get()) && state.getValue(FACING) == facing);
    }

    public static BlockState setBlockState(BlockState state, LevelAccessor worldAccessor, BlockPos pos, Direction facing) {
        boolean top = !WaterSpoutBlock.isSpoutFacing(worldAccessor, pos.relative(facing), facing);

        if (state.is(BlockRegistry.WATER_SPOUT.get()))
            return state.setValue(FACING, facing).setValue(TOP, top);
        else if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.FACING) == facing
                && !state.getValue(WarpPipeBlock.CLOSED))
            return BlockRegistry.WATER_SPOUT.get().defaultBlockState().setValue(FACING, facing).setValue(TOP, top);
        return Blocks.AIR.defaultBlockState();
    }

    public void addParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                             int amt, double xMotion, double yMotion, double zMotion, double speed) {
        if (world instanceof ServerLevel serverWorld)
            serverWorld.sendParticles(particleOptions, xPos, yPos, zPos, amt, xMotion, yMotion, zMotion, speed);
    }

    public void addAlwaysVisibleParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                                          double xMotion, double yMotion, double zMotion) {
        if (world.isClientSide)
            world.addAlwaysVisibleParticle(particleOptions, xPos, yPos, zPos, xMotion, yMotion, zMotion);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        Direction facing = state.getValue(FACING);
        boolean openAhead = world.getBlockState(pos.relative(facing)).isAir();

        if (openAhead) {
            double centerX = x + 0.5D + facing.getStepX() * 0.25D;
            double centerY = y + 0.5D + facing.getStepY() * 0.25D;
            double centerZ = z + 0.5D + facing.getStepZ() * 0.25D;

            for (Direction local : Direction.Plane.HORIZONTAL) {
                if (state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(local)))
                    continue;

                Direction localTangent = local.getClockWise();
                Direction outwardDirection = WaterSpoutBlock.getWorldSide(facing, local);
                Direction tangentDirection = WaterSpoutBlock.getWorldSide(facing, localTangent);
                double start = state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(local.getCounterClockWise())) ? -0.5D : -0.1875D;
                double end = state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(localTangent)) ? 0.5D : 0.1875D;
                int amount = 3 + (int) Math.round((end - start) * 9.0D) + random.nextInt(4);

                for (int i = 0; i < amount; ++i) {
                    double along = start + random.nextDouble() * (end - start);
                    double outward = 0.05D + random.nextDouble() * 0.12D;
                    double sideways = (random.nextDouble() - 0.5D) * 0.06D;
                    double edge = 0.1875D + random.nextDouble() * 0.04D;

                    this.addAlwaysVisibleParticles(world, ParticleTypes.SPLASH,
                            centerX + outwardDirection.getStepX() * edge + tangentDirection.getStepX() * along,
                            centerY + outwardDirection.getStepY() * edge + tangentDirection.getStepY() * along,
                            centerZ + outwardDirection.getStepZ() * edge + tangentDirection.getStepZ() * along,
                            outwardDirection.getStepX() * outward + tangentDirection.getStepX() * sideways,
                            outwardDirection.getStepY() * outward + tangentDirection.getStepY() * sideways,
                            outwardDirection.getStepZ() * outward + tangentDirection.getStepZ() * sideways);
                }
            }

            Direction eastDirection = WaterSpoutBlock.getWorldSide(facing, Direction.EAST);
            Direction southDirection = WaterSpoutBlock.getWorldSide(facing, Direction.SOUTH);
            double minEast = state.getValue(WEST) ? -0.5D : -0.1875D;
            double maxEast = state.getValue(EAST) ? 0.5D : 0.1875D;
            double minSouth = state.getValue(NORTH) ? -0.5D : -0.1875D;
            double maxSouth = state.getValue(SOUTH) ? 0.5D : 0.1875D;

            for (int i = 0; i < 2 + random.nextInt(2); ++i) {
                double eastOffset = minEast + random.nextDouble() * (maxEast - minEast);
                double southOffset = minSouth + random.nextDouble() * (maxSouth - minSouth);

                this.addAlwaysVisibleParticles(world, ParticleTypes.SPLASH,
                        centerX + eastDirection.getStepX() * eastOffset + southDirection.getStepX() * southOffset,
                        centerY + eastDirection.getStepY() * eastOffset + southDirection.getStepY() * southOffset,
                        centerZ + eastDirection.getStepZ() * eastOffset + southDirection.getStepZ() * southOffset,
                        facing.getStepX() * 0.04D, facing.getStepY() * 0.04D, facing.getStepZ() * 0.04D);
            }
        }

        if (random.nextInt(20) == 0) {
            world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS,
                    0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        Direction facing = state.getValue(FACING);
        Vec3 push = SubLevelCollision.getWorldDirection(world, pos, Vec3.atLowerCornerOf(facing.getNormal()));

        if (world.getBlockState(pos.relative(facing)).isAir()) {
            if (facing == Direction.UP && entity instanceof Boat boat) {
                boat.onAboveBubbleCol(Boolean.FALSE);
            } else this.onAboveBubbleColumn(entity, push);
        } else this.onInsideBubbleColumn(entity, push);

        if (!world.isClientSide) {
            ServerLevel serverWorld = (ServerLevel) world;

            for (int i = 0; i < 2; ++i) {
                Vec3 splashPos = WaterSpoutBlock.getRandomEndPos(pos, facing, world.random);
                Vec3 bubblePos = WaterSpoutBlock.getRandomEndPos(pos, facing, world.random);
                this.addParticles(serverWorld, ParticleTypes.SPLASH, splashPos.x, splashPos.y, splashPos.z,
                        1, 0.0D, 0.0D, 0.0D, 1.0D);
                this.addParticles(serverWorld, ParticleTypes.BUBBLE, bubblePos.x, bubblePos.y, bubblePos.z,
                        1, facing.getStepX() * 0.01D, facing.getStepY() * 0.01D, facing.getStepZ() * 0.01D, 0.2D);
            }
        }

       if (entity instanceof LivingEntity livingEntity) {
           if (!world.isClientSide && livingEntity.canDrownInFluidType(Fluids.WATER.getFluidType())) {
               int refillAmount = 1;
               int newAir = Math.min(livingEntity.getAirSupply() + refillAmount, livingEntity.getMaxAirSupply());
               livingEntity.setAirSupply(newAir);
           }
       }
    }

    public void onAboveBubbleColumn(Entity entity, Vec3 direction) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0 = Math.min(1.8D, vec3.dot(direction) + 0.1D);

        entity.setDeltaMovement(SubLevelCollision.withSpeedAlong(vec3, direction, d0));
    }

    public void onInsideBubbleColumn(Entity entity, Vec3 direction) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0 = Math.min(0.5D, vec3.dot(direction) + 0.04D);

        entity.setDeltaMovement(SubLevelCollision.withSpeedAlong(vec3, direction, d0 + 0.05D));
        entity.resetFallDistance();
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        Direction facing = state.getValue(FACING);
        BlockPos sourcePos = pos.relative(facing.getOpposite());
        int spoutHeight = serverWorld.getBlockEntity(sourcePos) instanceof WarpPipeBlockEntity pipeBlockEntity
                && pipeBlockEntity.hasWaterSpout() ? pipeBlockEntity.spoutHeight : 0;

        WaterSpoutBlock.repeatColumn(serverWorld, pos, serverWorld.getBlockState(sourcePos), facing, spoutHeight);
    }

    public static void repeatColumn(LevelAccessor worldAccessor, BlockPos pos, BlockState sourceState, Direction facing, int spoutHeight) {
        if (WaterSpoutBlock.canExistIn(worldAccessor, pos, facing) && spoutHeight != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(facing);
            BlockState pipeColumnState = WaterSpoutBlock.setBlockState(sourceState, worldAccessor, pos, facing);

            worldAccessor.setBlock(pos, WaterSpoutBlock.keepExistingConnections(pipeColumnState, worldAccessor, pos), 2);
            worldAccessor.scheduleTick(pos, BlockRegistry.WATER_SPOUT.get(), 3);

            // Used 4 - 1 since this somehow places one more block than intended
            while (WaterSpoutBlock.canExistIn(worldAccessor, mutablePos, facing) && initialDistance < spoutHeight - 1) {
                pipeColumnState = WaterSpoutBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos, facing);

                if (!worldAccessor.setBlock(mutablePos, WaterSpoutBlock.keepExistingConnections(pipeColumnState, worldAccessor, mutablePos), 2)) {
                    return;
                }
                mutablePos.move(facing);
                initialDistance++;
            }
        }
    }

    @NotNull
    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        if (ConfigRegistry.WATER_SPOUTS_BUCKETABLE.get() && player != null
                && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem) {
            worldAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return new ItemStack(Items.WATER_BUCKET);
        } else if (ConfigRegistry.WATER_SPOUTS_BUCKETABLE.get() && player != null
                && player.getItemInHand(player.getUsedItemHand()).getItem() instanceof PlasticBucketItem) {
            worldAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return new ItemStack(ItemRegistry.PLASTIC_WATER_BUCKET.get());
        } else if (ConfigRegistry.WATER_SPOUTS_BUCKETABLE.get()) {
            worldAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return new ItemStack(Items.WATER_BUCKET);
        } else return ItemStack.EMPTY;
    }

    @NotNull
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    public static int getModelXRotation(Direction facing) {
        return facing == Direction.UP ? 0 : facing == Direction.DOWN ? 180 : 90;
    }

    public static int getModelYRotation(Direction facing) {
        return facing.getAxis().isHorizontal() ? ((facing.get2DDataValue() + 2) % 4) * 90 : 0;
    }

    public static Direction rotateDirection(Direction direction, int xRotation, int yRotation) {
        for (int i = 0; i < xRotation / 90; i++)
            direction = direction.getClockWise(Direction.Axis.X);
        for (int i = 0; i < yRotation / 90; i++)
            direction = direction.getClockWise(Direction.Axis.Y);
        return direction;
    }

    public static Direction getWorldSide(Direction facing, Direction local) {
        return WaterSpoutBlock.rotateDirection(local, WaterSpoutBlock.getModelXRotation(facing), WaterSpoutBlock.getModelYRotation(facing));
    }

    public static Vec3 getRandomEndPos(BlockPos pos, Direction facing, RandomSource random) {
        Direction eastDirection = WaterSpoutBlock.getWorldSide(facing, Direction.EAST);
        Direction southDirection = WaterSpoutBlock.getWorldSide(facing, Direction.SOUTH);
        double eastOffset = random.nextDouble() - 0.5D;
        double southOffset = random.nextDouble() - 0.5D;

        return Vec3.atCenterOf(pos).add(Vec3.atLowerCornerOf(facing.getNormal()).scale(0.5D))
                .add(Vec3.atLowerCornerOf(eastDirection.getNormal()).scale(eastOffset))
                .add(Vec3.atLowerCornerOf(southDirection.getNormal()).scale(southOffset));
    }

    public static BlockState updateConnections(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        if (!(state.getBlock() instanceof WaterSpoutBlock))
            return state;

        BlockPos start = pos.immutable();
        Direction facing = state.getValue(FACING);
        return WaterSpoutBlock.applyConnections(worldAccessor, state, WaterSpoutBlock.getMergedRectangles(worldAccessor, start, facing), start);
    }

    public static BlockState applyConnections(LevelAccessor worldAccessor, BlockState state, Map<BlockPos, Integer> partition, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Integer rectangle = partition.get(pos);

        for (Direction local : Direction.Plane.HORIZONTAL) {
            BlockPos neighborPos = pos.relative(WaterSpoutBlock.getWorldSide(facing, local));
            state = state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(local),
                    rectangle != null && rectangle.equals(partition.get(neighborPos)));
        }
        return state;
    }

    public static BlockState keepExistingConnections(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        BlockState existingState = worldAccessor.getBlockState(pos);
        if (state.getBlock() instanceof WaterSpoutBlock && existingState.getBlock() instanceof WaterSpoutBlock
                && existingState.getValue(FACING) == state.getValue(FACING))
            return state.setValue(NORTH, existingState.getValue(NORTH)).setValue(EAST, existingState.getValue(EAST))
                    .setValue(SOUTH, existingState.getValue(SOUTH)).setValue(WEST, existingState.getValue(WEST));
        return WaterSpoutBlock.updateConnections(state, worldAccessor, pos);
    }

    public static Map<BlockPos, Integer> getMergedRectangles(LevelAccessor worldAccessor, BlockPos start, Direction facing) {
        Set<BlockPos> group = WaterSpoutBlock.findTouchingSpouts(worldAccessor, start, facing);
        if (group == null)
            return Map.of();
        if (facing == CACHED_FACING.get() && group.equals(CACHED_GROUP.get()))
            return CACHED_PARTITION.get();

        Map<BlockPos, Integer> partition = WaterSpoutBlock.splitIntoRectangles(worldAccessor, group, facing);
        CACHED_GROUP.set(group);
        CACHED_FACING.set(facing);
        CACHED_PARTITION.set(partition);

        if (!worldAccessor.isClientSide()) {
            for (BlockPos member : group) {
                BlockState memberState = worldAccessor.getBlockState(member);
                if (!member.equals(start) && memberState.getBlock() instanceof WaterSpoutBlock) {
                    BlockState connectedState = WaterSpoutBlock.applyConnections(worldAccessor, memberState, partition, member);
                    if (connectedState != memberState)
                        worldAccessor.setBlock(member, connectedState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                }
            }
        }
        return partition;
    }

    @Nullable
    public static Set<BlockPos> findTouchingSpouts(LevelAccessor worldAccessor, BlockPos start, Direction facing) {
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        int columnLength = 1 + WaterSpoutBlock.getColumnLength(worldAccessor, start.relative(facing), facing);
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (Direction local : Direction.Plane.HORIZONTAL) {
                BlockPos next = current.relative(WaterSpoutBlock.getWorldSide(facing, local));
                if (!visited.contains(next) && WaterSpoutBlock.isSpoutFacing(worldAccessor, next, facing)
                        && WaterSpoutBlock.getColumnLength(worldAccessor, next, facing) == columnLength) {
                    if (visited.size() >= MAX_CONNECTED_SPOUTS)
                        return null;
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return visited;
    }

    public static Map<BlockPos, Integer> splitIntoRectangles(LevelAccessor worldAccessor, Set<BlockPos> group, Direction facing) {
        Direction eastDirection = WaterSpoutBlock.getWorldSide(facing, Direction.EAST);
        Direction southDirection = WaterSpoutBlock.getWorldSide(facing, Direction.SOUTH);
        int minU = Integer.MAX_VALUE, maxU = Integer.MIN_VALUE, minV = Integer.MAX_VALUE, maxV = Integer.MIN_VALUE;

        for (BlockPos member : group) {
            int u = WaterSpoutBlock.getPlaneCoordinate(member, eastDirection);
            int v = WaterSpoutBlock.getPlaneCoordinate(member, southDirection);
            minU = Math.min(minU, u);
            maxU = Math.max(maxU, u);
            minV = Math.min(minV, v);
            maxV = Math.max(maxV, v);
        }

        int width = maxU - minU + 1;
        int depth = maxV - minV + 1;
        BlockPos[][] cells = new BlockPos[width][depth];
        boolean[][] free = new boolean[width][depth];
        boolean[][] connectedEast = new boolean[width][depth];
        boolean[][] connectedSouth = new boolean[width][depth];

        for (BlockPos member : group) {
            int u = WaterSpoutBlock.getPlaneCoordinate(member, eastDirection) - minU;
            int v = WaterSpoutBlock.getPlaneCoordinate(member, southDirection) - minV;
            BlockState memberState = worldAccessor.getBlockState(member);
            cells[u][v] = member;
            free[u][v] = true;
            if (memberState.getBlock() instanceof WaterSpoutBlock) {
                connectedEast[u][v] = memberState.getValue(EAST);
                connectedSouth[u][v] = memberState.getValue(SOUTH);
            }
        }

        Map<BlockPos, Integer> partition = new HashMap<>();
        int id = 0;
        int[] best = WaterSpoutBlock.findLargestRectangle(free, connectedEast, connectedSouth, width, depth);

        while (best != null) {
            for (int u = best[0]; u <= best[2]; u++) {
                for (int v = best[1]; v <= best[3]; v++) {
                    free[u][v] = false;
                    partition.put(cells[u][v], id);
                }
            }
            id++;
            best = WaterSpoutBlock.findLargestRectangle(free, connectedEast, connectedSouth, width, depth);
        }
        return partition;
    }

    public static int getPlaneCoordinate(BlockPos pos, Direction direction) {
        return pos.getX() * direction.getStepX() + pos.getY() * direction.getStepY() + pos.getZ() * direction.getStepZ();
    }

    @Nullable
    public static int[] findLargestRectangle(boolean[][] free, boolean[][] connectedEast, boolean[][] connectedSouth,
                                             int width, int depth) {
        int[] heights = new int[width];
        int[] best = null;
        int bestArea = 0;
        int bestScore = -1;

        for (int z = 0; z < depth; z++) {
            for (int x = 0; x < width; x++)
                heights[x] = free[x][z] ? heights[x] + 1 : 0;

            for (int left = 0; left < width; left++) {
                int minHeight = Integer.MAX_VALUE;
                for (int right = left; right < width; right++) {
                    minHeight = Math.min(minHeight, heights[right]);
                    if (minHeight < 1)
                        break;

                    int area = (right - left + 1) * minHeight;
                    if (area < 2 || area < bestArea)
                        continue;

                    int top = z - minHeight + 1;
                    int score = WaterSpoutBlock.countExistingConnections(connectedEast, connectedSouth, left, top, right, z);
                    if (area > bestArea || score > bestScore) {
                        bestArea = area;
                        bestScore = score;
                        best = new int[]{left, top, right, z};
                    }
                }
            }
        }
        return best;
    }

    public static int countExistingConnections(boolean[][] connectedEast, boolean[][] connectedSouth,
                                               int left, int top, int right, int bottom) {
        int score = 0;
        for (int x = left; x <= right; x++) {
            for (int z = top; z <= bottom; z++) {
                if (x < right && connectedEast[x][z])
                    score++;
                if (z < bottom && connectedSouth[x][z])
                    score++;
            }
        }
        return score;
    }

    public static VoxelShape createShape(BlockState state) {
        Direction facing = state.getValue(FACING);
        double height = state.getValue(TOP) ? 12 : 16;
        VoxelShape shape = WaterSpoutBlock.makeSectionBox(facing, 0, 0, height);

        for (Direction local : Direction.Plane.HORIZONTAL) {
            if (!state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(local)))
                continue;

            Direction clockwise = local.getClockWise();
            shape = Shapes.or(shape, WaterSpoutBlock.makeSectionBox(facing, local.getStepX(), local.getStepZ(), height));

            if (state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(clockwise)))
                shape = Shapes.or(shape, WaterSpoutBlock.makeSectionBox(facing, local.getStepX() + clockwise.getStepX(),
                        local.getStepZ() + clockwise.getStepZ(), height));
        }
        return shape.optimize();
    }

    public static VoxelShape makeSectionBox(Direction facing, int stepX, int stepZ, double height) {
        double[] from = {stepX < 0 ? 0 : stepX > 0 ? 11 : 5, 0, stepZ < 0 ? 0 : stepZ > 0 ? 11 : 5};
        double[] to = {stepX < 0 ? 5 : stepX > 0 ? 16 : 11, height, stepZ < 0 ? 5 : stepZ > 0 ? 16 : 11};
        double[] rotatedFrom = WaterSpoutBlock.rotatePoint(from, facing);
        double[] rotatedTo = WaterSpoutBlock.rotatePoint(to, facing);

        return Block.box(Math.min(rotatedFrom[0], rotatedTo[0]), Math.min(rotatedFrom[1], rotatedTo[1]),
                Math.min(rotatedFrom[2], rotatedTo[2]), Math.max(rotatedFrom[0], rotatedTo[0]),
                Math.max(rotatedFrom[1], rotatedTo[1]), Math.max(rotatedFrom[2], rotatedTo[2]));
    }

    public static double[] rotatePoint(double[] point, Direction facing) {
        double x = point[0], y = point[1], z = point[2];

        for (int i = 0; i < WaterSpoutBlock.getModelXRotation(facing) / 90; i++) {
            double newY = z;
            z = 16 - y;
            y = newY;
        }
        for (int i = 0; i < WaterSpoutBlock.getModelYRotation(facing) / 90; i++) {
            double newX = 16 - z;
            z = x;
            x = newX;
        }
        return new double[]{x, y, z};
    }

    public static int getColumnLength(LevelAccessor worldAccessor, BlockPos pos, Direction facing) {
        BlockPos.MutableBlockPos mutablePos = pos.mutable();
        int length = 0;

        while (length < MAX_COLUMN_LENGTH && WaterSpoutBlock.isSpoutFacing(worldAccessor, mutablePos, facing)) {
            length++;
            mutablePos.move(facing);
        }
        return length;
    }

    public static boolean isSpoutFacing(LevelAccessor worldAccessor, BlockPos pos, Direction facing) {
        if (!worldAccessor.hasChunkAt(pos))
            return false;
        BlockState state = worldAccessor.getBlockState(pos);
        return state.is(BlockRegistry.WATER_SPOUT.get()) && state.getValue(FACING) == facing;
    }
}
