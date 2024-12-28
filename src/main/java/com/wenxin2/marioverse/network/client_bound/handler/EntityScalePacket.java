package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.network.client_bound.data.EntityScalePayload;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EntityScalePacket {
    public static final EntityScalePacket INSTANCE = new EntityScalePacket();

    public static EntityScalePacket get() {
        return INSTANCE;
    }

    public void handle(final EntityScalePayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                Level world = Minecraft.getInstance().level;
                Entity entity = world.getEntity(payload.entityId());
                if (entity != null) {
                    entity.setBoundingBox(EntityDimensions.scalable(payload.width(), payload.height()).makeBoundingBox(entity.position()));
                    entity.refreshDimensions();
                }
            });
        }
    }
}
