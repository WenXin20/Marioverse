package com.wenxin2.marioverse.network.client_bound.handler;

import com.wenxin2.marioverse.network.client_bound.data.OneUpPayload;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OneUpPacket {
    public static final OneUpPacket INSTANCE = new OneUpPacket();

    public static OneUpPacket get() {
        return INSTANCE;
    }

    public void handle(final OneUpPayload payload, IPayloadContext context) {
        if (context.flow().isClientbound()) {
            context.enqueueWork(() -> {
                if (payload.triggerOneUp())
                    Minecraft.getInstance().gameRenderer.displayItemActivation(ItemRegistry.ONE_UP_MUSHROOM.get().getDefaultInstance());
            });
        }
    }
}