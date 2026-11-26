package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PipeCostPayload(int containerId, int action, int value) implements CustomPacketPayload {
    public static final int ITEM_COUNT = 0;
    public static final int XP_COST = 1;
    public static final Type<PipeCostPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "pipe_cost_payload"));

    @NotNull
    @Override
    public Type<PipeCostPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PipeCostPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PipeCostPayload::containerId,
            ByteBufCodecs.VAR_INT, PipeCostPayload::action,
            ByteBufCodecs.VAR_INT, PipeCostPayload::value,
            PipeCostPayload::new
    );
}