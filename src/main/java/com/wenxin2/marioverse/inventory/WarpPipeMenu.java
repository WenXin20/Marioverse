package com.wenxin2.marioverse.inventory;

import com.wenxin2.marioverse.init.ModRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class WarpPipeMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public BlockPos pos;

    public WarpPipeMenu(int id, Inventory inventory) {
        this(id, inventory, ContainerLevelAccess.NULL);
    }

    public WarpPipeMenu(int id, Inventory inventory, final ContainerLevelAccess levelAccess, final BlockPos pos)
    {
        super(ModRegistry.WARP_PIPE_MENU.get(), id);
        this.access = levelAccess;
        this.pos = pos;

        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(inventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(inventory, l, 8 + l * 18, 142));
        }
    }

    public WarpPipeMenu(int id, Inventory inventory, final ContainerLevelAccess levelAccess)
    {
        super(ModRegistry.WARP_PIPE_MENU.get(), id);
        this.access = levelAccess;

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

    public BlockPos getBlockPos() {
        return this.pos;
    }
}
