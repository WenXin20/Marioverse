package com.wenxin2.marioverse.entities;

import com.mojang.authlib.GameProfile;
import com.wenxin2.marioverse.entities.ai.controls.AmphibiousMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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

public class KoopaTroopaEntity extends Monster implements GeoEntity {
//    private static final EntityDataAccessor<Byte> DATA_ID_RIDE_FLAGS = SynchedEntityData.defineId(KoopaTroopaEntity.class, EntityDataSerializers.BYTE);
//    private static final EntityDataAccessor<Byte> DATA_ID_SCARE_FLAGS = SynchedEntityData.defineId(KoopaTroopaEntity.class, EntityDataSerializers.BYTE);
//    private static final EntityDataAccessor<Byte> DATA_ID_SIT_FLAGS = SynchedEntityData.defineId(KoopaTroopaEntity.class, EntityDataSerializers.BYTE);
//    private static final EntityDataAccessor<Byte> DATA_ID_SLEEP_FLAGS = SynchedEntityData.defineId(KoopaTroopaEntity.class, EntityDataSerializers.BYTE);
//    public static final RawAnimation DEATH_ANIM = RawAnimation.begin().thenPlayAndHold("goomba.death");
    public static final RawAnimation ATTACK_SWING_LEFT = RawAnimation.begin().thenPlay("attack.swing.left");
    public static final RawAnimation ATTACK_SWING_RIGHT = RawAnimation.begin().thenPlay("attack.swing.right");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
//    public static final RawAnimation IDLE_SWIM_ANIM = RawAnimation.begin().thenLoop("goomba.idle_swim");
//    public static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("goomba.run");
//    public static final RawAnimation SCARE_ANIM = RawAnimation.begin().thenLoop("goomba.scared");
//    public static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("goomba.sit");
//    public static final RawAnimation SLEEP_ANIM = RawAnimation.begin().thenLoop("goomba.sleep");
//    public static final RawAnimation SQUASH_ANIM = RawAnimation.begin().thenPlayAndHold("goomba.squash");
//    public static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("goomba.swim");
//    public static final RawAnimation SWIM_SQUASH_ANIM = RawAnimation.begin().thenPlayAndHold("goomba.swim_squash");
//    public static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("goomba.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public boolean wasSleeping;

    public KoopaTroopaEntity(EntityType<? extends KoopaTroopaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.WATER, 2.0F);
        this.moveControl = new AmphibiousMoveControl(this, 85, 10, 0.6F, 1.0F, true);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(1);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.GOOMBA_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.GOOMBA_STOMP.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundRegistry.GOOMBA_STEP.get(), 1.0F, 1.0F);
    }

    protected SoundEvent getBumpSound() {
        return SoundRegistry.GOOMBA_BUMP.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
//        builder.define(DATA_ID_RIDE_FLAGS, (byte)0);
//        builder.define(DATA_ID_SCARE_FLAGS, (byte)0);
//        builder.define(DATA_ID_SIT_FLAGS, (byte)0);
//        builder.define(DATA_ID_SLEEP_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RandomStrollGoal(this, 0.4D));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
//        this.goalSelector.addGoal(2, new GoombaSitGoal(this, 100, 1200, 3000, 300));
//        this.goalSelector.addGoal(3, new GoombaSleepGoal(this, 25, 2400, 6000));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 0.6D, true));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
//        this.goalSelector.addGoal(7, new GoombaRideGoal(this, 0.001F));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.GOOMBA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
//        controllers.add(new AnimationController<>(this, "Death", 5, this::squashAnimController));
//        controllers.add(new AnimationController<>(this, "Idle", 5, this::walkAnimController));
//        controllers.add(new AnimationController<>(this, "Run", 5, this::walkAnimController));
//        controllers.add(new AnimationController<>(this, "Scare", 5, this::scareAnimController));
//        controllers.add(new AnimationController<>(this, "Squash", 5, this::squashAnimController));
//        controllers.add(new AnimationController<>(this, "Swim", 15, this::walkAnimController));
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkAnimation));
        controllers.add(DefaultAnimations.genericIdleController(this));
        controllers.add(DefaultAnimations.genericWalkController(this));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, this.isLeftHanded() ? ATTACK_SWING_LEFT : ATTACK_SWING_RIGHT).transitionLength(1));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimation(final AnimationState<E> event) {
        /*if (this.isInWaterOrBubble()) {
            if (!this.isRunning() && !this.isWalking())
                event.setAndContinue(IDLE_SWIM_ANIM);
            else event.setAndContinue(SWIM_ANIM);
            return PlayState.CONTINUE;
        } else if (this.isRunning()) {
            event.setAndContinue(RUN_ANIM);
            return PlayState.CONTINUE;
        } else*/ if (event.isMoving()) {
            event.setAndContinue(WALK);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE);
            return PlayState.CONTINUE;
        }
    }

