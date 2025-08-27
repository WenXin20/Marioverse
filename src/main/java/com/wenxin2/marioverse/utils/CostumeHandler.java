package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.power_ups.AbstractPowerUpEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface CostumeHandler {
    default boolean mv$hasMarioCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.MARIO_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.MARIO_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.MARIO_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.MARIO_COSTUMES))
            return true;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            if (containerHat != null && containerShirt != null && containerPants != null && containerShoes != null) {
                ItemStack stackHat = containerHat.getAccessories().getItem(0);
                ItemStack stackShirt = containerShirt.getAccessories().getItem(0);
                ItemStack stackPants = containerPants.getAccessories().getItem(0);
                ItemStack stackShoes = containerShoes.getAccessories().getItem(0);
                return stackHat.is(TagRegistry.MARIO_COSTUMES) && stackShirt.is(TagRegistry.MARIO_COSTUMES)
                        && stackPants.is(TagRegistry.MARIO_COSTUMES) && stackShoes.is(TagRegistry.MARIO_COSTUMES);
            }
        }
        return false;
    }

    default boolean mv$hasLuigiCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.LUIGI_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.LUIGI_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.LUIGI_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.LUIGI_COSTUMES))
            return true;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            if (containerHat != null && containerShirt != null && containerPants != null && containerShoes != null) {
                ItemStack stackHat = containerHat.getAccessories().getItem(0);
                ItemStack stackShirt = containerShirt.getAccessories().getItem(0);
                ItemStack stackPants = containerPants.getAccessories().getItem(0);
                ItemStack stackShoes = containerShoes.getAccessories().getItem(0);
                return stackHat.is(TagRegistry.LUIGI_COSTUMES) && stackShirt.is(TagRegistry.LUIGI_COSTUMES)
                        && stackPants.is(TagRegistry.LUIGI_COSTUMES) && stackShoes.is(TagRegistry.LUIGI_COSTUMES);
            }
        }
        return false;
    }

    default boolean mv$hasPeachCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.PEACH_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.PEACH_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.PEACH_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.PEACH_COSTUMES))
            return true;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            if (containerHat != null && containerShirt != null && containerPants != null && containerShoes != null) {
                ItemStack stackHat = containerHat.getAccessories().getItem(0);
                ItemStack stackShirt = containerShirt.getAccessories().getItem(0);
                ItemStack stackPants = containerPants.getAccessories().getItem(0);
                ItemStack stackShoes = containerShoes.getAccessories().getItem(0);
                return stackHat.is(TagRegistry.PEACH_COSTUMES) && stackShirt.is(TagRegistry.PEACH_COSTUMES)
                        && stackPants.is(TagRegistry.PEACH_COSTUMES) && stackShoes.is(TagRegistry.PEACH_COSTUMES);
            }
        }
        return false;
    }

    default void applyCostumeChange(LivingEntity entity, AbstractPowerUpEntity powerUp) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability != null) {
            if (entity instanceof Player && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get())
                this.updateCostume(entity, powerUp, capability);
            else if (!(entity instanceof Player) && ConfigRegistry.EQUIP_COSTUMES_MOBS.get())
                this.updateCostume(entity, powerUp, capability);
        }
    }

    default void updateCostume(LivingEntity entity, AbstractPowerUpEntity powerUp, AccessoriesCapability capability) {
        AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
        AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
        AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
        AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

        int randomIndex = (int) (Math.random() * powerUp.getHatItems().size());

        if (entity.getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor()) continue;
                ItemStack currentStack = entity.getItemBySlot(slot);

                switch (slot) {
                    case HEAD -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.HEAD);
                        ItemStack newStack = powerUp.getHatItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getHatItems(), newStack, currentStack);
                    }
                    case CHEST -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.CHEST);
                        ItemStack newStack = powerUp.getShirtItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getShirtItems(), newStack, currentStack);
                    }
                    case LEGS -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.LEGS);
                        ItemStack newStack = powerUp.getPantsItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getPantsItems(), newStack, currentStack);
                    }
                    case FEET -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.FEET);
                        ItemStack newStack = powerUp.getShoesItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getShoesItems(), newStack, currentStack);
                    }
                }
            }
        }

        if (containerHat != null && !containerHat.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerHat.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.HEAD) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getHatItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getHatItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerHat.getAccessories().setItem(0, newStack);
        }

        if (containerShirt != null && !containerShirt.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerShirt.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.BODY) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getShirtItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getShirtItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerShirt.getAccessories().setItem(0, newStack);
        }

        if (containerPants != null && !containerPants.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerPants.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.LEGS) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getPantsItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getPantsItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerPants.getAccessories().setItem(0, newStack);
        }

        if (containerShoes != null && !containerShoes.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerShoes.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.FEET) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getShoesItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getShoesItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerShoes.getAccessories().setItem(0, newStack);
        }
    }

    default ItemStack equipCostumesInAccessorySlots(AbstractPowerUpEntity powerUp, ItemStack item, ItemStack stackArmor, ItemStack newStack, ItemStack stack) {
        if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stack.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stack.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stack.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        }
        return newStack;
    }

    default void equipCostumesInArmorSlots(LivingEntity entity, AbstractPowerUpEntity powerUp, EquipmentSlot slot, ItemStack stackArmor, List<ItemStack> costumeList, ItemStack newStack, ItemStack currentStack) {
        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES)) {
            for (ItemStack item : costumeList) {
                if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(powerUp.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(powerUp.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(powerUp.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(currentStack.getComponents());
            entity.setItemSlot(slot, newStack);
        }
    }
}