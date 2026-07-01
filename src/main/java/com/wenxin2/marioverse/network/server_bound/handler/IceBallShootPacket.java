package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.server_bound.data.IceBallShootPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
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
                if (player.getData(DataAttachmentRegistry.HAS_ICE_FLOWER))
                    this.handleIceballShooting(player);
            });
        }
    }

    public void handleIceballShooting(Player player) {
        if (player.getData(DataAttachmentRegistry.ICE_BALL_COOLDOWN) == 0
                && player.getData(DataAttachmentRegistry.ICE_BALL_COUNT) < ConfigRegistry.MAX_PLAYER_ICE_BALLS.get()) {
            IceBallShootPacket.shootIceBall(player);
            player.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN, ICE_BALL_COOLDOWN);
            player.setData(DataAttachmentRegistry.ICE_BALL_COUNT, player.getData(DataAttachmentRegistry.ICE_BALL_COUNT) + 1);
        } else if (player.getData(DataAttachmentRegistry.ICE_BALL_COUNT) >= ConfigRegistry.MAX_PLAYER_ICE_BALLS.get()) {
            player.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN, ConfigRegistry.ICE_BALL_COOLDOWN.get());
            player.setData(DataAttachmentRegistry.ICE_BALL_COUNT, 0);
        }
    }

    public static void shootIceBall(Player player) {
        Level level = player.level();
        SoundSource soundSource = SoundSource.PLAYERS;
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        BouncingIceBallProjectile iceBall = new BouncingIceBallProjectile(EntityRegistry.BOUNCING_ICE_BALL.get(), level);
        iceBall.setOwner(player);
        iceBall.setPos(player.getX(), player.getEyeY() - 0.5, player.getZ());
        iceBall.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.2F, 1.0F);
        level.playSound(null, player.blockPosition(), SoundRegistry.ICE_BALL_THROWN.get(), soundSource, 1.0F, pitch);

        Vec3 look = player.getLookAngle();
        iceBall.setDeltaMovement(look.scale(0.5));

        // Set the ice ball's rotation based on the look direction
        iceBall.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        iceBall.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        level.addFreshEntity(iceBall);
        level.gameEvent(player, GameEvent.PROJECTILE_SHOOT, player.position());
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer)
            PacketDistributor.sendToPlayer(serverPlayer, new SwingHandPayload(Boolean.TRUE));
    }
}