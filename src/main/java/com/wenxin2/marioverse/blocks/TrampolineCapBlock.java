package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrampolineCapBlock extends MushroomBlock implements BonemealableBlock, ToggleableBlock {
    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 10.0, 12.0);

    public TrampolineCapBlock(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature, Properties properties) {
        super(configuredFeature, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(RedDottedLineBlock.ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RedDottedLineBlock.ACTIVE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        final Vec3 vec3 = state.getOffset(blockGetter, pos);

        return SHAPE.move(vec3.x, vec3.y, vec3.z);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.getStateForPlacementSavedData(this.defaultBlockState(), placeContext);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        this.onPlaceSavedData(level, pos);
        super.onPlace(state, level, pos, oldState, moved);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        this.onRemoveSavedData(level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos posBelow = pos.below();
        BlockState stateBelow = levelReader.getBlockState(posBelow);

        return stateBelow.isSolidRender(levelReader, pos) || state.getBlock() instanceof FarmBlock;
    }
}