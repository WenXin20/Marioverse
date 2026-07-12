package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.power_ups.AbstractPowerUpEntity;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import java.util.Map;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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

    List<TagKey<Item>> CHARACTER_COSTUME_TAGS =
            List.of(TagRegistry.MARIO_COSTUMES, TagRegistry.LUIGI_COSTUMES, TagRegistry.PEACH_COSTUMES);

    default void updateCostume(LivingEntity entity, AbstractPowerUpEntity powerUp, AccessoriesCapability capability) {
        int randomIndex = (int) (Math.random() * powerUp.getPowerUpHatItems().size());

        if (entity.getType().is(TagRegistry.CAN_WEAR_COSTUMES)) {
            Map<EquipmentSlot, List<ItemStack>> costumeItemsBySlot = Map.of(
                    EquipmentSlot.HEAD, powerUp.getPowerUpHatItems(),
                    EquipmentSlot.CHEST, powerUp.getPowerUpShirtItems(),
                    EquipmentSlot.LEGS, powerUp.getPowerUpPantsItems(),
                    EquipmentSlot.FEET, powerUp.getPowerUpShoesItems()
            );

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor())
                    continue;
                List<ItemStack> costumeItems = costumeItemsBySlot.get(slot);
                if (costumeItems == null)
                    continue;
                ItemStack stackArmor = entity.getItemBySlot(slot);
                ItemStack newStack = costumeItems.get(randomIndex);

                if ((stackArmor.isEmpty() && entity.getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS))
                        || stackArmor.is(TagRegistry.COSTUMES))
                    this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, costumeItems, newStack, stackArmor);
            }
        }

        this.handleAccessoryCostumeSlot(entity, powerUp,
                capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat")),
                EquipmentSlot.HEAD, powerUp.getPowerUpHatItems(), randomIndex);

        this.handleAccessoryCostumeSlot(entity, powerUp,
                capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt")),
                EquipmentSlot.CHEST, powerUp.getPowerUpShirtItems(), randomIndex);

        this.handleAccessoryCostumeSlot(entity, powerUp,
                capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants")),
                EquipmentSlot.LEGS, powerUp.getPowerUpPantsItems(), randomIndex);

        this.handleAccessoryCostumeSlot(entity, powerUp,
                capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes")),
                EquipmentSlot.FEET, powerUp.getPowerUpShoesItems(), randomIndex);
    }

    default void handleAccessoryCostumeSlot(LivingEntity entity, AbstractPowerUpEntity powerUp,
                                             AccessoriesContainer container, EquipmentSlot correspondingArmorSlot,
                                             List<ItemStack> costumeItems, int randomIndex) {
        if (container == null)
            return;

        ItemStack stack = container.getAccessories().getItem(0);
        if (stack.is(powerUp.getPowerUpCostumeTag()))
            return;

        ItemStack stackArmor = !(entity instanceof Player) ? entity.getItemBySlot(correspondingArmorSlot) : stack;
        ItemStack newStack = !(entity instanceof Player) ? costumeItems.get(randomIndex) : stack;

        for (ItemStack item : costumeItems)
            newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

        newStack.applyComponents(stack.getComponents());
        container.getAccessories().setItem(0, newStack);
    }

    default ItemStack matchCostumeItem(ItemStack item, AbstractPowerUpEntity powerUp,
                                       ItemStack currentBest, ItemStack... references) {
        if (!item.is(powerUp.getPowerUpCostumeTag()))
            return currentBest;

        for (TagKey<Item> tag : CHARACTER_COSTUME_TAGS) {
            if (!item.is(tag)) continue;
            for (ItemStack reference : references) {
                if (reference.is(tag))
                    return item.copy();
            }
        }
        return currentBest;
    }

    default ItemStack equipCostumesInAccessorySlots(AbstractPowerUpEntity powerUp, ItemStack item,
                                                    ItemStack stackArmor, ItemStack newStack, ItemStack stack) {
        ItemStack matched = this.matchCostumeItem(item, powerUp, newStack, stackArmor, stack);
        if (matched == newStack) {
            stackArmor.set(DataComponentRegistry.HAS_FIRE_FLOWER, powerUp instanceof FireFlowerEntity);
            stackArmor.set(DataComponentRegistry.HAS_ICE_FLOWER, powerUp instanceof IceFlowerEntity);
        }
        return matched;
    }

    default void equipCostumesInArmorSlots(LivingEntity entity, AbstractPowerUpEntity powerUp, EquipmentSlot slot,
                                           ItemStack stackArmor, List<ItemStack> costumeList,
                                           ItemStack newStack, ItemStack currentStack) {
        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.MARIO_COSTUMES)) {
            for (ItemStack item : costumeList) {
                ItemStack matched = this.matchCostumeItem(item, powerUp, newStack, stackArmor);
                if (matched != newStack)
                    newStack = matched;
            }
            newStack.applyComponents(currentStack.getComponents());
            entity.setItemSlot(slot, newStack);
        } else {
            stackArmor.set(DataComponentRegistry.HAS_FIRE_FLOWER, powerUp instanceof FireFlowerEntity);
            stackArmor.set(DataComponentRegistry.HAS_ICE_FLOWER, powerUp instanceof IceFlowerEntity);
        }
    }
}