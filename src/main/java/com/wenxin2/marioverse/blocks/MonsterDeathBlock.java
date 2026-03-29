package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MonsterDeathBlock extends DeathBlock {
    public static final MapCodec<MonsterDeathBlock> CODEC = simpleCodec(MonsterDeathBlock::new);

    @NotNull
    @Override
    protected MapCodec<? extends MonsterDeathBlock> codec() {
        return CODEC;
    }

    public MonsterDeathBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (entity instanceof Monster)
            super.entityInside(state, level, pos, entity);
    }
}
