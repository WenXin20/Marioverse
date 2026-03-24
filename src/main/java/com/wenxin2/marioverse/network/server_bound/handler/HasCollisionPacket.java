package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.network.server_bound.data.HasCollisionPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class HasCollisionPacket {
    public static final HasCollisionPacket INSTANCE = new HasCollisionPacket();

    public static HasCollisionPacket get() {
        return INSTANCE;
    }

    public void handle(final HasCollisionPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof BlockSpawnerMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof BlockSpawnerBlockEntity spawnerBE) {
                            spawnerBE.setCollision(payload.hasCollision());
                            spawnerBE.setChanged();
                        }
                    });
                }
            });
        }
    }
}
