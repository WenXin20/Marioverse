package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.CommonHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShroomsoilFarmlandBlock extends FarmBlock {
    public ShroomsoilFarmlandBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? BlockRegistry.SHROOMSOIL.get().defaultBlockState()
                : super.getStateForPlacement(context);
    }

    @Override
    protected void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(serverLevel, pos))
            ShroomsoilFarmlandBlock.turnToShroomsoil(null, state, serverLevel, pos);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        int moisture = state.getValue(MOISTURE);

        if (!ShroomsoilFarmlandBlock.isNearWater(serverLevel, pos) && !serverLevel.isRainingAt(pos.above())) {
            if (moisture > 0)
                serverLevel.setBlock(pos, state.setValue(MOISTURE, moisture - 1), 2);
            else if (!ShroomsoilFarmlandBlock.shouldMaintainFarmland(serverLevel, pos))
                ShroomsoilFarmlandBlock.turnToShroomsoil(null, state, serverLevel, pos);
        } else if (moisture < MAX_MOISTURE)
            serverLevel.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (!level.isClientSide && CommonHooks.onFarmlandTrample(level, pos,
                BlockRegistry.SHROOMSOIL.get().defaultBlockState(), fallDistance, entity))
            ShroomsoilFarmlandBlock.turnToShroomsoil(entity, state, level, pos);

        super.fallOn(level, state, pos, entity, fallDistance);
    }

    public static void turnToShroomsoil(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState dirtState = Block.pushEntitiesUp(state, BlockRegistry.SHROOMSOIL.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, dirtState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, dirtState));
    }

    private static boolean shouldMaintainFarmland(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos.above()).is(BlockTags.MAINTAINS_FARMLAND);
    }

    private static boolean isNearWater(LevelReader levelReader, BlockPos pos) {
        for (BlockPos posOffset : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
            if (levelReader.getFluidState(posOffset).is(FluidTags.WATER))
                return true;
        }
        return false;
    }
}
