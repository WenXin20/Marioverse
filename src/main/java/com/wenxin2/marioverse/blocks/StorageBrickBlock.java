package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;

public class StorageBrickBlock extends QuestionBlock {
    public StorageBrickBlock(Properties properties) {
        super(properties);
    }

    public static void smashBlock(Level world, BlockPos pos, BlockState state, Entity entity) {
        BlockState stateAbove = world.getBlockState(pos.above());
        ItemStack coinItem = new ItemStack(stateAbove.getBlock().asItem());

        QuestionBlock.hitEntityAbove(pos, world, entity);

        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM)) {
            if (state.getBlock() instanceof SlabBlock) {
                if (state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                    world.setBlock(pos, state.setValue(SlabBlock.TYPE, SlabType.TOP), 3);
                } else world.destroyBlock(pos, false);
                world.levelEvent(2001, pos, Block.getId(state));
            } else {
                if (state.getBlock() instanceof DecoratedPotBlock) {
                    world.setBlock(pos, state.setValue(DecoratedPotBlock.CRACKED, true), 4);
                    world.destroyBlock(pos, true, entity);
                } else world.destroyBlock(pos, false);
            }

            entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), 1);
            world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);

            if (state.is(BlockTags.CRYSTAL_SOUND_BLOCKS))
                world.playSound(null, pos, SoundType.AMETHYST.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else if (state.getBlock() instanceof DecoratedPotBlock)
                world.playSound(null, pos, SoundType.DECORATED_POT.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, pos, SoundRegistry.BLOCK_SMASH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
            else if (stateAbove.getBlock() instanceof CoinBlock)
                CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);
        } else {
            world.playSound(null, pos, SoundRegistry.BLOCK_SMASH_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!(state.getBlock() instanceof QuestionBlock)) {
                if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                    StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
                else if (stateAbove.getBlock() instanceof CoinBlock)
                    CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);
            }
        }
    }

    public static void smashBlockFromSide(BlockState stateNorth, Entity entity, Level world, BlockPos posNorth, BlockState stateSouth, BlockPos posSouth, BlockState stateEast, BlockPos posEast, BlockState stateWest, BlockPos posWest) {
        if (stateNorth.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, Direction.NORTH);
            StorageBrickBlock.smashBlock(world, posNorth, stateNorth, entity);
        }

        if (stateSouth.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, Direction.SOUTH);
            StorageBrickBlock.smashBlock(world, posSouth, stateSouth, entity);
        }

        if (stateEast.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, Direction.EAST);
            StorageBrickBlock.smashBlock(world, posEast, stateEast, entity);
        }

        if (stateWest.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, Direction.WEST);
            StorageBrickBlock.smashBlock(world, posWest, stateWest, entity);
        }
    }

    public static void bonkBlockFromSide(BlockState stateNorth, Level world, BlockPos posNorth, BlockState stateSouth, BlockPos posSouth, BlockState stateEast, BlockPos posEast, BlockState stateWest, BlockPos posWest) {
        if (stateNorth.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateNorth.hasProperty(QuestionBlock.EMPTY) && stateNorth.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posNorth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posNorth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateSouth.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateSouth.hasProperty(QuestionBlock.EMPTY) && stateSouth.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posSouth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posSouth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateEast.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateEast.hasProperty(QuestionBlock.EMPTY) && stateEast.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posEast, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posEast, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateWest.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateWest.hasProperty(QuestionBlock.EMPTY) && stateWest.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posWest, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posWest, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
