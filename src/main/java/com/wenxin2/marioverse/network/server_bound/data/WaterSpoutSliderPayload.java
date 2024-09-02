package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WaterSpoutSliderPayload(BlockPos pos, int waterSpoutHeight) implements CustomPacketPayload {
    public static final Type<WaterSpoutSliderPayload> SPOUT_HEIGHT_PAYLOAD = new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MODID, "spout_height_payload"));

    @Override
    public Type<WaterSpoutSliderPayload> type() {
        return SPOUT_HEIGHT_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, WaterSpoutSliderPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, WaterSpoutSliderPayload::pos,
            ByteBufCodecs.INT, WaterSpoutSliderPayload::waterSpoutHeight,
            WaterSpoutSliderPayload::new
    );
}
