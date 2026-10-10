package com.wenxin2.marioverse.blocks;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PottedMushroomTrampolineBlock extends PottedSuperMushroomBlock {
    public PottedMushroomTrampolineBlock(@Nullable Supplier<FlowerPotBlock> emptyPot, Supplier<? extends Block> block, Properties properties) {
        super(emptyPot, block, properties);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance * 0.5F);
    }

    @Override
    public void updateEntityAfterFallOn(BlockGetter blockGetter, Entity entity) {
        Vec3 motion = entity.getDeltaMovement();

        if (!entity.isSuppressingBounce() && motion.y < 0.0)
            entity.setDeltaMovement(motion.x, -motion.y * 0.5F * (entity instanceof LivingEntity ? 1.0 : 0.8), motion.z);
        else super.updateEntityAfterFallOn(blockGetter, entity);
    }
}
