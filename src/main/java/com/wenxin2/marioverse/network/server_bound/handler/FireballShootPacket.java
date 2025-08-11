package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
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

public class FireballShootPacket {
    public static final FireballShootPacket INSTANCE = new FireballShootPacket();
    private static final int FIREBALL_COOLDOWN = 5;

    public static FireballShootPacket get() {
        return INSTANCE;
    }

    public void handle(final FireballShootPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                Player player = context.player();
                if (player instanceof AbilitiesHandler handler && handler.mv$hasFireFlower())
                    this.handleFireballShooting(player);
            });
        }
    }

    public void handleFireballShooting(Player player) {
        if (player instanceof AbilitiesHandler handler) {
            if (handler.mv$getFireballCooldown() == 0 && handler.mv$getFireballCount() < ConfigRegistry.MAX_PLAYER_FIREBALLS.get()) {
                shootFireball(player);
                handler.mv$setFireballCooldown(FIREBALL_COOLDOWN);
                handler.mv$setFireballCount(handler.mv$getFireballCount() + 1);
            } else if (handler.mv$getFireballCount() >= ConfigRegistry.MAX_PLAYER_FIREBALLS.get()) {
                handler.mv$setFireballCooldown(ConfigRegistry.FIREBALL_COOLDOWN.get());
                handler.mv$setFireballCount(0);
            }
        }
    }

    public static void shootFireball(Player player) {
        Level world = player.level();
        SoundSource soundSource = SoundSource.PLAYERS;

        BouncingFireballProjectile fireball = new BouncingFireballProjectile(EntityRegistry.BOUNCING_FIREBALL.get(), world);
        fireball.setOwner(player);
        fireball.setPos(player.getX(), player.getEyeY() - 0.5, player.getZ());
        fireball.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, player.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(), soundSource, 1.0F, 1.0F);

        Vec3 look = player.getLookAngle();
        fireball.setDeltaMovement(look.scale(0.5));

        // Set the fireball's rotation based on the look direction
        fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(fireball);
        world.gameEvent(player, GameEvent.PROJECTILE_SHOOT, player.position());
        if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer)
            PacketDistributor.sendToPlayer(serverPlayer, new SwingHandPayload(Boolean.TRUE));
    }
}