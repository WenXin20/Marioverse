package com.wenxin2.warp_pipes.network.server_bound.data;

import com.wenxin2.warp_pipes.WarpPipes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RenamePipePayload(BlockPos pos, String customName) implements CustomPacketPayload {
    public static final Type<RenamePipePayload> RENAME_PIPE_PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(WarpPipes.MODID, "rename_pipe_payload"));

    @NotNull
    @Override
    public Type<RenamePipePayload> type() {
        return RENAME_PIPE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, RenamePipePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RenamePipePayload::pos,
            ByteBufCodecs.STRING_UTF8, RenamePipePayload::customName,
            RenamePipePayload::new
    );
}
