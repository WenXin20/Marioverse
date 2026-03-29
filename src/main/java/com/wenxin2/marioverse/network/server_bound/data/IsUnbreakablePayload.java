package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record IsUnbreakablePayload(int containerId, int isUnbreakable) implements CustomPacketPayload {
    public static final Type<IsUnbreakablePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "is_unbreakable_payload"));

    @NotNull
    @Override
    public Type<IsUnbreakablePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, IsUnbreakablePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, IsUnbreakablePayload::containerId,
                    ByteBufCodecs.INT, IsUnbreakablePayload::isUnbreakable,
                    IsUnbreakablePayload::new);
}