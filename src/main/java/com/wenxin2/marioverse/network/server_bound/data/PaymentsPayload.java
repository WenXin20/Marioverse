package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PaymentsPayload(int containerId, int action) implements CustomPacketPayload {
    public static final int TAKE_ONE = 0;
    public static final int TOGGLE_VOID = 1;
    public static final int COLLECT_XP = 2;
    public static final int TOGGLE_AUTOMATION = 3;
    public static final int RESET_LIFETIME = 4;
    public static final Type<PaymentsPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "payments_payload"));

    @NotNull
    @Override
    public Type<PaymentsPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PaymentsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PaymentsPayload::containerId,
            ByteBufCodecs.VAR_INT, PaymentsPayload::action,
            PaymentsPayload::new
    );
}