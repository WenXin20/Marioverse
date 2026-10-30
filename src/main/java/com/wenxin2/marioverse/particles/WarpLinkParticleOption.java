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
import net.minecraft.world.level.gameevent.PositionSource;

public record WarpLinkParticleOption(PositionSource destination, int arrivalInTicks) implements ParticleOptions {
    public static final MapCodec<WarpLinkParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PositionSource.CODEC.fieldOf("destination").forGetter(WarpLinkParticleOption::destination),
            Codec.INT.fieldOf("arrival_in_ticks").forGetter(WarpLinkParticleOption::arrivalInTicks)
    ).apply(instance, WarpLinkParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WarpLinkParticleOption> STREAM_CODEC = StreamCodec.composite(
            PositionSource.STREAM_CODEC, WarpLinkParticleOption::destination,
            ByteBufCodecs.VAR_INT, WarpLinkParticleOption::arrivalInTicks,
            WarpLinkParticleOption::new);

    @Override
    public ParticleType<?> getType() {
        return ParticleRegistry.WARP_LINK.get();
    }
}
