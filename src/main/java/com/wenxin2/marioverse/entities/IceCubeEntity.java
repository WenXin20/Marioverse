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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
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
                this.unfreeze(false);
        }

        if (this.frozenEntityData != null) {
            float height = this.frozenEntityData.getFloat("Height") * 1.55F;
            float width = this.frozenEntityData.getFloat("Width") * 1.55F;
            this.setSize(width, height);
        }


        if (this.onGround() && this.fallDistance > 1) {
            this.getPersistentData().putInt("marioverse:entity_frozen_cooldown", 0);
            this.unfreeze(true);
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
        this.reapplyPosition();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FROZEN_DATA, new CompoundTag());
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Nullable
    @Override
    public ItemStack getPickedResult(@NotNull HitResult target) {
        if (displayEntity != null) {
            Entity entityInstance = this.displayEntity.getType().create(level());
            Item spawnEggItem = SpawnEggItem.byId(this.displayEntity.getType());
            if (spawnEggItem != null) {
                return new ItemStack(spawnEggItem);
            } else if (entityInstance != null) {
                ItemStack pickedResult = entityInstance.getPickedResult(target);
                if (pickedResult != null && !pickedResult.isEmpty())
                    return pickedResult;
            }
        }
        return super.getPickedResult(target);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.04;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @NotNull
    @Override
    protected AABB makeBoundingBox() {
        if (this.frozenEntityData != null) {
            float height = this.frozenEntityData.getFloat("Height") * 1.55F;
            float width = this.frozenEntityData.getFloat("Width") * 1.55F;
            return new AABB(this.position().subtract(width / 2, 0, width / 2), this.position().add(width / 2, height, width / 2));
        } else {
            return super.makeBoundingBox();
        }
    }

    public void setFrozenEntity(Entity entity) {
        if (entity != null) {
            frozenEntityData = new CompoundTag();
            entity.save(frozenEntityData);
            frozenEntityData.putString("id", EntityType.getKey(entity.getType()).toString());

            this.setSize(entity.getBbWidth() * 1.55F, entity.getBbHeight() * 1.55F);
//            frozenEntityData.putFloat("BodyRotation", entity.getYRot());
//            frozenEntityData.putFloat("HeadRotation", entity.getYHeadRot());
//            frozenEntityData.putFloat("Pitch", entity.getXRot());
            frozenEntityData.putFloat("Height", entity.getBbHeight());
            frozenEntityData.putFloat("Width", entity.getBbWidth());

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
            this.reapplyPosition();
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
//                if (tag.contains("BodyRotation"))
//                    this.displayEntity.setYBodyRot(tag.getFloat("BodyRotation"));
//                if (tag.contains("HeadRotation"))
//                    this.displayEntity.setYHeadRot(tag.getFloat("HeadRotation"));
//                if (tag.contains("Pitch"))
//                    this.displayEntity.setXRot(tag.getFloat("Pitch"));

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

    public void unfreeze(boolean applyDamage) {
        if (frozenEntityData != null && this.level() instanceof ServerLevel serverWorld) {
            Entity entity = EntityType.loadEntityRecursive(frozenEntityData, serverWorld, (e) -> {
                e.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                return e;
            });

            if (entity != null) {
                if (entity instanceof LivingEntity livingEntity) {
                    if (applyDamage) {
                        float damageAmount = Math.max(0, this.fallDistance - 3);
                        livingEntity.hurt(this.level().damageSources().fall(), damageAmount);
                    }
                }
                serverWorld.addFreshEntity(entity);
            }
        }
        this.discard();
    }
}
