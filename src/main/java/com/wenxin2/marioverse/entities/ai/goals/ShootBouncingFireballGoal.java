package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.EnumSet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShootBouncingFireballGoal extends Goal {
    private final LivingEntity livingEntity;
    private static final int FIREBALL_COOLDOWN = 5;

    public ShootBouncingFireballGoal(LivingEntity entity) {
        this.livingEntity = entity;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK)); // Example flags, adjust as needed
    }

    @Override
    public boolean canUse() {
        return livingEntity.getPersistentData().getBoolean("marioverse:has_fire_flower")
                && (livingEntity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS)
                || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())
                && !(livingEntity instanceof Player)
                && !(livingEntity instanceof ArmorStand)
                && (livingEntity.getDeltaMovement().x > 0.0F || livingEntity.getDeltaMovement().z > 0.0F);
    }

    @Override
    public void tick() {
        if (canUse()) {
            if ((livingEntity instanceof Monster monster && monster.getTarget() != null)
                    || (livingEntity instanceof AbstractGolem golem && golem.getTarget() != null)
                    || !(livingEntity instanceof Monster) && !(livingEntity instanceof AbstractGolem)) {

                handleFireballShooting();
            }
        }

        int fireballCooldown = livingEntity.getPersistentData().getInt("marioverse:fireball_cooldown");
        if (fireballCooldown > 0) {
            livingEntity.getPersistentData().putInt("marioverse:fireball_cooldown", fireballCooldown - 1);
        }
    }

    public void handleFireballShooting() {
        int fireballCount = livingEntity.getPersistentData().getInt("marioverse:fireball_count");
        int fireballCooldown = livingEntity.getPersistentData().getInt("marioverse:fireball_cooldown");

        if (fireballCooldown == 0 && fireballCount < ConfigRegistry.MAX_FIREBALLS.get()) {
            shootFireball();
            livingEntity.getPersistentData().putInt("marioverse:fireball_cooldown", FIREBALL_COOLDOWN);
            livingEntity.getPersistentData().putInt("marioverse:fireball_count", fireballCount + 1);
        } else if (fireballCount >= ConfigRegistry.MAX_FIREBALLS.get()) {
            livingEntity.getPersistentData().putInt("marioverse:fireball_cooldown", ConfigRegistry.FIREBALL_COOLDOWN.get());
            livingEntity.getPersistentData().putInt("marioverse:fireball_count", 0);
        }
    }

    public void shootFireball() {
        Level world = livingEntity.level();
        BouncingFireballProjectile fireball = new BouncingFireballProjectile(EntityRegistry.BOUNCING_FIREBALL.get(), world);
        fireball.setOwner(livingEntity);
        fireball.setPos(livingEntity.getX(), livingEntity.getEyeY() - 0.5, livingEntity.getZ());
        fireball.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, livingEntity.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

        Vec3 look = livingEntity.getLookAngle();
        fireball.setDeltaMovement(look.scale(0.5));
        fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(fireball);
        livingEntity.swing(InteractionHand.MAIN_HAND);
    }
}
