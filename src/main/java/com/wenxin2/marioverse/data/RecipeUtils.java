package com.wenxin2.marioverse.data;

import com.google.common.collect.ImmutableMap;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.datagen.HexColorRecipeBuilder;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class RecipeUtils extends RecipeProvider {
    private static final Set<Block> processedRecipes = new HashSet<>();
    private final Set<String> claimedStonecuttingIds = new HashSet<>();
    private final Set<List<Object>> processedStonecuttingPairs = new HashSet<>();

    public RecipeUtils(PackOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    public static final Map<BlockFamilyExtended.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS =
            ImmutableMap.<BlockFamilyExtended.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>>builder()
                    .put(BlockFamilyExtended.Variant.BUTTON, (outputItem, inputItem) -> buttonBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.BRICKS, (outputItem, inputItem) -> twoByTwoBuilder(4, outputItem, RecipeCategory.BUILDING_BLOCKS, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.BRIDGE, (outputItem, inputItem) -> bridgeBuilder(6, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.BRIDGE_STAIRS, (outputItem, inputItem) -> bridgeStairBuilder(6, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CHISELED, (outputItem, inputItem) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUSTOM_FENCE, (outputItem, inputItem) -> fenceBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUSTOM_FENCE_GATE, (outputItem, inputItem) -> fenceGateBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.CUT, (outputItem, inputItem) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.DOOR, (outputItem, inputItem) -> doorBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.FENCE, (outputItem, inputItem) -> fenceBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.FENCE_GATE, (outputItem, inputItem) -> fenceGateBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.HARD_BLOCK, (outputItem, inputItem) -> threeByThreeBuilder(9, outputItem, RecipeCategory.BUILDING_BLOCKS, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.PEDESTAL, (outputItem, inputItem) -> pedestalBuilder(5, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.POLISHED, (outputItem, inputItem) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.PRESSURE_PLATE, (outputItem, inputItem) -> pressurePlateBuilder(RecipeCategory.REDSTONE, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.QUESTION_BLOCK, (outputItem, inputItem) -> questionBlockBuilder(1, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.QUESTION_PANEL, (outputItem, inputItem) -> questionPanelBuilder(4, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.ROCKY, (outputItem, inputItem) -> rockyBuilder(1, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.SIGN, (outputItem, inputItem) -> signBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.SLAB, (outputItem, inputItem) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.STAIRS, (outputItem, inputItem) -> stairBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.STORAGE_BRICKS, (outputItem, inputItem) -> storageBrickBuilder(4, outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.TRAPDOOR, (outputItem, inputItem) -> trapdoorBuilder(outputItem, Ingredient.of(inputItem)))
                    .put(BlockFamilyExtended.Variant.WALL, (outputItem, inputItem) -> wallBuilder(RecipeCategory.DECORATIONS, outputItem, Ingredient.of(inputItem)))
                    .build();

    public static final Map<BlockFamilyExtended.Variant, BiFunction<ItemLike, TagKey<Item>, RecipeBuilder>> SHAPE_TAG_BUILDERS =
            ImmutableMap.<BlockFamilyExtended.Variant, BiFunction<ItemLike, TagKey<Item>, RecipeBuilder>>builder()
                    .put(BlockFamilyExtended.Variant.QUESTION_BLOCK_TAG, (outputItem, inputItemTag) -> questionBlockTagBuilder(1, outputItem, inputItemTag))
                    .build();

    public static final Map<BlockFamilyExtended.Variant, Integer> STONECUTTING_OUTPUTS = Map.ofEntries(
            Map.entry(BlockFamilyExtended.Variant.BRICKS, 1),
            Map.entry(BlockFamilyExtended.Variant.CHISELED, 1),
            Map.entry(BlockFamilyExtended.Variant.CUT, 1),
            Map.entry(BlockFamilyExtended.Variant.HARD_BLOCK, 1),
            Map.entry(BlockFamilyExtended.Variant.PEDESTAL, 1),
            Map.entry(BlockFamilyExtended.Variant.POLISHED, 1),
            Map.entry(BlockFamilyExtended.Variant.ROCKY, 1),
            Map.entry(BlockFamilyExtended.Variant.SLAB, 2),
            Map.entry(BlockFamilyExtended.Variant.SMASHABLE_BLOCKS, 1),
            Map.entry(BlockFamilyExtended.Variant.STAIRS, 1),
            Map.entry(BlockFamilyExtended.Variant.WALL, 1)
    );

    public static Stream<VanillaRecipeProvider.TrimTemplate> smithingTrims() {
        return Stream.of(ItemRegistry.LUIGI_ARMOR_TRIM_SMITHING_TEMPLATE.get(),
                        ItemRegistry.MARIO_ARMOR_TRIM_SMITHING_TEMPLATE.get())
                .map(item -> new VanillaRecipeProvider.TrimTemplate(item,
                        ResourceLocation.withDefaultNamespace(getItemName(item) + "_smithing_trim")));
    }

    public static RecipeBuilder threeByThreeBuilder(int outputAmt, ItemLike outputItem, RecipeCategory category, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("###")
                .pattern("###")
                .pattern("###");
    }

    public static RecipeBuilder twoByTwoBuilder(int outputAmt, ItemLike outputItem, RecipeCategory category, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(category, outputItem, outputAmt)
                .define('#', inputItem)
                .pattern("##")
                .pattern("##");
    }

    public static RecipeBuilder bridgeBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('#', inputItem)
                .define('S', Tags.Items.STRINGS)
                .pattern("#S#")
                .unlockedBy("has_string", has(Tags.Items.STRINGS))
                .group(Marioverse.MOD_ID + ":bridges");
    }

    public static RecipeBuilder bridgeStairBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .define('#', inputItem)
                .define('S', Tags.Items.STRINGS)
                .pattern("  #")
                .pattern(" S ")
                .pattern("#  ")
                .unlockedBy("has_string", has(Tags.Items.STRINGS))
                .group(Marioverse.MOD_ID + ":bridge_stairs");
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

    public static RecipeBuilder questionBlockTagBuilder(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItemTag)
                .requires(Tags.Items.CHESTS_WOODEN)
                .unlockedBy("has_chest", has(Tags.Items.CHESTS_WOODEN))
                .group(Marioverse.MOD_ID + ":question_blocks");
    }

    public static RecipeBuilder questionPanelBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItem)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_redstone_dust", has(Tags.Items.DUSTS_REDSTONE))
                .group(Marioverse.MOD_ID + ":question_panels");
    }

    public static RecipeBuilder rockyBuilder(int outputAmt, ItemLike outputItem, Ingredient inputItem) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .requires(inputItem)
                .requires(Blocks.POINTED_DRIPSTONE)
                .unlockedBy("has_dripstone", has(Blocks.POINTED_DRIPSTONE))
                .group(Marioverse.MOD_ID + ":rocky");
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

    public void twoByOneRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                               Object input1, Object input2, boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("A")
                .pattern("B")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'A', input1);
        defineIngredient(builder, 'B', input2);

        builder.unlockedBy(getUnlockName(input1), unlockCriterion(input1));
        builder.unlockedBy(getUnlockName(input2), unlockCriterion(input2));

        if (uniqueFileName && input1 instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike));
        else builder.save(output);
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

    public void plusRecipe(int outputAmt, String groupName, ItemLike outputItem, Object input1, Object input2, boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .pattern(" B ")
                .pattern("BCB")
                .pattern(" B ")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'B', input1);
        defineIngredient(builder, 'C', input2);

        builder.unlockedBy(getUnlockName(input1), unlockCriterion(input1));
        builder.unlockedBy(getUnlockName(input2), unlockCriterion(input2));

        if (uniqueFileName && input1 instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike));
        else builder.save(output);
    }

    public void abilityBlockRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                   Object trim, Object abilityItem, Object diamond, Object wool, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("ADA")
                .pattern("DTD")
                .pattern("WDW")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'A', abilityItem);
        defineIngredient(builder, 'D', diamond);
        defineIngredient(builder, 'T', trim);
        defineIngredient(builder, 'W', wool);

        builder.unlockedBy(getUnlockName(trim), unlockCriterion(trim));

        if (uniqueFileName && wool instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && abilityItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
    }

    public void bodiceRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                            Object colorItem, Object wool, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("C C")
                .pattern("CCC")
                .pattern("WCW")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);
        defineIngredient(builder, 'W', wool);

        builder.unlockedBy(getUnlockName(wool), unlockCriterion(wool));

        if (uniqueFileName && wool instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
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

    public void checkeredRecipe(int outputAmt, String groupName, ItemLike outputItem, Object input1, Object input2, boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .pattern("XY")
                .pattern("YX")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'X', input1);
        defineIngredient(builder, 'Y', input2);

        builder.unlockedBy(getUnlockName(input1), unlockCriterion(input1));
        builder.unlockedBy(getUnlockName(input2), unlockCriterion(input2));

        if (uniqueFileName && input1 instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike));
        else builder.save(output);
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

    public void christmasHatRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                             Object colorItem, Object wool, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("  W")
                .pattern("CC ")
                .pattern("WW ")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);
        defineIngredient(builder, 'W', wool);

        builder.unlockedBy(getUnlockName(wool), unlockCriterion(wool));

        if (uniqueFileName && wool instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
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

    public void crownRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                            Object colorItem, Object ingot, Object wool, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("ICI")
                .pattern("IWI")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);
        defineIngredient(builder, 'I', ingot);
        defineIngredient(builder, 'W', wool);

        builder.unlockedBy(getUnlockName(ingot), unlockCriterion(ingot));
        builder.unlockedBy(getUnlockName(wool), unlockCriterion(wool));

        if (uniqueFileName && colorItem instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
    }

    public void dressRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                            Object colorItem, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("CCC")
                .pattern("CCC")
                .pattern("C C")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);

        builder.unlockedBy(getUnlockName(colorItem), unlockCriterion(colorItem));

        if (uniqueFileName && colorItem instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
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

    public void hatRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                Object colorItem, Object wool, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("CWC")
                .pattern("C C")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);
        defineIngredient(builder, 'W', wool);

        builder.unlockedBy(getUnlockName(wool), unlockCriterion(wool));

        if (uniqueFileName && wool instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
    }

    public void heelsRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                              Object colorItem, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("C C")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);

        builder.unlockedBy(getUnlockName(colorItem), unlockCriterion(colorItem));

        if (uniqueFileName && colorItem instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
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

    public void mushroomTrampolineRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                         Object mushroom, Object dottedLineBlock, boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("MMM")
                .pattern("MDM")
                .pattern("MMM")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'M', mushroom);
        defineIngredient(builder, 'D', dottedLineBlock);

        builder.unlockedBy(getUnlockName(mushroom), unlockCriterion(mushroom));

        if (uniqueFileName && mushroom instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike) + "_" + outputAmt);
        else builder.save(output);
    }

    public void onOffBlockRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                 Object concrete, Object redstone, boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("CCC")
                .pattern("CRC")
                .pattern("CCC")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'C', concrete);
        defineIngredient(builder, 'R', redstone);

        builder.unlockedBy(getUnlockName(concrete), unlockCriterion(concrete));

        if (uniqueFileName && concrete instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike));
        else builder.save(output);
    }

    public void onOffSwitchRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                  Object redBlock, Object blueBlock, Object quartz, Object torch,
                                  boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("RRR")
                .pattern("ITI")
                .pattern("BBB")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'R', redBlock);
        defineIngredient(builder, 'B', blueBlock);
        defineIngredient(builder, 'I', quartz);
        defineIngredient(builder, 'T', torch);

        builder.unlockedBy(getUnlockName(quartz), unlockCriterion(quartz));

        if (uniqueFileName && redBlock instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike));
        else builder.save(output);
    }

    public void pantsRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                          Object colorItem, Object wool, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("WWW")
                .pattern("C C")
                .pattern("W W")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);
        defineIngredient(builder, 'W', wool);

        builder.unlockedBy(getUnlockName(wool), unlockCriterion(wool));

        if (uniqueFileName && wool instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
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

    public void picketFenceRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                                         Object slab, Object stick, boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("PSP")
                .pattern("PSP")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'P', slab);
        defineIngredient(builder, 'S', stick);

        builder.unlockedBy(getUnlockName(slab), unlockCriterion(slab));

        if (uniqueFileName && slab instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike) + "_" + outputAmt);
        else builder.save(output);
    }

    public void shirtRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                            Object colorItem, Object ingot, Object wool, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("C C")
                .pattern("IWI")
                .pattern("WWW")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);
        defineIngredient(builder, 'I', ingot);
        defineIngredient(builder, 'W', wool);

        builder.unlockedBy(getUnlockName(ingot), unlockCriterion(ingot));
        builder.unlockedBy(getUnlockName(wool), unlockCriterion(wool));

        if (uniqueFileName && wool instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName)
            builder.save(output, getConversionRecipeTagName(outputItem, (TagKey<?>) colorItem));
        else builder.save(output);
    }

    public void shoesRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                            Object colorItem, Object leather, boolean uniqueFileName, RecipeOutput output) {
        HexColorRecipeBuilder builder = HexColorRecipeBuilder
                .shaped(category, outputItem, outputAmt)
                .pattern("C C")
                .pattern("L L")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineColorIngredient(builder, 'C', colorItem);
        defineIngredient(builder, 'L', leather);

        builder.unlockedBy(getUnlockName(leather), unlockCriterion(leather));

        if (uniqueFileName && leather instanceof ItemLike itemLike)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeName(outputItem, itemLike)));
        else if (uniqueFileName && colorItem instanceof TagKey<?> tag)
            builder.save(output, ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, getConversionRecipeTagName(outputItem, tag)));
        else builder.save(output);
    }

    public void spikePanelRecipe(int outputAmt, String groupName, ItemLike outputItem, Object input1, Object input2, Object input3, boolean uniqueFileName, RecipeOutput output) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder
                .shaped(RecipeCategory.BUILDING_BLOCKS, outputItem, outputAmt)
                .pattern(" N ")
                .pattern("NIN")
                .pattern("PPP")
                .group(Marioverse.MOD_ID + ":" + groupName);

        defineIngredient(builder, 'N', input1);
        defineIngredient(builder, 'I', input2);
        defineIngredient(builder, 'P', input3);

        builder.unlockedBy(getUnlockName(input1), unlockCriterion(input1));
        builder.unlockedBy(getUnlockName(input2), unlockCriterion(input2));
        builder.unlockedBy(getUnlockName(input3), unlockCriterion(input3));

        if (uniqueFileName && input1 instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike));
        else builder.save(output);
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

    public void wrenchRecipe(int outputAmt, ItemLike outputItem, TagKey<Item> inputItemTag, RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputItem, outputAmt)
                .define('I', inputItemTag)
                .pattern("I I")
                .pattern(" I ")
                .pattern(" I ")
                .unlockedBy("has_iron_ingot", has(inputItemTag))
                .group(Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem))
                .save(output);
    }

    public void dyeItemRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                              Object input1, Object input2, boolean uniqueFileName, RecipeOutput output) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder
                .shapeless(category, outputItem, outputAmt)
                .group(Marioverse.MOD_ID + ":" + groupName);

        builder.unlockedBy(getUnlockName(input1), unlockCriterion(input1));
        builder.unlockedBy(getUnlockName(input2), unlockCriterion(input2));

        if (input1 instanceof ItemLike itemLike)
            builder.requires(itemLike);
        else if (input1 instanceof TagKey<?> itemLike && itemLike.registry() == Registries.ITEM) {
            TagKey<Item> tag = (TagKey<Item>) itemLike;
            builder.requires(tag);
        }

        if (input2 instanceof ItemLike itemLike)
            builder.requires(itemLike);
        else if (input2 instanceof TagKey<?> itemLike && itemLike.registry() == Registries.ITEM) {
            TagKey<Item> tag = (TagKey<Item>) itemLike;
            builder.requires(tag);
        }

        if (uniqueFileName && input2 instanceof ItemLike itemLike)
            builder.save(output, Marioverse.MOD_ID + ":" + getConversionRecipeName(outputItem, itemLike));
        else builder.save(output, Marioverse.MOD_ID + ":" + getItemName(outputItem) + "_from_dye");
    }

    public void oneToOneRecipe(int outputAmt, String groupName, ItemLike outputItem, RecipeCategory category,
                               Object input1, RecipeOutput output) {
        ShapelessRecipeBuilder builder = ShapelessRecipeBuilder
                .shapeless(category, outputItem, outputAmt)
                .group(Marioverse.MOD_ID + ":" + groupName);

        builder.unlockedBy(getUnlockName(input1), unlockCriterion(input1));

        if (input1 instanceof ItemLike itemLike)
            builder.requires(itemLike);
        else if (input1 instanceof TagKey<?> itemLike && itemLike.registry() == Registries.ITEM) {
            TagKey<Item> tag = (TagKey<Item>) itemLike;
            builder.requires(tag);
        }

        builder.save(output, Marioverse.MOD_ID + ":" + getItemName(outputItem) + "_from_" + getRecipeItemName(input1));
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

    protected void blastingRecipe(int cookingTime, float xp, ItemLike outputItem, RecipeCategory category, Object inputItem, RecipeOutput output) {
        Ingredient ingredient;
        if (inputItem instanceof ItemLike itemLike)
            ingredient = Ingredient.of(itemLike);
        else if (inputItem instanceof TagKey<?> tagKey
                && tagKey.registry().equals(Registries.ITEM))
            ingredient = Ingredient.of((TagKey<Item>) tagKey);
        else throw new IllegalArgumentException("Unsupported blasting input: " + inputItem);

        SimpleCookingRecipeBuilder builder = SimpleCookingRecipeBuilder
                .blasting(ingredient, category, outputItem, xp, cookingTime);
        builder.unlockedBy(getUnlockName(inputItem), unlockCriterion(inputItem));
        builder.save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_blasting");
    }

    protected void campfireCookingRecipe(int cookingTime, float xp, ItemLike outputItem, RecipeCategory category, Object inputItem, RecipeOutput output) {
        Ingredient ingredient;
        if (inputItem instanceof ItemLike itemLike)
            ingredient = Ingredient.of(itemLike);
        else if (inputItem instanceof TagKey<?> tagKey
                && tagKey.registry().equals(Registries.ITEM))
            ingredient = Ingredient.of((TagKey<Item>) tagKey);
        else throw new IllegalArgumentException("Unsupported campfire cooking input: " + inputItem);

        SimpleCookingRecipeBuilder builder = SimpleCookingRecipeBuilder
                .campfireCooking(ingredient, category, outputItem, xp, cookingTime);
        builder.unlockedBy(getUnlockName(inputItem), unlockCriterion(inputItem));
        builder.save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_campfire_cooking");
    }

    protected void smeltingRecipe(int cookingTime, float xp, ItemLike outputItem, RecipeCategory category, Object inputItem, RecipeOutput output) {
        Ingredient ingredient;
        if (inputItem instanceof ItemLike itemLike)
            ingredient = Ingredient.of(itemLike);
        else if (inputItem instanceof TagKey<?> tagKey
                && tagKey.registry().equals(Registries.ITEM))
            ingredient = Ingredient.of((TagKey<Item>) tagKey);
        else throw new IllegalArgumentException("Unsupported smelting input: " + inputItem);

        SimpleCookingRecipeBuilder builder = SimpleCookingRecipeBuilder
                .smelting(ingredient, category, outputItem, xp, cookingTime);
        builder.unlockedBy(getUnlockName(inputItem), unlockCriterion(inputItem));
        builder.save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_smelting");
    }

    protected void smokingRecipe(int cookingTime, float xp, ItemLike outputItem, RecipeCategory category, Object inputItem, RecipeOutput output) {
        Ingredient ingredient;
        if (inputItem instanceof ItemLike itemLike)
            ingredient = Ingredient.of(itemLike);
        else if (inputItem instanceof TagKey<?> tagKey
                && tagKey.registry().equals(Registries.ITEM))
            ingredient = Ingredient.of((TagKey<Item>) tagKey);
        else throw new IllegalArgumentException("Unsupported smoking input: " + inputItem);

        SimpleCookingRecipeBuilder builder = SimpleCookingRecipeBuilder
                .smoking(ingredient, category, outputItem, xp, cookingTime);
        builder.unlockedBy(getUnlockName(inputItem), unlockCriterion(inputItem));
        builder.save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_smoking");
    }

    protected static void smeltingResultFromBase(RecipeOutput output, ItemLike outputItem, ItemLike inputItem) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(inputItem), RecipeCategory.BUILDING_BLOCKS, outputItem, 0.1F, 200)
                .unlockedBy(getHasName(inputItem), has(inputItem))
                .save(output, Marioverse.MOD_ID + ":" + getSimpleRecipeName(outputItem) + "_smelting");
    }

    protected static void generateRecipes(RecipeOutput output, BlockFamilyExtended family, FeatureFlagSet set) {
        family.getVariants().forEach((variant, block) -> {
            if (block.requiredFeatures().isSubsetOf(set) && processedRecipes.add(block)) {
                BiFunction<ItemLike, ItemLike, RecipeBuilder> recipeFunction = SHAPE_BUILDERS.get(variant);
                ItemLike itemlike = getBaseBlock(family, variant);
                if (variant == BlockFamilyExtended.Variant.CHISELED
                        && !family.getVariants().containsKey(BlockFamilyExtended.Variant.SLAB))
                    itemlike = family.getBaseBlock();

                if (recipeFunction != null) {
                    RecipeBuilder recipeBuilder = recipeFunction.apply(block, itemlike);
                    family.getRecipeGroupPrefix().ifPresent(
                            string -> recipeBuilder.group(string +
                                    (variant == BlockFamilyExtended.Variant.CUT ? "" : "_" + variant.getRecipeGroup())));
                    ItemLike finalItemlike = itemlike;
                    recipeBuilder.unlockedBy(family.getRecipeUnlockedBy().orElseGet(() -> getHasName(finalItemlike)), has(itemlike));
                    recipeBuilder.save(output);
                }

                BiFunction<ItemLike, TagKey<Item>, RecipeBuilder> recipeTagFunction = SHAPE_TAG_BUILDERS.get(variant);
                TagKey<Item> itemTag = TagRegistry.POLISHED_CALCITE_ITEMS;

                if (recipeTagFunction != null) {
                    RecipeBuilder recipeBuilder = recipeTagFunction.apply(block, itemTag);
                    family.getRecipeGroupPrefix().ifPresent(
                            string -> recipeBuilder.group(string +
                                    (variant == BlockFamilyExtended.Variant.CUT ? "" : "_" + variant.getRecipeGroup())));
                    ItemLike finalItemlike = itemlike;
                    recipeBuilder.unlockedBy(family.getRecipeUnlockedBy().orElseGet(() -> getHasName(finalItemlike)), has(itemTag));
                    recipeBuilder.save(output);
                }

                if (variant == BlockFamilyExtended.Variant.CRACKED)
                    RecipeUtils.smeltingResultFromBase(output, block, itemlike);
            }
        });
    }

    protected static Block getBaseBlock(BlockFamilyExtended family, BlockFamilyExtended.Variant variant) {
        if (variant == BlockFamilyExtended.Variant.CHISELED) {
            if (!family.getVariants().containsKey(BlockFamilyExtended.Variant.SLAB))
                return null;
            else return family.get(BlockFamilyExtended.Variant.SLAB);
        } else return family.getBaseBlock();
    }

    protected void generateStonecuttingRecipes(RecipeOutput output, BlockFamilyExtended family, FeatureFlagSet featureFlags) {
        family.getVariants().forEach((variant, block) -> {
            if (block.requiredFeatures().isSubsetOf(featureFlags)) {
                ItemLike baseBlock = (variant == BlockFamilyExtended.Variant.CHISELED)
                        ? family.getBaseBlock() : getBaseBlock(family, variant);
                int outputAmount = STONECUTTING_OUTPUTS.getOrDefault(variant, 1);

                if (baseBlock != null && variant != BlockFamilyExtended.Variant.BRIDGE
                        && variant != BlockFamilyExtended.Variant.BUTTON
                        && variant != BlockFamilyExtended.Variant.DOOR
                        && variant != BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.PRESSURE_PLATE
                        && variant != BlockFamilyExtended.Variant.QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.QUESTION_BLOCK_TAG
                        && variant != BlockFamilyExtended.Variant.QUESTION_PANEL
                        && variant != BlockFamilyExtended.Variant.STORAGE_BRICKS) {
                    String id = getSimpleRecipeName(block) + "_stonecutting";
                    if (this.claimStonecutting(baseBlock, block, id)) {
                        SingleItemRecipeBuilder.stonecutting(Ingredient.of(baseBlock), RecipeCategory.BUILDING_BLOCKS, block, outputAmount)
                                .unlockedBy(getHasName(baseBlock), has(baseBlock))
                                .save(output, Marioverse.MOD_ID + ":" + id);
                    }
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

                if (baseBlock != null && variant != BlockFamilyExtended.Variant.BRIDGE
                        && variant != BlockFamilyExtended.Variant.BUTTON
                        && variant != BlockFamilyExtended.Variant.DOOR
                        && variant != BlockFamilyExtended.Variant.INVISIBLE_QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.PRESSURE_PLATE
                        && variant != BlockFamilyExtended.Variant.QUESTION_BLOCK
                        && variant != BlockFamilyExtended.Variant.QUESTION_BLOCK_TAG
                        && variant != BlockFamilyExtended.Variant.QUESTION_PANEL
                        && variant != BlockFamilyExtended.Variant.STORAGE_BRICKS) {
                    String id = getConversionRecipeName(block, inputItem) + "_stonecutting";
                    if (this.claimStonecutting(inputItem, block, id)) {
                        SingleItemRecipeBuilder.stonecutting(Ingredient.of(inputItem), RecipeCategory.BUILDING_BLOCKS, block, outputAmount)
                                .unlockedBy(getHasName(baseBlock), has(baseBlock))
                                .save(output, Marioverse.MOD_ID + ":" + id);
                    }
                }
            }
        });
    }

    private boolean claimStonecutting(ItemLike input, ItemLike output, String id) {
        if (!this.processedStonecuttingPairs.add(List.of(input, output)))
            return false;
        return this.claimedStonecuttingIds.add(id);
    }

    @SuppressWarnings("unchecked")
    private void defineColorIngredient(HexColorRecipeBuilder builder, char symbol, Object ingredient) {
        if (ingredient instanceof Item item)
            builder.defineColorItem(symbol, item);
        else if (ingredient instanceof TagKey<?> tag && tag.registry() == Registries.ITEM)
            builder.defineColorTag(symbol, (TagKey<Item>) tag);
        else throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient);
    }

    @SuppressWarnings("unchecked")
    private void defineIngredient(HexColorRecipeBuilder builder, char symbol, Object ingredient) {
        if (ingredient instanceof ItemLike item)
            builder.define(symbol, item);
        else if (ingredient instanceof TagKey<?> tag && tag.registry() == Registries.ITEM)
            builder.define(symbol, (TagKey<Item>) tag);
        else throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient);
    }

    @SuppressWarnings("unchecked")
    private void defineIngredient(ShapedRecipeBuilder builder, char symbol, Object ingredient) {
        if (ingredient instanceof ItemLike item)
            builder.define(symbol, item);
        else if (ingredient instanceof TagKey<?> tag && tag.registry() == Registries.ITEM)
            builder.define(symbol, (TagKey<Item>) tag);
        else throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient);
    }

    private String getUnlockName(Object ingredient) {
        if (ingredient instanceof ItemLike item)
            return getHasName(item);
        else if (ingredient instanceof TagKey<?> tag)
            return "has_" + tag.location().getPath();
        throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient);
    }

    private String getRecipeItemName(Object ingredient) {
        if (ingredient instanceof ItemLike item)
            return getItemName(item);
        else if (ingredient instanceof TagKey<?> tag)
            return tag.location().getPath();
        throw new IllegalArgumentException("Unsupported ingredient type: " + ingredient);
    }

    @SuppressWarnings("unchecked")
    private Criterion<?> unlockCriterion(Object ingredient) {
        if (ingredient instanceof ItemLike item)
            return RecipeProvider.has(item);
        else if (ingredient instanceof TagKey<?> raw && raw.registry() == Registries.ITEM) {
            TagKey<Item> tag = (TagKey<Item>) raw;
            return RecipeProvider.has(tag);
        }
        throw new IllegalArgumentException("Unsupported ingredient: " + ingredient);
    }

    @NotNull
    protected String getConversionRecipeTagName(ItemLike outputItem, TagKey<?> tag) {
        return getItemName(outputItem) + "_from_" + this.getRecipeItemName(tag);
    }
}
