package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.integration.automobility_compat.AutomobilityEntityProvider;
import com.wenxin2.marioverse.registries.SoundRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

public interface HoldableEntity {
    int THROW_DELAY_TICKS = 10;

    int getHolderId();

    int getHeldSlot();

    int getHoldTicks();

    void startHolding(LivingEntity holder);

    void releaseHold();

    void throwFrom(LivingEntity thrower);

    boolean placeAt(LivingEntity placer, BlockPos pos);

    boolean canEnterClearPipes();

    default boolean isHeld() {
        return this.getHolderId() != -1;
    }

    @Nullable
    default LivingEntity getHolder() {
        int holderId = this.getHolderId();
        return holderId != -1 && ((Entity) this).level().getEntity(holderId) instanceof LivingEntity holder ? holder : null;
    }

    default Vec3 getHoldPosition(Entity holder, float partialTick) {
        Entity self = (Entity) this;
        float xRot = holder.getViewXRot(partialTick);
        float yRot = HoldableEntity.getHolderYaw(holder, partialTick);
        Vec3 look = Vec3.directionFromRotation(xRot, yRot);
        Vec3 up = Vec3.directionFromRotation(xRot - 90.0F, yRot);
        double distance = holder.getBbWidth() * 0.5 + self.getBbWidth() * 0.5 + 0.35;

        return holder.getEyePosition(partialTick).add(look.scale(distance)).add(up.scale(-0.45))
                .subtract(0.0, self.getBbHeight() * 0.5, 0.0);
    }

    default boolean shouldReleaseHold(LivingEntity holder) {
        return !holder.isAlive() || holder.isSpectator()
                || (holder instanceof Player player && player.getInventory().selected != this.getHeldSlot());
    }

    @Nullable
    static HoldableEntity getHeldEntity(Entity holder) {
        List<Entity> held = holder.level().getEntities(holder, holder.getBoundingBox().inflate(4.0),
                entity -> entity instanceof HoldableEntity holdable && holdable.getHolderId() == holder.getId());
        return held.isEmpty() ? null : (HoldableEntity) held.getFirst();
    }

    static float getHolderYaw(Entity holder, float partialTick) {
        if (holder instanceof LivingEntity livingHolder && !(holder instanceof Player))
            return Mth.rotLerp(partialTick, livingHolder.yBodyRotO, livingHolder.yBodyRot);
        return holder.getViewYRot(partialTick);
    }

    static Vec3 launch(Entity thrown, LivingEntity thrower) {
        Level level = thrower.level();
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        Entity rootVehicle = thrower.getRootVehicle();
        boolean isAutomobile = rootVehicle.getType() == CompatRegistry.AUTOMOBILE.get()
                || rootVehicle.getType() == CompatRegistry.AUTOMOBILITY_HITBOX.get();
        double minSpeed = 1.25;
        double spawnDistance = 1.0;
        Vec3 look = thrower.getLookAngle();
        double horizontalSpeed = Math.sqrt(rootVehicle.getDeltaMovement().horizontalDistanceSqr());

        if (ModList.get().isLoaded("automobility") && isAutomobile)
            horizontalSpeed = AutomobilityEntityProvider.getHSpeed(rootVehicle);
        double speed = Math.max(minSpeed, horizontalSpeed * 3);

        Vec3 spawnPos = thrower.position()
                .add(look.x * spawnDistance, thrower.getEyeHeight() - 0.6 + look.y * spawnDistance, look.z * spawnDistance);

        thrown.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        if (look.y >= 0.9) {
            thrown.setDeltaMovement(look.x, minSpeed, look.z);
            level.playSound(null, thrower.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN_UP.get(),
                    SoundSource.PLAYERS, 1.0F, pitch);
        } else {
            thrown.setDeltaMovement(look.x * speed, look.y * Math.max(minSpeed, speed), look.z * speed);
            level.playSound(null, thrower.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN.get(),
                    SoundSource.PLAYERS, 1.0F, pitch);
        }

        thrown.hasImpulse = true;
        Vec3 thrownMotion = thrown.getDeltaMovement();
        return thrownMotion.horizontalDistanceSqr() > 0.0025 ? new Vec3(thrownMotion.x, 0.0, thrownMotion.z) : Vec3.ZERO;
    }

    static Vec3 travelInClearPipe(Entity entity, Vec3 slidingMovement, double slideSpeed) {
        Vec3 motion = entity.getDeltaMovement();

        entity.move(MoverType.SELF, motion);

        if (motion.horizontalDistanceSqr() > 0.01)
            return new Vec3(motion.x, 0.0, motion.z).normalize().scale(slideSpeed);
        return slidingMovement;
    }

    @Nullable
    static Vec3 getPlacePosition(Entity placed, LivingEntity placer, BlockPos pos) {
        Vec3 placePos = Vec3.atBottomCenterOf(pos);
        AABB placeBox = placed.getDimensions(placed.getPose()).makeBoundingBox(placePos);

        if (!placed.level().noCollision(placed, placeBox) || placeBox.intersects(placer.getBoundingBox()))
            return null;
        return placePos;
    }
}
