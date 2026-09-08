package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WetMudFarmlandBlock extends FarmBlock {
    protected static final VoxelShape COLLISION_SHAPE = Block
            .box(0.0, 0.0, 0.0, 16.0, 13.0, 16.0);

    public WetMudFarmlandBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, MAX_MOISTURE));
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? BlockRegistry.WET_MUD.get().defaultBlockState()
                : super.getStateForPlacement(context);
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(serverLevel, pos))
            WetMudFarmlandBlock.turnToMud(null, state, serverLevel, pos);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (state.getValue(MOISTURE) < MAX_MOISTURE)
            serverLevel.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide && CommonHooks.onFarmlandTrample(level, pos,
                BlockRegistry.WET_MUD.get().defaultBlockState(), fallDistance, entity))
            WetMudFarmlandBlock.turnToMud(entity, state, level, pos);

        entity.causeFallDamage(fallDistance, 1.0F, level.damageSources().fall());
    }

    public static void turnToMud(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState dirtState = Block.pushEntitiesUp(state, BlockRegistry.WET_MUD.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, dirtState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, dirtState));
    }
}
