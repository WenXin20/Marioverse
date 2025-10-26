package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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

public class DryBonesPartEntity extends Monster implements GeoEntity, TraceableEntity {
    public static final RawAnimation SHAKE = RawAnimation.begin().thenLoop("move.shake");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<String> PART_TYPE =
            SynchedEntityData.defineId(DryBonesPartEntity.class, EntityDataSerializers.STRING);

    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private boolean leftOwner;
    private String type;

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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "shake", 5, this::shakeAnimation));
    }

    protected <E extends GeoAnimatable> PlayState shakeAnimation(final AnimationState<E> event) {
        int reattachmentCountdown = this.getData(DataAttachmentRegistry.REATTACHMENT_COUNTDOWN);
        int failTimer = this.getData(DataAttachmentRegistry.FAIL_TIMER);

        if (!this.isNoAi() && this.getOwnerUUID() != null
                && reattachmentCountdown <= 1 && failTimer < 300) {
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
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        if (this.ownerUUID != null)
            tag.putUUID("Owner", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
        if (this.type != null)
            tag.putString("Type", this.type);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.leftOwner = tag.getBoolean("LeftOwner");
        this.type = tag.getString("Type");

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PART_TYPE, "shell");
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
        int reattachmentCountdown = this.getData(DataAttachmentRegistry.REATTACHMENT_COUNTDOWN);
        int failTimer = this.getData(DataAttachmentRegistry.FAIL_TIMER);

        if (!this.leftOwner)
            this.leftOwner = this.checkLeftOwner();

        if (reattachmentCountdown > 0)
            this.setData(DataAttachmentRegistry.REATTACHMENT_COUNTDOWN, reattachmentCountdown - 1);

        if (!this.level().isClientSide && !this.isNoAi() && reattachmentCountdown == 0) {
            if (failTimer < 300) // TODO: config
                this.setData(DataAttachmentRegistry.FAIL_TIMER, failTimer + 1);
            if (failTimer >= 300) {
                this.setData(DataAttachmentRegistry.REATTACHMENT_COUNTDOWN, -1);
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
                    if (part.getPartType() == PartType.SHELL) shell = part;
                    if (part.getPartType() == PartType.HEAD) head = part;

                    if (this.getPartType() != PartType.SHELL) {
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
                        this.spawnDryBones(parts);
                        this.playDeathAnimation(this);
                        this.playSound(SoundRegistry.DRY_BONES_REASSEMBLE.get());
                        for (DryBonesPartEntity part : parts)
                            part.discard();
                    }
                }
            }
        }
    }

    @NotNull // TODO: Remove
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.canTakeItem(player.getItemInHand(hand))) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            return InteractionResult.SUCCESS;
        } else return super.mobInteract(player, hand);
    }

    @Override
    public boolean canTakeItem(ItemStack stack) {
        EquipmentSlot equipmentslot = this.getEquipmentSlotForItem(stack);
        return this.getItemBySlot(equipmentslot).isEmpty();
    }

    @Override
    public void push(Entity entity) {
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
        if (!(entity instanceof DryBonesPartEntity))
            super.doPush(entity);
    }

    @Override
    public void die(DamageSource source) {
        this.playDeathAnimation(this);
        super.die(source);
    }

    @Override
    protected void dropEquipment() {
        if (this.getPartType() == PartType.LEFT_ARM || this.getPartType() == PartType.LEFT_LEG)
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

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof DryBonesPartEntity shell)
            this.cachedOwner = shell.cachedOwner;
    }

    @NotNull
    public SimpleParticleType getShatterParticle() {
        return ParticleTypes.POOF;
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

    public enum PartType {
        HEAD,
        SHELL,
        LEFT_ARM,
        LEFT_LEG,
        RIGHT_ARM,
        RIGHT_LEG,
        TAIL
    }

    public void setPartType(PartType type) {
        this.entityData.set(PART_TYPE, type.name().toLowerCase());
    }

    public PartType getPartType() {
        return PartType.valueOf(this.entityData.get(PART_TYPE).toUpperCase());
    }

    private void spawnDryBones(List<DryBonesPartEntity> parts) {
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

        if (partSource instanceof AbilitiesHandler handler && entity instanceof AbilitiesHandler entityHandler) {
            entityHandler.mv$setSuperMushroom(handler.mv$hasSuperMushroom());
            entityHandler.mv$setMegaMushroom(handler.mv$hasMegaMushroom());
            entityHandler.mv$setFireFlower(handler.mv$hasFireFlower());
            entityHandler.mv$setIceFlower(handler.mv$hasIceFlower());
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, partSource.getData(DataAttachmentRegistry.HAS_SUPER_STAR));
            entity.setData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN, partSource.getData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN));
        }

        partSource.copyAttributeWithModifiers(entity, Attributes.SAFE_FALL_DISTANCE);
        partSource.copyAttributeWithModifiers(entity, Attributes.SCALE);
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
            AccessoriesCapability entityCap = AccessoriesCapability.get(entity);
            if (entityCap != null) {
                String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};

                for (String slotType : slotTypes) {
                    AccessoriesContainer containerEntity = entityCap.getContainer(SlotTypeLoader.getSlotType(entity, slotType));
                    if (containerEntity == null) continue;

                    for (DryBonesPartEntity part : parts) {
                        AccessoriesCapability partCap = AccessoriesCapability.get(part);
                        if (partCap == null) continue;

                        AccessoriesContainer containerPart = partCap.getContainer(SlotTypeLoader.getSlotType(part, slotType));
                        if (containerPart == null) continue;

                        ItemStack partStack = containerPart.getAccessories().getItem(0);
                        ItemStack existing = containerEntity.getAccessories().getItem(0);

                        if (!partStack.isEmpty() && existing.isEmpty()) {
                            containerEntity.getAccessories().setItem(0, partStack.copy());
                            break;
                        }
                    }
                }
            }
        }

        partSource.level().addFreshEntity(entity);
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

    public void playDeathAnimation(Entity entity) {
        float scale = (float) this.getAttributeValue(Attributes.SCALE);
        float heightScale = (float) this.getAttributeValue(AttributesRegistry.HEIGHT_SCALE);
        float widthScale = (float) this.getAttributeValue(AttributesRegistry.WIDTH_SCALE);

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
    }
}