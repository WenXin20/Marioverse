package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataComponentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public interface CostumeHandler {
    default boolean mv$hasCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.COSTUMES))
            return true;

        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(entity);
        if (curiosInventory.isPresent()) {
            Map<String, ICurioStacksHandler> curios = curiosInventory.get().getCurios();
            ICurioStacksHandler slotHat = curios.get("costume_hat");
            ICurioStacksHandler slotShirt = curios.get("costume_shirt");
            ICurioStacksHandler slotPants = curios.get("costume_pants");
            ICurioStacksHandler slotShoes = curios.get("costume_shoes");

            if (slotHat != null && slotShirt != null && slotPants != null && slotShoes != null) {
                ItemStack stackHat = slotHat.getStacks().getStackInSlot(0);
                ItemStack stackShirt = slotShirt.getStacks().getStackInSlot(0);
                ItemStack stackPants = slotPants.getStacks().getStackInSlot(0);
                ItemStack stackShoes = slotShoes.getStacks().getStackInSlot(0);

                return stackHat.is(TagRegistry.COSTUMES) && stackShirt.is(TagRegistry.COSTUMES)
                        && stackPants.is(TagRegistry.COSTUMES) && stackShoes.is(TagRegistry.COSTUMES);
            }
        }
        return false;
    }

    default void applyCostumeChange(LivingEntity entity, PowerUpSource source) {
        Optional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosInventory(entity);

        if (curiosInventory.isPresent()) {
            if (entity instanceof Player && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get())
                this.updateCostume(entity, source, curiosInventory.get());
            else if (!(entity instanceof Player) && ConfigRegistry.EQUIP_COSTUMES_MOBS.get())
                this.updateCostume(entity, source, curiosInventory.get());
        }
    }

    default void updateCostume(LivingEntity entity, PowerUpSource source, ICuriosItemHandler capability) {
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

        Map<String, ICurioStacksHandler> curios = capability.getCurios();
        this.applyCurioCostumeComponents(curios.get("costume_hat"), source);
        this.applyCurioCostumeComponents(curios.get("costume_shirt"), source);
        this.applyCurioCostumeComponents(curios.get("costume_pants"), source);
        this.applyCurioCostumeComponents(curios.get("costume_shoes"), source);
    }

    default void applyCurioCostumeComponents(ICurioStacksHandler slotHandler, PowerUpSource source) {
        if (slotHandler == null)
            return;

        ItemStack stack = slotHandler.getStacks().getStackInSlot(0);
        if (stack.is(TagRegistry.COSTUMES))
            this.applyPowerUpComponents(stack, source);
    }

    default void applyPowerUpComponents(ItemStack stack, PowerUpSource source) {
        stack.set(DataComponentRegistry.POWER_UP_TYPE.get(), source.getPowerUpType());
    }
}