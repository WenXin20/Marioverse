package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record IsSneakingPayload(int containerId, int isSneaking) implements CustomPacketPayload {
    public static final Type<IsSneakingPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "is_sneaking_payload"));

    @NotNull
    @Override
    public Type<IsSneakingPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, IsSneakingPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, IsSneakingPayload::containerId,
                    ByteBufCodecs.INT, IsSneakingPayload::isSneaking,
                    IsSneakingPayload::new);
}