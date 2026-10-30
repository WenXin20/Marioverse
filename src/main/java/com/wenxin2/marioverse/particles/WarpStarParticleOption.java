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
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public record WarpStarParticleOption(Vector3f axis, float spin) implements ParticleOptions {
    public static final MapCodec<WarpStarParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("axis").forGetter(WarpStarParticleOption::axis),
            Codec.FLOAT.fieldOf("spin").forGetter(WarpStarParticleOption::spin)
    ).apply(instance, WarpStarParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpStarParticleOption> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, WarpStarParticleOption::axis,
            ByteBufCodecs.FLOAT, WarpStarParticleOption::spin,
            WarpStarParticleOption::new);

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.WARP_STAR.get();
    }
}
