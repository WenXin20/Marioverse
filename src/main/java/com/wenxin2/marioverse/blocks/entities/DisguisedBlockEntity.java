package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
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
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DisguisedBlockEntity extends BlockEntity {
    public static final ModelProperty<BlockState> DISGUISED = new ModelProperty<>();
    protected BlockState disguiseState = Blocks.AIR.defaultBlockState();

    protected final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0 && DisguisedBlockEntity.this.level != null)
                DisguisedBlockEntity.this.setDisguise();
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

    public DisguisedBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void setDisguise() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        boolean disguised = false;

        if (stack.getItem() instanceof BlockItem blockItem) {
            this.disguiseState = blockItem.getBlock().defaultBlockState();
            disguised = true;
        }
        else this.disguiseState = Blocks.AIR.defaultBlockState();

        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);

            if (state.hasProperty(BlockStatePropertyRegistry.DISGUISED)
                    && state.getValue(BlockStatePropertyRegistry.DISGUISED) != disguised)
                this.level.setBlock(this.worldPosition, state.setValue(BlockStatePropertyRegistry.DISGUISED, disguised), 3);
            if (state.hasProperty(BlockStatePropertyRegistry.INVISIBLE)
                    && state.hasProperty(BlockStatePropertyRegistry.DISGUISED)
                    && state.getValue(BlockStatePropertyRegistry.DISGUISED))
                this.level.setBlock(this.worldPosition, state.setValue(BlockStatePropertyRegistry.INVISIBLE, true), 3);
            this.requestModelDataUpdate();
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
            this.setChanged();
        }
    }

    public BlockState getDisguise() {
        return this.disguiseState;
    }

    public void setDisguiseItem(ItemStack stack) {
        this.inventory.setStackInSlot(0, stack);
    }

    public ItemStack getDisguiseItem() {
        return this.inventory.getStackInSlot(0);
    }

    public IItemHandler getInventory() {
        return this.inventory;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inventory", this.inventory.serializeNBT(provider));

        if (!this.disguiseState.isAir())
            tag.putString("disguiseBlock", BuiltInRegistries.BLOCK.getKey(this.disguiseState.getBlock()).toString());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.inventory.deserializeNBT(provider, tag.getCompound("inventory"));

        if (tag.contains("disguiseBlock")) {
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(tag.getString("disguiseBlock")));
            this.disguiseState = block.defaultBlockState();
        }
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(DISGUISED, this.getDisguise()).build();
    }
}