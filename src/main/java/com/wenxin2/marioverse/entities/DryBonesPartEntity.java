package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.particles.EntityShatterParticleOption;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ClearPipeNetwork;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class DryBonesPartEntity extends Monster implements GeoEntity, HoldableEntity, TraceableEntity {
    private static final EntityDataAccessor<Integer> DATA_HELD_SLOT = SynchedEntityData.defineId(DryBonesPartEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_HOLDER_ID = SynchedEntityData.defineId(DryBonesPartEntity.class, EntityDataSerializers.INT);
    public static final RawAnimation SHAKE = RawAnimation.begin().thenLoop("move.shake");
    private static final double SLIDE_SPEED = 0.7;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private boolean leftOwner;
    public Vec3 slidingMovement = Vec3.ZERO;
    @Nullable private UUID throwerUUID;
    @Nullable private UUID angerTargetUUID;
    private int holdTicks;

    public DryBonesPartEntity(EntityType<? extends DryBonesPartEntity> type, Level world) {
        super(type, world);
        this.xpReward = 1;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.DRY_BONES_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.DRY_BONES_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {}

    @Nullable
    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ItemRegistry.DRY_BONES_SPAWN_EGG.get());
    }

    @NotNull
    public SimpleParticleType getShatterParticle() {
        return ParticleTypes.POOF;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "shake", 5, this::shakeAnimation));
    }

    protected <E extends GeoAnimatable> PlayState shakeAnimation(final AnimationState<E> event) {
        int deathDuration = this.getData(DataAttachmentRegistry.DEATH_DURATION);
        int reassembleDuration = this.getData(DataAttachmentRegistry.REASSEMBLE_DURATION);

        if (!this.isNoAi() && this.getOwnerUUID() != null
                && deathDuration <= 1 && reassembleDuration < ConfigRegistry.DRY_BONES_REASSEMBLE_DURATION.get()) {
            event.setAndContinue(SHAKE);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_HELD_SLOT, -1);
        builder.define(DATA_HOLDER_ID, -1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.ownerUUID != null)
            tag.putUUID("Owner", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
        if (this.angerTargetUUID != null)
            tag.putUUID("AngerTarget", this.angerTargetUUID);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.leftOwner = tag.getBoolean("LeftOwner");

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }

        if (tag.hasUUID("AngerTarget"))
            this.angerTargetUUID = tag.getUUID("AngerTarget");
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
        this.tickHolding();
        int deathDuration = this.getData(DataAttachmentRegistry.DEATH_DURATION);
        int reassembleDuration = this.getData(DataAttachmentRegistry.REASSEMBLE_DURATION);

        if (!this.leftOwner)
            this.leftOwner = this.checkLeftOwner();

        if (deathDuration > 0)
            this.setData(DataAttachmentRegistry.DEATH_DURATION, deathDuration - 1);

        if (!this.level().isClientSide && !this.isNoAi() && deathDuration == 0)
            this.reassembleParts(reassembleDuration);

        if (!this.level().isClientSide && !this.isRemoved()) {
            boolean sliding = this.isMovingShell() && !this.isHeld() && this.isAlive();

            if (sliding != this.isSliding())
                this.setSliding(sliding);
            if (sliding)
                this.collideWhileSliding();
        }
    }

    @Override
    protected void tickDeath() {
        if (!this.getPartType().equals("shell") && !this.getPartType().equals("head")) {
            super.tickDeath();
            return;
        }

        this.deathTime++;

        if (this.deathTime >= 20 && this.level() instanceof ServerLevel serverLevel && !this.isRemoved()) {
            this.spawnShards(serverLevel);
            this.crumbleParts(serverLevel);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void travel(Vec3 vec3) {
        if (this.isHeld())
            return;

        if (!this.isMovingShell() || !this.isControlledByLocalInstance()) {
            super.travel(vec3);
            return;
        }

        if (ClearPipeNetwork.isTravelingInPipe(this)) {
            this.slidingMovement = HoldableEntity.travelInClearPipe(this, this.slidingMovement, SLIDE_SPEED);
            return;
        }

        Vec3 motion = this.getDeltaMovement();
        boolean inFluid = !this.level().getFluidState(this.blockPosition()).isEmpty();
        double fluidScale = inFluid ? 0.5 : 1.0;

        if (this.onGround())
            this.slidingMovement = new Vec3(this.slidingMovement.x, 0.0, this.slidingMovement.z).normalize().scale(SLIDE_SPEED);

        double horizontalX = this.slidingMovement.x * fluidScale;
        double horizontalZ = this.slidingMovement.z * fluidScale;
        double verticalY = inFluid ? motion.y * 0.8 - this.getGravity() * 0.25 : (motion.y - this.getGravity()) * 0.98;
        Vec3 intended = new Vec3(horizontalX, verticalY, horizontalZ);

        this.setDeltaMovement(intended);
        this.move(MoverType.SELF, intended);
        Vec3 result = this.getDeltaMovement();

        if (this.horizontalCollision) {
            boolean blockedX = Math.abs(horizontalX) > 1.0E-5 && result.x == 0.0;
            boolean blockedZ = Math.abs(horizontalZ) > 1.0E-5 && result.z == 0.0;

            if (blockedX || blockedZ)
                this.shatter(new Vec3(blockedX ? -Math.signum(horizontalX) : 0.0, 0.0,
                        blockedZ ? -Math.signum(horizontalZ) : 0.0));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isHeld() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return false;
        return super.hurt(source, amount);
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
    public void push(Entity entity) {
        if (this.isHeld())
            return;

        super.push(entity);

        if (!this.level().isClientSide && !this.noPhysics
                && !(entity instanceof DryBonesPartEntity)) {
            this.setYRot(this.getYRot() + 8.0F);
            this.yRotO = this.getYRot() + 8.0F;
            this.setYHeadRot(this.getYRot() + 8.0F);
        }
    }


    @Override
    public void doPush(Entity entity) {
        if (!this.isHeld() && !(entity instanceof DryBonesPartEntity))
            super.doPush(entity);
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || !this.getPartType().equals("shell") || this.isHeld()
                || this.isMovingShell() || !player.getType().is(TagRegistry.CAN_PICKUP_AND_THROW_SHELLS)
                || !player.getMainHandItem().isEmpty() || HoldableEntity.getHeldEntity(player) != null)
            return super.mobInteract(player, hand);

        if (!this.level().isClientSide)
            this.startHolding(player);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    @Override
    protected void dropEquipment() {
        if (this.getPartType().equals("left_arm") || this.getPartType().equals("left_leg"))
            return;
        super.dropEquipment();
    }

    @Override
    public @NotNull AABB makeBoundingBox() {
        return super.makeBoundingBox();
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return false;
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

    public void setOwner(@Nullable Entity ownerEntity) {
        if (ownerEntity != null) {
            this.ownerUUID = ownerEntity.getUUID();
            this.cachedOwner = ownerEntity;
        }
    }

    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Nullable
    public Entity getThrower() {
        if (this.throwerUUID != null && this.level() instanceof ServerLevel serverLevel)
            return serverLevel.getEntity(this.throwerUUID);
        return null;
    }

    public EntityShatterParticleOption getShatterParticleOption() {
        String path = BuiltInRegistries.ENTITY_TYPE.getKey(this.getType()).getPath();
        String texture = this.getData(DataAttachmentRegistry.DEATH_DURATION) == 0 ? "dry_bones" : "dry_bones_dead";

        return new EntityShatterParticleOption(
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "geo/entity/dry_bones/" + path + ".geo.json"),
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "textures/entity/dry_bones/" + texture + ".png"), "");
    }

    public boolean isThrown() {
        return this.throwerUUID != null && !this.isHeld();
    }

    @Override
    public int getHolderId() {
        return this.entityData.get(DATA_HOLDER_ID);
    }

    @Override
    public int getHeldSlot() {
        return this.entityData.get(DATA_HELD_SLOT);
    }

    @Override
    public int getHoldTicks() {
        return this.holdTicks;
    }

    public boolean isMovingShell() {
        return this.slidingMovement.horizontalDistanceSqr() > 1.0E-4;
    }

    public void setSliding(boolean isSliding) {
        this.setData(DataAttachmentRegistry.IS_SLIDING.get(), isSliding);
    }

    public boolean isSliding() {
        return this.getData(DataAttachmentRegistry.IS_SLIDING.get());
    }

    @Override
    public void startHolding(LivingEntity holder) {
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;
        Vec3 holdPos = this.getHoldPosition(holder, 1.0F);

        this.setDeltaMovement(Vec3.ZERO);
        this.slidingMovement = Vec3.ZERO;
        this.setSliding(false);
        this.holdTicks = 0;
        this.throwerUUID = null;
        this.entityData.set(DATA_HOLDER_ID, holder.getId());
        this.entityData.set(DATA_HELD_SLOT, holder instanceof Player player ? player.getInventory().selected : -1);
        this.angerTargetUUID = holder.getUUID();
        this.setPos(holdPos.x, holdPos.y, holdPos.z);
        this.level().playSound(null, holder.blockPosition(), SoundEvents.ITEM_PICKUP, holder.getSoundSource(), 1.0F, pitch);
    }

    @Override
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

    @Override
    public boolean canEnterClearPipes() {
        return this.isThrown() && this.isMovingShell() && this.isAlive();
    }

    @Override
    public void throwFrom(LivingEntity thrower) {
        if (this.isHeld())
            this.releaseHold();

        this.slidingMovement = HoldableEntity.launch(this, thrower);
        this.throwerUUID = thrower.getUUID();
    }

    @Override
    public boolean placeAt(LivingEntity placer, BlockPos pos) {
        Vec3 placePos = HoldableEntity.getPlacePosition(this, placer, pos);

        if (placePos == null)
            return false;

        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;
        float placerYaw = HoldableEntity.getHolderYaw(placer, 1.0F);

        this.releaseHold();
        this.setPos(placePos.x, placePos.y, placePos.z);
        this.setYRot(placerYaw);
        this.yBodyRot = placerYaw;
        this.setYHeadRot(placerYaw);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_EQUIP_TURTLE.value(), SoundSource.PLAYERS, 1.0F, pitch);
        return true;
    }

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof DryBonesPartEntity shell)
            this.cachedOwner = shell.cachedOwner;
    }

    protected boolean ownedBy(Entity entity) {
        return entity.getUUID().equals(this.ownerUUID);
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

    @NotNull
    public DryBonesEntity getDryBonesEntity() {
        return new DryBonesEntity(EntityRegistry.DRY_BONES.get(), this.level());
    }

    public void setPartType(String type) {
        this.setData(DataAttachmentRegistry.TYPE, type.toLowerCase());
    }

    public String getPartType() {
        return this.getData(DataAttachmentRegistry.TYPE).toLowerCase();
    }

    public void reassembleParts(int reassembleDuration) {
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;

        if (reassembleDuration < ConfigRegistry.DRY_BONES_REASSEMBLE_DURATION.get())
            this.setData(DataAttachmentRegistry.REASSEMBLE_DURATION, reassembleDuration + 1);
        if (reassembleDuration >= ConfigRegistry.DRY_BONES_REASSEMBLE_DURATION.get()) {
            this.setData(DataAttachmentRegistry.DEATH_DURATION, -1);
            this.noPhysics = false;
            this.setNoGravity(false);
            return;
        }

        if (this.getOwnerUUID() != null) {
            List<DryBonesPartEntity> parts = this.level().getEntitiesOfClass(DryBonesPartEntity.class,
                    this.getBoundingBox().inflate(32.0D),
                    partEntity -> partEntity.getOwnerUUID() != null
                            && this.getOwnerUUID().equals(partEntity.getOwnerUUID()));

            DryBonesPartEntity shell = null;
            DryBonesPartEntity head = null;

            for (DryBonesPartEntity part : parts) {
                if (part.getPartType().equals("shell")) shell = part;
                if (part.getPartType().equals("head")) head = part;

                if (!this.getPartType().equals("shell")) {
                    this.noPhysics = true;
                    this.setNoGravity(true);
                }
            }

            if (shell != null && head != null) {
                boolean allClose = true;

                for (DryBonesPartEntity part : parts) {
                    if (part != shell) {
                        Vec3 dir = shell.position().subtract(part.position());
                        double dist = dir.length();
                        double speed = 0.02D;
                        Vec3 motion = dir.normalize().scale(speed);

                        if (dist > 0.1D)
                            part.setDeltaMovement(part.getDeltaMovement().scale(0.9D).add(motion));

                        if (dist > 0.25D)
                            allClose = false;
                    }
                }

                if (allClose) {
                    this.spawnDryBones(parts, shell.getHolder() != null ? shell.getHolder() : shell.getAngerTarget());
                    if (this.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnParticlesOnEntityRandomly(this.getShatterParticle(), serverWorld,
                                this, 0.0, 15);
                    this.playSound(SoundRegistry.DRY_BONES_REASSEMBLE.get(), 1.0F, pitch);
                    for (DryBonesPartEntity part : parts)
                        part.discard();
                }
            }
        }
    }

    private void spawnDryBones(List<DryBonesPartEntity> parts, @Nullable LivingEntity target) {
        if (parts.isEmpty()) return;

        DryBonesEntity entity = parts.getFirst().getDryBonesEntity();
        DryBonesPartEntity partSource = parts.getFirst();

        entity.setPos(partSource.getX(), partSource.getY() + 0.15, partSource.getZ());
        entity.setYRot(partSource.getYRot());
        entity.setXRot(partSource.getXRot());
        entity.yBodyRot = partSource.yBodyRot;
        entity.setYHeadRot(partSource.getYHeadRot());
        entity.setNoAi(partSource.isNoAi());
        entity.setInvulnerable(partSource.isInvulnerable());
        entity.setCustomName(partSource.getCustomName());

        if (partSource.isPersistenceRequired())
            entity.setPersistenceRequired();

        if (partSource.getOwner() != null)
            entity.setUUID(partSource.getOwner().getUUID());

        entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, partSource.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM));
        entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, partSource.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER));
        entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, partSource.getData(DataAttachmentRegistry.HAS_ICE_FLOWER));
        entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, partSource.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));
        entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, partSource.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM));
        entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, partSource.getData(DataAttachmentRegistry.HAS_SUPER_STAR));
        entity.setData(DataAttachmentRegistry.SUPER_STAR_DURATION, partSource.getData(DataAttachmentRegistry.SUPER_STAR_DURATION));

        partSource.copyAttributeWithModifiers(entity, Attributes.MAX_HEALTH);
        partSource.copyAttributeWithModifiers(entity, Attributes.SAFE_FALL_DISTANCE);
        partSource.copyAttributeWithModifiers(entity, Attributes.SCALE);
        partSource.copyAttributeWithModifiers(entity, AttributesRegistry.EYE_HEIGHT_SCALE);
        partSource.copyAttributeWithModifiers(entity, AttributesRegistry.HEIGHT_SCALE);
        partSource.copyAttributeWithModifiers(entity, AttributesRegistry.WIDTH_SCALE);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            for (DryBonesPartEntity part : parts) {
                ItemStack partStack = part.getItemBySlot(slot);
                if (!partStack.isEmpty()) {
                    ItemStack existing = entity.getItemBySlot(slot);
                    if (existing.isEmpty()) {
                        entity.setItemSlot(slot, partStack.copy());
                        break;
                    }
                }
            }
        }

        if (ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                && !partSource.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
            Optional<ICuriosItemHandler> entityCuriosInventory = CuriosApi.getCuriosInventory(entity);
            if (entityCuriosInventory.isPresent()) {
                Map<String, ICurioStacksHandler> entityCurios = entityCuriosInventory.get().getCurios();
                String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};

                for (String slotType : slotTypes) {
                    ICurioStacksHandler slotEntity = entityCurios.get(slotType);
                    if (slotEntity == null)
                        continue;

                    for (DryBonesPartEntity part : parts) {
                        Optional<ICuriosItemHandler> partCuriosInventory = CuriosApi.getCuriosInventory(part);
                        if (partCuriosInventory.isEmpty())
                            continue;

                        Map<String, ICurioStacksHandler> partCurios = partCuriosInventory.get().getCurios();
                        ICurioStacksHandler slotPart = partCurios.get(slotType);
                        if (slotPart == null)
                            continue;

                        ItemStack partStack = slotPart.getStacks().getStackInSlot(0);
                        ItemStack existing = slotEntity.getStacks().getStackInSlot(0);

                        if (!partStack.isEmpty() && existing.isEmpty()) {
                            slotEntity.getStacks().setStackInSlot(0, partStack.copy());
                            break;
                        }
                    }
                }
            }
        }

        partSource.level().addFreshEntity(entity);

        if (target != null && target.isAlive())
            entity.setTarget(target);
    }

    @Nullable
    private LivingEntity getAngerTarget() {
        if (this.angerTargetUUID != null && this.level() instanceof ServerLevel serverLevel
                && serverLevel.getEntity(this.angerTargetUUID) instanceof LivingEntity target)
            return target;
        return null;
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
        float holderYaw = HoldableEntity.getHolderYaw(holder, 1.0F);

        this.holdTicks++;
        this.setDeltaMovement(Vec3.ZERO);
        this.setPos(holdPos.x, holdPos.y, holdPos.z);
        this.setYRot(holderYaw);
        this.yBodyRot = holderYaw;
        this.setYHeadRot(holderYaw);

        if (!this.level().isClientSide && this.shouldReleaseHold(holder))
            this.releaseHold();
    }

    private void collideWhileSliding() {
        Entity thrower = this.getThrower();

        for (Entity entity : this.level().getEntities(this, this.getBoundingBox().inflate(0.1, 0.0, 0.1))) {
            LivingEntity target = entity instanceof EnderDragonPart part ? part.parentMob
                    : entity instanceof PiranhaPlantPart part ? part.parentMob
                    : entity instanceof LivingEntity living ? living : null;

            if (target == null || target == thrower || target instanceof DryBonesPartEntity || !target.isAlive()
                    || target.isSpectator() || target.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE))
                continue;

            this.hitEntity(target, thrower);
            return;
        }
    }

    private void hitEntity(LivingEntity target, @Nullable Entity thrower) {
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;
        Vec3 away = this.slidingMovement.normalize().reverse();
        Vec3 toShell = this.position().subtract(target.position()).normalize();

        if (target.isBlocking() && toShell.dot(target.getLookAngle().normalize()) > 0.25) {
            target.getUseItem().hurtAndBreak(1, target, LivingEntity.getSlotForHand(target.getUsedItemHand()));
            this.level().playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.NEUTRAL, 1.0F, pitch);
            this.shatter(away);
            return;
        }

        boolean instaKill = target.getType().is(TagRegistry.GREEN_KOOPA_SHELL_CAN_INSTAKILL);
        float damage = instaKill ? target.getHealth() * 1.25F : ConfigRegistry.DRY_BONES_SHELL_DAMAGE.get().floatValue();

        target.hurt(DamageSourceRegistry.spinningShell(target, thrower != null ? thrower : this), damage);

        if (this.level() instanceof ServerLevel serverLevel)
            serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + this.getBbHeight() / 2, target.getZ(),
                    3, 0.1, 0.1, 0.1, 0.0);

        this.shatter(away);
    }

    private void shatter(Vec3 normal) {
        if (!(this.level() instanceof ServerLevel serverLevel) || this.isRemoved())
            return;

        float pitch = 0.9F + this.random.nextFloat() * 0.2F;
        Vec3 direction = normal.normalize();
        Vec3 impact = this.position().add(direction.scale(-this.getBbWidth() * 0.5)).add(0.0, this.getBbHeight() * 0.5, 0.0);

        ServerParticleUtils.spawnSplashParticles(this.getShatterParticleOption(), serverLevel, impact, direction, 30);

        this.level().playSound(null, this.blockPosition(), SoundEvents.BONE_BLOCK_BREAK, SoundSource.NEUTRAL, 1.0F, pitch);
        this.level().playSound(null, this.blockPosition(), SoundRegistry.DRY_BONES_DEATH.get(), SoundSource.HOSTILE, 1.0F, pitch);
        this.dropAllDeathLoot(serverLevel, this.damageSources().generic());
        this.crumbleParts(serverLevel);
        this.discard();
    }

    private void crumbleParts(ServerLevel serverLevel) {
        if (this.ownerUUID == null)
            return;

        List<DryBonesPartEntity> parts = this.level().getEntitiesOfClass(DryBonesPartEntity.class,
                this.getBoundingBox().inflate(32.0), part -> part != this && this.ownerUUID.equals(part.getOwnerUUID()));

        for (DryBonesPartEntity part : parts) {
            part.spawnShards(serverLevel);
            part.dropAllDeathLoot(serverLevel, part.damageSources().generic());
            part.discard();
        }
    }

    private void spawnShards(ServerLevel serverLevel) {
        EntityShatterParticleOption particle = this.getShatterParticleOption();
        int amount = Math.max(8, (int) (this.getBbWidth() * this.getBbHeight() * 40));

        for (int i = 0; i < amount; i++)
            ServerParticleUtils.spawnEntityBurstParticles(particle, serverLevel, this, this.getBbHeight(), this.getBbWidth());
    }

    private void copyAttributeWithModifiers(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance fromAttr = this.getAttribute(attribute);
        AttributeInstance toAttr = entity.getAttribute(attribute);

        if (fromAttr != null && toAttr != null) {
            toAttr.setBaseValue(fromAttr.getBaseValue());
            for (AttributeModifier modifier : fromAttr.getModifiers())
                toAttr.addPermanentModifier(modifier);
        }
    }
}