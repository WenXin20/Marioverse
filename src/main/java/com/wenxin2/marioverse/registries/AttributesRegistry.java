package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AttributesRegistry {
    public static final DeferredHolder<Attribute, Attribute> EYE_HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> WIDTH_SCALE;

    public static final ResourceLocation DAMAGED_SCALE = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "damaged_scale");
    public static final ResourceLocation RESET_SCALE = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "reset_scale");
    public static final ResourceLocation JUMP_BOOST = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "character_jump_boost");
    public static final ResourceLocation RUNNING_JUMP_BOOST = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "character_running_jump_boost");
    public static final ResourceLocation SAFE_FALL_DISTANCE = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "character_safe_fall_distance");
    public static final ResourceLocation SLOW_GRAVITY = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "character_gravity");
    public static final ResourceLocation COSTUME_ARMOR_TOUGHNESS = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume_armor_toughness");
    public static final ResourceLocation COSTUME_ARMOR_KNOCKBACK_RESISTANCE = ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "costume_armor_knockback_resistance");

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
}
