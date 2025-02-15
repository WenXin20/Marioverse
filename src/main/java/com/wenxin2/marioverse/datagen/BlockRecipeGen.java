package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BlockFamiliesRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredBlock;

public class BlockRecipeGen extends RecipeProvider {
    public BlockRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    protected void generateForEnabledBlockFamilies(RecipeOutput output, FeatureFlagSet set) {
        BlockFamiliesRegistry.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe).forEach(recipes -> generateRecipes(output, recipes, set));
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        generateForEnabledBlockFamilies(output, FeatureFlagSet.of(FeatureFlags.VANILLA));

        classicGoalPoleRecipe(4, BlockRegistry.CLASSIC_GOAL_POLE, Tags.Items.INGOTS_GOLD, Tags.Items.DYES_LIME, Items.WHITE_WOOL, Items.BAMBOO, output);
        coinRecipe(4, BlockRegistry.COIN, Tags.Items.INGOTS_GOLD, Tags.Items.NUGGETS_GOLD, output);
        twoByTwoRecipe(4, BlockRegistry.AMETHYST_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_AMETHYST, output);
        twoByTwoRecipe(4, BlockRegistry.DEEP_FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, output);
        twoByTwoRecipe(4, BlockRegistry.FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_FUNGAL_STONE, output);
        twoItemTagRecipe(1, "warp_pipes", "_from_glass", BlockRegistry.CLEAR_WARP_PIPE, RecipeCategory.BUILDING_BLOCKS, TagRegistry.DYEABLE_WARP_PIPE_ITEMS, Tags.Items.GLASS_BLOCKS_COLORLESS, output);
        warpPipeRecipe(4, BlockRegistry.CLEAR_WARP_PIPE, Tags.Items.INGOTS_COPPER, Tags.Items.GLASS_BLOCKS_COLORLESS, Tags.Items.GEMS_DIAMOND, Tags.Items.ENDER_PEARLS, output);

        pedestalRecipe(5, BlockRegistry.AMETHYST_BRICK_PEDESTAL, BlockRegistry.AMETHYST_BRICKS, output);
        pedestalRecipe(5, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.EXPOSED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.OXIDIZED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Items.WAXED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Items.WAXED_EXPOSED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Items.WAXED_OXIDIZED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Items.WAXED_WEATHERED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.WEATHERED_CUT_COPPER, output);

        questionBlockRecipe(1, BlockRegistry.AMETHYST_QUESTION_BLOCK, BlockRegistry.POLISHED_AMETHYST, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.COPPER_QUESTION_BLOCK, Items.COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, Items.EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, Items.OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, Items.WAXED_COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, Items.WAXED_EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, Items.WAXED_OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, Items.WAXED_WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, Items.WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, output);

        storageBrickRecipe(4, BlockRegistry.STORAGE_AMETHYST_BRICKS, BlockRegistry.AMETHYST_BRICKS, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_CUT_COPPER, Items.CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, Items.EXPOSED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, Items.OXIDIZED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_CUT_COPPER, Items.WAXED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(4, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, Items.WEATHERED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);

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


        stonecutting(1, BlockRegistry.AMETHYST_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_AMETHYST, output);
        stonecutting(1, BlockRegistry.DEEP_FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, output);
        stonecutting(1, BlockRegistry.FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_FUNGAL_STONE, output);

        stonecutting(1, BlockRegistry.AMETHYST_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.AMETHYST_BRICKS, output);
        stonecutting(1, BlockRegistry.CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.CUT_COPPER, output);
        stonecutting(1, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_WEATHERED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WEATHERED_CUT_COPPER, output);

        stonecutting(1, BlockRegistry.SMASHABLE_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_EXPOSED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_OXIDIZED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_WEATHERED_CUT_COPPER, output);
        stonecutting(1, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WEATHERED_CUT_COPPER, output);

        stonecuttingFromBase(1, BlockRegistry.AMETHYST_BRICKS, RecipeCategory.BUILDING_BLOCKS, Blocks.AMETHYST_BLOCK, output);
        stonecuttingFromBase(1, BlockRegistry.DEEP_FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.DEEP_FUNGAL_STONE, output);
        stonecuttingFromBase(1, BlockRegistry.FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FUNGAL_STONE, output);

        stonecuttingFromBase(1, BlockRegistry.AMETHYST_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_AMETHYST, output);
        stonecuttingFromBase(1, BlockRegistry.AMETHYST_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.AMETHYST_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_WEATHERED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Items.WEATHERED_COPPER, output);

        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_COPPER_BLOCK, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_EXPOSED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_OXIDIZED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WAXED_WEATHERED_COPPER, output);
        stonecuttingFromBase(4, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, RecipeCategory.BUILDING_BLOCKS, Items.WEATHERED_COPPER, output);

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

