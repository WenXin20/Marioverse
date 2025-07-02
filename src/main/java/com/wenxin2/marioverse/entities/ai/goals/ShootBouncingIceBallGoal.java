package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
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
        boolean canShoot = !requireIceFlower|| (livingEntity instanceof AbilitiesHandler handler && handler.mv$hasIceFlower());
        return livingEntity.getDeltaMovement().horizontalDistance() > 0.0F && canShoot;
    }

    @Override
    public void tick() {
        if (canUse()) {
            if ((livingEntity instanceof Monster monster && monster.getTarget() != null && monster.getSensing().hasLineOfSight(monster.getTarget()))
                    || (livingEntity instanceof AbstractGolem golem && golem.getTarget() != null && golem.getSensing().hasLineOfSight(golem.getTarget()))
                    || !(livingEntity instanceof Monster) && !(livingEntity instanceof AbstractGolem))
                handleIceBallShooting();
        }

        if (livingEntity instanceof AbilitiesHandler handler && handler.mv$getIceBallCooldown() > 0)
            handler.mv$setIceBallCooldown(handler.mv$getIceBallCooldown() - 1);

        if (livingEntity instanceof Mob mob) {
            LivingEntity target = mob.getTarget();
            if (target != null) {
                mob.getNavigation().moveTo(target, 1.2);
                if (mob.getControlledVehicle() instanceof Mob)
                    mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
                mob.getLookControl().setLookAt(target.getX(), target.getEyeY(), target.getZ());
            }
        }

        super.tick();
    }

    public void handleIceBallShooting() {
        if (livingEntity instanceof AbilitiesHandler handler) {
            if (!requireIceFlower && handler.mv$getIceBallCooldown() == 0
                    && handler.mv$getIceBallCount() < maxIceBalls + addIceBallsWithIceFlower) {
                this.shootIceBall();
                handler.mv$setIceBallCooldown(ICE_BALL_COOLDOWN);
                handler.mv$setIceBallCount(handler.mv$getIceBallCooldown() + 1);
            } else if (handler.mv$getIceBallCooldown() == 0
                    && handler.mv$getIceBallCount() < maxIceBalls + addIceBallsWithIceFlower
                    && handler.mv$hasIceFlower()) {
                this.shootIceBall();
                handler.mv$setIceBallCooldown(ICE_BALL_COOLDOWN);
                handler.mv$setIceBallCount(handler.mv$getIceBallCooldown() + 1);
            } else if (!requireIceFlower && handler.mv$getIceBallCount() >= maxIceBalls + addIceBallsWithIceFlower) {
                handler.mv$setIceBallCooldown(ConfigRegistry.ICE_BALL_COOLDOWN.get());
                handler.mv$setIceBallCount(0);
            } else if (handler.mv$getIceBallCount() >= maxIceBalls) {
                handler.mv$setIceBallCooldown(ConfigRegistry.ICE_BALL_COOLDOWN.get());
                handler.mv$setIceBallCount(0);
            }
        }
    }

    public void shootIceBall() {
        Level world = livingEntity.level();
        BouncingIceBallProjectile iceBall = new BouncingIceBallProjectile(EntityRegistry.BOUNCING_ICE_BALL.get(), world);

        iceBall.setOwner(livingEntity);
        iceBall.setPos(livingEntity.getX(), livingEntity.getEyeY() - 0.5, livingEntity.getZ());
        iceBall.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, livingEntity.blockPosition(), SoundRegistry.ICE_BALL_THROWN.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

        Vec3 look = livingEntity.getLookAngle();
        iceBall.setDeltaMovement(look.scale(0.5));
        iceBall.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        iceBall.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(iceBall);
        livingEntity.swing(InteractionHand.MAIN_HAND);
    }
}
