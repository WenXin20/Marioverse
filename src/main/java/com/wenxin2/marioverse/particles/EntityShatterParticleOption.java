package com.wenxin2.marioverse.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record EntityShatterParticleOption(ResourceLocation model, ResourceLocation texture, String bone) implements ParticleOptions {
    public static final MapCodec<EntityShatterParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("model").forGetter(EntityShatterParticleOption::model),
            ResourceLocation.CODEC.fieldOf("texture").forGetter(EntityShatterParticleOption::texture),
            Codec.STRING.optionalFieldOf("bone", "").forGetter(EntityShatterParticleOption::bone)
    ).apply(instance, EntityShatterParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityShatterParticleOption> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, EntityShatterParticleOption::model,
            ResourceLocation.STREAM_CODEC, EntityShatterParticleOption::texture,
            ByteBufCodecs.STRING_UTF8, EntityShatterParticleOption::bone,
            EntityShatterParticleOption::new);

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.ENTITY_SHATTER.get();
    }
}
