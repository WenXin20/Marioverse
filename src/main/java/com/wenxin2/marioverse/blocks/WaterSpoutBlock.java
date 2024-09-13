package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import java.util.Optional;
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
import net.minecraft.world.item.DebugStickItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WaterSpoutBlock extends Block implements BucketPickup {
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    public static final VoxelShape SPOUT = Shapes.or(
            Block.box(5, 0, 5, 11, 16, 11).optimize());
    public static final VoxelShape SPOUT_TOP = Shapes.or(
            Block.box(5, 0, 5, 11, 12, 11).optimize());

    public WaterSpoutBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(TOP, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(TOP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext) context).getEntity() instanceof Player player) {
            if ((player.hasPermissions(1) && player.isCreative() && ConfigRegistry.DEBUG_WATER_SPOUT_SELECTION_BOX.get())
                    || (((player.getItemInHand(player.getUsedItemHand()).getItem() instanceof BucketItem
                        && ConfigRegistry.WATER_SPOUTS_BUCKETABLE.get())
                    || player.getItemInHand(player.getUsedItemHand()).getItem() instanceof DebugStickItem))) {
                if (state.getValue(TOP)) {
                    return SPOUT_TOP;
                } else return SPOUT;
            }
        }
        // Shapes.empty() causes a crash, use a tiny bounding box instead
        return Shapes.box(8, 8, 8, 8.00001, 8.00001, 8.00001);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
        BlockState stateAbove = worldAccessor.getBlockState(pos.above());
        BlockState stateBelow = worldAccessor.getBlockState(pos.below());

        if (stateBelow.getBlock() instanceof WarpPipeBlock
                && (stateBelow.getValue(WarpPipeBlock.CLOSED) || stateBelow.getValue(WarpPipeBlock.FACING) != Direction.UP
                || !stateBelow.getValue(WarpPipeBlock.WATER_SPOUT))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        if (stateBelow.getBlock() instanceof ClearWarpPipeBlock
                && (stateBelow.getValue(WarpPipeBlock.CLOSED) || stateBelow.getValue(WarpPipeBlock.FACING) != Direction.UP
                || !stateBelow.getValue(ClearWarpPipeBlock.WATERLOGGED) || !stateBelow.getValue(WarpPipeBlock.WATER_SPOUT))) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        if (!(stateBelow.getBlock() instanceof WarpPipeBlock) && !(stateBelow.getBlock() instanceof WaterSpoutBlock)) {
            worldAccessor.destroyBlock(pos, true);
            return Blocks.AIR.defaultBlockState();
        }

        if (stateAbove.getBlock() != BlockRegistry.WATER_SPOUT.get()) {
            return state.setValue(TOP, Boolean.TRUE);
        }
        else return state.setValue(TOP, Boolean.FALSE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldReader, BlockPos pos) {
        BlockState stateBelow = worldReader.getBlockState(pos.below());

        if (stateBelow.is(BlockRegistry.WATER_SPOUT.get()))
            return true;
        else if ((stateBelow.is(Blocks.WATER) && stateBelow.getFluidState().getAmount() >= 8 && stateBelow.getFluidState().isSource()))
            return true;
        else if (stateBelow.getBlock() instanceof WarpPipeBlock && stateBelow.getValue(WarpPipeBlock.FACING) == Direction.UP
                && (!stateBelow.getValue(WarpPipeBlock.CLOSED) && stateBelow.getValue(WarpPipeBlock.WATER_SPOUT))
                && !(stateBelow.getBlock() instanceof ClearWarpPipeBlock))
            return true;
        else if (stateBelow.getBlock() instanceof ClearWarpPipeBlock && stateBelow.getValue(WarpPipeBlock.FACING) == Direction.UP
                && (!stateBelow.getValue(WarpPipeBlock.CLOSED) && stateBelow.getValue(WarpPipeBlock.WATER_SPOUT))
                && stateBelow.getValue(ClearWarpPipeBlock.WATERLOGGED))
            return true;
        else return false;
    }

    public static boolean canExistIn(LevelAccessor worldAccessor, BlockPos pos) {
        return worldAccessor.getBlockState(pos).is(BlockRegistry.WATER_SPOUT.get()) ||  worldAccessor.getBlockState(pos).isAir();
    }

    public static BlockState setBlockState(BlockState state, LevelAccessor worldAccessor, BlockPos pos) {
        BlockState stateAbove = worldAccessor.getBlockState(pos.above());
        if (state.isAir() || state.is(BlockRegistry.WATER_SPOUT.get())
                || (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.WATER_SPOUT) && !state.getValue(WarpPipeBlock.CLOSED))) {
            if (state.is(BlockRegistry.WATER_SPOUT.get())) {
                if (!stateAbove.is(BlockRegistry.WATER_SPOUT.get()))
                    return state.setValue(TOP, Boolean.TRUE);
                else return state.setValue(TOP, Boolean.FALSE);
            } else if (state.getBlock() instanceof WarpPipeBlock && state.getValue(WarpPipeBlock.FACING) == Direction.UP
                    && state.getValue(WarpPipeBlock.WATER_SPOUT) && !state.getValue(WarpPipeBlock.CLOSED)) {
                if (!stateAbove.is(BlockRegistry.WATER_SPOUT.get()))
                    return BlockRegistry.WATER_SPOUT.get().defaultBlockState().setValue(TOP, Boolean.TRUE);
                else return BlockRegistry.WATER_SPOUT.get().defaultBlockState().setValue(TOP, Boolean.FALSE);
            }
        }
        return Blocks.AIR.defaultBlockState();
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
        double randomNum = -1 + random.nextDouble();
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        if (world.getBlockState(pos.above()).isAir()) {
            for (int i = 0; i < 75; ++i)
                this.addAlwaysVisibleParticles(world, ParticleTypes.SPLASH, x + random.nextDouble(),
                        y + 1, z + random.nextDouble(), randomNum, 0.04D, randomNum);
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
            } else this.onAboveUpBubbleCol(entity);
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
            this.onInsideUpBubbleColumn(entity);
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

    public void onAboveUpBubbleCol(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0 = Math.min(1.8D, vec3.y + 0.1D);

        entity.setDeltaMovement(vec3.x, d0, vec3.z);
    }

    public void onInsideUpBubbleColumn(Entity entity) {
        Vec3 vec3 = entity.getDeltaMovement();
        double d0 = Math.min(0.5D, vec3.y + 0.04D);

        entity.setDeltaMovement(vec3.x, d0 + 0.05D, vec3.z);
        entity.resetFallDistance();
    }

    @Override
    public void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        WarpPipeBlockEntity pipeBlockEntity = (WarpPipeBlockEntity) serverWorld.getBlockEntity(pos.below());
        if (pipeBlockEntity != null && pipeBlockEntity.getBlockState().getValue(WarpPipeBlock.WATER_SPOUT))
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
                if (stateAbove.getBlock() != BlockRegistry.WATER_SPOUT.get()) {
                    worldAccessor.setBlock(pos, pipeColumnState.setValue(TOP, Boolean.TRUE), 2);
                } else worldAccessor.setBlock(pos, pipeColumnState.setValue(TOP, Boolean.FALSE), 2);
            } else worldAccessor.setBlock(pos, pipeColumnState, 2);
            worldAccessor.scheduleTick(pos, BlockRegistry.WATER_SPOUT.get(), 3);

            // Used 4 - 1 since this somehow places one more block than intended
            while (WaterSpoutBlock.canExistIn(worldAccessor, mutablePos) && initialDistance < spoutHeight - 1) {

                if (!worldAccessor.setBlock(mutablePos, pipeColumnState, 2)) {
                    return;
                }
                mutablePos.move(Direction.UP);
                initialDistance++;
                pipeColumnState = WaterSpoutBlock.setBlockState(pipeColumnState, worldAccessor, mutablePos);
            }
        }
    }

    @Override
    public ItemStack pickupBlock(@Nullable Player player, LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        if (ConfigRegistry.WATER_SPOUTS_BUCKETABLE.get()) {
            worldAccessor.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return new ItemStack(Items.WATER_BUCKET);
        } else return ItemStack.EMPTY;
    }

    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }
}
