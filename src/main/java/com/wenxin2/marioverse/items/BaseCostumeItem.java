package com.wenxin2.marioverse.items;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

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
}
