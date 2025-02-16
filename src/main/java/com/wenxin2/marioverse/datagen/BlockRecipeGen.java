package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.data.RecipeUtils;
import com.wenxin2.marioverse.init.BlockFamiliesRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockRecipeGen extends RecipeUtils {
    public BlockRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    protected void generateForEnabledBlockFamilies(RecipeOutput output, FeatureFlagSet set) {
        BlockFamiliesRegistry.getAllExtendedFamilies().filter(BlockFamilyExtended::shouldGenerateRecipe).forEach(recipes -> generateRecipes(output, recipes, set));
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        generateForEnabledBlockFamilies(output, FeatureFlagSet.of(FeatureFlags.VANILLA));

        chestplateRecipe(1, "fire_costume", ItemRegistry.FIRE_SHIRT, Blocks.WHITE_WOOL, output);
        classicGoalPoleRecipe(4, BlockRegistry.CLASSIC_GOAL_POLE, Tags.Items.INGOTS_GOLD, Tags.Items.DYES_LIME, Items.WHITE_WOOL, Items.BAMBOO, output);
        coinRecipe(4, BlockRegistry.COIN, Tags.Items.INGOTS_GOLD, Tags.Items.NUGGETS_GOLD, output);
        fireShoesRecipe(1, ItemRegistry.FIRE_SHOES, Blocks.BROWN_WOOL, Tags.Items.LEATHERS, output);
        helmetRecipe(1, "fire_costume", ItemRegistry.FIRE_HAT, Blocks.WHITE_WOOL, output);
        leggingsRecipe(1, "fire_costume", ItemRegistry.FIRE_OVERALLS, Blocks.RED_WOOL, output);
        plusRecipe(1, "brick_pedestals", BlockRegistry.RED_NETHER_BRICK_PEDESTAL, Items.NETHER_WART, BlockRegistry.NETHER_BRICK_PEDESTAL, output);
        plusRecipe(1, "invisible_question_blocks", BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS, Items.NETHER_WART, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS, output);
        plusRecipe(1, "question_blocks", BlockRegistry.RED_NETHER_QUESTION_BRICKS, Items.NETHER_WART, BlockRegistry.NETHER_QUESTION_BRICKS, output);
        plusRecipe(1, "smashable_blocks", BlockRegistry.SMASHABLE_RED_NETHER_BRICKS, Items.NETHER_WART, BlockRegistry.SMASHABLE_NETHER_BRICKS, output);
        plusRecipe(1, "storage_bricks", BlockRegistry.STORAGE_RED_NETHER_BRICKS, Items.NETHER_WART, BlockRegistry.STORAGE_NETHER_BRICKS, output);
        twoItemRecipe(1, "brick_pedestals", BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STONE_BRICK_PEDESTAL, Blocks.MOSS_BLOCK, output);
        twoItemRecipe(1, "brick_pedestals", BlockRegistry.MOSSY_STONE_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STONE_BRICK_PEDESTAL, Blocks.VINE, output);
        twoItemRecipe(1, "invisible_question_blocks", BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS, Blocks.MOSS_BLOCK, output);
        twoItemRecipe(1, "invisible_question_blocks", BlockRegistry.INVISIBLE_MOSSY_STONE_QUESTION_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.INVISIBLE_STONE_QUESTION_BRICKS, Blocks.VINE, output);
        twoItemRecipe(1, "question_blocks", BlockRegistry.MOSSY_STONE_QUESTION_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STONE_QUESTION_BRICKS, Blocks.MOSS_BLOCK, output);
        twoItemRecipe(1, "question_blocks", BlockRegistry.MOSSY_STONE_QUESTION_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STONE_QUESTION_BRICKS, Blocks.VINE, output);
        twoItemRecipe(1, "smashable_blocks", BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_STONE_BRICKS, Blocks.MOSS_BLOCK, output);
        twoItemRecipe(1, "smashable_blocks", BlockRegistry.SMASHABLE_MOSSY_STONE_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_STONE_BRICKS, Blocks.VINE, output);
        twoItemRecipe(1, "storage_bricks", BlockRegistry.STORAGE_MOSSY_STONE_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_STONE_BRICKS, Blocks.MOSS_BLOCK, output);
        twoItemRecipe(1, "storage_bricks", BlockRegistry.STORAGE_MOSSY_STONE_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_STONE_BRICKS, Blocks.VINE, output);
        twoItemTagRecipe(1, "warp_pipes", "_from_glass", BlockRegistry.CLEAR_WARP_PIPE, RecipeCategory.BUILDING_BLOCKS, TagRegistry.DYEABLE_WARP_PIPE_ITEMS, Tags.Items.GLASS_BLOCKS_COLORLESS, output);
        warpPipeRecipe(4, BlockRegistry.CLEAR_WARP_PIPE, Tags.Items.INGOTS_COPPER, Tags.Items.GLASS_BLOCKS_COLORLESS, Tags.Items.GEMS_DIAMOND, Tags.Items.ENDER_PEARLS, output);

        pedestalRecipe(5, BlockRegistry.CUT_COPPER_PEDESTAL, Blocks.CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Blocks.EXPOSED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Blocks.OXIDIZED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Blocks.WAXED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Blocks.WAXED_EXPOSED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Blocks.WAXED_OXIDIZED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Blocks.WAXED_WEATHERED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Blocks.WEATHERED_CUT_COPPER, output);

        questionBlockRecipe(1, BlockRegistry.COPPER_QUESTION_BLOCK, Blocks.COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, Blocks.EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, Blocks.OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, Blocks.WAXED_COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, Blocks.WAXED_OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, Blocks.WAXED_WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, Blocks.WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, output);

        storageBrickRecipe(4, BlockRegistry.STORAGE_CUT_COPPER, Blocks.CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, Blocks.EXPOSED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, Blocks.OXIDIZED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_CUT_COPPER, Blocks.WAXED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER, Blocks.WAXED_EXPOSED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, Blocks.WAXED_OXIDIZED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER, Blocks.WAXED_WEATHERED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, Blocks.WEATHERED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);

        waxedBlockRecipe(1, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, BlockRegistry.SMASHABLE_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.STORAGE_WAXED_CUT_COPPER, BlockRegistry.STORAGE_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, BlockRegistry.COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, BlockRegistry.CUT_COPPER_PEDESTAL, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.HONEYCOMB, output);

        stonecutting(1, BlockRegistry.CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER, output);
        stonecutting(1, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER, output);

        stonecutting(1, BlockRegistry.SMASHABLE_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_CUT_COPPER, output);

        stonecuttingFromBase(4, BlockRegistry.CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_COPPER, output);

        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WAXED_WEATHERED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Blocks.WEATHERED_COPPER, output);

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.AMETHYST, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamiliesRegistry.AMETHYST_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamiliesRegistry.POLISHED_AMETHYST, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.AMETHYST_BRICKS, BlockRegistry.POLISHED_AMETHYST, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.AMETHYST_BRICKS, Blocks.AMETHYST_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.POLISHED_AMETHYST, Blocks.AMETHYST_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamiliesRegistry.DEEP_FUNGAL_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamiliesRegistry.POLISHED_DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.DEEP_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.POLISHED_DEEP_FUNGAL_STONE, BlockRegistry.DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamiliesRegistry.FUNGAL_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamiliesRegistry.POLISHED_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.FUNGAL_BRICKS, BlockRegistry.FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.POLISHED_FUNGAL_STONE, BlockRegistry.FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.BLACKSTONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.BLACKSTONE_BRICKS, Blocks.BLACKSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.DARK_PRISMARINE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.DEEPSLATE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.DEEPSLATE_TILES, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.END_STONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.END_STONE_BRICKS, Blocks.END_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.MOSSY_STONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.MUD_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.MUD_BRICKS, Blocks.PACKED_MUD, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.NETHER_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.PRISMARINE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.PURPUR_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.QUARTZ_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.RED_NETHER_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.STONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.STONE_BRICKS, Blocks.STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamiliesRegistry.TUFF_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.TUFF_BRICKS, Blocks.TUFF, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamiliesRegistry.TUFF_BRICKS, Blocks.POLISHED_TUFF, FeatureFlagSet.of(FeatureFlags.VANILLA));


        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.GOAL_POLES.entrySet()) {
            DyeColor dyeColor = entry.getKey();
            TagKey<Item> dyeItemTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dyes/" + dyeColor.getName()));
            Item woolItem = BuiltInRegistries.ITEM.stream().filter(item -> {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                return itemId != null && itemId.getPath().endsWith("_wool") && itemId.getPath().startsWith(dyeColor.getName());
            }).findFirst().orElse(Items.WHITE_WOOL);

            goalPoleRecipe(4, entry.getValue(), Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_IRON, woolItem, output);
            dyeItemTagRecipe(1, "goal_poles_from_dye", entry.getValue(), RecipeCategory.BUILDING_BLOCKS, dyeItemTag, TagRegistry.DYEABLE_GOAL_POLE_ITEMS, output);
        }

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.WARP_PIPES.entrySet()) {
            DyeColor dyeColor = entry.getKey();
            TagKey<Item> dyeItemTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dyes/" + dyeColor.getName()));

            warpPipeRecipe(4, entry.getValue(), Tags.Items.INGOTS_COPPER, dyeItemTag, Tags.Items.GEMS_DIAMOND, Tags.Items.ENDER_PEARLS, output);
            dyeItemTagRecipe(1, "warp_pipes_from_dye", entry.getValue(), RecipeCategory.BUILDING_BLOCKS, dyeItemTag, TagRegistry.DYEABLE_WARP_PIPE_ITEMS, output);
        }
    }
}