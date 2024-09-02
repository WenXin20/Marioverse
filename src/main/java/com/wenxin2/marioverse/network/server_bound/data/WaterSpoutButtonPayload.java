package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record WaterSpoutButtonPayload(BlockPos pos, Boolean hasWaterSpout) implements CustomPacketPayload {
    public static final Type<WaterSpoutButtonPayload> SPOUT_STATE_PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "spout_state_payload"));

    @NotNull
    @Override
    public Type<WaterSpoutButtonPayload> type() {
        return SPOUT_STATE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, WaterSpoutButtonPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, WaterSpoutButtonPayload::pos,
            ByteBufCodecs.BOOL, WaterSpoutButtonPayload::hasWaterSpout,
            WaterSpoutButtonPayload::new
    );
}
