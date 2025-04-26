package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

public class RedKoopaShellEntity extends KoopaShellEntity implements CrackableEntity, GeoEntity, TraceableEntity {
    public RedKoopaShellEntity(EntityType<? extends RedKoopaShellEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers(RedKoopaTroopaEntity.class));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isAlive())
            this.targetEntity();

        int ticksToDie = this.getPersistentData().getInt("marioverse:ticks_to_die");

        if (ticksToDie > 1)
            this.getPersistentData().putInt("marioverse:ticks_to_die", ticksToDie - 1);
        else if (ticksToDie == 1) this.kill();
    }

    @NotNull
    @Override
    public SimpleParticleType getShatterParticle() {
        return ParticleRegistry.RED_KOOPA_SHELL_SHATTER.get();
    }

    @NotNull
    @Override
    public KoopaTroopaEntity getKoopaTroopaEntity() {
        return new RedKoopaTroopaEntity(EntityRegistry.RED_KOOPA_TROOPA.get(), this.level());
    }

    @Override
    public TagKey<EntityType<?>> getInstakillEntityTag() {
        return TagRegistry.RED_KOOPA_SHELL_CAN_INSTAKILL;
    }

    @Override
    public float getShellDamage() {
        return ConfigRegistry.RED_KOOPA_SHELL_DAMAGE.get().floatValue();
    }

    @NotNull
    public Integer getMobDetectionRadius() {
        return ConfigRegistry.RED_KOOPA_SHELL_MOB_DETECTION_RADIUS.get();
    }

    @NotNull
    public Integer getPlayerDetectionRadius() {
        return ConfigRegistry.RED_KOOPA_SHELL_PLAYER_DETECTION_RADIUS.get();
    }

    public void targetEntity() {
        Entity target = null;
        double closestDistance = Double.MAX_VALUE;
        double speed = this.getDeltaMovement().horizontalDistance();

        List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox()
                .inflate(this.getPlayerDetectionRadius(), 3, this.getPlayerDetectionRadius()));
        for (Player player : players) {
            if (!player.isSpectator() && player.isAlive()
                    && !player.getType().is(TagRegistry.RED_KOOPA_SHELL_CANNOT_ATTACK)
                    && (this.getOwner() == null
                        || !this.getOwner().getUUID().equals(player.getUUID()))) {
                double dist = this.distanceToSqr(player);
                if (dist < closestDistance) {
                    closestDistance = dist;
                    target = player;
                }
            }
        }

        if (target == null) {
            List<PartEntity> entities = this.level().getEntitiesOfClass(PartEntity.class, this.getBoundingBox()
                    .inflate(this.getMobDetectionRadius(), 3, this.getMobDetectionRadius()));
            for (PartEntity<?> entity : entities) {
                if (entity.isAlive() && !entity.is(this)
                        && !entity.getType().is(TagRegistry.RED_KOOPA_SHELL_CANNOT_ATTACK)) {
                    double dist = this.distanceToSqr(entity);
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        target = entity;
                    }
                }
            }
        }

        if (target == null) {
            List<Monster> monsters = this.level().getEntitiesOfClass(Monster.class, this.getBoundingBox()
                    .inflate(this.getMobDetectionRadius(), 3, this.getMobDetectionRadius()));
            for (Monster monster : monsters) {
                if (monster.isAlive() && !monster.is(this)
                        && !monster.getType().is(TagRegistry.RED_KOOPA_SHELL_CANNOT_ATTACK)) {
                    double dist = this.distanceToSqr(monster);
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        target = monster;
                    }
                }
            }
        }

        if (target == null) {
            List<Shulker> shulkers = this.level().getEntitiesOfClass(Shulker.class, this.getBoundingBox()
                    .inflate(this.getMobDetectionRadius(), 3, this.getMobDetectionRadius()));
            for (Shulker shulker : shulkers) {
                if (shulker.isAlive() && !shulker.isClosed() && !shulker.is(this)
                        && !shulker.getType().is(TagRegistry.RED_KOOPA_SHELL_CANNOT_ATTACK)) {
                    double dist = this.distanceToSqr(shulker);
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        target = shulker;
                    }
                }
            }
        }

        if (target != null && speed >= 0.1) {
            Vec3 toTarget = target.position().subtract(this.position()).normalize();
            Vec3 currentVelocity = this.getDeltaMovement();
            double length = currentVelocity.length();
            Vec3 newDirection = currentVelocity.normalize().scale(0.9).add(toTarget.scale(0.1)).normalize();
            Vec3 newVelocity = newDirection.scale(length);

            this.setDeltaMovement(newVelocity.x, this.getDeltaMovement().y, newVelocity.z);
            this.slidingDirection = new Vec3(newVelocity.x, this.getDeltaMovement().y, newVelocity.z);
            this.setYRot((float) (Mth.atan2(-newVelocity.x, newVelocity.z) * (180F / Math.PI)));
        }
    }
}