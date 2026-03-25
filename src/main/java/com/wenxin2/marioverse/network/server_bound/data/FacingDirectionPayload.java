package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record FacingDirectionPayload(int containerId, int facingDirection) implements CustomPacketPayload {
    public static final Type<FacingDirectionPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "facing_direction_payload"));

    @NotNull
    @Override
    public Type<FacingDirectionPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, FacingDirectionPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, FacingDirectionPayload::containerId,
                    ByteBufCodecs.INT, FacingDirectionPayload::facingDirection,
                    FacingDirectionPayload::new);
}
