package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PipeBubblesSliderPayload(BlockPos pos, int bubblesDistance) implements CustomPacketPayload {
    public static final Type<PipeBubblesSliderPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "bubbles_distance_payload"));

    @NotNull
    @Override
    public Type<PipeBubblesSliderPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PipeBubblesSliderPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PipeBubblesSliderPayload::pos,
            ByteBufCodecs.INT, PipeBubblesSliderPayload::bubblesDistance,
            PipeBubblesSliderPayload::new
    );
}
