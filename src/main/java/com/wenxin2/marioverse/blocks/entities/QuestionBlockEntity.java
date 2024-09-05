package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.init.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class QuestionBlockEntity extends BlockEntity {
    private final ItemStackHandler items = new ItemStackHandler(1);
    public static final String INVENTORY = "Inventory";

    public QuestionBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.QUESTION_BLOCK_ENTITY.get(), pos, state);
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public ItemStack getStackInSlot() {
        return items.getStackInSlot(0);
    }

    public boolean hasItems() {
        return !items.getStackInSlot(0).isEmpty();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(INVENTORY, items.serializeNBT(provider));
    }

    // Load data from NBT
    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        items.deserializeNBT(provider, tag.getCompound(INVENTORY));
    }

    public void addItem(Player player, ItemStack stack) {
        ItemStack existingStack = items.getStackInSlot(0);
        if (existingStack.isEmpty()) {
            items.setStackInSlot(0, stack.split(stack.getMaxStackSize()));
        } else if (ItemStack.isSameItemSameComponents(existingStack, stack)) {
            int countToAdd = Math.min(stack.getMaxStackSize() - existingStack.getCount(), stack.getCount());
            existingStack.grow(countToAdd);
        }
    }

    public boolean removeOneItem() {
        ItemStack storedStack = items.getStackInSlot(0);
        if (!storedStack.isEmpty() && storedStack.getCount() > 0) {
            storedStack.shrink(1);  // Remove one item
            items.setStackInSlot(0, storedStack);
            return true;
        }
        return false;
    }
}
