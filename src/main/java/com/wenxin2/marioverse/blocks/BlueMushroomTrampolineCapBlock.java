package com.wenxin2.marioverse.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlueMushroomTrampolineCapBlock extends RedMushroomTrampolineCapBlock implements ToggleableBlock {
    public BlueMushroomTrampolineCapBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        if (entity.isSuppressingBounce() && state.getValue(ACTIVE))
            super.fallOn(level, state, pos, entity, fallDistance);
        else entity.causeFallDamage(fallDistance, 0.0F, level.damageSources().fall());
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        BlockPos pos = entity.getOnPos();
        BlockState state = blockGetter.getBlockState(pos);

        if (!entity.isSuppressingBounce() && !(entity instanceof Player)
                && state.hasProperty(ACTIVE) && !state.getValue(ACTIVE))
            MushroomTrampolineCapBlock.bounceEntity(entity.level(), pos, entity, false, entity.getDeltaMovement().y);
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }
}
