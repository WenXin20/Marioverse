package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record OneUpPayload(Boolean triggerOneUp) implements CustomPacketPayload {
    public static final Type<OneUpPayload> ONE_UP_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "one_up_payload"));

    @NotNull
    @Override
    public Type<OneUpPayload> type() {
        return ONE_UP_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, OneUpPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, OneUpPayload::triggerOneUp,
            OneUpPayload::new
    );
}
