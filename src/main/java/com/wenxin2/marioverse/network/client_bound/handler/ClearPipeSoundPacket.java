package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.client.sounds.PlayClientSound;
import com.wenxin2.marioverse.network.server_bound.data.ClearPipeSoundPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClearPipeSoundPacket {
    public static final ClearPipeSoundPacket INSTANCE = new ClearPipeSoundPacket();

    public static ClearPipeSoundPacket get() {
        return INSTANCE;
    }

    public void handle(final ClearPipeSoundPayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                PlayClientSound.playClearPipeSound(player, payload.fadeInDuration(), payload.fadeOutDuration(), payload.inClearPipe());
            });
        }
    }
}