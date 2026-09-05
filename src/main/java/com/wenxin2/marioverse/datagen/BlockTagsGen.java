package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
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
    private static final ResourceLocation AUTOMOBILITY_SLOPE = ResourceLocation.fromNamespaceAndPath("automobility", "slope");
    private static final ResourceLocation AUTOMOBILITY_STEEP_SLOPE = ResourceLocation.fromNamespaceAndPath("automobility", "steep_slope");
    private static final ResourceLocation AUTOMOBILITY_DASH_PANEL_SLOPE = ResourceLocation.fromNamespaceAndPath("automobility", "slope_with_dash_panel");
    private static final ResourceLocation AUTOMOBILITY_DASH_PANEL_STEEP_SLOPE = ResourceLocation.fromNamespaceAndPath("automobility", "steep_slope_with_dash_panel");
    private static final ResourceLocation BB_BLUE_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "blue_bigshroom_block");
    private static final ResourceLocation BB_GREEN_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "green_bigshroom_block");
    private static final ResourceLocation BB_LIME_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "lime_bigshroom_block");
    private static final ResourceLocation BB_ORANGE_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "orange_bigshroom_block");
    private static final ResourceLocation BB_PURPLE_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "purple_bigshroom_block");
    private static final ResourceLocation BB_RED_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "red_bigshroom_block");
    private static final ResourceLocation BB_YELLOW_BIGSHROOM = ResourceLocation.fromNamespaceAndPath("superbb", "yellow_bigshroom_block");
    private static final ResourceLocation CC_CALCITE_BRICKS = ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "calcite_bricks");
    private static final ResourceLocation CC_POLISHED_CALCITE = ResourceLocation.fromNamespaceAndPath("caverns_and_chasms", "polished_calcite");
    private static final ResourceLocation CHIPPED_BROWN_MUSHROOMS = ResourceLocation.fromNamespaceAndPath("chipped", "brown_mushroom_block");
    private static final ResourceLocation CHIPPED_RED_MUSHROOMS = ResourceLocation.fromNamespaceAndPath("chipped", "red_mushroom_block");
    private static final ResourceLocation CREATE_CALCITE_BRICKS = ResourceLocation.fromNamespaceAndPath("create", "cut_calcite_bricks");
    private static final ResourceLocation CREATE_POLISHED_CALCITE = ResourceLocation.fromNamespaceAndPath("create", "polished_cut_calcite");
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
        BlockRegistry.PIPE_JUNCTION.values().forEach(block -> tag(TagRegistry.PIPE_JUNCTION_BLOCKS).add(block.get()));
        BlockRegistry.POLISHED_CALCITE.values().forEach(block -> tag(TagRegistry.POLISHED_CALCITE_BLOCKS).add(block.get()));
        BlockRegistry.STORAGE_CALCITE_BRICKS.values().forEach(block -> tag(TagRegistry.STORAGE_CALCITE_BRICK_BLOCKS).add(block.get()));
        BlockRegistry.WARP_PIPES.values().forEach(block -> tag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS).add(block.get()));

        for (DyeColor color : DyeColor.values()) {
            tag(TagRegistry.blockTags("c", "dyed/" + color))
                    .add(BlockRegistry.CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.CALCITE_BRICK_PEDESTAL.get(color).get())
                    .add(BlockRegistry.CHECKPOINT_FLAGS.get(color).get())
                    .add(BlockRegistry.CHISELED_CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.CRACKED_CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.GOAL_POLES.get(color).get())
                    .add(BlockRegistry.PIPE_JUNCTION.get(color).get())
                    .add(BlockRegistry.POLISHED_CALCITE.get(color).get())
                    .add(BlockRegistry.STORAGE_CALCITE_BRICKS.get(color).get())
                    .add(BlockRegistry.WARP_PIPES.get(color).get());

            if (color == DyeColor.WHITE)
                tag(TagRegistry.blockTags("c", "dyed/" + color))
                        .add(Blocks.CALCITE)
                        .addOptional(CC_CALCITE_BRICKS)
                        .addOptional(CC_POLISHED_CALCITE)
                        .addOptional(CREATE_CALCITE_BRICKS)
                        .addOptional(CREATE_POLISHED_CALCITE);
            else tag(TagRegistry.blockTags("c", "dyed/" + color))
                    .add(BlockRegistry.CALCITE.get(color).get());
        }

        tag(CompatRegistry.CREATE_BRITTLE)
                .addTag(TagRegistry.CORAL_TOWER_BLOCKS)
                .addTag(TagRegistry.DEAD_CORAL_TOWER_BLOCKS)
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.DANGO_BLOSSOM.get())
                .add(BlockRegistry.POTTED_BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.POTTED_DANGO_BLOSSOM.get())
                .add(BlockRegistry.POTTED_PIRANHA_PLANT.get())
                .add(BlockRegistry.POTTED_RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get());

        tag(CompatRegistry.CREATE_COPYCAT_ALLOW)
                .addTag(TagRegistry.DOTTED_LINE_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get());

        tag(CompatRegistry.CREATE_MOVABLE_EMPTY_COLLIDER)
                .addTag(TagRegistry.BRIDGE_STAIR_BLOCKS)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.CORAL_TOWER_BLOCKS)
                .addTag(TagRegistry.DEAD_CORAL_TOWER_BLOCKS)
                .addTag(TagRegistry.DOTTED_LINE_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get())
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.CLEAR_WARP_PIPE.get())
                .add(BlockRegistry.COIN.get())
                .add(BlockRegistry.DANGO_BLOSSOM.get())
                .add(BlockRegistry.QUICKSAND.get())
                .add(BlockRegistry.RED_QUICKSAND.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.SPIKE_PANEL.get())
                .add(BlockRegistry.STAR_COIN.get())
                .add(BlockRegistry.WATER_SPOUT.get());

        tag(CompatRegistry.CREATE_SAFE_NBT)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get());

        tag(CompatRegistry.CREATE_SIMPLE_MOUNTED_STORAGE)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get());;

        tag(CompatRegistry.CREATE_SINGLE_BLOCK_INVENTORIES); // Causes animated textures to unanimate

        tag(TagRegistry.CANNOT_USE_AS_DISGUISE)
                .add(BlockRegistry.BLOCK_SPAWNER.get())
                .addOptional(AUTOMOBILITY_DASH_PANEL_SLOPE)
                .addOptional(AUTOMOBILITY_DASH_PANEL_STEEP_SLOPE)
                .addOptional(AUTOMOBILITY_SLOPE)
                .addOptional(AUTOMOBILITY_STEEP_SLOPE);

        tag(TagRegistry.BONKABLE_BLOCKS)
                .addTag(TagRegistry.HARD_BLOCKS)
                .addTag(TagRegistry.HARD_SLABS)
                .addTag(TagRegistry.HARD_STAIRS)
                .addTag(TagRegistry.HARD_WALLS)
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

        tag(TagRegistry.ABILITY_BLOCKS)
                .add(BlockRegistry.DAISY_ABILITY_BLOCK.get())
                .add(BlockRegistry.LUIGI_ABILITY_BLOCK.get())
                .add(BlockRegistry.MARIO_ABILITY_BLOCK.get())
                .add(BlockRegistry.PEACH_ABILITY_BLOCK.get())
                .add(BlockRegistry.ROSALINA_ABILITY_BLOCK.get())
                .add(BlockRegistry.STEVE_ABILITY_BLOCK.get())
                .add(BlockRegistry.WALUIGI_ABILITY_BLOCK.get())
                .add(BlockRegistry.WARIO_ABILITY_BLOCK.get());

        tag(TagRegistry.ARROW_SIGNS)
                .addTag(TagRegistry.FLAMMABLE_ARROW_SIGNS)
                .addTag(TagRegistry.WOODEN_ARROW_SIGNS);

        tag(TagRegistry.BOUNCY_BLOCKS)
                .add(Blocks.BROWN_MUSHROOM_BLOCK)
                .add(Blocks.RED_MUSHROOM_BLOCK)
                .addOptionalTag(CHIPPED_BROWN_MUSHROOMS)
                .addOptionalTag(CHIPPED_RED_MUSHROOMS)
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
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS);

        tag(TagRegistry.BRIDGE_STAIR_BLOCKS)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS);

        tag(TagRegistry.CALCITE_BLOCKS)
                .add(Blocks.CALCITE);

        tag(TagRegistry.CALCITE_BRICK_BLOCKS)
                .addOptional(CC_CALCITE_BRICKS)
                .addOptional(CREATE_CALCITE_BRICKS);

        tag(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS);

        tag(TagRegistry.CAVE_PIRANHA_PLANTS_SPAWNABLE_ON)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.GRASSY_STONES)
                .addTag(BlockTags.BASE_STONE_OVERWORLD);

        tag(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS);

        tag(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS);

        tag(TagRegistry.POLISHED_CALCITE_BLOCKS)
                .addOptional(CC_POLISHED_CALCITE)
                .addOptional(CREATE_POLISHED_CALCITE);

        tag(TagRegistry.STORAGE_CALCITE_BRICK_BLOCKS);

        tag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.DYEABLE_CHECKPOINT_FLAG_BLOCKS)
                .add(BlockRegistry.CLASSIC_CHECKPOINT_FLAG.get());

        tag(TagRegistry.CORAL_TOWER_BLOCKS)
                .add(BlockRegistry.BRAIN_CORAL_TOWER.get())
                .add(BlockRegistry.BUBBLE_CORAL_TOWER.get())
                .add(BlockRegistry.FIRE_CORAL_TOWER.get())
                .add(BlockRegistry.HORN_CORAL_TOWER.get())
                .add(BlockRegistry.TUBE_CORAL_TOWER.get());

        tag(TagRegistry.DEAD_CORAL_TOWER_BLOCKS)
                .add(BlockRegistry.DEAD_BRAIN_CORAL_TOWER.get())
                .add(BlockRegistry.DEAD_BUBBLE_CORAL_TOWER.get())
                .add(BlockRegistry.DEAD_FIRE_CORAL_TOWER.get())
                .add(BlockRegistry.DEAD_HORN_CORAL_TOWER.get())
                .add(BlockRegistry.DEAD_TUBE_CORAL_TOWER.get());

        tag(TagRegistry.DEATH_BLOCKS)
                .add(BlockRegistry.DEATH_BLOCK.get())
                .add(BlockRegistry.MONSTER_DEATH_BLOCK.get())
                .add(BlockRegistry.PASSIVE_DEATH_BLOCK.get())
                .add(BlockRegistry.PLAYER_DEATH_BLOCK.get());

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

        tag(TagRegistry.DYED_PICKET_FENCES)
                .add(BlockRegistry.RED_PICKET_FENCE.get())
                .add(BlockRegistry.WHITE_PICKET_FENCE.get());

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

        tag(TagRegistry.FLAMMABLE_ARROW_SIGNS)
                .add(BlockRegistry.ACACIA_ARROW_SIGN.get())
                .add(BlockRegistry.ACACIA_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.ACACIA_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.BAMBOO_ARROW_SIGN.get())
                .add(BlockRegistry.BAMBOO_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.BAMBOO_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.BIRCH_ARROW_SIGN.get())
                .add(BlockRegistry.BIRCH_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.BIRCH_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.CHERRY_ARROW_SIGN.get())
                .add(BlockRegistry.CHERRY_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.CHERRY_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.DARK_OAK_ARROW_SIGN.get())
                .add(BlockRegistry.DARK_OAK_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.DARK_OAK_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.JUNGLE_ARROW_SIGN.get())
                .add(BlockRegistry.JUNGLE_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.JUNGLE_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.MANGROVE_ARROW_SIGN.get())
                .add(BlockRegistry.MANGROVE_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.MANGROVE_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.MUSHROOT_ARROW_SIGN.get())
                .add(BlockRegistry.MUSHROOT_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.MUSHROOT_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.OAK_ARROW_SIGN.get())
                .add(BlockRegistry.OAK_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.OAK_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.SPRUCE_ARROW_SIGN.get())
                .add(BlockRegistry.SPRUCE_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.SPRUCE_WALL_ARROW_SIGN.get());

        tag(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS)
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

        tag(TagRegistry.FLAMMABLE_BRIDGE_STAIR_BLOCKS)
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

        tag(TagRegistry.FLAMMABLE_HARD_BLOCKS)
                .add(BlockRegistry.HARD_ACACIA_BLOCK.get())
                .add(BlockRegistry.HARD_BAMBOO_BLOCK.get())
                .add(BlockRegistry.HARD_BIRCH_BLOCK.get())
                .add(BlockRegistry.HARD_CHERRY_BLOCK.get())
                .add(BlockRegistry.HARD_DARK_OAK_BLOCK.get())
                .add(BlockRegistry.HARD_JUNGLE_BLOCK.get())
                .add(BlockRegistry.HARD_MANGROVE_BLOCK.get())
                .add(BlockRegistry.HARD_MUSHROOT_BLOCK.get())
                .add(BlockRegistry.HARD_OAK_BLOCK.get())
                .add(BlockRegistry.HARD_SPRUCE_BLOCK.get());

        tag(TagRegistry.FLAMMABLE_HARD_SLABS)
                .add(BlockRegistry.HARD_ACACIA_SLAB.get())
                .add(BlockRegistry.HARD_BAMBOO_SLAB.get())
                .add(BlockRegistry.HARD_BIRCH_SLAB.get())
                .add(BlockRegistry.HARD_CHERRY_SLAB.get())
                .add(BlockRegistry.HARD_DARK_OAK_SLAB.get())
                .add(BlockRegistry.HARD_JUNGLE_SLAB.get())
                .add(BlockRegistry.HARD_MANGROVE_SLAB.get())
                .add(BlockRegistry.HARD_MUSHROOT_SLAB.get())
                .add(BlockRegistry.HARD_OAK_SLAB.get())
                .add(BlockRegistry.HARD_SPRUCE_SLAB.get());

        tag(TagRegistry.FLAMMABLE_HARD_STAIRS)
                .add(BlockRegistry.HARD_ACACIA_STAIRS.get())
                .add(BlockRegistry.HARD_BAMBOO_STAIRS.get())
                .add(BlockRegistry.HARD_BIRCH_STAIRS.get())
                .add(BlockRegistry.HARD_CHERRY_STAIRS.get())
                .add(BlockRegistry.HARD_DARK_OAK_STAIRS.get())
                .add(BlockRegistry.HARD_JUNGLE_STAIRS.get())
                .add(BlockRegistry.HARD_MANGROVE_STAIRS.get())
                .add(BlockRegistry.HARD_MUSHROOT_STAIRS.get())
                .add(BlockRegistry.HARD_OAK_STAIRS.get())
                .add(BlockRegistry.HARD_SPRUCE_STAIRS.get());

        tag(TagRegistry.FLAMMABLE_HARD_WALLS)
                .add(BlockRegistry.HARD_ACACIA_WALL.get())
                .add(BlockRegistry.HARD_BAMBOO_WALL.get())
                .add(BlockRegistry.HARD_BIRCH_WALL.get())
                .add(BlockRegistry.HARD_CHERRY_WALL.get())
                .add(BlockRegistry.HARD_DARK_OAK_WALL.get())
                .add(BlockRegistry.HARD_JUNGLE_WALL.get())
                .add(BlockRegistry.HARD_MANGROVE_WALL.get())
                .add(BlockRegistry.HARD_MUSHROOT_WALL.get())
                .add(BlockRegistry.HARD_OAK_WALL.get())
                .add(BlockRegistry.HARD_SPRUCE_WALL.get());

        tag(TagRegistry.FLAMMABLE_LARGE_ARROW_SIGNS)
                .add(BlockRegistry.LARGE_ACACIA_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_ACACIA_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_BAMBOO_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_BAMBOO_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_BIRCH_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_BIRCH_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_CHERRY_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_CHERRY_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_DARK_OAK_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_DARK_OAK_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_JUNGLE_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_JUNGLE_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_MANGROVE_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_MANGROVE_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_MUSHROOT_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_MUSHROOT_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_OAK_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_OAK_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_SPRUCE_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_SPRUCE_WALL_ARROW_SIGN.get());

        tag(TagRegistry.FLAMMABLE_PICKET_FENCES)
                .addTag(TagRegistry.DYED_PICKET_FENCES)
                .add(BlockRegistry.ACACIA_PICKET_FENCE.get())
                .add(BlockRegistry.BAMBOO_PICKET_FENCE.get())
                .add(BlockRegistry.BIRCH_PICKET_FENCE.get())
                .add(BlockRegistry.CHERRY_PICKET_FENCE.get())
                .add(BlockRegistry.DARK_OAK_PICKET_FENCE.get())
                .add(BlockRegistry.JUNGLE_PICKET_FENCE.get())
                .add(BlockRegistry.MANGROVE_PICKET_FENCE.get())
                .add(BlockRegistry.MUSHROOT_PICKET_FENCE.get())
                .add(BlockRegistry.OAK_PICKET_FENCE.get())
                .add(BlockRegistry.SPRUCE_PICKET_FENCE.get());

        tag(TagRegistry.FLAMMABLE_PLATFORMS)
                .add(BlockRegistry.MUSHROOT_LOG_PLATFORM.get())
                .add(BlockRegistry.STRIPPED_MUSHROOT_LOG_PLATFORM.get());

        tag(TagRegistry.FLAMMABLE_WALLS)
                .add(BlockRegistry.HARD_ACACIA_WALL.get())
                .add(BlockRegistry.HARD_BAMBOO_WALL.get())
                .add(BlockRegistry.HARD_BIRCH_WALL.get())
                .add(BlockRegistry.HARD_CHERRY_WALL.get())
                .add(BlockRegistry.HARD_DARK_OAK_WALL.get())
                .add(BlockRegistry.HARD_JUNGLE_WALL.get())
                .add(BlockRegistry.HARD_MANGROVE_WALL.get())
                .add(BlockRegistry.HARD_MUSHROOT_WALL.get())
                .add(BlockRegistry.HARD_OAK_WALL.get())
                .add(BlockRegistry.HARD_SPRUCE_WALL.get())
                .add(BlockRegistry.MUSHROOT_BOARD_WALL.get())
                .add(BlockRegistry.MUSHROOT_PANEL_WALL.get());

        tag(TagRegistry.FLAMMABLE_WINDOWS)
                .add(BlockRegistry.MUSHROOT_FRAMED_WINDOW.get());

        tag(TagRegistry.FLAMMABLE_WINDOW_PANES)
                .add(BlockRegistry.MUSHROOT_FRAMED_WINDOW_PANE.get());

        tag(TagRegistry.FLOATY)
                .addTag(TagRegistry.DOTTED_LINE_BLOCKS)
                .add(BlockRegistry.COIN.get())
                .add(BlockRegistry.ON_OFF_SWITCH.get())
                .add(BlockRegistry.STAR_COIN.get());

        tag(TagRegistry.FREEZES_INTO_PACKED_ICE)
                .add(Blocks.ICE);

        tag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.DYEABLE_GOAL_POLE_BLOCKS)
                .add(BlockRegistry.CLASSIC_GOAL_POLE.get());

        tag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.GRASSY_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.GRASSY_FUNGAL_STONE.get());

        tag(TagRegistry.HARD_BLOCKS)
                .addTag(TagRegistry.FLAMMABLE_HARD_BLOCKS)
                .addTag(TagRegistry.STONE_HARD_BLOCKS)
                .addTag(TagRegistry.WOODEN_HARD_BLOCKS);

        tag(TagRegistry.HARD_SLABS)
                .addTag(TagRegistry.FLAMMABLE_HARD_SLABS)
                .addTag(TagRegistry.STONE_HARD_SLABS)
                .addTag(TagRegistry.WOODEN_HARD_SLABS);

        tag(TagRegistry.HARD_STAIRS)
                .addTag(TagRegistry.FLAMMABLE_HARD_STAIRS)
                .addTag(TagRegistry.STONE_HARD_STAIRS)
                .addTag(TagRegistry.WOODEN_HARD_STAIRS);

        tag(TagRegistry.HARD_WALLS)
                .addTag(TagRegistry.FLAMMABLE_HARD_WALLS)
                .addTag(TagRegistry.STONE_HARD_WALLS)
                .addTag(TagRegistry.WOODEN_HARD_WALLS);

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

        tag(TagRegistry.LARGE_ARROW_SIGNS)
                .addTag(TagRegistry.FLAMMABLE_LARGE_ARROW_SIGNS)
                .addTag(TagRegistry.WOODEN_LARGE_ARROW_SIGNS);

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

        tag(TagRegistry.MUSHROOT_LOGS)
                .add(BlockRegistry.MUSHROOT_LOG.get())
                .add(BlockRegistry.MUSHROOT_WOOD.get())
                .add(BlockRegistry.STRIPPED_MUSHROOT_LOG.get())
                .add(BlockRegistry.STRIPPED_MUSHROOT_WOOD.get());

        tag(TagRegistry.MUSHROOT_PLANKS)
                .add(BlockRegistry.MUSHROOT_BOARDS.get())
                .add(BlockRegistry.MUSHROOT_PANELS.get())
                .add(BlockRegistry.MUSHROOT_PLANKS.get());

        tag(TagRegistry.PICKET_FENCES)
                .addTag(TagRegistry.WOODEN_PICKET_FENCES);

        tag(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS)
                .add(Blocks.DECORATED_POT);

        tag(TagRegistry.PIRANHA_PLANTS_CANNOT_ATTACH)
                .addTag(BlockTags.ICE);

        tag(TagRegistry.PIRANHA_PLANTS_SPAWNABLE_ON)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)
                .addTag(BlockTags.DIRT);

        tag(TagRegistry.PLATFORMS)
                .addTag(TagRegistry.FLAMMABLE_PLATFORMS)
                .addTag(TagRegistry.WOODEN_PLATFORMS);

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

        tag(TagRegistry.QUESTION_BLOCKS_CAN_PLACE)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .add(Blocks.CAKE);

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

        tag(TagRegistry.STONE_HARD_BLOCKS)
                .add(BlockRegistry.HARD_DEEP_FUNGAL_BLOCK.get())
                .add(BlockRegistry.HARD_FUNGAL_BLOCK.get());

        tag(TagRegistry.STONE_HARD_SLABS)
                .add(BlockRegistry.HARD_DEEP_FUNGAL_SLAB.get())
                .add(BlockRegistry.HARD_FUNGAL_SLAB.get());

        tag(TagRegistry.STONE_HARD_STAIRS)
                .add(BlockRegistry.HARD_DEEP_FUNGAL_STAIRS.get())
                .add(BlockRegistry.HARD_FUNGAL_STAIRS.get());

        tag(TagRegistry.STONE_HARD_WALLS)
                .add(BlockRegistry.HARD_DEEP_FUNGAL_WALL.get())
                .add(BlockRegistry.HARD_FUNGAL_WALL.get());

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

        tag(TagRegistry.WINDOWS)
                .addTag(TagRegistry.FLAMMABLE_WINDOWS)
                .addTag(TagRegistry.WOODEN_WINDOWS);

        tag(TagRegistry.WINDOW_PANES)
                .addTag(TagRegistry.FLAMMABLE_WINDOW_PANES)
                .addTag(TagRegistry.WOODEN_WINDOW_PANES);

        tag(TagRegistry.WOODEN_ARROW_SIGNS)
                .addTag(TagRegistry.FLAMMABLE_ARROW_SIGNS)
                .add(BlockRegistry.CRIMSON_ARROW_SIGN.get())
                .add(BlockRegistry.CRIMSON_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.CRIMSON_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.WARPED_ARROW_SIGN.get())
                .add(BlockRegistry.WARPED_HANGING_ARROW_SIGN.get())
                .add(BlockRegistry.WARPED_WALL_ARROW_SIGN.get());

        tag(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_BLOCKS)
                .add(BlockRegistry.CRIMSON_STEM_BRIDGE.get())
                .add(BlockRegistry.WARPED_STEM_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE.get())
                .add(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE.get());

        tag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS)
                .addTag(TagRegistry.FLAMMABLE_BRIDGE_STAIR_BLOCKS)
                .add(BlockRegistry.CRIMSON_STEM_BRIDGE_STAIRS.get())
                .add(BlockRegistry.WARPED_STEM_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_CRIMSON_STEM_BRIDGE_STAIRS.get())
                .add(BlockRegistry.STRIPPED_WARPED_STEM_BRIDGE_STAIRS.get());

        tag(TagRegistry.WOODEN_HARD_BLOCKS)
                .addTag(TagRegistry.FLAMMABLE_HARD_BLOCKS)
                .add(BlockRegistry.HARD_CRIMSON_BLOCK.get())
                .add(BlockRegistry.HARD_WARPED_BLOCK.get());

        tag(TagRegistry.WOODEN_HARD_SLABS)
                .addTag(TagRegistry.FLAMMABLE_HARD_SLABS)
                .add(BlockRegistry.HARD_CRIMSON_SLAB.get())
                .add(BlockRegistry.HARD_WARPED_SLAB.get());

        tag(TagRegistry.WOODEN_HARD_STAIRS)
                .addTag(TagRegistry.FLAMMABLE_HARD_STAIRS)
                .add(BlockRegistry.HARD_CRIMSON_STAIRS.get())
                .add(BlockRegistry.HARD_WARPED_STAIRS.get());

        tag(TagRegistry.WOODEN_HARD_WALLS)
                .addTag(TagRegistry.FLAMMABLE_HARD_WALLS)
                .add(BlockRegistry.HARD_CRIMSON_WALL.get())
                .add(BlockRegistry.HARD_WARPED_WALL.get());

        tag(TagRegistry.WOODEN_LARGE_ARROW_SIGNS)
                .addTag(TagRegistry.FLAMMABLE_LARGE_ARROW_SIGNS)
                .add(BlockRegistry.LARGE_CRIMSON_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_CRIMSON_WALL_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_WARPED_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_WARPED_WALL_ARROW_SIGN.get());

        tag(TagRegistry.WOODEN_PICKET_FENCES)
                .addTag(TagRegistry.FLAMMABLE_PICKET_FENCES)
                .add(BlockRegistry.CRIMSON_PICKET_FENCE.get())
                .add(BlockRegistry.WARPED_PICKET_FENCE.get());

        tag(TagRegistry.WOODEN_PLATFORMS)
                .add(BlockRegistry.MUSHROOT_LOG_PLATFORM.get())
                .add(BlockRegistry.STRIPPED_MUSHROOT_LOG_PLATFORM.get());

        tag(TagRegistry.WOODEN_WALLS)
                .addTag(TagRegistry.FLAMMABLE_WALLS)
                .add(BlockRegistry.HARD_CRIMSON_WALL.get())
                .add(BlockRegistry.HARD_WARPED_WALL.get());

        tag(TagRegistry.WOODEN_WINDOWS)
                .add(BlockRegistry.MUSHROOT_FRAMED_WINDOW.get());

        tag(TagRegistry.WOODEN_WINDOW_PANES)
                .add(BlockRegistry.MUSHROOT_FRAMED_WINDOW_PANE.get());

        tag(TagRegistry.WRENCH_EFFICIENT)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS);


        tag(Tags.Blocks.COBBLESTONES)
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE.get())
                .add(BlockRegistry.FUNGAL_COBBLESTONE.get());

        tag(Tags.Blocks.FENCES_WOODEN)
                .add(BlockRegistry.MUSHROOT_FENCE.get());

        tag(Tags.Blocks.FENCE_GATES_WOODEN)
                .add(BlockRegistry.MUSHROOT_FENCE_GATE.get());

        tag(Tags.Blocks.GLASS_BLOCKS_COLORLESS)
                .addTag(TagRegistry.WOODEN_WINDOWS);

        tag(Tags.Blocks.GLASS_PANES_COLORLESS)
                .addTag(TagRegistry.WOODEN_WINDOW_PANES);

        tag(Tags.Blocks.PUMPKINS_CARVED)
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get());

        tag(Tags.Blocks.PUMPKINS_JACK_O_LANTERNS)
                .add(BlockRegistry.SPLUNKIN_O_LANTERN.get());

        tag(Tags.Blocks.SANDS_COLORLESS)
                .add(BlockRegistry.QUICKSAND.get());

        tag(Tags.Blocks.SANDS_RED)
                .add(BlockRegistry.RED_QUICKSAND.get());

        tag(Tags.Blocks.STONES)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE.get());

        tag(BlockTags.ANCIENT_CITY_REPLACEABLE)
                .add(BlockRegistry.DEEPSLATE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.DEEPSLATE_TILE_PEDESTAL.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_DEEPSLATE_TILES.get());

        tag(BlockTags.ANIMALS_SPAWNABLE_ON)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get());

        tag(BlockTags.BASE_STONE_OVERWORLD)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE.get());

        tag(BlockTags.BIG_DRIPLEAF_PLACEABLE)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get())
                .add(BlockRegistry.SHROOMSOIL.get());

        tag(BlockTags.CEILING_HANGING_SIGNS)
                .add(BlockRegistry.MUSHROOT_HANGING_SIGN.get());

        tag(BlockTags.CONVERTABLE_TO_MUD)
                .add(BlockRegistry.SHROOMSOIL.get());

        tag(BlockTags.CORALS);

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

        tag(BlockTags.DIRT)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get())
                .add(BlockRegistry.SHROOMSOIL.get());

        tag(BlockTags.DRAGON_IMMUNE)
                .addTag(TagRegistry.DEATH_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get());

        tag(BlockTags.ENDERMAN_HOLDABLE)
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.GLOW_BLOCK.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get());

        tag(BlockTags.FEATURES_CANNOT_REPLACE)
                .addTag(TagRegistry.DEATH_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get());

        tag(BlockTags.FENCE_GATES)
                .add(BlockRegistry.MUSHROOT_FENCE_GATE.get());

        tag(BlockTags.FLOWER_POTS)
                .add(BlockRegistry.POTTED_BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.POTTED_DANGO_BLOSSOM.get())
                .add(BlockRegistry.POTTED_MUSHROOT_SAPLING.get())
                .add(BlockRegistry.POTTED_PIRANHA_PLANT.get())
                .add(BlockRegistry.POTTED_RED_TRAMPOLINE_CAP.get());

        tag(BlockTags.FLOWERS)
                .add(BlockRegistry.DANGO_BLOSSOM.get());

        tag(BlockTags.FOXES_SPAWNABLE_ON)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get());

        tag(BlockTags.FROGS_SPAWNABLE_ON)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get());

        tag(BlockTags.GEODE_INVALID_BLOCKS)
                .addTag(TagRegistry.DEATH_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get());

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

        tag(BlockTags.LEAVES)
                .add(BlockRegistry.MUSHROOT_LEAVES.get());

        tag(BlockTags.LOGS);

        tag(BlockTags.LOGS_THAT_BURN)
                .addTag(TagRegistry.MUSHROOT_LOGS);

        tag(BlockTags.NEEDS_IRON_TOOL)
                .addTag(TagRegistry.ABILITY_BLOCKS);

        tag(BlockTags.NEEDS_STONE_TOOL)
                .addTag(TagRegistry.DYEABLE_WARP_PIPE_BLOCKS)
                .addTag(TagRegistry.HARD_BLOCKS)
                .addTag(TagRegistry.PIPE_JUNCTION_BLOCKS);

        tag(BlockTags.PARROTS_SPAWNABLE_ON)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get());

        tag(BlockTags.PLANKS)
                .add(BlockRegistry.MUSHROOT_BOARDS.get())
                .add(BlockRegistry.MUSHROOT_PANELS.get())
                .add(BlockRegistry.MUSHROOT_PLANKS.get());

        tag(BlockTags.RABBITS_SPAWNABLE_ON)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get());

        tag(BlockTags.REPLACEABLE)
                .add(BlockRegistry.SHORT_SHROOMGRASS.get())
                .add(BlockRegistry.SHROOMGRASS.get())
                .add(BlockRegistry.TALL_SHROOMGRASS.get())
                .add(BlockRegistry.SHRUBROOM.get());

        tag(BlockTags.REPLACEABLE_BY_TREES)
                .add(BlockRegistry.SHORT_SHROOMGRASS.get())
                .add(BlockRegistry.SHROOMGRASS.get())
                .add(BlockRegistry.TALL_SHROOMGRASS.get())
                .add(BlockRegistry.SHRUBROOM.get());

        tag(BlockTags.SAND)
                .add(BlockRegistry.QUICKSAND.get())
                .add(BlockRegistry.RED_QUICKSAND.get());

        tag(BlockTags.SAPLINGS)
                .add(BlockRegistry.MUSHROOT_SAPLING.get());

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
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.FUNGAL_COBBLESTONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_SLAB.get())
                .add(BlockRegistry.HARD_FUNGAL_SLAB.get())
                .add(BlockRegistry.POLISHED_AMETHYST_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_SLAB.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_SLAB.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_SLAB.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.SANDSTONE_BRICK_SLAB.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_SLAB.get());

        tag(BlockTags.SNIFFER_DIGGABLE_BLOCK)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get())
                .add(BlockRegistry.SHROOMSOIL.get());

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
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.FUNGAL_COBBLESTONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_STAIRS.get())
                .add(BlockRegistry.HARD_FUNGAL_STAIRS.get())
                .add(BlockRegistry.POLISHED_AMETHYST_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_STAIRS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_STAIRS.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_STAIRS.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.SANDSTONE_BRICK_STAIRS.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_STAIRS.get());

        tag(BlockTags.STANDING_SIGNS)
                .add(BlockRegistry.MUSHROOT_SIGN.get());

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

        tag(BlockTags.VALID_SPAWN)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get());

        tag(BlockTags.WALL_POST_OVERRIDE)
                .addTag(TagRegistry.QUESTION_PANEL_BLOCKS)
                .add(BlockRegistry.ACACIA_ARROW_SIGN.get())
                .add(BlockRegistry.BAMBOO_ARROW_SIGN.get())
                .add(BlockRegistry.BIRCH_ARROW_SIGN.get())
                .add(BlockRegistry.CHERRY_ARROW_SIGN.get())
                .add(BlockRegistry.CRIMSON_ARROW_SIGN.get())
                .add(BlockRegistry.DARK_OAK_ARROW_SIGN.get())
                .add(BlockRegistry.JUNGLE_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_ACACIA_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_BAMBOO_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_BIRCH_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_CHERRY_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_CRIMSON_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_DARK_OAK_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_JUNGLE_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_MANGROVE_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_MUSHROOT_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_OAK_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_SPRUCE_ARROW_SIGN.get())
                .add(BlockRegistry.LARGE_WARPED_ARROW_SIGN.get())
                .add(BlockRegistry.MANGROVE_ARROW_SIGN.get())
                .add(BlockRegistry.MUSHROOT_ARROW_SIGN.get())
                .add(BlockRegistry.OAK_ARROW_SIGN.get())
                .add(BlockRegistry.SPRUCE_ARROW_SIGN.get())
                .add(BlockRegistry.WARPED_ARROW_SIGN.get())
                .add(BlockRegistry.SPIKE_PANEL.get());

        tag(BlockTags.WALL_HANGING_SIGNS)
                .add(BlockRegistry.MUSHROOT_WALL_HANGING_SIGN.get());

        tag(BlockTags.WALL_SIGNS)
                .add(BlockRegistry.MUSHROOT_WALL_SIGN.get());

        tag(BlockTags.WALLS)
                .addTag(TagRegistry.HARD_WALLS)
                .add(BlockRegistry.AMETHYST_BRICK_WALL.get())
                .add(BlockRegistry.AMETHYST_WALL.get())
                .add(BlockRegistry.CALCITE_CHECKERED_TILE_WALL.get())
                .add(BlockRegistry.CALCITE_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_WALL.get())
                .add(BlockRegistry.DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.FUNGAL_COBBLESTONE_WALL.get())
                .add(BlockRegistry.FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_WALL.get())
                .add(BlockRegistry.HARD_FUNGAL_WALL.get())
                .add(BlockRegistry.MUSHROOT_BOARD_WALL.get())
                .add(BlockRegistry.MUSHROOT_PANEL_WALL.get())
                .add(BlockRegistry.POLISHED_AMETHYST_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICK_WALL.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.POLISHED_WHITE_CALCITE_WALL.get())
                .add(BlockRegistry.RED_SANDSTONE_BRICK_WALL.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.SANDSTONE_BRICK_WALL.get())
                .add(BlockRegistry.WHITE_CALCITE_BRICK_WALL.get());

        tag(BlockTags.WOLVES_SPAWNABLE_ON)
                .addTag(TagRegistry.GRASSY_STONES)
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get());

        tag(BlockTags.WOODEN_FENCES)
                .add(BlockRegistry.MUSHROOT_FENCE.get());

        tag(BlockTags.WOODEN_SLABS)
                .addTag(TagRegistry.WOODEN_HARD_SLABS)
                .add(BlockRegistry.MUSHROOT_BOARD_SLAB.get())
                .add(BlockRegistry.MUSHROOT_PANEL_SLAB.get())
                .add(BlockRegistry.MUSHROOT_SLAB.get());

        tag(BlockTags.WOODEN_STAIRS)
                .addTag(TagRegistry.WOODEN_HARD_STAIRS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS)
                .add(BlockRegistry.MUSHROOT_BOARD_STAIRS.get())
                .add(BlockRegistry.MUSHROOT_PANEL_STAIRS.get())
                .add(BlockRegistry.MUSHROOT_STAIRS.get());

        tag(BlockTags.MINEABLE_WITH_AXE)
                .addTag(TagRegistry.WOODEN_ARROW_SIGNS)
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS)
                .addTag(TagRegistry.WOODEN_HARD_BLOCKS)
                .addTag(TagRegistry.WOODEN_HARD_WALLS)
                .addTag(TagRegistry.WOODEN_LARGE_ARROW_SIGNS)
                .addTag(TagRegistry.WOODEN_PICKET_FENCES)
                .addTag(TagRegistry.WOODEN_PLATFORMS)
                .addTag(TagRegistry.WOODEN_WALLS)
                .addTag(TagRegistry.WOODEN_WINDOWS)
                .addTag(TagRegistry.WOODEN_WINDOW_PANES)
                .add(BlockRegistry.BLUE_MUSHROOM_TRAMPOLINE.get())
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.RED_MUSHROOM_TRAMPOLINE.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get())
                .add(BlockRegistry.SPLUNKIN_O_LANTERN.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(BlockRegistry.GLOW_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .addTag(TagRegistry.ABILITY_BLOCKS)
                .addTag(TagRegistry.BRICK_PEDESTAL_BLOCKS)
                .addTag(TagRegistry.CALCITE_BLOCKS)
                .addTag(TagRegistry.CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.CALCITE_BRICK_PEDESTAL_BLOCKS)
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS)
                .addTag(TagRegistry.CHISELED_CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.CORAL_TOWER_BLOCKS)
                .addTag(TagRegistry.CRACKED_CALCITE_BRICK_BLOCKS)
                .addTag(TagRegistry.DEAD_CORAL_TOWER_BLOCKS)
                .addTag(TagRegistry.GOAL_POLE_BLOCKS)
                .addTag(TagRegistry.GRASSY_STONES)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.PIPE_JUNCTION_BLOCKS)
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
                .add(BlockRegistry.BLOCK_SPAWNER.get())
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
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE.get())
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_SLAB.get())
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_STAIRS.get())
                .add(BlockRegistry.DEEP_FUNGAL_COBBLESTONE_WALL.get())
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
                .add(BlockRegistry.FUNGAL_COBBLESTONE.get())
                .add(BlockRegistry.FUNGAL_COBBLESTONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_COBBLESTONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_COBBLESTONE_WALL.get())
                .add(BlockRegistry.FUNGAL_STONE.get())
                .add(BlockRegistry.FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.FUNGAL_STONE_BUTTON.get())
                .add(BlockRegistry.FUNGAL_STONE_PRESSURE_PLATE.get())
                .add(BlockRegistry.FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_BLOCK.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_SLAB.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_STAIRS.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_WALL.get())
                .add(BlockRegistry.HARD_FUNGAL_BLOCK.get())
                .add(BlockRegistry.HARD_FUNGAL_SLAB.get())
                .add(BlockRegistry.HARD_FUNGAL_STAIRS.get())
                .add(BlockRegistry.HARD_FUNGAL_WALL.get())
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
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.ROCKY_DEEP_FUNGAL_STONE_WALL.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE_SLAB.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE_STAIRS.get())
                .add(BlockRegistry.ROCKY_FUNGAL_STONE_WALL.get())
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
                .add(BlockRegistry.RED_QUICKSAND.get())
                .add(BlockRegistry.SHROOMGRASS_BLOCK.get())
                .add(BlockRegistry.SHROOMSOIL.get());

        tag(BlockTags.SWORD_EFFICIENT)
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.DANGO_BLOSSOM.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get())
                .add(BlockRegistry.SPLUNKIN_O_LANTERN.get());

        tag(BlockTags.UNDERWATER_BONEMEALS)
                .addTag(TagRegistry.CORAL_TOWER_BLOCKS);

        tag(BlockTags.WITHER_IMMUNE)
                .addTag(TagRegistry.DEATH_BLOCKS)
                .add(BlockRegistry.BLOCK_SPAWNER.get());

        tag(Tags.Blocks.STRIPPED_LOGS)
                .add(BlockRegistry.STRIPPED_MUSHROOT_LOG.get());

        tag(Tags.Blocks.STRIPPED_WOODS)
                .add(BlockRegistry.STRIPPED_MUSHROOT_WOOD.get());

        tag(TagRegistry.blockTags("c", "glass_blocks/wooden"))
                .addTag(TagRegistry.WOODEN_WINDOWS);

        tag(TagRegistry.blockTags("c", "glass_panes/wooden"))
                .addTag(TagRegistry.WOODEN_WINDOW_PANES);

        tag(TagRegistry.blockTags("create", "corals"))
                .addTag(TagRegistry.CORAL_TOWER_BLOCKS)
                .addTag(TagRegistry.DEAD_CORAL_TOWER_BLOCKS);

        tag(TagRegistry.blockTags("framedblocks", "blockentity_whitelisted"))
                .addTag(TagRegistry.QUESTION_BLOCKS)
                .addTag(TagRegistry.INVISIBLE_QUESTION_BLOCKS)
                .addTag(TagRegistry.STORAGE_BRICK_BLOCKS)
                .addTag(TagRegistry.WARP_PIPE_BLOCKS);

        tag(TagRegistry.blockTags("sable", "bouncy"))
                .addTag(TagRegistry.BOUNCY_BLOCKS);

        tag(TagRegistry.blockTags("sable", "end_stones"))
                .add(BlockRegistry.END_STONE_BRICK_PEDESTAL.get())
                .add(BlockRegistry.END_STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.INVISIBLE_END_STONE_QUESTION_BRICKS.get())
                .add(BlockRegistry.SMASHABLE_END_STONE_BRICKS.get())
                .add(BlockRegistry.STORAGE_END_STONE_BRICKS.get());

        tag(TagRegistry.blockTags("sable", "frictive"))
                .add(BlockRegistry.IRON_SPIKE.get())
                .add(BlockRegistry.SPIKE_PANEL.get());

        tag(TagRegistry.blockTags("sable", "half_volume"))
                .remove(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .remove(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS);

        tag(TagRegistry.blockTags("sable", "heavy"))
                .add(BlockRegistry.CHISELED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CHISELED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.CRACKED_POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.HARD_DEEP_FUNGAL_BLOCK.get())
                .add(BlockRegistry.HARD_FUNGAL_BLOCK.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_BRICKS.get())
                .add(BlockRegistry.POLISHED_DEEP_FUNGAL_STONE.get())
                .add(BlockRegistry.POLISHED_FUNGAL_BRICKS.get())
                .add(BlockRegistry.POLISHED_FUNGAL_STONE.get());

        tag(TagRegistry.blockTags("sable", "light"))
                .addTag(TagRegistry.CHECKPOINT_FLAG_BLOCKS);

        tag(TagRegistry.blockTags("sable", "quarter_volume"))
                .addTag(TagRegistry.WOODEN_BRIDGE_BLOCKS)
                .addTag(TagRegistry.WOODEN_BRIDGE_STAIR_BLOCKS);

        tag(TagRegistry.blockTags("sable", "super_light"));

        tag(TagRegistry.blockTags("twilightforest", "portal/decoration"))
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get());

        tag(TagRegistry.blockTags("twilightforest", "portal/generated_decoration"))
                .add(BlockRegistry.BLUE_TRAMPOLINE_CAP.get())
                .add(BlockRegistry.DANGO_BLOSSOM.get())
                .add(BlockRegistry.RED_TRAMPOLINE_CAP.get());
    }
}