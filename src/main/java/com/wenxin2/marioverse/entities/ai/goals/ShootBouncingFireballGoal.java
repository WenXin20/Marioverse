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
        boolean canShoot = !this.requireFireFlower || (this.livingEntity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER));
        return this.livingEntity.getDeltaMovement().horizontalDistance() > 0.0F && canShoot;
    }

    @Override
    public void tick() {
        if (canUse()) {
            if ((this.livingEntity instanceof Monster monster && monster.getTarget() != null && monster.getSensing().hasLineOfSight(monster.getTarget()))
                    || (this.livingEntity instanceof AbstractGolem golem && golem.getTarget() != null && golem.getSensing().hasLineOfSight(golem.getTarget()))
                    || !(this.livingEntity instanceof Monster) && !(this.livingEntity instanceof AbstractGolem))
                this.handleFireballShooting();
        }

        if (this.livingEntity.getData(DataAttachmentRegistry.FIREBALL_COOLDOWN) > 0)
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COOLDOWN,
                    this.livingEntity.getData(DataAttachmentRegistry.FIREBALL_COOLDOWN) - 1);

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

    public void handleFireballShooting() {
        int fireballCooldown = this.livingEntity.getData(DataAttachmentRegistry.FIREBALL_COOLDOWN);
        int fireballCount = this.livingEntity.getData(DataAttachmentRegistry.FIREBALL_COUNT);

        if (!this.requireFireFlower && fireballCooldown == 0
                && fireballCount < this.maxFireballs + this.addFireballsWithFireFlower) {
            this.shootFireball();
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COOLDOWN, FIREBALL_COOLDOWN);
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COUNT, fireballCount + 1);
        } else if (fireballCooldown == 0
                && fireballCount < this.maxFireballs + this.addFireballsWithFireFlower
                && this.livingEntity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)) {
            this.shootFireball();
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COOLDOWN, FIREBALL_COOLDOWN);
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COUNT, fireballCount + 1);
        } else if (!this.requireFireFlower && fireballCount >= this.maxFireballs + this.addFireballsWithFireFlower) {
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COOLDOWN, ConfigRegistry.FIREBALL_COOLDOWN.get());
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COUNT, 0);
        } else if (fireballCount >= this.maxFireballs) {
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COOLDOWN, ConfigRegistry.FIREBALL_COOLDOWN.get());
            this.livingEntity.setData(DataAttachmentRegistry.FIREBALL_COUNT, 0);
        }
    }

    public void shootFireball() {
        Level level = this.livingEntity.level();
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        BouncingFireballProjectile fireball = new BouncingFireballProjectile(EntityRegistry.BOUNCING_FIREBALL.get(), level);

        fireball.setOwner(this.livingEntity);
        fireball.setPos(this.livingEntity.getX(), this.livingEntity.getEyeY() - 0.5, this.livingEntity.getZ());
        fireball.shootFromRotation(this.livingEntity, this.livingEntity.getXRot(), this.livingEntity.getYRot(), 0.0F, 1.2F, 1.0F);
        level.playSound(null, this.livingEntity.blockPosition(), SoundRegistry.FIREBALL_THROWN.get(),
                SoundSource.HOSTILE, 1.0F, pitch);

        Vec3 look = this.livingEntity.getLookAngle();
        fireball.setDeltaMovement(look.scale(0.5));
        fireball.setYRot((float) Math.toDegrees(Math.atan2(look.z, look.x)) + 90);
        fireball.setXRot((float) Math.toDegrees(Math.atan2(look.y, Math.sqrt(look.x * look.x + look.z * look.z))));

        level.addFreshEntity(fireball);
        this.livingEntity.swing(InteractionHand.MAIN_HAND);
    }
}
