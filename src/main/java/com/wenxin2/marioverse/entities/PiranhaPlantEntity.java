package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.PartEntity;
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

public class PiranhaPlantEntity extends Monster implements GeoEntity, TraceableEntity {
    public static final RawAnimation CONSTANT_BITES = RawAnimation.begin().thenLoop("piranha_plant.constant_bite");
    public static final RawAnimation BABY_DEATH = RawAnimation.begin().thenPlayAndHold("piranha_plant.baby_death");
    public static final RawAnimation BABY_EMERGE = RawAnimation.begin().thenPlay("piranha_plant.baby_emerge");
    public static final RawAnimation BABY_HIDE = RawAnimation.begin().thenPlay("piranha_plant.baby_hide");
    public static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("piranha_plant.death");
    public static final RawAnimation EMERGE = RawAnimation.begin().thenPlay("piranha_plant.emerge");
    public static final RawAnimation GROW = RawAnimation.begin().thenPlayAndHold("piranha_plant.grow");
    public static final RawAnimation HIDE = RawAnimation.begin().thenPlay("piranha_plant.hide");
    public static final RawAnimation HURT = RawAnimation.begin().thenPlay("piranha_plant.hurt");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("piranha_plant.idle");
    public static final RawAnimation SQUASH = RawAnimation.begin().thenPlayAndHold("piranha_plant.squash");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(PiranhaPlantEntity.class, EntityDataSerializers.BOOLEAN);
    public static final int BABY_START_AGE = -24000;
    private static final int FORCED_AGE_PARTICLE_TICKS = 40;

    private BlockPos attachedBlockPos;
    private Direction attachedSide;
    private PiranhaPlantPart[] subEntities;
    public PiranhaPlantPart head;
    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private boolean leftOwner;
    public boolean isHiding;
    public int hideTicks = -1;
    public int hideAnimationTicks = 0;
    public int attackCooldown = 0;
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;

