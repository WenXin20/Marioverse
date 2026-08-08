package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.network.client_bound.data.UndyingCharmPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class UndyingCharmPacket {
    public static final UndyingCharmPacket INSTANCE = new UndyingCharmPacket();

    public static UndyingCharmPacket get() {
        return INSTANCE;
    }

    public void handle(final UndyingCharmPayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                if (payload.triggerUndyingCharm() && !payload.stack().isEmpty())
                    Minecraft.getInstance().gameRenderer.displayItemActivation(payload.stack());
            });
        }
    }
}