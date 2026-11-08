package com.wenxin2.marioverse.integration.jei_compat;

import com.wenxin2.marioverse.data.WarpDoorRecipe;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class WarpDoorCraftingExtension implements ICraftingCategoryExtension<WarpDoorRecipe> {
    private final IDrawable inputSlot;
    private final ICraftingGridHelper craftingGridHelper;

    public WarpDoorCraftingExtension(IGuiHelper guiHelper) {
        this.inputSlot = guiHelper.getSlotDrawable();
        this.craftingGridHelper = guiHelper.createCraftingGridHelper();
    }

    @Override
    public int getWidth(RecipeHolder<WarpDoorRecipe> recipeHolder) {
        return recipeHolder.value().getWidth();
    }

    @Override
    public int getHeight(RecipeHolder<WarpDoorRecipe> recipeHolder) {
        return recipeHolder.value().getHeight();
    }

    @Override
    public void setRecipe(RecipeHolder<WarpDoorRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        WarpDoorRecipe recipe = recipeHolder.value();
        List<Ingredient> slots = recipe.getIngredients();
        int columns = recipe.isShaped() ? recipe.getWidth() : (slots.size() <= 1 ? 1 : slots.size() <= 4 ? 2 : 3);
        IRecipeSlotBuilder sourceSlot = null;

        if (!recipe.isShaped())
            builder.setShapeless();

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, x * 18 + 1, y * 18 + 1)
                        .setBackground(this.inputSlot, -1, -1);
                int index = y * columns + x;
                boolean inRecipe = x < columns && index < slots.size()
                        && (!recipe.isShaped() || y < recipe.getHeight());

                if (!inRecipe)
                    continue;

                if (index == recipe.getSourceIndex()) {
                    slot.addItemStacks(recipe.getSourceStacks());
                    sourceSlot = slot;
                } else slot.addIngredients(slots.get(index));
            }
        }

        IRecipeSlotBuilder outputSlot = this.craftingGridHelper
                .createAndSetOutputs(builder, VanillaTypes.ITEM_STACK, recipe.getOutputStacks());

        if (sourceSlot != null)
            builder.createFocusLink(sourceSlot, outputSlot);
    }
}
