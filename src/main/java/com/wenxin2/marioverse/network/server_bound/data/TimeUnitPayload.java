package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record TimeUnitPayload(int containerId, int timeUnitID) implements CustomPacketPayload {
    public static final Type<TimeUnitPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "time_unit_payload"));

    @NotNull
    @Override
    public Type<TimeUnitPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, TimeUnitPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, TimeUnitPayload::containerId,
                    ByteBufCodecs.INT, TimeUnitPayload::timeUnitID,
                    TimeUnitPayload::new);
}
