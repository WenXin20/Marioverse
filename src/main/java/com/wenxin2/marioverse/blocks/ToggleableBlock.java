package com.wenxin2.marioverse.blocks;

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

public interface ToggleableBlock {
    default void onPlaceSavedData(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server)
            SwitchSavedData.get(server).add(pos);
    }

    default void onRemoveSavedData(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server)
            SwitchSavedData.get(server).remove(pos);
    }

    default BlockState getStateForPlacementSavedData(BlockState defaultState, BlockPlaceContext placeContext) {
        Level level = placeContext.getLevel();
        Player player = placeContext.getPlayer();

        if (level instanceof ServerLevel serverLevel) {
            SwitchSavedData data = SwitchSavedData.get(serverLevel);
            if (player != null && player.isShiftKeyDown())
                return defaultState.setValue(OnBlock.ACTIVE, !data.isActive());
            return defaultState.setValue(OnBlock.ACTIVE, data.isActive());
        }
        return defaultState.setValue(OnBlock.ACTIVE, true);
    }

    static void toggle(ServerLevel level, BlockPos switchPos) {
        BlockState switchState = level.getBlockState(switchPos);
        int radius = switchState.getValue(OnOffSwitchBlock.RADIUS);
        SwitchSavedData data = SwitchSavedData.get(level);

        data.setOn(!data.isActive());
        boolean isActive = data.isActive();

        if (radius > 0) {
            BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        posMutable.set(switchPos.getX() + x, switchPos.getY() + y, switchPos.getZ() + z);

                        if (!level.isLoaded(posMutable)) continue;

                        BlockState state = level.getBlockState(posMutable);

                        if (state.getBlock() instanceof ToggleableBlock && state.getValue(OnBlock.ACTIVE) != isActive)
                            level.setBlock(posMutable, state.setValue(OnBlock.ACTIVE, isActive), Block.UPDATE_ALL);
                    }
                }
            }
            return;
        }

        for (Set<BlockPos> posSet : List.copyOf(data.allPositions())) {
            for (BlockPos pos : List.copyOf(posSet)) {
                if (!level.isLoaded(pos)) continue;
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof ToggleableBlock)) {
                    data.remove(pos);
                    continue;
                }

                if (state.getValue(OnBlock.ACTIVE) != isActive)
                    level.setBlock(pos, state.setValue(OnBlock.ACTIVE, isActive), Block.UPDATE_CLIENTS);
            }
        }
    }
}
