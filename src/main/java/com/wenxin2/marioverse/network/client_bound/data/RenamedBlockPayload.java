package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesButtonPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record RenamedBlockPayload(BlockPos pos, boolean renderWonderFlag) implements CustomPacketPayload {
    public static final Type<RenamedBlockPayload> RENAMED_BLOCK_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "renamed_block_payload"));

    @NotNull
    @Override
    public Type<RenamedBlockPayload> type() {
        return RENAMED_BLOCK_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, RenamedBlockPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, RenamedBlockPayload::pos,
            ByteBufCodecs.BOOL, RenamedBlockPayload::renderWonderFlag,
            RenamedBlockPayload::new
    );
}
