package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

public class ModBlockRecipeProvider extends RecipeProvider {
    public ModBlockRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COPPER_QUESTION_BLOCK, Items.COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_CUT_COPPER, Items.CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.COPPER_BLOCK, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.COPPER_BLOCK, 1);

        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.EXPOSED_CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, Items.EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, Items.EXPOSED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.EXPOSED_CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, Items.EXPOSED_CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.EXPOSED_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, Items.EXPOSED_COPPER, 1);

        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.WEATHERED_CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, Items.WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, Items.WEATHERED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.WEATHERED_CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, Items.WEATHERED_CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.WEATHERED_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, Items.WEATHERED_COPPER, 1);

        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.OXIDIZED_CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, Items.OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, Items.OXIDIZED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.OXIDIZED_CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, Items.OXIDIZED_CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.OXIDIZED_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, Items.OXIDIZED_COPPER, 1);

        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Items.WAXED_CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, Items.WAXED_COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_CUT_COPPER, Items.WAXED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Items.WAXED_CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, Items.WAXED_CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Items.WAXED_COPPER_BLOCK, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, Items.WAXED_COPPER_BLOCK, 1);

        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Items.WAXED_EXPOSED_CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, Items.WAXED_EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Items.WAXED_EXPOSED_CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Items.WAXED_EXPOSED_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_COPPER, 1);

        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Items.WAXED_WEATHERED_CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, Items.WAXED_WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Items.WAXED_WEATHERED_CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Items.WAXED_WEATHERED_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_COPPER, 1);

        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Items.WAXED_OXIDIZED_CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, Items.WAXED_OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Items.WAXED_OXIDIZED_CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Items.WAXED_OXIDIZED_COPPER, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_COPPER, 1);

        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, BlockRegistry.CUT_COPPER_PEDESTAL, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.HONEYCOMB, 1);

        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, BlockRegistry.COPPER_QUESTION_BLOCK, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, 1);

        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, BlockRegistry.SMASHABLE_CUT_COPPER, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, Items.HONEYCOMB, 1);

        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_CUT_COPPER, BlockRegistry.STORAGE_CUT_COPPER, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_EXPOSED_CUT_COPPER, BlockRegistry.STORAGE_EXPOSED_CUT_COPPER, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_WEATHERED_CUT_COPPER, BlockRegistry.STORAGE_WEATHERED_CUT_COPPER, Items.HONEYCOMB, 1);
        waxedBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_WAXED_OXIDIZED_CUT_COPPER, BlockRegistry.STORAGE_OXIDIZED_CUT_COPPER, Items.HONEYCOMB, 1);
    }

    private void pedestalRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem, int outputAmt) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('B', inputItem)
                .pattern("B B")
                .pattern("BBB")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output);
    }

    private void questionBlockRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem,
                                     TagKey<Item> itemTag, int outputAmt) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItem)
                .requires(itemTag)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_chest", has(itemTag))
                .save(output);
    }

    private void storageBrickRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem,
                                    TagKey<Item> itemTag, int outputAmt) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('B', inputItem)
                .define('C', itemTag)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_chest", has(itemTag))
                .save(output);
    }

    private void waxedBlockRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem,
                                  ItemLike inputItem2, int outputAmt) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItem)
                .requires(inputItem2)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem2));
    }

    protected void stonecutterRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem, int outputAmt) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), category, outputItem, outputAmt)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_stonecutting");
    }

    protected void stonecutterFromBaseRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem, int outputAmt) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), category, outputItem, outputAmt)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem) + "_stonecutting");
    }
}
