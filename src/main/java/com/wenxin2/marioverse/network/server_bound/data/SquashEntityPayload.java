package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SquashEntityPayload(boolean isHoldingJump) implements CustomPacketPayload {
    public static final Type<SquashEntityPayload> SQUASH_ENTITY_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "squash_entity_payload"));

    @NotNull
    @Override
    public Type<SquashEntityPayload> type() {
        return SQUASH_ENTITY_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, SquashEntityPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, SquashEntityPayload::isHoldingJump,
            SquashEntityPayload::new
    );
}