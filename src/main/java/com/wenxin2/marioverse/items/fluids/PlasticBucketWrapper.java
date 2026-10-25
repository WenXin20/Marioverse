package com.wenxin2.marioverse.items.fluids;

import com.wenxin2.marioverse.items.PlasticBucketItem;
import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.wrappers.FluidBucketWrapper;

public class PlasticBucketWrapper extends FluidBucketWrapper {
    public PlasticBucketWrapper(ItemStack container) {
        super(container);
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        if (fluid.is(Fluids.LAVA))
            return PlasticBucketItem.isFireProof(this.container);
        return true;
    }

    protected void setFluid(FluidStack fluidStack) {
        ItemStack oldStack = this.container;
        ItemStack newStack;

        if (fluidStack.isEmpty())
            newStack = new ItemStack(ItemRegistry.PLASTIC_BUCKET.get());
        else newStack = PlasticBucketItem.getPlasticBucketForFluid(fluidStack);

        newStack.applyComponents(oldStack.getComponents());
        this.container = newStack;
    }
}
