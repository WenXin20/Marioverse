package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class DeathBlock extends Block {
    public static final MapCodec<DeathBlock> CODEC = simpleCodec(DeathBlock::new);

    protected static final VoxelShape SHAPE =
            Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9).optimize();

    @NotNull
    @Override
    protected MapCodec<? extends DeathBlock> codec() {
        return CODEC;
    }

    public DeathBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any());
    }

    @NotNull
    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!entity.getType().is(TagRegistry.DEATH_BLOCKS_IMMUNE) && entity.isAlive()) {
            if (entity instanceof Player player && !player.isCreative() && !player.isSpectator())
                entity.hurt(DamageSourceRegistry.instakill(entity), Float.MAX_VALUE);
            else if (!(entity instanceof Player)) entity.remove(Entity.RemovalReason.KILLED);

            if (level instanceof ServerLevel serverLevel && !(entity instanceof Player))
                ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleRegistry.GLOWING_STAR.get(), serverLevel, entity, 2.0, 10);
        }
        super.entityInside(state, level, pos, entity);
    }
}
