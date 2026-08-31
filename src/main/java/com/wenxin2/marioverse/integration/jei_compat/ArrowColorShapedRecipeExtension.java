package com.wenxin2.marioverse.integration.jei_compat;

import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.ArrowColorShapedRecipe;
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

public class ArrowColorShapedRecipeExtension implements ICraftingCategoryExtension<ArrowColorShapedRecipe> {
    private final ICraftingGridHelper craftingGridHelper;

    public ArrowColorShapedRecipeExtension(IGuiHelper guiHelper) {
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public int getWidth(RecipeHolder<ArrowColorShapedRecipe> recipeHolder) {
        return recipeHolder.value().getWidth();
    }

    @Override
    public int getHeight(RecipeHolder<ArrowColorShapedRecipe> recipeHolder) {
        return recipeHolder.value().getHeight();
    }

    @Override
    public void setRecipe(RecipeHolder<ArrowColorShapedRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        ArrowColorShapedRecipe recipe = recipeHolder.value();
        List<Either<DyeColorIngredient, Ingredient>> slots = recipe.getSlots();
        int width = recipe.getWidth();
        int height = recipe.getHeight();

        Map<Object, List<ItemStack>> colorCandidates = new IdentityHashMap<>();
        for (Either<DyeColorIngredient, Ingredient> slot : slots) {
            slot.ifLeft(dye -> colorCandidates.computeIfAbsent(dye, t -> List.of(dye.toIngredient().getItems())));
        }

        List<List<ItemStack>> inputStacks = new ArrayList<>(slots.size());
        List<ItemStack> baseline = new ArrayList<>(slots.size());

        for (Either<DyeColorIngredient, Ingredient> slot : slots) {
            List<ItemStack> stacksForSlot = slot.left().map(colorCandidates::get)
                    .orElseGet(() -> List.of(slot.right().orElseThrow().getItems()));

            inputStacks.add(stacksForSlot);
            baseline.add(stacksForSlot.isEmpty() ? ItemStack.EMPTY : stacksForSlot.getFirst());
        }

        this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputStacks, width, height);

        ItemStack baselineOutput = this.simulateOutput(recipe, baseline);
        this.craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(baselineOutput));
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<ArrowColorShapedRecipe> recipeHolder,
                                             List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        ArrowColorShapedRecipe recipe = recipeHolder.value();
        List<Either<DyeColorIngredient, Ingredient>> slots = recipe.getSlots();
        List<ItemStack> liveInputs = new ArrayList<>(Collections.nCopies(slots.size(), ItemStack.EMPTY));
        int viewIndex = 0;

        for (int i = 0; i < slots.size(); i++) {
            Either<DyeColorIngredient, Ingredient> slot = slots.get(i);
            boolean empty = slot.right().map(ingredient -> ingredient == Ingredient.EMPTY).orElse(false);

            if (empty)
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

    private ItemStack simulateOutput(ArrowColorShapedRecipe recipe, List<ItemStack> flatInputs) {
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