//    protected <E extends GeoAnimatable> PlayState squashAnimController(final AnimationState<E> event) {
//        if (this.dead) {
//            if (this.getLastDamageSource() != null
//                && (this.getLastDamageSource().is(DamageTypeRegistry.STOMP)
//                    || this.getLastDamageSource().is(DamageTypeRegistry.PLAYER_STOMP))) {
//                if (this.isInWaterOrBubble()) {
//                    if (!this.isRunning() && !this.isWalking())
//                        event.setAndContinue(SQUASH_ANIM);
//                    else event.setAndContinue(SWIM_SQUASH_ANIM);
//                } else event.setAndContinue(SQUASH_ANIM);
//                return PlayState.CONTINUE;
//            } else {
//                event.setAndContinue(DEATH_ANIM);
//                return PlayState.CONTINUE;
//            }
//        }
//        return PlayState.STOP;
//    }
//
//    protected <E extends GeoAnimatable> PlayState scareAnimController(final AnimationState<E> event) {
//        if (this.isScared()) {
//            event.setAndContinue(SCARE_ANIM);
//            return PlayState.CONTINUE;
//        }
//        return PlayState.STOP;
//    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
//        this.entityData.set(DATA_ID_RIDE_FLAGS, tag.getByte("RideFlags"));
//        this.entityData.set(DATA_ID_SCARE_FLAGS, tag.getByte("ScareFlags"));
//        this.entityData.set(DATA_ID_SIT_FLAGS, tag.getByte("SitFlags"));
//        this.entityData.set(DATA_ID_SLEEP_FLAGS, tag.getByte("SleepFlags"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
//        tag.putByte("RideFlags", this.entityData.get(DATA_ID_RIDE_FLAGS));
//        tag.putByte("ScareFlags", this.entityData.get(DATA_ID_SCARE_FLAGS));
//        tag.putByte("SitFlags", this.entityData.get(DATA_ID_SIT_FLAGS));
//        tag.putByte("SleepFlags", this.entityData.get(DATA_ID_SLEEP_FLAGS));
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
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        this.handleAirSupply(i);
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
        if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int i = random.nextInt(6);
            int randomInt = random.nextInt(1);
            if (i == 0) {
                if (randomInt == 0)
                    this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.MARIO_FIRE_HAT.get()));
                else this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.LUIGI_FIRE_HAT.get()));
                this.getPersistentData().putBoolean("marioverse:has_fire_flower", Boolean.TRUE);
            } else if (i == 1) {
                if (randomInt == 0)
                    this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.MARIO_ICE_HAT.get()));
                else this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.LUIGI_ICE_HAT.get()));
                this.getPersistentData().putBoolean("marioverse:has_ice_flower", Boolean.TRUE);
            } else {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
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
        if (/*this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && */(player.getItemInHand(hand).getItem() instanceof ArmorItem
                || (player.getItemInHand(hand).getItem() instanceof BlockItem blockItem
                    && (blockItem.getBlock() instanceof SkullBlock
                        || blockItem.getBlock() instanceof EquipableCarvedPumpkinBlock
                        || blockItem.getBlock() instanceof CarvedPumpkinBlock)))) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            player.swing(hand);
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
        if (random.nextFloat() < 0.9F && this.getItemBySlot(EquipmentSlot.FEET).isEmpty())
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));

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

    public static boolean checkKoopaSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
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

    public static class GoombaGroupData implements SpawnGroupData {
        public final boolean canSpawnJockey;

        public GoombaGroupData(boolean canSpawnJockey) {
            this.canSpawnJockey = canSpawnJockey;
        }
    }
}
