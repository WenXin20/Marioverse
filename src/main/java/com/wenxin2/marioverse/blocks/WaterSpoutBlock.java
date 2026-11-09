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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WaterSpoutBlock extends Block implements BucketPickup {
    public static final BooleanProperty TOP = BlockStatePropertyRegistry.TOP;
    public static final BooleanProperty SURFACE = BlockStatePropertyRegistry.SURFACE;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty COVERED_NORTH = BlockStatePropertyRegistry.COVERED_NORTH;
    public static final BooleanProperty COVERED_EAST = BlockStatePropertyRegistry.COVERED_EAST;
    public static final BooleanProperty COVERED_SOUTH = BlockStatePropertyRegistry.COVERED_SOUTH;
    public static final BooleanProperty COVERED_WEST = BlockStatePropertyRegistry.COVERED_WEST;
    public static final Map<Direction, BooleanProperty> COVERED_BY_DIRECTION = Map.of(Direction.NORTH, COVERED_NORTH,
            Direction.EAST, COVERED_EAST, Direction.SOUTH, COVERED_SOUTH, Direction.WEST, COVERED_WEST);
    public static final int MAX_CONNECTED_SPOUTS = 256;
    private static final ThreadLocal<Set<BlockPos>> CACHED_GROUP = new ThreadLocal<>();
    private static final ThreadLocal<Map<BlockPos, Integer>> CACHED_PARTITION = new ThreadLocal<>();
    private final Map<BlockState, VoxelShape> shapes;

    public WaterSpoutBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, Boolean.FALSE).setValue(SURFACE, Boolean.FALSE)
                .setValue(NORTH, Boolean.FALSE).setValue(EAST, Boolean.FALSE)
                .setValue(SOUTH, Boolean.FALSE).setValue(WEST, Boolean.FALSE)
                .setValue(COVERED_NORTH, Boolean.FALSE).setValue(COVERED_EAST, Boolean.FALSE)
                .setValue(COVERED_SOUTH, Boolean.FALSE).setValue(COVERED_WEST, Boolean.FALSE));
        this.shapes = this.getShapeForEachState(WaterSpoutBlock::createShape);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(TOP, SURFACE, NORTH, EAST, SOUTH, WEST, COVERED_NORTH, COVERED_EAST, COVERED_SOUTH, COVERED_WEST);
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
        BlockState stateAbove = worldAccessor.getBlockState(pos.above());
        BlockState stateBelow = worldAccessor.getBlockState(pos.below());

        if (stateBelow.getBlock() instanceof WarpPipeBlock
                && (stateBelow.getValue(WarpPipeBlock.CLOSED) || stateBelow.getValue(WarpPipeBlock.FACING) != Direction.UP
                || !WarpPipeBlock.hasWaterSpout(worldAccessor, pos.below()))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        if (stateBelow.getBlock() instanceof ClearWarpPipeBlock
                && (stateBelow.getValue(WarpPipeBlock.CLOSED) || stateBelow.getValue(WarpPipeBlock.FACING) != Direction.UP
                || !stateBelow.getValue(ClearWarpPipeBlock.WATERLOGGED) || !WarpPipeBlock.hasWaterSpout(worldAccessor, pos.below()))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        if (!(stateBelow.getBlock() instanceof WarpPipeBlock) && !(stateBelow.getBlock() instanceof WaterSpoutBlock)) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        return WaterSpoutBlock.withConnections(state.setValue(TOP, stateAbove.getBlock() != BlockRegistry.WATER_SPOUT.get()),
                worldAccessor, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        BlockState stateBelow = worldReader.getBlockState(pos.below());

        if (stateBelow.is(BlockRegistry.WATER_SPOUT.get()))
            return true;
        else if ((stateBelow.is(Blocks.WATER) && stateBelow.getFluidState().getAmount() >= 8 && stateBelow.getFluidState().isSource()))
            return true;
        else if (stateBelow.getBlock() instanceof WarpPipeBlock && stateBelow.getValue(WarpPipeBlock.FACING) == Direction.UP
                && (!stateBelow.getValue(WarpPipeBlock.CLOSED) && WarpPipeBlock.hasWaterSpout(worldReader, pos.below()))
                && !(stateBelow.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else return stateBelow.getBlock() instanceof ClearWarpPipeBlock && stateBelow.getValue(WarpPipeBlock.FACING) == Direction.UP
                    && (!stateBelow.getValue(WarpPipeBlock.CLOSED) && WarpPipeBlock.hasWaterSpout(worldReader, pos.below()))
                    && stateBelow.getValue(ClearWarpPipeBlock.WATERLOGGED);
    }

    public static boolean canExistIn(LevelAccessor worldAccessor, BlockPos pos) {
        return worldAccessor.getBlockState(pos).is(BlockRegistry.WATER_SPOUT.get()) ||  worldAccessor.getBlockState(pos).isAir();
    }

    public static BlockState setBlockState(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        BlockState stateAbove = worldAccessor.getBlockState(pos.above());
        if (state.isAir() || state.is(BlockRegistry.WATER_SPOUT.get())
                || (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))) {
            if (state.is(BlockRegistry.WATER_SPOUT.get())) {
                if (!stateAbove.is(BlockRegistry.WATER_SPOUT.get()))
                    return state.setValue(TOP, Boolean.TRUE);
                else return state.setValue(TOP, Boolean.FALSE);
            } else if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.FACING) == Direction.UP
                    && !state.getValue(WarpPipeBlock.CLOSED)) {
                if (!stateAbove.is(BlockRegistry.WATER_SPOUT.get()))
                    return BlockRegistry.WATER_SPOUT.get().defaultBlockState().setValue(TOP, Boolean.TRUE);
                else return BlockRegistry.WATER_SPOUT.get().defaultBlockState().setValue(TOP, Boolean.FALSE);
            }
        }
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

        boolean openAbove = world.getBlockState(pos.above()).isAir();

        if (openAbove || (!state.getValue(TOP) && state.getValue(SURFACE))) {
            double topY = y + 0.75D;

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                if (state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction)))
                    continue;

                Direction tangent = direction.getClockWise();
                double start = state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction.getCounterClockWise())) ? -0.5D : -0.1875D;
                double end = state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(tangent)) ? 0.5D : 0.1875D;
                int amount = 3 + (int) Math.round((end - start) * 9.0D) + random.nextInt(4);

                for (int i = 0; i < amount; ++i) {
                    double along = start + random.nextDouble() * (end - start);
                    double outward = 0.05D + random.nextDouble() * 0.12D;
                    double sideways = (random.nextDouble() - 0.5D) * 0.06D;
                    double edge = 0.1875D + random.nextDouble() * 0.04D;
                    double px = x + 0.5D + direction.getStepX() * edge + tangent.getStepX() * along;
                    double pz = z + 0.5D + direction.getStepZ() * edge + tangent.getStepZ() * along;

                    this.addAlwaysVisibleParticles(world, ParticleTypes.SPLASH, px, topY, pz,
                            direction.getStepX() * outward + tangent.getStepX() * sideways, 0.0D,
                            direction.getStepZ() * outward + tangent.getStepZ() * sideways);
                }
            }

            double minX = state.getValue(WEST) ? 0.0D : 0.3125D;
            double maxX = state.getValue(EAST) ? 1.0D : 0.6875D;
            double minZ = state.getValue(NORTH) ? 0.0D : 0.3125D;
            double maxZ = state.getValue(SOUTH) ? 1.0D : 0.6875D;

            for (int i = 0; openAbove && i < 2 + random.nextInt(2); ++i)
                this.addAlwaysVisibleParticles(world, ParticleTypes.SPLASH,
                        x + minX + random.nextDouble() * (maxX - minX), topY,
                        z + minZ + random.nextDouble() * (maxZ - minZ), 0.0D, 0.04D, 0.0D);
        }

        if (random.nextInt(20) == 0) {
            world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS,
                    0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockState stateAbove = world.getBlockState(pos.above());

        if (stateAbove.isAir()) {
            if (entity instanceof Boat boat) {
                boat.onAboveBubbleCol(Boolean.FALSE);
            } else this.onAboveUpBubbleCol(entity, world, pos);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else {
            this.onInsideUpBubbleColumn(entity, world, pos);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
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

    public void onAboveUpBubbleCol(Entity entity, Level world, BlockPos pos) {
        Vec3 vec3 = entity.getDeltaMovement();
        Vec3 up = SubLevelCollision.getUpDirection(world, pos);
        double d0 = Math.min(1.8D, vec3.dot(up) + 0.1D);

        entity.setDeltaMovement(SubLevelCollision.withSpeedAlong(vec3, up, d0));
    }

    public void onInsideUpBubbleColumn(Entity entity, Level world, BlockPos pos) {
        Vec3 vec3 = entity.getDeltaMovement();
        Vec3 up = SubLevelCollision.getUpDirection(world, pos);
        double d0 = Math.min(0.5D, vec3.dot(up) + 0.04D);

        entity.setDeltaMovement(SubLevelCollision.withSpeedAlong(vec3, up, d0 + 0.05D));
        entity.resetFallDistance();
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        WarpPipeBlockEntity pipeBlockEntity = (WarpPipeBlockEntity) serverWorld.getBlockEntity(pos.below());
        if (pipeBlockEntity != null && pipeBlockEntity.hasWaterSpout())
            WaterSpoutBlock.repeatColumnUp(serverWorld, pos, state, serverWorld.getBlockState(pos.below()), pipeBlockEntity.spoutHeight);
        else WaterSpoutBlock.repeatColumnUp(serverWorld, pos, state, serverWorld.getBlockState(pos.below()), 0);
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state, int spoutHeight) {

        repeatColumnUp(worldAccessor, pos, worldAccessor.getBlockState(pos), state, spoutHeight);
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState, int spoutHeight) {
        if (WaterSpoutBlock.canExistIn(worldAccessor, pos) && spoutHeight != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.UP);
            BlockState pipeColumnState = WaterSpoutBlock.setBlockState(neighborState, worldAccessor, pos);
            BlockState stateAbove = worldAccessor.getBlockState(mutablePos.above());

            if (pipeColumnState.getBlock() instanceof WaterSpoutBlock) {
                BlockState baseState = pipeColumnState.setValue(TOP, stateAbove.getBlock() != BlockRegistry.WATER_SPOUT.get());
                worldAccessor.setBlock(pos, WaterSpoutBlock.withExistingConnections(baseState, worldAccessor, pos), 2);
            } else worldAccessor.setBlock(pos, pipeColumnState, 2);
            worldAccessor.scheduleTick(pos, BlockRegistry.WATER_SPOUT.get(), 3);

            // Used 4 - 1 since this somehow places one more block than intended
            while (WaterSpoutBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < spoutHeight - 1) {

                if (!worldAccessor.setBlock(mutablePos, WaterSpoutBlock.withExistingConnections(pipeColumnState, worldAccessor, mutablePos), 2)) {
                    return;
                }
                mutablePos.move(Direction.UP);
                initialDistance++;
                pipeColumnState = WaterSpoutBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
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

    public static BlockState withConnections(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        if (!(state.getBlock() instanceof WaterSpoutBlock))
            return state;

        BlockPos start = pos.immutable();
        return WaterSpoutBlock.connect(worldAccessor, state, WaterSpoutBlock.getPartition(worldAccessor, start), start);
    }

    public static BlockState connect(LevelAccessor worldAccessor, BlockState state, Map<BlockPos, Integer> partition, BlockPos pos) {
        Integer rectangle = partition.get(pos);
        return WaterSpoutBlock.withCover(worldAccessor, state.setValue(NORTH, rectangle != null && rectangle.equals(partition.get(pos.north())))
                .setValue(EAST, rectangle != null && rectangle.equals(partition.get(pos.east())))
                .setValue(SOUTH, rectangle != null && rectangle.equals(partition.get(pos.south())))
                .setValue(WEST, rectangle != null && rectangle.equals(partition.get(pos.west()))), pos);
    }

    public static BlockState withCover(LevelAccessor worldAccessor, BlockState state, BlockPos pos) {
        BlockState stateAbove = worldAccessor.getBlockState(pos.above());
        boolean covered = !state.getValue(TOP) && stateAbove.getBlock() instanceof WaterSpoutBlock;
        boolean surface = state.getValue(TOP);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BooleanProperty property = PipeBlock.PROPERTY_BY_DIRECTION.get(direction);
            boolean coveredSide = covered && stateAbove.getValue(property);
            state = state.setValue(COVERED_BY_DIRECTION.get(direction), coveredSide);
            if (state.getValue(property) && !coveredSide)
                surface = true;
        }
        return state.setValue(SURFACE, surface);
    }

    public static BlockState withExistingConnections(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        BlockState existingState = worldAccessor.getBlockState(pos);
        if (state.getBlock() instanceof WaterSpoutBlock && existingState.getBlock() instanceof WaterSpoutBlock)
            return WaterSpoutBlock.withCover(worldAccessor, state.setValue(NORTH, existingState.getValue(NORTH))
                    .setValue(EAST, existingState.getValue(EAST)).setValue(SOUTH, existingState.getValue(SOUTH))
                    .setValue(WEST, existingState.getValue(WEST)), pos);
        return WaterSpoutBlock.withConnections(state, worldAccessor, pos);
    }

    public static Map<BlockPos, Integer> getPartition(LevelAccessor worldAccessor, BlockPos start) {
        Set<BlockPos> group = WaterSpoutBlock.collectGroup(worldAccessor, start);
        if (group == null)
            return Map.of();
        if (group.equals(CACHED_GROUP.get()))
            return CACHED_PARTITION.get();

        Map<BlockPos, Integer> partition = WaterSpoutBlock.partitionIntoRectangles(worldAccessor, group);
        CACHED_GROUP.set(group);
        CACHED_PARTITION.set(partition);

        if (!worldAccessor.isClientSide()) {
            for (BlockPos member : group) {
                BlockState memberState = worldAccessor.getBlockState(member);
                if (!member.equals(start) && memberState.getBlock() instanceof WaterSpoutBlock) {
                    BlockState connectedState = WaterSpoutBlock.connect(worldAccessor, memberState, partition, member);
                    if (connectedState != memberState)
                        worldAccessor.setBlock(member, connectedState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                }
            }
        }
        return partition;
    }

    @Nullable
    public static Set<BlockPos> collectGroup(LevelAccessor worldAccessor, BlockPos start) {
        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        visited.add(start);
        queue.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos next = current.relative(direction);
                if (!visited.contains(next) && WaterSpoutBlock.isSpout(worldAccessor, next)) {
                    if (visited.size() >= MAX_CONNECTED_SPOUTS)
                        return null;
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return visited;
    }

    public static Map<BlockPos, Integer> partitionIntoRectangles(LevelAccessor worldAccessor, Set<BlockPos> group) {
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, minZ = Integer.MAX_VALUE, maxZ = Integer.MIN_VALUE;
        int y = 0;
        for (BlockPos member : group) {
            minX = Math.min(minX, member.getX());
            maxX = Math.max(maxX, member.getX());
            minZ = Math.min(minZ, member.getZ());
            maxZ = Math.max(maxZ, member.getZ());
            y = member.getY();
        }

        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;
        boolean[][] free = new boolean[width][depth];
        boolean[][] connectedEast = new boolean[width][depth];
        boolean[][] connectedSouth = new boolean[width][depth];
        for (BlockPos member : group) {
            int x = member.getX() - minX;
            int z = member.getZ() - minZ;
            BlockState memberState = worldAccessor.getBlockState(member);
            free[x][z] = true;
            if (memberState.getBlock() instanceof WaterSpoutBlock) {
                connectedEast[x][z] = memberState.getValue(EAST);
                connectedSouth[x][z] = memberState.getValue(SOUTH);
            }
        }

        Map<BlockPos, Integer> partition = new HashMap<>();
        int id = 0;
        int[] best = WaterSpoutBlock.findLargestRectangle(free, connectedEast, connectedSouth, width, depth);

        while (best != null) {
            for (int x = best[0]; x <= best[2]; x++) {
                for (int z = best[1]; z <= best[3]; z++) {
                    free[x][z] = false;
                    partition.put(new BlockPos(x + minX, y, z + minZ), id);
                }
            }
            id++;
            best = WaterSpoutBlock.findLargestRectangle(free, connectedEast, connectedSouth, width, depth);
        }
        return partition;
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
        VoxelShape shape = Block.box(5, 0, 5, 11, state.getValue(TOP) ? 12 : 16, 11);

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (!state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(direction)))
                continue;

            Direction clockwise = direction.getClockWise();
            boolean covered = state.getValue(COVERED_BY_DIRECTION.get(direction));
            shape = Shapes.or(shape, WaterSpoutBlock.sectionBox(direction.getStepX(), direction.getStepZ(), covered ? 16 : 12));

            if (state.getValue(PipeBlock.PROPERTY_BY_DIRECTION.get(clockwise))) {
                boolean cornerCovered = covered && state.getValue(COVERED_BY_DIRECTION.get(clockwise));
                shape = Shapes.or(shape, WaterSpoutBlock.sectionBox(direction.getStepX() + clockwise.getStepX(),
                        direction.getStepZ() + clockwise.getStepZ(), cornerCovered ? 16 : 12));
            }
        }
        return shape.optimize();
    }

    public static VoxelShape sectionBox(int stepX, int stepZ, double height) {
        double minX = stepX < 0 ? 0 : stepX > 0 ? 11 : 5;
        double maxX = stepX < 0 ? 5 : stepX > 0 ? 16 : 11;
        double minZ = stepZ < 0 ? 0 : stepZ > 0 ? 11 : 5;
        double maxZ = stepZ < 0 ? 5 : stepZ > 0 ? 16 : 11;
        return Block.box(minX, 0, minZ, maxX, height, maxZ);
    }

    public static boolean isSpout(LevelAccessor worldAccessor, BlockPos pos) {
        return worldAccessor.hasChunkAt(pos) && worldAccessor.getBlockState(pos).is(BlockRegistry.WATER_SPOUT.get());
    }
}