package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.registries.BlockRegistry;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;

public class SmashableBrickBlock extends Block {
    public SmashableBrickBlock(Properties properties) {
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
            smashBlock(world, posNorth, stateNorth, entity);
        }

        if (stateSouth.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, Direction.SOUTH);
            smashBlock(world, posSouth, stateSouth, entity);
        }

        if (stateEast.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, Direction.EAST);
            smashBlock(world, posEast, stateEast, entity);
        }

        if (stateWest.is(TagRegistry.SMASHABLE_BLOCKS)) {
            if (entity instanceof KoopaShellEntity shell)
                shell.bounceShell(world, Direction.WEST);
            smashBlock(world, posWest, stateWest, entity);
        }
    }

    @Override
    public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return state.getBlock() == BlockRegistry.SMASHABLE_DARK_PRISMARINE.get()
                || state.getBlock() == BlockRegistry.SMASHABLE_PRISMARINE_BRICKS.get();
    }
}