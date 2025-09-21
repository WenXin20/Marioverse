package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClearPipeSoundPayload(int fadeInDuration, int fadeOutDuration, boolean inClearPipe) implements CustomPacketPayload {
    public static final Type<ClearPipeSoundPayload> SOUND_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "clear_pipe_sound_payload"));

    @NotNull
    @Override
    public Type<ClearPipeSoundPayload> type() {
        return SOUND_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, ClearPipeSoundPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClearPipeSoundPayload::fadeInDuration,
            ByteBufCodecs.INT, ClearPipeSoundPayload::fadeOutDuration,
            ByteBufCodecs.BOOL, ClearPipeSoundPayload::inClearPipe,
            ClearPipeSoundPayload::new
    );
}