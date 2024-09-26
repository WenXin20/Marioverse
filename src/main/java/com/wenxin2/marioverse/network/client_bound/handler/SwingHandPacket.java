package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
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
