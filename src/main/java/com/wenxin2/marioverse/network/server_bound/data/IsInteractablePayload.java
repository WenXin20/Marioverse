package com.wenxin2.marioverse.network.server_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record IsInteractablePayload(int containerId, int isInteractable) implements CustomPacketPayload {
    public static final Type<IsInteractablePayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "is_interactable_payload"));

    @NotNull
    @Override
    public Type<IsInteractablePayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, IsInteractablePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, IsInteractablePayload::containerId,
                    ByteBufCodecs.INT, IsInteractablePayload::isInteractable,
                    IsInteractablePayload::new);
}