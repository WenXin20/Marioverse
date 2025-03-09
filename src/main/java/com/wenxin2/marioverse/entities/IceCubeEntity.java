package com.wenxin2.marioverse.entities;

import java.util.function.Function;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SpawnData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceCubeEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<CompoundTag> FROZEN_DATA =
            SynchedEntityData.defineId(IceCubeEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private CompoundTag frozenEntityData;
    private Entity displayEntity;
    private float entityWidth = 1.0F;
    private float entityHeight = 1.0F;

    public IceCubeEntity(EntityType<? extends IceCubeEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getPersistentData().contains("marioverse:entity_frozen_cooldown")) {
            int entityFrozenCooldown = this.getPersistentData().getInt("marioverse:entity_frozen_cooldown");
            if (entityFrozenCooldown > 0)
                this.getPersistentData().putInt("marioverse:entity_frozen_cooldown", entityFrozenCooldown - 1);
            if (entityFrozenCooldown == 0)
                this.unfreeze();
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putFloat("FrozenEntityWidth", entityWidth);
        tag.putFloat("FrozenEntityHeight", entityHeight);
        tag.put("FrozenData", this.entityData.get(FROZEN_DATA).copy());
        if (frozenEntityData != null)
            tag.put("FrozenEntityData", frozenEntityData);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("FrozenEntityWidth") && tag.contains("FrozenEntityHeight")) {
            this.entityWidth = tag.getFloat("FrozenEntityWidth");
            this.entityHeight = tag.getFloat("FrozenEntityHeight");
        }
        if (tag.contains("FrozenData", Tag.TAG_COMPOUND))
            this.entityData.set(FROZEN_DATA, tag.getCompound("FrozenData"));
        if (tag.contains("FrozenEntityData", Tag.TAG_COMPOUND))
            frozenEntityData = tag.getCompound("FrozenEntityData");
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FROZEN_DATA, new CompoundTag());
    }

    public void setFrozenEntity(Entity entity) {
        if (entity != null) {
//            SpawnData spawnData = new SpawnData();
            frozenEntityData = new CompoundTag();
            entity.save(frozenEntityData);
            frozenEntityData.putString("id", EntityType.getKey(entity.getType()).toString());

            this.setSize(entity.getBbWidth() * 1.45F, entity.getBbHeight() * 1.35F);
            frozenEntityData.putFloat("BodyRotation", entity.getYRot());
            frozenEntityData.putFloat("HeadRotation", entity.getYHeadRot());
            frozenEntityData.putFloat("Pitch", entity.getXRot());
            frozenEntityData.putFloat("Height", entity.getBbHeight());
            frozenEntityData.putFloat("Width", entity.getBbWidth());
//            entity.setDeltaMovement(Vec3.ZERO);
//            spawnData.getEntityToSpawn().merge(frozenEntityData);

            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
                mob.setNoGravity(true);
                mob.yBodyRot = 0;
            }

            if (!(entity instanceof Player))
                entity.discard();

            if (!this.getPersistentData().contains("marioverse:entity_frozen_cooldown"))
                this.getPersistentData().putInt("marioverse:entity_frozen_cooldown", 500);
            this.entityData.set(FROZEN_DATA, frozenEntityData);
        }
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(Level world) {
        CompoundTag tag = this.entityData.get(FROZEN_DATA);
        if (tag.isEmpty())
            return null;
        if (this.displayEntity == null) {
            if (!tag.contains("id", 8)) {
                System.err.println("Error: Frozen data does not contain 'id'.");
                return null;
            }
            this.displayEntity = EntityType.loadEntityRecursive(tag, world, Function.identity());
            if (this.displayEntity != null) {
                if (tag.contains("BodyRotation"))
                    this.displayEntity.setYBodyRot(tag.getFloat("BodyRotation"));
                if (tag.contains("HeadRotation"))
                    this.displayEntity.setYHeadRot(tag.getFloat("HeadRotation"));
                if (tag.contains("Pitch"))
                    this.displayEntity.setXRot(tag.getFloat("Pitch"));

                if (this.displayEntity instanceof LivingEntity livingEntity) {
                    livingEntity.hurtTime = 0;
                    livingEntity.hurtDuration = 0;
                }
            }
        }
        return this.displayEntity;
    }

    public void setSize(float width, float height) {
        this.setBoundingBox(new AABB(this.getX() - width / 2, this.getY(), this.getZ() - width / 2,
                this.getX() + width / 2, this.getY() + height, this.getZ() + width / 2));
    }

    public CompoundTag getFrozenEntityData() {
        return frozenEntityData;
    }

    public void unfreeze() {
        if (frozenEntityData != null && level() instanceof ServerLevel serverWorld) {
            Entity entity = EntityType.loadEntityRecursive(frozenEntityData, serverWorld, (e) -> {
                e.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                return e;
            });

            if (entity != null)
                serverWorld.addFreshEntity(entity);
        }
        this.discard();
    }
}
