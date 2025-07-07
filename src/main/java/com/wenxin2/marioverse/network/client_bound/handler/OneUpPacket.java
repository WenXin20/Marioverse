package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.network.client_bound.data.OneUpPayload;
import com.wenxin2.marioverse.registries.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OneUpPacket {
    public static final OneUpPacket INSTANCE = new OneUpPacket();

    public static OneUpPacket get() {
        return INSTANCE;
    }

    public void handle(final OneUpPayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                if (payload.triggerOneUp())
                    Minecraft.getInstance().gameRenderer.displayItemActivation(ItemRegistry.ONE_UP_MUSHROOM.get().getDefaultInstance());
            });
        }
    }
}