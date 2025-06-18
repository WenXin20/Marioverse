package com.wenxin2.marioverse.entities;

import com.google.common.base.MoreObjects;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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

public class KoopaShellEntity extends Monster implements CrackableEntity, GeoEntity, TraceableEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_HIDE_FLAGS = SynchedEntityData.defineId(KoopaShellEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("move.emerge");
    public static final RawAnimation FLIP = RawAnimation.begin().thenPlayAndHold("misc.flip");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    public static final RawAnimation SPIN = RawAnimation.begin().thenLoop("move.spin");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public final Set<UUID> entityCollided = new HashSet<>();
    public Vec3 slidingDirection = new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z);
    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private boolean leftOwner;
    private boolean isSliding = false;
    private int bounceCount = 0;
    private int hideTicks = -1;
    public int emergeAnimationTicks = -1;

    public KoopaShellEntity(EntityType<? extends KoopaShellEntity> type, Level world) {
        super(type, world);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(2);
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
    protected void registerGoals() {
        this.targetSelector.addGoal(0, new HurtByTargetGoal(this).setAlertOthers(KoopaTroopaEntity.class));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(DefaultAnimations.genericIdleController(this).transitionLength(0));
        controllers.add(new AnimationController<>(this, "flip_controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("flip", FLIP));
        controllers.add(new AnimationController<>(this, "spin", 5, this::walkAnimation));
        controllers.add(new AnimationController<>(this, "emerge_controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("emerge", EMERGE));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimation(final AnimationState<E> event) {
        if (this.getDeltaMovement().horizontalDistance() > 0) {
            event.setAndContinue(SPIN);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE);
            return PlayState.CONTINUE;
        }
    }

    public boolean isHiding() {
        return hideTicks > 0;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_HIDE_FLAGS, (byte)0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("HideFlags", this.entityData.get(DATA_ID_HIDE_FLAGS));
        tag.putInt("BounceCount", this.bounceCount);
        tag.putInt("HideTicks", this.hideTicks);

        if (this.ownerUUID != null)
            tag.putUUID("Owner", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ID_HIDE_FLAGS, tag.getByte("HideFlags"));
        this.leftOwner = tag.getBoolean("LeftOwner");
        this.bounceCount = tag.getInt("BounceCount");
        this.hideTicks = tag.getInt("HideTicks");

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

        if (motion.horizontalDistance() > 0.1)
            this.spawnTrailParticles();

        if (!this.leftOwner)
            this.leftOwner = this.checkLeftOwner();

        if (this.bounceCount >= ConfigRegistry.MAX_KOOPA_SHELL_BOUNCES.get().floatValue()) {
            this.playDeathAnimation(this);
            this.discard();
        }

        if (hideTicks > 0 && this.getDeltaMovement().horizontalDistance() == 0 && this.onGround())
            hideTicks--;

        if (emergeAnimationTicks > 0)
            emergeAnimationTicks--;

        if (!this.level().isClientSide && emergeAnimationTicks == 1)
            this.spawnKoopaTroopa();

        if (hideTicks == 0 && emergeAnimationTicks == 0
                && this.getDeltaMovement().horizontalDistance() == 0 && this.onGround()) {
            this.triggerAnim("emerge_controller", "emerge");
            this.emergeAnimationTicks = 60;
        }

        if (this.isAlive()) {
            this.collideWithWall(this.level());
            this.collideWithEntity();
        }

        if (isSliding && this.isAlive()) {
            BlockPos posBelow = this.blockPosition().below();
            BlockState stateBelow = level().getBlockState(posBelow);
            float friction = stateBelow.getFriction(level(), posBelow, this);
            double slideSpeed = (friction > 0.8) ? 0.4 + friction / 1.5 : 1.0;
            Vec3 slideMotion = this.slidingDirection.normalize().scale(slideSpeed);

            if (this.getLastDamageSource() != null
                    && (this.getLastDamageSource().is(DamageTypeRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageTypeRegistry.PLAYER_STOMP))) {
                this.setDeltaMovement(Vec3.ZERO);
                this.slidingDirection = Vec3.ZERO;
            } else if (this.onGround()) {
                this.setDeltaMovement(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
                this.slidingDirection = new Vec3(slideMotion.x, this.getDeltaMovement().y, slideMotion.z);
            }
        }

        if (motion.horizontalDistance() < 0.0001) {
            isSliding = false;
        } else if (!isSliding && motion.horizontalDistance() > 0.0001) {
            this.slidingDirection = motion;
            isSliding = true;
        }
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        float soundPitch = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
        ItemStack stack = player.getItemInHand(hand);
        SpawnEggItem spawnEggItem = SpawnEggItem.byId(this.getType());

        if (this.bounceCount > 0 && player.getItemInHand(hand).is(TagRegistry.REPAIRS_KOOPA_SHELLS)
                && ConfigRegistry.REPAIR_KOOPA_SHELLS.get()) {
            stack.consume(1, player);
            this.bounceCount = Math.max(0, this.bounceCount - 25);;
            this.playSound(SoundRegistry.KOOPA_SHELL_STOMP.get(), 1.0F, soundPitch);
            return InteractionResult.SUCCESS;
        } else if (this.getDeltaMovement().horizontalDistance() < 0.1 && spawnEggItem != null) {
            player.setItemInHand(hand, new ItemStack(spawnEggItem));
            player.level().playSound(player, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS);
            this.discard();
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        Level world = this.level();
        BlockPos posBelow = this.blockPosition().below();
        BlockState stateBelow = world.getBlockState(posBelow);

        if (source.is(TagRegistry.FLIPS_KOOPA_SHELL))
            this.triggerAnim("flip_controller", "flip");

        if (source.is(TagRegistry.STOPS_KOOPA_SHELL)) {
            if (this.slidingDirection != Vec3.ZERO) {
                this.setXxa(0.0F);
                this.setSpeed(0.0F);
                this.setDeltaMovement(0, this.getDeltaMovement().y, 0);
                this.slidingDirection = new Vec3(0, this.getDeltaMovement().y, 0);
                this.isSliding = false;
            }
        }

        if (!source.is(TagRegistry.FLIPS_KOOPA_SHELL) && !source.is(TagRegistry.STOPS_KOOPA_SHELL)
                || this.slidingDirection == Vec3.ZERO) {
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
                this.isSliding = true;
                this.slidingDirection = new Vec3(movement.x, this.getDeltaMovement().y, movement.z);
                this.setOwner(source.getEntity());
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        this.playDeathAnimation(this);
        super.die(source);
    }

    public static boolean checkKoopaSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                               MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
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

    @Override
    public void restoreFrom(Entity entity) {
        super.restoreFrom(entity);
        if (entity instanceof KoopaShellEntity shell)
            this.cachedOwner = shell.cachedOwner;
    }

    @NotNull
    public SimpleParticleType getShatterParticle() {
        return ParticleRegistry.GREEN_KOOPA_SHELL_SHATTER.get();
    }

    @NotNull
    public KoopaTroopaEntity getKoopaTroopaEntity() {
        return new KoopaTroopaEntity(EntityRegistry.GREEN_KOOPA_TROOPA.get(), this.level());
    }

    public TagKey<EntityType<?>> getInstakillEntityTag() {
        return TagRegistry.GREEN_KOOPA_SHELL_CAN_INSTAKILL;
    }

    public float getShellDamage() {
        return ConfigRegistry.GREEN_KOOPA_SHELL_DAMAGE.get().floatValue();
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
            this.setOwner(entity);
            this.onDeflection(entity, deflect);
        }
        return true;
    }

    protected void onDeflection(@Nullable Entity entity, boolean deflect) {
        Vec3 motion = this.slidingDirection;
        this.setDeltaMovement(new Vec3(-motion.x, motion.y, -motion.z));
        this.slidingDirection = new Vec3(-motion.x, motion.y, -motion.z);
        if (this.bounceCount != -1)
            this.bounceCount++;
    }

    public void setHideTicks(int hideTicks) {
        this.hideTicks = hideTicks;
    }

    @Override
    public Crackiness.Level getCrackiness() {
        return Crackiness.WOLF_ARMOR.byFraction(this.bounceCount / ConfigRegistry.MAX_KOOPA_SHELL_BOUNCES.get().floatValue());
    }

    public void collideWithWall(Level world) {
        if (!world.isClientSide) {
            AABB bb = this.getBoundingBox();
            double maxStep = this.maxUpStep();
            double forwardDistance = 0.5;
            double stepIncrement = 0.1;

            outer:
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                Vec3 forward = new Vec3(dir.getStepX() * forwardDistance, 0, dir.getStepZ() * forwardDistance);

                for (double dy = 0; dy <= maxStep; dy += stepIncrement) {
                    Vec3 offset = forward.add(0, dy, 0);
                    AABB movedBox = bb.move(offset);

                    if (world.noCollision(this, movedBox))
                        continue outer;
                }

                this.bounceShell(world, dir);
                break;
            }
        }
    }

    public void bounceShell(Level world, Direction dir) {
        Crackiness.Level crackinessLevel = this.getCrackiness();
        Vec3 motion = this.slidingDirection;
        double newX = motion.x;
        double newZ = motion.z;

        if (dir.getAxis() == Direction.Axis.X)
            newX = -motion.x;
        if (dir.getAxis() == Direction.Axis.Z)
            newZ = -motion.z;

        this.setDeltaMovement(new Vec3(newX, motion.y, newZ));
        this.slidingDirection = new Vec3(newX, motion.y, newZ);

        Vec3 hitPos = this.position().add(Vec3.atLowerCornerOf(dir.getNormal()).scale(0.4));
        if (world instanceof ServerLevel serverWorld && this.getDeltaMovement().horizontalDistance() > 0.25) {
            serverWorld.sendParticles(ParticleTypes.CRIT, hitPos.x, hitPos.y + this.getBbHeight() / 2, hitPos.z,
                    3, 0.1, 0.1, 0.1, 0.0);
            if (this.bounceCount != -1)
                this.bounceCount++;
        }

        if (this.getDeltaMovement().horizontalDistance() > 0.25)
            world.playSound(null, this.blockPosition(), SoundRegistry.KOOPA_SHELL_BOUNCED.get(), SoundSource.NEUTRAL, 1.0F, 1.0F);

        if (this.getCrackiness() != crackinessLevel)
            this.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 1.0F, 1.0F); // TODO
    }

    public void collideWithEntity() {
        AABB collisionBox = this.getBoundingBox().inflate(0.01, 0, 0.01);
        List<Entity> entities = this.level().getEntities(this, collisionBox);
        double speed = this.getDeltaMovement().horizontalDistance();
        Set<UUID> newCollisions = new HashSet<>();

        for (Entity entityHit : entities) {
            if (speed >= 0.1 && !this.hasPassenger(entityHit)) {
                if (entityHit instanceof VehicleEntity vehicle) {
                    vehicle.getPersistentData().putInt("marioverse:spinning_ticks", 30);

                    for (Entity rider : vehicle.getPassengers()) {
                        if (rider instanceof LivingEntity livingEntity && !livingEntity.getType().is(TagRegistry.KOOPA_SHELL_CANNOT_DAMAGE)) {
                            float shellDamage = livingEntity.getType().is(this.getInstakillEntityTag())
                                    ? livingEntity.getHealth() * 1.25F : this.getShellDamage();

                            if (this.getOwner() != null)
                                entityHit.hurt(DamageTypeRegistry.spinningShell(entityHit, this.getOwner()), shellDamage);
                            else entityHit.hurt(DamageTypeRegistry.spinningShell(entityHit, this), shellDamage);

                            if (!entityHit.isAlive()) {
                                this.playDeathAnimation(this);
                                this.discard();
                            }
                        }
                    }
                }

                if (entityHit instanceof KoopaShellEntity koopaShell) {
                    this.playDeathAnimation(this);
                    this.discard();
                    koopaShell.playDeathAnimation(koopaShell);
                    koopaShell.discard();
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

    public void damageEntity(LivingEntity entity, Set<UUID> newCollisions) {
        Level world = this.level();
        ItemStack shield = entity.getUseItem();
        Vec3 toShell = this.position().subtract(entity.position()).normalize();
        Vec3 look = entity.getLookAngle().normalize();
        double dot = toShell.dot(look);

        UUID id = entity.getUUID();
        newCollisions.add(id);

        if (!entityCollided.contains(id)) {
            if (entity.isBlocking() && dot > 0.25) {
                this.deflect(entity, this.getOwner(), true);
                shield.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
                world.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK,
                        SoundSource.NEUTRAL, 1.0F, 1.0F);
                world.playSound(null, this.blockPosition(), SoundRegistry.KOOPA_SHELL_BOUNCED.get(),
                        SoundSource.NEUTRAL, 1.0F, 1.0F);
                return;
            }

            if (entity instanceof Breeze) {
                this.deflect(entity, this.getOwner(), true);
                world.playSound(null, entity.blockPosition(), SoundEvents.BREEZE_DEFLECT,
                        entity.getSoundSource(), 1.0F, 1.0F);
                return;
            }

            float shellDamage = entity.getType().is(this.getInstakillEntityTag())
                    ? entity.getHealth() * 1.25F : this.getShellDamage();

            if (this.getOwner() != null)
                entity.hurt(DamageTypeRegistry.spinningShell(entity, this.getOwner()), shellDamage);
            else entity.hurt(DamageTypeRegistry.spinningShell(entity, this), shellDamage);

            if (world instanceof ServerLevel serverWorld)
                serverWorld.sendParticles(ParticleTypes.CRIT, entity.getX(), entity.getY() + this.getBbHeight() / 2, entity.getZ(),
                        3, 0.1, 0.1, 0.1, 0.0);

            if (entity.isPassenger() && entity.getVehicle() != null) {
                Entity vehicle = entity.getVehicle();
                vehicle.getPersistentData().putInt("marioverse:spinning_ticks", 30);
            }

            this.playDeathAnimation(this);
            this.discard();
        }
    }

    private void spawnKoopaTroopa() {
        KoopaTroopaEntity entity = this.getKoopaTroopaEntity();

        entity.setPos(this.getX(), this.getY(), this.getZ());
        entity.setYRot(this.getYRot());
        entity.setXRot(this.getXRot());
        entity.yBodyRot = this.yBodyRot;
        entity.setYHeadRot(this.getYHeadRot());
        entity.setHealth(this.getHealth());
        entity.setNoAi(this.isNoAi());

        if (this instanceof AbilitiesHandler handler && entity instanceof AbilitiesHandler entityHandler) {
            entityHandler.mv$setMushroom(handler.mv$hasMushroom());
            entityHandler.mv$setMegaMushroom(handler.mv$hasMegaMushroom());
            entityHandler.mv$setFireFlower(handler.mv$hasFireFlower());
            entityHandler.mv$setIceFlower(handler.mv$hasIceFlower());
            entityHandler.mv$setSuperStar(handler.mv$hasSuperStar());
            entityHandler.mv$setSuperStarCooldown(handler.mv$getSuperStarCooldown());
        }

        this.copyAttributeWithModifiers(entity, Attributes.SAFE_FALL_DISTANCE);
        this.copyAttributeWithModifiers(entity, Attributes.SCALE);
        this.copyAttributeWithModifiers(entity, AttributesRegistry.HEIGHT_SCALE);
        this.copyAttributeWithModifiers(entity, AttributesRegistry.WIDTH_SCALE);

        for (EquipmentSlot slot : EquipmentSlot.values())
            entity.setItemSlot(slot, this.getItemBySlot(slot).copy());

        AccessoriesCapability capability = AccessoriesCapability.get(this);
        if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                && !this.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
            String[] slotTypes = {"costume_hat", "costume_shirt", "costume_pants", "costume_shoes"};
            for (String slotType : slotTypes) {
                AccessoriesContainer container = capability.getContainer(SlotTypeLoader.getSlotType(this, slotType));
                AccessoriesContainer containerEntity = capability.getContainer(SlotTypeLoader.getSlotType(entity, slotType));
                if (container != null) {
                    ItemStack stack = container.getAccessories().getItem(0);
                    if (containerEntity != null)
                        containerEntity.getAccessories().setItem(0, stack);
                }
            }
        }

        this.level().addFreshEntity(entity);
        this.discard();
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

        if (this.getDeathSound() != null)
            this.playSound(this.getDeathSound(), this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
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