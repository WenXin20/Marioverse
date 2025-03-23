package com.wenxin2.marioverse.data;

import com.google.common.collect.ImmutableMap;
import com.wenxin2.marioverse.Marioverse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class RecipeUtils extends RecipeProvider {
    public static final Map<BlockFamilyExtended.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS =
            ImmutableMap.<BlockFamilyExtended.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
                    .put(BlockFamilyExtended.Variant.BUTTON, (outputItem, inputItem) -> buttonBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.BRICKS, (outputItem, inputItem) -> twoByTwoBuilder(4, outputItem, RecipeCategory.BUILDING_BLOCKS, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CHISELED, (outputItem, inputItem) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUSTOM_FENCE, (outputItem, inputItem) -> fenceBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUSTOM_FENCE_GATE, (outputItem, inputItem) -> fenceGateBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUT, (outputItem, inputItem) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.DOOR, (outputItem, inputItem) -> doorBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.FENCE, (outputItem, inputItem) -> fenceBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.FENCE_GATE, (outputItem, inputItem) -> fenceGateBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.PEDESTAL, (outputItem, inputItem) -> pedestalBuilder(5, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.POLISHED, (outputItem, inputItem) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.PRESSURE_PLATE, (outputItem, inputItem) -> pressurePlateBuilder(RecipeCategory.REDSTONE, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.QUESTION_BLOCK, (outputItem, inputItem) -> questionBlockBuilder(1, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.SIGN, (outputItem, inputItem) -> signBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.SLAB, (outputItem, inputItem) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.STAIRS, (outputItem, inputItem) -> stairBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.STORAGE_BRICKS, (outputItem, inputItem) -> storageBrickBuilder(4, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.TRAPDOOR, (outputItem, inputItem) -> trapdoorBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.WALL, (outputItem, inputItem) -> wallBuilder(RecipeCategory.DECORATIONS, outputItem, Ingredient.of(inputItem)))
                    .build();

    public static final Map<BlockFamilyExtended.Variant, Integer> STONECUTTING_OUTPUTS = Map.of(
            BlockFamilyExtended.Variant.BRICKS, 1,
            BlockFamilyExtended.Variant.CHISELED, 1,
            BlockFamilyExtended.Variant.CUT, 1,
            BlockFamilyExtended.Variant.PEDESTAL, 1,
            BlockFamilyExtended.Variant.POLISHED, 1,
            BlockFamilyExtended.Variant.SLAB, 2,
            BlockFamilyExtended.Variant.SMASHABLE_BLOCKS, 1,
            BlockFamilyExtended.Variant.STAIRS, 1,
            BlockFamilyExtended.Variant.WALL, 1
    );

    public RecipeUtils(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    public static RecipeBuilder twoByTwoBuilder(int outputAmt, ItemLike outputItem, RecipeCategory category, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("##")
                .pattern("##");
    }

    public static RecipeBuilder pedestalBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("# #")
                .pattern("###")
                .group(Marioverse.MOD_ID + ":brick_pedestals");
    }

    public static RecipeBuilder questionBlockBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItem)
                .requires(Tags.Items.CHESTS_WOODEN)
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN))
                .group(Marioverse.MOD_ID + ":question_blocks");
    }

    public static RecipeBuilder storageBrickBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('B', inputItem)
                .define('C', Tags.Items.CHESTS_WOODEN)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN))
                .group(Marioverse.MOD_ID + ":storage_bricks");
    }

    public void oneByTwoRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("##")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void oneByThreeRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("###")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void twoByOneRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("#")
                .pattern("#")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void twoByTwoRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("##")
                .pattern("##")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void twoByThreeRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void plusRecipe(int outputAmt, String groupName, ItemLike outputItem, ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('B', inputItem)
                .define('C', inputItem2)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem));
    }

    public void bootsRecipe(int outputAmt, String groupName, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output);
    }

    public void checkpointFlagRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag,
                               TagKey<Item> inputItemTag2, ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('G', inputItemTag)
                .define('I', inputItemTag2)
                .define('W', inputItem)
                .define('S', inputItem2)
                .pattern(" G")
                .pattern("WI")
                .pattern(" S")
                .unlockedBy("has_gold_ingot", has(inputItemTag))
                .unlockedBy("has_iron_ingot", has(inputItemTag2))
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .group(Marioverse.MOD_ID + ":checkpoint_flags")
                .save(output);
    }

    public void chestplateRecipe(int outputAmt, String groupName, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("# #")
                .pattern("###")
                .pattern("###")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output);
    }

    public void classicCheckpointFlagRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, ItemLike inputItem,
                                            ItemLike inputItem2, ItemLike inputItem3, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('M', inputItem)
                .define('W', inputItem2)
                .define('G', inputItemTag)
                .define('S', inputItem3)
                .pattern("MG")
                .pattern("WG")
                .pattern(" S")
                .unlockedBy("has_gold_ingot", has(inputItemTag))
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .unlockedBy(getHasName(inputItem3), has(inputItem3))
                .group(Marioverse.MOD_ID + ":checkpoint_flags")
                .save(output);
    }

    public void classicGoalPoleRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2,
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

    public void coinRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('I', inputItemTag)
                .define('N', inputItemTag2)
                .pattern(" N ")
                .pattern("NIN")
                .pattern(" N ")
                .unlockedBy("has_ingot", has(inputItemTag))
                .unlockedBy("has_nugget", has(inputItemTag2))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void fireShirtRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('W', inputItem)
                .define('R', inputItem2)
                .pattern("W W")
                .pattern("RRR")
                .pattern("RRR")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":fire_costume")
                .save(output);
    }

    public void fireShoesRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, TagKey<Item> inputItemTag, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('W', inputItem)
                .define('L', inputItemTag)
                .pattern("W W")
                .pattern("L L")
                .unlockedBy("has_leather", has(inputItemTag))
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":fire_costume")
                .save(output);
    }

    public void goalPoleRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag,
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

    public void helmetRecipe(int outputAmt, String groupName, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("###")
                .pattern("# #")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output);
    }

    public void leggingsRecipe(int outputAmt, String groupName, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("###")
                .pattern("# #")
                .pattern("# #")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output);
    }

    public void pedestalRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('B', inputItem)
                .pattern("B B")
                .pattern("BBB")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":brick_pedestals")
                .save(output);
    }

    public void smithingTemplateRecipe(int outputAmt, ItemLike outputItem, ItemLike templateItem, ItemLike inputItem, TagKey<Item> inputItemTag, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('T', templateItem)
                .define('W', inputItem)
                .define('L', inputItemTag)
                .pattern("LTL")
                .pattern("LWL")
                .pattern("LLL")
                .unlockedBy(getHasName(templateItem), has(templateItem))
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_leather", has(inputItemTag))
                .group(Marioverse.MOD_ID + ":mario_costume_templates")
                .save(output);
    }

    public void smithingTemplateRecipe(int outputAmt, ItemLike outputItem, ItemLike templateItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('T', templateItem)
                .define('I', inputItemTag)
                .define('L', inputItemTag2)
                .pattern("LTL")
                .pattern("LIL")
                .pattern("LLL")
                .unlockedBy(getHasName(templateItem), has(templateItem))
                .unlockedBy("has_ice", has(inputItemTag))
                .unlockedBy("has_leather", has(inputItemTag))
                .group(Marioverse.MOD_ID + ":mario_costume_templates")
                .save(output);
    }

    public void stairRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("#  ")
                .pattern("## ")
                .pattern("###")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void storageBrickRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, TagKey<Item> inputItemTag, RecipeOutput output) {
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

    public void warpDisruptorRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('E', inputItem)
                .define('G', inputItemTag)
                .define('S', inputItemTag2)
                .pattern("  E")
                .pattern(" G ")
                .pattern("S  ")
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_gold_ingot", has(inputItemTag))
                .unlockedBy("has_stick", has(inputItemTag2))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void warpPipeRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2,
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

    public void wrenchRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('C', inputItemTag)
                .define('I', inputItemTag2)
                .pattern("C C")
                .pattern(" C ")
                .pattern(" I ")
                .unlockedBy("has_copper_ingot", has(inputItemTag))
                .unlockedBy("has_iron_ingot", has(inputItemTag2))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void dyeItemRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                              TagKey<Item> inputItemTag, ItemLike inputItem, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItemTag)
                .requires(inputItem)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_goal_pole", has(inputItemTag))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getItemName(outputItem) + "_from_dye");
    }

    public void dyeItemTagRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                 TagKey<Item> inputItemTag, TagKey<Item> inputItemTag2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItemTag)
                .requires(inputItemTag2)
                .unlockedBy("has_dye", has(inputItemTag))
                .unlockedBy("has_goal_pole", has(inputItemTag2))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getItemName(outputItem) + "_from_dye");
    }

    public void questionBlockRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, TagKey<Item> itemTag, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItem)
                .requires(itemTag)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy("has_chest", has(itemTag))
                .group(Marioverse.MOD_ID + ":question_blocks")
                .save(output);
    }

    public void singleItemRecipe(int outputAmt, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItem)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void twoItemRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category, ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(inputItem)
                .requires(inputItem2)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .unlockedBy(getHasName(inputItem2), has(inputItem2))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, inputItem2));
    }

    public void twoItemTagRecipe(int outputAmt, String groupName, String recipeName, ItemLike outputItem, RecipeCategory category,
                                 TagKey<Item> itemTag, TagKey<Item> itemTag2, RecipeOutput output) {
        ShapelessRecipeBuilder.shapeless(category, outputItem, outputAmt)
                .requires(itemTag)
                .requires(itemTag2)
                .unlockedBy("has_tag_item", has(itemTag))
                .unlockedBy("has_tag_item2", has(itemTag2))
                .group(Marioverse.MOD_ID + ":" + groupName)
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + recipeName);
    }

    public void waxedBlockRecipe(int outputAmt, ItemLike outputItem, ItemLike inputItem, ItemLike inputItem2, RecipeOutput output) {
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

    protected static void smeltingResultFromBase(RecipeOutput output, ItemLike outputItem, ItemLike inputItem) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(inputItem), RecipeCategory.BUILDING_BLOCKS, outputItem, 0.1F, 200)
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
                int outputAmount = STONECUTTING_OUTPUTS.getOrDefault(variant, 1);

                if (variant != BlockFamilyExtended.Variant.BUTTON && variant != BlockFamilyExtended.Variant.DOOR
                        && variant != BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.PRESSURE_PLATE
                        && variant != BlockFamilyExtended.Variant.QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.STORAGE_BRICKS) {
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
                int outputAmount = STONECUTTING_OUTPUTS.getOrDefault(variant, 1);

                if (variant != BlockFamilyExtended.Variant.BUTTON && variant != BlockFamilyExtended.Variant.DOOR
                        && variant != BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.PRESSURE_PLATE
                        && variant != BlockFamilyExtended.Variant.QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.STORAGE_BRICKS) {
                    SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), RecipeCategory.BUILDING_BLOCKS, block, outputAmount)
                            .unlockedBy(getHasName(baseBlock), has(baseBlock))
                            .save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(block, inputItem) + "_stonecutting");
                }
            }
        });
    }

    protected static void costumeSmithing(Item outputItem, RecipeCategory category, Item templateItem, Item smithingItem, Item inputItem, RecipeOutput output) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(templateItem), Ingredient.of(smithingItem), Ingredient.of(inputItem), category, outputItem)
                .unlocks("has_smithing_item", has(smithingItem))
                .save(output, Marioverse.MOD_ID + ":" + getItemName(outputItem) + "_smithing");
    }
}
