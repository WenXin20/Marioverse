package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.client.sounds.PlayClientSound;
import com.wenxin2.marioverse.network.server_bound.data.SuperStarThemePayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SuperStarThemePacket {
    public static final SuperStarThemePacket INSTANCE = new SuperStarThemePacket();

    public static SuperStarThemePacket get() {
        return INSTANCE;
    }

    public void handle(final SuperStarThemePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> PlayClientSound.playSuperStarSound(payload, payload.fadeDuration()));
        }
    }
}