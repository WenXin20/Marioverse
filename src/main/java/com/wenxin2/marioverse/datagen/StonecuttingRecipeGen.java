package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.init.BlockRegistry;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class StonecuttingRecipeGen extends RecipeProvider {
    public StonecuttingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.AMETHYST_BRICK_PEDESTAL, BlockRegistry.AMETHYST_BRICKS, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.AMETHYST_BRICK_PEDESTAL, BlockRegistry.POLISHED_AMETHYST, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.AMETHYST_BRICK_PEDESTAL, Items.AMETHYST_BLOCK, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.CUT_COPPER_PEDESTAL, Items.COPPER_BLOCK, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_CUT_COPPER, Items.COPPER_BLOCK, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.EXPOSED_CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, Items.EXPOSED_CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Items.EXPOSED_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_EXPOSED_CUT_COPPER, Items.EXPOSED_COPPER, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.WEATHERED_CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, Items.WEATHERED_CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Items.WEATHERED_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WEATHERED_CUT_COPPER, Items.WEATHERED_COPPER, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.OXIDIZED_CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, Items.OXIDIZED_CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Items.OXIDIZED_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_OXIDIZED_CUT_COPPER, Items.OXIDIZED_COPPER, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Items.WAXED_CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, Items.WAXED_CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Items.WAXED_COPPER_BLOCK, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_CUT_COPPER, Items.WAXED_COPPER_BLOCK, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Items.WAXED_EXPOSED_CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Items.WAXED_EXPOSED_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_EXPOSED_CUT_COPPER, Items.WAXED_EXPOSED_COPPER, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Items.WAXED_WEATHERED_CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Items.WAXED_WEATHERED_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_WEATHERED_CUT_COPPER, Items.WAXED_WEATHERED_COPPER, 1);

        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Items.WAXED_OXIDIZED_CUT_COPPER, 1);
        stonecutting(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_CUT_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Items.WAXED_OXIDIZED_COPPER, 1);
        stonecuttingFromBase(output, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.SMASHABLE_WAXED_OXIDIZED_CUT_COPPER, Items.WAXED_OXIDIZED_COPPER, 1);
    }

    protected void stonecutting(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem, int outputAmt) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), category, outputItem, outputAmt)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_stonecutting");
    }

    protected void stonecuttingFromBase(RecipeOutput output, RecipeCategory category, ItemLike outputItem, ItemLike inputItem, int outputAmt) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), category, outputItem, outputAmt)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem) + "_stonecutting");
    }
}
