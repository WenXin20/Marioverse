package com.wenxin2.marioverse.registries;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AttributesRegistry {
    public static final DeferredHolder<Attribute, Attribute> COSTUME_ARMOR_KNOCKBACK_RESISTANCE;
    public static final DeferredHolder<Attribute, Attribute> COSTUME_ARMOR_TOUGHNESS;
    public static final DeferredHolder<Attribute, Attribute> DAMAGED_SCALE;
    public static final DeferredHolder<Attribute, Attribute> EYE_HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> JUMP_BOOST;
    public static final DeferredHolder<Attribute, Attribute> RUNNING_JUMP_BOOST;
    public static final DeferredHolder<Attribute, Attribute> SAFE_FALL_DISTANCE;
    public static final DeferredHolder<Attribute, Attribute> WIDTH_SCALE;

    static {
        COSTUME_ARMOR_TOUGHNESS = Marioverse.ATTRIBUTES.register("costume_armor_toughness",
                () -> new RangedAttribute("attribute.marioverse.generic.costume_armor_toughness", 1.0, 0.0, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        COSTUME_ARMOR_KNOCKBACK_RESISTANCE = Marioverse.ATTRIBUTES.register("costume_armor_knockback_resistance",
                () -> new RangedAttribute("attribute.marioverse.generic.costume_armor_knockback_resistance", 1.0, 0.0, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        DAMAGED_SCALE = Marioverse.ATTRIBUTES.register("damaged_scale",
                () -> new RangedAttribute("attribute.marioverse.generic.damaged_scale", 1.0, 0.0, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        EYE_HEIGHT_SCALE = Marioverse.ATTRIBUTES.register("eye_height_scale",
                () -> new RangedAttribute("attribute.marioverse.generic.eye_height_scale", 1.0, 0.0625, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        HEIGHT_SCALE = Marioverse.ATTRIBUTES.register("height_scale",
                () -> new RangedAttribute("attribute.marioverse.generic.height_scale", 1.0, 0.0625, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        JUMP_BOOST = Marioverse.ATTRIBUTES.register("jump_boost",
                () -> new RangedAttribute("attribute.marioverse.generic.jump_boost", 1.0, 0.0, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        RUNNING_JUMP_BOOST = Marioverse.ATTRIBUTES.register("running_jump_boost",
                () -> new RangedAttribute("attribute.marioverse.generic.running_jump_boost", 1.0, 0.0, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        SAFE_FALL_DISTANCE = Marioverse.ATTRIBUTES.register("safe_fall_distance",
                () -> new RangedAttribute("attribute.marioverse.generic.safe_fall_distance", 1.0, 0.0, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
        WIDTH_SCALE = Marioverse.ATTRIBUTES.register("width_scale",
                () -> new RangedAttribute("attribute.marioverse.generic.width_scale", 1.0, 0.0625, 32.0)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEUTRAL));
    }

    public static void init() {}
}
