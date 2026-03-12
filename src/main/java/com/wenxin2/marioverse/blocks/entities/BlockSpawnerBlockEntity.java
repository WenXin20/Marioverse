package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import org.jetbrains.annotations.NotNull;

public class BlockSpawnerBlockEntity extends DisguiseBlockEntity implements MenuProvider, Nameable, RandomizableContainer {
    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.block_spawner");
    public static final String CUSTOM_NAME = "CustomName";
    @Nullable public Component name;
    @Nullable protected ResourceKey<LootTable> lootTable;
    private ItemStack ghostStack = ItemStack.EMPTY;
    private boolean lastPowered;
    private int activeRefillCountdown = -1;
    private int placeDirection = 0;
    private int placeOffset = 1;
    protected long lootTableSeed;
    private BlockPos targetPos;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BlockSpawnerBlockEntity.this.getRefillCountdown();
                case 1 -> BlockSpawnerBlockEntity.this.getActiveRefillCountdown();
                case 2 -> BlockSpawnerBlockEntity.this.getTimeUnit();
                case 3 -> BlockSpawnerBlockEntity.this.placeDirection;
                case 4 -> BlockSpawnerBlockEntity.this.placeOffset;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> BlockSpawnerBlockEntity.this.setRefillCountdown(value);
                case 1 -> BlockSpawnerBlockEntity.this.setTimeUnit(value);
                case 3 -> BlockSpawnerBlockEntity.this.placeDirection = value;
                case 4 -> BlockSpawnerBlockEntity.this.placeOffset = value;
            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public BlockSpawnerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if (this.level != null)
            return new BlockSpawnerMenu(id, inventory, this,
                    this.getDataAccess(), ContainerLevelAccess.create(this.level, this.getBlockPos()));
        else return null;
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @NotNull
    @Override
    public Component getName() {
        if (this.getCustomName() == null)
            return DEFAULT_NAME;
        else return this.getCustomName();
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
        tag.putInt("activeRefillCountdown", this.activeRefillCountdown);
        tag.putInt("placeDirection", this.placeDirection);
        tag.putInt("placeOffset", this.placeOffset);

        if (!this.trySaveLootTable(tag) && !this.ghostStack.isEmpty())
            tag.put("item", this.ghostStack.save(provider));

        if (this.targetPos != null)
            tag.putLong("targetPos", this.targetPos.asLong());

        if (this.name != null)
            tag.putString(CUSTOM_NAME, Component.Serializer.toJson(this.name, provider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        this.lastPowered = tag.getBoolean("lastPowered");
        this.placeDirection = tag.getInt("placeDirection");
        this.placeOffset = tag.getInt("placeOffset");

        if (!this.tryLoadLootTable(tag)) {
            if (tag.contains("item", 10))
                this.ghostStack = ItemStack.parse(provider, tag.getCompound("item")).orElse(ItemStack.EMPTY);
            else this.ghostStack = ItemStack.EMPTY;
        }

        if (tag.contains(CUSTOM_NAME, 8))
            this.name = parseCustomNameSafe(tag.getString(CUSTOM_NAME), provider);

        if (tag.contains("activeRefillCountdown", 10))
            this.activeRefillCountdown = tag.getInt("activeRefillCountdown");

        if (tag.contains("targetPos", 10))
            this.targetPos = BlockPos.of(tag.getLong("targetPos"));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        this.ghostStack = input.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
        this.name = input.get(DataComponents.CUSTOM_NAME);
        this.setData(DataAttachmentRegistry.REFILL_COUNTDOWN.get(), input.getOrDefault(DataComponentRegistry.REFILL_COUNTDOWN.get(), -1));
        this.setData(DataAttachmentRegistry.REFILL_TIME_UNIT.get(), input.getOrDefault(DataComponentRegistry.REFILL_TIME_UNIT.get(), 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.ghostStack)));
        builder.set(DataComponents.CUSTOM_NAME, this.name);
        builder.set(DataComponentRegistry.REFILL_COUNTDOWN.get(), this.getData(DataAttachmentRegistry.REFILL_COUNTDOWN.get()));
        builder.set(DataComponentRegistry.REFILL_TIME_UNIT.get(), this.getData(DataAttachmentRegistry.REFILL_TIME_UNIT.get()));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveCustomOnly(provider);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.updateTargetTracking();
    }

    public void setCustomName(Component name) {
        this.name = name;
        this.getUpdatePacket();
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

    public static void tick(Level level, BlockPos pos, BlockState state, BlockSpawnerBlockEntity blockEntity) {
        if (level.isClientSide) return;
        blockEntity.checkTargetBlock();

        if (blockEntity.activeRefillCountdown > 0)
            blockEntity.activeRefillCountdown--;
        else if (blockEntity.activeRefillCountdown == 0) {
            blockEntity.placeBlock();
            blockEntity.activeRefillCountdown = -1;
        }
    }

    @Override
    public boolean isEmpty() {
        return this.ghostStack.isEmpty();
    }

    public ItemStack getGhostItem() {
        return ghostStack;
    }

    @NotNull
    @Override
    public ItemStack getItem(int slot) {
        this.unpackLootTable(null);

        if (slot == 0)
            return this.inventory.getStackInSlot(0);
        if (slot == 1)
            return this.ghostStack;

        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);

        if (slot == 0)
            return this.inventory.extractItem(0, amount, false);

        if (slot == 1) {
            ItemStack stack = this.ghostStack;
            this.ghostStack = ItemStack.EMPTY;
            return stack;
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

        if (slot == 1) {
            ItemStack stack = this.ghostStack;
            this.ghostStack = ItemStack.EMPTY;
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

        if (slot == 1)
            this.ghostStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);

        this.setChanged();
    }

    @Override
    public void clearContent() {
        this.ghostStack = ItemStack.EMPTY;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canTakeItem(Container target, int slot, ItemStack stack) {
        return false;
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
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove("item");
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
    }

    public int getActiveRefillCountdown() {
        return this.activeRefillCountdown;
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

        if (refillCountdown >= 0)
            this.activeRefillCountdown = refillCountdown;
    }

    public int getTimeUnit() {
        return this.getData(DataAttachmentRegistry.REFILL_TIME_UNIT);
    }

    public void setTimeUnit(int timeUnit) {
        this.setData(DataAttachmentRegistry.REFILL_TIME_UNIT, timeUnit);
        this.setChanged();
    }

    public int convertToTicks(int time) {
        return switch (this.getTimeUnit()) {
            case 1 -> time * 20;
            case 2 -> time * 20 * 60;
            case 3 -> time * 20 * 60 * 60;
            default -> time;
        };
    }

    public int convertFromTicks(int ticks) {
        return switch (getTimeUnit()) {
            case 1 -> ticks / 20;               // seconds
            case 2 -> ticks / (20 * 60);        // minutes
            case 3 -> ticks / (20 * 60 * 60);   // hours
            default -> ticks;                   // ticks
        };
    }

    private static Direction directionFromIndex(int i) {
        return switch (i) {
            case 1 -> Direction.DOWN;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.EAST;
            case 5 -> Direction.WEST;
            default -> Direction.UP;
        };
    }

    private void placeBlock() {
        if (this.level == null || this.ghostStack.isEmpty())
            return;
        if (!(ghostStack.getItem() instanceof BlockItem blockItem))
            return;
        if (!(this.level instanceof ServerLevel serverLevel))
            return;

        Direction direction = directionFromIndex(placeDirection);
        BlockPos posOffset = this.worldPosition.relative(direction, placeOffset);
        ItemStack stack = this.ghostStack.copy();
        var fakePlayer = FakePlayerFactory.getMinecraft(serverLevel);

        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        fakePlayer.setPos(Vec3.atCenterOf(this.worldPosition));

        if (direction.getAxis().isHorizontal()) {
            float yaw = direction.toYRot();
            fakePlayer.setYRot(yaw);
            fakePlayer.setYHeadRot(yaw);
            fakePlayer.setYBodyRot(yaw);
        }

        UseOnContext useContext = new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(posOffset), Direction.UP, posOffset, false));

        BlockPlaceContext placeContext = new BlockPlaceContext(useContext);
        InteractionResult interactionResult = blockItem.place(placeContext);

        if (interactionResult.consumesAction()) {
            this.targetPos = posOffset;
            BlockState stateOffset = this.level.getBlockState(posOffset);

            if (stateOffset.getBlock() instanceof CoinBlock)
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverLevel, posOffset, UniformInt.of(1, 1));
            else serverLevel.levelEvent(2001, posOffset, Block.getId(stateOffset));
        }

        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    public void updateTargetTracking() {
        if (this.level == null) return;

        Direction dir = directionFromIndex(this.placeDirection);
        BlockPos posOffset = this.worldPosition.relative(dir, this.placeOffset);

        if (this.ghostStack.getItem() instanceof BlockItem)
            this.targetPos = posOffset;
    }

    public void checkTargetBlock() {
        if (this.level == null || this.targetPos == null)
            return;

        if (this.level.getBlockState(this.targetPos).isAir() && this.activeRefillCountdown == -1)
            this.startRefillCountdown();
    }
}