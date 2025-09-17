package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.BrickPedestalBlock;
import com.wenxin2.marioverse.blocks.CheckpointFlagBlock;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.GoalPoleBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.IronSpikeBlock;
import com.wenxin2.marioverse.blocks.PottedPiranhaPlantBlock;
import com.wenxin2.marioverse.blocks.StarCoinBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.PipeBubblesBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.WaterSpoutBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperInvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperPedestalBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperStorageBrickBlock;
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
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperFullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockRegistry {
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CALCITE =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CALCITE_BRICKS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CHISELED_CALCITE_BRICKS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> POLISHED_CALCITE =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> CHECKPOINT_FLAGS =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> GOAL_POLES =
            new EnumMap<>(DyeColor.class);
    public static final EnumMap<DyeColor, DeferredBlock<Block>> WARP_PIPES =
            new EnumMap<>(DyeColor.class);

    public static final DeferredBlock<Block> AMETHYST_BRICKS;
    public static final DeferredBlock<Block> AMETHYST_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> AMETHYST_BRICK_SLAB;
    public static final DeferredBlock<Block> AMETHYST_BRICK_STAIRS;
    public static final DeferredBlock<Block> AMETHYST_BRICK_WALL;
    public static final DeferredBlock<Block> AMETHYST_BUTTON;
    public static final DeferredBlock<Block> AMETHYST_PRESSURE_PLATE;
    public static final DeferredBlock<Block> AMETHYST_QUESTION_BLOCK;
    public static final DeferredBlock<Block> AMETHYST_SLAB;
    public static final DeferredBlock<Block> AMETHYST_STAIRS;
    public static final DeferredBlock<Block> AMETHYST_WALL;
    public static final DeferredBlock<Block> BLACKSTONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> BLACKSTONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> BRICK_PEDESTAL;
    public static final DeferredBlock<Block> CHISELED_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> CHISELED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CHISELED_RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CHISELED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CLASSIC_CHECKPOINT_FLAG;
    public static final DeferredBlock<Block> CLASSIC_GOAL_POLE;
    public static final DeferredBlock<Block> CLEAR_WARP_PIPE;
    public static final DeferredBlock<Block> COIN;
    public static final DeferredBlock<Block> COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> CRACKED_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> CRACKED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> CRACKED_RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CRACKED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> DARK_PRISMARINE_PEDESTAL;
    public static final DeferredBlock<Block> DARK_PRISMARINE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> DEEPSLATE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> DEEPSLATE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> DEEPSLATE_QUESTION_TILES;
    public static final DeferredBlock<Block> DEEPSLATE_TILE_PEDESTAL;
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
    public static final DeferredBlock<Block> EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> EXPOSED_CUT_COPPER_PEDESTAL;
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
    public static final DeferredBlock<Block> INVISIBLE_AMETHYST_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_BLACKSTONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_DEEPSLATE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_DEEPSLATE_QUESTION_TILES;
    public static final DeferredBlock<Block> INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_END_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_FUNGAL_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_MOSSY_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_MUD_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_PRISMARINE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_QUARTZ_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_TUFF_QUESTION_BRICKS;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> IRON_SPIKE;
    public static final DeferredBlock<Block> MOSSY_STONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> MOSSY_STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> MUD_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> MUD_QUESTION_BRICKS;
    public static final DeferredBlock<Block> NETHER_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> OXIDIZED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> PIPE_BUBBLES;
    public static final DeferredBlock<Block> POLISHED_AMETHYST;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_SLAB;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_STAIRS;
    public static final DeferredBlock<Block> POLISHED_AMETHYST_WALL;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> POLISHED_DEEP_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_SLAB;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_STAIRS;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_BRICK_WALL;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_SLAB;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_STAIRS;
    public static final DeferredBlock<Block> POLISHED_FUNGAL_STONE_WALL;
    public static final DeferredBlock<Block> POTTED_PIRANHA_PLANT;
    public static final DeferredBlock<Block> PRISMARINE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> PRISMARINE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> PURPUR_BLOCK_PEDESTAL;
    public static final DeferredBlock<Block> PURPUR_QUESTION_BLOCK;
    public static final DeferredBlock<Block> QUARTZ_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> QUARTZ_QUESTION_BRICKS;
    public static final DeferredBlock<Block> QUESTION_BRICKS;
    public static final DeferredBlock<Block> RED_NETHER_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> RED_NETHER_QUESTION_BRICKS;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_SLAB;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_STAIRS;
    public static final DeferredBlock<Block> RED_SANDSTONE_BRICK_WALL;
    public static final DeferredBlock<Block> RED_SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_SLAB;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_STAIRS;
    public static final DeferredBlock<Block> SANDSTONE_BRICK_WALL;
    public static final DeferredBlock<Block> SANDSTONE_QUESTION_BLOCK;
    public static final DeferredBlock<Block> SMASHABLE_BLACKSTONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_DARK_PRISMARINE;
    public static final DeferredBlock<Block> SMASHABLE_DEEPSLATE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_DEEPSLATE_TILES;
    public static final DeferredBlock<Block> SMASHABLE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_MOSSY_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_MUD_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_NETHER_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_PRISMARINE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> SMASHABLE_QUARTZ_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_STONE_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_TUFF_BRICKS;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WAXED_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> SMASHABLE_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> STAR_COIN;
    public static final DeferredBlock<Block> STONE_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> STONE_QUESTION_BRICKS;
    public static final DeferredBlock<Block> STORAGE_AMETHYST_BRICKS;
    public static final DeferredBlock<Block> STORAGE_BLACKSTONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_DARK_PRISMARINE;
    public static final DeferredBlock<Block> STORAGE_DEEPSLATE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_DEEPSLATE_TILES;
    public static final DeferredBlock<Block> STORAGE_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_END_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_MOSSY_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_MUD_BRICKS;
    public static final DeferredBlock<Block> STORAGE_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_POLISHED_DEEP_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_POLISHED_FUNGAL_BRICKS;
    public static final DeferredBlock<Block> STORAGE_PRISMARINE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_PURPUR_BLOCK;
    public static final DeferredBlock<Block> STORAGE_QUARTZ_BRICKS;
    public static final DeferredBlock<Block> STORAGE_RED_NETHER_BRICKS;
    public static final DeferredBlock<Block> STORAGE_RED_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_SANDSTONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_STONE_BRICKS;
    public static final DeferredBlock<Block> STORAGE_TUFF_BRICKS;
    public static final DeferredBlock<Block> STORAGE_WAXED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WAXED_EXPOSED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WAXED_OXIDIZED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WAXED_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> STORAGE_WEATHERED_CUT_COPPER;
    public static final DeferredBlock<Block> TUFF_BRICK_PEDESTAL;
    public static final DeferredBlock<Block> TUFF_QUESTION_BRICKS;
    public static final DeferredBlock<Block> WATER_SPOUT;
    public static final DeferredBlock<Block> WAXED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WAXED_EXPOSED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_EXPOSED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WAXED_OXIDIZED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_OXIDIZED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WAXED_WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WAXED_WEATHERED_CUT_COPPER_PEDESTAL;
    public static final DeferredBlock<Block> WEATHERED_COPPER_QUESTION_BLOCK;
    public static final DeferredBlock<Block> WEATHERED_CUT_COPPER_PEDESTAL;

    static {
        POTTED_PIRANHA_PLANT = registerNoItemBlock("potted_piranha_plant",
                () -> new PottedPiranhaPlantBlock(null, () -> Blocks.AIR,
                        BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY)));


        STAR_COIN = registerNoItemBlock("star_coin",
                () -> new StarCoinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(MarioverseSoundTypes.COIN_TYPE).instrument(NoteBlockInstrument.CHIME)
                        .pushReaction(PushReaction.DESTROY).isSuffocating(BlockRegistry::never)
                        .isViewBlocking(BlockRegistry::never).strength(0.5F, 0.5F)
                        .instabreak().noCollission()));

        COIN = registerBlock("coin",
                () -> new CoinBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(MarioverseSoundTypes.COIN_TYPE).instrument(NoteBlockInstrument.CHIME)
                        .pushReaction(PushReaction.DESTROY).isSuffocating(BlockRegistry::never)
                        .isViewBlocking(BlockRegistry::never).strength(0.5F, 0.5F)
                        .instabreak().noCollission()));


        IRON_SPIKE = registerBlock("iron_spike",
                () -> new IronSpikeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                        .sound(SoundType.NETHERITE_BLOCK).instrument(NoteBlockInstrument.IRON_XYLOPHONE)
                        .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(25.0F, 1200.0F).requiresCorrectToolForDrops()));


        FUNGAL_STONE = registerBlock("fungal_stone",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(2.0F, 6.0F).requiresCorrectToolForDrops()));

        FUNGAL_STONE_BUTTON = registerBlock("fungal_stone_button", () -> button(FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE, 25));

        FUNGAL_STONE_PRESSURE_PLATE = registerBlock("fungal_stone_pressure_plate", () -> pressurePlate(FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE));

        FUNGAL_STONE_SLAB = registerBlock("fungal_stone_slab", () -> slab(FUNGAL_STONE.get()));

        FUNGAL_STONE_STAIRS = registerBlock("fungal_stone_stairs", () -> stair(FUNGAL_STONE.get()));

        FUNGAL_STONE_WALL = registerBlock("fungal_stone_wall", () -> wall(FUNGAL_STONE.get()));


        FUNGAL_BRICKS = registerBlock("fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_STONE.get())));

        FUNGAL_BRICK_SLAB = registerBlock("fungal_brick_slab", () -> slab(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_STAIRS = registerBlock("fungal_brick_stairs", () -> stair(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_WALL = registerBlock("fungal_brick_wall", () -> wall(FUNGAL_BRICKS.get()));

        FUNGAL_BRICK_PEDESTAL = registerBlock("fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));

        CHISELED_FUNGAL_BRICKS = registerBlock("chiseled_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));

        CRACKED_FUNGAL_BRICKS = registerBlock("cracked_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));

        STORAGE_FUNGAL_BRICKS = registerBlock("storage_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(FUNGAL_BRICKS.get())));


        POLISHED_FUNGAL_STONE = registerBlock("polished_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(FUNGAL_STONE.get()).strength(1.5F, 6.0F)));

        POLISHED_FUNGAL_STONE_STAIRS = registerBlock("polished_fungal_stone_stairs", () -> stair(POLISHED_FUNGAL_STONE.get()));

        POLISHED_FUNGAL_STONE_SLAB = registerBlock("polished_fungal_stone_slab", () -> slab(POLISHED_FUNGAL_STONE.get()));

        POLISHED_FUNGAL_STONE_WALL = registerBlock("polished_fungal_stone_wall", () -> wall(POLISHED_FUNGAL_STONE.get()));


        FUNGAL_QUESTION_BLOCK = registerBlock("fungal_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.TERRACOTTA_RED : MapColor.GOLD)));

        INVISIBLE_FUNGAL_QUESTION_BLOCK = registerBlock("invisible_fungal_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.TERRACOTTA_RED
                                : state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.GOLD)));


        POLISHED_FUNGAL_BRICKS = registerBlock("polished_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_STONE.get())));

        POLISHED_FUNGAL_BRICK_SLAB = registerBlock("polished_fungal_brick_slab", () -> slab(POLISHED_FUNGAL_BRICKS.get()));

        POLISHED_FUNGAL_BRICK_STAIRS = registerBlock("polished_fungal_brick_stairs", () -> stair(POLISHED_FUNGAL_BRICKS.get()));

        POLISHED_FUNGAL_BRICK_WALL = registerBlock("polished_fungal_brick_wall", () -> wall(POLISHED_FUNGAL_BRICKS.get()));

        POLISHED_FUNGAL_BRICK_PEDESTAL = registerBlock("polished_fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));

        CHISELED_POLISHED_FUNGAL_BRICKS = registerBlock("chiseled_polished_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));

        CRACKED_POLISHED_FUNGAL_BRICKS = registerBlock("cracked_polished_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));


        STORAGE_POLISHED_FUNGAL_BRICKS = registerBlock("storage_polished_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_FUNGAL_BRICKS.get())));


        DEEP_FUNGAL_STONE = registerBlock("deep_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                        .sound(SoundType.TUFF_BRICKS).instrument(NoteBlockInstrument.BASEDRUM)
                        .strength(3.0F, 6.5F).requiresCorrectToolForDrops()));

        DEEP_FUNGAL_STONE_BUTTON = registerBlock("deep_fungal_stone_button", () -> button(DEEP_FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE, 35));

        DEEP_FUNGAL_STONE_PRESSURE_PLATE = registerBlock("deep_fungal_stone_pressure_plate", () -> pressurePlate(DEEP_FUNGAL_STONE.get(), BlockSetTypeRegistry.FUNGAL_STONE));

        DEEP_FUNGAL_STONE_SLAB = registerBlock("deep_fungal_stone_slab", () -> slab(DEEP_FUNGAL_STONE.get()));

        DEEP_FUNGAL_STONE_STAIRS = registerBlock("deep_fungal_stone_stairs", () -> stair(DEEP_FUNGAL_STONE.get()));

        DEEP_FUNGAL_STONE_WALL = registerBlock("deep_fungal_stone_wall", () -> wall(DEEP_FUNGAL_STONE.get()));


        DEEP_FUNGAL_BRICKS = registerBlock("deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_STONE.get())));

        DEEP_FUNGAL_BRICK_STAIRS = registerBlock("deep_fungal_brick_stairs", () -> stair(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_SLAB = registerBlock("deep_fungal_brick_slab", () -> slab(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_WALL = registerBlock("deep_fungal_brick_wall", () -> wall(DEEP_FUNGAL_BRICKS.get()));

        DEEP_FUNGAL_BRICK_PEDESTAL = registerBlock("deep_fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));

        CHISELED_DEEP_FUNGAL_BRICKS = registerBlock("chiseled_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));

        CRACKED_DEEP_FUNGAL_BRICKS = registerBlock("cracked_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));

        STORAGE_DEEP_FUNGAL_BRICKS = registerBlock("storage_deep_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_BRICKS.get())));


        POLISHED_DEEP_FUNGAL_STONE = registerBlock("polished_deep_fungal_stone",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(DEEP_FUNGAL_STONE.get()).strength(2.5F, 6.0F)));

        POLISHED_DEEP_FUNGAL_STONE_STAIRS = registerBlock("polished_deep_fungal_stone_stairs", () -> stair(POLISHED_DEEP_FUNGAL_STONE.get()));

        POLISHED_DEEP_FUNGAL_STONE_SLAB = registerBlock("polished_deep_fungal_stone_slab", () -> slab(POLISHED_DEEP_FUNGAL_STONE.get()));

        POLISHED_DEEP_FUNGAL_STONE_WALL = registerBlock("polished_deep_fungal_stone_wall", () -> wall(POLISHED_DEEP_FUNGAL_STONE.get()));


        DEEP_FUNGAL_QUESTION_BLOCK = registerBlock("deep_fungal_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.COLOR_CYAN : MapColor.COLOR_GREEN)));

        INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK = registerBlock("invisible_deep_fungal_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())
                        .mapColor(state -> state.getValue(QuestionBlock.EMPTY) ? MapColor.COLOR_CYAN
                                : state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_GREEN)));


        POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("polished_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_STONE.get())));

        POLISHED_DEEP_FUNGAL_BRICK_STAIRS = registerBlock("polished_deep_fungal_brick_stairs", () -> stair(POLISHED_DEEP_FUNGAL_BRICKS.get()));

        POLISHED_DEEP_FUNGAL_BRICK_SLAB = registerBlock("polished_deep_fungal_brick_slab", () -> slab(POLISHED_DEEP_FUNGAL_BRICKS.get()));

        POLISHED_DEEP_FUNGAL_BRICK_WALL = registerBlock("polished_deep_fungal_brick_wall", () -> wall(POLISHED_DEEP_FUNGAL_BRICKS.get()));

        POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL = registerBlock("polished_deep_fungal_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));

        CHISELED_POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("chiseled_polished_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));

        CRACKED_POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("cracked_polished_deep_fungal_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));

        STORAGE_POLISHED_DEEP_FUNGAL_BRICKS = registerBlock("storage_polished_deep_fungal_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_DEEP_FUNGAL_BRICKS.get())));


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
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())));

        CHISELED_AMETHYST_BRICKS = registerBlock("chiseled_amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BRICKS.get())));

        CRACKED_AMETHYST_BRICKS = registerBlock("cracked_amethyst_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BRICKS.get())));

        AMETHYST_BRICK_SLAB = registerBlock("amethyst_brick_slab", () -> slab(AMETHYST_BRICKS.get()));

        AMETHYST_BRICK_STAIRS = registerBlock("amethyst_brick_stairs", () -> stair(AMETHYST_BRICKS.get()));

        AMETHYST_BRICK_WALL = registerBlock("amethyst_brick_wall", () -> wall(AMETHYST_BRICKS.get()));

        AMETHYST_BRICK_PEDESTAL = registerBlock("amethyst_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(AMETHYST_BRICKS.get())));


        AMETHYST_QUESTION_BLOCK = registerBlock("amethyst_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())));

        INVISIBLE_AMETHYST_QUESTION_BLOCK = registerBlock("invisible_amethyst_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_PURPLE)));

        STORAGE_AMETHYST_BRICKS = registerBlock("storage_amethyst_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(POLISHED_AMETHYST.get())));


        SANDSTONE_BRICKS = registerBlock("sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.SANDSTONE)));

        SANDSTONE_BRICK_SLAB = registerBlock("sandstone_brick_slab", () -> slab(SANDSTONE_BRICKS.get()));

        SANDSTONE_BRICK_STAIRS = registerBlock("sandstone_brick_stairs", () -> stair(SANDSTONE_BRICKS.get()));

        SANDSTONE_BRICK_WALL = registerBlock("sandstone_brick_wall", () -> wall(SANDSTONE_BRICKS.get()));

        SANDSTONE_BRICK_PEDESTAL = registerBlock("sandstone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));

        CHISELED_SANDSTONE_BRICKS = registerBlock("chiseled_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));

        CRACKED_SANDSTONE_BRICKS = registerBlock("cracked_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));


        SANDSTONE_QUESTION_BLOCK = registerBlock("sandstone_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE)));

        INVISIBLE_SANDSTONE_QUESTION_BLOCK = registerBlock("invisible_sandstone_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_SANDSTONE)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.SAND)));

        STORAGE_SANDSTONE_BRICKS = registerBlock("storage_sandstone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(SANDSTONE_BRICKS.get())));


        RED_SANDSTONE_BRICKS = registerBlock("red_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_SANDSTONE)));

        RED_SANDSTONE_BRICK_SLAB = registerBlock("red_sandstone_brick_slab", () -> slab(RED_SANDSTONE_BRICKS.get()));

        RED_SANDSTONE_BRICK_STAIRS = registerBlock("red_sandstone_brick_stairs", () -> stair(RED_SANDSTONE_BRICKS.get()));

        RED_SANDSTONE_BRICK_WALL = registerBlock("red_sandstone_brick_wall", () -> wall(RED_SANDSTONE_BRICKS.get()));

        RED_SANDSTONE_BRICK_PEDESTAL = registerBlock("red_sandstone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));

        CHISELED_RED_SANDSTONE_BRICKS = registerBlock("chiseled_red_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));

        CRACKED_RED_SANDSTONE_BRICKS = registerBlock("cracked_red_sandstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));


        RED_SANDSTONE_QUESTION_BLOCK = registerBlock("red_sandstone_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_RED_SANDSTONE)));

        INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK = registerBlock("invisible_red_sandstone_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHISELED_RED_SANDSTONE)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_ORANGE)));

        STORAGE_RED_SANDSTONE_BRICKS = registerBlock("storage_red_sandstone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(RED_SANDSTONE_BRICKS.get())));


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


        MOSSY_STONE_QUESTION_BRICKS = registerBlock("mossy_stone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));

        INVISIBLE_MOSSY_STONE_QUESTION_BRICKS = registerBlock("invisible_mossy_stone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.STONE)));

        STORAGE_MOSSY_STONE_BRICKS = registerBlock("storage_mossy_stone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));

        SMASHABLE_MOSSY_STONE_BRICKS = registerBlock("smashable_mossy_stone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));

        MOSSY_STONE_BRICK_PEDESTAL = registerBlock("mossy_stone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSSY_STONE_BRICKS)));


        TUFF_QUESTION_BRICKS = registerBlock("tuff_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));

        INVISIBLE_TUFF_QUESTION_BRICKS = registerBlock("invisible_tuff_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_GRAY)));

        STORAGE_TUFF_BRICKS = registerBlock("storage_tuff_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));

        SMASHABLE_TUFF_BRICKS = registerBlock("smashable_tuff_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));

        TUFF_BRICK_PEDESTAL = registerBlock("tuff_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TUFF_BRICKS)));


        DEEPSLATE_QUESTION_BRICKS = registerBlock("deepslate_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));

        INVISIBLE_DEEPSLATE_QUESTION_BRICKS = registerBlock("invisible_deepslate_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DEEPSLATE)));

        STORAGE_DEEPSLATE_BRICKS = registerBlock("storage_deepslate_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));

        SMASHABLE_DEEPSLATE_BRICKS = registerBlock("smashable_deepslate_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));

        DEEPSLATE_BRICK_PEDESTAL = registerBlock("deepslate_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICKS)));


        DEEPSLATE_QUESTION_TILES = registerBlock("deepslate_question_tiles",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));

        INVISIBLE_DEEPSLATE_QUESTION_TILES = registerBlock("invisible_deepslate_question_tiles",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DEEPSLATE)));

        STORAGE_DEEPSLATE_TILES = registerBlock("storage_deepslate_tiles",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));

        SMASHABLE_DEEPSLATE_TILES = registerBlock("smashable_deepslate_tiles",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));

        DEEPSLATE_TILE_PEDESTAL = registerBlock("deepslate_tile_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_TILES)));


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


        PRISMARINE_QUESTION_BRICKS = registerBlock("prismarine_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));

        INVISIBLE_PRISMARINE_QUESTION_BRICKS = registerBlock("invisible_prismarine_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DIAMOND)));

        STORAGE_PRISMARINE_BRICKS = registerBlock("storage_prismarine_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));

        SMASHABLE_PRISMARINE_BRICKS = registerBlock("smashable_prismarine_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));

        PRISMARINE_BRICK_PEDESTAL = registerBlock("prismarine_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)));


        DARK_PRISMARINE_QUESTION_BLOCK = registerBlock("dark_prismarine_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));

        INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK = registerBlock("invisible_dark_prismarine_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.DIAMOND)));

        STORAGE_DARK_PRISMARINE = registerBlock("storage_dark_prismarine",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));

        SMASHABLE_DARK_PRISMARINE = registerBlock("smashable_dark_prismarine",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));

        DARK_PRISMARINE_PEDESTAL = registerBlock("dark_prismarine_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)));


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


        BLACKSTONE_QUESTION_BRICKS = registerBlock("blackstone_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

        INVISIBLE_BLACKSTONE_QUESTION_BRICKS = registerBlock("invisible_blackstone_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_BLACK)));

        STORAGE_BLACKSTONE_BRICKS = registerBlock("storage_blackstone_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

        SMASHABLE_BLACKSTONE_BRICKS = registerBlock("smashable_blackstone_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));

        BLACKSTONE_BRICK_PEDESTAL = registerBlock("blackstone_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POLISHED_BLACKSTONE_BRICKS)));


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


        QUARTZ_QUESTION_BRICKS = registerBlock("quartz_question_bricks",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));

        INVISIBLE_QUARTZ_QUESTION_BRICKS = registerBlock("invisible_quartz_question_bricks",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.QUARTZ)));

        STORAGE_QUARTZ_BRICKS = registerBlock("storage_quartz_bricks",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));

        SMASHABLE_QUARTZ_BRICKS = registerBlock("smashable_quartz_bricks",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));

        QUARTZ_BRICK_PEDESTAL = registerBlock("quartz_brick_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.QUARTZ_BRICKS)));


        COPPER_QUESTION_BLOCK = registerBlock("copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)));

        INVISIBLE_COPPER_QUESTION_BLOCK = registerBlock("invisible_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_ORANGE)));

        STORAGE_CUT_COPPER = registerBlock("storage_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_COPPER)));

        SMASHABLE_CUT_COPPER = registerBlock("smashable_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_COPPER)));

        CUT_COPPER_PEDESTAL = registerBlock("cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.UNAFFECTED, BlockBehaviour.Properties.ofFullCopy(Blocks.CUT_COPPER)));


        EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("exposed_copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));

        INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("invisible_exposed_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_LIGHT_GRAY)));

        STORAGE_EXPOSED_CUT_COPPER = registerBlock("storage_exposed_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));

        SMASHABLE_EXPOSED_CUT_COPPER = registerBlock("smashable_exposed_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));

        EXPOSED_CUT_COPPER_PEDESTAL = registerBlock("exposed_cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.EXPOSED, BlockBehaviour.Properties.ofFullCopy(Blocks.EXPOSED_COPPER)));


        WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("weathered_copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));

        INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("invisible_weathered_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_STEM)));

        STORAGE_WEATHERED_CUT_COPPER = registerBlock("storage_weathered_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));

        SMASHABLE_WEATHERED_CUT_COPPER = registerBlock("smashable_weathered_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));

        WEATHERED_CUT_COPPER_PEDESTAL = registerBlock("weathered_cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.WEATHERED, BlockBehaviour.Properties.ofFullCopy(Blocks.WEATHERED_COPPER)));


        OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("oxidized_copper_question_block",
                () -> new WeatheringCopperQuestionBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));

        INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("invisible_oxidized_copper_question_block",
                () -> new WeatheringCopperInvisibleQuestionBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_NYLIUM)));

        STORAGE_OXIDIZED_CUT_COPPER = registerBlock("storage_oxidized_cut_copper",
                () -> new WeatheringCopperStorageBrickBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));

        SMASHABLE_OXIDIZED_CUT_COPPER = registerBlock("smashable_oxidized_cut_copper",
                () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));

        OXIDIZED_CUT_COPPER_PEDESTAL = registerBlock("oxidized_cut_copper_pedestal",
                () -> new WeatheringCopperPedestalBlock(WeatheringCopper.WeatherState.OXIDIZED, BlockBehaviour.Properties.ofFullCopy(Blocks.OXIDIZED_COPPER)));


        WAXED_COPPER_QUESTION_BLOCK = registerBlock("waxed_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_BLOCK)));

        INVISIBLE_WAXED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_COPPER_BLOCK)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.COLOR_ORANGE)));

        STORAGE_WAXED_CUT_COPPER = registerBlock("storage_waxed_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_CUT_COPPER)));

        SMASHABLE_WAXED_CUT_COPPER = registerBlock("smashable_waxed_cut_copper",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_CUT_COPPER)));

        WAXED_CUT_COPPER_PEDESTAL = registerBlock("waxed_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_CUT_COPPER)));


        WAXED_EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("waxed_exposed_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));

        INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_exposed_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.TERRACOTTA_LIGHT_GRAY)));

        STORAGE_WAXED_EXPOSED_CUT_COPPER = registerBlock("storage_waxed_exposed_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));

        SMASHABLE_WAXED_EXPOSED_CUT_COPPER = registerBlock("smashable_waxed_exposed_cut_copper",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));

        WAXED_EXPOSED_CUT_COPPER_PEDESTAL = registerBlock("waxed_exposed_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_EXPOSED_COPPER)));


        WAXED_WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("waxed_weathered_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));

        INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_weathered_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_STEM)));

        STORAGE_WAXED_WEATHERED_CUT_COPPER = registerBlock("storage_waxed_weathered_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));

        SMASHABLE_WAXED_WEATHERED_CUT_COPPER = registerBlock("smashable_waxed_weathered_cut_copper",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));

        WAXED_WEATHERED_CUT_COPPER_PEDESTAL = registerBlock("waxed_weathered_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_WEATHERED_COPPER)));


        WAXED_OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("waxed_oxidized_copper_question_block",
                () -> new QuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));

        INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK = registerBlock("invisible_waxed_oxidized_copper_question_block",
                () -> new InvisibleQuestionBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)
                        .mapColor(state -> state.getValue(InvisibleQuestionBlock.INVISIBLE) ? MapColor.NONE : MapColor.WARPED_NYLIUM)));

        STORAGE_WAXED_OXIDIZED_CUT_COPPER = registerBlock("storage_waxed_oxidized_cut_copper",
                () -> new StorageBrickBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));

        SMASHABLE_WAXED_OXIDIZED_CUT_COPPER = registerBlock("smashable_waxed_oxidized_cut_copper",
                () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));

        WAXED_OXIDIZED_CUT_COPPER_PEDESTAL = registerBlock("waxed_oxidized_cut_copper_pedestal",
                () -> new BrickPedestalBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WAXED_OXIDIZED_COPPER)));


        Arrays.stream(DyeColor.values()).filter(color -> color != DyeColor.WHITE).forEach(color ->
                CALCITE.put(color, registerBlock(color.getName() + "_calcite",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE).mapColor(color)))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                POLISHED_CALCITE.put(color, registerBlock("polished_" + color.getName() + "_calcite",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CALCITE_BRICKS.put(color, registerBlock(color.getName() + "_calcite_bricks",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CHISELED_CALCITE_BRICKS.put(color, registerBlock("chiseled_" + color.getName() + "_calcite_bricks",
                        () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.CALCITE)
                                .mapColor(color.getName().equals(DyeColor.WHITE.getName()) ? MapColor.TERRACOTTA_WHITE : color.getMapColor())))));

        CLASSIC_CHECKPOINT_FLAG = registerNoItemBlock("classic_checkpoint_flag",
                () -> new CheckpointFlagBlock(null, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                        .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                        .strength(2.5F, 3.0F).requiresCorrectToolForDrops().noCollission()));

        Arrays.stream(DyeColor.values()).forEach(color ->
                CHECKPOINT_FLAGS.put(color, registerNoItemBlock(color.getName() + "_checkpoint_flag",
                        () -> new CheckpointFlagBlock(color, BlockBehaviour.Properties.of().mapColor(MapColor.GOLD)
                                .sound(SoundType.NETHERITE_BLOCK).isViewBlocking(BlockRegistry::never)
                                .strength(2.5F, 3.0F).requiresCorrectToolForDrops().noCollission()))));


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
                () -> new ClearWarpPipeBlock(null, BlockBehaviour.Properties.of().mapColor(MapColor.NONE)
                        .sound(MarioverseSoundTypes.CLEAR_PIPE).instrument(NoteBlockInstrument.CHIME)
                        .isSuffocating(BlockRegistry::never).isViewBlocking(BlockRegistry::never)
                        .strength(3.0F, 500.0F).requiresCorrectToolForDrops().noOcclusion()));

        // Keep below CLEAR_WARP_PIPE to prevent crash
        Arrays.stream(DyeColor.values()).forEach(color ->
                WARP_PIPES.put(color, registerBlock(color.getName() + "_warp_pipe",
                        () -> new WarpPipeBlock(color, BlockBehaviour.Properties.of().mapColor(color)
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
        return new ButtonBlock(blockSetType, ticksPressed, BlockBehaviour.Properties.ofFullCopy(block).noCollission());
    }

    private static Block pressurePlate(Block block, BlockSetType blockSetType) {
        return new PressurePlateBlock(blockSetType, BlockBehaviour.Properties.ofFullCopy(block).noCollission());
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
