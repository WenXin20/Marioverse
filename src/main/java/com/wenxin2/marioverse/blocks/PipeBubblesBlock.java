package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BubbleColumnBlock;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
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

public class PipeBubblesBlock extends BubbleColumnBlock implements BucketPickup {
    public static final BooleanProperty DRAG_DOWN = BlockStateProperties.DRAG;
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public PipeBubblesBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(DRAG_DOWN, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(DRAG_DOWN, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext)context).getEntity() instanceof Player player
                && player.hasPermissions(1) && player.isCreative() && Config.DEBUG_PIPE_BUBBLES_SELECTION_BOX.get()) {
            return Shapes.block();
        }
        // Shapes.empty() causes a crash, use a tiny bounding box instead
        return Shapes.box(8, 8, 8, 8.00001, 8.00001, 8.00001);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        BlockState stateAbove = worldAccessor.getBlockState(pos.above());
        BlockState stateBelow = worldAccessor.getBlockState(pos.below());
        BlockState stateNorth = worldAccessor.getBlockState(pos.north());
        BlockState stateSouth = worldAccessor.getBlockState(pos.south());
        BlockState stateEast = worldAccessor.getBlockState(pos.east());
        BlockState stateWest = worldAccessor.getBlockState(pos.west());

        if (stateBelow.getBlock() instanceof WarpPipeBlock && stateBelow.getValue(WarpPipeBlock.CLOSED)) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.UP && stateBelow.getBlock() instanceof ClearWarpPipeBlock
                && (stateBelow.getValue(WarpPipeBlock.CLOSED) || !stateBelow.getValue(ClearWarpPipeBlock.WATERLOGGED)
                || !stateBelow.getValue(WarpPipeBlock.BUBBLES))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.DOWN && stateAbove.getBlock() instanceof ClearWarpPipeBlock
                && (stateAbove.getValue(WarpPipeBlock.CLOSED) || !stateAbove.getValue(ClearWarpPipeBlock.WATERLOGGED)
                || !stateAbove.getValue(WarpPipeBlock.BUBBLES))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.NORTH && stateSouth.getBlock() instanceof ClearWarpPipeBlock
                && (stateSouth.getValue(WarpPipeBlock.CLOSED) || !stateSouth.getValue(ClearWarpPipeBlock.WATERLOGGED)
                || !stateSouth.getValue(WarpPipeBlock.BUBBLES))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.SOUTH && stateNorth.getBlock() instanceof ClearWarpPipeBlock
                && (stateNorth.getValue(WarpPipeBlock.CLOSED) || !stateNorth.getValue(ClearWarpPipeBlock.WATERLOGGED)
                || !stateNorth.getValue(WarpPipeBlock.BUBBLES))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.EAST && stateWest.getBlock() instanceof ClearWarpPipeBlock
                && (stateWest.getValue(WarpPipeBlock.CLOSED) || !stateWest.getValue(ClearWarpPipeBlock.WATERLOGGED)
                || !stateWest.getValue(WarpPipeBlock.BUBBLES))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.WEST && stateEast.getBlock() instanceof ClearWarpPipeBlock
                && (stateEast.getValue(WarpPipeBlock.CLOSED) || !stateEast.getValue(ClearWarpPipeBlock.WATERLOGGED)
                || !stateEast.getValue(WarpPipeBlock.BUBBLES))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.UP
                && ((!(stateBelow.getBlock() instanceof WarpPipeBlock) && !(stateBelow.getBlock() instanceof PipeBubblesBlock))
                || (stateBelow.getBlock() instanceof WarpPipeBlock
                && (stateBelow.getValue(WarpPipeBlock.CLOSED) || !stateBelow.getValue(WarpPipeBlock.BUBBLES))))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.DOWN
                && ((!(stateAbove.getBlock() instanceof WarpPipeBlock) && !(stateAbove.getBlock() instanceof PipeBubblesBlock))
                || (stateAbove.getBlock() instanceof WarpPipeBlock
                && (stateAbove.getValue(WarpPipeBlock.CLOSED) || !stateAbove.getValue(WarpPipeBlock.BUBBLES))))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.NORTH
                && ((!(stateSouth.getBlock() instanceof WarpPipeBlock) && !(stateSouth.getBlock() instanceof PipeBubblesBlock))
                || (stateSouth.getBlock() instanceof WarpPipeBlock
                && (stateSouth.getValue(WarpPipeBlock.CLOSED) || !stateSouth.getValue(WarpPipeBlock.BUBBLES))))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.SOUTH
                && ((!(stateNorth.getBlock() instanceof WarpPipeBlock) && !(stateNorth.getBlock() instanceof PipeBubblesBlock))
                || (stateNorth.getBlock() instanceof WarpPipeBlock
                && (stateNorth.getValue(WarpPipeBlock.CLOSED) || !stateNorth.getValue(WarpPipeBlock.BUBBLES))))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.EAST
                && ((!(stateWest.getBlock() instanceof WarpPipeBlock) && !(stateWest.getBlock() instanceof PipeBubblesBlock))
                || (stateWest.getBlock() instanceof WarpPipeBlock
                && (stateWest.getValue(WarpPipeBlock.CLOSED) || !stateWest.getValue(WarpPipeBlock.BUBBLES))))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (state.getValue(FACING) == Direction.WEST
                && ((!(stateEast.getBlock() instanceof WarpPipeBlock) && !(stateEast.getBlock() instanceof PipeBubblesBlock))
                || (stateEast.getBlock() instanceof WarpPipeBlock
                && (stateEast.getValue(WarpPipeBlock.CLOSED) || !stateEast.getValue(WarpPipeBlock.BUBBLES))))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.WATER.defaultBlockState();
        }

        if (!state.canSurvive(worldAccessor, pos) && !neighborState.is(BlockRegistry.PIPE_BUBBLES.get())
                && canExistIn(worldAccessor, pos)) {
            worldAccessor.scheduleTick(pos, this, 3);
        }

        worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        BlockState stateAbove = worldReader.getBlockState(pos.above());
        BlockState stateBelow = worldReader.getBlockState(pos.below());
        BlockState stateNorth = worldReader.getBlockState(pos.below());
        BlockState stateSouth = worldReader.getBlockState(pos.below());
        BlockState stateEast = worldReader.getBlockState(pos.below());
        BlockState stateWest = worldReader.getBlockState(pos.below());

        if (state.getValue(FACING) == Direction.UP && stateBelow.is(BlockRegistry.PIPE_BUBBLES.get()))
            return true;
        else if (state.getValue(FACING) == Direction.DOWN && stateAbove.is(BlockRegistry.PIPE_BUBBLES.get()))
            return true;
        else if (state.getValue(FACING) == Direction.NORTH && stateSouth.is(BlockRegistry.PIPE_BUBBLES.get()))
            return true;
        else if (state.getValue(FACING) == Direction.SOUTH && stateNorth.is(BlockRegistry.PIPE_BUBBLES.get()))
            return true;
        else if (state.getValue(FACING) == Direction.EAST && stateWest.is(BlockRegistry.PIPE_BUBBLES.get()))
            return true;
        else if (state.getValue(FACING) == Direction.WEST && stateEast.is(BlockRegistry.PIPE_BUBBLES.get()))
            return true;
        else if (state.getValue(FACING) == Direction.UP && stateBelow.getBlock() instanceof WarpPipeBlock
                && (!stateBelow.getValue(WarpPipeBlock.CLOSED) && stateBelow.getValue(WarpPipeBlock.BUBBLES))
                && !(stateBelow.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else if (state.getValue(FACING) == Direction.DOWN && stateAbove.getBlock() instanceof WarpPipeBlock
                && (!stateAbove.getValue(WarpPipeBlock.CLOSED) && stateAbove.getValue(WarpPipeBlock.BUBBLES))
                && !(stateAbove.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else if (state.getValue(FACING) == Direction.NORTH && stateSouth.getBlock() instanceof WarpPipeBlock
                && (!stateSouth.getValue(WarpPipeBlock.CLOSED) && stateSouth.getValue(WarpPipeBlock.BUBBLES))
                && !(stateSouth.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else if (state.getValue(FACING) == Direction.SOUTH && stateNorth.getBlock() instanceof WarpPipeBlock
                && (!stateNorth.getValue(WarpPipeBlock.CLOSED) && stateNorth.getValue(WarpPipeBlock.BUBBLES))
                && !(stateNorth.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else if (state.getValue(FACING) == Direction.EAST && stateWest.getBlock() instanceof WarpPipeBlock
                && (!stateWest.getValue(WarpPipeBlock.CLOSED) && stateWest.getValue(WarpPipeBlock.BUBBLES))
                && !(stateWest.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else if (state.getValue(FACING) == Direction.WEST && stateEast.getBlock() instanceof WarpPipeBlock
                && (!stateEast.getValue(WarpPipeBlock.CLOSED) && stateEast.getValue(WarpPipeBlock.BUBBLES))
                && !(stateEast.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else if (state.getValue(FACING) == Direction.UP && stateBelow.getBlock() instanceof ClearWarpPipeBlock
                && (!stateBelow.getValue(WarpPipeBlock.CLOSED) && stateBelow.getValue(WarpPipeBlock.BUBBLES))
                && stateBelow.getValue(ClearWarpPipeBlock.WATERLOGGED))
            return true;
        else if (state.getValue(FACING) == Direction.DOWN && stateAbove.getBlock() instanceof ClearWarpPipeBlock
                && (!stateAbove.getValue(WarpPipeBlock.CLOSED) && stateAbove.getValue(WarpPipeBlock.BUBBLES))
                && stateAbove.getValue(ClearWarpPipeBlock.WATERLOGGED))
            return true;
        else if (state.getValue(FACING) == Direction.NORTH && stateSouth.getBlock() instanceof ClearWarpPipeBlock
                && (!stateSouth.getValue(WarpPipeBlock.CLOSED) && stateSouth.getValue(WarpPipeBlock.BUBBLES))
                && stateSouth.getValue(ClearWarpPipeBlock.WATERLOGGED))
            return true;
        else if (state.getValue(FACING) == Direction.SOUTH && stateNorth.getBlock() instanceof ClearWarpPipeBlock
                && (!stateNorth.getValue(WarpPipeBlock.CLOSED) && stateNorth.getValue(WarpPipeBlock.BUBBLES))
                && stateNorth.getValue(ClearWarpPipeBlock.WATERLOGGED))
            return true;
        else if (state.getValue(FACING) == Direction.EAST && stateWest.getBlock() instanceof ClearWarpPipeBlock
                && (!stateWest.getValue(WarpPipeBlock.CLOSED) && stateWest.getValue(WarpPipeBlock.BUBBLES))
                && stateWest.getValue(ClearWarpPipeBlock.WATERLOGGED))
            return true;
        else if (state.getValue(FACING) == Direction.WEST && stateEast.getBlock() instanceof ClearWarpPipeBlock
                && (!stateEast.getValue(WarpPipeBlock.CLOSED) && stateEast.getValue(WarpPipeBlock.BUBBLES))
                && stateEast.getValue(ClearWarpPipeBlock.WATERLOGGED))
            return true;
        else return false;
    }

    public static boolean canExistIn(LevelAccessor worldAccessor, BlockPos pos) {
        return worldAccessor.getBlockState(pos).is(BlockRegistry.PIPE_BUBBLES.get())
                || worldAccessor.getBlockState(pos).is(Blocks.WATER)
                && worldAccessor.getBlockState(pos).getFluidState().isSource();
    }

    public static BlockState setBlockState(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values()) {
            posMutable.setWithOffset(pos, direction);
        }

        if (state.is(BlockRegistry.PIPE_BUBBLES.get())) {
            return state;
        } else if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED) && state.getValue(WarpPipeBlock.BUBBLES)) {
            return BlockRegistry.PIPE_BUBBLES.get().defaultBlockState().setValue(DRAG_DOWN, Boolean.FALSE)
                    .setValue(FACING, state.getValue(FACING));
        }
        return Blocks.WATER.defaultBlockState();
    }

    public void addParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                             int amt, double xMotion, double yMotion, double zMotion, double speed) {
        if (!world.isClientSide) {
            ServerLevel serverWorld = (ServerLevel)world;
            serverWorld.sendParticles(particleOptions, xPos, yPos, zPos, amt, xMotion, yMotion, zMotion, speed);
        }
    }

    public void addAlwaysVisibleParticles(Level world, ParticleOptions particleOptions, double xPos, double yPos, double zPos,
                                          double xMotion, double yMotion, double zMotion) {
        if (world.isClientSide) {
            world.addAlwaysVisibleParticle(particleOptions, xPos, yPos, zPos, xMotion, yMotion, zMotion);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();
        if (state.getValue(DRAG_DOWN) || state.getValue(FACING) == Direction.DOWN) {
            this.addAlwaysVisibleParticles(world, ParticleTypes.CURRENT_DOWN, x + 0.5D, y + 0.8D, z, 0.0D, -1.0D, 0.0D);
            this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                    y + random.nextFloat(), z + random.nextFloat(), 0.0D, -1.5D, 0.0D);
            if (random.nextInt(200) == 0) {
                world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS,
                        0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
            }
        } else {
            if (state.getValue(FACING) == Direction.UP) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 1.0D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 1.0D, 0.0D);
            } else if (state.getValue(FACING) == Direction.NORTH) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, -1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, -1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, -1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, -1.5D);
            } else if (state.getValue(FACING) == Direction.SOUTH) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, 1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 0.0D, 0.04D, 1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, 1.5D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 0.0D, 0.04D, 1.5D);
            } else if (state.getValue(FACING) == Direction.EAST) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, 1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), 1.5D, 0.04D, 0.0D);
            } else if (state.getValue(FACING) == Direction.WEST) {
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, -1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + 0.5D, y, z + 0.5D, -1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), -1.5D, 0.04D, 0.0D);
                this.addAlwaysVisibleParticles(world, ParticleTypes.BUBBLE, x + random.nextFloat(),
                        y + random.nextFloat(), z + random.nextFloat(), -1.5D, 0.04D, 0.0D);
            }
        }

        if (random.nextInt(200) == 0) {
            world.playLocalSound(x, y, z, SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT, SoundSource.BLOCKS,
                    0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
        }
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        BlockState stateAbove = world.getBlockState(pos.above());

        if (stateAbove.isAir() && state.getValue(FACING) == Direction.UP) {
            if (entity instanceof Boat boat) {
                boat.onAboveBubbleCol(Boolean.FALSE);
            } else this.onAboveUpBubbleCol(state.getValue(DRAG_DOWN), entity);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else if (state.getValue(FACING) == Direction.UP) {
            this.onInsideUpBubbleColumn(state.getValue(DRAG_DOWN), entity);
            if (!world.isClientSide) {
                ServerLevel serverWorld = (ServerLevel)world;

                for(int i = 0; i < 2; ++i) {
                    this.addParticles(serverWorld, ParticleTypes.SPLASH, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
                    this.addParticles(serverWorld, ParticleTypes.BUBBLE, pos.getX() + world.random.nextDouble(),
                            (pos.getY() + 1), pos.getZ() + world.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
                }
            }
        } else if (state.getValue(FACING) == Direction.DOWN) {
            this.onInsideDownBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.NORTH) {
            this.onInsideNorthBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.SOUTH) {
            this.onInsideSouthBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.EAST) {
            this.onInsideEastBubbleColumn(state.getValue(DRAG_DOWN), entity);
        } else if (state.getValue(FACING) == Direction.WEST) {
            this.onInsideWestBubbleColumn(state.getValue(DRAG_DOWN), entity);
        }

       if (entity instanceof LivingEntity livingEntity) {
           if (!world.isClientSide && livingEntity.canDrownInFluidType(Fluids.WATER.getFluidType())) {
               int refillAmount = 1;
               int newAir = Math.min(livingEntity.getAirSupply() + refillAmount, livingEntity.getMaxAirSupply());
               livingEntity.setAirSupply(newAir);
           }
       }
    }

    public void onAboveUpBubbleCol(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.9D, vec3.y - 0.03D);
        } else {
            d0 = Math.min(1.8D, vec3.y + 0.1D);
        }

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
    }

    public void onInsideUpBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.3D, vec3.y - 0.03D);
        } else {
            d0 = Math.min(0.5D, vec3.y + 0.04D);
        }

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
        entity.resetFallDistance();
    }

    public void onInsideDownBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(0.3D, vec3.y + 0.03D);
        } else {
            d0 = Math.min(-0.5D, vec3.y - 0.04D);
        }

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
        entity.resetFallDistance();
    }

    public void onInsideNorthBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(0.3D, vec3.z + 0.03D);
        } else {
            d0 = Math.min(-0.5D, vec3.z - 0.04D);
        }

        entity.setDeltaMovement(vec3.x, vec3.y, d0);
        entity.resetFallDistance();
    }

    public void onInsideSouthBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.3D, vec3.z - 0.03D);
        } else {
            d0 = Math.min(0.5D, vec3.z + 0.04D);
        }

        entity.setDeltaMovement(vec3.x, vec3.y, d0);
        entity.resetFallDistance();
    }

    public void onInsideEastBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(-0.3D, vec3.x - 0.03D);
        } else {
            d0 = Math.min(0.5D, vec3.x + 0.04D);
        }

        entity.setDeltaMovement(d0, vec3.y, vec3.z);
        entity.resetFallDistance();
    }

    public void onInsideWestBubbleColumn(boolean isDragDown, Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0;
        if (isDragDown) {
            d0 = Math.max(0.3D, vec3.x + 0.03D);
        } else {
            d0 = Math.min(-0.5D, vec3.x - 0.04D);
        }

        entity.setDeltaMovement(d0, vec3.y, vec3.z);
        entity.resetFallDistance();
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        WarpPipeBlockEntity pipeBlockEntity = (WarpPipeBlockEntity) serverWorld.getBlockEntity(pos);
        Direction facing = state.getValue(FACING);

        if (pipeBlockEntity != null) {
            if (facing == Direction.UP) {
                PipeBubblesBlock.repeatColumnUp(serverWorld, pos, state, serverWorld.getBlockState(pos.below()), pipeBlockEntity.bubblesDistance);
            } else if (facing == Direction.DOWN) {
                PipeBubblesBlock.repeatColumnDown(serverWorld, pos, state, serverWorld.getBlockState(pos.above()), pipeBlockEntity.bubblesDistance);
            } else if (facing == Direction.NORTH) {
                PipeBubblesBlock.repeatColumnNorth(serverWorld, pos, state, serverWorld.getBlockState(pos.south()), pipeBlockEntity.bubblesDistance);
            } else if (facing == Direction.SOUTH) {
                PipeBubblesBlock.repeatColumnSouth(serverWorld, pos, state, serverWorld.getBlockState(pos.north()), pipeBlockEntity.bubblesDistance);
            } else if (facing == Direction.EAST) {
                PipeBubblesBlock.repeatColumnEast(serverWorld, pos, state, serverWorld.getBlockState(pos.west()), pipeBlockEntity.bubblesDistance);
            } else if (facing == Direction.WEST) {
                PipeBubblesBlock.repeatColumnWest(serverWorld, pos, state, serverWorld.getBlockState(pos.east()), pipeBlockEntity.bubblesDistance);
            }
        }
        else PipeBubblesBlock.repeatColumnUp(serverWorld, pos, state, serverWorld.getBlockState(pos.below()), 0);
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state, int bubblesDistance) {
        repeatColumnUp(worldAccessor, pos, worldAccessor.getBlockState(pos), state, bubblesDistance);
    }

    public static void repeatColumnDown(LevelAccessor worldAccessor, BlockPos pos, BlockState state, int bubblesDistance) {
        repeatColumnDown(worldAccessor, pos, worldAccessor.getBlockState(pos), state, bubblesDistance);
    }

    public static void repeatColumnNorth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, int bubblesDistance) {
        repeatColumnNorth(worldAccessor, pos, worldAccessor.getBlockState(pos), state, bubblesDistance);
    }

    public static void repeatColumnSouth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, int bubblesDistance) {
        repeatColumnSouth(worldAccessor, pos, worldAccessor.getBlockState(pos), state, bubblesDistance);
    }

    public static void repeatColumnEast(LevelAccessor worldAccessor, BlockPos pos, BlockState state, int bubblesDistance) {
        repeatColumnEast(worldAccessor, pos, worldAccessor.getBlockState(pos), state, bubblesDistance);
    }

    public static void repeatColumnWest(LevelAccessor worldAccessor, BlockPos pos, BlockState state, int bubblesDistance) {
        repeatColumnWest(worldAccessor, pos, worldAccessor.getBlockState(pos), state, bubblesDistance);
    }

    public static void repeatColumnUp(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState, int bubblesDistance) {
        if (PipeBubblesBlock.canExistIn(worldAccessor, pos) && bubblesDistance != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.UP);
            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos);

            worldAccessor.setBlock(pos, pipeColumnState, 2);

            // Used 3 - 1 since this somehow places one more block than intended
            while (PipeBubblesBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < bubblesDistance - 1) {

                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.UP);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }

    public static void repeatColumnDown(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState, int bubblesDistance) {
        if (PipeBubblesBlock.canExistIn(worldAccessor, pos) && bubblesDistance != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.DOWN);
            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos);

            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < bubblesDistance - 1) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.DOWN);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }

    public static void repeatColumnNorth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState, int bubblesDistance) {
        if (PipeBubblesBlock.canExistIn(worldAccessor, pos) && bubblesDistance != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.NORTH);
            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos);

            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < bubblesDistance - 1) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.NORTH);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }

    public static void repeatColumnSouth(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState, int bubblesDistance) {
        if (PipeBubblesBlock.canExistIn(worldAccessor, pos) && bubblesDistance != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.SOUTH);
            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos);

            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < bubblesDistance - 1) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.SOUTH);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }

    public static void repeatColumnEast(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState, int bubblesDistance) {
        if (PipeBubblesBlock.canExistIn(worldAccessor, pos) && bubblesDistance != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.EAST);
            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos);

            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < bubblesDistance - 1) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.EAST);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }

    public static void repeatColumnWest(LevelAccessor worldAccessor, BlockPos pos, BlockState state, BlockState neighborState, int bubblesDistance) {
        if (PipeBubblesBlock.canExistIn(worldAccessor, pos) && bubblesDistance != 0) {
            int initialDistance = 0;
            BlockPos.MutableBlockPos mutablePos = pos.mutable().move(Direction.WEST);
            BlockState pipeColumnState = PipeBubblesBlock.setBlockState(neighborState, worldAccessor, pos);

            worldAccessor.setBlock(pos, pipeColumnState, 2);

            while (PipeBubblesBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < bubblesDistance - 1) {
                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.WEST);
                initialDistance++;
                pipeColumnState = PipeBubblesBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }
}
