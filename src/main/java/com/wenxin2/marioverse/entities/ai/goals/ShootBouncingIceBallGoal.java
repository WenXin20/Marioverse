package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.EnumSet;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
        if (this.livingEntity instanceof Mob mob)
            mob.setAggressive(true);
    }

    @Override
    public void stop() {
        super.stop();
        if (this.livingEntity instanceof Mob mob)
            mob.setAggressive(false);
    }

    @Override
    public boolean canUse() {
        boolean canShoot = !this.requireIceFlower || this.livingEntity.getData(DataAttachmentRegistry.HAS_ICE_FLOWER);
        return this.livingEntity.getDeltaMovement().horizontalDistance() > 0.0F && canShoot;
    }

    @Override
    public void tick() {
        if (canUse()) {
            if ((this.livingEntity instanceof Monster monster && monster.getTarget() != null && monster.getSensing().hasLineOfSight(monster.getTarget()))
                    || (this.livingEntity instanceof AbstractGolem golem && golem.getTarget() != null && golem.getSensing().hasLineOfSight(golem.getTarget()))
                    || !(this.livingEntity instanceof Monster) && !(this.livingEntity instanceof AbstractGolem))
                handleIceBallShooting();
        }

        if (this.livingEntity.getData(DataAttachmentRegistry.ICE_BALL_COOLDOWN) > 0)
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN,
                    this.livingEntity.getData(DataAttachmentRegistry.ICE_BALL_COOLDOWN) - 1);

        if (this.livingEntity instanceof Mob mob) {
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
        int iceBallCooldown = this.livingEntity.getData(DataAttachmentRegistry.ICE_BALL_COOLDOWN);
        int iceBallCount = this.livingEntity.getData(DataAttachmentRegistry.ICE_BALL_COUNT);

        if (!this.requireIceFlower && iceBallCooldown == 0
                && iceBallCount < this.maxIceBalls + this.addIceBallsWithIceFlower) {
            this.shootIceBall();
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN, ICE_BALL_COOLDOWN);
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COUNT, iceBallCount + 1);
        } else if (iceBallCooldown == 0
                && iceBallCount < this.maxIceBalls + this.addIceBallsWithIceFlower
                && this.livingEntity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)) {
            this.shootIceBall();
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN, ICE_BALL_COOLDOWN);
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COUNT, iceBallCount + 1);
        } else if (!this.requireIceFlower && iceBallCount >= this.maxIceBalls + this.addIceBallsWithIceFlower) {
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN, ConfigRegistry.FIREBALL_COOLDOWN.get());
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COUNT, 0);
        } else if (iceBallCount >= this.maxIceBalls) {
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN, ConfigRegistry.FIREBALL_COOLDOWN.get());
            this.livingEntity.setData(DataAttachmentRegistry.ICE_BALL_COUNT, 0);
        }
    }

    public void shootIceBall() {
        Level level = this.livingEntity.level();
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        BouncingIceBallProjectile iceBall = new BouncingIceBallProjectile(EntityRegistry.BOUNCING_ICE_BALL.get(), level);

        iceBall.setOwner(this.livingEntity);
        iceBall.setPos(this.livingEntity.getX(), this.livingEntity.getEyeY() - 0.5, this.livingEntity.getZ());
        iceBall.shootFromRotation(this.livingEntity, this.livingEntity.getXRot(), this.livingEntity.getYRot(), 0.0F, 1.2F, 1.0F);
        level.playSound(null, this.livingEntity.blockPosition(), SoundRegistry.ICE_BALL_THROWN.get(),
                SoundSource.HOSTILE, 1.0F, pitch);

        Vec3 look = this.livingEntity.getLookAngle();
        iceBall.setDeltaMovement(look.scale(0.5));
        iceBall.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        iceBall.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        level.addFreshEntity(iceBall);
        this.livingEntity.swing(InteractionHand.MAIN_HAND);
    }
}
