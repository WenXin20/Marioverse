package com.wenxin2.marioverse.blocks;

import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public interface MultiPartWarpDoor {
    List<BlockPos> mv$getParts(BlockState state, BlockPos pos);

    boolean mv$isOpen(BlockState state);

    BlockState mv$setOpen(BlockState state, boolean open);

    Direction mv$getFacing(BlockState state);

    BlockSetType mv$getType();

    String mv$getFamily();

    Map<String, String> mv$getDropProperties();
}
