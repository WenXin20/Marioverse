package com.wenxin2.marioverse.integration.rei_compat;

import com.wenxin2.marioverse.data.WarpDoorRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class WarpDoorCraftingDisplay extends DefaultCraftingDisplay<WarpDoorRecipe> {
    public WarpDoorCraftingDisplay(RecipeHolder<WarpDoorRecipe> recipeHolder) {
        super(WarpDoorCraftingDisplay.createInputs(recipeHolder.value()),
                List.of(EntryIngredients.ofItemStacks(recipeHolder.value().getOutputStacks())),
                Optional.of(recipeHolder));
    }

    @Override
    public int getWidth() {
        WarpDoorRecipe recipe = this.getOptionalRecipe().orElseThrow().value();
        return recipe.isShaped() ? recipe.getWidth() : Math.min(3, recipe.getIngredients().size());
    }

    @Override
    public int getHeight() {
        WarpDoorRecipe recipe = this.getOptionalRecipe().orElseThrow().value();
        return recipe.isShaped() ? recipe.getHeight() : (recipe.getIngredients().size() + 2) / 3;
    }

    @Override
    public boolean isShapeless() {
        return !this.getOptionalRecipe().orElseThrow().value().isShaped();
    }

    private static List<EntryIngredient> createInputs(WarpDoorRecipe recipe) {
        List<Ingredient> slots = recipe.getIngredients();
        List<EntryIngredient> inputs = new ArrayList<>(slots.size());
        for (int i = 0; i < slots.size(); i++) {
            if (i == recipe.getSourceIndex())
                inputs.add(EntryIngredients.ofItemStacks(recipe.getSourceStacks()));
            else inputs.add(slots.get(i).isEmpty() ? EntryIngredient.empty()
                    : EntryIngredients.ofIngredient(slots.get(i)));
        }
        return inputs;
    }
}
