package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TransferOwnerPayload(int containerId, String playerName) implements CustomPacketPayload {
    public static final Type<TransferOwnerPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "transfer_owner_payload"));

    @NotNull
    @Override
    public Type<TransferOwnerPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, TransferOwnerPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, TransferOwnerPayload::containerId,
            ByteBufCodecs.stringUtf8(16), TransferOwnerPayload::playerName,
            TransferOwnerPayload::new
    );
}