    private void oneByTwoRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("##")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void oneByThreeRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("###")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void twoByOneRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("#")
                .pattern("#")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void twoByTwoRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void twoByThreeRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void classicGoalPoleRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2,
                                       ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('B', inputItem2)
                .define('D', inputItemTag2)
                .define('G', inputItemTag)
                .define('W', inputItem)
                .pattern("DG")
                .pattern("WB")
                .pattern(" B")
                .unlockedBy("has_gold_ingot", has(inputItemTag))
                .unlockedBy("has_dye", has(inputItemTag2))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":goal_poles")
                .save(output);
    }

    private void coinRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('I', inputItemTag)
                .define('N', inputItemTag2)
                .pattern("NNN")
                .pattern("NIN")
                .pattern("NNN")
                .unlockedBy("has_ingot", has(inputItemTag))
                .unlockedBy("has_nugget", has(inputItemTag2))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void goalPoleRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag,
                                TagKey<Item> inputItemTag2, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('G', inputItemTag)
                .define('I', inputItemTag2)
                .define('W', inputItem)
                .pattern(" G")
                .pattern("WI")
                .pattern(" I")
                .unlockedBy("has_gold_ingot", has(inputItemTag))
                .unlockedBy("has_iron_ingot", has(inputItemTag2))
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":goal_poles")
                .save(output);
    }

    private void pedestalRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('B', inputItem)
                .pattern("B B")
                .pattern("BBB")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":brick_pedestals")
                .save(output);
    }

    private void stairRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void storageBrickRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, TagKey<Item> inputItemTag, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('B', inputItem)
                .define('C', inputItemTag)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_chest", has(inputItemTag))
                .group(Marioverse.MOD_ID + ":storage_bricks")
                .save(output);
    }

    private void warpPipeRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2,
                                TagKey<Item> inputItemTag3, TagKey<Item> inputItemTag4, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('I', inputItemTag)
                .define('D', inputItemTag2)
                .define('G', inputItemTag3)
                .define('E', inputItemTag4)
                .pattern("IDI")
                .pattern("IGI")
                .pattern("IEI")
                .unlockedBy("has_copper_ingot", has(inputItemTag))
                .unlockedBy("has_dye", has(inputItemTag2))
                .unlockedBy("has_diamond", has(inputItemTag3))
                .unlockedBy("has_ender_pearl", has(inputItemTag4))
                .group(Marioverse.MOD_ID + ":warp_pipes")
                .save(output);
    }

    private void dyeItemRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                               TagKey<Item> inputItemTag, ItemLike inputItem, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItemTag)
                .requires(inputItem)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_goal_pole", has(inputItemTag))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getItemName(outputItem) + "_from_dye");
    }

    private void dyeItemTagRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                  TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItemTag)
                .requires(inputItemTag2)
                .unlockedBy("has_dye", has(inputItemTag))
                .unlockedBy("has_goal_pole", has(inputItemTag2))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getItemName(outputItem) + "_from_dye");
    }

    private void questionBlockRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, TagKey<Item> itemTag, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItem)
                .requires(itemTag)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_chest", has(itemTag))
                .group(Marioverse.MOD_ID + ":question_blocks")
                .save(output);
    }

    private void singleItemRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItem)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    private void twoItemRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItem)
                .requires(inputItem2)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem2));
    }

    private void twoItemTagRecipe(int outputAmt, String groupName, String recipeName, ItemLike outputItem, RecipeCategory category,
                                  TagKey<Item> itemTag, TagKey<Item> itemTag2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(itemTag)
                .requires(itemTag2)
                .unlockedBy("has_tag_item", has(itemTag))
                .unlockedBy("has_tag_item2", has(itemTag2))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + recipeName);
    }

    private void waxedBlockRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItem)
                .requires(inputItem2)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem2));
    }

    protected void stonecutting(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), category, outputItem, outputAmt)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_stonecutting");
    }

    protected void stonecuttingFromBase(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), category, outputItem, outputAmt)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem) + "_stonecutting");
    }

    protected void generateStonecuttingRecipes(RecipeOutput output, BlockFamily family, FeatureFlagSet featureFlags) {
        family.getVariants().forEach((variant, block) -> {
            if (block.requiredFeatures().isSubsetOf(featureFlags)) {
                ItemLike baseBlock = getBaseBlock(family, variant);
                int outputAmount = BlockFamiliesRegistry.STONECUTTING_OUTPUTS.getOrDefault(variant, 1);

                if (variant != BlockFamily.Variant.BUTTON && variant != BlockFamily.Variant.PRESSURE_PLATE
                        && variant != BlockFamily.Variant.DOOR) {
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, block, outputAmount)
                            .unlockedBy(getHasName(baseBlock), has(baseBlock))
                            .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(block) + "_stonecutting");
                }
            }
        });
    }

    protected void generateStonecuttingFromBaseRecipes(RecipeOutput output, BlockFamily family, ItemLike inputItem, FeatureFlagSet featureFlags) {
        family.getVariants().forEach((variant, block) -> {
            if (block.requiredFeatures().isSubsetOf(featureFlags)) {
                ItemLike baseBlock = getBaseBlock(family, variant);
                int outputAmount = BlockFamiliesRegistry.STONECUTTING_OUTPUTS.getOrDefault(variant, 1);

                if (variant != BlockFamily.Variant.BUTTON || variant != BlockFamily.Variant.PRESSURE_PLATE
                        || variant != BlockFamily.Variant.DOOR) {
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), RecipeCategory.BUILDING_BLOCKS, block, outputAmount)
                            .unlockedBy(getHasName(baseBlock), has(baseBlock))
                            .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(block, inputItem) + "_stonecutting");
                }
            }
        });
    }
}
