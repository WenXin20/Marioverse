package com.wenxin2.marioverse.inventory;

import com.mojang.datafixers.util.Pair;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.inventory.slots.GhostSlot;
import com.wenxin2.marioverse.registries.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BlockSpawnerMenu extends AbstractContainerMenu {
    static final ResourceLocation EMPTY_SLOT_BLOCK = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "gui/slot/empty_block_slot");
    static final ResourceLocation EMPTY_SLOT_DISGUISE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "gui/slot/empty_disguise_slot");
    private final ContainerLevelAccess access;
    private final ContainerData data;
    protected final Player player;

    public BlockSpawnerMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(2), new SimpleContainerData(5), ContainerLevelAccess.NULL);
    }

    public BlockSpawnerMenu(int id, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(MenuRegistry.BLOCK_SPAWNER_MENU.get(), id);
        this.access = access;
        this.data = data;
        this.player = inventory.player;

        this.addDataSlots(data);
        this.createInventorySlots(inventory, container);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @NotNull
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);

        if (slot instanceof GhostSlot ghostSlot) {
            ghostSlot.set(ItemStack.EMPTY);
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();

        if (!stack.isEmpty()) {
            for (Slot slots : this.slots) {
                if (slots instanceof GhostSlot ghostSlot && ghostSlot.mayPlace(stack)) {
                    ItemStack ghost = stack.copy();
                    ghost.setCount(1);

                    ghostSlot.set(ghost);
                    return ItemStack.EMPTY;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slot, int button, ClickType clickType, Player player) {
        if (slot >= 0 && this.slots.get(slot) instanceof GhostSlot ghostSlot) {
            ItemStack carried = this.getCarried();

            if (!carried.isEmpty() && ghostSlot.mayPlace(carried)) {
                ItemStack ghostStack = carried.copy();
                ghostStack.setCount(1);
                ghostSlot.set(ghostStack);
                return;
            }

            if (carried.isEmpty()) {
                ghostSlot.set(ItemStack.EMPTY);
                return;
            }
        }

        super.clicked(slot, button, clickType, player);
    }

    private void createInventorySlots(Inventory inventory, Container container) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }

        this.addSlot(new GhostSlot(container, 1, 8, 22) {
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_BLOCK);
            }
        });

        this.addSlot(new GhostSlot(container, 0, 8, 48) {
            @Override
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_SLOT_DISGUISE);
            }
        });
    }

    public ContainerLevelAccess getAccess() {
        return this.access;
    }

    public int getRefillCountdown() {
        return this.data.get(0);
    }

    public int getActiveRefillCountdown() {
        return this.data.get(1);
    }

    public int getTimeUnit() {
        return this.data.get(2);
    }

    public int getPlaceDirection() {
        return this.data.get(3);
    }

    public int getPlaceOffset() {
        return this.data.get(4);
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

    public void playSound(SoundEvent soundEvent) {
        if (Minecraft.getInstance().player != null) {
            float pitch = 0.9F + Minecraft.getInstance().player.clientLevel.random.nextFloat() * 0.2F;
            Minecraft.getInstance().player.playSound(soundEvent, 1.0F, pitch);
        }
    }
}