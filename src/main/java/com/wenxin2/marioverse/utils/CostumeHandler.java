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
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface CostumeHandler {
    default boolean mv$hasCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.COSTUMES))
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

                return stackHat.is(TagRegistry.COSTUMES) && stackShirt.is(TagRegistry.COSTUMES)
                        && stackPants.is(TagRegistry.COSTUMES) && stackShoes.is(TagRegistry.COSTUMES);
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
        int randomIndex = (int) (Math.random() * powerUp.getPowerUpHatItems().size());

        if (entity.getType().is(TagRegistry.CAN_WEAR_COSTUMES)) {
            Map<EquipmentSlot, List<ItemStack>> costumeItemsBySlot = Map.of(
                    EquipmentSlot.HEAD, powerUp.getPowerUpHatItems(),
                    EquipmentSlot.CHEST, powerUp.getPowerUpShirtItems(),
                    EquipmentSlot.LEGS, powerUp.getPowerUpPantsItems(),
                    EquipmentSlot.FEET, powerUp.getPowerUpShoesItems());

            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor())
                    continue;

                ItemStack stackArmor = entity.getItemBySlot(slot);
                List<ItemStack> costumeItems = costumeItemsBySlot.get(slot);

                if (stackArmor.isEmpty() && entity.getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)
                        && entity.getType().is(TagRegistry.POWER_UP_APPLIES_COSTUME)) {
                    ItemStack newStack = costumeItems.get(randomIndex).copy();
                    this.applyPowerUpComponents(newStack, powerUp);
                    entity.setItemSlot(slot, newStack);
                } else if (stackArmor.is(TagRegistry.COSTUMES))
                    this.applyPowerUpComponents(stackArmor, powerUp);
            }
        }

        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_hat")), powerUp);
        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_shirt")), powerUp);
        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_pants")), powerUp);
        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_shoes")), powerUp);
    }

    default void applyAccessoryCostumeComponents(AccessoriesContainer container, AbstractPowerUpEntity powerUp) {
        if (container == null)
            return;

        ItemStack stack = container.getAccessories().getItem(0);
        if (stack.is(TagRegistry.COSTUMES))
            this.applyPowerUpComponents(stack, powerUp);
    }

    default void applyPowerUpComponents(ItemStack stack, AbstractPowerUpEntity powerUp) {
        stack.set(DataComponentRegistry.HAS_FIRE_FLOWER, powerUp instanceof FireFlowerEntity);
        stack.set(DataComponentRegistry.HAS_ICE_FLOWER, powerUp instanceof IceFlowerEntity);
    }
}