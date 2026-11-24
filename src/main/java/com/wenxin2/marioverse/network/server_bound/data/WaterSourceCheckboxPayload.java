package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record WaterSourceCheckboxPayload(int containerId, Boolean requiresWaterSource) implements CustomPacketPayload {
    public static final Type<WaterSourceCheckboxPayload> PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "water_source_checkbox_payload"));

    @NotNull
    @Override
    public Type<WaterSourceCheckboxPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, WaterSourceCheckboxPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, WaterSourceCheckboxPayload::containerId,
            ByteBufCodecs.BOOL, WaterSourceCheckboxPayload::requiresWaterSource,
            WaterSourceCheckboxPayload::new
    );
}
