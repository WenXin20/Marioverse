package com.wenxin2.marioverse.inventory;

import com.mojang.datafixers.util.Pair;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.slots.GhostSlot;
import com.wenxin2.marioverse.registries.MenuRegistry;
import com.wenxin2.marioverse.utils.WarpCost;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WarpPipeMenu extends AbstractContainerMenu {
    private static final ResourceLocation EMPTY_COIN_SLOT = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "slot/empty_coin_slot");
    public static final int COST_SLOT_X = 38;
    public static final int COST_SLOT_Y = 22;
    public static final int DATA_COUNT = 11;
    public static final int WARP_PIPE_TAB = 0;
    public static final int WATERWORKS_TAB = 1;
    public static final int PAYMENTS_TAB = 2;
    private final ContainerLevelAccess access;
    private final Slot costSlot;
    private final ContainerData data;
    private final BlockPos blockPos;
    @Nullable private final BlockPos destinationPos;
    private final DataSlot canManagePayments = DataSlot.standalone();
    private int configXpCost;

    public WarpPipeMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(id, inventory, new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL,
                buf.readBlockPos(), buf.readOptional(BlockPos.STREAM_CODEC).orElse(null), new SimpleContainer(1) {
                    @Override
                    public int getMaxStackSize(ItemStack stack) {
                        return Integer.MAX_VALUE;
                    }
                }, buf.readBoolean());
        this.data.set(10, buf.readVarInt());
        this.configXpCost = buf.readVarInt();
    }

    public WarpPipeMenu(int id, Inventory inventory, ContainerData data, final ContainerLevelAccess levelAccess,
                        BlockPos blockPos, @Nullable BlockPos destinationPos, Container costContainer, boolean canManagePayments) {
        super(MenuRegistry.WARP_PIPE_MENU.get(), id);
        this.access = levelAccess;
        this.blockPos = blockPos;
        this.data = data;
        this.destinationPos = destinationPos;
        this.canManagePayments.set(canManagePayments ? 1 : 0);

        this.addDataSlots(data);
        this.addDataSlot(this.canManagePayments);

        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 142));
        }

        this.costSlot = this.addSlot(new GhostSlot(costContainer, 0, COST_SLOT_X, COST_SLOT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return WarpPipeMenu.this.canEditCost();
            }

            @Override
            public void set(ItemStack stack) {
                this.container.setItem(this.getContainerSlot(), stack);
                this.setChanged();
            }

            @Override
            public boolean isActive() {
                return WarpPipeMenu.this.getMenuType() == WARP_PIPE_TAB;
            }

            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_COIN_SLOT);
            }
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId < 0 || this.slots.get(slotId) != this.costSlot) {
            super.clicked(slotId, button, clickType, player);
            return;
        }

        if (!this.canEditCost() || (clickType != ClickType.PICKUP && clickType != ClickType.QUICK_MOVE))
            return;

        ItemStack stack = button == 1 || clickType == ClickType.QUICK_MOVE ? ItemStack.EMPTY
                : new ItemStack(this.getCarried().getItem(), this.getCarried().getCount());
        this.access.execute((level, pos) -> {
            if (level.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE && !pipeBE.canChangeCostItem(stack))
                player.displayClientMessage(Component.translatable("display.marioverse.warp_cost.collect_payments_first")
                        .withStyle(ChatFormatting.RED), true);
        });
        this.costSlot.set(stack);
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot != this.costSlot && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public boolean canDragTo(Slot slot) {
        return slot != this.costSlot && super.canDragTo(slot);
    }

    public ContainerLevelAccess getAccess() {
        return this.access;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Nullable
    public BlockPos getDestinationPos() {
        return this.destinationPos;
    }

    public int getConfigXpCost() {
        return this.configXpCost;
    }

    public boolean isClosed() {
        return this.data.get(0) == 1;
    }

    public boolean hasWaterSpout() {
        return this.data.get(1) == 1;
    }

    public boolean hasBubbles() {
        return this.data.get(2) == 1;
    }

    public boolean isWaxed() {
        return this.data.get(3) == 1;
    }

    public int getSpoutHeight() {
        return this.data.get(4);
    }

    public int getBubblesDistance() {
        return this.data.get(5);
    }

    public boolean needsWaterlogging() {
        return this.data.get(6) == 1;
    }

    public boolean requiresWaterSource() {
        return this.data.get(7) == 1;
    }

    public boolean requiresConnection() {
        return this.data.get(8) == 1;
    }

    public boolean isClearPipe() {
        return this.data.get(9) == 1;
    }

    public int getMenuType() {
        int menuType = this.data.get(10);
        return menuType == PAYMENTS_TAB && !this.canManagePayments() ? WARP_PIPE_TAB : menuType;
    }

    public boolean canManagePayments() {
        return this.canManagePayments.get() == 1;
    }

    public boolean canEditCost() {
        return this.canManagePayments() && !WarpCost.hasConfigPipeCost();
    }

    public Slot getCostSlot() {
        return this.costSlot;
    }
}