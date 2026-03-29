package com.wenxin2.marioverse.inventory.slots;

import java.util.Optional;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GhostSlot extends Slot {
    public GhostSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof BlockItem || stack.getItem() instanceof BucketItem;
    }

    @Override
    public void set(ItemStack stack) {
        if (!stack.isEmpty()) {
            stack = stack.copy();
            stack.setCount(1);
        }
        super.set(stack);
    }

    @NotNull
    @Override
    public Optional<ItemStack> tryRemove(int count, int decrement, Player player) {
        this.set(ItemStack.EMPTY);
        this.setChanged();
        return Optional.empty();
    }
}