package com.wenxin2.marioverse.event_handlers;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.event.CurioAttributeModifierEvent;

@EventBusSubscriber(modid = "marioverse")
public class CuriosEventHandler {
    private static final String[] COSTUME_SLOTS = { "costume_hat", "costume_shirt", "costume_pants", "costume_shoes" };

    @SubscribeEvent
    public static void armorValueModifier(CurioAttributeModifierEvent event) {
        ItemStack stack = event.getItemStack();
        SlotContext slotContext = event.getSlotContext();
        String identifier = slotContext.identifier();

        boolean isCostumeSlot = false;
        for (String slot : COSTUME_SLOTS) {
            if (slot.equals(identifier)) {
                isCostumeSlot = true;
                break;
            }
        }

        if (!isCostumeSlot || !(stack.getItem() instanceof ArmorItem armorItem))
            return;

        ArmorMaterial material = armorItem.getMaterial().value();
        int extraArmor = material.getDefense(armorItem.getType()) / 2;
        float toughness = material.toughness() / 2.0F;
        float knockbackResistance = material.knockbackResistance() / 2.0F;

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(armorItem);
        ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(itemId.getNamespace(),
                itemId.getPath() + "_" + identifier);

        if (extraArmor != 0)
            event.addModifier(Attributes.ARMOR, new AttributeModifier(modifierId, extraArmor, AttributeModifier.Operation.ADD_VALUE));
        if (toughness != 0)
            event.addModifier(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(modifierId, toughness, AttributeModifier.Operation.ADD_VALUE));
        if (knockbackResistance != 0)
            event.addModifier(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(modifierId, knockbackResistance, AttributeModifier.Operation.ADD_VALUE));
    }
}