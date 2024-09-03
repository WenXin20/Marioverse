package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.Properties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CoinBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final int MAX = RotationSegment.getMaxSegmentIndex();
    private static final int ROTATIONS = MAX + 1;

    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);
    protected static final VoxelShape ANGLED = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

    public CoinBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, Boolean.FALSE).setValue(ROTATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(ROTATION, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        FluidState fluidState = placeContext.getLevel().getFluidState(placeContext.getClickedPos());

        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(FluidTags.WATER) && fluidState.getAmount() == 8)
                .setValue(ROTATION, RotationSegment.convertToSegment(placeContext.getRotation()));
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 1);
        }
        super.onPlace(state, world, pos, oldState, notify);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockNeighbor, BlockPos posNeighbor, boolean isMoving) {
        if (!world.isClientSide()) {
            world.scheduleTick(pos, this, 1);
        }
        super.neighborChanged(state, world, pos, blockNeighbor, posNeighbor, isMoving);
    }

    @NotNull
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor worldAccessor, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            worldAccessor.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(worldAccessor));
        }

        return super.updateShape(state, direction, neighborState, worldAccessor, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverWorld, BlockPos pos, RandomSource random) {
        int currentRotation = state.getValue(ROTATION);
        int nextRotation = (currentRotation + 1) % 16; // Cycle through all 16 rotation states

        serverWorld.setBlock(pos, state.setValue(ROTATION, nextRotation), 3);

        serverWorld.scheduleTick(pos, this, 1);
        super.tick(state, serverWorld, pos, random);
    }

    @NotNull
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), ROTATIONS));
    }

    @NotNull
    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), ROTATIONS));
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        ItemStack coinItem = new ItemStack(this.asItem());

        if (entity instanceof Player player) {
            world.removeBlock(pos, Boolean.TRUE);
            player.addItem(coinItem);

            if (!player.addItem(coinItem)) {
                player.drop(coinItem, false);
            }
        }
        super.entityInside(state, world, pos, entity);
    }

    @NotNull
    @Override
    public FluidState getFluidState(final BlockState state)
    {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
