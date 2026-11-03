package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.network.client_bound.data.PaintingRipplePayload;
import com.wenxin2.marioverse.utils.PaintingRipples;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PaintingRipplePacket {
    public static final PaintingRipplePacket INSTANCE = new PaintingRipplePacket();

    public static PaintingRipplePacket get() {
        return INSTANCE;
    }

    public void handle(final PaintingRipplePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                Entity painting = player.level().getEntity(payload.entityId());

                if (painting != null && PaintingRipples.isRipplePainting(painting))
                    PaintingRipples.start(painting);
            });
        }
    }
}
