package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.network.server_bound.data.DoubleJumpPayload;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class DoubleJumpPacket {
    public static final DoubleJumpPacket INSTANCE = new DoubleJumpPacket();

    public static DoubleJumpPacket get() {
        return INSTANCE;
    }

    public void handle(final DoubleJumpPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (!(context.player() instanceof ServerPlayer player))
                    return;
                if (!player.getData(DataAttachmentRegistry.HAS_DOUBLE_JUMP))
                    return;
                if (player.onGround())
                    return;

                Vec3 motion = player.getDeltaMovement();
                double jumpStrength = player.getAttributeValue(Attributes.JUMP_STRENGTH);
                int minAirborneDuration = 5;

                if (player.getData(DataAttachmentRegistry.AIRBORNE_DURATION) < minAirborneDuration)
                    return;

                player.setDeltaMovement(motion.x, jumpStrength, motion.z);
                player.setData(DataAttachmentRegistry.HAS_DOUBLE_JUMP.get(), false);
                player.connection.send(new ClientboundSetEntityMotionPacket(player));
                player.hasImpulse = true;
                player.level().playSound(null, player.blockPosition(), SoundRegistry.PLAYER_JUMP.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);

                if (player.level() instanceof ServerLevel serverLevel)
                    ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.CLOUD, serverLevel, player,
                            player.getBbWidth() / 2, 0.05, 8);
            });
        }
    }
}