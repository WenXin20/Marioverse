package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.blocks.entities.DisguisedBlockEntity;
import com.wenxin2.marioverse.network.client_bound.data.DisguiseStatePayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DisguiseStatePacket {
    public static final DisguiseStatePacket INSTANCE = new DisguiseStatePacket();

    public static DisguiseStatePacket get() {
        return INSTANCE;
    }

    public void handle(final DisguiseStatePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Level level = context.player().level();
                BlockEntity blockEntity = level.getBlockEntity(payload.pos());
                if (blockEntity instanceof DisguisedBlockEntity disguisedBE) {
                    disguisedBE.setDisguise();
                    disguisedBE.setDisguiseState(payload.disguiseState());
                    disguisedBE.requestModelDataUpdate();
                    disguisedBE.setChanged();
                }
            });
        }
    }
}