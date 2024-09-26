package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.server_bound.data.ClosePipeButtonPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SwingHandPacket {
    public static final SwingHandPacket INSTANCE = new SwingHandPacket();

    public static SwingHandPacket get() {
        return INSTANCE;
    }

    public void handle(final SwingHandPayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player != null)
                    player.swing(InteractionHand.MAIN_HAND);
            });
        }
    }
}
