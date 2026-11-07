package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PaintingRipplePayload(Integer entityId) implements CustomPacketPayload {
    public static final Type<PaintingRipplePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "painting_ripple_payload"));

    @NotNull
    @Override
    public Type<PaintingRipplePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PaintingRipplePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, PaintingRipplePayload::entityId,
            PaintingRipplePayload::new
    );
}
