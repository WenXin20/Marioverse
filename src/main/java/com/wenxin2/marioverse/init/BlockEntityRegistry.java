package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.CoinBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import java.util.stream.Stream;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class BlockEntityRegistry {
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CoinBlockEntity>> COIN_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuestionBlockEntity>> STORAGE_BRICKS_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuestionBlockEntity>> INVISIBLE_QUESTION_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<QuestionBlockEntity>> QUESTION_BLOCK_ENTITY;
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WarpPipeBlockEntity>> WARP_PIPE_BLOCK_ENTITY;

    static
    {
        COIN_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("coin",
                () -> BlockEntityType.Builder.of(CoinBlockEntity::new,
                                BlockRegistry.COIN.get()).build(null));

        INVISIBLE_QUESTION_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("invisible_question_block",
                () -> BlockEntityType.Builder.of(QuestionBlockEntity::new,
                        BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get(), BlockRegistry.INVISIBLE_QUESTION_BRICKS.get(),
                        BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get(), BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get()).build(null));

        STORAGE_BRICKS_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("storage_bricks",
                () -> BlockEntityType.Builder.of(QuestionBlockEntity::new,
                        BlockRegistry.STORAGE_FUNGAL_BRICKS.get(), BlockRegistry.STORAGE_BRICKS.get(),
                        BlockRegistry.STORAGE_NETHER_BRICKS.get(), BlockRegistry.STORAGE_RED_NETHER_BRICKS.get()).build(null));

        QUESTION_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("question_block",
                () -> BlockEntityType.Builder.of(QuestionBlockEntity::new,
                        BlockRegistry.FUNGAL_QUESTION_BLOCK.get(), BlockRegistry.NETHER_QUESTION_BRICKS.get(),
                        BlockRegistry.QUESTION_BRICKS.get(), BlockRegistry.RED_NETHER_QUESTION_BRICKS.get()).build(null));

        WARP_PIPE_BLOCK_ENTITY = Marioverse.BLOCK_ENTITIES.register("warp_pipe",
                () -> BlockEntityType.Builder.of(WarpPipeBlockEntity::new,
                                Stream.concat(BlockRegistry.WARP_PIPES.values().stream().map(DeferredBlock::get),
                                        Stream.of(BlockRegistry.CLEAR_WARP_PIPE.get())).toArray(Block[]::new)).build(null));
    }

    public static void init()
    {}
}
