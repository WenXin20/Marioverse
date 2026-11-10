package com.wenxin2.marioverse.entities;

import com.google.common.base.MoreObjects;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.integration.automobility_compat.AutomobilityEntityProvider;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public abstract class ShellEntity extends Monster implements CrackableEntity, GeoEntity, TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_BOUNCE_COUNT = SynchedEntityData.defineId(ShellEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_KILL_COUNT = SynchedEntityData.defineId(ShellEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HELD_SLOT = SynchedEntityData.defineId(ShellEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HOLDER_ID = SynchedEntityData.defineId(ShellEntity.class, EntityDataSerializers.INT);
    public static final RawAnimation FLIP = RawAnimation.begin().thenPlayAndHold("misc.flip");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    public static final RawAnimation SPIN = RawAnimation.begin().thenLoop("move.spin");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    public static final int THROW_DELAY_TICKS = 10;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public final Set<UUID> entityCollided = new HashSet<>();
    public Vec3 slidingMovement = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private boolean leftOwner;
    private int holdTicks;

    public ShellEntity(EntityType<? extends ShellEntity> type, Level world) {
        super(type, world);
        this.xpReward = 6;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.KOOPA_SHELL_STOMP.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.KOOPA_SHELL_SHATTER.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this).transitionLength(0));
        controllers.add(new AnimationController<>(this, "flip_controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("flip", FLIP));
        controllers.add(new AnimationController<>(this, "spin", 5, this::walkAnimation));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BOUNCE_COUNT, 0);
        builder.define(DATA_KILL_COUNT, 0);
        builder.define(DATA_HELD_SLOT, -1);
        builder.define(DATA_HOLDER_ID, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("BounceCount", this.entityData.get(DATA_BOUNCE_COUNT));
        tag.putInt("KillCount", this.entityData.get(DATA_KILL_COUNT));

        if (this.ownerUUID != null)
            tag.putUUID("Owner", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_BOUNCE_COUNT, tag.getInt("BounceCount"));
        this.entityData.set(DATA_KILL_COUNT, tag.getInt("KillCount"));
        this.leftOwner = tag.getBoolean("LeftOwner");

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }
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
        Vec3 motion = this.getDeltaMovement();

        this.tickHolding();

        if (this.isSliding())
            this.spawnTrailParticles();

        if (!this.leftOwner)
            this.leftOwner = this.checkLeftOwner();

        if (this.getBounceCount() >= ConfigRegistry.MAX_KOOPA_SHELL_DAMAGE_POINTS.get().floatValue()) {
            this.playDeathAnimation(this);
            this.discard();
        }

        if (this.isAlive() && !this.isNoAi()) {
            this.collideWithWall(this.level());
        }

        if (this.isSliding() && this.isAlive() && !this.isNoAi()) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = level().getBlockState(posBelow);
            float friction = stateBelow.getFriction(level(), posBelow, this);
            double slideFriction = /*(friction > 0.8) ? 0.4 + friction / 1.5 :*/ 1.0;
            Vec3 slideMotion = this.slidingMovement.scale(slideFriction);

            if (this.getLastDamageSource() != null
                    && this.getDeltaMovement().horizontalDistance() > 0
                    && (this.getLastDamageSource().is(DamageTypeRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageTypeRegistry.PLAYER_STOMP))) {
                this.setDeltaMovement(Vec3.ZERO);
                this.slidingMovement = Vec3.ZERO;
            } else if (this.getLastDamageSource() != null
                    && this.getLastDamageSource().getEntity() != null
                    && this.getDeltaMovement().horizontalDistance() == 0
                    && (this.getLastDamageSource().is(DamageTypeRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageTypeRegistry.PLAYER_STOMP))) {
                Vec3 lookDir = this.getLastDamageSource().getEntity().getLookAngle().normalize();
                Vec3 horizontalDir = new Vec3(lookDir.x, 0, lookDir.z).normalize();
                Vec3 newSlideMotion = horizontalDir.scale(1.2);

                this.setDeltaMovement(newSlideMotion.x, this.getDeltaMovement().y, newSlideMotion.z);
                this.slidingMovement = new Vec3(newSlideMotion.x, this.getDeltaMovement().y, newSlideMotion.z);
                this.hasImpulse = true;
            } else if ((this.onGround()) && motion.horizontalDistance() > 0.0001) {
                this.setDeltaMovement(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
                this.slidingMovement = new Vec3(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
                this.hasImpulse = true;
            } else if (this.isInWaterOrBubble() && !this.isNoGravity()) {
                this.setDeltaMovement(slideMotion.x, this.getDeltaMovement().y - this.getAttributeValue(Attributes.GRAVITY), slideMotion.z);
            }
        }

        if (!this.isNoAi()) {
            if (motion.length() == 0) {
                this.setSliding(false);
            } else if (!this.isSliding() && this.isAlive() && motion.horizontalDistance() > 0.0001) {
                this.setSliding(true);
                this.setDeltaMovement(motion);
                this.slidingMovement = motion;
                this.hasImpulse = true;
            }
        }

        if (this.isDeadOrDying()) {
            this.setSliding(false);
            this.setDeltaMovement(Vec3.ZERO);
            this.slidingMovement = Vec3.ZERO;
        }
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || this.isHeld()
                || !player.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS)
                || this.getDeltaMovement().horizontalDistance() >= 0.1)
            return InteractionResult.PASS;

        if (this.level().isClientSide)
            return InteractionResult.SUCCESS;

        if (this.isEmptyShell())
            return this.pickUpAsItem(player);
        else if (player.getMainHandItem().isEmpty() && ShellEntity.getHeldShell(player) == null) {
            this.startHolding(player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void push(Entity entity) {
        if (this.isHeld())
            return;

        Vec3 lookDir = entity.getLookAngle().normalize();
        Vec3 horizontalDir = new Vec3(lookDir.x, 0, lookDir.z).normalize();
        Vec3 slideMotion = horizontalDir.scale(1.2);

        if (this.getOwner() == null) {
            this.setOwner(entity);
            this.leftOwner = false;
        }

        if ((this.onGround()) && entity.getDeltaMovement().horizontalDistance() > 0
                && (this.getOwner() == null || this.getOwner() == entity)) {
            this.setDeltaMovement(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
            this.slidingMovement = new Vec3(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
            this.hasImpulse = true;
        }
        super.push(entity);
    }

    @Override
    protected void doPush(Entity entity) {
        if (this.isHeld())
            return;

        if (this.getOwner() == null) {
            this.setOwner(entity);
            this.leftOwner = false;
        }

        if (!entity.level().isClientSide && this.isAlive() && !this.isNoAi())
            this.collideWithEntity(entity);
        super.doPush(entity);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isHeld() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;

        Level world = this.level();
        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = world.getBlockState(posBelow);

        if (source.is(TagRegistry.FLIPS_KOOPA_SHELL))
            this.triggerAnim("flip_controller", "flip");

        if (source.is(TagRegistry.STOPS_KOOPA_SHELL)) {
            if (this.slidingMovement != Vec3.ZERO) {
                this.setXxa(0.0F);
                this.setSpeed(0.0F);
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                this.slidingMovement = new Vec3(0, this.getDeltaMovement().y, 0);
                this.setSliding(false);
            }
        }

        if (!source.is(TagRegistry.FLIPS_KOOPA_SHELL) && !source.is(TagRegistry.STOPS_KOOPA_SHELL)
                || this.slidingMovement == Vec3.ZERO) {
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

            if (!isNoAi()) {
                this.setDeltaMovement(movement.x, this.getDeltaMovement().y, movement.z);
                this.slidingMovement = new Vec3(movement.x, this.getDeltaMovement().y, movement.z);
                this.setSliding(true);
                this.hasImpulse = true;

                if (this.getOwner() == null || source.getEntity() instanceof Player) {
                    this.setOwner(source.getEntity());
                    this.leftOwner = false;
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        this.playDeathAnimation(this);
        super.die(source);
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isHeld())
            return;

        if (this.isControlledByLocalInstance()) {
            double d9 = this.getY();
            double d0 = this.getGravity();
            boolean flag = this.getDeltaMovement().y <= 0.0;
            FluidState fluidstate = this.level().getFluidState(this.blockPosition());
            if (flag && this.hasEffect(MobEffects.SLOW_FALLING))
                d0 = Math.min(d0, 0.01);

            if ((this.isInWaterOrBubble() || (this.isInFluidType(fluidstate)
                    && fluidstate.getFluidType() != NeoForgeMod.LAVA_TYPE.value()))
                    && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)) {
                if (this.isInWaterOrBubble() || (this.isInFluidType(fluidstate)
                        && !this.moveInFluid(fluidstate, vec3, d0))) {
                    float f4 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
                    float f5 = 0.02F;
                    float f6 = (float) this.getAttributeValue(Attributes.WATER_MOVEMENT_EFFICIENCY);

                    if (!this.onGround())
                        f6 *= 1.0F;

                    if (f6 > 0.0F) {
                        f4 += (0.54600006F - f4) * f6;
                        f5 += (this.getSpeed() - f5) * f6;
                    }

                    if (this.hasEffect(MobEffects.DOLPHINS_GRACE))
                        f4 = 0.96F;

                    f5 *= (float) this.getAttributeValue(NeoForgeMod.SWIM_SPEED);
                    this.moveRelative(f5, vec3);
                    this.move(MoverType.SELF, this.getDeltaMovement());
                    Vec3 vec36 = this.getDeltaMovement();
                    if (this.horizontalCollision && this.onClimbable())
                        vec36 = new Vec3(vec36.x, 0.5, vec36.z);

                    this.setDeltaMovement(vec36.multiply(f4, 0.7F, f4));
                    Vec3 vec32 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                    this.setDeltaMovement(vec32);
                    if (this.horizontalCollision
                            && this.isFree(vec32.x, vec32.y + 0.6F - this.getY() + d9, vec32.z))
                        this.setDeltaMovement(vec32.x, 0.5F, vec32.z);
                }
            } else super.travel(vec3);
        }
    }

    @Override
    public boolean isPickable() {
        return !this.isHeld() && super.isPickable();
    }

    @Override
    public boolean isPushable() {
        return !this.isHeld() && super.isPushable();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    protected boolean updateInWaterStateAndDoFluidPushing() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    public @NotNull AABB makeBoundingBox() {
        return super.makeBoundingBox();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public Crackiness.Level getCrackiness() {
        return Crackiness.WOLF_ARMOR.byFraction(1.0F - ((float) this.getBounceCount() / ConfigRegistry.MAX_KOOPA_SHELL_DAMAGE_POINTS.getAsInt()));
    }

    @Nullable
    @Override
    public Entity getOwner() {
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved()) {
            return this.cachedOwner;
        } else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverWorld) {
            this.cachedOwner = serverWorld.getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else {
            return null;
        }
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof ShellEntity shell)
            this.cachedOwner = shell.cachedOwner;
    }

    @NotNull
    public abstract SimpleParticleType getShatterParticle();

    public abstract TagKey<EntityType<?>> getInstaKillEntityTag();

    public abstract float getShellDamage();

    public boolean isEmptyShell() {
        return true;
    }

    public boolean canStayHeld() {
        return true;
    }

    protected void onPlaced() {}

    protected <E extends GeoAnimatable> PlayState walkAnimation(final AnimationState<E> event) {
        if (this.isSliding()) {
            event.setAndContinue(SPIN);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE);
            return PlayState.CONTINUE;
        }
    }

    public void setSliding(boolean isSliding) {
        this.setData(DataAttachmentRegistry.IS_SLIDING.get(), isSliding);
    }

    public boolean isSliding() {
        return this.getData(DataAttachmentRegistry.IS_SLIDING.get());
    }

    public void setBounceCount(int bounceCount) {
        this.entityData.set(DATA_BOUNCE_COUNT, bounceCount);
    }

    public int getBounceCount() {
        return this.entityData.get(DATA_BOUNCE_COUNT);
    }

    public void setKillCount(int killCount) {
        this.entityData.set(DATA_KILL_COUNT, killCount);
    }

    public int getKillCount() {
        return this.entityData.get(DATA_KILL_COUNT);
    }

    public int getHeldSlot() {
        return this.entityData.get(DATA_HELD_SLOT);
    }

    public int getHoldTicks() {
        return this.holdTicks;
    }

    public boolean isHeld() {
        return this.entityData.get(DATA_HOLDER_ID) != -1;
    }

    @Nullable
    public LivingEntity getHolder() {
        int holderId = this.entityData.get(DATA_HOLDER_ID);
        return holderId != -1 && this.level().getEntity(holderId) instanceof LivingEntity holder ? holder : null;
    }

    public void setOwner(@Nullable Entity ownerEntity) {
        if (ownerEntity != null) {
            this.ownerUUID = ownerEntity.getUUID();
            this.cachedOwner = ownerEntity;
        }
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

    public void deflect(@Nullable Entity entity, @Nullable Entity ownerEntity, boolean deflect) {
        this.setOwner(entity);
        this.leftOwner = false;
        this.onDeflection(entity, deflect);
    }

    protected void onDeflection(@Nullable Entity entity, boolean deflect) {
        Vec3 motion = this.slidingMovement;
        this.setDeltaMovement(new Vec3(-motion.x, motion.y, -motion.z));
        this.slidingMovement = new Vec3(-motion.x, motion.y, -motion.z);
        if (this.getBounceCount() != -1)
            this.setBounceCount(this.getBounceCount() + 1);
    }

    @Nullable
    public static ShellEntity getHeldShell(Entity holder) {
        List<ShellEntity> shells = holder.level().getEntitiesOfClass(ShellEntity.class, holder.getBoundingBox().inflate(4.0),
                shell -> shell.entityData.get(DATA_HOLDER_ID) == holder.getId());
        return shells.isEmpty() ? null : shells.getFirst();
    }

    public static ItemStack createShellItem(SpawnEggItem item, int bounceCount) {
        ItemStack stack = new ItemStack(item);

        if (bounceCount > 0) {
            CompoundTag tag = new CompoundTag();
            tag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(item.getType(stack)).toString());
            tag.putInt("BounceCount", bounceCount);
            stack.set(DataComponents.ENTITY_DATA, CustomData.of(tag));
        }
        return stack;
    }

    public Vec3 getHoldPosition(Entity holder, float partialTick) {
        float xRot = holder.getViewXRot(partialTick);
        float yRot = holder.getViewYRot(partialTick);
        Vec3 look = Vec3.directionFromRotation(xRot, yRot);
        Vec3 up = Vec3.directionFromRotation(xRot - 90.0F, yRot);
        double distance = holder.getBbWidth() * 0.5 + this.getBbWidth() * 0.5 + 0.35;

        return holder.getEyePosition(partialTick).add(look.scale(distance)).add(up.scale(-0.45))
                .subtract(0.0, this.getBbHeight() * 0.5, 0.0);
    }

    public void startHolding(Player player) {
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;
        Vec3 holdPos = this.getHoldPosition(player, 1.0F);

        this.setDeltaMovement(Vec3.ZERO);
        this.slidingMovement = Vec3.ZERO;
        this.setSliding(false);
        this.holdTicks = 0;
        this.entityData.set(DATA_HOLDER_ID, player.getId());
        this.entityData.set(DATA_HELD_SLOT, player.getInventory().selected);
        this.setPos(holdPos.x, holdPos.y, holdPos.z);
        this.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, pitch);
    }

    public void releaseHold() {
        LivingEntity holder = this.getHolder();

        this.entityData.set(DATA_HOLDER_ID, -1);
        this.entityData.set(DATA_HELD_SLOT, -1);
        this.holdTicks = 0;
        this.setDeltaMovement(Vec3.ZERO);
        this.slidingMovement = Vec3.ZERO;

        if (holder != null) {
            Vec3 holdPos = this.getHoldPosition(holder, 1.0F);

            if (this.level().noCollision(this, this.getDimensions(this.getPose()).makeBoundingBox(holdPos)))
                this.setPos(holdPos.x, holdPos.y, holdPos.z);
            else this.setPos(holder.getX(), holder.getY(), holder.getZ());
        }
    }

    private void tickHolding() {
        if (!this.isHeld()) {
            this.holdTicks = 0;
            return;
        }

        LivingEntity holder = this.getHolder();

        if (holder == null || holder.isRemoved()) {
            if (!this.level().isClientSide)
                this.releaseHold();
            return;
        }

        Vec3 holdPos = this.getHoldPosition(holder, 1.0F);
        this.holdTicks++;
        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(holdPos.x, holdPos.y, holdPos.z);
        this.setYRot(holder.getYRot());
        this.yBodyRot = holder.getYRot();
        this.setYHeadRot(holder.getYRot());

        if (!this.level().isClientSide && (!holder.isAlive() || holder.isSpectator() || !this.canStayHeld()
                || (holder instanceof Player player && player.getInventory().selected != this.getHeldSlot())))
            this.releaseHold();
    }

    public void throwFrom(LivingEntity thrower) {
        Level level = thrower.level();
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        Entity rootVehicle = thrower.getRootVehicle();
        boolean isAutomobile = rootVehicle.getType() == CompatRegistry.AUTOMOBILE.get()
                || rootVehicle.getType() == CompatRegistry.AUTOMOBILITY_HITBOX.get();
        double minSpeed = 1.25;
        double spawnDistance = 1.0;

        if (this.isHeld())
            this.releaseHold();

        Vec3 look = thrower.getLookAngle();
        double horizontalSpeed = Math.sqrt(rootVehicle.getDeltaMovement().horizontalDistanceSqr());

        if (ModList.get().isLoaded("automobility") && isAutomobile)
            horizontalSpeed = AutomobilityEntityProvider.getHSpeed(rootVehicle);
        double speed = Math.max(minSpeed, horizontalSpeed * 3);

        Vec3 spawnPos = thrower.position()
                .add(look.x * spawnDistance, thrower.getEyeHeight() - 0.6 + look.y * spawnDistance, look.z * spawnDistance);

        this.setPos(spawnPos.x, spawnPos.y, spawnPos.z);

        if (look.y >= 0.9) {
            this.setDeltaMovement(look.x, minSpeed, look.z);
            level.playSound(null, thrower.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN_UP.get(),
                    SoundSource.PLAYERS, 1.0F, pitch);
        } else {
            this.setDeltaMovement(look.x * speed, look.y * Math.max(minSpeed, speed), look.z * speed);
            level.playSound(null, thrower.blockPosition(), SoundRegistry.KOOPA_SHELL_THROWN.get(),
                    SoundSource.PLAYERS, 1.0F, pitch);
        }

        this.hasImpulse = true;
        this.setOwner(thrower);
        this.leftOwner = false;
    }

    public void placeFrom(LivingEntity placer) {
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;
        float yaw = placer.getYRot() * Mth.DEG_TO_RAD;
        double distance = placer.getBbWidth() * 0.5 + this.getBbWidth() * 0.5 + 0.2;
        Vec3 placePos = new Vec3(placer.getX() - Mth.sin(yaw) * distance, placer.getY(), placer.getZ() + Mth.cos(yaw) * distance);

        this.releaseHold();

        if (this.level().noCollision(this, this.getDimensions(this.getPose()).makeBoundingBox(placePos)))
            this.setPos(placePos.x, placePos.y, placePos.z);
        else this.setPos(placer.getX(), placer.getY(), placer.getZ());

        this.setYRot(placer.getYRot());
        this.yBodyRot = placer.getYRot();
        this.setYHeadRot(placer.getYRot());
        this.onPlaced();
        this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_EQUIP_TURTLE.value(), SoundSource.PLAYERS, 1.0F, pitch);
    }

    private InteractionResult pickUpAsItem(Player player) {
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;

        if (!(SpawnEggItem.byId(this.getType()) instanceof SpawnEggItem spawnEggItem))
            return InteractionResult.PASS;

        ItemStack stack = ShellEntity.createShellItem(spawnEggItem, this.getBounceCount());

        if (!player.addItem(stack))
            player.drop(stack, false);

        this.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, pitch);
        this.discard();
        return InteractionResult.SUCCESS;
    }

    public void collideWithWall(Level level) {
        Vec3 motion = this.slidingMovement;
        double horizontalSpeed = motion.horizontalDistance();
        if (horizontalSpeed < 1e-6) return;

        Vec3 horizontalDir = new Vec3(motion.x, 0, motion.z);
        if (horizontalDir.lengthSqr() < 1e-6) return;

        horizontalDir = horizontalDir.normalize();
        Vec3 perpendicular = new Vec3(-horizontalDir.z, 0, horizontalDir.x);
        Vec3 rayOrigin = this.position().add(0, this.getBbHeight() * 0.6, 0);
        BlockHitResult closestHit = null;
        double closestAlong = Double.MAX_VALUE;
        double halfWidth = this.getBbWidth() * 0.5;
        double rayLength = horizontalSpeed;

        Object object = null;
        if (SableProvider.isLoaded())
            object = SableProvider.getContext(level, this);

        for (double offset : new double[]{0, -halfWidth, halfWidth}) {
            Vec3 start = rayOrigin.add(perpendicular.scale(offset));
            Vec3 end = start.add(horizontalDir.scale(rayLength));
            BlockHitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
            BlockState state = level.getBlockState(hitResult.getBlockPos());

            if (object instanceof SableProvider.SableContext context) {
                BlockPos pos = hitResult.getBlockPos();
                BlockPos base = BlockPos.containing(context.posLocal);
                BlockPos localPos = context.posEmbedded
                        .offset(pos.getX() - base.getX(),
                                pos.getY() - base.getY(),
                                pos.getZ() - base.getZ());
                state = context.accessor.getBlockState(localPos);

                if (level instanceof ServerLevel) {
                    localPos = context.posWorld
                            .offset(pos.getX() - base.getX(),
                                    pos.getY() - base.getY(),
                                    pos.getZ() - base.getZ());
                    state = context.accessor.getServerBlockState(localPos);
                }

                if (!state.isSolid()) continue;

                Vec3 hitPos = hitResult.getLocation();
                hitResult = new BlockHitResult(hitPos, hitResult.getDirection(), pos, hitResult.isInside());
            }

            if (state.is(BlockRegistry.ON_OFF_SWITCH)
                    || state.is(TagRegistry.BONKABLE_BLOCKS)
                    || state.is(TagRegistry.SMASHABLE_BLOCKS)
                    || (state.hasProperty(QuestionBlock.EMPTY) && !state.getValue(QuestionBlock.EMPTY)))
                continue;

            if (hitResult.getType() != HitResult.Type.BLOCK) continue;
            if (hitResult.getDirection().getAxis() == Direction.Axis.Y) continue;

            double along = hitResult.getLocation().subtract(start).dot(horizontalDir);

            if (along < closestAlong) {
                closestAlong = along;
                closestHit = hitResult;
            }
        }

        if (closestHit != null)
            this.bounceShell(level, closestHit);
    }

    public void bounceShell(Level level, BlockHitResult hitResult) {
        float pitch = 0.8F + this.level().random.nextFloat() * 0.3F;
        Direction direction = hitResult.getDirection();

        if (direction.getAxis() == Direction.Axis.Y)
            return;

        Vec3 motion = this.slidingMovement;
        double speed = motion.horizontalDistance();
        if (speed < 1e-6) return;
        if (level.getBlockState(hitResult.getBlockPos()).isAir()) return;

        double newX = motion.x;
        double newZ = motion.z;
        if (direction.getAxis() == Direction.Axis.X)
            newX = -motion.x;
        else if (direction.getAxis() == Direction.Axis.Z)
            newZ = -motion.z;

        Vec3 newMotion = new Vec3(newX, 0, newZ).normalize().scale(speed).add(0, this.getDeltaMovement().y, 0);

        this.setDeltaMovement(newMotion);
        this.slidingMovement = newMotion;
        this.hasImpulse = true;
        Vec3 hitPos = hitResult.getLocation();

        if (level instanceof ServerLevel server && speed > 0.25) {
            server.sendParticles(ParticleTypes.CRIT, hitPos.x, hitPos.y + this.getBbHeight() * 0.5, hitPos.z,
                    3, 0.1, 0.1, 0.1, 0.0);

            if (this.getBounceCount() != -1)
                this.setBounceCount(this.getBounceCount() + 1);
        }

        if (speed > 0.25 && this.getData(DataAttachmentRegistry.HIT_BLOCK_SOUND_COOLDOWN.get()) == 0) {
            level.playSound(null, this.blockPosition(), SoundRegistry.KOOPA_SHELL_BOUNCED.get(),
                    SoundSource.NEUTRAL, 1.0F, pitch);
            this.setData(DataAttachmentRegistry.HIT_BLOCK_SOUND_COOLDOWN.get(), 2);
        }
    }

    public void collideWithEntity() {
        AABB collisionBox = this.getBoundingBox().inflate(0.1, 0, 0.1);
        List<Entity> entities = this.level().getEntities(this, collisionBox);
        double shellSpeed = this.getDeltaMovement().horizontalDistance();
        Set<UUID> newCollisions = new HashSet<>();

        for (Entity entityHit : entities) {
            double entityHitSpeed = entityHit.getDeltaMovement().horizontalDistance();

            if (shellSpeed >= 0.1 && !this.hasPassenger(entityHit)) {
                if (entityHit instanceof VehicleEntity vehicle) {
                    vehicle.getPersistentData().putInt("marioverse:spinning_ticks", 30);

                    for (Entity rider : vehicle.getPassengers()) {
                        if (rider instanceof LivingEntity livingEntity && !livingEntity.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE)) {
                            float shellDamage = livingEntity.getType().is(this.getInstaKillEntityTag())
                                    ? livingEntity.getHealth() * 1.25F : this.getShellDamage();

                            if (this.getOwner() != null && this.leftOwner)
                                entityHit.hurt(DamageSourceRegistry.spinningShell(entityHit, this.getOwner()), shellDamage);
                            else if (this.getOwner() == null) entityHit.hurt(DamageSourceRegistry.spinningShell(entityHit, this), shellDamage);

                            if (rider.getType().is(this.getInstaKillEntityTag()))
                                this.setKillCount(this.getKillCount() + 1);

                            if (!entityHit.isAlive()) {
                                this.playDeathAnimation(this);
                                this.discard();
                            }
                        }
                    }
                }

                if (entityHit instanceof ShellEntity shell && entityHitSpeed >= 0.1 && shell.isAlive()) {
                    this.playDeathAnimation(this);
                    this.discard();
                    shell.playDeathAnimation(shell);
                    shell.discard();
                    return;
                }

                if (entityHit instanceof LivingEntity livingEntity && livingEntity.isAlive()
                        && !livingEntity.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE))
                    this.damageEntity(livingEntity, newCollisions);

                if (entityHit instanceof EnderDragonPart part && part.isAlive()
                        && !part.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE))
                    this.damageEntity(part.parentMob, newCollisions);

                if (entityHit instanceof PiranhaPlantPart part && part.isAlive()
                        && !part.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE))
                    this.damageEntity(part.parentMob, newCollisions);
            }
        }

        entityCollided.retainAll(newCollisions);
        entityCollided.addAll(newCollisions);
    }

    public void collideWithEntity(Entity entity) {
        double shellSpeed = this.getDeltaMovement().horizontalDistance();
        Set<UUID> newCollisions = new HashSet<>();
        double entityHitSpeed = entity.getDeltaMovement().horizontalDistance();

        if (shellSpeed >= 0.1 && !this.hasPassenger(entity)) {
            if (entity instanceof VehicleEntity vehicle) {
                vehicle.getPersistentData().putInt("marioverse:spinning_ticks", 30);

                for (Entity rider : vehicle.getPassengers()) {
                    if (rider instanceof LivingEntity livingEntity && !livingEntity.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE)) {
                        float shellDamage = livingEntity.getType().is(this.getInstaKillEntityTag())
                                ? livingEntity.getHealth() * 1.25F : this.getShellDamage();

                        if (this.getOwner() != null && this.leftOwner)
                            entity.hurt(DamageSourceRegistry.spinningShell(entity, this.getOwner()), shellDamage);
                        else if (this.getOwner() == null) entity.hurt(DamageSourceRegistry.spinningShell(entity, this), shellDamage);

                        if (rider.getType().is(this.getInstaKillEntityTag()))
                            this.setKillCount(this.getKillCount() + 1);

                        if (!entity.isAlive()) {
                            this.playDeathAnimation(this);
                            this.discard();
                        }
                    }
                }
            }

            if (entity instanceof ShellEntity shell && entityHitSpeed >= 0.1 && shell.isAlive()) {
                this.playDeathAnimation(this);
                this.discard();
                shell.playDeathAnimation(shell);
                shell.discard();
                return;
            }

            if (entity instanceof LivingEntity livingEntity && livingEntity.isAlive()
                    && !livingEntity.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE))
                this.damageEntity(livingEntity, newCollisions);

            if (entity instanceof EnderDragonPart part && part.isAlive()
                    && !part.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE))
                this.damageEntity(part.parentMob, newCollisions);

            if (entity instanceof PiranhaPlantPart part && part.isAlive()
                    && !part.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE))
                this.damageEntity(part.parentMob, newCollisions);
        }

        entityCollided.retainAll(newCollisions);
        entityCollided.addAll(newCollisions);
    }

    public void damageEntity(LivingEntity entityHit, Set<UUID> newCollisions) {
        Level level = this.level();
        ItemStack shield = entityHit.getUseItem();
        Vec3 toShell = this.position().subtract(entityHit.position()).normalize();
        Vec3 look = entityHit.getLookAngle().normalize();
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        double dot = toShell.dot(look);

        UUID id = entityHit.getUUID();
        newCollisions.add(id);

        if (!entityCollided.contains(id)) {
            if (entityHit.isBlocking() && dot > 0.25) {
                this.deflect(entityHit, this.getOwner(), true);
                shield.hurtAndBreak(1, entityHit, LivingEntity.getSlotForHand(entityHit.getUsedItemHand()));
                level.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                        SoundSource.NEUTRAL, 1.0F, pitch);
                level.playSound(null, this.blockPosition(), SoundRegistry.KOOPA_SHELL_BOUNCED.get(),
                        SoundSource.NEUTRAL, 1.0F, pitch);
                return;
            }

            if (entityHit instanceof Breeze) {
                this.deflect(entityHit, this.getOwner(), true);
                level.playSound(null, entityHit.blockPosition(), SoundEvents.BREEZE_DEFLECT,
                        entityHit.getSoundSource(), 1.0F, pitch);
                return;
            }

            float shellDamage = entityHit.getType().is(this.getInstaKillEntityTag())
                    ? entityHit.getHealth() * 1.25F : this.getShellDamage();

            if (this.getOwner() != null && this.leftOwner)
                entityHit.hurt(DamageSourceRegistry.spinningShell(entityHit, this.getOwner()), shellDamage);
            else if (this.getOwner() == null) entityHit.hurt(DamageSourceRegistry.spinningShell(entityHit, this), shellDamage);
            this.getDamageFromKills();

            if (this.getOwner() instanceof LivingEntity entity && this.leftOwner)
                OneUpMushroomEntity.consecutiveReward(entity, entityHit, this.getKillCount());

            if (entityHit.getType().is(this.getInstaKillEntityTag()))
                this.setKillCount(this.getKillCount() + 1);

            if (level instanceof ServerLevel serverWorld)
                serverWorld.sendParticles(ParticleTypes.CRIT, entityHit.getX(), entityHit.getY() + this.getBbHeight() / 2, entityHit.getZ(),
                        3, 0.1, 0.1, 0.1, 0.0);

            if (entityHit.isPassenger() && entityHit.getVehicle() != null) {
                Entity vehicle = entityHit.getVehicle();
                vehicle.getPersistentData().putInt("marioverse:spinning_ticks", 30);
            }

            if (!entityHit.getType().is(getInstaKillEntityTag())) {
                if ((this.getOwner() != null && this.leftOwner)
                        || this.getOwner() == null) {
                    this.playDeathAnimation(this);
                    this.discard();
                }
            }
        }
    }

    private void getDamageFromKills() {
        if (this.getBounceCount() != -1)
            this.setBounceCount(this.getBounceCount() + ConfigRegistry.KOOPA_SHELL_DAMAGE_FROM_KILLS.get());
    }

    public void playDeathAnimation(Entity entity) {
        float scale = (float) this.getAttributeValue(Attributes.SCALE);
        float heightScale = (float) this.getAttributeValue(AttributesRegistry.HEIGHT_SCALE);
        float widthScale = (float) this.getAttributeValue(AttributesRegistry.WIDTH_SCALE);
        float pitch = 0.9F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;

        if (entity.level() instanceof ServerLevel serverWorld) {
            float height = this.getBbHeight() * scale * heightScale;
            float width = this.getBbWidth() * scale * widthScale;

            if (this.getBbHeight() >= this.getBbWidth() * 3)
                width *= 2.0F;

            float scaleFactor = height * width * 1.2F;
            int numParticles = (int) (scaleFactor * 15);
            for (int i = 0; i < numParticles; ++i)
                ServerParticleUtils.spawnEntityBreakParticles(this.getShatterParticle(), serverWorld,
                        entity, height * 1.55F + 0.1F, width * 1.55F);
        }

        if (this.getDeathSound() != null)
            this.playSound(this.getDeathSound(), this.getSoundVolume(), pitch);
    }

    protected void spawnTrailParticles() {
        BlockPos posLegacy = this.getOnPosLegacy();
        BlockState state = this.level().getBlockState(posLegacy);
        float scale = (float) this.getAttributeValue(Attributes.SCALE);
        float widthScale = (float) this.getAttributeValue(AttributesRegistry.WIDTH_SCALE);

        if (!state.addRunningEffects(this.level(), posLegacy, this)) {
            if (state.getRenderShape() != RenderShape.INVISIBLE) {
                Vec3 vec3 = this.getDeltaMovement();
                BlockPos pos = this.blockPosition();
                double x = this.getX() + (this.random.nextDouble() - 0.5) * scale * widthScale;
                double z = this.getZ() + (this.random.nextDouble() - 0.5) * scale * widthScale;
                if (pos.getX() != posLegacy.getX())
                    x = Mth.clamp(x, posLegacy.getX(), posLegacy.getX() + 1.0);

                if (pos.getZ() != posLegacy.getZ())
                    z = Mth.clamp(z, posLegacy.getZ(), posLegacy.getZ() + 1.0);

                this.trailParticles(state, posLegacy, x, z, vec3);
            }
        }
    }

    public void trailParticles(BlockState state, BlockPos posLegacy, double x, double z, Vec3 vec3) {
        this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state).setPos(posLegacy), x, this.getY() + 0.1, z, vec3.x * -4.0, 1.5, vec3.z * -4.0);
    }
}
