package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record MenuTypePayload(int containerId, int menuTypeID) implements CustomPacketPayload {
    public static final Type<MenuTypePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "menu_type_payload"));

    @NotNull
    @Override
    public Type<MenuTypePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, MenuTypePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, MenuTypePayload::containerId,
                    ByteBufCodecs.INT, MenuTypePayload::menuTypeID,
                    MenuTypePayload::new);
}