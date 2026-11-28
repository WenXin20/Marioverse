package com.wenxin2.marioverse.inventory;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class WarpPipeFluidHandler implements IFluidHandler {
    public static final int MB_PER_XP = 20;
    private final WarpPipeBlockEntity pipeBE;

    public WarpPipeFluidHandler(WarpPipeBlockEntity pipeBE) {
        this.pipeBE = pipeBE;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {
        Fluid xpFluid = this.getXpFluid();
        if (xpFluid == null)
            return this.pipeBE.isSpoutActive() ? new FluidStack(Fluids.WATER, Integer.MAX_VALUE) : FluidStack.EMPTY;
        if (this.pipeBE.getStashedXp() <= 0)
            return FluidStack.EMPTY;
        return new FluidStack(xpFluid, (int) Math.min(Integer.MAX_VALUE, (long) this.pipeBE.getStashedXp() * MB_PER_XP));
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.getFluidInTank(tank).getAmount();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return false;
    }

    @Override
    public int fill(@NotNull FluidStack resource, @NotNull FluidAction action) {
        return 0;
    }

    @NotNull
    @Override
    public FluidStack drain(@NotNull FluidStack resource, @NotNull FluidAction action) {
        if (resource.isEmpty())
            return FluidStack.EMPTY;
        if (this.getXpFluid() == null)
            return resource.is(Fluids.WATER) ? this.drainWater(resource.getAmount()) : FluidStack.EMPTY;
        return resource.is(Tags.Fluids.EXPERIENCE) ? this.drainXp(resource.getFluid(), resource.getAmount(), action) : FluidStack.EMPTY;
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, @NotNull FluidAction action) {
        Fluid xpFluid = this.getXpFluid();
        return xpFluid == null ? this.drainWater(maxDrain) : this.drainXp(xpFluid, maxDrain, action);
    }

    private FluidStack drainWater(int maxDrain) {
        return maxDrain > 0 && this.pipeBE.isSpoutActive() ? new FluidStack(Fluids.WATER, maxDrain) : FluidStack.EMPTY;
    }

    private FluidStack drainXp(Fluid fluid, int maxDrain, FluidAction action) {
        int points = Math.min(maxDrain / MB_PER_XP, this.pipeBE.getStashedXp());
        if (points <= 0)
            return FluidStack.EMPTY;

        if (action.execute())
            this.pipeBE.takeStashedXp(points);
        return new FluidStack(fluid, points * MB_PER_XP);
    }

    @Nullable
    private Fluid getXpFluid() {
        return this.pipeBE.isAllowAutomation() ? WarpPipeFluidHandler.getExperienceFluid() : null;
    }

    @Nullable
    private static Fluid getExperienceFluid() {
        return BuiltInRegistries.FLUID.getTag(Tags.Fluids.EXPERIENCE)
                .flatMap(tag -> tag.stream().map(Holder::value)
                        .filter(fluid -> fluid.isSource(fluid.defaultFluidState())).findFirst())
                .orElse(null);
    }
}
