package com.wenxin2.marioverse.accessories;

import io.wispforest.accessories.api.Accessory;
import net.minecraft.world.item.ItemStack;

public class PlasticBucketAccessory implements Accessory {
    @Override
    public boolean canEquipFromUse(ItemStack stack) {
        return false;
    }
}
