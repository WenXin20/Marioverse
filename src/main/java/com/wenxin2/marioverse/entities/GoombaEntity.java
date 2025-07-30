package com.wenxin2.marioverse.entities;

import com.mojang.authlib.GameProfile;
import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.GoombaRideGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSitGoal;
import com.wenxin2.marioverse.entities.ai.goals.GoombaSleepGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.AmphibiousPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
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

public class GoombaEntity extends Monster implements GeoEntity {
    private static final EntityDataAccessor<Byte> DATA_ID_RIDE_FLAGS = SynchedEntityData.defineId(GoombaEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_ID_SCARE_FLAGS = SynchedEntityData.defineId(GoombaEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_ID_SIT_FLAGS = SynchedEntityData.defineId(GoombaEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> DATA_ID_SLEEP_FLAGS = SynchedEntityData.defineId(GoombaEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("goomba.death");
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("goomba.idle");
    public static final RawAnimation IDLE_SWIM_ANIM = RawAnimation.begin().thenLoop("goomba.idle_swim");
    public static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("goomba.run");
    public static final RawAnimation SCARE_ANIM = RawAnimation.begin().thenLoop("goomba.scared");
    public static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("goomba.sit");
    public static final RawAnimation SLEEP_ANIM = RawAnimation.begin().thenLoop("goomba.sleep");
    public static final RawAnimation SQUASH_ANIM = RawAnimation.begin().thenPlayAndHold("goomba.squash");
    public static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("goomba.swim");
    public static final RawAnimation SWIM_SQUASH_ANIM = RawAnimation.begin().thenPlayAndHold("goomba.swim_squash");
    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("goomba.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public boolean wasSleeping;

    public GoombaEntity(EntityType<? extends GoombaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(1);
    }

    @Nullable    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.GOOMBA_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    public void die(DamageSource source) {
        if (source.is(DamageTypeRegistry.STOMP)
                || source.is(DamageTypeRegistry.PLAYER_STOMP))
            this.playSound(getStompSound());
        else if (source.is(DamageTypeRegistry.MINI_GOOMBA_DEFEATED)
                || source.is(DamageTypeRegistry.PLAYER_MINI_GOOMBA_DEFEATED))
            this.playSound(SoundRegistry.MINI_GOOMBA_DEFEATED.get());
        else this.playSound(SoundRegistry.GOOMBA_DEATH.get());
        super.die(source);
    }

    @NotNull
    public SoundEvent getStompSound() {
        return SoundRegistry.GOOMBA_STOMP.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.GOOMBA_AMBIENT.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundRegistry.GOOMBA_STEP.get(), 1.0F, 1.0F);
        super.playStepSound(pos, state);
    }

    protected SoundEvent getBumpSound() {
        return SoundRegistry.GOOMBA_BUMP.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_RIDE_FLAGS, (byte)0);
        builder.define(DATA_ID_SCARE_FLAGS, (byte)0);
        builder.define(DATA_ID_SIT_FLAGS, (byte)0);
        builder.define(DATA_ID_SLEEP_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(1, new GoombaSitGoal(this, 0.7F, 1200, 3000, 300));
        this.goalSelector.addGoal(2, new GoombaSleepGoal(this, 0.25F, 2400, 6000));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new GoombaRideGoal(this, 0.01F));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.GOOMBA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Death", 5, this::squashAnimController));
        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Scare", 5, this::scareAnimController));
        controllers.add(new AnimationController<>(this, "Squash", 5, this::squashAnimController));
        controllers.add(new AnimationController<>(this, "Swim", 15, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkAnimController));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
        controllers.add(DefaultAnimations.genericWalkController(this));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimController(final AnimationState<E> event) {
        if ((this.isSitting() || (this.isPassenger() && !(this.getVehicle() instanceof LivingEntity))) && !this.isScared()) {
            event.setAndContinue(SIT_ANIM);
            return PlayState.CONTINUE;
        }

        if (this.isSleeping() && !this.isScared()) {
            event.setAndContinue(SLEEP_ANIM);
            return PlayState.CONTINUE;
        }

        if (this.isInWaterOrBubble()) {
            if (!this.isRunning() && !this.isWalking())
                event.setAndContinue(IDLE_SWIM_ANIM);
            else event.setAndContinue(SWIM_ANIM);
            return PlayState.CONTINUE;
        } else if (this.isRunning() && !this.isScared()) {
            event.setAndContinue(RUN_ANIM);
            return PlayState.CONTINUE;
        } else if (event.isMoving() && !this.isScared()) {
            event.setAndContinue(WALK_ANIM);
            return PlayState.CONTINUE;
        } else if (this.isScared()) {
            event.setAndContinue(SCARE_ANIM);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE_ANIM);
            return PlayState.CONTINUE;
        }
    }

    protected <E extends GeoAnimatable> PlayState squashAnimController(final AnimationState<E> event) {
        if (this.dead) {
            if (this.getLastDamageSource() != null
                && (this.getLastDamageSource().is(DamageTypeRegistry.STOMP)
                    || this.getLastDamageSource().is(DamageTypeRegistry.PLAYER_STOMP))) {
                if (this.isInWaterOrBubble()) {
                    if (!this.isRunning() && !this.isWalking())
                        event.setAndContinue(SQUASH_ANIM);
                    else event.setAndContinue(SWIM_SQUASH_ANIM);
                } else event.setAndContinue(SQUASH_ANIM);
                return PlayState.CONTINUE;
            } else {
                event.setAndContinue(DEATH_ANIM);
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    protected <E extends GeoAnimatable> PlayState scareAnimController(final AnimationState<E> event) {
        if (this.isScared()) {
            event.setAndContinue(SCARE_ANIM);
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ID_RIDE_FLAGS, tag.getByte("RideFlags"));
        this.entityData.set(DATA_ID_SCARE_FLAGS, tag.getByte("ScareFlags"));
        this.entityData.set(DATA_ID_SIT_FLAGS, tag.getByte("SitFlags"));
        this.entityData.set(DATA_ID_SLEEP_FLAGS, tag.getByte("SleepFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("RideFlags", this.entityData.get(DATA_ID_RIDE_FLAGS));
        tag.putByte("ScareFlags", this.entityData.get(DATA_ID_SCARE_FLAGS));
        tag.putByte("SitFlags", this.entityData.get(DATA_ID_SIT_FLAGS));
        tag.putByte("SleepFlags", this.entityData.get(DATA_ID_SLEEP_FLAGS));
    }

    public boolean isSitting() {
        return this.getSitFlag(8);
    }

    public boolean isSleeping() {
        return this.getSleepFlag(12);
    }

    public boolean isScared() {
        return this.getScareFlag(9);
    }

    public boolean isWalking() {
        return (this.getDeltaMovement().horizontalDistance() >= 0.01
                && this.getDeltaMovement().horizontalDistance() < 0.5)
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof RandomStrollGoal
                || this.walkDist > 0);
    }

    private boolean isRunning() {
        return this.isSprinting() || this.getSpeed() >= 0.5 || this.getDeltaMovement().horizontalDistance() >= 0.5
                || this.goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof MeleeAttackGoal)
                || this.targetSelector.getAvailableGoals().stream().anyMatch(goal -> goal.isRunning() && goal.getGoal() instanceof NearestAttackableTargetGoal<?>);
    }

    private int scareDuration = 0;
    private int scareTime = 0;

    @Override
    public void tick() {
        super.tick();

        if (this.isScared()) {
            float scaleFactor = this.getBbHeight() * this.getBbWidth();
            int numParticles = (int) (scaleFactor * 5);
            double radius = this.getBbWidth() / 2;

            for (int i = 0; i < numParticles; i++) {
                // Calculate angle for each particle
                double angle = 2 * Math.PI * i / numParticles;
                // Calculate the X and Z offset using sine and cosine to spread in an ellipse
                double offsetX = Math.cos(angle) * radius;
                double offsetY = this.getBbHeight();
                double offsetZ = Math.sin(angle) * radius;

                double x = this.getX() + offsetX;
                double y = this.getY();
                double z = this.getZ() + offsetZ;

                this.level().addParticle(new BlockParticleOption(ParticleTypes.BLOCK, this.getBlockStateOn()), x, y, z, 0, 0, 0);
            }
        }

        if (this.isScared()) {
            if (scareTime == 0)
                scareDuration = 25 + this.random.nextInt(50);

            if (scareTime > scareDuration) {
                this.scare(Boolean.FALSE);
                this.sit(Boolean.FALSE);
                this.sleep(Boolean.FALSE);
                scareTime = 0;
            }
            scareTime++;
        }

        if (this.isSleeping() || this.isSitting())
            this.checkForCollisionsAndWakeUp();

        if (this.isInWaterOrBubble())
            this.ejectPassengers();
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        this.handleAirSupply(i);

        if (this.getTarget() != null) {
            this.setSpeed(0.8F);
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new AmphibiousPathNavigation(this, world);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        if (this instanceof AbilitiesHandler handler) {
            if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
                int i = random.nextInt(6);
                int randomInt = random.nextInt(1);
                if (i == 0) {
                    if (randomInt == 0)
                        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.MARIO_FIRE_HAT.get()));
                    else this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.LUIGI_FIRE_HAT.get()));
                    handler.mv$setFireFlower(true);
                } else if (i == 1) {
                    if (randomInt == 0)
                        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.MARIO_ICE_HAT.get()));
                    else this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.LUIGI_ICE_HAT.get()));
                    handler.mv$setIceFlower(true);
                } else {
                    this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
                }
            }
        }
    }

    @Override
    public boolean canTakeItem(ItemStack stack) {
        EquipmentSlot equipmentslot = this.getEquipmentSlotForItem(stack);
        return this.getItemBySlot(equipmentslot).isEmpty();
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && (player.getItemInHand(hand).getItem() instanceof ArmorItem
                || (player.getItemInHand(hand).getItem() instanceof BlockItem blockItem
                    && (blockItem.getBlock() instanceof SkullBlock
                        || blockItem.getBlock() instanceof EquipableCarvedPumpkinBlock
                        || blockItem.getBlock() instanceof CarvedPumpkinBlock)))) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData) {
        RandomSource random = serverWorld.getRandom();

        if (groupData instanceof GoombaGroupData goombaGroupData) {
            this.populateDefaultEquipmentSlots(random, difficulty);
            this.populateDefaultEquipmentEnchantments(serverWorld, random, difficulty);

            if (goombaGroupData.canSpawnJockey) {
                if (random.nextDouble() < 0.05) {
                    List<Mob> nearbyEntities = serverWorld.getEntitiesOfClass(
                            Mob.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0),
                            entity -> entity.getType().is(TagRegistry.GOOMBA_CAN_RIDE) && !entity.isVehicle()
                    );

                    if (!nearbyEntities.isEmpty()) {
                        Mob mob = nearbyEntities.getFirst();
                        this.startRiding(mob);
                    }
                } else if (random.nextDouble() < 0.05) {
                    Optional<? extends Holder<EntityType<?>>> randomEntityHolder = serverWorld.registryAccess()
                            .registryOrThrow(Registries.ENTITY_TYPE)
                            .getTag(TagRegistry.GOOMBA_CAN_RIDE)
                            .flatMap(tag -> tag.getRandomElement(random));

                    if (randomEntityHolder.isPresent()) {
                        EntityType<?> entityType = randomEntityHolder.get().value();
                        Mob mob = (Mob) entityType.create(this.level());
                        if (mob != null) {
                            mob.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                            mob.finalizeSpawn(serverWorld, difficulty, MobSpawnType.JOCKEY, null);
                            this.startRiding(mob);
                            serverWorld.addFreshEntity(mob);
                        }
                    }
                }
            }
        }

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localdate = LocalDate.now();
            int day = localdate.getDayOfMonth();
            int month = localdate.getMonth().getValue();
            List<ServerPlayer> players = serverWorld.getLevel().players();

            if ((month == 10 && day == 31 && !ConfigRegistry.DISABLE_GOOMBA_MASKS.get())
                    || ConfigRegistry.FORCE_GOOMBA_MASKS.get()) {
                if (random.nextFloat() < 0.25F)
                    this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F
                            ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));

                if (random.nextFloat() < 0.15F) {
                    List<ItemStack> skulls = new ArrayList<>();

                    serverWorld.registryAccess().registryOrThrow(Registries.BLOCK).getTagOrEmpty(Tags.Blocks.SKULLS).forEach(holder -> {
                        Block block = holder.value();
                        skulls.add(new ItemStack(block));
                        ItemStack randomSkull = skulls.get(random.nextInt(skulls.size()));
                        if (randomSkull.getItem() instanceof PlayerHeadItem) {
                            if (!players.isEmpty()) {
                                ServerPlayer randomPlayer = players.get(random.nextInt(players.size()));
                                GameProfile playerProfile = randomPlayer.getGameProfile();
                                SkullBlockEntity.fetchGameProfile(randomPlayer.getUUID());
                                ItemStack playerHeadItem = new ItemStack(Items.PLAYER_HEAD);

                                playerHeadItem.set(DataComponents.PROFILE, new ResolvableProfile(playerProfile));

                                this.setItemSlot(EquipmentSlot.HEAD, playerHeadItem);
                            }
                        } else this.setItemSlot(EquipmentSlot.HEAD, randomSkull);
                    });
                }

                if (random.nextFloat() < 0.1F) {
                    if (!players.isEmpty()) {
                        ServerPlayer randomPlayer = players.get(random.nextInt(players.size()));
                        GameProfile playerProfile = randomPlayer.getGameProfile();
                        SkullBlockEntity.fetchGameProfile(randomPlayer.getUUID());
                        ItemStack playerHeadItem = new ItemStack(Items.PLAYER_HEAD);

                        playerHeadItem.set(DataComponents.PROFILE, new ResolvableProfile(playerProfile));

                        this.setItemSlot(EquipmentSlot.HEAD, playerHeadItem);
                    }
                }

                this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }
        return super.finalizeSpawn(serverWorld, difficulty, spawnType, groupData);
    }

    public static boolean checkGoombaSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    @NotNull
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float height) {
        return new Vec3(0.0D, this.getBbHeight() - 0.1D, 0.0D);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasHurt = super.hurt(source, amount);

        if (wasHurt && (this.isSitting() || this.isSleeping())) {
            this.sit(Boolean.FALSE);
            this.sleep(Boolean.FALSE);
            this.scare(Boolean.TRUE);
        }
        return wasHurt;
    }


    @Override
    public boolean checkSpawnObstruction(LevelReader worldReader) {
        return worldReader.isUnobstructed(this);
    }

    @Override
    public int getAmbientSoundInterval() {
        return 120;
    }

    protected void handleAirSupply(int airSupplyAmount) {
        if (this.isAlive() && this.isInWaterOrBubble()) {
            this.setAirSupply(airSupplyAmount);
        }
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    public void checkForCollisionsAndWakeUp() {
        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.25D, 0, 0.25D), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof GoombaEntity));

        if (!nearbyEntities.isEmpty()) {
            for (Entity collidingEntity : nearbyEntities) {
                if ((!this.isSleeping() && !this.isSitting())
                        || collidingEntity.getY() >= this.getY() + this.getEyeHeight()
                        || !(collidingEntity.getDeltaMovement().horizontalDistance() > 0.1))
                    return;

                if (collidingEntity instanceof AbilitiesHandler handler && handler.mv$hasSuperStar())
                    return;

                // Apply knockback to both the Goomba and the bumping collidingEntity
                Vec3 knockbackDirection = new Vec3(collidingEntity.getX() - this.getX(), 0.4D,
                        collidingEntity.getZ() - this.getZ()).normalize();
                double knockbackStrength = 1.0D;

                // Knock back the Goomba
                this.setDeltaMovement(-knockbackDirection.x * knockbackStrength, 0.4D,
                        -knockbackDirection.z * knockbackStrength);
                this.hurtMarked = true; // Mark as hurt to apply knockback
                // Knock back the other collidingEntity
                collidingEntity.setDeltaMovement(knockbackDirection.x * knockbackStrength, 0.4D,
                        knockbackDirection.z * knockbackStrength);
                collidingEntity.hurtMarked = true;

                this.playSound(this.getBumpSound());
                this.tryToScare();
                break;
            }
        }
    }

    public void sit(boolean isSitting) {
        this.setSitFlag(8, isSitting);
    }

    private boolean getSitFlag(int i) {
        return (this.entityData.get(DATA_ID_SIT_FLAGS) & i) != 0;
    }

    public void tryToSit() {
        if (!this.isInWaterOrBubble()) {
            this.sit(Boolean.TRUE);
            this.stopInPlace();
        }
    }

    private void setSitFlag(int i, boolean b) {
        byte b0 = this.entityData.get(DATA_ID_SIT_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_SIT_FLAGS, (byte)(b0 | i));
        } else {
            this.entityData.set(DATA_ID_SIT_FLAGS, (byte)(b0 & ~i));
        }
    }

    public void sleep(boolean isSleeping) {
        this.setSleepFlag(12, isSleeping);
    }

    private boolean getSleepFlag(int i) {
        return (this.entityData.get(DATA_ID_SLEEP_FLAGS) & i) != 0;
    }

    public void tryToSleep() {
        if (!this.isInWaterOrBubble()) {
            this.sit(Boolean.FALSE);
            this.sleep(Boolean.TRUE);
            this.stopInPlace();
        }
    }

    private void setSleepFlag(int i, boolean b) {
        byte b1 = this.entityData.get(DATA_ID_SLEEP_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_SLEEP_FLAGS, (byte)(b1 | i));
        } else {
            this.entityData.set(DATA_ID_SLEEP_FLAGS, (byte)(b1 & ~i));
        }
    }

    public boolean wasSleeping() {
        return wasSleeping;
    }

    public void setWasSleeping(boolean wasSleeping) {
        this.wasSleeping = wasSleeping;
    }

    public void scare(boolean isScared) {
        this.setScareFlag(9, isScared);
    }

    private boolean getScareFlag(int i) {
        return (this.entityData.get(DATA_ID_SCARE_FLAGS) & i) != 0;
    }

    public void tryToScare() {
        if (!this.isInWaterOrBubble()) {
            this.sit(Boolean.FALSE);
            this.sleep(Boolean.FALSE);
            this.scare(Boolean.TRUE);
            this.stopInPlace();
        }
    }

    private void setScareFlag(int i, boolean b) {
        byte b1 = this.entityData.get(DATA_ID_SCARE_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_SCARE_FLAGS, (byte)(b1 | i));
        } else {
            this.entityData.set(DATA_ID_SCARE_FLAGS, (byte)(b1 & ~i));
        }
    }

    public void ride(boolean isRiding) {
        this.setRideFlag(10, isRiding);
    }

    public void tryToRide() {
        if (!this.isInWaterOrBubble() && !this.isPassenger()) {
            this.stopInPlace();
        }
    }

    private void setRideFlag(int i, boolean b) {
        byte b1 = this.entityData.get(DATA_ID_SCARE_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_SCARE_FLAGS, (byte)(b1 | i));
        } else {
            this.entityData.set(DATA_ID_SCARE_FLAGS, (byte)(b1 & ~i));
        }
    }

    public static class GoombaGroupData implements SpawnGroupData {
        public final boolean canSpawnJockey;

        public GoombaGroupData(boolean canSpawnJockey) {
            this.canSpawnJockey = canSpawnJockey;
        }
    }
}
