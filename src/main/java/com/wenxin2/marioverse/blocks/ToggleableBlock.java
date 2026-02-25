package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.world.GlobalSwitchSavedData;
import com.wenxin2.marioverse.world.LinkedSwitchSavedData;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ToggleableBlock {
    default void onPlaceSavedData(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server)
            GlobalSwitchSavedData.get(server).add(pos);
    }

    default void onRemoveSavedData(Level level, BlockPos pos) {
        if (level instanceof ServerLevel server)
            GlobalSwitchSavedData.get(server).unlink(pos);
    }

    default BlockState getStateForPlacementSavedData(BlockState defaultState, BlockPlaceContext placeContext) {
        Level level = placeContext.getLevel();
        Player player = placeContext.getPlayer();

        if (level instanceof ServerLevel serverLevel) {
            GlobalSwitchSavedData data = GlobalSwitchSavedData.get(serverLevel);
            if (player != null && player.isShiftKeyDown())
                return defaultState.setValue(OnBlock.ACTIVE, !data.isActive());
            return defaultState.setValue(OnBlock.ACTIVE, data.isActive());
        }
        return defaultState.setValue(OnBlock.ACTIVE, true);
    }

    static void toggle(ServerLevel level, BlockPos switchPos) {
        BlockState stateSwitch = level.getBlockState(switchPos);
        int radius = stateSwitch.getValue(OnOffSwitchBlock.RADIUS);
        GlobalSwitchSavedData dataGlobal = GlobalSwitchSavedData.get(level);
        LinkedSwitchSavedData dataLinked = LinkedSwitchSavedData.get(level);

        dataGlobal.setActive(!dataGlobal.isActive());
        boolean isActiveGlobal = dataGlobal.isActive();
        boolean stateActive = stateSwitch.getValue(OnBlock.ACTIVE);

        if (radius > 0) {
            BlockPos.MutableBlockPos posMutable = new BlockPos.MutableBlockPos();

            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        posMutable.set(switchPos.getX() + x, switchPos.getY() + y, switchPos.getZ() + z);

                        if (!level.isLoaded(posMutable)) continue;

                        BlockState state = level.getBlockState(posMutable);

                        if (state.getBlock() instanceof ToggleableBlock && state.getValue(OnBlock.ACTIVE) != stateActive)
                            level.setBlock(posMutable, state.setValue(OnBlock.ACTIVE, stateActive), Block.UPDATE_ALL);

                        dataGlobal.unlink(posMutable);
                        dataLinked.unlink(posMutable);
                    }
                }
            }
            return;
        }


        for (Set<BlockPos> posSet : List.copyOf(dataLinked.allPositions(switchPos))) {
            for (BlockPos pos : List.copyOf(posSet)) {
                if (!level.isLoaded(pos)) continue;
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof ToggleableBlock)) {
                    dataLinked.unlink(pos);
                    continue;
                }

                if (state.getValue(OnBlock.ACTIVE) != stateActive)
                    level.setBlock(pos, state.setValue(OnBlock.ACTIVE, stateActive), Block.UPDATE_CLIENTS);
                dataGlobal.unlink(pos);
            }
        }

        if (radius == 0 && dataLinked.allPositions(switchPos).isEmpty()) {

            for (Set<BlockPos> posSet : List.copyOf(dataGlobal.allPositions())) {
                for (BlockPos pos : List.copyOf(posSet)) {
                    if (!level.isLoaded(pos)) continue;
                    BlockState state = level.getBlockState(pos);

                    if (!(state.getBlock() instanceof ToggleableBlock)) {
                        dataGlobal.unlink(pos);
                        continue;
                    }

                    if (state.getValue(OnBlock.ACTIVE) != isActiveGlobal)
                        level.setBlock(pos, state.setValue(OnBlock.ACTIVE, isActiveGlobal), Block.UPDATE_CLIENTS);
                }
            }
        }
    }
}