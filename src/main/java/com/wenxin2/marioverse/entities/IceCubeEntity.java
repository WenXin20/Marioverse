package com.wenxin2.marioverse.entities;

import com.google.common.base.MoreObjects;
import com.wenxin2.marioverse.entities.projectiles.BouncingIceBallProjectile;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
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
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
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
    private float previousFallDistance = 0;

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
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (FROZEN_DATA.equals(accessor) && level().isClientSide) {
            CompoundTag tag = this.entityData.get(FROZEN_DATA);
            if (!tag.isEmpty()) {
                this.frozenEntityData = tag.copy();
                this.refreshDimensions();
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.put("FrozenData", this.entityData.get(FROZEN_DATA).copy());

        if (this.getFrozenEntityData() != null)
            tag.put("FrozenEntityData", this.getFrozenEntityData());
        if (this.ownerUUID != null)
            tag.putUUID("OwnerUUID", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        this.leftOwner = tag.getBoolean("LeftOwner");

        if (tag.contains("FrozenData", Tag.TAG_COMPOUND))
            this.entityData.set(FROZEN_DATA, tag.getCompound("FrozenData"));
        if (tag.contains("FrozenEntityData", Tag.TAG_COMPOUND))
            this.setFrozenEntityData(tag);

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

        if (this.onGround() && this.getDeltaMovement().horizontalDistance() <= 0.005) {
            if (this.getEntityFrozenCooldown() > 0)
                this.setEntityFrozenCooldown(this.getEntityFrozenCooldown() - 1);
        }
        if (this.getEntityFrozenCooldown() == 0)
            this.shatterIceCube(this, false, false, false);

        if (!this.onGround() && this.getTicksInAir() > 0)
            this.setTicksInAir(this.getTicksInAir() - 1);

        if (this.getDeltaMovement().horizontalDistance() > 0.1)
            this.spawnSnowParticles();

        if (this.getFrozenEntityData() != null) {
            float height = this.getFrozenEntityData().getFloat("Height") * 1.55F;
            float width = this.getFrozenEntityData().getFloat("Width") * 1.55F;
            this.setSize(width, height);
        }

        if (!this.isOnSolidGround() && this.fallDistance > this.previousFallDistance)
            this.previousFallDistance = this.fallDistance;
        if (this.isOnSolidGround() && this.previousFallDistance > 3) {
            this.shatterIceCube(this, true, false, false);
            this.previousFallDistance = 0;
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

        this.collideWithFire(world);
        this.collideWithEntity();

        if (this.horizontalCollision)
            this.shatterIceCube(this, false, true, false);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (itemstack.is(Items.NAME_TAG))
            return InteractionResult.PASS;
        else return super.mobInteract(player, hand);
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
            this.shatterIceCube(this, false, false, false);
            return true;
        } else if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.shatterIceCube(null, false, false, false);
            return true;
        } else {
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

            if (!isNoAi() || !(this.displayEntity instanceof Mob)) {
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
    protected boolean wouldNotSuffocateAtTargetPose(Pose pose) {
        return false;
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
                    if (d3 > 1.0)
                        d3 = 1.0;

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05F;
                    d1 *= 0.05F;

                    if (!this.isVehicle() && this.isPushable())
                        this.push(-d0, 0.0, -d1);
                    if (!entity.isVehicle() && entity.isPushable())
                        entity.push(d0, 0.0, d1);
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
        if (this.getTicksInAir() > 0 && !this.onGround()
                && this.getDeltaMovement().horizontalDistance() == 0)
            return true;
        else if (this.isNoAi())
            return true;
        else return super.isNoGravity();
    }

    @Override
    protected void applyGravity() {
        if (this.getTicksInAir() == 0 && !this.isNoAi())
            super.applyGravity();
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
            moveFunction.accept(riderEntity, this.getX(), this.getY() + riderEntity.getBbHeight() / 4, this.getZ());
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

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        if (!this.getPassengers().isEmpty())
            return false;
        return this.getFrozenEntityData() == null;
    }

    @Override
    public void turn(double yaw, double pitch) {
    }

    @Override
    public void setYRot(float yaw) {
    }

    @Override
    public void setYHeadRot(float yaw) {
    }

    @Override
    public void setYya(float yaw) {
    }

    @Override
    public void setRot(float yaw, float pitch) {
    }

    @NotNull
    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        if (this.getFrozenEntityData() != null) {
            float height = this.getFrozenEntityData().getFloat("Height") * 1.55F;
            float width = this.getFrozenEntityData().getFloat("Width") * 1.55F;

            return EntityDimensions.fixed(width, height);
        }
        return super.getDefaultDimensions(pose);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUUID = entity.getUUID();
            this.cachedOwner = entity;
        }
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverWorld) {
            this.cachedOwner = serverWorld.getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else return null;
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
            float eyeHeightScale = 1.0F;
            float heightScale = 1.0F;
            float widthScale = 1.0F;
            this.frozenEntityData = new CompoundTag();
            entity.save(frozenEntityData);

            if (entity instanceof LivingEntity living) {
                AttributeInstance scaleAttribute = living.getAttribute(Attributes.SCALE);
                AttributeInstance eyeHeightScaleAttribute = living.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
                AttributeInstance heightScaleAttribute = living.getAttribute(AttributesRegistry.HEIGHT_SCALE);
                AttributeInstance widthScaleAttribute = living.getAttribute(AttributesRegistry.WIDTH_SCALE);
                if (scaleAttribute != null)
                    scale = (float) scaleAttribute.getValue();
                if (eyeHeightScaleAttribute != null)
                    eyeHeightScale = (float) eyeHeightScaleAttribute.getValue();
                if (heightScaleAttribute != null)
                    heightScale = (float) heightScaleAttribute.getValue();
                if (widthScaleAttribute != null)
                    widthScale = (float) widthScaleAttribute.getValue();

                this.copyAttributeWithModifiers(living, Attributes.SAFE_FALL_DISTANCE);
                this.getFrozenEntityData().putFloat("YBodyRot", living.yBodyRot);
            }

            if (entity instanceof Mob mob)
                this.setNoAi(mob.isNoAi());

            if (!level().isClientSide) {
                this.getFrozenEntityData().putString("id", EntityType.getKey(entity.getType()).toString());
                this.getFrozenEntityData().putFloat("BodyRotation", entity.getYRot());
                this.getFrozenEntityData().putFloat("HeadRotation", entity.getYHeadRot());
                this.getFrozenEntityData().putFloat("Pitch", entity.getXRot());
                this.getFrozenEntityData().putFloat("EyeHeight", entity.getEyeHeight());
                this.getFrozenEntityData().putFloat("Height", entity.getBbHeight());
                this.getFrozenEntityData().putFloat("Width", entity.getBbWidth());
                this.getFrozenEntityData().putFloat("Scale", scale);
                this.getFrozenEntityData().putFloat("EyeHeightScale", eyeHeightScale);
                this.getFrozenEntityData().putFloat("HeightScale", heightScale);
                this.getFrozenEntityData().putFloat("WidthScale", widthScale);
                this.entityData.set(FROZEN_DATA, frozenEntityData);
            }

            this.setHeight(entity.getBbHeight());
            this.setWidth(entity.getBbWidth());
            this.setEyeHeightScale(eyeHeightScale);
            this.setHeightScale(heightScale);
            this.setWidthScale(widthScale);
            this.setScale(scale);

            if (entity instanceof KoopaTroopaEntity koopa)
                koopa.hide(koopa.isHiding());

            this.setSize(entity.getBbWidth() * scale * widthScale * 1.55F, entity.getBbHeight() * scale * heightScale * 1.55F);
            if (!(entity instanceof Player))
                entity.discard();

            this.refreshDimensions();
            this.setEntityFrozenCooldown(ticksFrozen);
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
                if (this.displayEntity instanceof LivingEntity entity) {
                    entity.hurtDuration = 0;
                    entity.hurtTime = 0;
                    entity.yBodyRot = this.getYBodyRot();

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
        if (this.displayEntity == null)
            this.displayEntity = this.getOrCreateDisplayEntity(this.level());
    }

    public void shatterIceCube(@Nullable Entity attackingEntity, boolean applyFallDamage, boolean applyCollisionDamage, boolean useWaterParticles) {
        if (this.getFrozenEntityData() != null && this.level() instanceof ServerLevel serverWorld) {
            Entity entity = EntityType.loadEntityRecursive(this.getFrozenEntityData(), serverWorld, (e) -> {
                e.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                return e;
            });

            if (entity != null) {
                if (attackingEntity != null)
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
                        else if (attackingEntity != null) livingEntity.hurt(DamageSourceRegistry.iceCubeCrushed(livingEntity, attackingEntity), damageAmount);
                        livingEntity.hurtDuration = 10;
                        livingEntity.hurtTime = 10;
                        livingEntity.hurtMarked = true;
                    }
                    livingEntity.yBodyRot = this.getYBodyRot();
                }
                entity.setYRot(this.getBodyRotation());
                entity.setYHeadRot(this.getHeadRotation());
                entity.setXRot(this.getPitch());
                entity.setIsInPowderSnow(true);
                this.discard();
                if (entity.canFreeze())
                    entity.setTicksFrozen(ConfigRegistry.ICE_CUBE_FREEZE_DURATION.get());

                this.level().playSound(this, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, 1.0F, 1.0F);
                if (useWaterParticles)
                    this.spawnShatterParticles(serverWorld, entity, ParticleTypes.SPLASH);
                else this.spawnShatterParticles(serverWorld, entity, ParticleRegistry.ICE_CUBE_SHATTER.get());
            }
        }

        if (this.getControllingPassenger() != null)
            this.getControllingPassenger().setData(DataAttachmentRegistry.FREEZE_IMMUNITY_DURATION, 20);
        this.ejectPassengers();

        if (this.previousFallDistance > 3 || attackingEntity != null || this.getEntityFrozenCooldown() == 0) {
            this.discard();
            this.level().playSound(this, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.AMBIENT, 1.0F, 1.0F);

            if (this.level() instanceof ServerLevel serverWorld) {
                if (useWaterParticles)
                    this.spawnShatterParticles(serverWorld, this, ParticleTypes.SPLASH);
                else this.spawnShatterParticles(serverWorld, this, ParticleRegistry.ICE_CUBE_SHATTER.get());
            }
        }
    }

    private void collideWithFire(Level world) {
        AABB box = this.getBoundingBox().inflate(0.05);

        for (BlockPos hitPos : BlockPos.betweenClosed(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ),
                Mth.floor(box.maxX), Mth.floor(box.maxY), Mth.floor(box.maxZ))) {
            BlockState state = world.getBlockState(hitPos);

            if (state.is(TagRegistry.ICE_CUBE_EXTINGUISHES) || state.getFluidState().is(FluidTags.LAVA)) {
                if (state.is(BlockTags.FIRE)) {
                    if (this.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 10);
                    world.setBlock(hitPos, Blocks.AIR.defaultBlockState(), 3);
                    world.playSound(null, hitPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                if (state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
                    if (this.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnParticleRingOnBlock(ParticleTypes.SMOKE, serverWorld, hitPos, 0.25D, 10);
                    world.setBlock(hitPos, state.setValue(BlockStateProperties.LIT, false), 3);
                    world.playSound(null, hitPos, state.getBlock() instanceof CandleBlock || state.getBlock() instanceof CandleCakeBlock
                            ? SoundEvents.CANDLE_EXTINGUISH : SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }

            if (this.isOnFire() || state.getFluidState().is(FluidTags.LAVA)) {
                this.shatterIceCube(this, false, false, true);
                break;
            }

            if (state.is(TagRegistry.MELTS_ICE_CUBE) && state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT)) {
                this.shatterIceCube(this, false, false, true);
                break;
            } else if (state.is(TagRegistry.MELTS_ICE_CUBE) && !state.hasProperty(BlockStateProperties.LIT)) {
                this.shatterIceCube(this, false, false, true);
                break;
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
                    this.shatterIceCube(this, false, true, false);
                    otherIceCube.shatterIceCube(this, false, true, false);
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
                this.shatterIceCube(this, false, false, false);
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

    private void spawnShatterParticles(ServerLevel serverWorld, Entity entity, ParticleOptions particle) {
        float height = this.getHeight() * this.getScale() * this.getHeightScale() * 1.55F;
        float width = this.getWidth() * this.getScale() * this.getWidthScale() * 1.55F;

        if (entity.getBbHeight() >= entity.getBbWidth() * 3)
            width *= 2.0F;

        float scaleFactor = height * width * 1.2F;
        int numParticles = (int) (scaleFactor * 10);

        for(int i = 0; i < numParticles; ++i) {
            ServerParticleUtils.spawnEntityBreakParticles(particle, serverWorld, entity, height, width);
        }
    }

    public CompoundTag getFrozenEntityData() {
        return this.frozenEntityData;
    }

    public void setFrozenEntityData(CompoundTag tag) {
        this.frozenEntityData = tag.getCompound("FrozenEntityData");
    }

    public Vec2 getSize() {
        return new Vec2(this.getWidth(), this.getHeight());
    }

    public void setSize(float width, float height) {
        this.setHeight(height);
        this.setWidth(width);
        this.refreshDimensions();
        this.setBoundingBox(new AABB(this.getX() - width / 2, this.getY(), this.getZ() - width / 2,
                this.getX() + width / 2, this.getY() + height , this.getZ() + width / 2));
    }

    public float getHeight() {
        return this.getData(DataAttachmentRegistry.HEIGHT);
    }

    public void setHeight(float height) {
        this.setData(DataAttachmentRegistry.HEIGHT, height);
    }

    public float getWidth() {
        return this.getData(DataAttachmentRegistry.WIDTH);
    }

    public void setWidth(float width) {
        this.setData(DataAttachmentRegistry.WIDTH, width);
    }

    public void setScale(float scale) {
        this.setData(DataAttachmentRegistry.SCALE, scale);
    }

    public float getEyeHeightScale() {
        AttributeMap attributeMap = this.getAttributes();
        return attributeMap == null ? 1.0F : this.sanitizeScale((float) attributeMap.getValue(AttributesRegistry.EYE_HEIGHT_SCALE));
    }

    public void setEyeHeightScale(float eyeHeightScale) {
        this.setData(DataAttachmentRegistry.EYE_HEIGHT_SCALE, eyeHeightScale);
    }

    public float getHeightScale() {
        AttributeMap attributeMap = this.getAttributes();
        return attributeMap == null ? 1.0F : this.sanitizeScale((float) attributeMap.getValue(AttributesRegistry.HEIGHT_SCALE));
    }

    public void setHeightScale(float heightScale) {
        this.setData(DataAttachmentRegistry.HEIGHT_SCALE, heightScale);
    }

    public float getWidthScale() {
        AttributeMap attributeMap = this.getAttributes();
        return attributeMap == null ? 1.0F : this.sanitizeScale((float) attributeMap.getValue(AttributesRegistry.WIDTH_SCALE));
    }

    public void setWidthScale(float widthScale) {
        this.setData(DataAttachmentRegistry.WIDTH_SCALE, widthScale);
    }

    public int getEntityFrozenCooldown() {
        return this.getData(DataAttachmentRegistry.ENTITY_FROZEN_COOLDOWN);
    }

    public void setEntityFrozenCooldown(int frozenCooldown) {
        this.setData(DataAttachmentRegistry.ENTITY_FROZEN_COOLDOWN, frozenCooldown);
    }

    public int getTicksInAir() {
        return this.getData(DataAttachmentRegistry.TICKS_IN_AIR);
    }

    public void setTicksInAir(int ticksInAir) {
        this.setData(DataAttachmentRegistry.TICKS_IN_AIR, ticksInAir);
    }

    public float getBodyRotation() {
        return this.getData(DataAttachmentRegistry.BODY_ROTATION);
    }

    public void setBodyRotation(float bodyRotation) {
        this.setData(DataAttachmentRegistry.BODY_ROTATION, bodyRotation);
    }

    public float getHeadRotation() {
        return this.getData(DataAttachmentRegistry.HEAD_ROTATION);
    }

    public void setHeadRotation(float headRotation) {
        this.setData(DataAttachmentRegistry.HEAD_ROTATION, headRotation);
    }

    public float getPitch() {
        return this.getData(DataAttachmentRegistry.PITCH);
    }

    public void setPitch(float pitch) {
        this.setData(DataAttachmentRegistry.PITCH, pitch);
    }

    public float getYBodyRot() {
        return this.getData(DataAttachmentRegistry.Y_BODY_ROT);
    }

    public void setYBodyRot(float yBodyRot) {
        this.setData(DataAttachmentRegistry.Y_BODY_ROT, yBodyRot);
    }

    private void copyAttributeWithModifiers(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance fromAttr = entity.getAttribute(attribute);
        AttributeInstance toAttr = this.getAttribute(attribute);

        if (fromAttr != null && toAttr != null) {
            toAttr.setBaseValue(fromAttr.getBaseValue());
            for (AttributeModifier modifier : fromAttr.getModifiers())
                toAttr.addPermanentModifier(modifier);
        }
    }
}