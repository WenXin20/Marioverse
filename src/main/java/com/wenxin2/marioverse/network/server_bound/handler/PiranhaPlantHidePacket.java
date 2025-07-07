package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.network.server_bound.data.PiranhaPlantHidePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PiranhaPlantHidePacket {
    public static final PiranhaPlantHidePacket INSTANCE = new PiranhaPlantHidePacket();

    public static PiranhaPlantHidePacket get() {
        return INSTANCE;
    }

    public void handle(final PiranhaPlantHidePayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (context.player().level() instanceof ServerLevel serverWorld) {
                    Entity entity = serverWorld.getEntity(payload.entityID());
                    if (entity instanceof PiranhaPlantEntity piranha)
                        piranha.hide(payload.isHiding());
                }
            });
        }
    }
}