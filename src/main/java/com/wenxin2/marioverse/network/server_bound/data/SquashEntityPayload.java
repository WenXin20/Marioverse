package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SquashEntityPayload(boolean isHoldingJump, double motionY, double boundingBoxMinY) implements CustomPacketPayload {
    public static final Type<SquashEntityPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "squash_entity_payload"));

    @NotNull
    @Override
    public Type<SquashEntityPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, SquashEntityPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, SquashEntityPayload::isHoldingJump,
            ByteBufCodecs.DOUBLE, SquashEntityPayload::motionY,
            ByteBufCodecs.DOUBLE, SquashEntityPayload::boundingBoxMinY,
            SquashEntityPayload::new
    );
}