package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record EntityScalePayload(int entityId, float width, float height) implements CustomPacketPayload {
    public static final Type<EntityScalePayload> ENTITY_SCALE_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "entity_scale_payload"));

    @NotNull
    @Override
    public Type<EntityScalePayload> type() {
        return ENTITY_SCALE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, EntityScalePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EntityScalePayload::entityId,
            ByteBufCodecs.FLOAT, EntityScalePayload::width,
            ByteBufCodecs.FLOAT, EntityScalePayload::height,
            EntityScalePayload::new
    );
}
