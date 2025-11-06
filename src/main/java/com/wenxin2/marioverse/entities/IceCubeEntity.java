package com.wenxin2.marioverse.entities;

import com.google.common.base.MoreObjects;
import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class IceCubeEntity extends Mob implements GeoEntity, TraceableEntity {
    private static final EntityDataAccessor<CompoundTag> FROZEN_DATA =
            SynchedEntityData.defineId(IceCubeEntity.class, EntityDataSerializers.COMPOUND_TAG);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public Vec3 slidingMovement = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    private CompoundTag frozenEntityData;
    private Entity displayEntity;
    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private boolean leftOwner;
    private float entityHeight = 1.0F;
    private float entityWidth = 1.0F;
    private float height = 1.0F;
    private float previousFallDistance = 0;
    private float width = 1.0F;
    public int entityFrozenCooldown;
    public int frozenCooldown;
    public int ticksInAir;

    public IceCubeEntity(EntityType<? extends IceCubeEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public void setSliding(boolean isSliding) {
        this.setData(DataAttachmentRegistry.IS_SLIDING.get(), isSliding);
    }

    public boolean isSliding() {
        return this.getData(DataAttachmentRegistry.IS_SLIDING.get());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FROZEN_DATA, new CompoundTag());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.put("FrozenData", this.entityData.get(FROZEN_DATA).copy());
        tag.putFloat("FrozenEntityHeight", this.entityHeight);
        tag.putFloat("FrozenEntityWidth", this.entityWidth);
        tag.putFloat("Height", this.height);
        tag.putFloat("Width", this.width);
        tag.putInt("FrozenCooldown", this.getFrozenCooldown());
        tag.putInt("EntityFrozenCooldown", this.getEntityFrozenCooldown());
        tag.putInt("TicksInAir", this.getTicksInAir());

        if (this.frozenEntityData != null)
            tag.put("FrozenEntityData", this.frozenEntityData);
        if (this.ownerUUID != null)
            tag.putUUID("OwnerUUID", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.leftOwner = tag.getBoolean("LeftOwner");
        this.frozenCooldown = tag.getInt("FrozenCooldown");
        this.setEntityFrozenCooldown(tag.getInt("EntityFrozenCooldown"));
        this.setTicksInAir(tag.getInt("TicksInAir"));

        if (tag.contains("FrozenEntityWidth") && tag.contains("FrozenEntityHeight")) {
            this.entityWidth = tag.getFloat("FrozenEntityWidth");
            this.entityHeight = tag.getFloat("FrozenEntityHeight");
        }

        if (tag.contains("Width") && tag.contains("Height")) {
            this.width = tag.getFloat("Width");
            this.height = tag.getFloat("Height");
        }

        if (tag.contains("FrozenData", Tag.TAG_COMPOUND))
            this.entityData.set(FROZEN_DATA, tag.getCompound("FrozenData"));
        if (tag.contains("FrozenEntityData", Tag.TAG_COMPOUND))
            this.frozenEntityData = tag.getCompound("FrozenEntityData");

        if (tag.hasUUID("OwnerUUID")) {
            this.ownerUUID = tag.getUUID("OwnerUUID");
            this.cachedOwner = null;
        }

        this.reapplyPosition();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, serverEntity, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Entity entity = this.level().getEntity(packet.getData());
        if (entity != null)
            this.setOwner(entity);
    }

    @Override
    public void tick() {
        super.tick();
        Level world = this.level();
        BlockPos pos = this.blockPosition();

        if (!this.leftOwner)
            this.leftOwner = this.checkLeftOwner();
        if (this.getPersistentData().contains("EntityFrozenCooldown")) {
            if (this.getEntityFrozenCooldown() > 0)
                this.setEntityFrozenCooldown(this.getEntityFrozenCooldown() - 1);
            if (this.getEntityFrozenCooldown() == 0)
                this.shatterIceCube(false, false, this);
        }

        if (this.getTicksInAir() > 0)
            this.setTicksInAir(this.getTicksInAir() - 1);

        if (this.getDeltaMovement().horizontalDistance() > 0.1)
            this.spawnSnowParticles();

        if (this.frozenEntityData != null) {
            float height = this.frozenEntityData.getFloat("Height") * 1.55F;
            float width = this.frozenEntityData.getFloat("Width") * 1.55F;
            this.setSize(width, height);
        }

        if (this.isSliding() && this.isAlive() && !this.isNoAi()) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = level().getBlockState(posBelow);
            float friction = stateBelow.getFriction(level(), posBelow, this);
            double slideFriction = /*(friction > 0.8) ? 0.4 + friction / 1.5 :*/ 1.0;
            Vec3 slideMotion = this.slidingMovement.scale(slideFriction);

            if ((this.onGround()) && this.getDeltaMovement().horizontalDistance() > 0.0001) {
                this.setDeltaMovement(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
                this.slidingMovement = new Vec3(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
                this.hasImpulse = true;
            }
        }

//        this.gravityWaterPhysics();
        this.collideWithWall(world, pos);
        this.collideWithEntity();
    }

//    @NotNull
//    @Override
//    protected Item getDropItem() {
//        return ItemStack.EMPTY.getItem();
//    }

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
        } else if (source.getEntity() instanceof LivingEntity entity && entity.getMainHandItem().is(ItemTags.PICKAXES)) {
            this.shatterIceCube(false, false, this);
            return true;
        } else {
//            this.setHurtDir(-this.getHurtDir());
//            this.setHurtTime(10);
//            this.markHurt();
//            this.setDamage(this.getDamage() + damage * 10.0F);
//            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
//            this.setOwner(source.getEntity());
//
//            float friction = stateBelow.getFriction(world, posBelow, this);
//            double slideSpeed;
//
//            if (friction > 0.6)
//                slideSpeed = 0.1 + friction / 1.5;
//            else slideSpeed = 0.5;
//
//            Vec3 slideDirection = Vec3.ZERO;
//
//            if (source.getEntity() != null) {
//                Vec3 attackerPos = source.getEntity().position();
//                Vec3 hitPos = this.position();
//                Vec3 slideDirRaw = hitPos.subtract(attackerPos).normalize();
//                slideDirection = new Vec3(slideDirRaw.x, 0, slideDirRaw.z).normalize();
//            } else if (source.getDirectEntity() != null) {
//                slideDirection = source.getDirectEntity().getDeltaMovement().normalize();
//            }
//
//            Vec3 movement = slideDirection.scale(slideSpeed);
//            if (this.displayEntity instanceof Mob mob && !mob.isNoAi()) {
//                this.setDeltaMovement(movement);
//            } else if (!(this.displayEntity instanceof Mob))
//                this.setDeltaMovement(movement);

            float friction = stateBelow.getFriction(world, posBelow, this);
            double slideSpeed;

            if (friction > 0.6)
                slideSpeed = 0.4 + friction / 1.5;
            else slideSpeed = 1.0;

            Vec3 slideDirection = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);

            if (source.getEntity() != null) {
                Vec3 attackerPos = source.getEntity().position();
                Vec3 hitPos = this.position();
                Vec3 slideDirRaw = hitPos.subtract(attackerPos).normalize();
                slideDirection = new Vec3(slideDirRaw.x, this.getDeltaMovement().y, slideDirRaw.z).normalize();
            } else if (source.getDirectEntity() != null)
                slideDirection = source.getDirectEntity().getDeltaMovement().normalize();

            Vec3 movement = slideDirection.scale(slideSpeed);

            if (!isNoAi() && this.displayEntity instanceof Mob mob || !(this.displayEntity instanceof Mob)) {
                this.setDeltaMovement(movement.x, this.getDeltaMovement().y, movement.z);
                this.slidingMovement = new Vec3(movement.x, this.getDeltaMovement().y, movement.z);
                this.hasImpulse = true;
                this.setOwner(source.getEntity());
                this.setSliding(true);
                this.leftOwner = false;
            }
            return true;
        }
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return super.canCollideWith(entity);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void push(Entity entity) {
        if (!(entity instanceof IceCubeEntity)) {
            if (!entity.noPhysics && !this.noPhysics) {
                double d0 = entity.getX() - this.getX();
                double d1 = entity.getZ() - this.getZ();
                double d2 = Mth.absMax(d0, d1);
                if (d2 >= 0.01F) {
                    d2 = Math.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0 / d2;
                    if (d3 > 1.0) {
                        d3 = 1.0;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05F;
                    d1 *= 0.05F;
                    if (!this.isVehicle() && this.isPushable()) {
                        this.push(-d0, 0.0, -d1);
                    }

                    if (!entity.isVehicle() && entity.isPushable()) {
                        entity.push(d0, 0.0, d1);
                    }
                }
            }
        }
    }

    @Override
    protected double getDefaultGravity() {
        return 0.06;
    }

    @Override
    public boolean isNoGravity() {
        return false;
    }

    @Override
    public boolean canBeRiddenUnderFluidType(FluidType type, Entity riderEntity) {
        return true;
    }

    @Override
    protected boolean canRide(Entity riderEntity) {
        return true;
    }

    @Override
    protected void positionRider(Entity riderEntity, MoveFunction moveFunction) {
        if (this.isAlive())
            moveFunction.accept(riderEntity, this.getX(), this.getY(), this.getZ());
        else super.positionRider(riderEntity, moveFunction);

        if (riderEntity instanceof Player player) {
            player.setYRot(this.getYRot());
            player.yRotO = this.getYRot();
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return false;
    }

    @Override
    public boolean canControlVehicle() {
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

    public void setOwner(@javax.annotation.Nullable Entity p_37263_) {
        if (p_37263_ != null) {
            this.ownerUUID = p_37263_.getUUID();
            this.cachedOwner = p_37263_;
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverlevel) {
            this.cachedOwner = serverlevel.getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof IceCubeEntity iceCube)
            this.cachedOwner = iceCube.cachedOwner;
    }

    protected boolean ownedBy(Entity entity) {
        return entity.getUUID().equals(this.ownerUUID);
    }

    public Entity getEffectSource() {
        return MoreObjects.firstNonNull(this.getOwner(), this);
    }

    private boolean checkLeftOwner() {
        Entity entity = this.getOwner();
        if (entity != null) {
            for (Entity entity1 : this.level().getEntities(this,
                    this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0),
                    mob -> !mob.isSpectator() && mob.isPickable())) {
                if (entity1.getRootVehicle() == entity.getRootVehicle())
                    return false;
            }
        }
        return true;
    }

    public boolean deflect(@Nullable Entity entity, @Nullable Entity ownerEntity, boolean deflect) {
        if (!this.level().isClientSide) {
            this.setOwner(ownerEntity);
            this.onDeflection(entity, deflect);
        }
        return true;
    }

    protected void onDeflection(@Nullable Entity entity, boolean deflect) {
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(new Vec3(-motion.x, motion.y, -motion.z));
    }

    public void setFrozenEntity(Entity entity, int ticksFrozen) {
        if (entity != null) {
            float scale = 1.0F;
            float heightScale = 1.0F;
            float widthScale = 1.0F;
            frozenEntityData = new CompoundTag();
            entity.save(frozenEntityData);

            if (entity instanceof LivingEntity living) {
                AttributeInstance scaleAttribute = living.getAttribute(Attributes.SCALE);
                AttributeInstance heightScaleAttribute = living.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                AttributeInstance widthScaleAttribute = living.getAttribute(AttributesRegistry.WIDTH_SCALE);
                if (scaleAttribute != null)
                    scale = (float) scaleAttribute.getValue();
                if (heightScaleAttribute != null)
                    heightScale = (float) heightScaleAttribute.getValue();
                if (widthScaleAttribute != null)
                    widthScale = (float) widthScaleAttribute.getValue();
            }

            frozenEntityData.putString("id", EntityType.getKey(entity.getType()).toString());
            frozenEntityData.putFloat("BodyRotation", entity.getYRot());
            frozenEntityData.putFloat("HeadRotation", entity.getYHeadRot());
            frozenEntityData.putFloat("Pitch", entity.getXRot());
            frozenEntityData.putFloat("Height", entity.getBbHeight());
            frozenEntityData.putFloat("Width", entity.getBbWidth());
            frozenEntityData.putFloat("Scale", scale);
            frozenEntityData.putFloat("HeightScale", heightScale);
            frozenEntityData.putFloat("WidthScale", widthScale);

            if (entity instanceof KoopaTroopaEntity koopa)
                koopa.hide(koopa.isHiding());

            this.setSize(entity.getBbWidth() * scale * widthScale * 1.55F, entity.getBbHeight() * scale * heightScale * 1.55F);
            if (!(entity instanceof Player))
                entity.discard();

            this.setEntityFrozenCooldown(ticksFrozen);
            this.entityData.set(FROZEN_DATA, frozenEntityData);
        }
    }

    @Nullable
    public Entity getPlayer(Level world) {
        CompoundTag tag = this.entityData.get(FROZEN_DATA);
        if (tag.isEmpty()) {
            for (Player player : world.players())
                return player;
        }
        return null;
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

                if (displayEntity instanceof LivingEntity entity) {
                    entity.hurtDuration = 0;
                    entity.hurtTime = 0;

                    if (tag.contains("Scale")) {
                        float scale = tag.getFloat("Scale");
                        entity.setBoundingBox(entity.getBoundingBox().inflate(scale - 1.0));
                        entity.getPersistentData().putFloat("Scale", scale);
                    }

                    if (tag.contains("HeightScale")) {
                        float scale = tag.getFloat("HeightScale");
                        entity.setBoundingBox(entity.getBoundingBox().inflate(scale - 1.0));
                        entity.getPersistentData().putFloat("HeightScale", scale);
                    }

                    if (tag.contains("WidthScale")) {
                        float scale = tag.getFloat("WidthScale");
                        entity.setBoundingBox(entity.getBoundingBox().inflate(scale - 1.0));
                        entity.getPersistentData().putFloat("WidthScale", scale);
                    }
                }
            }
        }
        return this.displayEntity;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (this.displayEntity == null) {
            this.displayEntity = this.getOrCreateDisplayEntity(this.level());
        }
    }

    public void setSize(float width, float height) {
        this.height = height;
        this.width = width;
        this.setBoundingBox(new AABB(this.getX() - width / 2, this.getY(), this.getZ() - width / 2,
                this.getX() + width / 2, this.getY() + height, this.getZ() + width / 2));
    }

    public Vec2 getSize() {
        return new Vec2(this.width, this.height);
    }

    public CompoundTag getFrozenEntityData() {
        return frozenEntityData;
    }

    public void shatterIceCube(boolean applyFallDamage, boolean applyCollisionDamage, Entity attackingEntity) {
        float scale = 1.0F;
        float heightScale = 1.0F;
        float widthScale = 1.0F;

        if (frozenEntityData != null && this.level() instanceof ServerLevel serverWorld) {
            Entity entity = EntityType.loadEntityRecursive(frozenEntityData, serverWorld, (e) -> {
                e.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                return e;
            });

            if (entity != null) {
                serverWorld.addFreshEntity(entity);
                if (entity instanceof LivingEntity livingEntity) {
                    if (applyFallDamage) {
                        float damageAmount = Math.max(0, this.fallDistance - livingEntity.getMaxFallDistance());
                        livingEntity.hurt(this.level().damageSources().fall(), damageAmount);
                        livingEntity.hurtDuration = 10;
                        livingEntity.hurtTime = 10;
                        livingEntity.hurtMarked = true;
                    }
                    if (applyCollisionDamage && !livingEntity.getType().is(TagRegistry.ICE_CUBE_SHATTER_CANNOT_DAMAGE)) {
                        float damageAmount = livingEntity.getType().is(TagRegistry.ICE_CUBE_SHATTER_CAN_INSTAKILL)
                                ? Float.MAX_VALUE : ConfigRegistry.ICE_CUBE_DAMAGE.get().floatValue();
                        if (this.getOwner() != null)
                            livingEntity.hurt(DamageSourceRegistry.iceCubeCrushed(livingEntity, this.getOwner()), damageAmount);
                        else livingEntity.hurt(DamageSourceRegistry.iceCubeCrushed(livingEntity, attackingEntity), damageAmount);
                        livingEntity.hurtDuration = 10;
                        livingEntity.hurtTime = 10;
                        livingEntity.hurtMarked = true;
                    }
                }
                entity.setIsInPowderSnow(true);
                if (entity.canFreeze())
                    entity.setTicksFrozen(ConfigRegistry.ICE_CUBE_FREEZE_DURATION.get());

                this.level().playSound(entity, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, 1.0F, 1.0F);
                this.level().gameEvent(entity, GameEvent.BLOCK_DESTROY, this.blockPosition());

                if (entity.getPersistentData().contains("Scale"))
                    scale = entity.getPersistentData().getFloat("Scale");
                if (entity.getPersistentData().contains("HeightScale"))
                    heightScale = entity.getPersistentData().getFloat("HeightScale");
                if (entity.getPersistentData().contains("WidthScale"))
                    widthScale = entity.getPersistentData().getFloat("WidthScale");

                float height = entity.getBbHeight() * scale * heightScale;
                float width = entity.getBbWidth() * scale * widthScale;

                if (entity.getBbHeight() >= entity.getBbWidth() * 3)
                    width *= 2.0F;

                float scaleFactor = height * width * 1.2F;
                int numParticles = (int) (scaleFactor * 10);
                for(int i = 0; i < numParticles; ++i) {
                    ServerParticleUtils.spawnEntityBreakParticles(ParticleRegistry.ICE_CUBE_SHATTER.get(), serverWorld, entity, height * 1.55F, width * 1.55F);
                }
            }
        }
        if (this.getControllingPassenger() instanceof AbilitiesHandler handler)
            handler.mv$setFreezeImmunityCooldown(20);
        this.ejectPassengers();
        this.discard();
        this.setRemoved(RemovalReason.DISCARDED);
    }

    private void collideWithWall(Level world, BlockPos pos) {
        if (!world.isClientSide) {
            AABB boundingBox = this.getBoundingBox();

            for (Direction direction : Direction.values()) {
                if (direction.getAxis().isHorizontal() && this.getDeltaMovement().y == 0
                        && this.getDeltaMovement().horizontalDistance() > 0) {
                    for (BlockPos hitPos : BlockPos.betweenClosed(
                            Mth.floor(boundingBox.minX), Mth.floor(boundingBox.minY), Mth.floor(boundingBox.minZ),
                            Mth.floor(boundingBox.maxX), Mth.floor(boundingBox.maxY), Mth.floor(boundingBox.maxZ))) {

                        BlockPos checkPos = hitPos.relative(direction);
                        BlockState hitState = world.getBlockState(checkPos);

                        if (hitState.isSolid()) {
                            this.shatterIceCube(false, true, this);
                            return;
                        }
                    }
                }
            }

            for (Direction direction : Direction.values()) {
                for (BlockPos hitPos : BlockPos.betweenClosed(
                        Mth.floor(boundingBox.minX), Mth.floor(boundingBox.minY), Mth.floor(boundingBox.minZ),
                        Mth.floor(boundingBox.maxX), Mth.floor(boundingBox.maxY), Mth.floor(boundingBox.maxZ))) {

                    BlockPos checkPos = hitPos.relative(direction);
                    BlockState hitState = world.getBlockState(checkPos);

                    if (hitState.is(BlockTags.FIRE)
                            || hitState.getFluidState().is(FluidTags.LAVA)
                            || (hitState.is(BlockTags.CAMPFIRES) && hitState.hasProperty(BlockStateProperties.LIT)
                            && hitState.getValue(BlockStateProperties.LIT))
                            || this.isOnFire()) {
                        if (hitState.is(BlockTags.FIRE)) {
                            world.setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                            if (!world.isClientSide())
                                world.levelEvent(null, 1009, checkPos, 0);
                        }
                        this.shatterIceCube(false, false, this);
                    }

                    if (hitState.hasProperty(BlockStateProperties.LIT)
                            && hitState.getValue(BlockStateProperties.LIT))
                        hitState.setValue(BlockStateProperties.LIT, false);
                }
            }
        }
    }

    private void collideWithEntity() {
        AABB collisionBox = this.getBoundingBox().inflate(0.01, 0, 0.01);
        AABB collisionBoxNoInflation = this.getBoundingBox();
        List<Entity> collidingEntities = this.level().getEntities(this, collisionBox);
        List<Entity> collidingEntitiesNoInflation = this.level().getEntities(this, collisionBoxNoInflation);

        for (Entity entity : collidingEntities) {
            if (this.getDeltaMovement().horizontalDistance() > 0) {
                if (entity instanceof IceCubeEntity otherIceCube
                        && (this.getDeltaMovement().horizontalDistance() >= 0.2
                        || this.getDeltaMovement().horizontalDistance() <= -0.2)) {
                    this.shatterIceCube(false, true, this);
                    otherIceCube.shatterIceCube(false, true, this);
                } else if (entity instanceof LivingEntity livingEntity
                        && (this.getDeltaMovement().horizontalDistance() >= 0.5
                        || this.getDeltaMovement().horizontalDistance() <= -0.5)
                    && !livingEntity.getType().is(TagRegistry.ICE_CUBE_COLLISION_CANNOT_DAMAGE)) {
                    ItemStack shield = livingEntity.getUseItem();
                    Vec3 toIceCube = this.position().subtract(livingEntity.position()).normalize();
                    Vec3 look = livingEntity.getLookAngle().normalize();
                    double dot = toIceCube.dot(look);

                    if (livingEntity.isBlocking() && dot > 0.25) {
                        this.deflect(entity, livingEntity, true);
                        shield.hurtAndBreak(1, livingEntity, LivingEntity.getSlotForHand(livingEntity.getUsedItemHand()));
                        this.level().playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                                SoundSource.NEUTRAL, 1.0F, 1.0F);
                        continue;
                    }

                    if (this.getOwner() != null)
                        livingEntity.hurt(DamageSourceRegistry.iceCubeCrushed(livingEntity, this.getOwner()), ConfigRegistry.ICE_CUBE_DAMAGE.get().floatValue());
                    else livingEntity.hurt(DamageSourceRegistry.iceCubeCrushed(livingEntity, this), ConfigRegistry.ICE_CUBE_DAMAGE.get().floatValue());
                }
            }

            if (entity instanceof AbstractArrow arrow) {
                this.shatterIceCube(false, false, this);
                if (arrow.isOnFire())
                    arrow.extinguishFire();
                if (arrow instanceof SpectralArrow)
                    this.setGlowingTag(true);
            }
        }

        for (Entity entity : collidingEntitiesNoInflation) {
            entity.extinguishFire();
            if (entity instanceof Player player && !player.isCreative()) {
                entity.setIsInPowderSnow(true);
                if (player.canFreeze())
                    entity.setTicksFrozen(ConfigRegistry.ICE_CUBE_FREEZE_DURATION.get());
            }
        }
    }

    private boolean isOnSolidGround() {
        BlockPos posBelow = this.blockPosition().below();
        return this.level().getBlockState(posBelow).isSolidRender(this.level(), posBelow);
    }

    private void gravityWaterPhysics() {
        Vec3 velocity = this.getDeltaMovement();
        Vec3 vecPos = this.position();

        float entityHeight = this.getBbHeight();
        if (this.displayEntity != null)
            entityHeight = this.displayEntity.getBbHeight();
        AABB aabb = this.getBoundingBox();
        if (this.displayEntity != null)
            aabb = this.displayEntity.getBoundingBox();
        double entityTop = aabb.maxY + entityHeight;
        boolean isUnderwater = false;
        double waterLevel = 0.0;

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        for (int x = Mth.floor(aabb.minX); x < Mth.ceil(aabb.maxX); x++) {
            for (int y = Mth.floor(aabb.minY); y < Mth.ceil(entityTop); y++) {
                for (int z = Mth.floor(aabb.minZ); z < Mth.ceil(aabb.maxZ); z++) {
                    blockPos.set(x, y, z);
                    FluidState fluidState = this.level().getFluidState(blockPos);

                    if (!fluidState.isEmpty()) {
                        double fluidHeight = fluidState.getHeight(this.level(), blockPos);
                        waterLevel = Math.max(waterLevel, blockPos.getY() + fluidHeight);
                        if (entityTop < waterLevel) {
                            isUnderwater = true;
                        }
                    }
                }
            }
        }

        BlockState stateAbove = this.level().getBlockState(BlockPos.containing(vecPos.x, vecPos.y + entityHeight, vecPos.z));
        boolean isAirAbove = stateAbove.isAir();
        double waterDrag = 0.9;
        boolean isSinking = vecPos.y + entityHeight / 3.0 >= waterLevel;
        boolean isRising = vecPos.y + entityHeight / 2.0 >= waterLevel;

        if (!this.isOnSolidGround() && this.fallDistance > previousFallDistance)
            previousFallDistance = this.fallDistance;
        if (this.isOnSolidGround() && previousFallDistance > 3) {
            this.shatterIceCube(true, false, this);
            previousFallDistance = 0;
        }

        /*if (this.isInWaterOrBubble() && isAirAbove && !isRising && !isUnderwater) {
            this.setDeltaMovement(velocity.x * waterDrag, 0.01, velocity.z * waterDrag);
        } else if (this.isInWaterOrBubble() && isAirAbove && isRising && isSinking && !isUnderwater) {
            this.setDeltaMovement(velocity.x * waterDrag, -0.01, velocity.z * waterDrag);
        } else if (isUnderwater && !isRising) {
            this.setDeltaMovement(velocity.x * waterDrag, 0.03, velocity.z * waterDrag);
        } else*/ if (!this.isOnSolidGround() && !this.isInWaterOrBubble() && !this.level().isClientSide()) {
            if (this.getTicksInAir() == 0) {
                if (this.displayEntity instanceof Mob mob && !mob.isNoAi())
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -this.getDefaultGravity(), 0.0));
                else if (!(this.displayEntity instanceof Mob))
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0, -this.getDefaultGravity(), 0.0));
            }
            else this.setDeltaMovement(0, 0, 0);
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    protected void spawnSnowParticles() {
        BlockPos posLegacy = this.getOnPosLegacy();
        BlockState state = this.level().getBlockState(posLegacy);
        if (!state.addRunningEffects(this.level(), posLegacy, this)) {
            if (state.getRenderShape() != RenderShape.INVISIBLE) {
                Vec3 vec3 = this.getDeltaMovement();
                BlockPos pos = this.blockPosition();
                double x = this.getX() + (this.random.nextDouble() - 0.5) * this.getSize().x;
                double z = this.getZ() + (this.random.nextDouble() - 0.5) * this.getSize().x;
                if (pos.getX() != posLegacy.getX())
                    x = Mth.clamp(x, posLegacy.getX(), posLegacy.getX() + 1.0);

                if (pos.getZ() != posLegacy.getZ())
                    z = Mth.clamp(z, posLegacy.getZ(), posLegacy.getZ() + 1.0);

                if (!this.isInWaterOrBubble()) {
                    this.level().addParticle(ParticleTypes.SNOWFLAKE, x, this.getY() + 0.1, z, vec3.x * -2.0, 0, vec3.z * -2.0);
                    this.level().addParticle(ParticleRegistry.ICE_CUBE_SHATTER.get(), x, this.getY() + 0.1, z, vec3.x * -2.0, 0, vec3.z * -2.0);
                }
                this.level().addParticle(ParticleRegistry.ICE_STAR.get(), x, this.getY() + 0.1, z, vec3.x * -4.0, 1.5, vec3.z * -4.0);
            }
        }
    }

    public int getFrozenCooldown() {
        return this.frozenCooldown;
    }

    public void setFrozenCooldown(int frozenCooldown) {
        this.frozenCooldown = frozenCooldown;
    }

    public int getEntityFrozenCooldown() {
        return this.getPersistentData().getInt("EntityFrozenCooldown");
    }

    public void setEntityFrozenCooldown(int entityFrozenCooldown) {
        this.getPersistentData().putInt("EntityFrozenCooldown", entityFrozenCooldown);
    }

    public int getTicksInAir() {
        return this.ticksInAir;
    }

    public void setTicksInAir(int ticksInAir) {
        this.ticksInAir = ticksInAir;
    }
}
