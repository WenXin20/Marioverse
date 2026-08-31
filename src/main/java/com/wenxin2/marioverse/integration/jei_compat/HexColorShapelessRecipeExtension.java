package com.wenxin2.marioverse.integration.jei_compat;

import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.HexColorShapelessRecipe;
import com.wenxin2.marioverse.data.ItemColorIngredient;
import com.wenxin2.marioverse.data.TagColorIngredient;
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

public class HexColorShapelessRecipeExtension implements ICraftingCategoryExtension<HexColorShapelessRecipe> {
    private final ICraftingGridHelper craftingGridHelper;

    public HexColorShapelessRecipeExtension(IGuiHelper guiHelper) {
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public int getWidth(RecipeHolder<HexColorShapelessRecipe> recipeHolder) {
        return Math.min(3, recipeHolder.value().getSlots().size());
    }

    @Override
    public int getHeight(RecipeHolder<HexColorShapelessRecipe> recipeHolder) {
        int size = recipeHolder.value().getSlots().size();
        return (size + 2) / 3;
    }

    @Override
    public void setRecipe(RecipeHolder<HexColorShapelessRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        HexColorShapelessRecipe recipe = recipeHolder.value();
        List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> slots = recipe.getSlots();

        Map<Object, List<ItemStack>> colorCandidates = new IdentityHashMap<>();
        for (Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot : slots) {
            if (slot.left().isPresent()) {
                Either<TagColorIngredient, ItemColorIngredient> color = slot.left().orElseThrow();
                color.left().ifPresent(tag -> colorCandidates.computeIfAbsent(tag, t -> List.of(tag.toIngredient().getItems())));
                color.right().ifPresent(item -> colorCandidates.computeIfAbsent(item, i -> List.of(new ItemStack(item.item()))));
            }
        }

        List<List<ItemStack>> inputStacks = new ArrayList<>(slots.size());
        List<ItemStack> baseline = new ArrayList<>(slots.size());

        for (Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot : slots) {
            List<ItemStack> stacksForSlot;

            if (slot.left().isPresent()) {
                Either<TagColorIngredient, ItemColorIngredient> color = slot.left().orElseThrow();
                stacksForSlot = color.left().map(colorCandidates::get)
                        .or(() -> color.right().map(colorCandidates::get)).orElse(List.of());
            } else stacksForSlot = List.of(slot.right().orElseThrow().getItems());

            inputStacks.add(stacksForSlot);
            baseline.add(stacksForSlot.isEmpty() ? ItemStack.EMPTY : stacksForSlot.getFirst());
        }

        this.craftingGridHelper.createAndSetInputs(builder, VanillaTypes.ITEM_STACK, inputStacks,
                this.getWidth(recipeHolder), this.getHeight(recipeHolder));

        ItemStack baselineOutput = this.simulateOutput(recipe, baseline);
        this.craftingGridHelper.createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, List.of(baselineOutput));
    }

    @Override
    public void onDisplayedIngredientsUpdate(RecipeHolder<HexColorShapelessRecipe> recipeHolder,
                                             List<IRecipeSlotDrawable> recipeSlots, IFocusGroup focuses) {
        HexColorShapelessRecipe recipe = recipeHolder.value();
        List<Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient>> slots = recipe.getSlots();
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

    private ItemStack simulateOutput(HexColorShapelessRecipe recipe, List<ItemStack> flatInputs) {
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
