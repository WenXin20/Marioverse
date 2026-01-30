package com.wenxin2.marioverse.entities.ai.goals;

import com.wenxin2.marioverse.entities.projectiles.BouncingFireballProjectile;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShootBouncingFireballGoal extends Goal {
    private final LivingEntity livingEntity;
    private final int maxFireballs;
    private final int addFireballsWithFireFlower;
    private final boolean requireFireFlower;
    private static final int FIREBALL_COOLDOWN = 5;

    public ShootBouncingFireballGoal(LivingEntity entity, int maxFireballs, int addFireballsWithFireFlower, boolean requireFireFlower) {
        this.livingEntity = entity;
        this.maxFireballs = maxFireballs;
        this.addFireballsWithFireFlower = addFireballsWithFireFlower;
        this.requireFireFlower = requireFireFlower;
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
        boolean canShoot = !requireFireFlower || (livingEntity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER));
        return livingEntity.getDeltaMovement().horizontalDistance() > 0.0F && canShoot;
    }

    @Override
    public void tick() {
        if (canUse()) {
            if ((livingEntity instanceof Monster monster && monster.getTarget() != null && monster.getSensing().hasLineOfSight(monster.getTarget()))
                    || (livingEntity instanceof AbstractGolem golem && golem.getTarget() != null && golem.getSensing().hasLineOfSight(golem.getTarget()))
                    || !(livingEntity instanceof Monster) && !(livingEntity instanceof AbstractGolem))
                handleFireballShooting();
        }

        if (livingEntity instanceof AbilitiesHandler handler && handler.mv$getFireballCooldown() > 0)
            handler.mv$setFireballCooldown(handler.mv$getFireballCooldown() - 1);

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

    public void handleFireballShooting() {
        if (livingEntity instanceof AbilitiesHandler handler) {
            if (!requireFireFlower && handler.mv$getFireballCooldown() == 0
                    && handler.mv$getFireballCount() < maxFireballs + addFireballsWithFireFlower) {
                this.shootFireball();
                handler.mv$setFireballCooldown(FIREBALL_COOLDOWN);
                handler.mv$setFireballCount(handler.mv$getFireballCount() + 1);
            } else if (handler.mv$getFireballCooldown() == 0
                    && handler.mv$getFireballCount() < maxFireballs + addFireballsWithFireFlower
                    && livingEntity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)) {
                this.shootFireball();
                handler.mv$setFireballCooldown(FIREBALL_COOLDOWN);
                handler.mv$setFireballCount(handler.mv$getFireballCount() + 1);
            } else if (!requireFireFlower && handler.mv$getFireballCount() >= maxFireballs + addFireballsWithFireFlower) {
                handler.mv$setFireballCooldown(ConfigRegistry.FIREBALL_COOLDOWN.get());
                handler.mv$setFireballCount(0);
            } else if (handler.mv$getFireballCount() >= maxFireballs) {
                handler.mv$setFireballCooldown(ConfigRegistry.FIREBALL_COOLDOWN.get());
                handler.mv$setFireballCount(0);
            }
        }
    }

    public void shootFireball() {
        Level world = livingEntity.level();
        BouncingFireballProjectile fireball = new BouncingFireballProjectile(EntityRegistry.BOUNCING_FIREBALL.get(), world);
        fireball.setOwner(livingEntity);
        fireball.setPos(livingEntity.getX(), livingEntity.getEyeY() - 0.5, livingEntity.getZ());
        fireball.shootFromRotation(livingEntity, livingEntity.getXRot(), livingEntity.getYRot(), 0.0F, 1.2F, 1.0F);
        world.playSound(null, livingEntity.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(), SoundSource.HOSTILE, 1.0F, 1.0F);

        Vec3 look = livingEntity.getLookAngle();
        fireball.setDeltaMovement(look.scale(0.5));
        fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        world.addFreshEntity(fireball);
        livingEntity.swing(InteractionHand.MAIN_HAND);
    }
}
