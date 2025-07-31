package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import com.wenxin2.marioverse.blocks.states.QuadrantBlockStates;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StarCoinBlock extends CoinBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<QuadrantBlockStates> QUADRANT = EnumProperty.create("quadrant", QuadrantBlockStates.class);

    protected static final VoxelShape LOWER_NORTH_WEST = Block.box(5.0, 3.5, 5.0, 27.0, 25.5, 27.0).optimize();
    protected static final VoxelShape LOWER_NORTH_EAST = Block.box(-11.0, 3.5, 5.0, 11.0, 25.5, 27.0).optimize();
    protected static final VoxelShape LOWER_SOUTH_WEST = Block.box(5.0, 3.5, -11.0, 27.0, 25.5, 11.0).optimize();
    protected static final VoxelShape LOWER_SOUTH_EAST = Block.box(-11.0, 3.5, -11.0, 11.0, 25.5, 11.0).optimize();
    protected static final VoxelShape UPPER_NORTH_WEST = Block.box(5.0,  -12.0, 5.0, 27.0, 10.0, 27.0).optimize();
    protected static final VoxelShape UPPER_NORTH_EAST = Block.box(-11.0,  -12.0, 5.0, 11.0, 10.0, 27.0).optimize();
    protected static final VoxelShape UPPER_SOUTH_WEST = Block.box(5.0,  -12.0, -11.0, 27.0, 10.0, 11.0).optimize();
    protected static final VoxelShape UPPER_SOUTH_EAST = Block.box(-11.0,  -12.0, -11.0, 11.0, 10.0, 11.0).optimize();

    public StarCoinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(FACING, HALF, QUADRANT, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        DoubleBlockHalf half = state.getValue(HALF);
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        Direction facing = state.getValue(FACING);

        VoxelShape baseShape;

        if (half == DoubleBlockHalf.LOWER) {
            baseShape = switch (quadrant) {
                case NORTH_EAST -> LOWER_NORTH_EAST;
                case SOUTH_WEST -> LOWER_SOUTH_WEST;
                case SOUTH_EAST -> LOWER_SOUTH_EAST;
                default -> LOWER_NORTH_WEST;
            };
        } else {
            baseShape = switch (quadrant) {
                case NORTH_EAST -> UPPER_NORTH_EAST;
                case SOUTH_WEST -> UPPER_SOUTH_WEST;
                case SOUTH_EAST -> UPPER_SOUTH_EAST;
                default -> UPPER_NORTH_WEST;
            };
        }

        if (facing == Direction.NORTH)
            return baseShape;

        VoxelShape rotated = Shapes.empty();
        for (AABB box : baseShape.toAabbs()) {
            AABB rotatedBox = switch (facing) {
                case EAST -> new AABB(
                        1.0 - box.maxZ, box.minY, box.minX,
                        1.0 - box.minZ, box.maxY, box.maxX
                );
                case SOUTH -> new AABB(
                        1.0 - box.maxX, box.minY, 1.0 - box.maxZ,
                        1.0 - box.minX, box.maxY, 1.0 - box.minZ
                );
                case WEST -> new AABB(
                        box.minZ, box.minY, 1.0 - box.maxX,
                        box.maxZ, box.maxY, 1.0 - box.minX
                );
                default -> box;
            };
            rotated = Shapes.or(rotated, Shapes.create(rotatedBox));
        }

        return rotated;
    }

    @NotNull
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StarCoinBlockEntity(pos, state);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        ItemStack coinItem = new ItemStack(this.asItem());

        if (entity instanceof KoopaShellEntity koopaShell && koopaShell.getOwner() != null
                && koopaShell.getOwner().getType().is(TagRegistry.CAN_COLLECT_COINS))
            StarCoinBlock.collectCoin(this, world, state, pos, koopaShell.getOwner(), coinItem);
        else if (entity.getType().is(TagRegistry.CAN_COLLECT_COINS) && ConfigRegistry.STAR_COINS_COLLECTED_ON_COLLISION.get()) {
            if (entity instanceof Player player && player.isCreative() && !ConfigRegistry.STAR_COINS_COLLECTED_IN_CREATIVE.get())
                return;
            StarCoinBlock.collectCoin(this, world, state, pos, entity, coinItem);
        }
    }

    public static void collectCoin(StarCoinBlock starCoinBlock, Level world, BlockState state, BlockPos pos, Entity entity, ItemStack coinItem) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        boolean itemAdded = false;

        starCoinBlock.removeCoinParts(world, pos, half, quadrant, facing, true, false, false);
        world.playSound(null, pos, SoundRegistry.STAR_COIN_PICKUP.get(), SoundSource.BLOCKS);

        if (entity instanceof Player player) {
            itemAdded = player.addItem(coinItem);

            if (!itemAdded)
                player.drop(coinItem, false);

            if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                PiglinAi.angerNearbyPiglins(player, false);
        } else if (entity instanceof LivingEntity livingEntity && livingEntity.getMainHandItem().isEmpty()) {
            livingEntity.setItemInHand(InteractionHand.MAIN_HAND, coinItem);
            itemAdded = true;
        } else if (entity instanceof LivingEntity livingEntity && livingEntity.getOffhandItem().isEmpty()) {
            livingEntity.setItemInHand(InteractionHand.OFF_HAND, coinItem);
            itemAdded = true;
        } else if (entity instanceof InventoryCarrier carrier) {
            SimpleContainer inventory = carrier.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (inventory.getItem(i).isEmpty()) {
                    inventory.setItem(i, coinItem);
                    itemAdded = true;
                    break;
                }
            }
        } else if (entity instanceof Container container) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                if (container.getItem(i).isEmpty()) {
                    container.setItem(i, coinItem);
                    itemAdded = true;
                    break;
                }
            }
        }

        if (!itemAdded)
            entity.spawnAtLocation(coinItem);
    }

    @Override
    public boolean canSurvive(BlockState state, @NotNull LevelReader world, BlockPos pos) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        BlockPos partPos = this.getPartPos(pos.above(), quadrant, half, facing);

        return this.canCoinPartsPlace(world, partPos, facing) || this.areCoinPartsValid(world, partPos, facing);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final Level world = context.getLevel();
        final FluidState fluidState = world.getFluidState(pos);
        final boolean waterlogged = fluidState.getType() == Fluids.WATER;

        Direction facing = context.getHorizontalDirection().getOpposite();

        return this.defaultBlockState().setValue(FACING, facing).setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST).setValue(WATERLOGGED, waterlogged);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            Direction facing = entity.getDirection().getOpposite();
            BlockState baseState = state.setValue(FACING, facing);

            // Lower half
            world.setBlock(pos, baseState.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
                    .setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER), 3);

            world.setBlock(pos.relative(facing.getClockWise()), baseState.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
                    .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getClockWise())).getType() == Fluids.WATER), 3);

            world.setBlock(pos.relative(facing.getOpposite()), baseState.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
                    .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getOpposite())).getType() == Fluids.WATER), 3);

            world.setBlock(pos.relative(facing.getOpposite()).relative(facing.getClockWise()), baseState.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
                    .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getOpposite()).relative(facing.getClockWise())).getType() == Fluids.WATER), 3);

            // Upper half
            world.setBlock(pos.above(), baseState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
                    .setValue(WATERLOGGED, world.getFluidState(pos.above()).getType() == Fluids.WATER), 3);

            world.setBlock(pos.relative(facing.getClockWise()).above(), baseState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
                    .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getClockWise()).above()).getType() == Fluids.WATER), 3);

            world.setBlock(pos.relative(facing.getOpposite()).above(), baseState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
                    .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getOpposite()).above()).getType() == Fluids.WATER), 3);

            world.setBlock(pos.relative(facing.getOpposite()).relative(facing.getClockWise()).above(), baseState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
                    .setValue(WATERLOGGED, world.getFluidState(pos.relative(facing.getOpposite()).relative(facing.getClockWise()).above()).getType() == Fluids.WATER), 3);
        }
    }

    @Override
    public void destroy(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        if (!worldAccessor.isClientSide() && worldAccessor instanceof Level world)
            this.removeCoinParts(world, pos, half, quadrant, facing, true, true, true);
        super.destroy(worldAccessor, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        Direction facing = state.getValue(FACING);
        if (!world.isClientSide) {
            if (player.isCreative() || !player.hasCorrectToolForDrops(state, world, pos))
                this.removeCoinParts(world, pos, half, quadrant, facing, false, false, true);
            else this.removeCoinParts(world, pos, half, quadrant, facing, true, true, true);
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    public BlockPos getPartPos(BlockPos pos, QuadrantBlockStates quadrant, DoubleBlockHalf half, Direction direction) {
        BlockPos base = switch (quadrant) {
            case NORTH_WEST -> pos;
            case NORTH_EAST -> pos.relative(direction.getClockWise());
            case SOUTH_WEST -> pos.relative(direction.getOpposite());
            case SOUTH_EAST -> pos.relative(direction.getClockWise()).relative(direction.getOpposite());
        };

        return half == DoubleBlockHalf.UPPER ? base.below() : base;
    }

    private boolean areCoinPartsValid(LevelReader world, BlockPos pos, Direction facing) {
        return world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_WEST, DoubleBlockHalf.LOWER, facing)).is(this)
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_EAST, DoubleBlockHalf.LOWER, facing)).is(this)
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_WEST, DoubleBlockHalf.LOWER, facing)).is(this)
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_EAST, DoubleBlockHalf.LOWER, facing)).is(this)
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_WEST, DoubleBlockHalf.UPPER, facing)).is(this)
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_EAST, DoubleBlockHalf.UPPER, facing)).is(this)
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_WEST, DoubleBlockHalf.UPPER, facing)).is(this)
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_EAST, DoubleBlockHalf.UPPER, facing)).is(this);
    }

    private boolean canCoinPartsPlace(LevelReader world, BlockPos pos, Direction facing) {
        return world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_WEST, DoubleBlockHalf.LOWER, facing)).canBeReplaced()
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_EAST, DoubleBlockHalf.LOWER, facing)).canBeReplaced()
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_WEST, DoubleBlockHalf.LOWER, facing)).canBeReplaced()
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_EAST, DoubleBlockHalf.LOWER, facing)).canBeReplaced()
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_WEST, DoubleBlockHalf.UPPER, facing)).canBeReplaced()
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.NORTH_EAST, DoubleBlockHalf.UPPER, facing)).canBeReplaced()
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_WEST, DoubleBlockHalf.UPPER, facing)).canBeReplaced()
                && world.getBlockState(this.getPartPos(pos, QuadrantBlockStates.SOUTH_EAST, DoubleBlockHalf.UPPER, facing)).canBeReplaced();
    }

    public void removeCoinParts(Level world, BlockPos pos, DoubleBlockHalf half, QuadrantBlockStates quadrant, Direction facing,
                                boolean spawnParticles, boolean dropResources, boolean destroyBlock) {
        BlockPos basePos = (half == DoubleBlockHalf.UPPER) ? pos.below() : pos;
        for (QuadrantBlockStates quadrants : QuadrantBlockStates.values()) {
            for (Direction directions : Direction.Plane.HORIZONTAL) {
                for (DoubleBlockHalf halves : DoubleBlockHalf.values()) {

                    if (quadrant == quadrants && facing == directions && half == halves) {
                        Direction right = directions.getClockWise();
                        Direction left = directions.getCounterClockWise();
                        Direction back = directions.getOpposite();

                        Direction axis1 = switch (quadrants) {
                            case NORTH_WEST, NORTH_EAST -> back;
                            case SOUTH_WEST, SOUTH_EAST -> directions;
                        };

                        Direction axis2 = switch (quadrants) {
                            case NORTH_WEST, SOUTH_WEST -> right;
                            case NORTH_EAST, SOUTH_EAST -> left;
                        };

                        for (int x = 0; x <= 1; x++) {
                            for (int z = 0; z <= 1; z++) {
                                BlockPos targetPos = basePos.relative(axis1, x).relative(axis2, z);

                                if (destroyBlock) {
                                    world.destroyBlock(targetPos, dropResources);
                                    world.destroyBlock(targetPos.above(), dropResources);
                                } else {
                                    world.removeBlock(targetPos, false);
                                    world.removeBlock(targetPos.above(), false);
                                }

                                if (world instanceof ServerLevel serverWorld && spawnParticles) {
                                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, targetPos, UniformInt.of(1, 1));
                                    ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, targetPos.above(), UniformInt.of(1, 1));
                                } else {
                                    world.levelEvent(2001, targetPos, Block.getId(world.getBlockState(targetPos)));
                                    world.levelEvent(2001, targetPos.above(), Block.getId(world.getBlockState(targetPos.above())));
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
