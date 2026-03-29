package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.inventory.QuestionBlockMenu;
import com.wenxin2.marioverse.network.server_bound.data.RefillCountdownPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RefillCountdownPacket {
    public static final RefillCountdownPacket INSTANCE = new RefillCountdownPacket();

    public static RefillCountdownPacket get() {
        return INSTANCE;
    }

    public void handle(final RefillCountdownPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof QuestionBlockMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);

                        if (blockEntity instanceof QuestionBlockEntity questionBE) {
                            questionBE.setRefillCountdown(questionBE.convertToTicks(payload.refillCountdown()));
                            questionBE.setChanged();
                        }
                    });
                }

                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof BlockSpawnerMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);

                        if (blockEntity instanceof BlockSpawnerBlockEntity spawnerBE) {
                            spawnerBE.setRefillCountdown(spawnerBE.convertToTicks(payload.refillCountdown()));
                            spawnerBE.setChanged();
                        }
                    });
                }
            });
        }
    }
}
