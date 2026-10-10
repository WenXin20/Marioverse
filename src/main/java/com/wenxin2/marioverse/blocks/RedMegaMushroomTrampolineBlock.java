package com.wenxin2.marioverse.blocks;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
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
        if (oldState.is(this) && state.getValue(TOP) && !oldState.getValue(ACTIVE) && state.getValue(ACTIVE))
            this.launchEntities(level, pos);
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
        else entity.setDeltaMovement(entity.getDeltaMovement().multiply(1.0, 0.0, 1.0));
    }

    protected void launchEntities(Level level, BlockPos pos) {
        for (Entity entity : level.getEntities(null, new AABB(pos).expandTowards(0.0, 0.5, 0.0))) {
            if (!entity.onGround() || entity.isSuppressingBounce() || !entity.getOnPos().equals(pos))
                continue;

            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x, 0.8, motion.z);
            entity.resetFallDistance();
            entity.hurtMarked = true;
        }
    }
}