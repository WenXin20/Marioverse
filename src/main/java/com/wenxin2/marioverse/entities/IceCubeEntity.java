package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.init.TagRegistry;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceCubeEntity extends VehicleEntity implements GeoEntity {
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FROZEN_DATA, new CompoundTag());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
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
    public void tick() {
        super.tick();
        Level world = this.level();

        if (this.getPersistentData().contains("marioverse:entity_frozen_cooldown")) {
            int entityFrozenCooldown = this.getPersistentData().getInt("marioverse:entity_frozen_cooldown");
            if (entityFrozenCooldown > 0)
                this.getPersistentData().putInt("marioverse:entity_frozen_cooldown", entityFrozenCooldown - 1);
            if (entityFrozenCooldown == 0)
                this.unfreeze(false, false);
        }

        if (this.frozenEntityData != null) {
            float height = this.frozenEntityData.getFloat("Height") * 1.55F;
            float width = this.frozenEntityData.getFloat("Width") * 1.55F;
            this.setSize(width, height);
        }

        if (this.onGround() && this.getDeltaMovement().y > 0) {
            this.getPersistentData().putInt("marioverse:entity_frozen_cooldown", 0);
            this.unfreeze(true, false);
        }

        if (!this.onGround())
            this.setDeltaMovement(0, -this.getDefaultGravity(), 0);

        this.move(MoverType.SELF, this.getDeltaMovement());

        if (!this.level().isClientSide) {
            BlockPos pos = this.blockPosition();
            BlockState state = world.getBlockState(pos);

            for (Direction direction : Direction.values()) {
                if (direction.getAxis().isHorizontal() && this.getDeltaMovement().horizontalDistance() > 0) {
                    BlockPos hitPos = pos.relative(direction);
                    BlockState hitState = world.getBlockState(hitPos);

                    if (!hitState.canBeReplaced()) {
                        this.setDeltaMovement(Vec3.ZERO);
                        this.unfreeze(false, true);
                        return;
                    }
                }
            }
        }
    }

    @NotNull
    @Override
    protected Item getDropItem() {
        return ItemStack.EMPTY.getItem();
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
    public boolean hurt(DamageSource source, float damage) {
        Level world = this.level();
        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = world.getBlockState(posBelow);

        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else if (source.getDirectEntity() instanceof BouncingIceBallProjectile && this.getType().is(TagRegistry.ICE_BALL_IMMUNE)) {
            return false;
        } else {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.markHurt();
            this.setDamage(this.getDamage() + damage * 10.0F);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());

            float friction = stateBelow.getFriction(world, posBelow, this);
            double slideSpeed;

            if (friction > 0.6)
                slideSpeed = 0.0 + friction;
            else slideSpeed = 0.5;

            Vec3 slideDirection = Vec3.ZERO;

            if (source.getEntity() != null) {
                Vec3 attackerPos = source.getEntity().position();
                Vec3 hitPos = this.position();
                Vec3 slideDirRaw = hitPos.subtract(attackerPos).normalize();
                slideDirection = new Vec3(slideDirRaw.x, 0, slideDirRaw.z).normalize();
            } else if (source.getDirectEntity() != null) {
                slideDirection = source.getDirectEntity().getDeltaMovement().normalize();
            }

            this.setDeltaMovement(slideDirection.scale(slideSpeed));

            return true;
        }
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
        return 0.5;
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
            frozenEntityData.putFloat("BodyRotation", entity.getYRot());
            frozenEntityData.putFloat("HeadRotation", entity.getYHeadRot());
            frozenEntityData.putFloat("Pitch", entity.getXRot());
            frozenEntityData.putFloat("Height", entity.getBbHeight());
            frozenEntityData.putFloat("Width", entity.getBbWidth());

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
            if (!tag.contains("id", 8))
                return null;

            this.displayEntity = EntityType.loadEntityRecursive(tag, world, Function.identity());
            if (this.displayEntity != null) {
                if (tag.contains("BodyRotation"))
                    this.displayEntity.setYBodyRot(tag.getFloat("BodyRotation"));
                if (tag.contains("BodyRotation"))
                    this.displayEntity.setYRot(tag.getFloat("BodyRotation"));
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

    public void unfreeze(boolean applyFallDamage, boolean applyCollisionDamage) {
        if (frozenEntityData != null && this.level() instanceof ServerLevel serverWorld) {
            Entity entity = EntityType.loadEntityRecursive(frozenEntityData, serverWorld, (e) -> {
                e.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                return e;
            });

            if (entity != null) {
                if (entity instanceof LivingEntity livingEntity) {
                    if (applyFallDamage) {
                        float damageAmount = Math.max(0, this.fallDistance - 3);
                        livingEntity.hurt(this.level().damageSources().fall(), damageAmount);
                    }
                    if (applyCollisionDamage) {
                        livingEntity.hurt(this.level().damageSources().flyIntoWall(), 4.0F); // TODO
                    }
                }
                serverWorld.addFreshEntity(entity);
            }
        }
        this.level().playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK,
                SoundSource.AMBIENT, 1.0F, 1.0F);
        this.discard();
    }
}
