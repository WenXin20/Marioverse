package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockRegistry {
    public static final EnumMap<DyeColor, DeferredBlock<Block>> WARP_PIPES =
            new EnumMap<>(DyeColor.class);
    public static final DeferredBlock<Block> CLEAR_WARP_PIPE;
    public static final DeferredBlock<Block> COIN;
    public static final DeferredBlock<Block> END_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> FUNGAL_BRICKS;
    public static final DeferredBlock<Block> FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_END_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> PIPE_BUBBLES;
    public static final DeferredBlock<Block> PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> QUESTION_BRICKS;
    public static final DeferredBlock<Block> RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_NETHER_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> SMASHABLE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> STORAGE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> WATER_SPOUT;

    static
    {
        COIN = registerBlock("coin",
                () -> new CoinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(MarioverseSoundTypes.COIN_TYPE).isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(0.5F, 0.5F).instabreak().noCollission()));

        FUNGAL_QUESTION_BLOCK = registerBlock("fungal_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BELL)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        INVISIBLE_FUNGAL_QUESTION_BLOCK = registerBlock("invisible_fungal_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BELL)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        STORAGE_FUNGAL_BRICKS = registerBlock("storage_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BELL)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        FUNGAL_BRICKS = registerBlock("fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BELL)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        QUESTION_BRICKS = registerBlock("question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F)
                        .requiresCorrectToolForDrops()));

        INVISIBLE_QUESTION_BRICKS = registerBlock("invisible_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                        .instrument(NoteBlockInstrument.BELL).strength(2.0F, 6.0F)
                        .requiresCorrectToolForDrops()));

        STORAGE_BRICKS = registerBlock("storage_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F)
                        .requiresCorrectToolForDrops()));

        SMASHABLE_BRICKS = registerBlock("smashable_bricks",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(2.0F, 6.0F)
                        .requiresCorrectToolForDrops()));

        NETHER_QUESTION_BRICKS = registerBlock("nether_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        INVISIBLE_NETHER_QUESTION_BRICKS = registerBlock("invisible_nether_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        STORAGE_NETHER_BRICKS = registerBlock("storage_nether_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        SMASHABLE_NETHER_BRICKS = registerBlock("smashable_nether_bricks",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        RED_NETHER_QUESTION_BRICKS = registerBlock("red_nether_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        INVISIBLE_RED_NETHER_QUESTION_BRICKS = registerBlock("invisible_red_nether_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        STORAGE_RED_NETHER_BRICKS = registerBlock("storage_red_nether_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        SMASHABLE_RED_NETHER_BRICKS = registerBlock("smashable_red_nether_bricks",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.NETHER)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        END_STONE_QUESTION_BRICKS = registerBlock("end_stone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F, 9.0F)
                        .requiresCorrectToolForDrops()));

        INVISIBLE_END_STONE_QUESTION_BRICKS = registerBlock("invisible_end_stone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F, 9.0F)
                        .requiresCorrectToolForDrops()));

        STORAGE_END_STONE_BRICKS = registerBlock("storage_end_stone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F, 9.0F)
                        .requiresCorrectToolForDrops()));

        SMASHABLE_END_STONE_BRICKS = registerBlock("smashable_end_stone_bricks",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.SAND)
                        .instrument(NoteBlockInstrument.BASEDRUM).strength(3.0F, 9.0F)
                        .requiresCorrectToolForDrops()));

        PURPUR_QUESTION_BLOCK = registerBlock("purpur_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(1.5F, 6.0F).requiresCorrectToolForDrops()));

        INVISIBLE_PURPUR_QUESTION_BLOCK = registerBlock("invisible_purpur_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(1.5F, 6.0F).requiresCorrectToolForDrops()));

        STORAGE_PURPUR_BLOCK = registerBlock("storage_purpur_block",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(1.5F, 6.0F).requiresCorrectToolForDrops()));

        SMASHABLE_PURPUR_BLOCK = registerBlock("smashable_purpur_block",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA)
                        .sound(SoundType.NETHER_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(1.5F, 6.0F).requiresCorrectToolForDrops()));

        CLEAR_WARP_PIPE = registerBlock("clear_warp_pipe",
                () -> new ClearWarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE)
                        .sound(SoundType.GLASS).isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(3.0F, 500.0F).requiresCorrectToolForDrops().noOcclusion()));

        // Keep below CLEAR_WARP_PIPE to prevent crash
        Arrays.stream(DyeColor.values()).forEach(color ->
                WARP_PIPES.put(color, registerBlock(color.getName() + "_warp_pipe",
                        () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(color)
                                .sound(SoundType.NETHERITE_BLOCK).strength(3.5F, 1000.0F)
                                .isViewBlocking(BlockRegistry::always).requiresCorrectToolForDrops()))));

        PIPE_BUBBLES = registerNoItemBlock("pipe_bubbles",
                () -> new PipeBubblesBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY)
                        .replaceable().noCollission().noLootTable().liquid()));

        WATER_SPOUT = registerNoItemBlock("water_spout",
                () -> new WaterSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER).sound(MarioverseSoundTypes.WATER_SPOUT_TYPE)
                        .pushReaction(PushReaction.DESTROY).isRedstoneConductor(BlockRegistry::never)
                        .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .replaceable().noCollission().noLootTable()));
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block)
    {
        DeferredBlock<T> blocks = Marioverse.BLOCKS.register(name, block);
        Marioverse.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties()));
        return blocks;
    }

    public static <T extends Block> DeferredBlock<T> registerNoItemBlock(String name, Supplier<T> block)
    {
        return Marioverse.BLOCKS.register(name, block);
    }

    private static boolean always(BlockState state, BlockGetter block, BlockPos pos)
    {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter block, BlockPos pos)
    {
        return false;
    }

    public static void init() {}
}
