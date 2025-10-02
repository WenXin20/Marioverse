package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.registries.BlockRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;

public class BlockFeatures {
    public static void flammableBlocks(final Block block, final int speed, final int flammability) {
        final FireBlock fire = (FireBlock) Blocks.FIRE;
        fire.setFlammable(block, speed, flammability);
    }

    public static void registerFlammable() {
        flammableBlocks(BlockRegistry.OAK_LOG_BRIDGE.get(), 5, 5);
    }

    public static void init() {
        BlockFeatures.registerFlammable();
    }
}
