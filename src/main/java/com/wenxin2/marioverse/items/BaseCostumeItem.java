package com.wenxin2.marioverse.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BaseCostumeItem extends ArmorItem {
    private final Ingredient repairIngredient;

    public BaseCostumeItem(Ingredient repairIngredient, Holder<ArmorMaterial> armorMaterial, Type armorType, Properties properties) {
        super(armorMaterial, armorType, properties);
        this.repairIngredient = repairIngredient;
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
