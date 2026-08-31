package com.wenxin2.marioverse.integration.jei_compat;

import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.ArrowColorShapelessRecipe;
import com.wenxin2.marioverse.data.DyeColorIngredient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
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

public class ArrowColorShapelessRecipeExtension implements ICraftingCategoryExtension<ArrowColorShapelessRecipe> {
    private final ICraftingGridHelper craftingGridHelper;

    public ArrowColorShapelessRecipeExtension(IGuiHelper guiHelper) {
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public int getWidth(RecipeHolder<ArrowColorShapelessRecipe> recipeHolder) {
        return Math.min(3, recipeHolder.value().getSlots().size());
    }

    @Override
    public int getHeight(RecipeHolder<ArrowColorShapelessRecipe> recipeHolder) {
        int size = recipeHolder.value().getSlots().size();
        return (size + 2) / 3;
    }

    @Override
    public void setRecipe(RecipeHolder<ArrowColorShapelessRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        ArrowColorShapelessRecipe recipe = recipeHolder.value();
        List<Either<DyeColorIngredient, Ingredient>> slots = recipe.getSlots();

        Map<Object, List<ItemStack>> colorCandidates = new IdentityHashMap<>();
        for (Either<DyeColorIngredient, Ingredient> slot : slots)
            slot.ifLeft(dye -> colorCandidates.computeIfAbsent(dye, t -> List.of(dye.toIngredient().getItems())));

        List<List<ItemStack>> inputStacks = new ArrayList<>(slots.size());
        List<ItemStack> baseline = new ArrayList<>(slots.size());

        for (Either<DyeColorIngredient, Ingredient> slot : slots) {
            List<ItemStack> stacksForSlot = slot.left().map(colorCandidates::get)
                    .orElseGet(() -> List.of(slot.right().orElseThrow().getItems()));

            inputStacks.add(stacksForSlot);
            baseline.add(stacksForSlot.isEmpty() ? ItemStack.EMPTY : stacksForSlot.getFirst());
        }

        this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputStacks,
                this.getWidth(recipeHolder), this.getHeight(recipeHolder));

        ItemStack baselineOutput = this.simulateOutput(recipe, baseline);
        this.craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(baselineOutput));
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<ArrowColorShapelessRecipe> recipeHolder,
                                             List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        ArrowColorShapelessRecipe recipe = recipeHolder.value();
        List<Either<DyeColorIngredient, Ingredient>> slots = recipe.getSlots();
        List<ItemStack> liveInputs = new ArrayList<>(Collections.nCopies(slots.size(), ItemStack.EMPTY));

        for (int i = 0; i < slots.size() && i < recipeSlots.size(); i++) {
            int iFinal = i;
            recipeSlots.get(i).getDisplayedIngredient(VanillaTypes.ITEM_STACK)
                    .ifPresent(stack -> liveInputs.set(iFinal, stack));
        }

        ItemStack output = simulateOutput(recipe, liveInputs);
        IRecipeSlotDrawable outputSlot = recipeSlots.getLast();

        outputSlot.clearDisplayOverrides();
        outputSlot.createDisplayOverrides().addItemStack(output);
    }

    private ItemStack simulateOutput(ArrowColorShapelessRecipe recipe, List<ItemStack> flatInputs) {
        var player = Minecraft.getInstance().player;
        HolderLookup.Provider registries = player != null ? player.registryAccess() : RegistryAccess.EMPTY;

        try {
            CraftingInput craftingInput = CraftingInput.of(flatInputs.size(), 1, flatInputs);
            ItemStack assembled = recipe.assemble(craftingInput, registries);
            return assembled.isEmpty() ? recipe.getResultItem(registries) : assembled;
        } catch (Exception e) {
            return recipe.getResultItem(registries);
        }
    }
}
