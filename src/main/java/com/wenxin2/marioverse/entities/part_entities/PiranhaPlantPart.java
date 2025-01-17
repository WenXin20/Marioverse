package com.wenxin2.marioverse.entities.part_entities;

import com.wenxin2.marioverse.entities.PiranhaPlantEntity;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.entity.PartEntity;
import org.jetbrains.annotations.NotNull;

public class PiranhaPlantPart extends PartEntity<PiranhaPlantEntity> {
    public final PiranhaPlantEntity parentMob;
    public final String name;
    private final EntityDimensions size;

    public PiranhaPlantPart(PiranhaPlantEntity parentMob, String name, float width, float height) {
        super(parentMob);
        this.size = EntityDimensions.scalable(width, height);
        this.refreshDimensions();
        this.parentMob = parentMob;
        this.name = name;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {}

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {}

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return this.parentMob.getPickResult();
    }

    @Override
    public void baseTick() {
        super.baseTick();
        this.checkForCollisions();
    }

    @Override
    public boolean hurt(DamageSource source, float damageAmount) {
        return !this.isInvulnerableTo(source) && this.parentMob.hurt(source, damageAmount);
    }

    @Override
    public boolean is(Entity entity) {
        return this == entity || this.parentMob == entity;
    }

    @NotNull
    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
        return new ClientboundAddEntityPacket(this, entity);
    }

    @NotNull
    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size;
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        this.setBoundingBox(this.size.makeBoundingBox(this.position()));
    }

    @Override
    public boolean canBeHitByProjectile() {
        return this.isAlive() && this.isPickable();
    }

    @Override
    public void push(final Entity entity) {
        if (!parentMob.isSleeping()) {
            super.push(entity);
        }
    }

    @Override
    public boolean canCollideWith(final Entity entity) {
        return !this.isPassengerOfSameVehicle(entity);
    }

    public void checkForCollisions() {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.15D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && parentMob.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || !(collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    return;

                parentMob.swing(InteractionHand.MAIN_HAND);
                parentMob.doHurtTarget(collidingEntity);
                break;
            }
        }
    }
}
