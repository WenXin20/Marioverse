package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record FireballShootPayload(BlockPos pos) implements CustomPacketPayload {
    public static final Type<FireballShootPayload> FIREBALL_SHOOT_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "fireball_shoot_payload"));

    @NotNull
    @Override
    public Type<FireballShootPayload> type() {
        return FIREBALL_SHOOT_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, FireballShootPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, FireballShootPayload::pos,
            FireballShootPayload::new
    );
}