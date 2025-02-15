package com.wenxin2.marioverse.datagen;

import com.google.common.collect.ImmutableMap;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.init.BlockFamiliesRegistry;
import com.wenxin2.marioverse.init.BlockRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
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
    private static final Map<BlockFamilyExtended.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS =
            ImmutableMap.<BlockFamilyExtended.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
                    .put(BlockFamilyExtended.Variant.BUTTON, (outputItem, inputItem) -> buttonBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CHISELED, (outputItem, inputItem) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUT, (outputItem, inputItem) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.DOOR, (outputItem, inputItem) -> doorBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUSTOM_FENCE, (outputItem, inputItem) -> fenceBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.FENCE, (outputItem, inputItem) -> fenceBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUSTOM_FENCE_GATE, (outputItem, inputItem) -> fenceGateBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.FENCE_GATE, (outputItem, inputItem) -> fenceGateBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.SIGN, (outputItem, inputItem) -> signBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.SLAB, (outputItem, inputItem) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.STAIRS, (outputItem, inputItem) -> stairBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.PEDESTAL, (outputItem, inputItem) -> pedestalBuilder(5, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.PRESSURE_PLATE, (outputItem, inputItem) -> pressurePlateBuilder(RecipeCategory.REDSTONE, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.QUESTION_BLOCK, (outputItem, inputItem) -> questionBlockBuilder(1, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.POLISHED, (outputItem, inputItem) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.TRAPDOOR, (outputItem, inputItem) -> trapdoorBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.WALL, (outputItem, inputItem) -> wallBuilder(RecipeCategory.DECORATIONS, outputItem, Ingredient.of(inputItem)))
                    .build();

    public BlockRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    protected void generateForEnabledBlockFamilies(RecipeOutput output, FeatureFlagSet set) {
        BlockFamiliesRegistry.getAllExtendedFamilies().filter(BlockFamilyExtended::shouldGenerateRecipe).forEach(recipes -> generateRecipes(output, recipes, set));
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

        pedestalRecipe(5, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, Blocks.POLISHED_BLACKSTONE_BRICKS, output);
        pedestalRecipe(5, BlockRegistry.BRICK_PEDESTAL, Blocks.BRICKS, output);
        pedestalRecipe(5, BlockRegistry.CUT_COPPER_PEDESTAL, Blocks.CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.DARK_PRISMARINE_PEDESTAL, Blocks.DARK_PRISMARINE, output);
        pedestalRecipe(5, BlockRegistry.EXPOSED_CUT_COPPER_PEDESTAL, Blocks.EXPOSED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.OXIDIZED_CUT_COPPER_PEDESTAL, Blocks.OXIDIZED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_CUT_COPPER_PEDESTAL, Blocks.WAXED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_EXPOSED_CUT_COPPER_PEDESTAL, Blocks.WAXED_EXPOSED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_OXIDIZED_CUT_COPPER_PEDESTAL, Blocks.WAXED_OXIDIZED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WAXED_WEATHERED_CUT_COPPER_PEDESTAL, Blocks.WAXED_WEATHERED_CUT_COPPER, output);
        pedestalRecipe(5, BlockRegistry.WEATHERED_CUT_COPPER_PEDESTAL, Blocks.WEATHERED_CUT_COPPER, output);

        questionBlockRecipe(1, BlockRegistry.BLACKSTONE_QUESTION_BRICKS, Blocks.POLISHED_BLACKSTONE_BRICKS, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.COPPER_QUESTION_BLOCK, Blocks.COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.DARK_PRISMARINE_QUESTION_BLOCK, Blocks.DARK_PRISMARINE, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.EXPOSED_COPPER_QUESTION_BLOCK, Blocks.EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.OXIDIZED_COPPER_QUESTION_BLOCK, Blocks.OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_COPPER_QUESTION_BLOCK, Blocks.WAXED_COPPER_BLOCK, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_EXPOSED_COPPER_QUESTION_BLOCK, Blocks.WAXED_EXPOSED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, Blocks.WAXED_OXIDIZED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WAXED_WEATHERED_COPPER_QUESTION_BLOCK, Blocks.WAXED_WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, output);
        questionBlockRecipe(1, BlockRegistry.WEATHERED_COPPER_QUESTION_BLOCK, Blocks.WEATHERED_COPPER, Tags.Items.CHESTS_WOODEN, output);

        storageBrickRecipe(4, BlockRegistry.STORAGE_AMETHYST_BRICKS, BlockRegistry.AMETHYST_BRICKS, Tags.Items.CHESTS_WOODEN, output);
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


        stonecutting(1, BlockRegistry.AMETHYST_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_AMETHYST, output);
        stonecutting(1, BlockRegistry.DEEP_FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, output);
        stonecutting(1, BlockRegistry.FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.POLISHED_FUNGAL_STONE, output);

        stonecutting(1, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE_BRICKS, output);
        stonecutting(1, BlockRegistry.BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.BRICKS, output);
        stonecutting(1, BlockRegistry.CUT_COPPER_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.CUT_COPPER, output);
        stonecutting(1, BlockRegistry.DARK_PRISMARINE_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.DARK_PRISMARINE, output);
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

        stonecuttingFromBase(1, BlockRegistry.AMETHYST_BRICKS, RecipeCategory.BUILDING_BLOCKS, Blocks.AMETHYST_BLOCK, output);
        stonecuttingFromBase(1, BlockRegistry.DEEP_FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.DEEP_FUNGAL_STONE, output);
        stonecuttingFromBase(1, BlockRegistry.FUNGAL_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FUNGAL_STONE, output);

        stonecuttingFromBase(1, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.BLACKSTONE, output);
        stonecuttingFromBase(1, BlockRegistry.BLACKSTONE_BRICK_PEDESTAL, RecipeCategory.BUILDING_BLOCKS, Blocks.POLISHED_BLACKSTONE, output);
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

        smeltingRecipe(200, 0.1F, BlockRegistry.CRACKED_AMETHYST_BRICKS, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.AMETHYST_BRICKS, output);

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

    protected void smeltingRecipe(int cookingTime, float xp, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(inputItem), category, outputItem, xp, cookingTime)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_smelting");
    }

    protected static void generateRecipes(RecipeOutput output, BlockFamilyExtended family, FeatureFlagSet set) {
        family.getVariants().forEach((variant, block) -> {
                if (block.requiredFeatures().isSubsetOf(set)) {
                    BiFunction<ItemLike, ItemLike, RecipeBuilder> bifunction = SHAPE_BUILDERS.get(variant);
                    ItemLike itemlike = getBaseBlock(family, variant);
                    if (bifunction != null) {
                        RecipeBuilder recipebuilder = bifunction.apply(block, itemlike);
                        family.getRecipeGroupPrefix().ifPresent(
                                string -> recipebuilder.group(string +
                                        (variant == BlockFamilyExtended.Variant.CUT ? "" : "_" + variant.getRecipeGroup()))
                        );
                        recipebuilder.unlockedBy(family.getRecipeUnlockedBy().orElseGet(() -> getHasName(itemlike)), has(itemlike));
                        recipebuilder.save(output);
                    }

                    if (variant == BlockFamilyExtended.Variant.CRACKED)
                        smeltingResultFromBase(output, block, itemlike);
                }
            }
        );
    }

    protected static Block getBaseBlock(BlockFamilyExtended family, BlockFamilyExtended.Variant variant) {
        if (variant == BlockFamilyExtended.Variant.CHISELED) {
            if (!family.getVariants().containsKey(BlockFamilyExtended.Variant.SLAB))
                throw new IllegalStateException("Slab is not defined for the family.");
            else return family.get(BlockFamilyExtended.Variant.SLAB);
        } else return family.getBaseBlock();
    }

    protected void generateStonecuttingRecipes(RecipeOutput output, BlockFamilyExtended family, FeatureFlagSet featureFlags) {
        family.getVariants().forEach((variant, block) -> {
            if (block.requiredFeatures().isSubsetOf(featureFlags)) {
                ItemLike baseBlock = (variant == BlockFamilyExtended.Variant.CHISELED)
                        ? family.getBaseBlock() : getBaseBlock(family, variant);
                int outputAmount = BlockFamiliesRegistry.STONECUTTING_OUTPUTS.getOrDefault(variant, 1);

                if (variant != BlockFamilyExtended.Variant.BUTTON && variant != BlockFamilyExtended.Variant.DOOR
                        && variant != BlockFamilyExtended.Variant.PRESSURE_PLATE) {
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, block, outputAmount)
                            .unlockedBy(getHasName(baseBlock), has(baseBlock))
                            .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(block) + "_stonecutting");
                }
            }
        });
    }

    protected void generateStonecuttingFromBaseRecipes(RecipeOutput output, BlockFamilyExtended family, ItemLike inputItem, FeatureFlagSet featureFlags) {
        family.getVariants().forEach((variant, block) -> {
            if (block.requiredFeatures().isSubsetOf(featureFlags)) {
                ItemLike baseBlock = (variant == BlockFamilyExtended.Variant.CHISELED)
                        ? family.getBaseBlock() : getBaseBlock(family, variant);
                int outputAmount = BlockFamiliesRegistry.STONECUTTING_OUTPUTS.getOrDefault(variant, 1);

                if (variant != BlockFamilyExtended.Variant.BUTTON && variant != BlockFamilyExtended.Variant.DOOR
                        && variant != BlockFamilyExtended.Variant.PRESSURE_PLATE) {
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), RecipeCategory.BUILDING_BLOCKS, block, outputAmount)
                            .unlockedBy(getHasName(baseBlock), has(baseBlock))
                            .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(block, inputItem) + "_stonecutting");
                }
            }
        });
    }

    private static RecipeBuilder pedestalBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("# #")
                .pattern("###")
                .group(Marioverse.MOD_ID + ":brick_pedestals");
    }

    private static RecipeBuilder questionBlockBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItem)
                .requires(Tags.Items.CHESTS_WOODEN)
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN))
                .group(Marioverse.MOD_ID + ":question_blocks");
    }
}
