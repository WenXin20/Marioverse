package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AttributesRegistry {
    public static final DeferredHolder<Attribute, Attribute> EYE_HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> HEIGHT_SCALE;
    public static final DeferredHolder<Attribute, Attribute> WIDTH_SCALE;

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
