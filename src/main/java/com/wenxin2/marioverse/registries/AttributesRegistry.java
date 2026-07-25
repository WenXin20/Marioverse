package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.Nullable;

public class AttributesRegistry {
    public static final DeferredHolder<Attribute, Attribute> EYE_HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> WIDTH_SCALE;

    public static final ResourceLocation AUTO_STEP_HEIGHT = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "auto_step_height");
    public static final ResourceLocation BLOCK_REACH_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "block_reach_distance");
    public static final ResourceLocation CHARACTER_JUMP_BOOST = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "character_jump_boost");
    public static final ResourceLocation CHARACTER_RUNNING_JUMP_BOOST = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "character_running_jump_boost");
    public static final ResourceLocation CHARACTER_SAFE_FALL_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "character_safe_fall_distance");
    public static final ResourceLocation COSTUME_ARMOR_KNOCKBACK_RESISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "costume_armor_knockback_resistance");
    public static final ResourceLocation COSTUME_ARMOR_TOUGHNESS = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "costume_armor_toughness");
    public static final ResourceLocation DAMAGED_SCALE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "damaged_scale");
    public static final ResourceLocation ENTITY_REACH_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "entity_reach_distance");
    public static final ResourceLocation MAX_HEATH = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "max_health");
    public static final ResourceLocation MEGA_BLOCK_REACH_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mega_block_reach_distance");
    public static final ResourceLocation MEGA_ENTITY_REACH_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mega_entity_reach_distance");
    public static final ResourceLocation MEGA_JUMP_BOOST = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mega_jump_boost");
    public static final ResourceLocation MEGA_RUNNING_JUMP_BOOST = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mega_running_jump_boost");
    public static final ResourceLocation MEGA_SAFE_FALL_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mega_safe_fall_distance");
    public static final ResourceLocation MINI_BLOCK_REACH_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mini_block_reach_distance");
    public static final ResourceLocation MINI_ENTITY_REACH_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mini_entity_reach_distance");
    public static final ResourceLocation MINI_JUMP_BOOST = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mini_jump_boost");
    public static final ResourceLocation MINI_RUNNING_JUMP_BOOST = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mini_running_jump_boost");
    public static final ResourceLocation MINI_SAFE_FALL_DISTANCE = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mini_safe_fall_distance");
    public static final ResourceLocation MINI_GOOMBA_SLOWDOWN = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_slowdown");

    static {
        EYE_HEIGHT_SCALE = Marioverse.ATTRIBUTES.register("eye_height_scale",
                () -> new RangedAttribute("attribute.marioverse.generic.eye_height_scale", 1.0, 0.0625, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        HEIGHT_SCALE = Marioverse.ATTRIBUTES.register("height_scale",
                () -> new RangedAttribute("attribute.marioverse.generic.height_scale", 1.0, 0.0625, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        WIDTH_SCALE = Marioverse.ATTRIBUTES.register("width_scale",
                () -> new RangedAttribute("attribute.marioverse.generic.width_scale", 1.0, 0.0625, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
    }

    public static void init() {}

    public static void updateAttributeModifiers(@Nullable AttributeInstance attribute, ResourceLocation id, double amount, boolean shouldAdd, boolean shouldRemove) {
        if (attribute == null)
            return;
        AttributeModifier modifier = attribute.getModifier(id);

        if (modifier != null && shouldRemove) {
            attribute.removeModifier(modifier);
            modifier = null;
        }

        if (modifier == null && shouldAdd && amount != 0)
            attribute.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
    }
}
