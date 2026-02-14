package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class RedDottedLineBlock extends Block implements ToggleableBlock {
    public static final MapCodec<RedDottedLineBlock> CODEC = simpleCodec(RedDottedLineBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    @NotNull
    @Override
    protected MapCodec<RedDottedLineBlock> codec() {
        return CODEC;
    }

    public RedDottedLineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(ACTIVE);
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (state.getValue(ACTIVE))
            return Shapes.block();
        return Shapes.empty();
    }

    @NotNull
    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (state.getValue(ACTIVE))
            return Shapes.block();
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (!state.getValue(ACTIVE))
            return 1.0F;
        return 0.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        if (state.getValue(ACTIVE))
            return false;
        return true;
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState neighborState, Direction direction) {
        return state.is(this) && neighborState.is(this)
                && !state.getValue(ACTIVE) && ! neighborState.getValue(ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.getStateForPlacementSavedData(this.defaultBlockState(), placeContext);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        this.onPlaceSavedData(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        this.onRemoveSavedData(level, pos);
    }
}
