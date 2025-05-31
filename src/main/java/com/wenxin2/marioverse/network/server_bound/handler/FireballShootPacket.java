package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
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

    public void handleFireballShooting(Entity entity) {
        int fireballCount = entity.getPersistentData().getInt("marioverse:fireball_count");
        int fireballCooldown = entity.getPersistentData().getInt("marioverse:fireball_cooldown");

        if (fireballCooldown == 0 && fireballCount < ConfigRegistry.MAX_PLAYER_FIREBALLS.get()) {
            shootFireball(entity);
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", FIREBALL_COOLDOWN);
            entity.getPersistentData().putInt("marioverse:fireball_count", fireballCount + 1);
        } else if (fireballCount >= ConfigRegistry.MAX_PLAYER_FIREBALLS.get()) {
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", ConfigRegistry.FIREBALL_COOLDOWN.get());
            entity.getPersistentData().putInt("marioverse:fireball_count", 0);
        }
    }

    public static void shootFireball(Entity entity) {
        Level world = entity.level();

        BouncingFireballProjectile fireball = new BouncingFireballProjectile(EntityRegistry.BOUNCING_FIREBALL.get(), world);
        fireball.setOwner(entity);
        fireball.setPos(entity.getX(), entity.getEyeY() - 0.5, entity.getZ());
        fireball.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, entity.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(),
                SoundSource.PLAYERS, 1.0F, 1.0F);

        Vec3 look = entity.getLookAngle();
        fireball.setDeltaMovement(look.scale(0.5));

        // Set the fireball's rotation based on the look direction
        fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(fireball);
        world.gameEvent(entity, GameEvent.PROJECTILE_SHOOT, entity.position());
        if (!world.isClientSide())
            PacketDistributor.sendToAllPlayers(new SwingHandPayload(Boolean.TRUE));
    }
}