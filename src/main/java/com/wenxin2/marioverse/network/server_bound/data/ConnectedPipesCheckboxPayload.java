package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ConnectedPipesCheckboxPayload(int containerId, Boolean requiresConnection) implements CustomPacketPayload {
    public static final Type<ConnectedPipesCheckboxPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "connected_pipes_checkbox_payload"));

    @NotNull
    @Override
    public Type<ConnectedPipesCheckboxPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, ConnectedPipesCheckboxPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ConnectedPipesCheckboxPayload::containerId,
            ByteBufCodecs.BOOL, ConnectedPipesCheckboxPayload::requiresConnection,
            ConnectedPipesCheckboxPayload::new
    );
}
