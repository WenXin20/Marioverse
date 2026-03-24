package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record HasCollisionPayload(int containerId, int hasCollision) implements CustomPacketPayload {
    public static final Type<HasCollisionPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "has_collision_payload"));

    @NotNull
    @Override
    public Type<HasCollisionPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, HasCollisionPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, HasCollisionPayload::containerId,
                    ByteBufCodecs.INT, HasCollisionPayload::hasCollision,
                    HasCollisionPayload::new);
}