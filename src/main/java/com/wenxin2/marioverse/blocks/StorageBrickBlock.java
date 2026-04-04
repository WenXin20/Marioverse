package com.wenxin2.marioverse.blocks;

import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StorageBrickBlock extends QuestionBlock {
    public StorageBrickBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new QuestionBlockEntity(BlockEntityRegistry.STORAGE_BRICKS_BLOCK_ENTITY.get(), pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, BlockEntityRegistry.STORAGE_BRICKS_BLOCK_ENTITY.get(), QuestionBlockEntity::tick);
    }

    @Override
    public boolean isConduitFrame(BlockState state, LevelReader level, BlockPos pos, BlockPos conduit) {
        return state.getBlock() == BlockRegistry.STORAGE_DARK_PRISMARINE.get()
                || state.getBlock() == BlockRegistry.STORAGE_PRISMARINE_BRICKS.get();
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