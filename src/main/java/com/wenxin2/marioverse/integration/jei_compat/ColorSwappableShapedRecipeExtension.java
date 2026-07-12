package com.wenxin2.marioverse.integration.jei_compat;

import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.ColorSwappableShapedRecipe;
import com.wenxin2.marioverse.data.TagColorIngredient;
import java.util.IdentityHashMap;
import java.util.Map;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class ColorSwappableShapedRecipeExtension implements ICraftingCategoryExtension<ColorSwappableShapedRecipe> {
    private final ICraftingGridHelper craftingGridHelper;

    public ColorSwappableShapedRecipeExtension(IGuiHelper guiHelper) {
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public int getWidth(RecipeHolder<ColorSwappableShapedRecipe> recipeHolder) {
        return recipeHolder.value().getWidth();
    }

    @Override
    public int getHeight(RecipeHolder<ColorSwappableShapedRecipe> recipeHolder) {
        return recipeHolder.value().getHeight();
    }

    @Override
    public void setRecipe(RecipeHolder<ColorSwappableShapedRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        ColorSwappableShapedRecipe recipe = recipeHolder.value();
        List<Either<TagColorIngredient, Ingredient>> slots = recipe.getSlots();
        int width = recipe.getWidth();
        int height = recipe.getHeight();

        // Resolve the full cycle of candidate items per unique color
        // ingredient (keyed by identity, since e.g. both corners in "C C"
        // share the same TagColorIngredient instance).
        Map<TagColorIngredient, List<ItemStack>> colorCandidates = new IdentityHashMap<>();
        for (Either<TagColorIngredient, Ingredient> slot : slots) {
            slot.ifLeft(ci -> colorCandidates.computeIfAbsent(ci,
                    key -> List.of(key.toIngredient().getItems())));
        }

        // Per-slot display lists: color slots cycle every item in their tag,
        // fixed slots just show whatever that Ingredient matches.
        List<List<ItemStack>> inputStacks = new ArrayList<>(slots.size());
        List<ItemStack> baseline = new ArrayList<>(slots.size());

        for (Either<TagColorIngredient, Ingredient> slot : slots) {
            List<ItemStack> stacksForSlot = slot.map(
                    colorCandidates::get,
                    ingredient -> List.of(ingredient.getItems()));
            inputStacks.add(stacksForSlot);
            baseline.add(stacksForSlot.isEmpty() ? ItemStack.EMPTY : stacksForSlot.getFirst());
        }

        this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputStacks, width, height);

        // Build one output frame per candidate in the color cycle, so the
        // output tint advances in step with whichever wool color is shown.
        int cycleLength = colorCandidates.values().stream().mapToInt(List::size).max().orElse(1);
        List<ItemStack> outputFrames = new ArrayList<>(cycleLength);

        for (int frame = 0; frame < cycleLength; frame++) {
            List<ItemStack> frameInputs = new ArrayList<>(baseline);

            for (int i = 0; i < slots.size(); i++) {
                int index = i;
                int finalFrame = frame;
                slots.get(i).ifLeft(ci -> {
                    List<ItemStack> candidates = colorCandidates.get(ci);
                    if (!candidates.isEmpty())
                        frameInputs.set(index, candidates.get(finalFrame % candidates.size()));
                });
            }
            outputFrames.add(this.simulateOutput(recipe, frameInputs));
        }
        this.craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, outputFrames);
    }

    private ItemStack simulateOutput(ColorSwappableShapedRecipe recipe, List<ItemStack> flatInputs) {
        var player = Minecraft.getInstance().player;
        HolderLookup.Provider registries = player != null ? player.registryAccess() : RegistryAccess.EMPTY;

        try {
            CraftingInput craftingInput = CraftingInput.of(recipe.getWidth(), recipe.getHeight(), flatInputs);
            ItemStack assembled = recipe.assemble(craftingInput, registries);
            return assembled.isEmpty() ? recipe.getResultItem(registries) : assembled;
        } catch (Exception e) {
            return recipe.getResultItem(registries);
        }
    }
}