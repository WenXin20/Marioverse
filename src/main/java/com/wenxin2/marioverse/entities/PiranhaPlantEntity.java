package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.LookAtEntityTagGoal;
import com.wenxin2.marioverse.entities.part_entities.PiranhaPlantPart;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
import com.wenxin2.marioverse.entities.variants.PiranhaPlantVariants;
import com.wenxin2.marioverse.network.server_bound.data.PiranhaPlantHidePayload;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.entity.PartEntity;
import net.neoforged.neoforge.network.PacketDistributor;
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

public class PiranhaPlantEntity extends AgeableMob implements GeoEntity, TraceableEntity {
    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData
            .defineId(PiranhaPlantEntity.class, EntityDataSerializers.STRING);
    public static final RawAnimation CONSTANT_BITES = RawAnimation.begin().thenLoop("piranha_plant.constant_bite");
    public static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("piranha_plant.death");
    public static final RawAnimation EMERGE = RawAnimation.begin().thenPlay("piranha_plant.emerge");
    public static final RawAnimation HIDE = RawAnimation.begin().thenPlayAndHold("piranha_plant.hide");
    public static final RawAnimation HURT = RawAnimation.begin().thenPlay("piranha_plant.hurt");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("piranha_plant.idle");
    public static final RawAnimation SQUASH = RawAnimation.begin().thenPlayAndHold("piranha_plant.squash");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final EntityDimensions BABY_DIMENSIONS = EntityRegistry.PIRANHA_PLANT.get().getDimensions()
            .scale(0.75F).withEyeHeight(0.9F);

    private static final EntityDataAccessor<Boolean> DATA_BABY_ID = SynchedEntityData.defineId(PiranhaPlantEntity.class, EntityDataSerializers.BOOLEAN);
    public static final int BABY_START_AGE = -24000;
    private static final int FORCED_AGE_PARTICLE_TICKS = 40;

    @Nullable private UUID ownerUUID;
    @Nullable private Entity cachedOwner;
    private BlockPos attachedBlockPos;
    private Direction attachedSide;
    private PiranhaPlantPart[] subEntities;
    public PiranhaPlantPart head;
    private boolean isEmerging;
    private boolean isHiding;
    private boolean isHidingAnim;
    private boolean leftOwner;
    public int hideTicks = -1;
    public int hideAnimationTicks = 0;
    public int emergeAnimationTicks = 0;
    public int attackCooldown = 0;
    protected int age;
    protected int forcedAge;
    protected int forcedAgeTimer;

