package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.fml.ModList;

public class SmashableBrickBlock extends Block {
    public SmashableBrickBlock(Properties properties) {
        super(properties);
    }

    public static void smashBlock(Level level, BlockPos pos, BlockState state, Entity entity) {
        BlockState stateAbove = level.getBlockState(pos.above());

        if (ModList.get().isLoaded("sable") && SableProvider.getContext(level, entity) != null) {
            SableProvider.SableContext context = SableProvider.getContext(level, entity);
            stateAbove = context.accessor.getBlockState(pos.above());
            if (level instanceof ServerLevel)
                stateAbove = context.accessor.getServerBlockState(pos.above());
        }

        ItemStack coinItem = new ItemStack(stateAbove.getBlock().asItem());

        QuestionBlock.hitEntityAbove(pos, level, entity);

        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM)) {
            if (state.getBlock() instanceof SlabBlock) {
                if (state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                    level.setBlock(pos, state.setValue(SlabBlock.TYPE, SlabType.TOP), 3);
                } else level.destroyBlock(pos, false);
                level.levelEvent(2001, pos, Block.getId(state));
            } else {
                if (state.getBlock() instanceof DecoratedPotBlock) {
                    level.setBlock(pos, state.setValue(DecoratedPotBlock.CRACKED, true), 4);
                    level.destroyBlock(pos, true, entity);
                } else level.destroyBlock(pos, false);
            }

            entity.setDeltaMovement(entity.getDeltaMovement().x, -entity.getDeltaMovement().y, entity.getDeltaMovement().z);
            entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 1);
            level.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);

            if (state.is(BlockTags.CRYSTAL_SOUND_BLOCKS))
                level.playSound(null, pos, SoundType.AMETHYST.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else if (state.getBlock() instanceof DecoratedPotBlock)
                level.playSound(null, pos, SoundType.DECORATED_POT.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else level.playSound(null, pos, SoundRegistry.BLOCK_SMASH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                StarCoinBlock.collectCoin(starCoin, level, stateAbove, pos.above(), entity, coinItem);
            else if (stateAbove.getBlock() instanceof CoinBlock)
                CoinBlock.collectCoin(level, stateAbove, pos.above(), entity, coinItem);
        } else {
            level.playSound(null, pos, SoundRegistry.BLOCK_SMASH_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!(state.getBlock() instanceof QuestionBlock)) {
                if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                    StarCoinBlock.collectCoin(starCoin, level, stateAbove, pos.above(), entity, coinItem);
                else if (stateAbove.getBlock() instanceof CoinBlock)
                    CoinBlock.collectCoin(level, stateAbove, pos.above(), entity, coinItem);
            }
        }
    }

    @Override
    public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return state.getBlock() == BlockRegistry.SMASHABLE_DARK_PRISMARINE.get()
                || state.getBlock() == BlockRegistry.SMASHABLE_PRISMARINE_BRICKS.get();
    }
}