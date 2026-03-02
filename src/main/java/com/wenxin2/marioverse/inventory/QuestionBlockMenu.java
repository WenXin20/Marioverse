package com.wenxin2.marioverse.inventory;

import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.registries.MenuRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.entity.BlockEntity;
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

    public void setRefillCountdown(int refillCountdown) {
        this.getAccess().execute((level, pos) -> {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof QuestionBlockEntity questionBE) {
                questionBE.setRefillCountdown(refillCountdown);
                questionBE.setChanged();
            }
        });
    }

    public void playSound(SoundEvent soundEvent, SoundSource soundSource) {
        this.getAccess().execute((level, pos) -> {
            float pitch = 0.9F + level.random.nextFloat() * 0.2F;
            level.playSound(null, pos, soundEvent, soundSource, 1.0F, pitch);
        });
    }
}