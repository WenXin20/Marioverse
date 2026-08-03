package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.power_ups.AbstractPowerUpEntity;
import com.wenxin2.marioverse.entities.power_ups.FireFlowerEntity;
import com.wenxin2.marioverse.entities.power_ups.IceFlowerEntity;
import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.power_up.PowerUpType;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
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

    default void applyCostumeChange(LivingEntity entity, PowerUpSource source) {
        AccessoriesCapability capability = AccessoriesCapability.get(entity);

        if (capability != null) {
            if (entity instanceof Player && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get())
                this.updateCostume(entity, source, capability);
            else if (!(entity instanceof Player) && ConfigRegistry.EQUIP_COSTUMES_MOBS.get())
                this.updateCostume(entity, source, capability);
        }
    }

    default void updateCostume(LivingEntity entity, PowerUpSource source, AccessoriesCapability capability) {
        if (entity.getType().is(TagRegistry.CAN_WEAR_COSTUMES)) {
            List<ItemStack> hatCostumeItems = BuiltInRegistries.ITEM.getOrCreateTag(TagRegistry.POWER_UP_HAT_COSTUMES).stream()
                    .map(holder -> new ItemStack(holder.value())).toList();
            int randomIndex = hatCostumeItems.isEmpty() ? 0 : (int) (Math.random() * hatCostumeItems.size());

            for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
                TagKey<Item> costumeTag;

                if (slot == EquipmentSlot.HEAD)
                    costumeTag = TagRegistry.POWER_UP_HAT_COSTUMES;
                else if (slot == EquipmentSlot.CHEST)
                    costumeTag = TagRegistry.POWER_UP_SHIRT_COSTUMES;
                else if (slot == EquipmentSlot.LEGS)
                    costumeTag = TagRegistry.POWER_UP_PANTS_COSTUMES;
                else costumeTag = TagRegistry.POWER_UP_SHOES_COSTUMES;

                List<ItemStack> costumeItems = BuiltInRegistries.ITEM.getOrCreateTag(costumeTag).stream()
                        .map(holder -> new ItemStack(holder.value())).toList();

                if (costumeItems.isEmpty())
                    continue;

                ItemStack stackArmor = entity.getItemBySlot(slot);

                if (stackArmor.isEmpty() && entity.getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)
                        && entity.getType().is(TagRegistry.POWER_UP_APPLIES_COSTUME)) {
                    ItemStack newStack = costumeItems.get(randomIndex).copy();
                    this.applyPowerUpComponents(newStack, source);
                    entity.setItemSlot(slot, newStack);
                } else if (stackArmor.is(TagRegistry.COSTUMES)) {
                    ItemStack newStack = stackArmor.copy();
                    this.applyPowerUpComponents(newStack, source);
                    entity.setItemSlot(slot, newStack);
                }
            }
        }

        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_hat")), source);
        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_shirt")), source);
        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_pants")), source);
        this.applyAccessoryCostumeComponents(capability.getContainer(SlotTypeLoader
                .getSlotType(entity, "costume_shoes")), source);
    }

    default void applyAccessoryCostumeComponents(AccessoriesContainer container, PowerUpSource source) {
        if (container == null)
            return;

        ItemStack stack = container.getAccessories().getItem(0);
        if (stack.is(TagRegistry.COSTUMES))
            this.applyPowerUpComponents(stack, source);
    }

    default void applyPowerUpComponents(ItemStack stack, PowerUpSource source) {
        stack.set(DataComponentRegistry.POWER_UP_TYPE.get(), source.getPowerUpType());
    }
}