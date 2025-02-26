package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperInvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperStorageBrickBlock;
import com.wenxin2.marioverse.init.BlockEntityRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.NotNull;

public class QuestionBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {
    @Nullable protected ResourceKey<LootTable> lootTable;
    private ItemStack item = ItemStack.EMPTY;
    protected long lootTableSeed;
    private boolean lastPowered;

    public QuestionBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.QUESTION_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return this.getType().isValid(state) || state.getBlock() instanceof InvisibleQuestionBlock
                || state.getBlock() instanceof StorageBrickBlock
                || state.getBlock() instanceof WeatheringCopperQuestionBlock
                || state.getBlock() instanceof WeatheringCopperInvisibleQuestionBlock
                || state.getBlock() instanceof WeatheringCopperStorageBrickBlock;
    }

    @NotNull
    @Override
    public BlockEntity getContainerBlockEntity() {
        return this;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putBoolean("lastPowered", this.lastPowered);
        if (!this.trySaveLootTable(tag) && !this.item.isEmpty())
            tag.put("item", this.item.save(provider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.lastPowered = tag.getBoolean("lastPowered");
        if (!this.tryLoadLootTable(tag)) {
            if (tag.contains("item", 10))
                this.item = ItemStack.parse(provider, tag.getCompound("item")).orElse(ItemStack.EMPTY);
            else this.item = ItemStack.EMPTY;
        }
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.item = input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    public void setFromItem(ItemStack stack) {
        this.applyComponentsFromItemStack(stack);
    }

    @Override
    public void setTheItem(ItemStack stack) {
        this.unpackLootTable(null);
        this.item = stack;
    }

    @NotNull
    @Override
    public ItemStack getTheItem() {
        this.unpackLootTable(null);
        return this.item;
    }

    @NotNull
    @Override
    public ItemStack splitTheItem(int splitAmt) {
        this.unpackLootTable(null);
        ItemStack itemstack = this.item.split(splitAmt);

        if (this.item.isEmpty())
            this.item = ItemStack.EMPTY;

        return itemstack;
    }

    public boolean hasItems() {
        return !this.item.isEmpty();
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> getLootTable() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> lootTable) {
        this.lootTable = lootTable;
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
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("item");
    }

    @Override
    public void setChanged() {
        if (this.level != null && this.level.getBlockState(this.getBlockPos()).getBlock() instanceof QuestionBlock) {
            Level world = this.level;
            BlockState state = world.getBlockState(this.getBlockPos());

            if (state.hasProperty(InvisibleQuestionBlock.INVISIBLE) && (this.getLootTable() != null || this.hasItems()))
                world.setBlock(this.getBlockPos(), this.getBlockState().setValue(QuestionBlock.EMPTY, Boolean.FALSE).setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.TRUE), 3);
            else if (this.getLootTable() != null || this.hasItems())
                world.setBlock(this.getBlockPos(), this.getBlockState().setValue(QuestionBlock.EMPTY, Boolean.FALSE), 3);
            else world.setBlock(this.getBlockPos(), this.getBlockState().setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);

            if (!world.isClientSide()) {
                world.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
                world.updateNeighborsAt(this.getBlockPos(), this.getBlockState().getBlock());
            }
        }
        super.setChanged();
    }

    public boolean isLastPowered() {
        return this.lastPowered;
    }

    public void setLastPowered(boolean powered) {
        this.lastPowered = powered;
    }
}
