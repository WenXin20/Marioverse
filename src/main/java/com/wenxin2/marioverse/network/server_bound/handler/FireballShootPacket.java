package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
                if (player.getPersistentData().getBoolean("marioverse:has_fire_flower"))
                    this.handleFireballShooting(player);
            });
        }
    }

    public void handleFireballShooting(Entity entity) {
        int fireballCount = entity.getPersistentData().getInt("marioverse:fireball_count");
        int fireballCooldown = entity.getPersistentData().getInt("marioverse:fireball_cooldown");

        // Check if the player can shoot a fireball
        if (fireballCooldown == 0 && fireballCount < ConfigRegistry.MAX_PLAYER_FIREBALLS.get()) {
            shootFireball(entity);
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", FIREBALL_COOLDOWN); // Reset cooldown
            entity.getPersistentData().putInt("marioverse:fireball_count", fireballCount + 1); // Increase active fireball count
        } else if (fireballCount >= ConfigRegistry.MAX_PLAYER_FIREBALLS.get()) {
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", ConfigRegistry.FIREBALL_COOLDOWN.get()); // Reset with higher cooldown
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
        fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90); // Adjust for correct facing
        fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(fireball);
        PacketHandler.sendToAllClients(new SwingHandPayload(Boolean.TRUE));
    }
}