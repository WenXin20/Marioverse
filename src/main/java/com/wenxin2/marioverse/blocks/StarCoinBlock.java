package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import com.wenxin2.marioverse.blocks.states.QuadrantBlockStates;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StarCoinBlock extends CoinBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<QuadrantBlockStates> QUADRANT = EnumProperty.create("quadrant", QuadrantBlockStates.class);

    protected static final VoxelShape LOWER_NORTH_WEST = Block.box(5.0, 3.5, 5.0, 27.0, 25.5, 27.0).optimize();
    protected static final VoxelShape LOWER_NORTH_EAST = Block.box(-11.0, 3.5, 5.0, 16.0, 25.5, 27.0).optimize();

    public StarCoinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST).setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(HALF, QUADRANT, WATERLOGGED);
    }

    @NotNull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        if (state.getValue(QUADRANT) == QuadrantBlockStates.NORTH_EAST)
            return LOWER_NORTH_EAST;
        else return LOWER_NORTH_WEST;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StarCoinBlockEntity(pos, state);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {

        ItemStack coinItem = new ItemStack(this.asItem());

        if (entity instanceof Player player) {
            QuadrantBlockStates quadrant = state.getValue(QUADRANT);
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos basePos = getPartPos(pos, quadrant, half);
            BlockPos basePosAbove = getPartPos(pos.above(), quadrant, half);

            world.playSound(player, pos, SoundRegistry.STAR_COIN_PICKUP.get(), SoundSource.BLOCKS);
            removeStructure(world, basePos);
            removeStructure(world, basePosAbove);
            player.addItem(coinItem);

            if (!player.addItem(coinItem))
                player.drop(coinItem, false);

            if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                PiglinAi.angerNearbyPiglins(player, false);
        }
        super.entityInside(state, world, pos, entity);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos basePos = getPartPos(pos, quadrant, half);

        // Check if structure is already placed OR if placement is possible
        return isStructureBeingPlaced(world, basePos) || isStructureValid(world, basePos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final Level world = context.getLevel();
        final FluidState fluidState = world.getFluidState(pos);
        final boolean waterlogged = fluidState.getType() == Fluids.WATER;
        DoubleBlockHalf half = (pos.getY() % 2 == 0) ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;

        BlockPos northWestPos = pos.relative(Direction.NORTH).relative(Direction.WEST);

        if (!canPlaceBlock(world, northWestPos.relative(Direction.EAST))
                || !canPlaceBlock(world, northWestPos.relative(Direction.SOUTH))
                || !canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST))
                || !canPlaceBlock(world, northWestPos.above())
                || !canPlaceBlock(world, northWestPos.relative(Direction.EAST).above())
                || !canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).above())
                || !canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above())) {
            return null;
        }
        return this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST).setValue(WATERLOGGED, waterlogged);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf half = state.getValue(HALF);
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);

        if ((direction == Direction.UP && half == DoubleBlockHalf.LOWER) ||
                (direction == Direction.DOWN && half == DoubleBlockHalf.UPPER)) {
            if (!neighborState.is(this))
                return state.getValue(WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            BlockPos northWestPos = pos.relative(Direction.NORTH).relative(Direction.WEST);

            if (canPlaceBlock(world, northWestPos.relative(Direction.EAST))
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH))
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST))
                    && canPlaceBlock(world, northWestPos.above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.EAST).above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above())) {

                // Lower half
                world.setBlock(northWestPos, state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.EAST), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.EAST)).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH)).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST)).getType() == Fluids.WATER), 3);

                // Upper half
                world.setBlock(northWestPos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.above()).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.EAST).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.EAST).above()).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH).above()).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above(), state.setValue(HALF, DoubleBlockHalf.UPPER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above()).getType() == Fluids.WATER), 3);
            }
        }
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        BlockPos neighborPos = (half == DoubleBlockHalf.LOWER) ? pos.above() : pos.below();
        BlockState neighborState = world.getBlockState(neighborPos);
        Direction facing = player.getDirection();
        BlockPos[] offsetNorthWest = getNorthWestPartPos(pos, facing);
        BlockPos[] offsetNorthEast = getNorthEastPartPos(pos, facing);

        if (!world.isClientSide) {
            if (player.isCreative())
                preventDropFromParts(world, pos, state, player);
            else dropResources(state, world, pos, null, player, player.getMainHandItem());

            for (BlockPos coinPartPos : offsetNorthWest) {
                BlockState partState = world.getBlockState(coinPartPos);

                if (partState.is(this)) {
                    boolean isWaterlogged = partState.getValue(WATERLOGGED);
                    world.setBlock(coinPartPos, isWaterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                    world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, coinPartPos, Block.getId(partState));
                }
            }

            for (BlockPos coinPartPos : offsetNorthEast) {
                BlockState partState = world.getBlockState(coinPartPos);

                if (partState.is(this)) {
                    boolean isWaterlogged = partState.getValue(WATERLOGGED);
                    world.setBlock(coinPartPos, isWaterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                    world.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, coinPartPos, Block.getId(partState));
                }
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    private boolean canPlaceBlock(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return (state.isAir() || state.canBeReplaced() || state.is(this))
                && world.getWorldBorder().isWithinBounds(pos);
    }

    private static BlockPos @NotNull [] getNorthWestPartPos(BlockPos pos, Direction facing) {
        return new BlockPos[]{
                pos.relative(facing.getClockWise()),
                pos.relative(facing, 1),
                pos.relative(facing.getClockWise()).relative(facing),
                pos.above(),
                pos.relative(facing.getClockWise()).above(),
                pos.relative(facing, 1).above(),
                pos.relative(facing.getClockWise()).relative(facing).above()
        };
    }

    private static BlockPos @NotNull [] getNorthEastPartPos(BlockPos pos, Direction facing) {
        return new BlockPos[]{
                pos.relative(facing.getCounterClockWise()),
                pos.relative(facing, 1),
                pos.relative(facing.getCounterClockWise()).relative(facing),
                pos.above(),
                pos.relative(facing.getCounterClockWise()).above(),
                pos.relative(facing, 1).above(),
                pos.relative(facing.getCounterClockWise()).relative(facing).above()
        };
    }

    private BlockPos getPartPos(BlockPos pos, QuadrantBlockStates quadrant, DoubleBlockHalf half) {
        // Adjust based on quadrant
        BlockPos base = switch (quadrant) {
            case NORTH_WEST -> pos;
            case NORTH_EAST -> pos.west();
            case SOUTH_WEST -> pos.north();
            case SOUTH_EAST -> pos.north().west();
        };

        // Adjust for upper half
        return (half == DoubleBlockHalf.UPPER) ? base.below() : base;
    }

    private boolean isStructureValid(LevelReader world, BlockPos partPos) {
        return world.getBlockState(partPos).is(this)
                && world.getBlockState(partPos.east()).is(this)
                && world.getBlockState(partPos.south()).is(this)
                && world.getBlockState(partPos.south().east()).is(this)
                && world.getBlockState(partPos.above()).is(this)
                && world.getBlockState(partPos.east().above()).is(this)
                && world.getBlockState(partPos.south().above()).is(this)
                && world.getBlockState(partPos.south().east().above()).is(this);
    }

    private boolean isStructureBeingPlaced(LevelReader world, BlockPos partPos) {
        BlockPos northWestPos = partPos.relative(Direction.NORTH).relative(Direction.WEST);

        return world.getBlockState(northWestPos.relative(Direction.EAST)).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH)).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST)).canBeReplaced()
                && world.getBlockState(northWestPos.above()).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.EAST).above()).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH).above()).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above()).canBeReplaced();
    }

    private void removeStructure(Level world, BlockPos basePos) {
        world.removeBlock(basePos, false);
        world.removeBlock(basePos.east(), false);
        world.removeBlock(basePos.south(), false);
        world.removeBlock(basePos.south().east(), false);
        world.removeBlock(basePos.above(), false);
        world.removeBlock(basePos.east().above(), false);
        world.removeBlock(basePos.south().above(), false);
        world.removeBlock(basePos.south().east().above(), false);

        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos, ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos.east(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos.south(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos.south().east(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos.above(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos.east().above(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos.south().above(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        ParticleUtils.spawnParticlesOnBlockFaces(world, basePos.south().east().above(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
    }

    protected static void preventDropFromParts(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (half == DoubleBlockHalf.LOWER) {
            BlockPos posAbove = pos.above();
            BlockState stateAbove = world.getBlockState(posAbove);

            if (stateAbove.is(state.getBlock()) && stateAbove.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockState stateAboveWaterOrAir = stateAbove.getFluidState().is(Fluids.WATER)
                        ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                world.setBlock(posAbove, stateAboveWaterOrAir, 35);
                world.levelEvent(player, 2001, posAbove, Block.getId(stateAbove));
            }
        }
    }
}
