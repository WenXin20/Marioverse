package com.wenxin2.marioverse.network.server_bound.handler;

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
                int bounceCooldown = player.getPersistentData().getInt("marioverse:bounce_cooldown");
                this.bounceEntity(player, payload.isHoldingJump(), bounceCooldown, player.level());
                player.getPersistentData().putBoolean("marioverse:holding_jump", payload.isHoldingJump());
            });
        }
    }

    private void bounceEntity(Entity entity, boolean holdingJump, int bounceCooldown, Level world) {
        Vec3 vec3 = entity.getDeltaMovement();

        entity.resetFallDistance();
        if (vec3.y < 0.0) {
            double baseBounce = 0.42;
            double bounceFactor = (entity instanceof LivingEntity ? 1.0 : 0.8);
            double fallMultiplier = Math.min(entity.fallDistance / 10.0, 2.0);
            double newBounce = Math.max(-vec3.y * bounceFactor * fallMultiplier, baseBounce);

            if (holdingJump)
                newBounce *= 2;

            if (bounceCooldown <= 0) {
                if (world instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticleRingBelowEntity(ParticleTypes.POOF, serverWorld, entity, entity.getBbWidth() / 2, 0.0, 3);
                entity.getPersistentData().putInt("marioverse:bounce_cooldown", 1);
                entity.setDeltaMovement(vec3.x, newBounce, vec3.z);
                entity.hasImpulse = true;
                if (entity instanceof ServerPlayer serverPlayer)
                    serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(entity));
                entity.resetFallDistance();
            }
        }
    }
}