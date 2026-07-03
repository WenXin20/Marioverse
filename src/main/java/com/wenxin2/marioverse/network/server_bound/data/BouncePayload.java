package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record BouncePayload(BlockPos pos, boolean isHoldingJump, double motionY) implements CustomPacketPayload {
    public static final Type<BouncePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "bounce_payload"));

    @NotNull
    @Override
    public Type<BouncePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, BouncePayload> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, BouncePayload::pos,
                    ByteBufCodecs.BOOL, BouncePayload::isHoldingJump,
                    ByteBufCodecs.DOUBLE, BouncePayload::motionY,
                    BouncePayload::new
    );
}