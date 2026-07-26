package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record DoubleJumpPayload() implements CustomPacketPayload {
    public static final Type<DoubleJumpPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "double_jump_payload"));

    @NotNull
    @Override
    public Type<DoubleJumpPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, DoubleJumpPayload> STREAM_CODEC =
            StreamCodec.unit(new DoubleJumpPayload());
}