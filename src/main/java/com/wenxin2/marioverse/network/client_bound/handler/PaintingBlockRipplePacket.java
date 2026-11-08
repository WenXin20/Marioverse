package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.network.client_bound.data.PaintingBlockRipplePayload;
import com.wenxin2.marioverse.utils.PaintingRipples;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PaintingBlockRipplePacket {
    public static final PaintingBlockRipplePacket INSTANCE = new PaintingBlockRipplePacket();

    public static PaintingBlockRipplePacket get() {
        return INSTANCE;
    }

    public void handle(final PaintingBlockRipplePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                PaintingRipples.start(player.level(), payload.pos());
            });
        }
    }
}
