package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.event_handlers.KeybindHandler;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class FireballShootPacket {
    public static final FireballShootPacket INSTANCE = new FireballShootPacket();

    public static FireballShootPacket get() {
        return INSTANCE;
    }

    public void handle(final FireballShootPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.getPersistentData().getBoolean("marioverse:has_fire_flower"))
                    this.handleFireballShooting(player);
            });
        }
    }

    public void handleFireballShooting(Entity entity) {
        // Check if the player can shoot a fireball
        if (BouncingFireballProjectile.fireballCooldown == 0 && BouncingFireballProjectile.fireballCount < BouncingFireballProjectile.MAX_FIREBALLS) {
            if ((KeybindHandler.FIREBALL_SHOOT_KEY.isDown() || entity.isSprinting())) {
                shootFireball(entity);
                BouncingFireballProjectile.fireballCooldown = BouncingFireballProjectile.FIREBALL_DELAY; // Reset cooldown
                BouncingFireballProjectile.fireballCount++; // Increase active fireball count
            }
        }
    }

    public static void shootFireball(Entity entity) {
        Level world = entity.level();
        Player player = (Player) entity;
        if (!world.isClientSide()) {
            BouncingFireballProjectile fireball = new BouncingFireballProjectile(EntityRegistry.BOUNCING_FIREBALL.get(), world);
            fireball.setOwner(entity);
            fireball.setPos(entity.getX(), entity.getEyeY() - 0.2, entity.getZ());
            player.swing(player.getUsedItemHand());

            Vec3 look = entity.getLookAngle();
            fireball.setDeltaMovement(look.scale(0.5));

            // Set the fireball's rotation based on the look direction
            fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90); // Adjust for correct facing
            fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

            world.addFreshEntity(fireball);
        }
    }
}
