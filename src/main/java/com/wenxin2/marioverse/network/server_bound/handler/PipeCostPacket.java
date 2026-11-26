package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.PipeCostPayload;
import com.wenxin2.marioverse.utils.WarpCost;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PipeCostPacket {
    public static final PipeCostPacket INSTANCE = new PipeCostPacket();

    public static PipeCostPacket get() {
        return INSTANCE;
    }

    public void handle(final PipeCostPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId != payload.containerId() || !(player.containerMenu instanceof WarpPipeMenu menu))
                    return;

                menu.getAccess().execute((level, pos) -> {
                    if (!(level.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE)
                            || !pipeBE.canManagePayments(player) || WarpCost.hasConfigPipeCost())
                        return;

                    switch (payload.action()) {
                        case PipeCostPayload.ITEM_COUNT -> pipeBE.setCostItemCount(payload.value());
                        case PipeCostPayload.XP_COST -> pipeBE.setXpCost(payload.value());
                    }
                });
            });
        }
    }
}