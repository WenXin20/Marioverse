package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperInvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperQuestionBlock;
import com.wenxin2.marioverse.blocks.WeatheringCopperStorageBrickBlock;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.registries.BlockEntityRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;
import org.jetbrains.annotations.NotNull;

public class QuestionBlockEntity extends BlockEntity implements MenuProvider, Nameable, RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {
    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.question_block");
    public static final String CUSTOM_NAME = "CustomName";
    @Nullable private ResourceKey<LootTable> refillLootTable;
    @Nullable protected ResourceKey<LootTable> lootTable;
    @Nullable public Component name;
    private ItemStack item = ItemStack.EMPTY;
    private ItemStack refillTemplate = ItemStack.EMPTY;
    private boolean lastPowered;
    private int activeRefillCountdown = -1;
    protected long lootTableSeed;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> QuestionBlockEntity.this.getRefillCountdown();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int refillCountdown) {
            if (index == 0) {
                QuestionBlockEntity.this.setRefillCountdown(refillCountdown);
            }
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public QuestionBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityRegistry.QUESTION_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return 1;
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return this.getType().isValid(state) || state.getBlock() instanceof QuestionBlock
                || state.getBlock() instanceof InvisibleQuestionBlock
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

    @NotNull
    @Override
    public Component getName() {
        return DEFAULT_NAME;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putBoolean("lastPowered", this.lastPowered);
        if (!this.trySaveLootTable(tag) && !this.item.isEmpty())
            tag.put("item", this.item.save(provider));

        if (this.name != null)
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));

        if (!this.refillTemplate.isEmpty())
            tag.put("refillTemplate", this.refillTemplate.save(provider));

        if (this.refillLootTable != null)
            tag.putString("refillLootTable", this.refillLootTable.location().toString());
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

        if (tag.contains(CUSTOM_NAME, 8))
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);

        if (tag.contains("RefillTemplate", 10))
            this.refillTemplate = ItemStack.parse(provider, tag.getCompound("refillTemplate"))
                    .orElse(ItemStack.EMPTY);

        if (tag.contains("RefillLootTable"))
            this.refillLootTable = ResourceKey.create(Registries.LOOT_TABLE,
                    ResourceLocation.parse(tag.getString("refillLootTable")));
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.item = input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
        this.name = input.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.item)));
        builder.set(DataComponents.CUSTOM_NAME, this.name);
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    public void setCustomName(Component name) {
        this.name = name;
        this.getUpdatePacket();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, QuestionBlockEntity blockEntity) {
        if (level.isClientSide) return;

        if (blockEntity.activeRefillCountdown > 0)
            blockEntity.activeRefillCountdown--;
        else if (blockEntity.activeRefillCountdown == 0) {
            blockEntity.refill();
            blockEntity.activeRefillCountdown = -1;
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if (this.level != null)
            return new QuestionBlockMenu(id, inventory, new SimpleContainer(1), new SimpleContainerData(3), ContainerLevelAccess.create(this.getLevel(), this.getBlockPos()));
        else return null;
    }

    public void setFromItem(ItemStack stack) {
        this.applyComponentsFromItemStack(stack);
    }

    @Override
    public void setTheItem(ItemStack stack) {
        this.unpackLootTable(null);
        this.item = stack;

        if (!stack.isEmpty()) {
            this.refillTemplate = stack.copy();
            this.refillLootTable = null;
        }

        this.setChanged();
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

        if (this.item.isEmpty()) {
            this.item = ItemStack.EMPTY;
            this.startRefillCountdown();
        }

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

        if (lootTable != null) {
            this.refillLootTable = lootTable;
            this.refillTemplate = ItemStack.EMPTY;
        }

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
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("item");
    }

    @Override
    public void setChanged() {
        if (this.level != null && this.level.getBlockState(this.getBlockPos()).getBlock() instanceof QuestionBlock) {
            Level world = this.level;
            BlockState state = world.getBlockState(this.getBlockPos());

            if (state.hasProperty(InvisibleQuestionBlock.INVISIBLE) && (this.getLootTable() != null))
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

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    public int getRefillCountdown() {
        return this.getData(DataAttachmentRegistry.REFILL_COUNTDOWN.get());
    }

    public void setRefillCountdown(int refillCountdown) {
        this.setData(DataAttachmentRegistry.REFILL_COUNTDOWN.get(), refillCountdown);
        this.setChanged();
    }

    private void startRefillCountdown() {
        int refillCountdown = this.getRefillCountdown();

        if (refillCountdown >= 0) {
            this.activeRefillCountdown = refillCountdown;
        }
    }

    private void refill() {
        if (!this.item.isEmpty())
            return;

        if (!this.refillTemplate.isEmpty()) {
            this.item = this.refillTemplate.copy();
            this.setChanged();
            return;
        }

        if (this.refillLootTable != null) {
            this.lootTable = this.refillLootTable;
            this.setChanged();
        }
    }
}