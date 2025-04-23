package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;

public class RedKoopaShellEntity extends KoopaShellEntity implements CrackableEntity, GeoEntity, TraceableEntity {

    public RedKoopaShellEntity(EntityType<? extends RedKoopaShellEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void tick() {
        super.tick();
        this.targetEntity();
    }

    @NotNull
    @Override
    public SimpleParticleType getShatterParticle() {
        return ParticleRegistry.RED_KOOPA_SHELL_SHATTER.get();
    }

    @Override
    public void collideWithEntity() {
        AABB collisionBox = this.getBoundingBox().inflate(0.01, 0, 0.01);
        List<Entity> collidingEntities = this.level().getEntities(this, collisionBox);
        double speed = this.getDeltaMovement().horizontalDistance();
        Set<UUID> newCollisions = new HashSet<>();

        for (Entity entity : collidingEntities) {
            if (speed >= 0.1) {
                if (entity instanceof LivingEntity livingEntity
                        && !livingEntity.getType().is(TagRegistry.ICE_CUBE_COLLISION_CANNOT_DAMAGE)) { // TODO
                    ItemStack shield = livingEntity.getUseItem();
                    Vec3 toShell = this.position().subtract(livingEntity.position()).normalize();
                    Vec3 look = livingEntity.getLookAngle().normalize();
                    double dot = toShell.dot(look);

                    UUID id = livingEntity.getUUID();
                    newCollisions.add(id);
                    if (entityCollided.contains(id)) continue;

                    if (livingEntity.isBlocking() && dot > 0.25) {
                        this.deflect(entity, livingEntity, true);
                        shield.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
                        this.level().playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                        continue;
                    }

                    float shellDamage = livingEntity.getType().is(TagRegistry.RED_KOOPA_SHELL_CAN_INSTAKILL)
                            ? livingEntity.getHealth() * 1.25F : (float) Mth.clamp(speed * 10, 1.0F, 4.0F);

                    if (this.getOwner() != null)
                        livingEntity.hurt(DamageTypeRegistry.spinningShell(livingEntity, this.getOwner()), shellDamage);
                    else livingEntity.hurt(DamageTypeRegistry.spinningShell(livingEntity, this), shellDamage);
                    if (this.level() instanceof ServerLevel serverWorld)
                        serverWorld.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + this.getBbHeight() / 2, entity.getZ(),
                                3, 0.1, 0.1, 0.1, 0.0);
                    this.kill();
                }
            }
        }

        entityCollided.retainAll(newCollisions);
        entityCollided.addAll(newCollisions);
    }

    public void targetEntity() {
        LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;
        double speed = this.getDeltaMovement().horizontalDistance();

        // TODO: Fix shulkers not targeted
        // TODO: Target vehicle entites, and hurt rider
        // TODO: Mobs run from shells
        // TODO: Fix jumping on red koopa & shoes

        List<Player> players = this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10, 3, 10));
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
            List<Monster> monsters = this.level().getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(10));
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

        if (target != null && speed >= 0.1) {
            Vec3 toTarget = target.position().subtract(this.position()).normalize();
            Vec3 currentVelocity = this.getDeltaMovement();
            double length = currentVelocity.length();
            Vec3 newDirection = currentVelocity.normalize().scale(0.9).add(toTarget.scale(0.1)).normalize();
            Vec3 newVelocity = newDirection.scale(length);

            this.setDeltaMovement(newVelocity.x, this.getDeltaMovement().y, newVelocity.z);
            this.slidingDirection = new Vec3(newVelocity.x, this.getDeltaMovement().y, newVelocity.z);
            this.setYRot((float) (Mth.atan2(-newVelocity.x, newVelocity.z) * (180F / Math.PI)));
            this.yRotO = this.getYRot();
        }
    }

}