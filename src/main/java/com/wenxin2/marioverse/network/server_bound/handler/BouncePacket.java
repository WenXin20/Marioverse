package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.BouncyOnBlock;
import com.wenxin2.marioverse.network.server_bound.data.BouncePayload;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
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

public class BouncePacket {
    public static final BouncePacket INSTANCE = new BouncePacket();

    public static BouncePacket get() {
        return INSTANCE;
    }

    public void handle(final BouncePayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                BouncyOnBlock.bounceEntity(player.level(), player, payload.isHoldingJump());
                if (player instanceof ServerPlayer serverPlayer)
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(player));
            });
        }
    }
}