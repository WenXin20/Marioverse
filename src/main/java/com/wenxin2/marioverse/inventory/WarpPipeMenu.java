package com.wenxin2.marioverse.inventory;

import com.wenxin2.marioverse.registries.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WarpPipeMenu extends AbstractContainerMenu {
    public static final int DATA_COUNT = 11;
    public static final int WARP_PIPE_TAB = 0;
    public static final int WATERWORKS_TAB = 1;
    public static final int PAYMENTS_TAB = 2;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final BlockPos blockPos;
    private final DataSlot canManagePayments = DataSlot.standalone();

    public WarpPipeMenu(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        this(id, inventory, new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL, buf.readBlockPos(), buf.readBoolean());
        this.data.set(10, buf.readVarInt());
    }

    public WarpPipeMenu(int id, Inventory inventory, ContainerData data, final ContainerLevelAccess levelAccess,
                        BlockPos blockPos, boolean canManagePayments) {
        super(MenuRegistry.WARP_PIPE_MENU.get(), id);
        this.access = levelAccess;
        this.data = data;
        this.blockPos = blockPos;
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
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public ContainerLevelAccess getAccess() {
        return this.access;
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
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
}