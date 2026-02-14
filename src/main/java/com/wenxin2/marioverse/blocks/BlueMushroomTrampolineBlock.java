package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class BlueMushroomTrampolineBlock extends RedMushroomTrampolineBlock implements ToggleableBlock {
    public BlueMushroomTrampolineBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockPos pos = entity.getOnPos();
        BlockState state = blockGetter.getBlockState(pos);

        if (!entity.isSuppressingBounce() && !(entity instanceof Player)
                && state.hasProperty(ACTIVE) && !state.getValue(ACTIVE))
            RedMushroomTrampolineBlock.bounceEntity(entity.level(), entity, false);
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }
}
