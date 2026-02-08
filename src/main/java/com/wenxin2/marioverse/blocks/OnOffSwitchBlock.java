package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class OnOffSwitchBlock extends OnBlock {
    public static final MapCodec<OnBlock> CODEC = simpleCodec(OnBlock::new);

    @NotNull
    @Override
    protected MapCodec<OnBlock> codec() {
        return CODEC;
    }

    public OnOffSwitchBlock(Properties properties) {
        super(properties);
    }

    @NotNull
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return Shapes.block();
    }

    @NotNull
    @Override
    protected VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_, CollisionContext p_309006_) {
        return Shapes.block();
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return 0.0F;
    }

    @Override
    protected void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        OnOffSwitchBlock.hitSwitchBlock(level, hitResult.getBlockPos(), projectile);
        projectile.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 20);
    }

    public static void hitSwitchBlock(Level level, BlockPos pos, Entity entity) {
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof OnOffSwitchBlock) {
            QuestionBlock.hitEntityAbove(pos, level, entity);

            if (level instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticlesOnBlockFace(ParticleTypes.CRIT, serverWorld, pos, Direction.DOWN,
                        UniformInt.of(3, 4), () -> ServerParticleUtils.getRandomSpeedRanges(level.getRandom()), 0.65D);

            if (state.getValue(ACTIVE))
                level.playSound(null, pos, SoundRegistry.SWITCH_OFF.get(), SoundSource.BLOCKS);
            else level.playSound(null, pos, SoundRegistry.SWITCH_ON.get(), SoundSource.BLOCKS);

            level.setBlock(pos, state.cycle(ACTIVE), 3);

            if (!level.isClientSide && level instanceof ServerLevel serverWorld)
                OnOffSwitchBlock.toggle(serverWorld);
        }
    }

    public static void hitSwitchBlockFromSide(Level level, BlockPos posNorth, Entity entity, BlockPos posSouth, BlockPos posEast, BlockPos posWest) {
        if (level.getBlockState(posNorth).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(level, posNorth, entity);

        if (level.getBlockState(posSouth).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(level, posSouth, entity);

        if (level.getBlockState(posEast).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(level, posEast, entity);

        if (level.getBlockState(posWest).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(level, posWest, entity);
    }
}
