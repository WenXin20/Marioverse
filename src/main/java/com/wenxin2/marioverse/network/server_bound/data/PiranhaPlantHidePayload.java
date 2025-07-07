package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PiranhaPlantHidePayload(boolean isHiding, int entityID) implements CustomPacketPayload {
    public static final Type<PiranhaPlantHidePayload> HIDE_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "piranha_plant_hide_payload"));

    @NotNull
    @Override
    public Type<PiranhaPlantHidePayload> type() {
        return HIDE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, PiranhaPlantHidePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PiranhaPlantHidePayload::isHiding,
            ByteBufCodecs.INT, PiranhaPlantHidePayload::entityID,
            PiranhaPlantHidePayload::new
    );
}