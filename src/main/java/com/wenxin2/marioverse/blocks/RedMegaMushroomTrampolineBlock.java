package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.jetbrains.annotations.NotNull;

public class RedMegaMushroomTrampolineBlock extends MegaMushroomTrampolineBlock implements ToggleableBlock {
    public static final BooleanProperty ACTIVE = BlockStatePropertyRegistry.ACTIVE;

    public RedMegaMushroomTrampolineBlock(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature,
                                          ResourceKey<ConfiguredFeature<?, ?>> wideFeature, Properties properties) {
        super(configuredFeature, wideFeature, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true)
                .setValue(TOP, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE, TOP);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState stateAbove = context.getLevel().getBlockState(context.getClickedPos().above());
        return this.getStateForPlacementSavedData(this.defaultBlockState().setValue(TOP, !stateAbove.is(this)), context);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.onPlaceSavedData(level, pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        this.onRemoveSavedData(level, pos);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        float distance = state.getValue(ACTIVE) ? fallDistance * 0.5F : fallDistance;
        super.fallOn(level, state, pos, entity, distance);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockPos pos = entity.getOnPos();
        BlockState state = blockGetter.getBlockState(pos);

        if (!entity.isSuppressingBounce() && state.getValue(ACTIVE))
            this.bounceUp(entity);
    }
}
