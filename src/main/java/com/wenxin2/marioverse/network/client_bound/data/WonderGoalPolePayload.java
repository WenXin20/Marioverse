package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record WonderGoalPolePayload(BlockPos pos, boolean renderRenamedFlag) implements CustomPacketPayload {
    public static final Type<WonderGoalPolePayload> WONDER_GOAL_POLE_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "wonder_goal_pole_payload"));

    @NotNull
    @Override
    public Type<WonderGoalPolePayload> type() {
        return WONDER_GOAL_POLE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, WonderGoalPolePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, WonderGoalPolePayload::pos,
            ByteBufCodecs.BOOL, WonderGoalPolePayload::renderRenamedFlag,
            WonderGoalPolePayload::new
    );
}
