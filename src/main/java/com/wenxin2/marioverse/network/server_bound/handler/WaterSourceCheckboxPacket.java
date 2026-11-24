package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.WaterSourceCheckboxPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class WaterSourceCheckboxPacket {
    public static final WaterSourceCheckboxPacket INSTANCE = new WaterSourceCheckboxPacket();

    public static WaterSourceCheckboxPacket get() {
        return INSTANCE;
    }

    public void handle(final WaterSourceCheckboxPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof WarpPipeMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);

                            if (blockEntity instanceof WarpPipeBlockEntity pipeBE && !pipeBE.isWaxed()) {
                                pipeBE.setRequiresWaterSource(payload.requiresWaterSource());
                                pipeBE.sendData();
                            }
                    });
                }
            });
        }
    }
}
