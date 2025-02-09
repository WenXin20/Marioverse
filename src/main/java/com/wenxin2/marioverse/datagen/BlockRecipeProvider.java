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
        pedestalRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, 5);
        questionBlockRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COPPER_QUESTION_BLOCK, Items.COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, 1);
        storageBrickRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_CUT_COPPER, Items.CUT_COPPER, Tags.Items.CHESTS_WOODEN, 4);

        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, 1);
        stonecutterRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.CUT_COPPER, 1);

        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.COPPER_BLOCK, 1);
        stonecutterFromBaseRecipe(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.COPPER_BLOCK, 1);
    }

    private void pedestalRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem, int outputAmt) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('B', inputItem)
                .pattern("B B")
                .pattern("BBB")
                .unlockedBy("has_block", has(inputItem))
                .save(output);
    }

    private void questionBlockRecipe(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem,
                                     TagKey<Item> itemTag, int outputAmt) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItem)
                .requires(itemTag)
                .unlockedBy("has_block", has(inputItem))
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
                .unlockedBy("has_block", has(inputItem))
                .unlockedBy("has_chest", has(itemTag))
                .save(output);
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
