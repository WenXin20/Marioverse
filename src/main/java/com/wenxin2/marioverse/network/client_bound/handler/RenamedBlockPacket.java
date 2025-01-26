package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.blocks.entities.GoalPoleBlockEntity;
import com.wenxin2.marioverse.network.client_bound.data.RenamedBlockPayload;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RenamedBlockPacket {
    public static final RenamedBlockPacket INSTANCE = new RenamedBlockPacket();

    public static RenamedBlockPacket get() {
        return INSTANCE;
    }

    public void handle(final RenamedBlockPayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Level level = Minecraft.getInstance().level;
                if (level != null) {
                    BlockEntity blockEntity = level.getBlockEntity(payload.pos());
                    if (blockEntity instanceof GoalPoleBlockEntity goalPoleBE) {
                        goalPoleBE.setWonderFlag(payload.renderWonderFlag());
                        goalPoleBE.markUpdated();
                        goalPoleBE.markUpdatedClients();
                    }
                }
            });
        }
    }
}
