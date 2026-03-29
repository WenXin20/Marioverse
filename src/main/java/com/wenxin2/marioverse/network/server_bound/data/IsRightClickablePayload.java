package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record IsRightClickablePayload(int containerId, int isRightClickable) implements CustomPacketPayload {
    public static final Type<IsRightClickablePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "is_right_clickable_payload"));

    @NotNull
    @Override
    public Type<IsRightClickablePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, IsRightClickablePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, IsRightClickablePayload::containerId,
                    ByteBufCodecs.INT, IsRightClickablePayload::isRightClickable,
                    IsRightClickablePayload::new);
}