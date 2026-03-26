package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record HideItemRenderedPayload(int containerId, int hideItemRendered) implements CustomPacketPayload {
    public static final Type<HideItemRenderedPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "hide_item_rendered_payload"));

    @NotNull
    @Override
    public Type<HideItemRenderedPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, HideItemRenderedPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, HideItemRenderedPayload::containerId,
                    ByteBufCodecs.INT, HideItemRenderedPayload::hideItemRendered,
                    HideItemRenderedPayload::new);
}