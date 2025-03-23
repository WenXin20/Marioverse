package com.wenxin2.marioverse.items;

import com.wenxin2.marioverse.Marioverse;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.SmithingTemplateItem;

public class CharacterSmithingTemplateItem extends SmithingTemplateItem {
    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    
    private static final ResourceLocation EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
    private static final ResourceLocation EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
    private static final ResourceLocation EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
    private static final ResourceLocation EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
    private static final ResourceLocation EMPTY_SLOT_INGOT = ResourceLocation.withDefaultNamespace("item/empty_slot_ingot");
    private static final ResourceLocation EMPTY_SLOT_BLOCK = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "gui/slot/empty_block_slot");
    private static final ResourceLocation EMPTY_SLOT_FLOWER = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "gui/slot/empty_flower_slot");

    private static final Component CHARACTER_COSTUME = Component.translatable(
            Util.makeDescriptionId("upgrade", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mario_costume")))
            .withStyle(TITLE_FORMAT);
    private static final Component ICE_COSTUME = Component.translatable(
            Util.makeDescriptionId("upgrade", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "ice_costume")))
            .withStyle(TITLE_FORMAT);
    private static final Component FIRE_COSTUME = Component.translatable(
            Util.makeDescriptionId("upgrade", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "fire_costume")))
            .withStyle(TITLE_FORMAT);
    
    private static final Component LEATHER_COSTUME_APPLIES_TO = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.leather_armor.applies_to")))
            .withStyle(DESCRIPTION_FORMAT);

    private static final Component LEATHER_COSTUME_BASE_SLOT_DESCRIPTION = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.leather_armor.base_slot_description")));

    private static final Component CHARACTER_COSTUME_APPLIES_TO = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.character_costume.applies_to")))
            .withStyle(DESCRIPTION_FORMAT);

    private static final Component CHARACTER_COSTUME_BASE_SLOT_DESCRIPTION = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.character_costume.base_slot_description")));

    private static final Component CHARACTER_COSTUME_INGREDIENTS = Component.translatable(
                    Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.character_costume.ingredients")))
            .withStyle(DESCRIPTION_FORMAT);

    private static final Component CHARACTER_COSTUME_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.character_costume.additions_slot_description")));
    
    private static final Component FIRE_COSTUME_INGREDIENTS = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.fire_costume.ingredients")))
            .withStyle(DESCRIPTION_FORMAT);

    private static final Component FIRE_COSTUME_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.fire_costume.additions_slot_description")));

    private static final Component ICE_COSTUME_INGREDIENTS = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.ice_costume.ingredients")))
            .withStyle(DESCRIPTION_FORMAT);

    private static final Component ICE_COSTUME_ADDITIONS_SLOT_DESCRIPTION = Component.translatable(
            Util.makeDescriptionId("item", ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "smithing_template.ice_costume.additions_slot_description")));
    
    public CharacterSmithingTemplateItem(Component appliesTo, Component ingredients, Component upgradeDescription, Component baseSlotDescription, Component additionsSlotDescription,
                                         List<ResourceLocation> baseSlotEmptyIcons, List<ResourceLocation> additionalSlotEmptyIcons, FeatureFlag... requiredFeatures) {
        super(appliesTo, ingredients, upgradeDescription, baseSlotDescription, additionsSlotDescription, baseSlotEmptyIcons, additionalSlotEmptyIcons, requiredFeatures);
    }

    public static SmithingTemplateItem createCharacterUpgradeTemplate() {
        return new SmithingTemplateItem(
                LEATHER_COSTUME_APPLIES_TO,
                CHARACTER_COSTUME_INGREDIENTS,
                CHARACTER_COSTUME,
                LEATHER_COSTUME_BASE_SLOT_DESCRIPTION,
                CHARACTER_COSTUME_ADDITIONS_SLOT_DESCRIPTION,
                createCharacterUpgradeIconList(),
                createCharacterUpgradeMaterialList()
        );
    }

    public static SmithingTemplateItem createFireUpgradeTemplate() {
        return new SmithingTemplateItem(
                CHARACTER_COSTUME_APPLIES_TO,
                FIRE_COSTUME_INGREDIENTS,
                FIRE_COSTUME,
                CHARACTER_COSTUME_BASE_SLOT_DESCRIPTION,
                FIRE_COSTUME_ADDITIONS_SLOT_DESCRIPTION,
                createCharacterUpgradeIconList(),
                createFlowerUpgradeMaterialList()
        );
    }

    public static SmithingTemplateItem createIceUpgradeTemplate() {
        return new SmithingTemplateItem(
                CHARACTER_COSTUME_APPLIES_TO,
                ICE_COSTUME_INGREDIENTS,
                ICE_COSTUME,
                CHARACTER_COSTUME_BASE_SLOT_DESCRIPTION,
                ICE_COSTUME_ADDITIONS_SLOT_DESCRIPTION,
                createCharacterUpgradeIconList(),
                createFlowerUpgradeMaterialList()
        );
    }

    private static List<ResourceLocation> createCharacterUpgradeIconList() {
        return List.of(
                EMPTY_SLOT_HELMET,
                EMPTY_SLOT_CHESTPLATE,
                EMPTY_SLOT_LEGGINGS,
                EMPTY_SLOT_BOOTS);
    }

    private static List<ResourceLocation> createCharacterUpgradeMaterialList() {
        return List.of(EMPTY_SLOT_BLOCK);
    }

    private static List<ResourceLocation> createFlowerUpgradeMaterialList() {
        return List.of(EMPTY_SLOT_FLOWER);
    }
}
