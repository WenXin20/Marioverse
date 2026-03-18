package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlacementOffsetPayload(int containerId, int placementOffset) implements CustomPacketPayload {
    public static final Type<PlacementOffsetPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "placement_offset_payload"));

    @NotNull
    @Override
    public Type<PlacementOffsetPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PlacementOffsetPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, PlacementOffsetPayload::containerId,
                    ByteBufCodecs.INT, PlacementOffsetPayload::placementOffset,
                    PlacementOffsetPayload::new);
}
