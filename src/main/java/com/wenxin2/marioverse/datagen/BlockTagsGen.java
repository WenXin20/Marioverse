package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class BlockTagsGen extends BlockTagsProvider {
    private static final ResourceLocation CREATE_CALCITE_BRICKS = ResourceLocation.fromNamespaceAndPath("create", "cut_calcite_bricks");
    private static final ResourceLocation CREATE_POLISHED_CALCITE = ResourceLocation.fromNamespaceAndPath("create", "polished_cut_calcite");
    private static final ResourceLocation BB_BLUE_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "blue_bigshroom_block");
    private static final ResourceLocation BB_GREEN_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "green_bigshroom_block");
    private static final ResourceLocation BB_LIME_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "lime_bigshroom_block");
    private static final ResourceLocation BB_ORANGE_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "orange_bigshroom_block");
    private static final ResourceLocation BB_PURPLE_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "purple_bigshroom_block");
    private static final ResourceLocation BB_RED_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "red_bigshroom_block");
    private static final ResourceLocation BB_YELLOW_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "yellow_bigshroom_block");
    private static final ResourceLocation PC_CRYSTALLIZED_CACTUS = ResourceLocation.fromNamespaceAndPath("pokecube_legends", "crystallized_cactus");
    private static final ResourceLocation SUPP_FIRE_PIT = ResourceLocation.fromNamespaceAndPath("supplementaries", "fire_pit");

    public BlockTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Marioverse.MOD_ID, existingFileHelper);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags(HolderLookup.Provider lookupProvider) {
        BlockRegistry.CALCITE.values().forEach(block -> tag(TagRegistry.CALCITE_BLOCKS).add(block.get()));
        BlockRegistry.CALCITE_BRICKS.values().forEach(block -> tag(TagRegistry.CALCITE_BRICK_BLOCKS).add(block.get()));
        BlockRegistry.CALCITE_BRICK_PEDESTAL.values().forEach(block -> tag(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS).add(block.get()));
        BlockRegistry.CHECKPOINT_FLAGS.values().forEach(block -> tag(TagRegistry.DYEABLE_CHECKPOINT_FLAG_BLOCKS).add(block.get()));
        BlockRegistry.CHISELED_CALCITE_BRICKS.values().forEach(block -> tag(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS).add(block.get()));
        BlockRegistry.CRACKED_CALCITE_BRICKS.values().forEach(block -> tag(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS).add(block.get()));
        BlockRegistry.GOAL_POLES.values().forEach(block -> tag(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS).add(block.get()));
        BlockRegistry.POLISHED_CALCITE.values().forEach(block -> tag(TagRegistry.POLISHED_CALCITE_BLOCKS).add(block.get()));
        BlockRegistry.STORAGE_CALCITE_BRICKS.values().forEach(block -> tag(TagRegistry.STORAGE_CALCITE_BRICK_BLOCKS).add(block.get()));
        BlockRegistry.WARP_PIPES.values().forEach(block -> tag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS).add(block.get()));

        for (DyeColor color : DyeColor.values()) {
            if (color == DyeColor.WHITE)
                tag(TagRegistry.blockTags("c", "dyed/" + color)).add(Blocks.CALCITE);
            else tag(TagRegistry.blockTags("c", "dyed/" + color))
                    .add(BlockRegistry.CALCITE.get(color).get());

            tag(TagRegistry.blockTags("c", "dyed/" + color))
                    .add(BlockRegistry.CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(color).get())
                    .add(BlockRegistry.CHECKPOINT_FLAGS.get(color).get())
                    .add(BlockRegistry.CHISELED_CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.CRACKED_CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.GOAL_POLES.get(color).get())
                    .add(BlockRegistry.POLISHED_CALCITE.get(color).get())
                    .add(BlockRegistry.STORAGE_CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.WARP_PIPES.get(color).get());
        }

        tag(CompatRegistry.CREATE_BRITTLE)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .add(BlockRegistry.STAR_COIN.get());

        tag(CompatRegistry.CREATE_COPYCAT_ALLOW)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS);

        tag(CompatRegistry.CREATE_MOVABLE_EMPTY_COLLIDER)
                .addTag(TagRegistry.BRIDGE_STAIR_BLOCKS)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .add(BlockRegistry.CLEAR_WARP_PIPE.get())
                .add(BlockRegistry.COIN.get())
                .add(BlockRegistry.QUICKSAND.get())
                .add(BlockRegistry.RED_QUICKSAND.get())
                .add(BlockRegistry.SPIKE_PANEL.get())
                .add(BlockRegistry.STAR_COIN.get())
                .add(BlockRegistry.WATER_SPOUT.get());

        tag(CompatRegistry.CREATE_SAFE_NBT)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS);

        tag(CompatRegistry.CREATE_SIMPLE_MOUNTED_STORAGE)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS);

        tag(CompatRegistry.CREATE_SINGLE_BLOCK_INVENTORIES); // Causes animated textures to unanimate

        tag(TagRegistry.BONKABLE_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .add(BlockRegistry.CHISELED_POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.POLISHED_AMETHYST.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get());

        tag(TagRegistry.BOUNCY_BLOCKS)
                .add(Blocks.BROWN_MUSHROOM_BLOCK)
                .add(Blocks.RED_MUSHROOM_BLOCK)
                .addOptional(BB_BLUE_BIGSHROOM)
                .addOptional(BB_GREEN_BIGSHROOM)
                .addOptional(BB_LIME_BIGSHROOM)
                .addOptional(BB_ORANGE_BIGSHROOM)
                .addOptional(BB_PURPLE_BIGSHROOM)
                .addOptional(BB_RED_BIGSHROOM)
                .addOptional(BB_YELLOW_BIGSHROOM);

        tag(TagRegistry.BRICK_PEDESTAL_BLOCKS)
                .addTag(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS)
                .add(BlockRegistry.AMETHYST_BRICK_PEDESTAL.get())
                .add(BlockRegistry.BLACKSTONE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.BRICK_PEDESTAL.get())
                .add(BlockRegistry.CUT_COPPER_PEDESTAL.get())
                .add(BlockRegistry.DARK_PRISMARINE_PEDESTAL.get())
                .add(BlockRegistry.DEEPSLATE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEPSLATE_TILE_PEDESTAL.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.END_STONE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL.get())
                .add(BlockRegistry.FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.MUD_BRICK_PEDESTAL.get())
                .add(BlockRegistry.NETHER_BRICK_PEDESTAL.get())
                .add(BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.PRISMARINE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.PURPUR_BLOCK_PEDESTAL.get())
                .add(BlockRegistry.QUARTZ_BRICK_PEDESTAL.get())
                .add(BlockRegistry.RED_NETHER_BRICK_PEDESTAL.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.SANDSTONE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.STONE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.TUFF_BRICK_PEDESTAL.get())
                .add(BlockRegistry.WAXED_CUT_COPPER_PEDESTAL.get())
                .add(BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL.get())
                .add(BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL.get())
                .add(BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL.get())
                .add(BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL.get());

        tag(TagRegistry.BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS);

        tag(TagRegistry.BRIDGE_STAIR_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS);

        tag(TagRegistry.CALCITE_BLOCKS)
                .add(Blocks.CALCITE);

        tag(TagRegistry.CALCITE_BRICK_BLOCKS)
                .addOptional(CREATE_CALCITE_BRICKS);

        tag(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS);

        tag(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS);

        tag(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS);

        tag(TagRegistry.POLISHED_CALCITE_BLOCKS)
                .addOptional(CREATE_POLISHED_CALCITE);

        tag(TagRegistry.STORAGE_CALCITE_BRICK_BLOCKS);

        tag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.DYEABLE_CHECKPOINT_FLAG_BLOCKS)
                .add(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get());

        tag(TagRegistry.DOTTED_LINE_BLOCKS)
                .add(BlockRegistry.BLUE_DOTTED_LINE_BLOCK.get())
                .add(BlockRegistry.RED_DOTTED_LINE_BLOCK.get());

        tag(TagRegistry.DYED_CALCITE_BLOCKS)
                .addTag(TagRegistry.CALCITE_BLOCKS)
                .addTag(TagRegistry.CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS)
                .addTag(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.POLISHED_CALCITE_BLOCKS);

        tag(TagRegistry.FIREBALL_SETS_ON_FIRE)
                .addTag(BlockTags.INFINIBURN_END)
                .addTag(BlockTags.INFINIBURN_END)
                .addTag(BlockTags.INFINIBURN_NETHER)
                .addTag(BlockTags.INFINIBURN_OVERWORLD)
                .addTag(BlockTags.SOUL_FIRE_BASE_BLOCKS)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.CAMPFIRES)
                .addTag(BlockTags.CANDLE_CAKES)
                .addOptionalTag(CompatRegistry.BUMBLEZONE_CANDLES)
                .addOptionalTag(CompatRegistry.SUPP_CANDLE_HOLDERS)
                .addOptionalTag(CompatRegistry.SUPP_SCONCES)
                .addOptional(SUPP_FIRE_PIT);

        tag(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_BLOCKS)
                .add(BlockRegistry.ACACIA_LOG_BRIDGE.get())
                .add(BlockRegistry.BAMBOO_BRIDGE.get())
                .add(BlockRegistry.BIRCH_LOG_BRIDGE.get())
                .add(BlockRegistry.CHERRY_LOG_BRIDGE.get())
                .add(BlockRegistry.DARK_OAK_LOG_BRIDGE.get())
                .add(BlockRegistry.JUNGLE_LOG_BRIDGE.get())
                .add(BlockRegistry.MANGROVE_LOG_BRIDGE.get())
                .add(BlockRegistry.OAK_LOG_BRIDGE.get())
                .add(BlockRegistry.SPRUCE_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_BAMBOO_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_OAK_LOG_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE.get());

        tag(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_STAIR_BLOCKS)
                .add(BlockRegistry.ACACIA_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.BAMBOO_BRIDGE_STAIRS.get())
                .add(BlockRegistry.BIRCH_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.CHERRY_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.DARK_OAK_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.JUNGLE_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.MANGROVE_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.OAK_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.SPRUCE_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_ACACIA_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_BAMBOO_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_BIRCH_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_CHERRY_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_DARK_OAK_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_JUNGLE_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_MANGROVE_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_OAK_LOG_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_SPRUCE_LOG_BRIDGE_STAIRS.get());

        tag(TagRegistry.FREEZES_INTO_PACKED_ICE)
                .add(Blocks.ICE);

        tag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS)
                .add(BlockRegistry.CLASSIC_GOAL_POLE.get());

        tag(TagRegistry.ICE_BALL_EXTINGUISHES)
                .addTag(BlockTags.FIRE)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.CAMPFIRES)
                .addTag(BlockTags.CANDLE_CAKES)
                .addOptionalTag(CompatRegistry.BUMBLEZONE_CANDLES)
                .addOptionalTag(CompatRegistry.SUPP_CANDLE_HOLDERS)
                .addOptionalTag(CompatRegistry.SUPP_SCONCES)
                .addOptional(SUPP_FIRE_PIT);

        tag(TagRegistry.ICE_CUBE_EXTINGUISHES)
                .addTag(BlockTags.FIRE)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.CAMPFIRES)
                .addTag(BlockTags.CANDLE_CAKES)
                .addOptionalTag(CompatRegistry.BUMBLEZONE_CANDLES)
                .addOptionalTag(CompatRegistry.SUPP_CANDLE_HOLDERS)
                .addOptionalTag(CompatRegistry.SUPP_SCONCES)
                .addOptional(SUPP_FIRE_PIT);

        tag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .add(BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_BLACKSTONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_CALCITE_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_DARK_PRISMARINE_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_DEEPSLATE_QUESTION_TILES.get())
                .add(BlockRegistry.INVISIBLE_DEEP_FUNGAL_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_FUNGAL_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_MUD_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_PRISMARINE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_PURPUR_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_QUARTZ_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_RED_SANDSTONE_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_SANDSTONE_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_TUFF_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK.get());

        tag(TagRegistry.MEGA_MUSHROOM_CAN_BREAK)
                .addTag(Tags.Blocks.GLASS_BLOCKS)
                .addTag(Tags.Blocks.GLASS_PANES)
                .addTag(Tags.Blocks.FENCES_WOODEN)
                .addTag(Tags.Blocks.FENCE_GATES_WOODEN)
                .addTag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.SMASHABLE_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.CAVE_VINES)
                .addTag(BlockTags.CROPS)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(BlockTags.MINEABLE_WITH_HOE)
                .addTag(BlockTags.MINEABLE_WITH_SHOVEL)
                .addTag(BlockTags.REPLACEABLE)
                .addTag(BlockTags.SWORD_EFFICIENT)
                .add(Blocks.FROSTED_ICE)
                .add(Blocks.ICE)
                .add(Blocks.SHORT_GRASS)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.TORCH)
                .add(Blocks.WALL_TORCH);

        tag(TagRegistry.MEGA_MUSHROOM_CAN_BREAK_IN_ADVENTURE_MODE)
                .addTag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.SMASHABLE_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.CAVE_VINES)
                .addTag(BlockTags.CROPS)
                .addTag(BlockTags.FLOWERS)
                .add(Blocks.FROSTED_ICE)
                .add(Blocks.ICE)
                .add(Blocks.SHORT_GRASS)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.TORCH)
                .add(Blocks.WALL_TORCH);

        tag(TagRegistry.MEGA_MUSHROOM_CAN_BREAK_WHEN_FALLING)
                .addTag(Tags.Blocks.GLASS_BLOCKS)
                .addTag(Tags.Blocks.GLASS_PANES)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.SMASHABLE_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.CAVE_VINES)
                .addTag(BlockTags.CROPS)
                .addTag(BlockTags.FLOWERS)
                .addTag(BlockTags.LEAVES)
                .addTag(BlockTags.ICE)
                .addTag(BlockTags.REPLACEABLE)
                .addTag(BlockTags.SWORD_EFFICIENT)
                .addTag(BlockTags.WOOL_CARPETS)
                .add(Blocks.FROSTED_ICE)
                .add(Blocks.ICE)
                .add(Blocks.SHORT_GRASS)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.TORCH)
                .add(Blocks.WALL_TORCH);

        tag(TagRegistry.MELTS)
                .add(Blocks.POWDER_SNOW)
                .add(Blocks.SNOW);

        tag(TagRegistry.MELTS_ICE_CUBE)
                .addTag(BlockTags.FIRE)
                .addTag(BlockTags.CAMPFIRES)
                .add(Blocks.MAGMA_BLOCK)
                .addOptional(SUPP_FIRE_PIT);

        tag(TagRegistry.MELTS_INTO_ICE)
                .add(Blocks.PACKED_ICE);

        tag(TagRegistry.MELTS_INTO_PACKED_ICE)
                .add(Blocks.BLUE_ICE);

        tag(TagRegistry.MELTS_INTO_WATER)
                .add(Blocks.FROSTED_ICE)
                .add(Blocks.ICE);

        tag(TagRegistry.MELTS_SNOWBALL)
                .addTag(BlockTags.FIRE)
                .addTag(BlockTags.CAMPFIRES)
                .add(Blocks.MAGMA_BLOCK)
                .addOptional(SUPP_FIRE_PIT);

        tag(TagRegistry.MUSHROOM_TRAMPOLINE_BLOCKS)
                .add(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.get())
                .add(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.get());

        tag(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .add(Blocks.DECORATED_POT);

        tag(TagRegistry.PIRANHA_PLANTS_CANNOT_ATTACH)
                .addTag(BlockTags.ICE);

        tag(TagRegistry.PIRANHA_PLANTS_SPAWNABLE_ON)
                .addTag(BlockTags.DIRT);

        tag(TagRegistry.QUESTION_BLOCKS)
                .add(BlockRegistry.AMETHYST_QUESTION_BLOCK.get())
                .add(BlockRegistry.BLACKSTONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.CALCITE_QUESTION_BLOCK.get())
                .add(BlockRegistry.COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK.get())
                .add(BlockRegistry.DEEPSLATE_QUESTION_BRICKS.get())
                .add(BlockRegistry.DEEPSLATE_QUESTION_TILES.get())
                .add(BlockRegistry.DEEP_FUNGAL_QUESTION_BLOCK.get())
                .add(BlockRegistry.END_STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.FUNGAL_QUESTION_BLOCK.get())
                .add(BlockRegistry.MOSSY_STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.MUD_QUESTION_BRICKS.get())
                .add(BlockRegistry.NETHER_QUESTION_BRICKS.get())
                .add(BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.PRISMARINE_QUESTION_BRICKS.get())
                .add(BlockRegistry.PURPUR_QUESTION_BLOCK.get())
                .add(BlockRegistry.QUARTZ_QUESTION_BRICKS.get())
                .add(BlockRegistry.QUESTION_BRICKS.get())
                .add(BlockRegistry.RED_NETHER_QUESTION_BRICKS.get())
                .add(BlockRegistry.RED_SANDSTONE_QUESTION_BLOCK.get())
                .add(BlockRegistry.SANDSTONE_QUESTION_BLOCK.get())
                .add(BlockRegistry.STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.TUFF_QUESTION_BRICKS.get())
                .add(BlockRegistry.WAXED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK.get())
                .add(BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK.get());

        tag(TagRegistry.QUESTION_PANEL_BLOCKS)
                .add(BlockRegistry.DEEP_FUNGAL_QUESTION_PANEL.get())
                .add(BlockRegistry.FUNGAL_QUESTION_PANEL.get());

        tag(TagRegistry.SMASHABLE_BLOCKS)
                .addTag(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS)
                .add(BlockRegistry.AMETHYST_BRICKS.get())
                .add(BlockRegistry.AMETHYST_BRICK_PEDESTAL.get())
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CHISELED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.CRACKED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.FUNGAL_BRICKS.get())
                .add(BlockRegistry.FUNGAL_BRICK_PEDESTAL.get())
                .add(BlockRegistry.FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.SMASHABLE_BLACKSTONE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_CUT_COPPER.get())
                .add(BlockRegistry.SMASHABLE_DARK_PRISMARINE.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_TILES.get())
                .add(BlockRegistry.SMASHABLE_END_STONE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER.get())
                .add(BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_MUD_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_NETHER_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER.get())
                .add(BlockRegistry.SMASHABLE_PRISMARINE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_PURPUR_BLOCK.get())
                .add(BlockRegistry.SMASHABLE_QUARTZ_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_RED_NETHER_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_STONE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_TUFF_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_WAXED_CUT_COPPER.get())
                .add(BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER.get())
                .add(BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER.get())
                .add(BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER.get())
                .add(BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER.get())
                .add(Blocks.DECORATED_POT);

        tag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.STORAGE_CALCITE_BRICK_BLOCKS)
                .add(BlockRegistry.STORAGE_AMETHYST_BRICKS.get())
                .add(BlockRegistry.STORAGE_BLACKSTONE_BRICKS.get())
                .add(BlockRegistry.STORAGE_BRICKS.get())
                .add(BlockRegistry.STORAGE_CUT_COPPER.get())
                .add(BlockRegistry.STORAGE_DARK_PRISMARINE.get())
                .add(BlockRegistry.STORAGE_DEEPSLATE_BRICKS.get())
                .add(BlockRegistry.STORAGE_DEEPSLATE_TILES.get())
                .add(BlockRegistry.STORAGE_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.STORAGE_END_STONE_BRICKS.get())
                .add(BlockRegistry.STORAGE_EXPOSED_CUT_COPPER.get())
                .add(BlockRegistry.STORAGE_FUNGAL_BRICKS.get())
                .add(BlockRegistry.STORAGE_MOSSY_STONE_BRICKS.get())
                .add(BlockRegistry.STORAGE_MUD_BRICKS.get())
                .add(BlockRegistry.STORAGE_NETHER_BRICKS.get())
                .add(BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER.get())
                .add(BlockRegistry.STORAGE_POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.STORAGE_POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.STORAGE_PRISMARINE_BRICKS.get())
                .add(BlockRegistry.STORAGE_PURPUR_BLOCK.get())
                .add(BlockRegistry.STORAGE_QUARTZ_BRICKS.get())
                .add(BlockRegistry.STORAGE_RED_NETHER_BRICKS.get())
                .add(BlockRegistry.STORAGE_RED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.STORAGE_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.STORAGE_STONE_BRICKS.get())
                .add(BlockRegistry.STORAGE_TUFF_BRICKS.get())
                .add(BlockRegistry.STORAGE_WAXED_CUT_COPPER.get())
                .add(BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER.get())
                .add(BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER.get())
                .add(BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER.get())
                .add(BlockRegistry.STORAGE_WEATHERED_CUT_COPPER.get());

        tag(TagRegistry.SUPPORTS_DANGO_BLOSSOM)
                .addTag(BlockTags.LEAVES)
                .add(Blocks.CACTUS)
                .addOptional(PC_CRYSTALLIZED_CACTUS);

        tag(TagRegistry.WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)
                .add(BlockRegistry.CLEAR_WARP_PIPE.get());

        tag(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .addTag(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_BLOCKS)
                .add(BlockRegistry.CRIMSON_STEM_BRIDGE.get())
                .add(BlockRegistry.WARPED_STEM_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE.get());

        tag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS)
                .addTag(TagRegistry.FLAMMABLE_WOODEN_BRIDGE_STAIR_BLOCKS)
                .add(BlockRegistry.CRIMSON_STEM_BRIDGE_STAIRS.get())
                .add(BlockRegistry.WARPED_STEM_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE_STAIRS.get());

        tag(TagRegistry.WRENCH_EFFICIENT)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS);

        tag(Tags.Blocks.PUMPKINS_CARVED)
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get());

        tag(Tags.Blocks.PUMPKINS_JACK_O_LANTERNS)
                .add(BlockRegistry.SPLUNKIN_O_LANTERN.get());

        tag(Tags.Blocks.SANDS_COLORLESS)
                .add(BlockRegistry.QUICKSAND.get());

        tag(Tags.Blocks.SANDS_RED)
                .add(BlockRegistry.RED_QUICKSAND.get());

        tag(Tags.Blocks.STONES)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE.get());

        tag(BlockTags.ANCIENT_CITY_REPLACEABLE)
                .add(BlockRegistry.DEEPSLATE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEPSLATE_TILE_PEDESTAL.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_TILES.get());

        tag(BlockTags.BASE_STONE_OVERWORLD)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE.get());

        tag(BlockTags.CRYSTAL_SOUND_BLOCKS)
                .add(BlockRegistry.AMETHYST_BRICKS.get())
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.AMETHYST_BUTTON.get())
                .add(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
                .add(BlockRegistry.AMETHYST_QUESTION_BLOCK.get())
                .add(BlockRegistry.AMETHYST_SLAB.get())
                .add(BlockRegistry.AMETHYST_STAIRS.get())
                .add(BlockRegistry.AMETHYST_WALL.get())
                .add(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_AMETHYST_QUESTION_BLOCK.get())
                .add(BlockRegistry.POLISHED_AMETHYST.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.STORAGE_AMETHYST_BRICKS.get());

        tag(BlockTags.ENDERMAN_HOLDABLE)
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.GLOW_BLOCK.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get());

        tag(BlockTags.FEATURES_CANNOT_REPLACE)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS);

        tag(BlockTags.FLOWER_POTS)
                .add(BlockRegistry.POTTED_DANGO_BLOSSOM.get())
                .add(BlockRegistry.POTTED_PIRANHA_PLANT.get());

        tag(BlockTags.FLOWERS)
                .add(BlockRegistry.DANGO_BLOSSOM.get());

        tag(BlockTags.GUARDED_BY_PIGLINS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .add(BlockRegistry.COIN.get())
                .add(BlockRegistry.STAR_COIN.get());

        tag(BlockTags.IMPERMEABLE)
                .add(BlockRegistry.CLEAR_WARP_PIPE.get());

        tag(BlockTags.INSIDE_STEP_SOUND_BLOCKS)
                .add(BlockRegistry.QUICKSAND.get())
                .add(BlockRegistry.RED_QUICKSAND.get());

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS);

        tag(BlockTags.SAND)
                .add(BlockRegistry.QUICKSAND.get())
                .add(BlockRegistry.RED_QUICKSAND.get());

        tag(BlockTags.SCULK_REPLACEABLE_WORLD_GEN)
                .add(BlockRegistry.DEEPSLATE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEPSLATE_TILE_PEDESTAL.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_TILES.get());

        tag(BlockTags.SLABS)
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_SLAB.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILE_SLAB.get())
                .add(BlockRegistry.CALCITE_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_SLAB.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_SLAB.get())
                .add(BlockRegistry.SANDSTONE_BRICK_SLAB.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_SLAB.get());

        tag(TagRegistry.SNOWBALL_EXTINGUISHES)
                .addTag(BlockTags.FIRE)
                .addTag(BlockTags.CANDLES)
                .addTag(BlockTags.CAMPFIRES)
                .addTag(BlockTags.CANDLE_CAKES)
                .addOptionalTag(CompatRegistry.BUMBLEZONE_CANDLES)
                .addOptionalTag(CompatRegistry.SUPP_CANDLE_HOLDERS)
                .addOptionalTag(CompatRegistry.SUPP_SCONCES)
                .addOptional(SUPP_FIRE_PIT);

        tag(BlockTags.STAIRS)
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_STAIRS.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILE_STAIRS.get())
                .add(BlockRegistry.CALCITE_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_STAIRS.get())
                .add(BlockRegistry.SANDSTONE_BRICK_STAIRS.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_STAIRS.get());

        tag(BlockTags.STONE_BUTTONS)
                .add(BlockRegistry.AMETHYST_BUTTON.get())
                .add(BlockRegistry.CALCITE_BUTTON.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.FUNGAL_STONE_BUTTON.get());

        tag(BlockTags.STONE_PRESSURE_PLATES)
                .add(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
                .add(BlockRegistry.CALCITE_PRESSURE_PLATE.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE.get())
                .add(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get());

        tag(BlockTags.WALL_POST_OVERRIDE)
                .addTag(TagRegistry.QUESTION_PANEL_BLOCKS)
                .add(BlockRegistry.SPIKE_PANEL.get());

        tag(BlockTags.WALLS)
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.AMETHYST_WALL.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILE_WALL.get())
                .add(BlockRegistry.CALCITE_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_WALL.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_WALL.get())
                .add(BlockRegistry.SANDSTONE_BRICK_WALL.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_WALL.get());

        tag(BlockTags.WOODEN_STAIRS)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS);

        tag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS)
                .add(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.get())
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get())
                .add(BlockRegistry.SPLUNKIN_O_LANTERN.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(BlockRegistry.GLOW_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(TagRegistry.BRICK_PEDESTAL_BLOCKS)
                .addTag(TagRegistry.CALCITE_BLOCKS)
                .addTag(TagRegistry.CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.POLISHED_CALCITE_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_PANEL_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .add(BlockRegistry.AMETHYST_BRICKS.get())
                .add(BlockRegistry.AMETHYST_BRICK_SLAB.get())
                .add(BlockRegistry.AMETHYST_BRICK_STAIRS.get())
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.AMETHYST_BUTTON.get())
                .add(BlockRegistry.AMETHYST_PRESSURE_PLATE.get())
                .add(BlockRegistry.AMETHYST_SLAB.get())
                .add(BlockRegistry.AMETHYST_STAIRS.get())
                .add(BlockRegistry.AMETHYST_WALL.get())
                .add(BlockRegistry.BLUE_DOTTED_LINE_BLOCK.get())
                .add(BlockRegistry.CALCITE_BUTTON.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILES.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILE_SLAB.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILE_STAIRS.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILE_WALL.get())
                .add(BlockRegistry.CALCITE_PRESSURE_PLATE.get())
                .add(BlockRegistry.CALCITE_SLAB.get())
                .add(BlockRegistry.CALCITE_STAIRS.get())
                .add(BlockRegistry.CALCITE_WALL.get())
                .add(BlockRegistry.CHISELED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CHISELED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CHISELED_POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CHISELED_POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CHISELED_RED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.CHISELED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.COIN.get())
                .add(BlockRegistry.CRACKED_AMETHYST_BRICKS.get())
                .add(BlockRegistry.CRACKED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_RED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.CRACKED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.DANGO_BLOSSOM.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_PRESSURE_PLATE.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.FUNGAL_BRICKS.get())
                .add(BlockRegistry.FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get())
                .add(BlockRegistry.FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.IRON_SPIKE.get())
                .add(BlockRegistry.ON_OFF_SWITCH.get())
                .add(BlockRegistry.POLISHED_AMETHYST.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_SLAB.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_WALL.get())
                .add(BlockRegistry.RED_DOTTED_LINE_BLOCK.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICKS.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_SLAB.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_STAIRS.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_WALL.get())
                .add(BlockRegistry.SANDSTONE_BRICKS.get())
                .add(BlockRegistry.SANDSTONE_BRICK_SLAB.get())
                .add(BlockRegistry.SANDSTONE_BRICK_STAIRS.get())
                .add(BlockRegistry.SANDSTONE_BRICK_WALL.get())
                .add(BlockRegistry.SPIKE_PANEL.get())
                .add(BlockRegistry.STAR_COIN.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_SLAB.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_STAIRS.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_WALL.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(BlockRegistry.QUICKSAND.get())
                .add(BlockRegistry.RED_QUICKSAND.get());

        tag(BlockTags.SWORD_EFFICIENT)
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.DANGO_BLOSSOM.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get())
                .add(BlockRegistry.SPLUNKIN_O_LANTERN.get());

        tag(TagRegistry.blockTags("twilightforest", "portal/generated_decoration"))
                .add(BlockRegistry.DANGO_BLOSSOM.get());
    }
}