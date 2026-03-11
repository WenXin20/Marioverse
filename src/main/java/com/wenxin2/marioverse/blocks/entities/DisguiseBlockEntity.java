package com.wenxin2.marioverse.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

public class DisguiseBlockEntity extends BlockEntity {
    protected BlockState disguiseState = Blocks.AIR.defaultBlockState();

    protected final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();

            if (slot == 0 && level != null)
                DisguiseBlockEntity.this.updateDisguise();
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof BlockItem;
        }
    };

    public DisguiseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void updateDisguise() {
        ItemStack stack = inventory.getStackInSlot(0);

        if (stack.getItem() instanceof BlockItem blockItem)
            this.disguiseState = blockItem.getBlock().defaultBlockState();
        else this.disguiseState = Blocks.AIR.defaultBlockState();

        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public BlockState getDisguise() {
        return this.disguiseState;
    }

    public void setDisguiseItem(ItemStack stack) {
        inventory.setStackInSlot(0, stack);
    }

    public ItemStack getDisguiseItem() {
        return inventory.getStackInSlot(0);
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inventory", inventory.serializeNBT(provider));

        if (!this.disguiseState.isAir())
            tag.putString("disguiseBlock", BuiltInRegistries.BLOCK.getKey(this.disguiseState.getBlock()).toString());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        inventory.deserializeNBT(provider, tag.getCompound("inventory"));

        if (tag.contains("disguiseBlock")) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("disguiseBlock")));
            this.disguiseState = block.defaultBlockState();
        }
    }
}