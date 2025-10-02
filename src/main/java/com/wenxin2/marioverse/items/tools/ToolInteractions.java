package com.wenxin2.marioverse.items.tools;

import com.wenxin2.marioverse.mixin.AxeItemAccessor;
import com.wenxin2.marioverse.registries.BlockRegistry;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.level.block.Block;

public class ToolInteractions {
    public static void strippableBlocks(Block block, Block strippedBlock) {
        Map<Block, Block> STRIPPABLES = new HashMap<>(AxeItemAccessor.getStrippables());
        STRIPPABLES.put(block, strippedBlock);
        AxeItemAccessor.setStrippables(Map.copyOf(STRIPPABLES));
    }

    public static void registerStrippableBlocks() {
        strippableBlocks(BlockRegistry.OAK_LOG_BRIDGE.get(), BlockRegistry.STRIPPED_OAK_LOG_BRIDGE.get());
    }

    public static void init() {
        ToolInteractions.registerStrippableBlocks();
    }
}
