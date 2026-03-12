package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.network.server_bound.data.TimeUnitPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TimeUnitPacket {
    public static final TimeUnitPacket INSTANCE = new TimeUnitPacket();

    public static TimeUnitPacket get() {
        return INSTANCE;
    }

    public void handle(final TimeUnitPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof QuestionBlockMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof QuestionBlockEntity questionBE) {
                            questionBE.setTimeUnit(payload.timeUnitID());
                            questionBE.setChanged();
                        }
                    });
                }

                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof BlockSpawnerMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof BlockSpawnerBlockEntity spawnerBE) {
                            spawnerBE.setTimeUnit(payload.timeUnitID());
                            spawnerBE.setChanged();
                        }
                    });
                }
            });
        }
    }
}
