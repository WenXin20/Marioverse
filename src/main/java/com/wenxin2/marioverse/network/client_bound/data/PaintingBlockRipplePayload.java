package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PaintingBlockRipplePayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<PaintingBlockRipplePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "painting_block_ripple_payload"));

    @NotNull
    @Override
    public Type<PaintingBlockRipplePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PaintingBlockRipplePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PaintingBlockRipplePayload::pos,
            PaintingBlockRipplePayload::new
    );
}
