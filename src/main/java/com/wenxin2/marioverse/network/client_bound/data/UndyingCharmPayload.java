package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record UndyingCharmPayload(Boolean triggerUndyingCharm, ItemStack stack) implements CustomPacketPayload {
    public static final Type<UndyingCharmPayload> PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "undying_charm_payload"));

    @NotNull
    @Override
    public Type<UndyingCharmPayload> type() {
        return PAYLOAD;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, UndyingCharmPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, UndyingCharmPayload::triggerUndyingCharm,
            ItemStack.STREAM_CODEC, UndyingCharmPayload::stack,
            UndyingCharmPayload::new
    );
}
