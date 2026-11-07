package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class BlueMegaMushroomTrampolineBlock extends RedMegaMushroomTrampolineBlock {
    public BlueMegaMushroomTrampolineBlock(ResourceKey<ConfiguredFeature<?, ?>> configuredFeature,
                                           ResourceKey<ConfiguredFeature<?, ?>> wideFeature, Properties properties) {
        super(configuredFeature, wideFeature, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true)
                .setValue(TOP, true));
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        this.onPlaceSavedData(level, pos);
        if (oldState.is(this) && state.getValue(TOP) && oldState.getValue(ACTIVE) && !state.getValue(ACTIVE))
            this.launchEntities(level, pos);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        float distance = !state.getValue(ACTIVE) ? fallDistance * 0.5F : fallDistance;
        super.fallOn(level, state, pos, entity, distance);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockPos pos = entity.getOnPos();
        BlockState state = blockGetter.getBlockState(pos);

        if (!entity.isSuppressingBounce() && !state.getValue(ACTIVE))
            this.bounceUp(entity);
        else entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }
}