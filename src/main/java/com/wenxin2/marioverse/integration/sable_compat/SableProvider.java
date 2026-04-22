package com.wenxin2.marioverse.integration.sable_compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SableProvider {
    public interface BlockGetter {
        BlockState get(Entity entity, Level level, BlockPos pos);
    }

    private static BlockGetter BLOCK_GETTER = (e, l, p) -> l.getBlockState(p);

    public static BlockState getBlockState(Entity entity, Level level, BlockPos pos) {
        return BLOCK_GETTER.get(entity, level, pos);
    }

    public static void set(BlockGetter getter) {
        BLOCK_GETTER = getter;
    }
}