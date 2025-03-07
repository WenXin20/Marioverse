package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.EntityRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import java.util.EnumSet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShootBouncingIceBallGoal extends Goal {
    private final LivingEntity livingEntity;
    private final int maxIceBalls;
    private final int addIceBallsWithIceFlower;
    private final boolean requireIceFlower;
    private static final int ICE_BALL_COOLDOWN = 5;

    public ShootBouncingIceBallGoal(LivingEntity entity, int maxIceBalls, int addIceBallsWithIceFlower, boolean requireIceFlower) {
        this.livingEntity = entity;
        this.maxIceBalls = maxIceBalls;
        this.addIceBallsWithIceFlower = addIceBallsWithIceFlower;
        this.requireIceFlower = requireIceFlower;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
    }

    @Override
    public void start() {
        super.start();
        if (livingEntity instanceof Mob mob)
            mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        if (livingEntity instanceof Mob mob)
            mob.setAggressive(false);
    }

    @Override
    public boolean canUse() {
        boolean canShoot = !requireIceFlower || livingEntity.getPersistentData().getBoolean("marioverse:has_ice_flower");
        return livingEntity.getDeltaMovement().horizontalDistance() > 0.0F && canShoot;
    }

    @Override
    public void tick() {
        if (canUse()) {
            if ((livingEntity instanceof Monster monster && monster.getTarget() != null && monster.getSensing().hasLineOfSight(monster.getTarget()))
                    || (livingEntity instanceof AbstractGolem golem && golem.getTarget() != null && golem.getSensing().hasLineOfSight(golem.getTarget()))
                    || !(livingEntity instanceof Monster) && !(livingEntity instanceof AbstractGolem)) {
                handleIceBallShooting();
            }
        }

        int iceBallCooldown = livingEntity.getPersistentData().getInt("marioverse:ice_ball_cooldown");
        if (iceBallCooldown > 0) {
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_cooldown", iceBallCooldown - 1);
        }

        if (livingEntity instanceof Mob mob) {
            LivingEntity livingentity = mob.getTarget();
            if (livingentity != null) {
                mob.getNavigation().moveTo(livingentity, livingentity.getAttributeValue(Attributes.MOVEMENT_SPEED));
                if (mob.getControlledVehicle() instanceof Mob)
                    mob.lookAt(livingentity, 30.0F, 30.0F);
                mob.lookAt(livingentity, 30.0F, 30.0F);
            }
        }

        super.tick();
    }

    public void handleIceBallShooting() {
        int iceBallCount = livingEntity.getPersistentData().getInt("marioverse:ice_ball_count");
        int iceBallCooldown = livingEntity.getPersistentData().getInt("marioverse:ice_ball_cooldown");

        if (!requireIceFlower && iceBallCooldown == 0 && iceBallCount < maxIceBalls + addIceBallsWithIceFlower) {
            this.shootIceBall();
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_cooldown", ICE_BALL_COOLDOWN);
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_count", iceBallCount + 1);
        } else if (iceBallCooldown == 0 && iceBallCount < maxIceBalls + addIceBallsWithIceFlower
                && livingEntity.getPersistentData().getBoolean("marioverse:has_ice_flower")) {
            this.shootIceBall();
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_cooldown", ICE_BALL_COOLDOWN);
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_count", iceBallCount + 1);
        } else if (!requireIceFlower && iceBallCount >= maxIceBalls + addIceBallsWithIceFlower) {
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_cooldown", ConfigRegistry.ICE_BALL_COOLDOWN.get());
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_count", 0);
        } else if (iceBallCount >= maxIceBalls) {
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_cooldown", ConfigRegistry.ICE_BALL_COOLDOWN.get());
            livingEntity.getPersistentData().putInt("marioverse:ice_ball_count", 0);
        }
    }

    public void shootIceBall() {
        Level world = livingEntity.level();
        BouncingIceBallProjectile iceBall = new BouncingIceBallProjectile(EntityRegistry.BOUNCING_ICE_BALL.get(), world);

        iceBall.setOwner(livingEntity);
        iceBall.setPos(livingEntity.getX(), livingEntity.getEyeY() - 0.5, livingEntity.getZ());
        iceBall.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, livingEntity.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F); // TODO

        Vec3 look = livingEntity.getLookAngle();
        iceBall.setDeltaMovement(look.scale(0.5));
        iceBall.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        iceBall.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(iceBall);
        livingEntity.swing(InteractionHand.MAIN_HAND);
    }
}
