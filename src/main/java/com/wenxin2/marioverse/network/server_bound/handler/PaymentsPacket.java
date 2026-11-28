package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.PaymentsPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PaymentsPacket {
    public static final PaymentsPacket INSTANCE = new PaymentsPacket();

    public static PaymentsPacket get() {
        return INSTANCE;
    }

    public void handle(final PaymentsPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId != payload.containerId() || !(player.containerMenu instanceof WarpPipeMenu menu))
                    return;

                menu.getAccess().execute((level, pos) -> {
                    if (!(level.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE) || !pipeBE.canManagePayments(player))
                        return;

                    switch (payload.action()) {
                        case PaymentsPayload.COLLECT_XP -> pipeBE.collectXp(player);
                        case PaymentsPayload.TAKE_ONE -> pipeBE.collectPayments(player, 1);
                        case PaymentsPayload.TOGGLE_AUTOMATION -> pipeBE.setAllowAutomation(!pipeBE.isAllowAutomation());
                        case PaymentsPayload.TOGGLE_VOID -> pipeBE.setVoidPayments(!pipeBE.isVoidPayments());
                    }
                });
            });
        }
    }
}