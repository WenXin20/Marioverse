package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PlacementDirectionPayload(int containerId, int placementDirection) implements CustomPacketPayload {
    public static final Type<PlacementDirectionPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "placement_direction_payload"));

    @NotNull
    @Override
    public Type<PlacementDirectionPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PlacementDirectionPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, PlacementDirectionPayload::containerId,
                    ByteBufCodecs.INT, PlacementDirectionPayload::placementDirection,
                    PlacementDirectionPayload::new);
}
