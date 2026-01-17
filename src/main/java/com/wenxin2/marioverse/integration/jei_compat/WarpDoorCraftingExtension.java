package com.wenxin2.marioverse.integration.jei_compat;

import com.wenxin2.marioverse.data.WarpDoorRecipe;
import com.wenxin2.marioverse.event_handlers.RegistryEventHandlers;
import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class WarpDoorCraftingExtension implements ICraftingCategoryExtension<WarpDoorRecipe> {
    private static final ResourceLocation OUTPUT_SLOT =
            ResourceLocation.parse("textures/gui/container/crafting_table.png");
    private final IDrawable inputSlot;
    private final IDrawable outputSlot;

    public WarpDoorCraftingExtension(IGuiHelper guiHelper) {
        this.inputSlot = guiHelper.getSlotDrawable();
        this.outputSlot = guiHelper.drawableBuilder(OUTPUT_SLOT, 135, 30, 26, 26).build();
    }

    @Override
    public int getHeight(RecipeHolder<WarpDoorRecipe> recipeHolder) {
        return 0;
    }

    @Override
    public int getWidth(RecipeHolder<WarpDoorRecipe> recipeHolder) {
        return 0;
    }

    @Override
    public void setRecipe(RecipeHolder<WarpDoorRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        List<ItemStack> extras = List.of(Ingredient.of(WarpDoorRecipe.INGREDIENTS).getItems());

        builder.setShapeless();

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .setBackground(inputSlot, -1, -1).setSlotName("Any Door")
                .addIngredients(VanillaTypes.ITEM_STACK, RegistryEventHandlers.WARP_DOORS.keySet()
                        .stream().map(ItemStack::new).toList());

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1)
                .setBackground(inputSlot, -1, -1)
                .addItemStacks(extras);

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 1)
                .setBackground(inputSlot, -1, -1)
                .addItemStack(ItemStack.EMPTY);

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 19)
                .setBackground(inputSlot, -1, -1)
                .addItemStack(ItemStack.EMPTY);

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 19)
                .setBackground(inputSlot, -1, -1)
                .addItemStack(ItemStack.EMPTY);

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 19)
                .setBackground(inputSlot, -1, -1)
                .addItemStack(ItemStack.EMPTY);

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 37)
                .setBackground(inputSlot, -1, -1)
                .addItemStack(ItemStack.EMPTY);

        builder.addSlot(RecipeIngredientRole.INPUT, 19, 37)
                .setBackground(inputSlot, -1, -1)
                .addItemStack(ItemStack.EMPTY);

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 37)
                .setBackground(inputSlot, -1, -1)
                .addItemStack(ItemStack.EMPTY);

        builder.addSlot(RecipeIngredientRole.OUTPUT, 95, 19)
                .setBackground(outputSlot, -4, -4)
                .addIngredients(VanillaTypes.ITEM_STACK, RegistryEventHandlers.WARP_DOORS.values()
                        .stream().map(ItemStack::new).toList());
    }
}
