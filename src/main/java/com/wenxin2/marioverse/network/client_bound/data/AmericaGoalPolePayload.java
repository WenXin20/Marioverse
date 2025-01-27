package com.wenxin2.marioverse.network.client_bound.data;

import com.wenxin2.marioverse.Marioverse;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record AmericaGoalPolePayload(BlockPos pos, boolean renderRenamedFlag) implements CustomPacketPayload {
    public static final Type<AmericaGoalPolePayload> AMERICA_GOAL_POLE_PAYLOAD =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "america_goal_pole_payload"));

    @NotNull
    @Override
    public Type<AmericaGoalPolePayload> type() {
        return AMERICA_GOAL_POLE_PAYLOAD;
    }

    public static final StreamCodec<FriendlyByteBuf, AmericaGoalPolePayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, AmericaGoalPolePayload::pos,
            ByteBufCodecs.BOOL, AmericaGoalPolePayload::renderRenamedFlag,
            AmericaGoalPolePayload::new
    );
}
