package com.wenxin2.marioverse.blocks.entities;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.properties.BlockStatePropertyRegistry;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
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

public class BlockSpawnerBlockEntity extends DisguisedBlockEntity implements MenuProvider, Nameable, RandomizableContainer {
    private static final Component DEFAULT_NAME = Component.translatable("menu.marioverse.block_spawner");
    public static final String CUSTOM_NAME = "CustomName";
    @Nullable public Component name;
    @Nullable protected ResourceKey<LootTable> lootTable;
    private ItemStack ghostStack = ItemStack.EMPTY;
    private boolean lastPowered;
    private int activeRefillCountdown = -1;
    protected long lootTableSeed;
    private BlockPos targetPos;

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BlockSpawnerBlockEntity.this.getRefillCountdown();
                case 1 -> BlockSpawnerBlockEntity.this.getActiveRefillCountdown();
                case 2 -> BlockSpawnerBlockEntity.this.getTimeUnit();
                case 3 -> BlockSpawnerBlockEntity.this.getPlacementDirection();
                case 4 -> BlockSpawnerBlockEntity.this.getPlacementOffset();
                case 5 -> BlockSpawnerBlockEntity.this.getMenuType();
                case 6 -> BlockSpawnerBlockEntity.this.getBlockFace();
                case 7 -> BlockSpawnerBlockEntity.this.isUnbreakable();
                case 8 -> BlockSpawnerBlockEntity.this.isInteractable();
                case 9 -> BlockSpawnerBlockEntity.this.isRightClickable();
                case 10 -> BlockSpawnerBlockEntity.this.hasCollision();
                case 11 -> BlockSpawnerBlockEntity.this.isSneaking();
                case 12 -> BlockSpawnerBlockEntity.this.getFacingDirection();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> BlockSpawnerBlockEntity.this.setRefillCountdown(value);
                case 1 -> BlockSpawnerBlockEntity.this.setTimeUnit(value);
                case 3 -> BlockSpawnerBlockEntity.this.setPlacementDirection(value);
                case 4 -> BlockSpawnerBlockEntity.this.setPlacementOffset(value);
                case 5 -> BlockSpawnerBlockEntity.this.setMenuType(value);
                case 6 -> BlockSpawnerBlockEntity.this.setBlockFace(value);
                case 7 -> BlockSpawnerBlockEntity.this.setUnbreakable(value);
                case 8 -> BlockSpawnerBlockEntity.this.setInteractable(value);
                case 9 -> BlockSpawnerBlockEntity.this.setRightClickable(value);
                case 10 -> BlockSpawnerBlockEntity.this.setCollision(value);
                case 11 -> BlockSpawnerBlockEntity.this.setSneaking(value);
                case 12 -> BlockSpawnerBlockEntity.this.setFacingDirection(value);
            }
        }

        @Override // Increase this with ContainerData above
        public int getCount() {
            return 13;
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
        this.setData(DataAttachmentRegistry.BLOCK_FACE.get(), input.getOrDefault(DataComponentRegistry.BLOCK_FACE.get(), 0));
        this.setData(DataAttachmentRegistry.FACING_DIRECTION.get(), input.getOrDefault(DataComponentRegistry.FACING_DIRECTION.get(), 2));
        this.setData(DataAttachmentRegistry.HAS_COLLISION.get(), input.getOrDefault(DataComponentRegistry.HAS_COLLISION.get(), true));
        this.setData(DataAttachmentRegistry.IS_INTERACTABLE.get(), input.getOrDefault(DataComponentRegistry.IS_INTERACTABLE.get(), false));
        this.setData(DataAttachmentRegistry.IS_RIGHT_CLICKABLE.get(), input.getOrDefault(DataComponentRegistry.IS_RIGHT_CLICKABLE.get(), false));
        this.setData(DataAttachmentRegistry.IS_SNEAKING.get(), input.getOrDefault(DataComponentRegistry.IS_SNEAKING.get(), false));
        this.setData(DataAttachmentRegistry.IS_UNBREAKABLE.get(), input.getOrDefault(DataComponentRegistry.IS_UNBREAKABLE.get(), true));
        this.setData(DataAttachmentRegistry.MENU_TYPE.get(), input.getOrDefault(DataComponentRegistry.MENU_TYPE.get(), 0));
        this.setData(DataAttachmentRegistry.PLACEMENT_DIRECTION.get(), input.getOrDefault(DataComponentRegistry.PLACEMENT_DIRECTION.get(), 0));
        this.setData(DataAttachmentRegistry.PLACEMENT_OFFSET.get(), input.getOrDefault(DataComponentRegistry.PLACEMENT_OFFSET.get(), 1));
        this.setData(DataAttachmentRegistry.REFILL_COUNTDOWN.get(), input.getOrDefault(DataComponentRegistry.REFILL_COUNTDOWN.get(), -1));
        this.setData(DataAttachmentRegistry.REFILL_TIME_UNIT.get(), input.getOrDefault(DataComponentRegistry.REFILL_TIME_UNIT.get(), 0));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(this.ghostStack)));
        builder.set(DataComponents.CUSTOM_NAME, this.name);
        builder.set(DataComponentRegistry.BLOCK_FACE.get(), this.getData(DataAttachmentRegistry.BLOCK_FACE.get()));
        builder.set(DataComponentRegistry.FACING_DIRECTION.get(), this.getData(DataAttachmentRegistry.FACING_DIRECTION.get()));
        builder.set(DataComponentRegistry.HAS_COLLISION.get(), this.getData(DataAttachmentRegistry.HAS_COLLISION.get()));
        builder.set(DataComponentRegistry.IS_INTERACTABLE.get(), this.getData(DataAttachmentRegistry.IS_INTERACTABLE.get()));
        builder.set(DataComponentRegistry.IS_RIGHT_CLICKABLE.get(), this.getData(DataAttachmentRegistry.IS_RIGHT_CLICKABLE.get()));
        builder.set(DataComponentRegistry.IS_SNEAKING.get(), this.getData(DataAttachmentRegistry.IS_SNEAKING.get()));
        builder.set(DataComponentRegistry.IS_UNBREAKABLE.get(), this.getData(DataAttachmentRegistry.IS_UNBREAKABLE.get()));
        builder.set(DataComponentRegistry.MENU_TYPE.get(), this.getData(DataAttachmentRegistry.MENU_TYPE.get()));
        builder.set(DataComponentRegistry.PLACEMENT_DIRECTION.get(), this.getData(DataAttachmentRegistry.PLACEMENT_DIRECTION.get()));
        builder.set(DataComponentRegistry.PLACEMENT_OFFSET.get(), this.getData(DataAttachmentRegistry.PLACEMENT_OFFSET.get()));
        builder.set(DataComponentRegistry.REFILL_COUNTDOWN.get(), this.getData(DataAttachmentRegistry.REFILL_COUNTDOWN.get()));
        builder.set(DataComponentRegistry.REFILL_TIME_UNIT.get(), this.getData(DataAttachmentRegistry.REFILL_TIME_UNIT.get()));
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
            return super.getItem(slot);
        if (slot == 1)
            return this.ghostStack;

        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItem(int slot, int amount) {
        this.unpackLootTable(null);

        if (slot == 0)
            return super.removeItem(slot, amount);

        if (slot == 1) {
            ItemStack stack = this.ghostStack;
            this.ghostStack = ItemStack.EMPTY;

            if (this.level != null) {
                BlockState state = this.level.getBlockState(this.getBlockPos());

                if (state.hasProperty(BlockStatePropertyRegistry.INVISIBLE))
                    this.level.setBlock(this.getBlockPos(), state.setValue(BlockStatePropertyRegistry.INVISIBLE, !stack.isEmpty()), 3);
            }

            return stack;
        }

        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot == 0)
            return super.removeItemNoUpdate(slot);

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
            super.setItem(slot, stack);
            return;
        }

        if (slot == 1) {
            this.ghostStack = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1);

            if (this.level != null) {
                BlockState state = this.level.getBlockState(this.getBlockPos());

                if (state.hasProperty(BlockStatePropertyRegistry.INVISIBLE))
                    this.level.setBlock(this.getBlockPos(), state.setValue(BlockStatePropertyRegistry.INVISIBLE, !stack.isEmpty()), 3);
            }
        }

        this.setChanged();
    }

    @Override
    public void clearContent() {
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.ghostStack = ItemStack.EMPTY;
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
        return this.getData(DataAttachmentRegistry.REFILL_COUNTDOWN);
    }

    public void setRefillCountdown(int refillCountdown) {
        this.setData(DataAttachmentRegistry.REFILL_COUNTDOWN, refillCountdown);
        this.setChanged();
    }

    private void startRefillCountdown() {
        int refillCountdown = this.getRefillCountdown();

        if (refillCountdown >= 0)
            this.activeRefillCountdown = refillCountdown;
    }

    public int getPlacementDirection() {
        return this.getData(DataAttachmentRegistry.PLACEMENT_DIRECTION);
    }

    public void setPlacementDirection(int placementDirection) {
        this.setData(DataAttachmentRegistry.PLACEMENT_DIRECTION, placementDirection);
        this.setChanged();
    }

    public int getBlockFace() {
        return this.getData(DataAttachmentRegistry.BLOCK_FACE);
    }

    public void setBlockFace(int blockFace) {
        this.setData(DataAttachmentRegistry.BLOCK_FACE, blockFace);
        this.setChanged();
    }

    public int getPlacementOffset() {
        return this.getData(DataAttachmentRegistry.PLACEMENT_OFFSET);
    }

    public void setPlacementOffset(int placementOffset) {
        this.setData(DataAttachmentRegistry.PLACEMENT_OFFSET, placementOffset);
        this.setChanged();
    }

    public int getTimeUnit() {
        return this.getData(DataAttachmentRegistry.REFILL_TIME_UNIT);
    }

    public void setTimeUnit(int timeUnit) {
        this.setData(DataAttachmentRegistry.REFILL_TIME_UNIT, timeUnit);
        this.setChanged();
    }

    public int getMenuType() {
        return this.getData(DataAttachmentRegistry.MENU_TYPE);
    }

    public void setMenuType(int menuType) {
        this.setData(DataAttachmentRegistry.MENU_TYPE, menuType);
        this.setChanged();
    }

    public int isUnbreakable() {
        return this.getData(DataAttachmentRegistry.IS_UNBREAKABLE) ? 1 : 0;
    }

    public void setUnbreakable(int isUnbreakable) {
        if (isUnbreakable == 1)
            this.setData(DataAttachmentRegistry.IS_UNBREAKABLE, true);
        else this.setData(DataAttachmentRegistry.IS_UNBREAKABLE, false);
        this.setChanged();
    }

    public int isInteractable() {
        return this.getData(DataAttachmentRegistry.IS_INTERACTABLE) ? 1 : 0;
    }

    public void setInteractable(int isInteractable) {
        if (isInteractable == 1)
            this.setData(DataAttachmentRegistry.IS_INTERACTABLE, true);
        else this.setData(DataAttachmentRegistry.IS_INTERACTABLE, false);
        this.setChanged();
    }

    public int isRightClickable() {
        return this.getData(DataAttachmentRegistry.IS_RIGHT_CLICKABLE) ? 1 : 0;
    }

    public void setRightClickable(int isRightClickable) {
        if (isRightClickable == 1)
            this.setData(DataAttachmentRegistry.IS_RIGHT_CLICKABLE, true);
        else this.setData(DataAttachmentRegistry.IS_RIGHT_CLICKABLE, false);
        this.setChanged();
    }

    public int hasCollision() {
        return this.getData(DataAttachmentRegistry.HAS_COLLISION) ? 1 : 0;
    }

    public void setCollision(int hasCollision) {
        if (hasCollision == 1)
            this.setData(DataAttachmentRegistry.HAS_COLLISION, true);
        else this.setData(DataAttachmentRegistry.HAS_COLLISION, false);
        this.setChanged();
    }

    public int isSneaking() {
        return this.getData(DataAttachmentRegistry.IS_SNEAKING.get()) ? 1 : 0;
    }

    public void setSneaking(int isSneaking) {
        if (isSneaking == 1)
            this.setData(DataAttachmentRegistry.IS_SNEAKING, true);
        else this.setData(DataAttachmentRegistry.IS_SNEAKING, false);
        this.setChanged();
    }

    public int getFacingDirection() {
        return this.getData(DataAttachmentRegistry.FACING_DIRECTION);
    }

    public void setFacingDirection(int facingDirection) {
        this.setData(DataAttachmentRegistry.FACING_DIRECTION, facingDirection);
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
        if (!(this.level instanceof ServerLevel serverLevel))
            return;

        Direction blockFace = directionFromIndex(this.getBlockFace());
        Direction fakePlayerDirection = directionFromIndex(this.getFacingDirection());
        Direction placementDirection = directionFromIndex(this.getPlacementDirection());
        BlockPos posOffset = this.worldPosition.relative(placementDirection, this.getPlacementOffset());
        ItemStack stack = this.ghostStack.copy();
        Item item = stack.getItem();
        boolean placed = false;
        var fakePlayer = FakePlayerFactory.getMinecraft(serverLevel);

        float yaw = switch (fakePlayerDirection) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case WEST  -> 90f;
            case EAST  -> -90f;
            default    -> fakePlayer.getYRot();
        };

        float pitch = switch (fakePlayerDirection) {
            case UP   -> -90f;
            case DOWN -> 90f;
            default   -> 0f;
        };

        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        fakePlayer.setPos(Vec3.atCenterOf(this.worldPosition));
        fakePlayer.setShiftKeyDown(this.isSneaking() == 1);
        fakePlayer.setYRot(yaw);
        fakePlayer.setXRot(pitch);
        fakePlayer.setYHeadRot(yaw);
        fakePlayer.setYBodyRot(yaw);

        if (item instanceof BlockItem blockItem) {
            Vec3 hitVec = Vec3.atCenterOf(posOffset).relative(blockFace, 0.5);
            UseOnContext useContext = new UseOnContext(fakePlayer, InteractionHand.MAIN_HAND,
                    new BlockHitResult(hitVec, blockFace, posOffset, false));

            BlockPlaceContext placeContext = new BlockPlaceContext(useContext);
            placed = blockItem.place(placeContext).consumesAction();
        } else if (item instanceof BucketItem bucketItem)
            placed = bucketItem.emptyContents(fakePlayer, this.level, posOffset, null, stack);

        if (placed) {
            this.targetPos = posOffset;
            BlockState stateOffset = this.level.getBlockState(posOffset);

            if (stateOffset.getBlock() instanceof CoinBlock)
                ServerParticleUtils.spawnParticlesOnBlockFaces(ParticleRegistry.COIN_GLINT.get(), serverLevel, posOffset, UniformInt.of(2, 5));
            else serverLevel.levelEvent(2001, posOffset, Block.getId(stateOffset));
        }

        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
    }

    public void updateTargetTracking() {
        if (this.level == null) return;

        Direction dir = directionFromIndex(this.getPlacementDirection());
        BlockPos posOffset = this.worldPosition.relative(dir, this.getPlacementOffset());

        if (this.ghostStack.getItem() instanceof BlockItem || this.ghostStack.getItem() instanceof BucketItem)
            this.targetPos = posOffset;
    }

    public void checkTargetBlock() {
        if (this.level == null || this.targetPos == null)
            return;

        if (this.level.getBlockState(this.targetPos).isAir() && this.activeRefillCountdown == -1)
            this.startRefillCountdown();
    }
}