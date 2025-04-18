package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.StarCoinBlockEntity;
import com.wenxin2.marioverse.blocks.states.QuadrantBlockStates;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
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
    protected static final VoxelShape LOWER_NORTH_EAST = Block.box(-11.0, 3.5, 5.0, 11.0, 25.5, 27.0).optimize();
    protected static final VoxelShape LOWER_SOUTH_WEST = Block.box(5.0, 3.5, -11.0, 27.0, 25.5, 11.0).optimize();
    protected static final VoxelShape LOWER_SOUTH_EAST = Block.box(-11.0, 3.5, -11.0, 11.0, 25.5, 11.0).optimize();
    protected static final VoxelShape UPPER_NORTH_WEST = Block.box(5.0,  -12.0, 5.0, 27.0, 10.0, 27.0).optimize();
    protected static final VoxelShape UPPER_NORTH_EAST = Block.box(-11.0,  -12.0, 5.0, 11.0, 10.0, 27.0).optimize();
    protected static final VoxelShape UPPER_SOUTH_WEST = Block.box(5.0,  -12.0, -11.0, 27.0, 10.0, 11.0).optimize();
    protected static final VoxelShape UPPER_SOUTH_EAST = Block.box(-11.0,  -12.0, -11.0, 11.0, 10.0, 11.0).optimize();

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
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {

            if (state.getValue(QUADRANT) == QuadrantBlockStates.NORTH_EAST)
                return LOWER_NORTH_EAST;
            else if (state.getValue(QUADRANT) == QuadrantBlockStates.SOUTH_WEST)
                return LOWER_SOUTH_WEST;
            else if (state.getValue(QUADRANT) == QuadrantBlockStates.SOUTH_EAST)
                return LOWER_SOUTH_EAST;
            else return LOWER_NORTH_WEST;

        } else if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {

            if (state.getValue(QUADRANT) == QuadrantBlockStates.NORTH_EAST)
                return UPPER_NORTH_EAST;
            else if (state.getValue(QUADRANT) == QuadrantBlockStates.SOUTH_WEST)
                return UPPER_SOUTH_WEST;
            else if (state.getValue(QUADRANT) == QuadrantBlockStates.SOUTH_EAST)
                return UPPER_SOUTH_EAST;
            else return UPPER_NORTH_WEST;

        } else return LOWER_NORTH_WEST;
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
                && koopaShell.getOwner().getType().is(TagRegistry.CAN_PICK_UP_COINS))
            this.collectCoin(state, world, pos, koopaShell.getOwner(), coinItem);
        else if (entity.getType().is(TagRegistry.CAN_PICK_UP_COINS))
            this.collectCoin(state, world, pos, entity, coinItem);
    }

    @Override
    public void collectCoin(BlockState state, Level world, BlockPos pos, Entity entity, ItemStack coinItem) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos partPos = this.getPartPos(pos, quadrant, half);
        boolean itemAdded = false;

        world.playSound(null, pos, SoundRegistry.STAR_COIN_PICKUP.get(), SoundSource.BLOCKS);
        removeCoinPartsWithParticles(world, partPos, entity);

        if (entity instanceof Player player) {
            itemAdded = player.addItem(coinItem);

            if (!itemAdded)
                player.drop(coinItem, false);

            if (state.is(BlockTags.GUARDED_BY_PIGLINS))
                PiglinAi.angerNearbyPiglins(player, false);
        } else if (entity instanceof LivingEntity livingEntity && livingEntity.getMainHandItem().isEmpty()) {
            livingEntity.setItemInHand(InteractionHand.MAIN_HAND, coinItem);
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
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos basePos = this.getPartPos(pos, quadrant, half);

        return areCoinPartsPlaced(world, basePos) || areCoinPartsValid(world, basePos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockPos pos = context.getClickedPos();
        final Level world = context.getLevel();
        final FluidState fluidState = world.getFluidState(pos);
        final boolean waterlogged = fluidState.getType() == Fluids.WATER;

        BlockPos northWestPos = pos.relative(Direction.NORTH).relative(Direction.WEST);

        if (!canPlaceBlock(world, northWestPos)
                ||!canPlaceBlock(world, northWestPos.relative(Direction.EAST))
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
        if (state.getValue(WATERLOGGED))
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        if (entity != null) {
            BlockPos northWestPos = pos.relative(Direction.NORTH).relative(Direction.WEST);

            if (canPlaceBlock(world, northWestPos)
                    && canPlaceBlock(world, northWestPos.relative(Direction.EAST))
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH))
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST))
                    && canPlaceBlock(world, northWestPos.above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.EAST).above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).above())
                    && canPlaceBlock(world, northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above())) {

                world.setBlock(northWestPos, state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.EAST), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.NORTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.EAST)).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_WEST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH)).getType() == Fluids.WATER), 3);

                world.setBlock(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST), state.setValue(HALF, DoubleBlockHalf.LOWER).setValue(QUADRANT, QuadrantBlockStates.SOUTH_EAST)
                        .setValue(WATERLOGGED, world.getFluidState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST)).getType() == Fluids.WATER), 3);

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

    @Override
    protected void onRemove(BlockState oldState, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        QuadrantBlockStates quadrant = oldState.getValue(QUADRANT);
        DoubleBlockHalf half = oldState.getValue(HALF);
        BlockPos partPos = this.getPartPos(pos, quadrant, half);
        if (!world.isClientSide() && !(newState.getBlock() instanceof StarCoinBlock)) {
            world.levelEvent(2001, partPos, Block.getId(world.getBlockState(partPos)));
            this.removeCoinPartsWithParticles(world, partPos, null);
        }
        super.onRemove(oldState, world, pos, newState, isMoving);
    }

    @Override
    public void destroy(LevelAccessor worldAccessor, BlockPos pos, BlockState state) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos partPos = this.getPartPos(pos, quadrant, half);
        if (!worldAccessor.isClientSide()) {
            worldAccessor.levelEvent(2001, partPos, Block.getId(worldAccessor.getBlockState(partPos)));
            this.removeCoinParts(worldAccessor, partPos);
        }
        super.destroy(worldAccessor, pos, state);
    }

    @NotNull
    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        DoubleBlockHalf half = state.getValue(HALF);
        BlockPos partPos = this.getPartPos(pos, quadrant, half);
        if (!world.isClientSide) {
            if (player.isCreative() || !player.hasCorrectToolForDrops(state, world, pos)) {
                world.levelEvent(player, 2001, partPos, Block.getId(world.getBlockState(partPos)));
                this.removeCoinPartsWithParticles(world, partPos, player);
            }
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    private boolean canPlaceBlock(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        return (state.isAir() || state.canBeReplaced() || state.is(this))
                && world.getWorldBorder().isWithinBounds(pos);
    }

    public BlockPos getPartPos(BlockPos pos, QuadrantBlockStates quadrant, DoubleBlockHalf half) {
        BlockPos base = switch (quadrant) {
            case NORTH_WEST -> pos;
            case NORTH_EAST -> pos.west();
            case SOUTH_WEST -> pos.north();
            case SOUTH_EAST -> pos.north().west();
        };

        return (half == DoubleBlockHalf.UPPER) ? base.below() : base;
    }

    private boolean areCoinPartsValid(LevelReader world, BlockPos partPos) {
        return world.getBlockState(partPos).is(this)
                && world.getBlockState(partPos.east()).is(this)
                && world.getBlockState(partPos.south()).is(this)
                && world.getBlockState(partPos.south().east()).is(this)
                && world.getBlockState(partPos.above()).is(this)
                && world.getBlockState(partPos.east().above()).is(this)
                && world.getBlockState(partPos.south().above()).is(this)
                && world.getBlockState(partPos.south().east().above()).is(this);
    }

    private boolean areCoinPartsPlaced(LevelReader world, BlockPos partPos) {
        BlockPos northWestPos = partPos.relative(Direction.NORTH).relative(Direction.WEST);

        return world.getBlockState(northWestPos.relative(Direction.EAST)).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH)).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST)).canBeReplaced()
                && world.getBlockState(northWestPos.above()).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.EAST).above()).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH).above()).canBeReplaced()
                && world.getBlockState(northWestPos.relative(Direction.SOUTH).relative(Direction.EAST).above()).canBeReplaced();
    }

    private void removeCoinPartsWithParticles(Level world, BlockPos pos, @Nullable Entity entity) {
        BlockPos[] positions = {
                pos, pos.east(), pos.south(), pos.south().east(),
                pos.above(), pos.east().above(), pos.south().above(), pos.south().east().above()
        };

        for (BlockPos partPos : positions) {
            if (world instanceof ServerLevel serverWorld && entity != null)
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverWorld, partPos, UniformInt.of(1, 1));
            world.removeBlock(partPos, false);
        }
    }

    private void removeCoinParts(LevelAccessor world, BlockPos pos) {
        BlockPos[] positions = {
                pos, pos.east(), pos.south(), pos.south().east(),
                pos.above(), pos.east().above(), pos.south().above(), pos.south().east().above()
        };

        for (BlockPos partPos : positions) {
            world.destroyBlock(partPos, false);
        }
    }

    protected void preventDropFromParts(Level world, BlockPos pos, BlockState state, Player player) {
        DoubleBlockHalf half = state.getValue(HALF);
        QuadrantBlockStates quadrant = state.getValue(QUADRANT);
        BlockPos partPos = this.getPartPos(pos, quadrant, half);

        if (!world.isClientSide) {
            if (areCoinPartsValid(world, partPos)) {
                replaceWithCorrectFluidState(world, partPos, player);
                replaceWithCorrectFluidState(world, partPos.east(), player);
                replaceWithCorrectFluidState(world, partPos.south(), player);
                replaceWithCorrectFluidState(world, partPos.south().east(), player);
                replaceWithCorrectFluidState(world, partPos.above(), player);
                replaceWithCorrectFluidState(world, partPos.east().above(), player);
                replaceWithCorrectFluidState(world, partPos.south().above(), player);
                replaceWithCorrectFluidState(world, partPos.south().east().above(), player);
            }
        }
    }

    private void replaceWithCorrectFluidState(Level world, BlockPos pos, Player player) {
        BlockState currentState = world.getBlockState(pos);
        BlockState replacementState = currentState.getFluidState().is(Fluids.WATER)
                ? Blocks.WATER.defaultBlockState()
                : Blocks.AIR.defaultBlockState();

        world.levelEvent(player, 2001, pos, Block.getId(currentState));
        world.setBlock(pos, replacementState, 35);
    }
}
