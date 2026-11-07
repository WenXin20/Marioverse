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

public record WarpStarParticleOption(Vector3f axis, float spin, boolean orbit, Vector3f semiAxis, float axisRatio) implements ParticleOptions {
    public static final MapCodec<WarpStarParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.VECTOR3F.fieldOf("axis").forGetter(WarpStarParticleOption::axis),
            Codec.FLOAT.fieldOf("spin").forGetter(WarpStarParticleOption::spin),
            Codec.BOOL.optionalFieldOf("orbit", false).forGetter(WarpStarParticleOption::orbit),
            ExtraCodecs.VECTOR3F.optionalFieldOf("semi_axis", new Vector3f()).forGetter(WarpStarParticleOption::semiAxis),
            Codec.FLOAT.optionalFieldOf("axis_ratio", 1.0F).forGetter(WarpStarParticleOption::axisRatio)
    ).apply(instance, WarpStarParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpStarParticleOption> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F, WarpStarParticleOption::axis,
            ByteBufCodecs.FLOAT, WarpStarParticleOption::spin,
            ByteBufCodecs.BOOL, WarpStarParticleOption::orbit,
            ByteBufCodecs.VECTOR3F, WarpStarParticleOption::semiAxis,
            ByteBufCodecs.FLOAT, WarpStarParticleOption::axisRatio,
            WarpStarParticleOption::new);

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.RAINBOW_STAR.get();
    }
}
