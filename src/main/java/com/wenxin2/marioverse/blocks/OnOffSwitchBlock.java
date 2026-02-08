package com.wenxin2.marioverse.blocks;

import com.mojang.serialization.MapCodec;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import com.wenxin2.marioverse.world.SwitchSavedData;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;

public class OnOffSwitchBlock extends Block {
    public static final MapCodec<OnOffSwitchBlock> CODEC = simpleCodec(OnOffSwitchBlock::new);
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    @NotNull
    @Override
    protected MapCodec<OnOffSwitchBlock> codec() {
        return CODEC;
    }

    public OnOffSwitchBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        SwitchSavedData data = SwitchSavedData.get((ServerLevel) placeContext.getLevel());
        Player player = placeContext.getPlayer();
        
        if (player.isShiftKeyDown())
            return this.defaultBlockState().setValue(ACTIVE, !data.isActive());
        return this.defaultBlockState().setValue(ACTIVE, data.isActive());
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moved) {
        if (!level.isClientSide && level instanceof ServerLevel server)
            SwitchSavedData.get(server).add(pos);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!level.isClientSide && level instanceof ServerLevel server)
            SwitchSavedData.get(server).remove(pos);
    }

    public static void toggle(ServerLevel level) {
        SwitchSavedData data = SwitchSavedData.get(level);
        data.setOn(!data.isActive());

        boolean isActive = data.isActive();

        for (Set<BlockPos> set : List.copyOf(data.allPositions())) {
            for (BlockPos pos : List.copyOf(set)) {
                if (!level.isLoaded(pos)) continue;
                BlockState state = level.getBlockState(pos);

                if (!(state.getBlock() instanceof OnOffSwitchBlock)) {
                    data.remove(pos);
                    continue;
                }
                if (state.getValue(OnOffSwitchBlock.ACTIVE) != isActive)
                    level.setBlock(pos, state.setValue(OnOffSwitchBlock.ACTIVE, isActive), Block.UPDATE_CLIENTS);
            }
        }
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
            entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 4);

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
