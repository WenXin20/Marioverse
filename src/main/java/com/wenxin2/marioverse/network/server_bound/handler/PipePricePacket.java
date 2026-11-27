package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.PipePricePayload;
import com.wenxin2.marioverse.utils.WarpPrice;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PipePricePacket {
    public static final PipePricePacket INSTANCE = new PipePricePacket();

    public static PipePricePacket get() {
        return INSTANCE;
    }

    public void handle(final PipePricePayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId != payload.containerId() || !(player.containerMenu instanceof WarpPipeMenu menu))
                    return;

                menu.getAccess().execute((level, pos) -> {
                    if (!(level.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE)
                            || !pipeBE.canManagePayments(player) || WarpPrice.hasConfigPipePrice())
                        return;

                    switch (payload.action()) {
                        case PipePricePayload.ITEM_COUNT -> pipeBE.setPriceItemCount(payload.value());
                        case PipePricePayload.XP_PRICE -> pipeBE.setXpPrice(payload.value());
                    }
                });
            });
        }
    }
}