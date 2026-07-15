package com.wenxin2.marioverse.client.renderers.costumes;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface CostumeRendererAccess {
    ItemStack getCurrentStack();
    EquipmentSlot getCurrentSlot();
}
