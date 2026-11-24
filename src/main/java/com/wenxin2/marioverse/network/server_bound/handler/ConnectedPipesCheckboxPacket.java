package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.ConnectedPipesCheckboxPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ConnectedPipesCheckboxPacket {
    public static final ConnectedPipesCheckboxPacket INSTANCE = new ConnectedPipesCheckboxPacket();

    public static ConnectedPipesCheckboxPacket get() {
        return INSTANCE;
    }

    public void handle(final ConnectedPipesCheckboxPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof WarpPipeMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);

                            if (blockEntity instanceof WarpPipeBlockEntity pipeBE && !pipeBE.isWaxed())
                                pipeBE.updateRequiresConnection(payload.requiresConnection());
                    });
                }
            });
        }
    }
}
