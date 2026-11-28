package com.wenxin2.marioverse.inventory;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class WarpPipeItemHandler implements IItemHandler {
    private static final int SPAWN_ITEM_SLOT = 0;
    private static final int SLOT_COUNT = 2;
    private final WarpPipeBlockEntity pipeBE;
    private final IItemHandler spawnItemHandler;

    public WarpPipeItemHandler(WarpPipeBlockEntity pipeBE) {
        this.pipeBE = pipeBE;
        this.spawnItemHandler = new InvWrapper(pipeBE);
    }

    @Override
    public int getSlots() {
        return SLOT_COUNT;
    }

    @NotNull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot == SPAWN_ITEM_SLOT)
            return this.spawnItemHandler.getStackInSlot(slot);
        if (!this.pipeBE.isAllowAutomation() || this.pipeBE.getDepositCount() <= 0)
            return ItemStack.EMPTY;

        ItemStack payment = this.pipeBE.getDeposit();
        return payment.copyWithCount(Math.min(this.pipeBE.getDepositCount(), payment.getMaxStackSize()));
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return slot == SPAWN_ITEM_SLOT ? this.spawnItemHandler.insertItem(slot, stack, simulate) : stack;
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == SPAWN_ITEM_SLOT)
            return this.spawnItemHandler.extractItem(slot, amount, simulate);
        if (!this.pipeBE.isAllowAutomation())
            return ItemStack.EMPTY;
        if (simulate) {
            ItemStack stack = this.getStackInSlot(slot);
            return stack.copyWithCount(Math.min(amount, stack.getCount()));
        }
        return this.pipeBE.takeDeposit(amount);
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot == SPAWN_ITEM_SLOT ? this.spawnItemHandler.getSlotLimit(slot) : Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return slot == SPAWN_ITEM_SLOT && this.spawnItemHandler.isItemValid(slot, stack);
    }
}