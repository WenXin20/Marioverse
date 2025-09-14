package com.wenxin2.marioverse.datagen;

import com.wenxin2.marioverse.data.BlockFamilyExtended;
import com.wenxin2.marioverse.data.RecipeUtils;
import com.wenxin2.marioverse.registries.BlockFamilyRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
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

public class RecipeGen extends RecipeUtils {
    public RecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    protected void generateForEnabledBlockFamilies(RecipeOutput output, FeatureFlagSet set) {
        BlockFamilyRegistry.getAllExtendedFamilies().filter(BlockFamilyExtended::shouldGenerateRecipe)
                .forEach(recipes -> generateRecipes(output, recipes, set));
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        generateForEnabledBlockFamilies(output, FeatureFlagSet.of(FeatureFlags.VANILLA));
        smeltingRecipe(200, 0.1F, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.DEEP_FUNGAL_STONE, output);
        smeltingRecipe(200, 0.1F, BlockRegistry.POLISHED_FUNGAL_STONE, RecipeCategory.BUILDING_BLOCKS, BlockRegistry.FUNGAL_STONE, output);
        waxRecipes(output, FeatureFlagSet.of(FeatureFlags.VANILLA));

        smithingTemplateRecipe(2, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE.get(), ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE.get(), Blocks.RED_WOOL, Tags.Items.LEATHERS, output);
        smithingTemplateRecipe(2, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE.get(), ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE.get(), Blocks.LIME_WOOL, Tags.Items.LEATHERS, output);
        smithingTemplateRecipe(2, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE.get(), ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE.get(), Blocks.PINK_WOOL, Tags.Items.LEATHERS, output);
        smithingTemplateRecipe(2, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), Blocks.MAGMA_BLOCK, Tags.Items.LEATHERS, output);
        smithingTemplateRecipe(2, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), Blocks.ICE, Tags.Items.LEATHERS, output);

        costumeSmithing(ItemRegistry.MARIO_HAT.get(), RecipeCategory.COMBAT, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_HELMET, Blocks.RED_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.MARIO_SHIRT.get(), RecipeCategory.COMBAT, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_CHESTPLATE, Blocks.RED_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.MARIO_PANTS.get(), RecipeCategory.COMBAT, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_LEGGINGS, Blocks.BLUE_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.MARIO_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.MARIO_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_BOOTS, Blocks.BROWN_WOOL.asItem(), output);

        costumeSmithing(ItemRegistry.MARIO_FIRE_HAT.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_HATS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.MARIO_FIRE_SHIRT.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_SHIRTS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.MARIO_FIRE_PANTS.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_PANTS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.MARIO_FIRE_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_SHOES, ItemRegistry.FIRE_FLOWER.get(), output);

        costumeSmithing(ItemRegistry.MARIO_ICE_HAT.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_HATS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.MARIO_ICE_SHIRT.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_SHIRTS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.MARIO_ICE_PANTS.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_PANTS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.MARIO_ICE_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.MARIO_SHOES, ItemRegistry.ICE_FLOWER.get(), output);

        costumeSmithing(ItemRegistry.LUIGI_HAT.get(), RecipeCategory.COMBAT, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_HELMET, Blocks.LIME_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.LUIGI_SHIRT.get(), RecipeCategory.COMBAT, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_CHESTPLATE, Blocks.LIME_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.LUIGI_PANTS.get(), RecipeCategory.COMBAT, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_LEGGINGS, Blocks.BLUE_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.LUIGI_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.LUIGI_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_BOOTS, Blocks.BROWN_WOOL.asItem(), output);

        costumeSmithing(ItemRegistry.LUIGI_FIRE_HAT.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_HATS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.LUIGI_FIRE_SHIRT.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_SHIRTS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.LUIGI_FIRE_PANTS.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_PANTS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.LUIGI_FIRE_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_SHOES, ItemRegistry.FIRE_FLOWER.get(), output);

        costumeSmithing(ItemRegistry.LUIGI_ICE_HAT.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_HATS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.LUIGI_ICE_SHIRT.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_SHIRTS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.LUIGI_ICE_PANTS.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_PANTS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.LUIGI_ICE_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.LUIGI_SHOES, ItemRegistry.ICE_FLOWER.get(), output);

        costumeSmithing(ItemRegistry.PEACH_CROWN.get(), RecipeCategory.COMBAT, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE.get(), Items.GOLDEN_HELMET, Tags.Items.GEMS, output);
        costumeSmithing(ItemRegistry.PEACH_BODICE.get(), RecipeCategory.COMBAT, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_CHESTPLATE, Blocks.PINK_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.PEACH_DRESS.get(), RecipeCategory.COMBAT, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_LEGGINGS, Blocks.PINK_WOOL.asItem(), output);
        costumeSmithing(ItemRegistry.PEACH_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.PEACH_COSTUME_SMITHING_TEMPLATE.get(), Items.LEATHER_BOOTS, Blocks.PINK_WOOL .asItem(), output);

        costumeSmithing(ItemRegistry.PEACH_FIRE_BODICE.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.PEACH_SHIRTS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.PEACH_FIRE_DRESS.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.PEACH_PANTS, ItemRegistry.FIRE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.PEACH_FIRE_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.FIRE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.PEACH_SHOES, ItemRegistry.FIRE_FLOWER.get(), output);

        costumeSmithing(ItemRegistry.PEACH_ICE_BODICE.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.PEACH_SHIRTS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.PEACH_ICE_DRESS.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.PEACH_PANTS, ItemRegistry.ICE_FLOWER.get(), output);
        costumeSmithing(ItemRegistry.PEACH_ICE_SHOES.get(), RecipeCategory.COMBAT, ItemRegistry.ICE_COSTUME_SMITHING_TEMPLATE.get(), TagRegistry.PEACH_SHOES, ItemRegistry.ICE_FLOWER.get(), output);

        classicCheckpointFlagRecipe(1, BlockRegistry.CLASSIC_CHECKPOINT_FLAG, Tags.Items.INGOTS_GOLD, ItemRegistry.SUPER_MUSHROOM, Items.WHITE_WOOL, Blocks.SMOOTH_STONE_SLAB, output);
        classicGoalPoleRecipe(4, BlockRegistry.CLASSIC_GOAL_POLE, Tags.Items.INGOTS_GOLD, Tags.Items.DYES_LIME, Items.WHITE_WOOL, Items.BAMBOO, output);
        coinRecipe(4, BlockRegistry.COIN, Tags.Items.INGOTS_GOLD, Tags.Items.NUGGETS_GOLD, output);
        plusRecipe(1, "brick_pedestals", BlockRegistry.RED_NETHER_BRICK_PEDESTAL, Items.NETHER_WART, BlockRegistry.NETHER_BRICK_PEDESTAL, output);
        plusRecipe(1, "invisible_question_blocks", BlockRegistry.INVISIBLE_RED_NETHER_QUESTION_BRICKS, Items.NETHER_WART, BlockRegistry.INVISIBLE_NETHER_QUESTION_BRICKS, output);
        plusRecipe(1, "question_blocks", BlockRegistry.RED_NETHER_QUESTION_BRICKS, Items.NETHER_WART, BlockRegistry.NETHER_QUESTION_BRICKS, output);
        plusRecipe(1, "smashable_blocks", BlockRegistry.SMASHABLE_RED_NETHER_BRICKS, Items.NETHER_WART, BlockRegistry.SMASHABLE_NETHER_BRICKS, output);
        plusRecipe(1, "storage_bricks", BlockRegistry.STORAGE_RED_NETHER_BRICKS, Items.NETHER_WART, BlockRegistry.STORAGE_NETHER_BRICKS, output);
        plusRecipe(1, "spikes", BlockRegistry.IRON_SPIKE, Tags.Items.NUGGETS_IRON, Tags.Items.INGOTS_IRON, output);
        threeByThreePacker(output, RecipeCategory.MISC, BlockRegistry.STAR_COIN, BlockRegistry.COIN);
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
        warpDisruptorRecipe(1, ItemRegistry.WARP_DISRUPTOR, Items.ENDER_EYE, Tags.Items.INGOTS_GOLD, Tags.Items.RODS_WOODEN, output);
        warpPipeRecipe(4, BlockRegistry.CLEAR_WARP_PIPE, Tags.Items.INGOTS_COPPER, Tags.Items.GLASS_BLOCKS_COLORLESS, Tags.Items.GEMS_DIAMOND, Tags.Items.ENDER_PEARLS, output);
        wrenchRecipe(1, ItemRegistry.WRENCH, Tags.Items.INGOTS_IRON, output);

        waxedBlockRecipe(1, BlockRegistry.INVISIBLE_WAXED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.INVISIBLE_WAXED_EXPOSED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_EXPOSED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.INVISIBLE_WAXED_OXIDIZED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_OXIDIZED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
        waxedBlockRecipe(1, BlockRegistry.INVISIBLE_WAXED_WEATHERED_COPPER_QUESTION_BLOCK, BlockRegistry.INVISIBLE_WEATHERED_COPPER_QUESTION_BLOCK, Items.HONEYCOMB, output);
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

        generateStonecuttingRecipes(output, BlockFamilyRegistry.AMETHYST, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.AMETHYST_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.POLISHED_AMETHYST, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.AMETHYST_BRICKS, BlockRegistry.POLISHED_AMETHYST, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.AMETHYST_BRICKS, Blocks.AMETHYST_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.POLISHED_AMETHYST, Blocks.AMETHYST_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.CALCITE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.DEEP_FUNGAL_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.DEEP_FUNGAL_BRICKS, BlockRegistry.DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.FUNGAL_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.FUNGAL_BRICKS, BlockRegistry.FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.POLISHED_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.POLISHED_FUNGAL_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.POLISHED_FUNGAL_BRICKS, BlockRegistry.POLISHED_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.POLISHED_DEEP_FUNGAL_BRICKS, BlockRegistry.POLISHED_DEEP_FUNGAL_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.CUT_SANDSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.CUT_SANDSTONE, Blocks.SANDSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.SANDSTONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.SANDSTONE_BRICKS, Blocks.SANDSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.CUT_RED_SANDSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.CUT_RED_SANDSTONE, Blocks.RED_SANDSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingRecipes(output, BlockFamilyRegistry.RED_SANDSTONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.RED_SANDSTONE_BRICKS, Blocks.RED_SANDSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.BLACKSTONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.BLACKSTONE_BRICKS, Blocks.BLACKSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.BLACKSTONE_BRICKS, Blocks.POLISHED_BLACKSTONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.DARK_PRISMARINE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.DEEPSLATE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.DEEPSLATE_BRICKS, Blocks.COBBLED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.DEEPSLATE_BRICKS, Blocks.POLISHED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.DEEPSLATE_TILES, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.DEEPSLATE_TILES, Blocks.COBBLED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.DEEPSLATE_TILES, Blocks.DEEPSLATE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.DEEPSLATE_TILES, Blocks.POLISHED_DEEPSLATE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.END_STONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.END_STONE_BRICKS, Blocks.END_STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.MOSSY_STONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.MUD_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.MUD_BRICKS, Blocks.PACKED_MUD, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.NETHER_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.PRISMARINE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.PURPUR_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.QUARTZ_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.QUARTZ_BRICKS, Blocks.QUARTZ_BLOCK, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.RED_NETHER_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.STONE_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.STONE_BRICKS, Blocks.STONE, FeatureFlagSet.of(FeatureFlags.VANILLA));

        generateStonecuttingRecipes(output, BlockFamilyRegistry.TUFF_BRICKS, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.TUFF_BRICKS, Blocks.TUFF, FeatureFlagSet.of(FeatureFlags.VANILLA));
        generateStonecuttingFromBaseRecipes(output, BlockFamilyRegistry.TUFF_BRICKS, Blocks.POLISHED_TUFF, FeatureFlagSet.of(FeatureFlags.VANILLA));


        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CALCITE_BRICKS.entrySet()) {
            DyeColor dyeColor = entry.getKey();
            TagKey<Item> dyeItemTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dyes/" + dyeColor.getName()));
            Item woolItem = BuiltInRegistries.ITEM.stream().filter(item -> {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                return itemId != null && itemId.getPath().endsWith("_wool") && itemId.getPath().startsWith(dyeColor.getName());
            }).findFirst().orElse(Items.WHITE_WOOL);

            dyeItemTagRecipe(1, "calcite_bricks_from_dye", entry.getValue(), RecipeCategory.BUILDING_BLOCKS, dyeItemTag, TagRegistry.CALCITE_BRICK_ITEMS, output);
        }

        for (Map.Entry<DyeColor, DeferredBlock<Block>> entry : BlockRegistry.CHECKPOINT_FLAGS.entrySet()) {
            DyeColor dyeColor = entry.getKey();
            TagKey<Item> dyeItemTag = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dyes/" + dyeColor.getName()));
            Item woolItem = BuiltInRegistries.ITEM.stream().filter(item -> {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                return itemId != null && itemId.getPath().endsWith("_wool") && itemId.getPath().startsWith(dyeColor.getName());
            }).findFirst().orElse(Items.WHITE_WOOL);

            checkpointFlagRecipe(1, entry.getValue(), Tags.Items.INGOTS_GOLD, Tags.Items.INGOTS_IRON, woolItem, Blocks.SMOOTH_STONE_SLAB, output);
            dyeItemTagRecipe(1, "checkpoint_flags_from_dye", entry.getValue(), RecipeCategory.BUILDING_BLOCKS, dyeItemTag, TagRegistry.DYEABLE_CHECKPOINT_FLAG_ITEMS, output);
        }

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