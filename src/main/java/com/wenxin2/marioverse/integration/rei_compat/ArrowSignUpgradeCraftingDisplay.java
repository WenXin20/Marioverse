package com.wenxin2.marioverse.integration.rei_compat;

import com.wenxin2.marioverse.data.ArrowSignUpgradeRecipe;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * REI's own DefaultCraftingDisplay.of(...) generic fallback doesn't know this recipe's actual
 * width/height (it's not a real vanilla ShapedRecipe), so without this it rendered as a flat/shapeless
 * list instead of a 2x2 grid. Overriding getWidth()/getHeight() (as ArrowColorCraftingDisplay does)
 * fixes that, and populating real outputs here (instead of the generic fallback's handling) is what
 * gives REI a correct result count to show.
 */
public class ArrowSignUpgradeCraftingDisplay extends DefaultCraftingDisplay<ArrowSignUpgradeRecipe> {
    public ArrowSignUpgradeCraftingDisplay(RecipeHolder<ArrowSignUpgradeRecipe> recipeHolder) {
        super(createInputs(recipeHolder.value()), createOutputs(recipeHolder.value()), Optional.of(recipeHolder));
    }

    private static List<EntryIngredient> createInputs(ArrowSignUpgradeRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        for (Ingredient slot : recipe.getSlots()) {
            inputs.add(slot == Ingredient.EMPTY ? EntryIngredient.empty()
                    : EntryIngredients.ofIngredients(List.of(slot)).getFirst());
        }
        return inputs;
    }

    private static List<EntryIngredient> createOutputs(ArrowSignUpgradeRecipe recipe) {
        HolderLookup.Provider registries = RegistryAccess.EMPTY;
        List<ItemStack> baseline = new ArrayList<>(recipe.getSlots().size());

        for (Ingredient slot : recipe.getSlots()) {
            if (slot == Ingredient.EMPTY)
                baseline.add(ItemStack.EMPTY);
            else {
                ItemStack[] items = slot.getItems();
                baseline.add(items.length > 0 ? items[0] : ItemStack.EMPTY);
            }
        }

        ItemStack output = recipe.assemble(CraftingInput.of(recipe.getWidth(), recipe.getHeight(), baseline), registries);
        return List.of(EntryIngredients.ofItemStacks(List.of(output)));
    }

    @Override
    public int getWidth() {
        return this.getOptionalRecipe().orElseThrow().value().getWidth();
    }

    @Override
    public int getHeight() {
        return this.getOptionalRecipe().orElseThrow().value().getHeight();
    }
}
