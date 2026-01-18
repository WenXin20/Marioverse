package com.wenxin2.marioverse.integration.jei_compat;

import com.wenxin2.marioverse.data.WarpTrapDoorRecipe;
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

public class WarpTrapDoorCraftingExtension implements ICraftingCategoryExtension<WarpTrapDoorRecipe> {
    private static final ResourceLocation OUTPUT_SLOT =
            ResourceLocation.parse("textures/gui/container/crafting_table.png");
    private final IDrawable inputSlot;
    private final IDrawable outputSlot;

    public WarpTrapDoorCraftingExtension(IGuiHelper guiHelper) {
        this.inputSlot = guiHelper.getSlotDrawable();
        this.outputSlot = guiHelper.drawableBuilder(OUTPUT_SLOT, 119, 30, 26, 26).build();
    }

    @Override
    public int getHeight(RecipeHolder<WarpTrapDoorRecipe> recipeHolder) {
        return 0;
    }

    @Override
    public int getWidth(RecipeHolder<WarpTrapDoorRecipe> recipeHolder) {
        return 0;
    }

    @Override
    public void setRecipe(RecipeHolder<WarpTrapDoorRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                          ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
        List<ItemStack> extras = List.of(Ingredient.of(WarpTrapDoorRecipe.INGREDIENTS).getItems());

        builder.setShapeless();

        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
                .setBackground(inputSlot, -1, -1).setSlotName("Any Trapdoor")
                .addIngredients(VanillaTypes.ITEM_STACK, RegistryEventHandlers.WARP_TRAPDOORS.keySet()
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
                .addIngredients(VanillaTypes.ITEM_STACK, RegistryEventHandlers.WARP_TRAPDOORS.values()
                        .stream().map(ItemStack::new).toList());
    }
}
