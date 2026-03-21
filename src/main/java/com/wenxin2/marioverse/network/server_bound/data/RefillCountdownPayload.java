package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RefillCountdownPayload(int containerId, int refillCountdown) implements CustomPacketPayload {
    public static final Type<RefillCountdownPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "refill_countdown_payload"));

    @NotNull
    @Override
    public Type<RefillCountdownPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, RefillCountdownPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, RefillCountdownPayload::containerId,
                    ByteBufCodecs.INT, RefillCountdownPayload::refillCountdown,
                    RefillCountdownPayload::new);
}
