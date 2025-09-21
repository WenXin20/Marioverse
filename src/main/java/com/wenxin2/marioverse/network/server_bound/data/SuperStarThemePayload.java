package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SuperStarThemePayload(int fadeDuration, int entityID) implements CustomPacketPayload {
    public static final Type<SuperStarThemePayload> SOUND_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "super_star_theme_sound_payload"));

    @NotNull
    @Override
    public Type<SuperStarThemePayload> type() {
        return SOUND_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, SuperStarThemePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SuperStarThemePayload::fadeDuration,
            ByteBufCodecs.INT, SuperStarThemePayload::entityID,
            SuperStarThemePayload::new
    );
}