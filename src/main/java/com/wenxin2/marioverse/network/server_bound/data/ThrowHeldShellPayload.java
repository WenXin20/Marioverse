package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record ThrowHeldShellPayload(boolean place, BlockPos placePos) implements CustomPacketPayload {
    public static final Type<ThrowHeldShellPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "throw_held_shell_payload"));

    @NotNull
    @Override
    public Type<ThrowHeldShellPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, ThrowHeldShellPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, ThrowHeldShellPayload::place,
                    BlockPos.STREAM_CODEC, ThrowHeldShellPayload::placePos,
                    ThrowHeldShellPayload::new);
}
