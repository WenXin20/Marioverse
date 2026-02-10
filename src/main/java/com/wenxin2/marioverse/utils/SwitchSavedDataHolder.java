package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.OnBlock;
import com.wenxin2.marioverse.world.SwitchSavedData;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface SwitchSavedDataHolder {
    default void onPlaceSavedData(Level level, BlockPos pos) {
        if (!level.isClientSide && level instanceof ServerLevel server)
            SwitchSavedData.get(server).add(pos);
    }

    default void onRemoveSavedData(Level level, BlockPos pos) {
        if (!level.isClientSide && level instanceof ServerLevel server)
            SwitchSavedData.get(server).remove(pos);
    }

    default BlockState getStateForPlacementSavedData(BlockState defaultState, BlockPlaceContext placeContext) {
        Level level = placeContext.getLevel();
        Player player = placeContext.getPlayer();

        if (level instanceof ServerLevel serverLevel) {
            SwitchSavedData data = SwitchSavedData.get(serverLevel);
            if (player.isShiftKeyDown())
                return defaultState.setValue(OnBlock.ACTIVE, !data.isActive());
            return defaultState.setValue(OnBlock.ACTIVE, data.isActive());
        }
        return defaultState.setValue(OnBlock.ACTIVE, true);
    }

    static void toggle(ServerLevel level) {
        SwitchSavedData data = SwitchSavedData.get(level);
        data.setOn(!data.isActive());

        boolean isActive = data.isActive();

        for (Set<BlockPos> set : List.copyOf(data.allPositions())) {
            for (BlockPos pos : List.copyOf(set)) {
                if (!level.isLoaded(pos)) continue;
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof SwitchSavedDataHolder)) {
                    data.remove(pos);
                    continue;
                }
                if (state.getValue(OnBlock.ACTIVE) != isActive)
                    level.setBlock(pos, state.setValue(OnBlock.ACTIVE, isActive), Block.UPDATE_CLIENTS);
            }
        }
    }
}
