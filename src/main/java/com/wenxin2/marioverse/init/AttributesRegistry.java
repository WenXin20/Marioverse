package com.wenxin2.marioverse.init;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AttributesRegistry {
    public static AttributeModifier SCALE_MODIFIER = new AttributeModifier(
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "scale_modifier"),
                            -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    public static void init() {}
}