    public PiranhaPlantEntity(EntityType<? extends PiranhaPlantEntity> type, Level world) {
        super(type, world);
        this.head = new PiranhaPlantPart(this, "head",
                1.0F * this.getWidthAttribute() * this.getScaleAttribute(), 1.0F * this.getHeightAttribute() * this.getScaleAttribute());
        this.subEntities = new PiranhaPlantPart[] {this.head};
        this.xpReward = 5;
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

    @Nullable
    @Override
    public PiranhaPlantEntity getBreedOffspring(ServerLevel serverLevel, AgeableMob mob) {
        return EntityRegistry.PIRANHA_PLANT.get().create(serverLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(2, new LookAtEntityTagGoal(this, TagRegistry.PIRANHA_PLANT_CAN_ATTACK, 8.0F, 1.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Death", 0, this::deathAnimation));
        controllers.add(new AnimationController<>(this, "Idle", 20, this::biteAnimation));
        controllers.add(new AnimationController<>(this, "Squash", 5, this::deathAnimation));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
        controllers.add(DefaultAnimations.getSpawnController(this, state -> this, 20));
        controllers.add(new AnimationController<>(this, "eat_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("eat", DefaultAnimations.ATTACK_BITE));
        controllers.add(new AnimationController<>(this, "emerge_controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("emerge", EMERGE));
        controllers.add(new AnimationController<>(this, "hide_controller", 5, state -> PlayState.CONTINUE)
                .triggerableAnim("hide", HIDE));
        controllers.add(new AnimationController<>(this, "hurt_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("hurt", HURT));
    }

    protected <E extends GeoAnimatable> PlayState biteAnimation(final AnimationState<E> event) {
        if (this.dead) return PlayState.STOP;

        if (this.isEmerging) {
            event.setAndContinue(EMERGE);
            return PlayState.CONTINUE;
        }

        if (this.isHidingAnim) {
            event.setAndContinue(HIDE);
            return PlayState.CONTINUE;
        }

        if (this.isHiding()) return PlayState.STOP;

        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(5.0D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity));

        if (!nearbyEntities.isEmpty() && !this.isHiding()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PiranhaPlantEntity
                        || this.isAlliedTo(collidingEntity))
                    continue;

                if ((this.getOwner() != null && !((collidingEntity instanceof Monster)
                            || collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK) || (this.isBaby() && collidingEntity instanceof Animal)))
                        || (this.getOwner() == null && !collidingEntity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                    continue;

                event.setAndContinue(CONSTANT_BITES);
                return PlayState.CONTINUE;
            }
        }
        event.setAndContinue(IDLE);
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
                event.setAndContinue(DEATH);
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
        tag.putString("Variant", this.getVariant());

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

        if (tag.contains("Variant"))
            this.setVariant(tag.getString("Variant"));

        if (tag.hasUUID("Owner")) {
            this.ownerUUID = tag.getUUID("Owner");
            this.cachedOwner = null;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BABY_ID, false);
        builder.define(VARIANT, CheepCheepVariants.NORMAL);
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
            if (this.level().isEmptyBlock(attachedBlockPos)) {
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
            if (newPos != null && this.onGround() && this.getAttachedSide() == Direction.UP)
                this.attachToBlock(newPos, this.determineAttachmentSide(newPos));
            else if (newPos != null)
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

        if (this.emergeAnimationTicks > 0)
            this.emergeAnimationTicks--;

        if (this.emergeAnimationTicks == 0)
            this.isEmerging = false;

        this.hideInBlock();

        Direction attachedSide = this.getAttachedSide();
        if (attachedSide != null) {
            Vec3 offset = calculateHitBoxOffset(attachedSide);
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
        }
    }

    @Override
    protected void doPush(Entity entity) {
        this.biteEntity(entity);
    }

    @NotNull
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;
        ItemStack stack = player.getItemInHand(hand);
        FoodProperties food = stack.getComponents().get(DataComponents.FOOD);

        if (this.isFood(stack)) {
            int age = this.getAge();

            if (this.isBaby() && !this.isHiding()) {
                stack.consume(1, player);
                this.swing(InteractionHand.MAIN_HAND);
                this.playSound(SoundRegistry.PIRANHA_PLANT_CHOMP.get(), 1.0F, pitch);
                this.triggerAnim("eat_controller", "eat");

                if (stack.getComponents().has(DataComponents.FOOD) && food != null)
                    this.ageUp(getSpeedUpSecondsWhenFeeding(-age), food.nutrition() * 10, true);
                else this.ageUp(getSpeedUpSecondsWhenFeeding(-age), 20, true);

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
        if (this.isHiding())
            return false;
        else return super.canBeHitByProjectile();
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

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType type, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, type, spawnData);
        Holder<Biome> biome = level.getBiome(this.blockPosition());
        float chance = random.nextFloat();

        if (biome.is(TagRegistry.HAS_TROPICAL_PIRANHA_PLANT) && chance < 0.75F)
            this.setVariant(PiranhaPlantVariants.TROPICAL);
        else if (this.blockPosition().getY() < 32 && !biome.is(TagRegistry.CAVE_PIRANHA_PLANT_CANNOT_SPAWN))
            this.setVariant(PiranhaPlantVariants.CAVE);
        else if (this.blockPosition().getY() < 0 && !biome.is(TagRegistry.DEEP_CAVE_PIRANHA_PLANT_CANNOT_SPAWN))
            this.setVariant(PiranhaPlantVariants.DEEP_CAVE);
        else this.setVariant(PiranhaPlantVariants.NORMAL);

        if (type == MobSpawnType.MOB_SUMMONED)
            this.setAge(-24000);
        return data;
    }

    public static boolean checkPiranhaPlantSpawnRules(EntityType<? extends AgeableMob> entityType, ServerLevelAccessor levelAccessor,
                                                      MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType)
                    || isBrightEnoughToSpawn(levelAccessor.getLevel(), pos)
                    || isValidCaveSpawn(levelAccessor.getLevel(), pos));
    }

    protected static boolean isBrightEnoughToSpawn(ServerLevel level, BlockPos pos) {
        return level.isDay() && level.getBrightness(LightLayer.SKY, pos) > 0
                && level.getBrightness(LightLayer.BLOCK, pos) <= 0
                && level.getBlockState(pos.below()).is(TagRegistry.PIRANHA_PLANTS_SPAWNABLE_ON);
    }

    protected static boolean isValidCaveSpawn(ServerLevel level, BlockPos pos) {
        return level.dimension() == Level.OVERWORLD
                && !level.canSeeSky(pos)
                && level.getBrightness(LightLayer.BLOCK, pos) <= 0
                && (pos.getY() < ConfigRegistry.CAVE_PIRANHA_PLANT_MAX_Y_SPAWN.get()
                    || pos.getY() < ConfigRegistry.DEEP_CAVE_PIRANHA_PLANT_MAX_Y_SPAWN.get())
                && level.getBlockState(pos.below()).is(TagRegistry.CAVE_PIRANHA_PLANTS_SPAWNABLE_ON);
    }

    @NotNull
    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        Direction attachedSide = this.getAttachedSide();

        if (attachedSide == Direction.NORTH || attachedSide == Direction.SOUTH
                || attachedSide == Direction.EAST || attachedSide == Direction.WEST) {
            return EntityDimensions.scalable(1.0F, 1.0F).withEyeHeight(0.9F);
        } else if (attachedSide == Direction.DOWN)
            return EntityDimensions.scalable(1.0F, 1.0F).withEyeHeight(-0.9F);

        return this.isBaby() && attachedSide == Direction.UP ? BABY_DIMENSIONS : super.getDefaultDimensions(pose);
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
                case UP -> new AABB(this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 2.3125 * height * scale, this.getZ() + 0.5 * width * scale);

                case DOWN -> new AABB(this.getX() - 0.5 * width * scale, this.getY() - 1.3125, this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);

                case NORTH -> new AABB(this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 1.8125 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);

                case SOUTH -> new AABB(this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 1.8125 * width * scale);

                case EAST -> new AABB(this.getX() - 0.5 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 1.8125 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);

                case WEST -> new AABB(this.getX() - 1.8125 * width * scale, this.getY(), this.getZ() - 0.5 * width * scale,
                        this.getX() + 0.5 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.5 * width * scale);
            };
        } else return switch (facing) {
            case UP -> new AABB(this.getX() - 0.45 * width * scale, this.getY(), this.getZ() - 0.45 * width * scale,
                    this.getX() + 0.45 * width * scale, this.getY() + 1.3125 * height * scale, this.getZ() + 0.45 * width * scale);

            case DOWN -> new AABB(this.getX() - 0.45 * width * scale, this.getY() - 0.3125, this.getZ() - 0.45 * width * scale,
                    this.getX() + 0.45 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.45 * width * scale);

            case NORTH -> new AABB(this.getX() - 0.45 * width * scale, this.getY(), this.getZ() - 0.8125 * width * scale,
                    this.getX() + 0.45 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.45 * width * scale);

            case SOUTH -> new AABB(this.getX() - 0.45 * width * scale, this.getY(), this.getZ() - 0.45 * width * scale,
                    this.getX() + 0.45 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.8125 * width * scale);

            case EAST -> new AABB(this.getX() - 0.45 * width * scale, this.getY(), this.getZ() - 0.45 * width * scale,
                    this.getX() + 0.8125 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.45 * width * scale);

            case WEST -> new AABB(this.getX() - 0.8125 * width * scale, this.getY(), this.getZ() - 0.45 * width * scale,
                    this.getX() + 0.45 * width * scale, this.getY() + 1.0 * height * scale, this.getZ() + 0.45 * width * scale);
        };
    }

    @Override
    protected void pushEntities() {
        if (!isHiding())
            super.pushEntities();
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean isInWall() {
        if (this.noPhysics || this.isHiding())
            return false;

        float width = this.dimensions.width() * 0.8F;
        AABB eyeBox = AABB.ofSize(this.getEyePosition(), width, 1.0E-6D, width);

        return BlockPos.betweenClosedStream(eyeBox).anyMatch(pos -> {
            if (pos.equals(this.attachedBlockPos))
                return false;

            BlockState state = this.level().getBlockState(pos);

            return !state.isAir() && state.isSuffocating(this.level(), pos)
                    && Shapes.joinIsNotEmpty(state.getCollisionShape(this.level(), pos)
                            .move(pos.getX(), pos.getY(), pos.getZ()),
                    Shapes.create(eyeBox), BooleanOp.AND);
        });
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
        return stack.is(TagRegistry.PIRANHA_PLANT_FOOD);
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
        if (this.cachedOwner != null && !this.cachedOwner.isRemoved())
            return this.cachedOwner;
        else if (this.ownerUUID != null && this.level() instanceof ServerLevel serverWorld) {
            this.cachedOwner = serverWorld.getEntity(this.ownerUUID);
            return this.cachedOwner;
        } else return null;
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

    public String getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(String variant) {
        this.entityData.set(VARIANT, variant);
    }

    public boolean isChomper() {
        return this.getName().getString().toLowerCase(Locale.ROOT).equals("chomper");
    }

    public int getAge() {
        if (this.level().isClientSide)
            return this.entityData.get(DATA_BABY_ID) ? -1 : 1;
        else return this.age;
    }

    public void ageUp(int age, int ageUp, boolean doForceAge) {
        int i = this.getAge();
        i += age * ageUp;
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
        this.ageUp(age, 20, false);
    }

    public void setAge(int age) {
        int i = this.getAge();
        this.age = age;
        if (i < 0 && age >= 0 || i >= 0 && age < 0) {
            this.entityData.set(DATA_BABY_ID, age < 0);
            this.refreshDimensions();
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

    private Vec3 calculateHitBoxOffset(Direction attachedSide) {
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

    public void attachToBlock(BlockPos pos, Direction direction) {
        this.setAttachedBlockPos(pos);
        this.setAttachedSide(direction);
        if (this.getAttachedSide() != Direction.UP) {
            this.setNoGravity(true);
            this.setDeltaMovement(Vec3.ZERO);
        }
    }

    public void detachFromBlock() {
        this.setAttachedBlockPos(null);
        this.setAttachedSide(null);
        this.setNoGravity(false);
    }

    public BlockPos findValidBlockPos() {
        BlockPos entityPos = this.blockPosition();
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = entityPos.relative(direction);
            BlockState neighborState = this.level().getBlockState(neighborPos);
            if (!neighborState.isAir() && !neighborState.is(TagRegistry.PIRANHA_PLANTS_CANNOT_ATTACH)) {
                if (neighborState.is(TagRegistry.WARP_PIPE_BLOCKS))
                    return neighborPos;
            }
        }

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = entityPos.relative(direction);
            BlockState neighborState = this.level().getBlockState(neighborPos);
            if (!neighborState.isAir() && !neighborState.is(TagRegistry.PIRANHA_PLANTS_CANNOT_ATTACH)) {
                if (neighborState.isSolid() || neighborState.getBlock() instanceof LeavesBlock
                        || neighborState.getBlock() instanceof DirtPathBlock)
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

    private boolean isPlayerNearby(double radius) {
        return !this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(radius),
                player -> !player.isSpectator() && player.isAlive() && !player.isCreative()).isEmpty();
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
                Direction attachDir = this.getAttachedSide();

                if (offsetState.hasProperty(BlockStateProperties.FACING)
                        && offsetState.getValue(BlockStateProperties.FACING) == direction
                        && offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                        && !isPlayerNearby(1.0)) {
                    if (direction != attachDir) continue;

                    if (world.getGameTime() % this.getHideDuration() == 0L && this.hideTicks == 0L) {
                        this.emergeAnimationTicks = 40;
                        this.isEmerging = true;
                        this.isHidingAnim = false;
                        this.hide(false);
                    }
                }
            }
        } else if (world.getGameTime() % this.getHideDuration() == 0L) {
            for (Direction direction : prioritizedDirections) {
                BlockPos oppositePos = pos.relative(direction.getOpposite());
                BlockState offsetState = world.getBlockState(oppositePos);
                Direction attachDir = this.getAttachedSide();

                if (offsetState.hasProperty(BlockStateProperties.FACING)
                        && offsetState.getValue(BlockStateProperties.FACING) == direction
                        && offsetState.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)) {
                    if (direction != attachDir) continue;

                    this.hideAnimationTicks = 20;
                    this.hideTicks = this.getHideDuration();
                    this.isHidingAnim = true;
                    this.isEmerging = false;
                    this.hide(true);
                }
            }
        }

        if (this.isHiding() && !state.hasProperty(BlockStateProperties.FACING)) {
            if (world.getGameTime() % this.getHideDuration() == 0L && this.hideTicks == 0L
                    && stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                    && !isPlayerNearby(1.0)) {
                this.emergeAnimationTicks = 40;
                this.isEmerging = true;
                this.isHidingAnim = false;
                this.hide(false);
            }
        } else if (world.getGameTime() % this.getHideDuration() == 0L
                && stateBelow.is(TagRegistry.PIRANHA_PLANTS_CAN_HIDE)
                && !stateBelow.hasProperty(BlockStateProperties.FACING)) {
            this.hideAnimationTicks = 20;
            this.hideTicks = this.getHideDuration();
            this.isHidingAnim = true;
            this.isEmerging = false;
            this.hide(true);
        }
    }

    public void biteEntity(Entity entity) {
        if (this.attackCooldown > 0)
            return;

        float pitch = 0.9F + this.level().random.nextFloat() * 0.2F;

        if (!this.level().isClientSide() && !this.isHiding() && !entity.isSpectator()
                && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity)) {

            if (this.getOwner() != null && this.getOwner().getUUID().equals(entity.getUUID()))
                return;

            if ((this.getOwner() != null && !((entity instanceof Monster)
                    || entity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK) || (this.isBaby() && entity instanceof Animal)))
                    || (this.getOwner() == null && !entity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK)))
                return;

            if (this.getOwner() != null && this.getOwner().isAlliedTo(entity)
                    && !entity.getType().is(TagRegistry.PIRANHA_PLANT_CAN_ATTACK))
                return;

            this.swing(InteractionHand.MAIN_HAND);

            float attackDamage = this.isBaby() ? (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2
                    : (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if (this.getOwner() != null)
                entity.hurt(DamageSourceRegistry.piranhaChomp(entity, this.getOwner()), attackDamage);
            else if (entity instanceof Creeper)
                entity.hurt(DamageSourceRegistry.piranhaChomp(null, entity), attackDamage);
            else entity.hurt(DamageSourceRegistry.piranhaChomp(null, this), attackDamage);

            if (entity instanceof NeutralMob neutralMob) {
                neutralMob.isAngryAt(this);
                neutralMob.setTarget(this);
                neutralMob.setPersistentAngerTarget(this.getUUID());
            }

            int age = this.getAge();
            if (this.isBaby()) {
                this.ageUp(getSpeedUpSecondsWhenFeeding(-age), 20, true);
                if (this.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.HAPPY_VILLAGER, serverWorld, this, 0.5, 5);
            }

            this.playSound(SoundRegistry.PIRANHA_PLANT_CHOMP.get(), 1.0F, pitch);
            this.attackCooldown = 20;
        }
    }

    public boolean isHiding() {
        return this.isHiding;
    }

    public void hide(boolean isHiding) {
        this.isHiding = isHiding;
        if (this.level().isClientSide)
            PacketDistributor.sendToServer(new PiranhaPlantHidePayload(isHiding, this.getId()));
    }
}