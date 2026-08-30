package com.wenxin2.marioverse.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/** Datapack-driven, armor-trim-style: names which texture holds this palette's color ramp. */
public record ArrowPalette(ResourceLocation texture) {
    public static final Codec<ArrowPalette> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("texture").forGetter(ArrowPalette::texture)
    ).apply(instance, ArrowPalette::new));
}
