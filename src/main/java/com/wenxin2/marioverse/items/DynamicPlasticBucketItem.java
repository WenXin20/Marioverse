package com.wenxin2.marioverse.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;

public class DynamicPlasticBucketItem extends FluidPlasticBucketItem {
    public DynamicPlasticBucketItem(int tooltipLineAmt, Fluid fluid, Properties properties) {
        super(tooltipLineAmt, fluid, properties);
    }

    @NotNull
    @Override
    public String getDescriptionId() {
        return "item.marioverse.plastic_fluid_bucket";
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(this.getDescriptionId(), this.content.getFluidType().getDescription());
    }
}