    public PiranhaPlantEntity(EntityType<? extends PiranhaPlantEntity> type, Level world) {
        super(type, world);
        this.head = new PiranhaPlantPart(this, "head",
                1.0F * this.getWidthAttribute() * this.getScaleAttribute(), 1.0F * this.getHeightAttribute() * this.getScaleAttribute());
        this.subEntities = new PiranhaPlantPart[]{this.head};
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(2);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.PIRANHA_PLANT_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.PIRANHA_PLANT_DEATH.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0D, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Death", 0, this::deathAnimation));
        controllers.add(new AnimationController<>(this, "Idle", 10, this::biteAnimation));
        controllers.add(new AnimationController<>(this, "Squash", 5, this::deathAnimation));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
        controllers.add(new AnimationController<>(this, "baby_emerge_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("baby_emerge", BABY_EMERGE));
        controllers.add(new AnimationController<>(this, "baby_hide_controller", 0, state -> PlayState.STOP)
                .triggerableAnim("baby_hide", BABY_HIDE));
        controllers.add(new AnimationController<>(this, "grow_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("grow", GROW));
        controllers.add(new AnimationController<>(this, "emerge_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("emerge", EMERGE));
        controllers.add(new AnimationController<>(this, "hide_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("hide", HIDE));
        controllers.add(new AnimationController<>(this, "hurt_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("hurt", HURT));
    }

    protected <E extends GeoAnimatable> PlayState biteAnimation(final AnimationState<E> event) {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(5.0D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && !this.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || (this.getOwner() != null && this.getOwner().getUUID().equals(collidingEntity.getUUID())))
                    continue;

                if ((this.getOwner() != null && !((collidingEntity instanceof Monster)
                            || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK) || (this.isBaby() && collidingEntity instanceof Animal)))
                        || (this.getOwner() == null && !collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    continue;

                if (this.getOwner() != null && collidingEntity.getTeam() != null && this.getOwner().getTeam() != null
                        && collidingEntity.getTeam() == this.getOwner().getTeam())
                    continue;

                event.setAndContinue(CONSTANT_BITES);
            }
        } else event.setAndContinue(IDLE);
        return PlayState.CONTINUE;
    }

    protected <E extends GeoAnimatable> PlayState deathAnimation(final AnimationState<E> event) {
        if (this.dead) {
            if (this.getLastDamageSource() != null
                && (this.getLastDamageSource().is(DamageTypeRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageTypeRegistry.PLAYER_STOMP))) {
                event.setAndContinue(SQUASH);
                return PlayState.CONTINUE;
            } else {
                if (this.isBaby())
                    event.setAndContinue(BABY_DEATH);
                else event.setAndContinue(DEATH);
                return PlayState.CONTINUE;
            }
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
        tag.putBoolean("IsBaby", this.isBaby());
        tag.putBoolean("IsHiding", this.isHiding());
        tag.putInt("Age", this.getAge());
        tag.putInt("ForcedAge", this.forcedAge);

        if (this.ownerUUID != null)
            tag.putUUID("Owner", this.ownerUUID);
        if (this.leftOwner)
            tag.putBoolean("LeftOwner", true);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setBaby(tag.getBoolean("IsBaby"));
        this.hide(tag.getBoolean("IsHiding"));
        this.leftOwner = tag.getBoolean("LeftOwner");
        this.setAge(tag.getInt("Age"));
        this.forcedAge = tag.getInt("ForcedAge");

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BABY_ID, false);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        if (DATA_BABY_ID.equals(accessor))
            this.refreshDimensions();
        super.onSyncedDataUpdated(accessor);
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

        if (true) return;
        PiranhaPlantPart[] piranhaPlantPart = this.getSubEntities();

        for (int i = 0; i < piranhaPlantPart.length; i++)
            piranhaPlantPart[i].setId(i + packet.getId());
    }

    @Override
    public void tick() {
        super.tick();
        this.head.tick();

        if (attachedBlockPos != null) {
            BlockPos newPos = this.findValidBlockPos();
            if (this.level().isEmptyBlock(attachedBlockPos) && !this.isHiding()) {
                this.detachFromBlock();
            } else if (newPos == null && !this.isHiding()) {
                this.detachFromBlock();
            } else if (newPos != null && this.determineAttachmentSide(newPos) != Direction.UP) {
                this.setNoGravity(true);
                this.setDeltaMovement(Vec3.ZERO);
            }
        }

        if (attachedBlockPos == null) {
            BlockPos newPos = this.findValidBlockPos();
            if (newPos != null && this.determineAttachmentSide(newPos) != Direction.UP)
                this.attachToBlock(newPos, this.determineAttachmentSide(newPos));
            else if (newPos != null && this.onGround() && this.getAttachedSide() == Direction.UP)
                this.attachToBlock(newPos, this.determineAttachmentSide(newPos));
        }

        BlockPos newPos = this.findValidBlockPos();
        if (newPos != null && !newPos.equals(attachedBlockPos))
            this.setAttachedBlockPos(newPos);

        if (!this.leftOwner)
            this.leftOwner = this.checkLeftOwner();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.attackCooldown > 0)
            this.attackCooldown--;

        if (this.hideTicks > 0)
            this.hideTicks--;

        if (this.hideAnimationTicks > 0)
            this.hideAnimationTicks--;

        this.biteEntity();
        this.hideInBlock();

        Direction attachedSide = this.getAttachedSide();
        if (attachedSide != null) {
            Vec3 offset = calculateHitboxOffset(attachedSide);
            this.tickPart(this.head, offset.x, offset.y, offset.z);
        } else this.tickPart(this.head, 0.0, 1.0, 00.0);

        if (this.level().isClientSide) {
            if (this.forcedAgeTimer > 0) {
                if (this.forcedAgeTimer % 4 == 0)
                    this.level().addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(1.0),
                            this.getRandomY() + 0.5, this.getRandomZ(1.0), 0.0, 0.0, 0.0);
                this.forcedAgeTimer--;
            }
        } else if (this.isAlive()) {
            int age = this.getAge();

            if (age < 0)
                this.setAge(++age);
            else if (age > 0)
                this.setAge(--age);

            if (age == -5 || age == 5)
                this.triggerAnim("grow_controller", "grow");
        }
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isFood(stack)) {
            int age = this.getAge();

            if (this.isBaby() && !this.isHiding()) {
                stack.consume(1, player);
                this.swing(InteractionHand.MAIN_HAND);
                this.ageUp(getSpeedUpSecondsWhenFeeding(-age), true);
                this.playSound(SoundRegistry.PIRANHA_PLANT_CHOMP.get(), 1.0F, 1.0F);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }

            if (this.level().isClientSide)
                return InteractionResult.CONSUME;
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.isHiding() || source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            boolean isHurt = super.hurt(source, amount);
            if (isHurt && this.isAlive())
                this.triggerAnim("hurt_controller", "hurt");
            return isHurt;
        }
        else return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        if (!this.isHiding())
            return super.canBeHitByProjectile();
        else return false;
    }

    @Override
    public void die(DamageSource source) {
        Component deathMessage = this.getCombatTracker().getDeathMessage();
        super.die(source);

        if (this.dead && this.getOwner() != null) {
            if (!this.level().isClientSide && this.level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES)
                    && this.getOwner() instanceof ServerPlayer)
                this.getOwner().sendSystemMessage(deathMessage);
        }
    }

    @NotNull
    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        Direction attachedSide = this.getAttachedSide();

        if (this.isBaby() && attachedSide == Direction.UP)
            return EntityRegistry.PIRANHA_PLANT.get().getDimensions().scale(0.75F).withEyeHeight(0.9F);

        if (attachedSide == Direction.NORTH || attachedSide == Direction.SOUTH
                || attachedSide == Direction.EAST || attachedSide == Direction.WEST) {
            return EntityDimensions.scalable(1.0F, 1.0F);
        } else if (attachedSide == Direction.DOWN)
            return EntityDimensions.scalable(1.0F, 1.0F).withEyeHeight(-0.9F);

        return super.getDefaultDimensions(pose);
    }

    @NotNull
    @Override
    public AABB makeBoundingBox() {
        Direction facing = this.getAttachedSide();
        double height = this.getHeightAttribute();
        double width = this.getWidthAttribute();
        double scale = this.getScaleAttribute();

        if (facing == null)
            facing = Direction.UP;

        if (!this.isBaby()) {
            return switch (facing) {
                case UP -> new AABB(
                        this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 2.3125 * height * scale, this.getZ() + 0.5 * width * scale);
                case DOWN -> new AABB(
                        this.getX() - 0.5 * width * scale, this.getY() - 1.3125, this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
                case NORTH -> new AABB(
                        this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 1.8125 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
                case SOUTH -> new AABB(
                        this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 1.8125 * width * scale);
                case EAST -> new AABB(
                        this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 1.8125 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
                case WEST -> new AABB(
                        this.getX() - 1.8125 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
            };
        } else return new AABB(0, 0, 0, 0, 0, 0);
    }

    public static boolean checkPiranhaPlantSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                      MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isBrightEnoughToSpawn(serverWorld, pos))
                && serverWorld.getBlockState(pos.below()).is(TagRegistry.PIRANHA_PLANTS_SPAWNABLE_ON);
    }

    protected static boolean isBrightEnoughToSpawn(BlockAndTintGetter blockGetter, BlockPos pos) {
        int skyLight = blockGetter.getRawBrightness(pos, 0);
        int blockLight = blockGetter.getBrightness(LightLayer.BLOCK, pos);
        return skyLight > 7 && blockLight <= 7;
    }

    @Override
    protected void pushEntities() {
        if (!isHiding())
            super.pushEntities();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isInWall() {
        if (isHiding())
            return false;
        else return super.isInWall();
    }

    @Override
    protected boolean wouldNotSuffocateAtTargetPose(Pose pose) {
        AABB aabb = this.getDimensions(pose).makeBoundingBox(this.position());
        return this.level().noBlockCollision(this, aabb) || this.isHiding();
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @NotNull
    @Override
    protected Vec3 getLeashOffset() {
        return new Vec3(0.0, this.getEyeHeight() / 1.75, this.getBbWidth() / 2);
    }

    @Override
    public boolean isMultipartEntity() {
        return true;
    }

    public PiranhaPlantPart[] getSubEntities() {
        return this.subEntities;
    }

    @Override
    public PartEntity<?> @NotNull [] getParts() {
        return this.subEntities;
    }

    public boolean isFood(ItemStack stack) {
        return stack.is(TagRegistry.PIRANHA_FOOD);
    }

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }

    @Override
    public void setBaby(boolean isBaby) {
        this.setAge(isBaby ? BABY_START_AGE : 0);
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
        if (entity instanceof PiranhaPlantEntity plant)
            this.cachedOwner = plant.cachedOwner;
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

    public int getAge() {
        if (this.level().isClientSide)
            return this.entityData.get(DATA_BABY_ID) ? -1 : 1;
        else return this.age;
    }

    public void ageUp(int age, boolean doForceAge) {
        int i = this.getAge();
        i += age * 20;
        if (i > 0)
            i = 0;

        int j = i - i;
        this.setAge(i);
        if (doForceAge) {
            this.forcedAge += j;
            if (this.forcedAgeTimer == 0)
                this.forcedAgeTimer = FORCED_AGE_PARTICLE_TICKS;
        }

        if (this.getAge() == 0)
            this.setAge(this.forcedAge);
    }

    public void ageUp(int age) {
        this.ageUp(age, false);
    }

    public void setAge(int age) {
        int i = this.getAge();
        this.age = age;
        if (i < 0 && age >= 0 || i >= 0 && age < 0) {
            this.entityData.set(DATA_BABY_ID, age < 0);
            this.ageBoundaryReached();
        }
    }

    protected void ageBoundaryReached() {
        if (!this.isBaby() && this.isPassenger() && this.getVehicle() instanceof Boat boat && !boat.hasEnoughSpaceFor(this))
            this.stopRiding();
    }

    public static int getSpeedUpSecondsWhenFeeding(int feedTicks) {
        return (int)((float)(feedTicks / 20) * 0.1F);
    }

    private void tickPart(PiranhaPlantPart part, double offsetX, double offsetY, double offsetZ) {
        double height = part.getBbHeight();
        double width = part.getBbWidth() / 2.0;
        double heightScale = this.getHeightAttribute();
        double widthScale = this.getWidthAttribute();
        double scale = this.getScaleAttribute();

        if (!this.isBaby()) {
            part.setPos(this.getX() + offsetX + 0.5, this.getY() + offsetY, this.getZ() + offsetZ + 0.5);
            part.setBoundingBox(new AABB(this.getX() + offsetX - width, this.getY() + offsetY, this.getZ() + offsetZ - width,
                    this.getX() + (offsetX + width * widthScale * (scale / 2)), this.getY() + (offsetY + height * heightScale * scale),
                    this.getZ() + (offsetZ + width * widthScale * (scale / 2))));
        }
    }

    private Vec3 calculateHitboxOffset(Direction attachedSide) {
        double height = this.getHeightAttribute();
        double width = this.getWidthAttribute();
        double scale = this.getScaleAttribute();
        double offsetX = 0.0;
        double offsetY = 0.0;
        double offsetZ = 0.0;

        switch (attachedSide) {
            case UP -> offsetY = 1.0 * height * scale;
            case DOWN -> offsetY = -1.0 * height * scale;
            case NORTH -> offsetZ = -1.0 * width * scale;
            case SOUTH -> offsetZ = 1.0 * width * scale;
            case WEST -> offsetX = -1.0 * width * scale;
            case EAST -> offsetX = 1.0 * width * scale;
        }

        return new Vec3(offsetX, offsetY, offsetZ);
    }

    private float getHeightAttribute() {
        AttributeMap attributeMap = this.getAttributes();
        if (attributeMap != null)
            return (float) this.getAttributeValue(AttributesRegistry.HEIGHT_SCALE);
        else return 1.0F;
    }

    private float getWidthAttribute() {
        AttributeMap attributeMap = this.getAttributes();
        if (attributeMap != null)
            return (float) this.getAttributeValue(AttributesRegistry.WIDTH_SCALE);
        else return 1.0F;
    }

    private float getScaleAttribute() {
        AttributeMap attributeMap = this.getAttributes();
        if (attributeMap != null)
            return (float) this.getAttributeValue(Attributes.SCALE);
        else return 1.0F;
    }

    public Direction getAttachedSide() {
        return attachedSide;
    }

    public BlockState getBlockAttachedTo(Level world) {
        return world.getBlockState(attachedBlockPos);
    }

    public void setAttachedSide(Direction side) {
        this.attachedSide = side;
    }

    public void setAttachedBlockPos(BlockPos pos) {
        this.attachedBlockPos = pos;
    }

    public void attachToBlock(BlockPos blockPos, Direction direction) {
        this.attachedBlockPos = blockPos;
        this.attachedSide = direction;
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public void detachFromBlock() {
        this.attachedBlockPos = null;
        this.attachedSide = null;
        this.setNoGravity(false);
    }

    public BlockPos findValidBlockPos() {
        BlockPos entityPos = this.blockPosition();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = entityPos.relative(direction);
            BlockState neighborState = this.level().getBlockState(neighborPos);
            if (!neighborState.isAir() && !neighborState.is(TagRegistry.PIRANHA_PLANTS_CANNOT_ATTACH)
                    && (neighborState.isSolid()
                        || neighborState.getBlock() instanceof LeavesBlock)) {
                return neighborPos;
            }
        }
        return null;
    }

    public Direction determineAttachmentSide(BlockPos blockPos) {
        BlockPos entityPos = this.blockPosition();
        for (Direction direction : Direction.values()) {
            if (blockPos.relative(direction.getOpposite()).equals(entityPos)) {
                return direction.getOpposite();
            }
        }
        return Direction.UP;
    }

    private int getHideDuration() {
        return ConfigRegistry.PIRANHA_PLANT_HIDE_DURATION.get();
    }

    public void hideInBlock() {
        Level world = this.level();
        BlockPos pos = this.blockPosition();
        BlockPos posBelow = pos.below();
        BlockState state = world.getBlockState(pos);
        BlockState stateBelow = world.getBlockState(posBelow);

        Direction[] prioritizedDirections = new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

        if (this.isHiding()) {
            for (Direction direction : prioritizedDirections) {
                BlockPos oppositePos = pos.relative(direction.getOpposite());
                BlockState offsetState = world.getBlockState(oppositePos);

                if (offsetState.hasProperty(BlockStateProperties.FACING)
                        && offsetState.getValue(BlockStateProperties.FACING) == direction
                        && offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {
                    if (world.getGameTime() % this.getHideDuration() == 0L && this.hideTicks == 0L) {
                        if (this.isBaby()) {
                            this.stopTriggeredAnim("baby_hide_controller", "baby_hide");
                            this.triggerAnim("baby_emerge_controller", "baby_emerge");
                        } else {
                            this.stopTriggeredAnim("hide_controller", "hide");
                            this.triggerAnim("emerge_controller", "emerge");
                        }
                        this.hide(false);
                    }
                }
            }
        } else if (world.getGameTime() % this.getHideDuration() == 0L) {
            for (Direction direction : prioritizedDirections) {
                BlockPos oppositePos = pos.relative(direction.getOpposite());
                BlockState offsetState = world.getBlockState(oppositePos);

                if (offsetState.hasProperty(BlockStateProperties.FACING)
                        && offsetState.getValue(BlockStateProperties.FACING) == direction
                        && offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {
                    this.hideAnimationTicks = 15;
                    this.hideTicks = this.getHideDuration();
                    if (this.isBaby()) {
                        this.stopTriggeredAnim("baby_emerge_controller", "baby_emerge");
                        this.triggerAnim("baby_hide_controller", "baby_hide");
                    } else {
                        this.stopTriggeredAnim("emerge_controller", "emerge");
                        this.triggerAnim("hide_controller", "hide");
                    }
                    this.hide(true);
                }
            }
        }

        if (this.isHiding() && !state.hasProperty(BlockStateProperties.FACING)) {
            if (world.getGameTime() % this.getHideDuration() == 0L && this.hideTicks == 0L
                    && stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {
                if (this.isBaby()) {
                    this.stopTriggeredAnim("baby_hide_controller", "baby_hide");
                    this.triggerAnim("baby_emerge_controller", "baby_emerge");
                } else {
                    this.stopTriggeredAnim("hide_controller", "hide");
                    this.triggerAnim("emerge_controller", "emerge");
                }
                this.hide(false);
            }
        } else if (world.getGameTime() % this.getHideDuration() == 0L
                && stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                && !stateBelow.hasProperty(BlockStateProperties.FACING)) {
            this.hideAnimationTicks = 15;
            this.hideTicks = this.getHideDuration();
            if (this.isBaby()) {
                this.stopTriggeredAnim("baby_emerge_controller", "baby_emerge");
                this.triggerAnim("baby_hide_controller", "baby_hide");
            } else {
                this.stopTriggeredAnim("emerge_controller", "emerge");
                this.triggerAnim("hide_controller", "hide");
            }
            this.hide(true);
        }
    }

    public void biteEntity() {
        if (this.attackCooldown > 0) {
            this.attackCooldown--;
            return;
        }

        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.01, 0.0, 0.01), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity)
                        && !this.level().isClientSide());

        if (!nearbyEntities.isEmpty() && !this.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || (this.getOwner() != null && this.getOwner().getUUID().equals(collidingEntity.getUUID())))
                    continue;

                if ((this.getOwner() != null && !((collidingEntity instanceof Monster)
                            || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK) || (this.isBaby() && collidingEntity instanceof Animal)))
                        || (this.getOwner() == null && !collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    continue;

                if (this.getOwner() != null && collidingEntity.getTeam() != null && this.getOwner().getTeam() != null
                        && collidingEntity.getTeam() == this.getOwner().getTeam())
                    continue;

                this.swing(InteractionHand.MAIN_HAND);

                float attackDamage = this.isBaby() ? (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2
                        : (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

                if (this.getOwner() != null)
                    collidingEntity.hurt(DamageTypeRegistry.piranhaChomp(collidingEntity, this.getOwner()), attackDamage);
                else collidingEntity.hurt(DamageTypeRegistry.piranhaChomp(null, this), attackDamage);

                if (collidingEntity instanceof NeutralMob neutralMob) {
                    neutralMob.isAngryAt(this);
                    neutralMob.setTarget(this);
                    neutralMob.setPersistentAngerTarget(this.getUUID());
                }

                int age = this.getAge();
                if (this.isBaby()) {
                    this.ageUp(getSpeedUpSecondsWhenFeeding(-age), true);
                    if (this.level() instanceof ServerLevel serverWorld)
                        ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.HAPPY_VILLAGER, serverWorld, this, 5);
                }

                this.playSound(SoundRegistry.PIRANHA_PLANT_CHOMP.get(), 1.0F, 1.0F);
                this.attackCooldown = 20;
                break;
            }
        }
    }

    public boolean isHiding() {
        return this.isHiding;
    }

    public void hide(boolean isHiding) {
        this.isHiding = isHiding;
    }
}
