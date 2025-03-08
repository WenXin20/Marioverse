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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
    private SpawnData frozenData;

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

//        int entityFrozenCooldown = this.getPersistentData().getInt("marioverse:entity_frozen_cooldown");
//        if (entityFrozenCooldown > 0)
//            this.getPersistentData().putInt("marioverse:entity_frozen_cooldown", entityFrozenCooldown - 1);
//        if (entityFrozenCooldown == 0)
//            this.unfreeze();
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if (frozenEntityData != null) {
            tag.put("FrozenEntityData", frozenEntityData);
        }

        tag.putFloat("FrozenEntityWidth", entityWidth);
        tag.putFloat("FrozenEntityHeight", entityHeight);

        if (this.frozenData != null) {
            CompoundTag spawnDataTag = new CompoundTag();
            spawnDataTag.put("Entity", this.frozenData.getEntityToSpawn().copy());
            tag.put("FrozenSpawnData", spawnDataTag);
        }
        tag.put("FrozenData", this.entityData.get(FROZEN_DATA).copy());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("FrozenEntityData", Tag.TAG_COMPOUND)) {
            frozenEntityData = tag.getCompound("FrozenEntityData");
        }

        if (tag.contains("FrozenEntityWidth") && tag.contains("FrozenEntityHeight")) {
            this.entityWidth = tag.getFloat("FrozenEntityWidth");
            this.entityHeight = tag.getFloat("FrozenEntityHeight");
        }

        if (tag.contains("FrozenSpawnData", Tag.TAG_COMPOUND)) {
            CompoundTag spawnDataTag = tag.getCompound("FrozenSpawnData");
            CompoundTag entityTag = spawnDataTag.getCompound("Entity");
            SpawnData spawnData = new SpawnData();

            spawnData.getEntityToSpawn().merge(entityTag);
            this.frozenData = spawnData;
        }
        if (tag.contains("FrozenData", Tag.TAG_COMPOUND)) {
            this.entityData.set(FROZEN_DATA, tag.getCompound("FrozenData"));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FROZEN_DATA, new CompoundTag());
    }

    public void setFrozenEntity(Entity entity) {
        if (entity != null) {
            SpawnData spawnData = new SpawnData();
            frozenEntityData = new CompoundTag();
            entity.save(frozenEntityData);
            frozenEntityData.putString("id", EntityType.getKey(entity.getType()).toString());

            this.setSize(entity.getBbWidth(), entity.getBbHeight());
            frozenEntityData.putFloat("FrozenYaw", entity.getYRot());
            frozenEntityData.putFloat("FrozenPitch", entity.getXRot());
            entity.setDeltaMovement(Vec3.ZERO);
            spawnData.getEntityToSpawn().merge(frozenEntityData);

            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
                mob.setNoGravity(true);
            }

            if (!(entity instanceof Player))
                entity.discard();

            if (!this.getPersistentData().contains("marioverse:entity_frozen_cooldown"))
                this.getPersistentData().putInt("marioverse:entity_frozen_cooldown", 100);

            this.frozenData = spawnData;
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
                if (tag.contains("FrozenYaw"))
                    this.displayEntity.setYRot(tag.getFloat("FrozenYaw"));
                if (tag.contains("FrozenPitch"))
                    this.displayEntity.setXRot(tag.getFloat("FrozenPitch"));

                if (this.displayEntity instanceof LivingEntity livingEntity) {
                    livingEntity.hurtTime = 0;
                    livingEntity.hurtDuration = 0;
                    livingEntity.attackAnim = 0;
                    livingEntity.tickCount = 0;
                }
            }
        }
        return this.displayEntity;
    }

    private void setSize(float width, float height) {
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

            if (entity != null) {
                serverWorld.addFreshEntity(entity);
            }
        }
        this.discard();
    }
}
