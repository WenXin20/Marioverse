package com.wenxin2.marioverse.datagen;

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
        pedestalRecipe(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COPPER_QUESTION_BLOCK, Items.CUT_COPPER, output);
        questionBlockRecipe(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.COPPER_QUESTION_BLOCK, Items.COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        storageBrickRecipe(RecipeCategory.BUILDING_BLOCKS, BlockRegistry.STORAGE_CUT_COPPER, Items.CUT_COPPER, Tags.Items.CHESTS_WOODEN, output);

        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Items.COPPER_BLOCK, BlockRegistry.SMASHABLE_CUT_COPPER);
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Items.CUT_COPPER, BlockRegistry.SMASHABLE_CUT_COPPER);
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Items.COPPER_BLOCK, BlockRegistry.CUT_COPPER_PEDESTAL);
        stonecutterResultFromBase(output, RecipeCategory.BUILDING_BLOCKS, Items.CUT_COPPER, BlockRegistry.CUT_COPPER_PEDESTAL);
    }

    private void pedestalRecipe(RecipeCategory category, ItemLike itemResult, ItemLike recipeItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, itemResult)
                .define('B', recipeItem)
                .pattern("B B")
                .pattern("BBB")
                .unlockedBy("has_block", has(recipeItem))
                .save(output);
    }

    private void questionBlockRecipe(RecipeCategory category, ItemLike itemResult, ItemLike recipeItem,
                                     TagKey<Item> itemTag, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, itemResult)
                .requires(recipeItem)
                .requires(itemTag)
                .unlockedBy("has_block", has(recipeItem))
                .save(output);
    }

    private void storageBrickRecipe(RecipeCategory category, ItemLike itemResult, ItemLike recipeItem,
                                    TagKey<Item> itemTag, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, itemResult)
                .define('B', recipeItem)
                .define('C', itemTag)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .unlockedBy("has_block", has(recipeItem))
                .save(output);
    }
}
