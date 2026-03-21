package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class DisguisedBlockEntity extends BlockEntity implements RandomizableContainer {
    public static final ModelProperty<BlockState> DISGUISED = new ModelProperty<>();
    private BlockState disguiseState = Blocks.AIR.defaultBlockState();
    private BlockEntity disguiseBlockEntity;
    @Nullable protected ResourceKey<LootTable> lootTable;
    protected long lootTableSeed;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            if (slot == 0 && DisguisedBlockEntity.this.level != null)
                DisguisedBlockEntity.this.setDisguise();
        }
        @Override
        public void setStackInSlot(int slot, ItemStack stack) {
            super.setStackInSlot(slot, stack);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0 && DisguisedBlockEntity.this.level != null) {
                DisguisedBlockEntity.this.setDisguise();
            }
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

        if (this.disguiseState != null)
            tag.put("disguiseBlock", NbtUtils.writeBlockState(this.disguiseState));
        if (this.disguiseBlockEntity != null)
            tag.put("disguiseBlockEntity", this.disguiseBlockEntity.saveWithoutMetadata(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.inventory.deserializeNBT(provider, tag.getCompound("inventory"));

        if (tag.contains("disguiseBlock"))
            this.disguiseState = NbtUtils.readBlockState(provider.lookupOrThrow(Registries.BLOCK), tag.getCompound("disguiseBlock"));

        if (tag.contains("disguiseBlockEntity") && this.disguiseState.getBlock() instanceof EntityBlock entityBlock) {
            this.disguiseBlockEntity = entityBlock.newBlockEntity(this.worldPosition, this.disguiseState);

            if (this.disguiseBlockEntity != null && this.level != null) {
                this.disguiseBlockEntity.setLevel(this.level);
                this.disguiseBlockEntity.loadWithComponents(tag.getCompound("disguiseBlockEntity"), provider);
                this.disguiseBlockEntity.applyComponentsFromItemStack(this.inventory.getStackInSlot(0));
                BlockItem.updateCustomBlockEntityTag(this.level, null, this.worldPosition, this.inventory.getStackInSlot(0));
            }
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        this.requestModelDataUpdate();
        if (this.level != null && this.level.getModelDataManager() != null)
            this.level.getModelDataManager().requestRefresh(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, packet, lookupProvider);
        this.requestModelDataUpdate();
        if (this.level != null && this.level.getModelDataManager() != null)
            this.level.getModelDataManager().requestRefresh(this);
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(DISGUISED, this.getDisguiseState()).build();
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

    public BlockEntity getDisguiseBlockEntity() {
        return this.disguiseBlockEntity;
    }

    public BlockState getDisguiseState() {
        return this.disguiseState;
    }

    public void setDisguiseState(BlockState state) {
        this.disguiseState = state;
        this.setChanged();
    }

    public BlockState getPlacementState(Player player, ItemStack stack, BlockHitResult hitResult) {
        if (!(stack.getItem() instanceof BlockItem blockItem))
            return null;
        BlockPlaceContext context = new BlockPlaceContext(player, InteractionHand.MAIN_HAND, stack, hitResult);

        return blockItem.getBlock().getStateForPlacement(context);
    }

    public void setDisguise() {
        ItemStack stack = this.inventory.getStackInSlot(0);
        boolean disguised = false;

        if (stack.getItem() instanceof BlockItem)
            disguised = true;

        if (this.level != null) {
            BlockState state = this.level.getBlockState(this.worldPosition);

            if (disguiseState == null || disguiseState.isAir()) {
                this.disguiseState = Blocks.AIR.defaultBlockState();

                if (state.hasProperty(BlockStatePropertyRegistry.DISGUISED)
                        && state.getValue(BlockStatePropertyRegistry.DISGUISED)) {
                    if (state.hasProperty(BlockStatePropertyRegistry.INVISIBLE)
                            && state.hasProperty(BlockStatePropertyRegistry.DISGUISED))
                        this.level.setBlock(this.worldPosition, state.setValue(BlockStatePropertyRegistry.DISGUISED, false)
                                .setValue(BlockStatePropertyRegistry.INVISIBLE, false), 3);
                    else this.level.setBlock(this.worldPosition, state.setValue(BlockStatePropertyRegistry.DISGUISED, false), 3);
                }
            } else {
                if (state.hasProperty(BlockStatePropertyRegistry.DISGUISED)
                        && state.getValue(BlockStatePropertyRegistry.DISGUISED) != disguised)
                    this.level.setBlock(this.worldPosition, state.setValue(BlockStatePropertyRegistry.DISGUISED, disguised), 3);
                if (state.hasProperty(BlockStatePropertyRegistry.INVISIBLE)
                        && state.hasProperty(BlockStatePropertyRegistry.DISGUISED)
                        && state.getValue(BlockStatePropertyRegistry.DISGUISED))
                    this.level.setBlock(this.worldPosition, state.setValue(BlockStatePropertyRegistry.INVISIBLE, true), 3);
            }
            this.createDisguiseBlockEntity(stack);
            this.requestModelDataUpdate();
            this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
            if (this.level instanceof ServerLevel serverLevel)
                serverLevel.getChunkSource().blockChanged(this.worldPosition);
            if (this.level != null && this.level.getModelDataManager() != null)
                this.level.getModelDataManager().requestRefresh(this);
            this.setChanged();
        }
    }

    public void createDisguiseBlockEntity(ItemStack stack) {
        this.disguiseBlockEntity = null;

        if (!(disguiseState.getBlock() instanceof EntityBlock entityBlock))
            return;

        BlockEntity blockEntity = entityBlock.newBlockEntity(this.worldPosition, disguiseState);

        if (blockEntity != null && this.level != null) {
            blockEntity.setLevel(this.level);
            blockEntity.applyComponentsFromItemStack(stack);
            BlockItem.updateCustomBlockEntityTag(this.level, null, this.worldPosition, stack);
            this.disguiseBlockEntity = blockEntity;
        }
    }
}