package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.network.PacketHandler;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.server_bound.data.IceBallShootPayload;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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
                if (player.getPersistentData().getBoolean("marioverse:has_ice_flower"))
                    this.handleIceballShooting(player);
            });
        }
    }

    public void handleIceballShooting(Entity entity) {
        int iceBallCount = entity.getPersistentData().getInt("marioverse:ice_ball_count");
        int iceBallCooldown = entity.getPersistentData().getInt("marioverse:ice_ball_cooldown");

        // Check if the player can shoot a iceball
        if (iceBallCooldown == 0 && iceBallCount < ConfigRegistry.MAX_PLAYER_ICE_BALLS.get()) {
            shootIceBall(entity);
            entity.getPersistentData().putInt("marioverse:ice_ball_cooldown", ICE_BALL_COOLDOWN); // Reset cooldown
            entity.getPersistentData().putInt("marioverse:ice_ball_count", iceBallCount + 1); // Increase active iceball count
        } else if (iceBallCount >= ConfigRegistry.MAX_PLAYER_ICE_BALLS.get()) {
            entity.getPersistentData().putInt("marioverse:ice_ball_cooldown", ConfigRegistry.ICE_BALL_COOLDOWN.get()); // Reset with higher cooldown
            entity.getPersistentData().putInt("marioverse:ice_ball_count", 0);
        }
    }

    public static void shootIceBall(Entity entity) {
        Level world = entity.level();

        BouncingIceBallProjectile iceBall = new BouncingIceBallProjectile(EntityRegistry.BOUNCING_ICE_BALL.get(), world);
        iceBall.setOwner(entity);
        iceBall.setPos(entity.getX(), entity.getEyeY() - 0.5, entity.getZ());
        iceBall.shootFromRotation(entity, entity.getXRot(), entity.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, entity.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(),
                SoundSource.PLAYERS, 1.0F, 1.0F); // TODO

        Vec3 look = entity.getLookAngle();
        iceBall.setDeltaMovement(look.scale(0.5));

        // Set the ice ball's rotation based on the look direction
        iceBall.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90); // Adjust for correct facing
        iceBall.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(iceBall);
        PacketHandler.sendToAllClients(new SwingHandPayload(Boolean.TRUE));
    }
}