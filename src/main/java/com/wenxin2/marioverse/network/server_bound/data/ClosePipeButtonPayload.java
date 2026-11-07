package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClosePipeButtonPayload(int containerId, Boolean closePipe) implements CustomPacketPayload {
    public static final Type<ClosePipeButtonPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "close_state_payload"));

    @NotNull
    @Override
    public Type<ClosePipeButtonPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, ClosePipeButtonPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClosePipeButtonPayload::containerId,
            ByteBufCodecs.BOOL, ClosePipeButtonPayload::closePipe,
            ClosePipeButtonPayload::new
    );
}
