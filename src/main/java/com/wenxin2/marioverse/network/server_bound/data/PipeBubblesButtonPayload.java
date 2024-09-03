package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PipeBubblesButtonPayload(BlockPos pos, Boolean hasPipeBubbles) implements CustomPacketPayload {
    public static final Type<PipeBubblesButtonPayload> BUBBLES_STATE_PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "bubbles_state_payload"));

    @NotNull
    @Override
    public Type<PipeBubblesButtonPayload> type() {
        return BUBBLES_STATE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PipeBubblesButtonPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PipeBubblesButtonPayload::pos,
            ByteBufCodecs.BOOL, PipeBubblesButtonPayload::hasPipeBubbles,
            PipeBubblesButtonPayload::new
    );
}
