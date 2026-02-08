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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

    public static void hitSwitchBlock(Level world, BlockPos pos, Entity entity) {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof OnOffSwitchBlock) {
            QuestionBlock.hitEntityAbove(pos, world, entity);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticlesOnBlockFace(ParticleTypes.CRIT, serverWorld, pos, Direction.DOWN,
                        UniformInt.of(3, 4), () -> ServerParticleUtils.getRandomSpeedRanges(world.getRandom()), 0.65D);

            if (state.getValue(ACTIVE))
                world.playSound(null, pos, SoundRegistry.SWITCH_OFF.get(), SoundSource.BLOCKS);
            else world.playSound(null, pos, SoundRegistry.SWITCH_ON.get(), SoundSource.BLOCKS);

            world.setBlock(pos, state.cycle(ACTIVE), 3);
            entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 2);

            if (!world.isClientSide && world instanceof ServerLevel serverWorld)
                OnOffSwitchBlock.toggle(serverWorld);
        }
    }

    public static void hitSwitchBlockFromSide(Level world, BlockPos posNorth, Entity entity, BlockPos posSouth, BlockPos posEast, BlockPos posWest) {
        if (world.getBlockState(posNorth).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(world, posNorth, entity);

        if (world.getBlockState(posSouth).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(world, posSouth, entity);

        if (world.getBlockState(posEast).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(world, posEast, entity);

        if (world.getBlockState(posWest).getBlock() instanceof OnOffSwitchBlock)
            OnOffSwitchBlock.hitSwitchBlock(world, posWest, entity);
    }
}
