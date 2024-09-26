package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SwingHandPayload(Boolean swingHand) implements CustomPacketPayload {
    public static final Type<SwingHandPayload> SWING_HAND_PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "swing_hand_payload"));

    @NotNull
    @Override
    public Type<SwingHandPayload> type() {
        return SWING_HAND_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, SwingHandPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SwingHandPayload::swingHand,
            SwingHandPayload::new
    );
}
