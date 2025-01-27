package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.network.client_bound.data.AmericaGoalPolePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class AmericaGoalPolePacket {
    public static final AmericaGoalPolePacket INSTANCE = new AmericaGoalPolePacket();

    public static AmericaGoalPolePacket get() {
        return INSTANCE;
    }

    public void handle(final AmericaGoalPolePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    BlockEntity blockEntity = level.getBlockEntity(payload.pos());
                    if (blockEntity instanceof GoalPoleBlockEntity goalPoleBE) {
                        goalPoleBE.setAmericanFlag(payload.renderRenamedFlag());
                        goalPoleBE.markUpdated();
                        goalPoleBE.markUpdatedClients();
                    }
                }
            });
        }
    }
}
