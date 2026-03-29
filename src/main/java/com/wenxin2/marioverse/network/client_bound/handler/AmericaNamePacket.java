package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.blocks.entities.CheckpointFlagBlockEntity;
import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AmericaNamePacket {
    public static final AmericaNamePacket INSTANCE = new AmericaNamePacket();

    public static AmericaNamePacket get() {
        return INSTANCE;
    }

    public void handle(final AmericaNamePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Level level = context.player().level();
                BlockEntity blockEntity = level.getBlockEntity(payload.pos());
                if (blockEntity instanceof GoalPoleBlockEntity goalPoleBE) {
                    goalPoleBE.setAmericanFlag(payload.renderRenamedFlag());
                    goalPoleBE.markUpdated();
                    goalPoleBE.markUpdatedClients();
                }

                if (blockEntity instanceof CheckpointFlagBlockEntity checkpointFlagBE) {
                    checkpointFlagBE.setAmericanFlag(payload.renderRenamedFlag());
                    checkpointFlagBE.markUpdated();
                    checkpointFlagBE.markUpdatedClients();
                }
            });
        }
    }
}
