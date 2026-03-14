package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DisguisedBlockEntity extends BlockEntity implements RandomizableContainer {
    public static final ModelProperty<BlockState> DISGUISED = new ModelProperty<>();
    private BlockState disguiseState = Blocks.AIR.defaultBlockState();
    @Nullable protected ResourceKey<LootTable> lootTable;
    protected long lootTableSeed;

    protected final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0 && DisguisedBlockEntity.this.level != null)
                DisguisedBlockEntity.this.setDisguise();
        }

        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            if (slot == 0 && DisguisedBlockEntity.this.level != null) {
            }
            super.setStackInSlot(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0 && DisguisedBlockEntity.this.level != null)
                DisguisedBlockEntity.this.setDisguise();
            return super.extractItem(slot, amount, simulate);
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

    @Override
    public int getContainerSize() {
        return 1;
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

    public IItemHandler getInventory() {
        return this.inventory;
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
        this.lootTable = lootTable;
        this.setChanged();
    }

    @Override
    public long getLootTableSeed() {
        return this.lootTableSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootTableSeed = lootTableSeed;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.getStackInSlot(0).isEmpty();
    }

    @NotNull
    @Override
    public ItemStack getItem(int slot) {
        this.unpackLootTable(null);

        if (slot == 0)
            return this.inventory.getStackInSlot(0);
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);

        if (slot == 0) {
            this.setDisguise();
            this.setDisguiseState(Blocks.AIR.defaultBlockState());
            return this.inventory.extractItem(0, amount, false);
        }
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0) {
            ItemStack stack = this.inventory.getStackInSlot(0);
            this.inventory.setStackInSlot(0, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        this.unpackLootTable(null);

        if (slot == 0) {
            this.inventory.setStackInSlot(0, stack);
            if (stack.getItem() instanceof BlockItem blockItem)
                this.setDisguiseState(blockItem.getBlock().defaultBlockState());
            return;
        }
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.level == null)
            return false;

        if (this.level.getBlockEntity(this.worldPosition) != this)
            return false;

        return player.distanceToSqr(this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5) <= 64.0;
    }

    @Override
    public void clearContent() {
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public BlockState getDisguise() {
        return this.disguiseState;
    }

    public void setDisguiseState(BlockState state) {
        this.disguiseState = state;
    }

    public void setDisguiseItem(ItemStack stack) {
        this.inventory.setStackInSlot(0, stack);
    }

    public ItemStack getDisguiseItem() {
        return this.inventory.getStackInSlot(0);
    }

    public BlockState getPlacementState(Player player, BlockPos pos, ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return null;

        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), player.getDirection(), pos, false);
        BlockPlaceContext context = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hit);

        return blockItem.getBlock().getStateForPlacement(context);
    }

    protected void setDisguise() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        boolean disguised = false;

        if (stack.getItem() instanceof BlockItem) {
            disguised = true;
        }

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
}