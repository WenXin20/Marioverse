package com.wenxin2.marioverse.items;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltip) {
        Component fluidName = this.content.getFluidType().getDescription().copy().withStyle(ChatFormatting.GRAY);

        if (Screen.hasShiftDown() && this.tooltipLineAmt > 0) {
            list.add(Component.literal(""));
            for (int lineAmt = 1; lineAmt <= tooltipLineAmt; lineAmt++)
                list.add(Component.translatable(this.getDescriptionId() + ".tooltip.line" + lineAmt, fluidName));
            list.add(Component.literal(""));
        } else if (this.tooltipLineAmt > 0)
            list.add(Component.translatable(this.getDescriptionId() + ".tooltip"));
    }
}