package com.wenxin2.marioverse.integration.rei_compat;

import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.ColorSwappableShapedRecipe;
import com.wenxin2.marioverse.data.ItemColorIngredient;
import com.wenxin2.marioverse.data.TagColorIngredient;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
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

public class ColorSwappableCraftingDisplay extends DefaultCraftingDisplay<ColorSwappableShapedRecipe> {
    public ColorSwappableCraftingDisplay(RecipeHolder<ColorSwappableShapedRecipe> recipeHolder) {
        super(createInputs(recipeHolder.value()), createOutputs(recipeHolder.value()), Optional.of(recipeHolder));
    }

    private static List<EntryIngredient> createInputs(ColorSwappableShapedRecipe recipe) {
        List<EntryIngredient> inputs = new ArrayList<>();
        Map<Object, EntryIngredient> colorEntryCache = new IdentityHashMap<>();

        for (Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot : recipe.getSlots()) {
            if (slot.left().isPresent()) {
                Either<TagColorIngredient, ItemColorIngredient> color = slot.left().orElseThrow();

                if (color.left().isPresent()) {
                    TagColorIngredient tag = color.left().orElseThrow();
                    inputs.add(colorEntryCache.computeIfAbsent(tag,
                            key -> EntryIngredients.ofIngredients(List.of(tag.toIngredient())).getFirst()));
                } else {
                    ItemColorIngredient item = color.right().orElseThrow();
                    inputs.add(colorEntryCache.computeIfAbsent(item,
                            key -> EntryIngredients.of(new ItemStack(item.item()))));
                }
            } else {
                Ingredient ingredient = slot.right().orElseThrow();
                inputs.add(ingredient == Ingredient.EMPTY ? EntryIngredient.empty()
                        : EntryIngredients.ofIngredients(List.of(ingredient)).getFirst());
            }
        }
        return inputs;
    }

    private static List<EntryIngredient> createOutputs(ColorSwappableShapedRecipe recipe) {
        HolderLookup.Provider registries = RegistryAccess.EMPTY;
        List<ItemStack> baseline = new ArrayList<>(recipe.getSlots().size());
        List<Integer> tagSlots = new ArrayList<>();
        List<ItemStack[]> tagCandidates = new ArrayList<>();

        for (int i = 0; i < recipe.getSlots().size(); i++) {
            Either<Either<TagColorIngredient, ItemColorIngredient>, Ingredient> slot = recipe.getSlots().get(i);

            if (slot.left().isPresent()) {
                Either<TagColorIngredient, ItemColorIngredient> color = slot.left().orElseThrow();

                if (color.left().isPresent()) {
                    TagColorIngredient tag = color.left().orElseThrow();
                    ItemStack[] candidates = tag.toIngredient().getItems();

                    tagSlots.add(i);
                    tagCandidates.add(candidates);
                    baseline.add(candidates.length > 0 ? candidates[0] : ItemStack.EMPTY);
                } else baseline.add(new ItemStack(color.right().orElseThrow().item()));
            } else {
                Ingredient ingredient = slot.right().orElseThrow();

                if (ingredient == Ingredient.EMPTY)
                    baseline.add(ItemStack.EMPTY);
                else {
                    ItemStack[] items = ingredient.getItems();
                    baseline.add(items.length > 0 ? items[0] : ItemStack.EMPTY);
                }
            }
        }

        List<ItemStack> outputs = new ArrayList<>();

        if (tagSlots.isEmpty()) {
            outputs.add(recipe.assemble(CraftingInput.of(recipe.getWidth(), recipe.getHeight(), baseline), registries));
        } else {
            int variants = tagCandidates.getFirst().length;

            for (int variant = 0; variant < variants; variant++) {
                List<ItemStack> grid = new ArrayList<>(baseline);

                for (int i = 0; i < tagSlots.size(); i++) {
                    ItemStack[] candidates = tagCandidates.get(i);

                    if (variant < candidates.length)
                        grid.set(tagSlots.get(i), candidates[variant]);
                }
                ItemStack output = recipe.assemble(CraftingInput.of(recipe.getWidth(), recipe.getHeight(), grid), registries);

                if (!output.isEmpty())
                    outputs.add(output);
            }
        }
        return List.of(EntryIngredients.ofItemStacks(outputs));
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