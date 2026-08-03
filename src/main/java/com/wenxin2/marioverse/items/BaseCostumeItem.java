package com.wenxin2.marioverse.items;

import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BaseCostumeItem extends ArmorItem {
    private final Ingredient repairIngredient;
    int tooltipLineAmt;

    public BaseCostumeItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType,
                           int tooltipLineAmt, Properties properties) {
        super(armorMaterial, armorType, properties);
        this.repairIngredient = repairIngredient;
        this.tooltipLineAmt = tooltipLineAmt;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));

            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt));

            if (stack.has(DataComponents.DYED_COLOR))
                list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));

        super.appendHoverText(stack, tooltipContext, list, tooltip);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairStack) {
        return repairIngredient.test(repairStack);
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item item, Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide)
            CriteriaTriggers.INVENTORY_CHANGED.trigger((ServerPlayer) player, player.getInventory(), item.getDefaultInstance());
        return super.swapWithEquipmentSlot(item, level, player, hand);
    }
}