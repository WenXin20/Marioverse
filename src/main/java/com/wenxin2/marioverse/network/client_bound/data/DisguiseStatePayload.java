package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public record DisguiseStatePayload(BlockPos pos, BlockState disguiseState) implements CustomPacketPayload {
    public static final Type<DisguiseStatePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "disguise_state_payload"));

    @NotNull
    @Override
    public Type<DisguiseStatePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, DisguiseStatePayload> STREAM_CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, DisguiseStatePayload::pos,
            ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY), DisguiseStatePayload::disguiseState,
            DisguiseStatePayload::new
    );
}
