package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockRegistry {
    public static final EnumMap<DyeColor, DeferredBlock<Block>> GOAL_POLES =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> WARP_PIPES =
            new EnumMap<>(DyeColor.class);

    public static final DeferredBlock<Block> AMETHYST_BRICKS;
    public static final DeferredBlock<Block> AMETHYST_BUTTON;
    public static final DeferredBlock<Block> AMETHYST_PRESSURE_PLATE;
    public static final DeferredBlock<Block> AMETHYST_SLAB;
    public static final DeferredBlock<Block> AMETHYST_STAIRS;
    public static final DeferredBlock<Block> AMETHYST_WALL;
    public static final DeferredBlock<Block> BRICK_PEDESTAL;
    public static final DeferredBlock<Block> CHISELED_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> CLASSIC_GOAL_POLE;
    public static final DeferredBlock<Block> CLEAR_WARP_PIPE;
    public static final DeferredBlock<Block> COIN;
    public static final DeferredBlock<Block> CRACKED_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> DEEP_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_BUTTON;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_PRESSURE_PLATE;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> DEEP_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> END_STONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> END_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> FUNGAL_BRICKS;
    public static final DeferredBlock<Block> FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> FUNGAL_STONE;
    public static final DeferredBlock<Block> FUNGAL_STONE_BUTTON;
    public static final DeferredBlock<Block> FUNGAL_STONE_PRESSURE_PLATE;
    public static final DeferredBlock<Block> FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_END_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_MUD_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> MUD_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> MUD_QUESTION_BRICKS;
    public static final DeferredBlock<Block> NETHER_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> PIPE_BUBBLES;
    public static final DeferredBlock<Block> POLISHED_AMETHYST;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_SLAB;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_STAIRS;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_WALL;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> PURPUR_BLOCK_PEDESTAL;
    public static final DeferredBlock<Block> PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> QUESTION_BRICKS;
    public static final DeferredBlock<Block> RED_NETHER_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_MUD_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_NETHER_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> SMASHABLE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_STONE_BRICKS;
    public static final DeferredBlock<Block> STONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> STORAGE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_MUD_BRICKS;
    public static final DeferredBlock<Block> STORAGE_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> STORAGE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_STONE_BRICKS;
    public static final DeferredBlock<Block> WATER_SPOUT;

    static {
        COIN = registerBlock("coin",
                () -> new CoinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(MarioverseSoundTypes.COIN_TYPE).instrument(NoteBlockInstrument.CHIME)
                        .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(0.5F, 0.5F).instabreak().noCollission()));


        FUNGAL_STONE = registerBlock("fungal_stone",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        FUNGAL_STONE_BUTTON = registerBlock("fungal_stone_button", () -> button(FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE, 25));

        FUNGAL_STONE_PRESSURE_PLATE = registerBlock("fungal_stone_pressure_plate", () -> pressurePlate(FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE));

        FUNGAL_STONE_SLAB = registerBlock("fungal_stone_slab", () -> slab(FUNGAL_STONE.get()));

        FUNGAL_STONE_STAIRS = registerBlock("fungal_stone_stairs", () -> stair(FUNGAL_STONE.get()));

        FUNGAL_STONE_WALL = registerBlock("fungal_stone_wall", () -> wall(FUNGAL_STONE.get()));


        POLISHED_FUNGAL_STONE = registerBlock("polished_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_STONE.get()).strength(1.5F, 6.0F)));

        POLISHED_FUNGAL_STONE_STAIRS = registerBlock("polished_fungal_stone_stairs", () -> stair(POLISHED_FUNGAL_STONE.get()));

        POLISHED_FUNGAL_STONE_SLAB = registerBlock("polished_fungal_stone_slab", () -> slab(POLISHED_FUNGAL_STONE.get()));

        POLISHED_FUNGAL_STONE_WALL = registerBlock("polished_fungal_stone_wall", () -> wall(POLISHED_FUNGAL_STONE.get()));


        FUNGAL_BRICKS = registerBlock("fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())));

        FUNGAL_BRICK_SLAB = registerBlock("fungal_brick_slab", () -> slab(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_STAIRS = registerBlock("fungal_brick_stairs", () -> stair(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_WALL = registerBlock("fungal_brick_wall", () -> wall(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_PEDESTAL = registerBlock("fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));


        FUNGAL_QUESTION_BLOCK = registerBlock("fungal_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.TERRACOTTA_RED : MapColor.GOLD)));

        INVISIBLE_FUNGAL_QUESTION_BLOCK = registerBlock("invisible_fungal_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.TERRACOTTA_RED
                                : state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.GOLD)));

        STORAGE_FUNGAL_BRICKS = registerBlock("storage_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())));


        DEEP_FUNGAL_STONE = registerBlock("deep_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(3.0F, 6.5F).requiresCorrectToolForDrops()));

        DEEP_FUNGAL_STONE_BUTTON = registerBlock("deep_fungal_stone_button", () -> button(DEEP_FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE, 35));

        DEEP_FUNGAL_STONE_PRESSURE_PLATE = registerBlock("deep_fungal_stone_pressure_plate", () -> pressurePlate(DEEP_FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE));

        DEEP_FUNGAL_STONE_SLAB = registerBlock("deep_fungal_stone_slab", () -> slab(DEEP_FUNGAL_STONE.get()));

        DEEP_FUNGAL_STONE_STAIRS = registerBlock("deep_fungal_stone_stairs", () -> stair(DEEP_FUNGAL_STONE.get()));

        DEEP_FUNGAL_STONE_WALL = registerBlock("deep_fungal_stone_wall", () -> wall(DEEP_FUNGAL_STONE.get()));


        POLISHED_DEEP_FUNGAL_STONE = registerBlock("polished_deep_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_STONE.get()).strength(2.5F, 6.0F)));

        POLISHED_DEEP_FUNGAL_STONE_STAIRS = registerBlock("polished_deep_fungal_stone_stairs", () -> stair(POLISHED_DEEP_FUNGAL_STONE.get()));

        POLISHED_DEEP_FUNGAL_STONE_SLAB = registerBlock("polished_deep_fungal_stone_slab", () -> slab(POLISHED_DEEP_FUNGAL_STONE.get()));

        POLISHED_DEEP_FUNGAL_STONE_WALL = registerBlock("polished_deep_fungal_stone_wall", () -> wall(POLISHED_DEEP_FUNGAL_STONE.get()));


        DEEP_FUNGAL_BRICKS = registerBlock("deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())));

        DEEP_FUNGAL_BRICK_STAIRS = registerBlock("deep_fungal_brick_stairs", () -> stair(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_SLAB = registerBlock("deep_fungal_brick_slab", () -> slab(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_WALL = registerBlock("deep_fungal_brick_wall", () -> wall(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_PEDESTAL = registerBlock("deep_fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));


        AMETHYST_BUTTON = registerBlock("amethyst_button", () -> button(Blocks.AMETHYST_BLOCK, BlockSetTypeRegistry.AMETHYST, 40));

        AMETHYST_PRESSURE_PLATE = registerBlock("amethyst_pressure_plate", () -> pressurePlate(Blocks.AMETHYST_BLOCK, BlockSetTypeRegistry.AMETHYST));

        AMETHYST_SLAB = registerBlock("amethyst_slab", () -> slab(Blocks.AMETHYST_BLOCK));

        AMETHYST_STAIRS = registerBlock("amethyst_stairs", () -> stair(Blocks.AMETHYST_BLOCK));

        AMETHYST_WALL = registerBlock("amethyst_wall", () -> wall(Blocks.AMETHYST_BLOCK));

        POLISHED_AMETHYST = registerBlock("polished_amethyst",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK).strength(1.25F)));

        POLISHED_AMETHYST_STAIRS = registerBlock("polished_amethyst_stairs", () -> stair(POLISHED_AMETHYST.get()));

        POLISHED_AMETHYST_SLAB = registerBlock("polished_amethyst_slab", () -> slab(POLISHED_AMETHYST.get()));

        POLISHED_AMETHYST_WALL = registerBlock("polished_amethyst_wall", () -> wall(POLISHED_AMETHYST.get()));

        AMETHYST_BRICKS = registerBlock("amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)));

        CHISELED_AMETHYST_BRICKS = registerBlock("chiseled_amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)));

        CRACKED_AMETHYST_BRICKS = registerBlock("cracked_amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.AMETHYST_BLOCK)));


        DEEP_FUNGAL_QUESTION_BLOCK = registerBlock("deep_fungal_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.COLOR_CYAN : MapColor.COLOR_GREEN)));

        INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK = registerBlock("invisible_deep_fungal_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.COLOR_CYAN
                                : state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_GREEN)));

        STORAGE_DEEP_FUNGAL_BRICKS = registerBlock("storage_deep_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())));


        STONE_QUESTION_BRICKS = registerBlock("stone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

        INVISIBLE_STONE_QUESTION_BRICKS = registerBlock("invisible_stone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.STONE)));

        STORAGE_STONE_BRICKS = registerBlock("storage_stone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

        SMASHABLE_STONE_BRICKS = registerBlock("smashable_stone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));

        STONE_BRICK_PEDESTAL = registerBlock("stone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_BRICKS)));


        QUESTION_BRICKS = registerBlock("question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));

        INVISIBLE_QUESTION_BRICKS = registerBlock("invisible_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_RED)));

        STORAGE_BRICKS = registerBlock("storage_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));

        SMASHABLE_BRICKS = registerBlock("smashable_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));

        BRICK_PEDESTAL = registerBlock("brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS)));


        MUD_QUESTION_BRICKS = registerBlock("mud_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));

        INVISIBLE_MUD_QUESTION_BRICKS = registerBlock("invisible_mud_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_LIGHT_GRAY)));

        STORAGE_MUD_BRICKS = registerBlock("storage_mud_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));

        SMASHABLE_MUD_BRICKS = registerBlock("smashable_mud_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));

        MUD_BRICK_PEDESTAL = registerBlock("mud_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MUD_BRICKS)));


        NETHER_QUESTION_BRICKS = registerBlock("nether_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));

        INVISIBLE_NETHER_QUESTION_BRICKS = registerBlock("invisible_nether_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.NETHER)));

        STORAGE_NETHER_BRICKS = registerBlock("storage_nether_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));

        SMASHABLE_NETHER_BRICKS = registerBlock("smashable_nether_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));

        NETHER_BRICK_PEDESTAL = registerBlock("nether_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.NETHER_BRICKS)));


        RED_NETHER_QUESTION_BRICKS = registerBlock("red_nether_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));

        INVISIBLE_RED_NETHER_QUESTION_BRICKS = registerBlock("invisible_red_nether_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.NETHER)));

        STORAGE_RED_NETHER_BRICKS = registerBlock("storage_red_nether_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));

        SMASHABLE_RED_NETHER_BRICKS = registerBlock("smashable_red_nether_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));

        RED_NETHER_BRICK_PEDESTAL = registerBlock("red_nether_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_NETHER_BRICKS)));


        END_STONE_QUESTION_BRICKS = registerBlock("end_stone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));

        INVISIBLE_END_STONE_QUESTION_BRICKS = registerBlock("invisible_end_stone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.SAND)));

        STORAGE_END_STONE_BRICKS = registerBlock("storage_end_stone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));

        SMASHABLE_END_STONE_BRICKS = registerBlock("smashable_end_stone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));

        END_STONE_BRICK_PEDESTAL = registerBlock("end_stone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.END_STONE_BRICKS)));


        PURPUR_QUESTION_BLOCK = registerBlock("purpur_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));

        INVISIBLE_PURPUR_QUESTION_BLOCK = registerBlock("invisible_purpur_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_MAGENTA)));

        STORAGE_PURPUR_BLOCK = registerBlock("storage_purpur_block",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));

        SMASHABLE_PURPUR_BLOCK = registerBlock("smashable_purpur_block",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));

        PURPUR_BLOCK_PEDESTAL = registerBlock("purpur_block_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PURPUR_BLOCK)));


        CLASSIC_GOAL_POLE = registerBlock("classic_goal_pole",
                () -> new GoalPoleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                        .strength(5.0F, 6.0F).requiresCorrectToolForDrops()));

        Arrays.stream(DyeColor.values()).forEach(color ->
                GOAL_POLES.put(color, registerBlock(color.getName() + "_goal_pole",
                        () -> new GoalPoleBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                                .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                                .strength(5.0F, 6.0F).requiresCorrectToolForDrops()))));


        CLEAR_WARP_PIPE = registerBlock("clear_warp_pipe",
                () -> new ClearWarpPipeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.NONE)
                        .sound(SoundType.GLASS).instrument(NoteBlockInstrument.CHIME)
                        .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(3.0F, 500.0F).requiresCorrectToolForDrops().noOcclusion()));

        // Keep below CLEAR_WARP_PIPE to prevent crash
        Arrays.stream(DyeColor.values()).forEach(color ->
                WARP_PIPES.put(color, registerBlock(color.getName() + "_warp_pipe",
                        () -> new WarpPipeBlock(BlockBehaviour.Properties.of().mapColor(color)
                                .sound(SoundType.NETHERITE_BLOCK).instrument(NoteBlockInstrument.BASS)
                                .strength(3.5F, 1000.0F).isViewBlocking(BlockRegistry::always)
                                .requiresCorrectToolForDrops()))));


        PIPE_BUBBLES = registerNoItemBlock("pipe_bubbles",
                () -> new PipeBubblesBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY)
                        .replaceable().noCollission().noLootTable().liquid()));

        WATER_SPOUT = registerNoItemBlock("water_spout",
                () -> new WaterSpoutBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WATER)
                        .sound(MarioverseSoundTypes.WATER_SPOUT_TYPE).pushReaction(PushReaction.DESTROY)
                        .isRedstoneConductor(BlockRegistry::never).isSuffocating(BlockRegistry::never)
                        .isViewBlocking(BlockRegistry::never).replaceable().noCollission().noLootTable()));
    }

    public static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> blocks = Marioverse.BLOCKS.register(name, block);
        Marioverse.ITEMS.register(name, () -> new BlockItem(blocks.get(), new Item.Properties()));
        return blocks;
    }

    public static <T extends Block> DeferredBlock<T> registerNoItemBlock(String name, Supplier<T> block) {
        return Marioverse.BLOCKS.register(name, block);
    }

    private static Block button(Block block, BlockSetType blockSetType, int ticksPressed) {
        return new ButtonBlock(blockSetType, ticksPressed, BlockBehaviour.Properties.ofFullCopy(block));
    }

    private static Block pressurePlate(Block block, BlockSetType blockSetType) {
        return new PressurePlateBlock(blockSetType, BlockBehaviour.Properties.ofFullCopy(block));
    }

    private static Block slab(Block block) {
        return new SlabBlock(BlockBehaviour.Properties.ofFullCopy(block));
    }

    private static Block stair(Block block) {
        return new StairBlock(block.defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(block));
    }

    private static Block wall(Block block) {
        return new WallBlock(BlockBehaviour.Properties.ofFullCopy(block).forceSolidOn());
    }

    private static boolean always(BlockState state, BlockGetter block, BlockPos pos) {
        return true;
    }

    private static boolean never(BlockState state, BlockGetter block, BlockPos pos) {
        return false;
    }

    public static void init() {}
}
