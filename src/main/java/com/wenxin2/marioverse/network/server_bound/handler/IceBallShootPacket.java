package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.server_bound.data.IceBallShootPayload;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class IceBallShootPacket {
    public static final IceBallShootPacket INSTANCE = new IceBallShootPacket();
    private static final int ICE_BALL_COOLDOWN = 5;

    public static IceBallShootPacket get() {
        return INSTANCE;
    }

    public void handle(final IceBallShootPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player instanceof AbilitiesHandler handler && handler.mv$hasIceFlower())
                    this.handleIceballShooting(player);
            });
        }
    }

    public void handleIceballShooting(Player player) {
        if (player instanceof AbilitiesHandler handler) {
            if (handler.mv$getIceBallCooldown() == 0
                    && handler.mv$getIceBallCount() < ConfigRegistry.MAX_PLAYER_ICE_BALLS.get()) {
                shootIceBall(player);
                handler.mv$setIceBallCooldown(ICE_BALL_COOLDOWN);
                handler.mv$setIceBallCount(handler.mv$getIceBallCount() + 1);
            } else if (handler.mv$getIceBallCount() >= ConfigRegistry.MAX_PLAYER_ICE_BALLS.get()) {
                handler.mv$setIceBallCooldown(ConfigRegistry.ICE_BALL_COOLDOWN.get());
                handler.mv$setIceBallCount(0);
            }
        }
    }

    public static void shootIceBall(Player player) {
        Level world = player.level();
        SoundSource soundSource = SoundSource.PLAYERS;

        BouncingIceBallProjectile iceBall = new BouncingIceBallProjectile(EntityRegistry.BOUNCING_ICE_BALL.get(), world);
        iceBall.setOwner(player);
        iceBall.setPos(player.getX(), player.getEyeY() - 0.5, player.getZ());
        iceBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, player.blockPosition(), SoundRegistry.ICE_BALL_THROWN.get(), soundSource, 1.0F, 1.0F);

        Vec3 look = player.getLookAngle();
        iceBall.setDeltaMovement(look.scale(0.5));

        // Set the ice ball's rotation based on the look direction
        iceBall.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        iceBall.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(iceBall);
        world.gameEvent(player, GameEvent.PROJECTILE_SHOOT, player.position());
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer)
            PacketDistributor.sendToPlayer(serverPlayer, new SwingHandPayload(Boolean.TRUE));
    }
}