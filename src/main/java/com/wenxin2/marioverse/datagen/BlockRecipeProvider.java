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

public class BlockRecipeProvider extends RecipeProvider {
    public BlockRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        pedestalRecipe(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, output);
        questionBlockRecipe(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COPPER_QUESTION_BLOCK, Items.COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_CUT_COPPER, Items.CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);

        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.CUT_COPPER, 1);

        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.COPPER_BLOCK, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.COPPER_BLOCK, 1);
    }

    private void pedestalRecipe(RecipeCategory category, ItemLike resultItem, ItemLike recipeItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, resultItem)
                .define('B', recipeItem)
                .pattern("B B")
                .pattern("BBB")
                .unlockedBy("has_block", has(recipeItem))
                .save(output);
    }

    private void questionBlockRecipe(RecipeCategory category, ItemLike resultItem, ItemLike recipeItem,
                                     TagKey<Item> itemTag, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, resultItem)
                .requires(recipeItem)
                .requires(itemTag)
                .unlockedBy("has_block", has(recipeItem))
                .unlockedBy("has_chest", has(itemTag))
                .save(output);
    }

    private void storageBrickRecipe(RecipeCategory category, ItemLike resultItem, ItemLike recipeItem,
                                    TagKey<Item> itemTag, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, resultItem)
                .define('B', recipeItem)
                .define('C', itemTag)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .unlockedBy("has_block", has(recipeItem))
                .unlockedBy("has_chest", has(itemTag))
                .save(output);
    }

    protected void stonecutterRecipe(RecipeOutput output, RecipeCategory category, ItemLike resultItem, ItemLike recipeItem, int outputAmt) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(recipeItem), category, resultItem, outputAmt)
                .unlockedBy(getHasName(recipeItem), has(recipeItem))
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(resultItem) + "_stonecutting");
    }

    protected void stonecutterFromBaseRecipe(RecipeOutput output, RecipeCategory category, ItemLike resultItem, ItemLike recipeItem, int outputAmt) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(recipeItem), category, resultItem, outputAmt)
                .unlockedBy(getHasName(recipeItem), has(recipeItem))
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(resultItem, recipeItem) + "_stonecutting");
    }
}
