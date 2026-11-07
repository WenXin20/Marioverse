package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PipeBubblesButtonPayload(int containerId, Boolean hasPipeBubbles) implements CustomPacketPayload {
    public static final Type<PipeBubblesButtonPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "bubbles_state_payload"));

    @NotNull
    @Override
    public Type<PipeBubblesButtonPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PipeBubblesButtonPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PipeBubblesButtonPayload::containerId,
            ByteBufCodecs.BOOL, PipeBubblesButtonPayload::hasPipeBubbles,
            PipeBubblesButtonPayload::new
    );
}
