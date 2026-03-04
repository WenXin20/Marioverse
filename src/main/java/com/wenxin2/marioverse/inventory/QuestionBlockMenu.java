package com.wenxin2.marioverse.inventory;

import com.wenxin2.marioverse.registries.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class QuestionBlockMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final ContainerData data;
    protected final Player player;

    public QuestionBlockMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1), new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }

    public QuestionBlockMenu(int id, Inventory inventory, Container container, ContainerData data, ContainerLevelAccess access) {
        super(MenuRegistry.QUESTION_BLOCK_MENU.get(), id);
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
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            stackCopy = stack.copy();

            if (index == 0) {
                if (!this.moveItemStackTo(stack, 1, 37, true))
                    return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stack, 0, 1, false))
                    return ItemStack.EMPTY;
            }

            if (stack.isEmpty())
                slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return stackCopy;
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

        this.addSlot(new Slot(container, 0, 26, 23));
    }

    public ContainerLevelAccess getAccess() {
        return this.access;
    }

    public int getRefillCountdown() {
        return this.data.get(0);
    }

    public int getTimeUnit() {
        return this.data.get(1);
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