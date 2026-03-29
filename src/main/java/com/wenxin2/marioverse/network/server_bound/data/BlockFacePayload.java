package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record BlockFacePayload(int containerId, int blockFace) implements CustomPacketPayload {
    public static final Type<BlockFacePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "block_face_payload"));

    @NotNull
    @Override
    public Type<BlockFacePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, BlockFacePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, BlockFacePayload::containerId,
                    ByteBufCodecs.INT, BlockFacePayload::blockFace,
                    BlockFacePayload::new);
}
