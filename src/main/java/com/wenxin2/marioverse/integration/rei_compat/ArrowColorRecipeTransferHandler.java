package com.wenxin2.marioverse.integration.rei_compat;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.util.Either;
import com.wenxin2.marioverse.data.ArrowColorShapedRecipe;
import com.wenxin2.marioverse.data.DyeColorIngredient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import me.shedaniel.rei.api.client.registry.transfer.TransferHandler;
import me.shedaniel.rei.api.client.registry.transfer.simple.SimpleTransferHandler;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.transfer.info.stack.SlotAccessor;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ArrowColorRecipeTransferHandler implements SimpleTransferHandler {
    private final CategoryIdentifier<DefaultCraftingDisplay<?>> categoryIdentifier;

    public ArrowColorRecipeTransferHandler(CategoryIdentifier<DefaultCraftingDisplay<?>> categoryIdentifier) {
        this.categoryIdentifier = categoryIdentifier;
    }

    @Override
    public TransferHandler.ApplicabilityResult checkApplicable(TransferHandler.Context context) {
        boolean applicable = context.getMenu() instanceof CraftingMenu
                && this.categoryIdentifier.equals(context.getDisplay().getCategoryIdentifier())
                && context.getContainerScreen() != null;
        return applicable ? TransferHandler.ApplicabilityResult.createApplicable()
                : TransferHandler.ApplicabilityResult.createNotApplicable();
    }

    @Override
    public Iterable<SlotAccessor> getInputSlots(TransferHandler.Context context) {
        AbstractContainerMenu menu = context.getMenu();
        if (menu == null)
            return Collections.emptyList();
        return IntStream.range(1, 10).mapToObj(id -> SlotAccessor.fromSlot(menu.getSlot(id))).toList();
    }

    @Override
    public Iterable<SlotAccessor> getInventorySlots(TransferHandler.Context context) {
        LocalPlayer player = context.getMinecraft().player;

        if (player == null)
            return Collections.emptyList();

        Inventory inventory = player.getInventory();

        List<SlotAccessor> slots = new ArrayList<>(inventory.items.size());
        for (int i = 0; i < inventory.items.size(); i++) {
            slots.add(SlotAccessor.fromPlayerInventory(player, i));
        }
        return slots;
    }

    @Override
    public List<InputIngredient<ItemStack>> getInputsIndexed(TransferHandler.Context context) {
        if (!(context.getDisplay() instanceof ArrowColorCraftingDisplay display))
            return SimpleTransferHandler.super.getInputsIndexed(context);

        RecipeHolder<ArrowColorShapedRecipe> recipeHolder = display.getOptionalRecipe().orElse(null);
        if (recipeHolder == null)
            return SimpleTransferHandler.super.getInputsIndexed(context);

        ArrowColorShapedRecipe recipe = recipeHolder.value();
        List<Either<DyeColorIngredient, Ingredient>> slots = recipe.getSlots();

        Map<Object, Integer> neededCountByGroup = new IdentityHashMap<>();
        for (Either<DyeColorIngredient, Ingredient> slot : slots) {
            slot.ifLeft(dye -> neededCountByGroup.merge(dye, 1, Integer::sum));
        }

        Map<Item, Integer> available = new IdentityHashMap<>();
        for (SlotAccessor accessor : Iterables.concat(this.getInputSlots(context), this.getInventorySlots(context))) {
            ItemStack stack = accessor.getItemStack();
            if (!stack.isEmpty())
                available.merge(stack.getItem(), stack.getCount(), Integer::sum);
        }

        Map<Object, ItemStack> chosenPerGroup = new IdentityHashMap<>();
        for (Map.Entry<Object, Integer> entry : neededCountByGroup.entrySet()) {
            DyeColorIngredient dye = (DyeColorIngredient) entry.getKey();
            int needed = entry.getValue();
            ItemStack[] candidates = dye.toIngredient().getItems();

            ItemStack chosen = null;
            for (ItemStack candidate : candidates) {
                if (available.getOrDefault(candidate.getItem(), 0) >= needed) {
                    chosen = candidate;
                    break;
                }
            }
            chosenPerGroup.put(dye, chosen != null ? chosen : (candidates.length > 0 ? candidates[0] : ItemStack.EMPTY));
        }

        List<InputIngredient<EntryStack<?>>> defaults =
                display.getInputIngredients(context.getMenu(), context.getMinecraft().player);

        List<InputIngredient<ItemStack>> result = new ArrayList<>(slots.size());
        for (int i = 0; i < slots.size(); i++) {
            Either<DyeColorIngredient, Ingredient> slot = slots.get(i);
            InputIngredient<EntryStack<?>> base = i < defaults.size() ? defaults.get(i) : InputIngredient.empty(i);

            InputIngredient<EntryStack<?>> raw = slot.left().isPresent()
                    ? InputIngredient.of(base.getIndex(), EntryIngredients.of(chosenPerGroup.get(slot.left().get())))
                    : base;

            result.add(InputIngredient.withType(raw, VanillaEntryTypes.ITEM));
        }
        return result;
    }
}
