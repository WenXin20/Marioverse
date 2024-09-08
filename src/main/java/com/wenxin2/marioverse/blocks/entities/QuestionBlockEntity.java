package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class QuestionBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private boolean lootTableProcessed = false;
    private boolean lastPowered = false;

    public QuestionBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.QUESTION_BLOCK_ENTITY.get(), pos, state);
    }

    @NotNull
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int slots, Inventory inventory) {
        return null;
    }

    public ItemStack getStackInSlot() {
        return items.getFirst();
    }

    public boolean hasItems() {
        return !items.getFirst().isEmpty();
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return Component.translatable("menu.marioverse.question_block");
    }

    public static void serverTick(Level p_155014_, BlockPos p_155015_, BlockState p_155016_) {

    }

    @Override
    public void setChanged() {
        if (this.level != null && this.level.getBlockState(this.getBlockPos()).getBlock() instanceof QuestionBlock) {
            if (this.getLootTable() != null || !this.items.getFirst().isEmpty())
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
            else
                this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
        }
        super.setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.trySaveLootTable(tag)) {
            ContainerHelper.saveAllItems(tag, this.items, provider);
        }
        tag.putBoolean("LastPowered", lastPowered);
        tag.putBoolean("LootTableProcessed", lootTableProcessed);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(tag)) {
            ContainerHelper.loadAllItems(tag, this.items, provider);
        }
        lastPowered = tag.getBoolean("LastPowered");
        lootTableProcessed = tag.getBoolean("LootTableProcessed");
    }

    public void addItem(ItemStack stack) {
        ItemStack existingStack = items.getFirst();
        if (existingStack.isEmpty()) {
            items.set(0, stack.split(stack.getMaxStackSize()));
        } else if (ItemStack.isSameItemSameComponents(existingStack, stack)) {
            int countToAdd = Math.min(stack.getMaxStackSize() - existingStack.getCount(), stack.getCount());
            existingStack.grow(countToAdd);
        }
    }

    public boolean removeItems() {
        ItemStack storedStack = items.getFirst();
        if (!storedStack.isEmpty() && storedStack.getCount() > 0) {
            items.set(0, storedStack);
            storedStack.shrink(1);  // Remove one item
            this.setChanged();
            return true;
        }
        return false;
    }

    public boolean hasLootTableBeenProcessed() {
        return lootTableProcessed;
    }

    public void processLootTable() {
        lootTableProcessed = true;
    }
    public boolean isLastPowered() {
        return lastPowered;
    }

    public void setLastPowered(boolean powered) {
        this.lastPowered = powered;
    }
}
