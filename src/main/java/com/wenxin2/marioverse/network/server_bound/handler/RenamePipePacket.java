package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.network.server_bound.data.RenamePipePayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RenamePipePacket {
    public static final RenamePipePacket INSTANCE = new RenamePipePacket();

    public static RenamePipePacket get() {
        return INSTANCE;
    }

    public void handle(final RenamePipePayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (payload.pos() == null || payload.customName() == null)
                    return;
                ServerPlayer player = (ServerPlayer) context.player();
                Level world = player.level();
                BlockEntity blockEntity = world.getBlockEntity(payload.pos());
                if (blockEntity instanceof WarpPipeBlockEntity) {
                    ((WarpPipeBlockEntity) blockEntity).sendData();
                    ((WarpPipeBlockEntity) blockEntity).setCustomName(Component.literal(payload.customName())); // Check this
                    ((WarpPipeBlockEntity) blockEntity).updateText(pipeText -> pipeText.setMessage(0, Component.literal(payload.customName())));
                    ((WarpPipeBlockEntity) blockEntity).markUpdated();
//                    blockEntity.getUpdateTag(blockEntity.getLevel().registryAccess());
                }
            });
        }
    }
}
