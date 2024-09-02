package com.wenxin2.warp_pipes.network.server_bound.data;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ClosePipeButtonPayload(BlockPos pos, Boolean closePipe) implements CustomPacketPayload {
    public static final Type<ClosePipeButtonPayload> CLOSE_STATE_PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(WarpPipes.MODID, "close_state_payload"));

    @NotNull
    @Override
    public Type<ClosePipeButtonPayload> type() {
        return CLOSE_STATE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, ClosePipeButtonPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClosePipeButtonPayload::pos,
            ByteBufCodecs.BOOL, ClosePipeButtonPayload::closePipe,
            ClosePipeButtonPayload::new
    );
}
