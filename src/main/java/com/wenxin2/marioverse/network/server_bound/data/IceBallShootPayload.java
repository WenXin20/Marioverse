package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record IceBallShootPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<IceBallShootPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "ice_ball_shoot_payload"));

    @NotNull
    @Override
    public Type<IceBallShootPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, IceBallShootPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, IceBallShootPayload::pos,
            IceBallShootPayload::new
    );
}