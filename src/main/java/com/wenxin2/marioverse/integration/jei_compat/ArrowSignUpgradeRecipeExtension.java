package com.wenxin2.marioverse.integration.jei_compat;

import com.wenxin2.marioverse.data.ArrowSignUpgradeRecipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

public class ArrowSignUpgradeRecipeExtension implements ICraftingCategoryExtension<ArrowSignUpgradeRecipe> {
    private final ICraftingGridHelper craftingGridHelper;

    public ArrowSignUpgradeRecipeExtension(IGuiHelper guiHelper) {
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public int getWidth(RecipeHolder<ArrowSignUpgradeRecipe> recipeHolder) {
        return recipeHolder.value().getWidth();
    }

    @Override
    public int getHeight(RecipeHolder<ArrowSignUpgradeRecipe> recipeHolder) {
        return recipeHolder.value().getHeight();
    }

    @Override
    public void setRecipe(RecipeHolder<ArrowSignUpgradeRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        ArrowSignUpgradeRecipe recipe = recipeHolder.value();
        List<Ingredient> slots = recipe.getSlots();
        int width = recipe.getWidth();
        int height = recipe.getHeight();

        List<List<ItemStack>> inputStacks = new ArrayList<>(slots.size());
        List<ItemStack> baseline = new ArrayList<>(slots.size());

        for (Ingredient slot : slots) {
            List<ItemStack> stacksForSlot = List.of(slot.getItems());
            inputStacks.add(stacksForSlot);
            baseline.add(stacksForSlot.isEmpty() ? ItemStack.EMPTY : stacksForSlot.getFirst());
        }

        this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputStacks, width, height);

        ItemStack baselineOutput = this.simulateOutput(recipe, baseline);
        this.craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(baselineOutput));
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<ArrowSignUpgradeRecipe> recipeHolder,
                                             List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        ArrowSignUpgradeRecipe recipe = recipeHolder.value();
        List<Ingredient> slots = recipe.getSlots();
        List<ItemStack> liveInputs = new ArrayList<>(Collections.nCopies(slots.size(), ItemStack.EMPTY));
        int viewIndex = 0;

        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == Ingredient.EMPTY)
                continue;

            if (viewIndex >= recipeSlots.size())
                break;

            int iFinal = i;
            recipeSlots.get(viewIndex).getDisplayedIngredient(VanillaTypes.ITEM_STACK)
                    .ifPresent(stack -> liveInputs.set(iFinal, stack));
            viewIndex++;
        }

        ItemStack output = simulateOutput(recipe, liveInputs);
        IRecipeSlotDrawable outputSlot = recipeSlots.getLast();

        outputSlot.clearDisplayOverrides();
        outputSlot.createDisplayOverrides().addItemStack(output);
    }

    private ItemStack simulateOutput(ArrowSignUpgradeRecipe recipe, List<ItemStack> flatInputs) {
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
