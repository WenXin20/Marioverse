package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class WonderNamePacket {
    public static final WonderNamePacket INSTANCE = new WonderNamePacket();

    public static WonderNamePacket get() {
        return INSTANCE;
    }

    public void handle(final WonderNamePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    BlockEntity blockEntity = level.getBlockEntity(payload.pos());
                    if (blockEntity instanceof GoalPoleBlockEntity goalPoleBE) {
                        goalPoleBE.setWonderFlag(payload.renderRenamedFlag());
                        goalPoleBE.markUpdated();
                        goalPoleBE.markUpdatedClients();
                    }

                    if (blockEntity instanceof CheckpointFlagBlockEntity checkpointFlagBE) {
                        checkpointFlagBE.setWonderFlag(payload.renderRenamedFlag());
                        checkpointFlagBE.markUpdated();
                        checkpointFlagBE.markUpdatedClients();
                    }
                }
            });
        }
    }
}
