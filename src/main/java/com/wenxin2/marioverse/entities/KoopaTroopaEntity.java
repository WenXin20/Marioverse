package com.wenxin2.marioverse.entities;

import com.mojang.authlib.GameProfile;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
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
    private static final EntityDataAccessor<Byte> DATA_ID_HIDE_FLAGS = SynchedEntityData.defineId(KoopaTroopaEntity.class, EntityDataSerializers.BYTE);
    public static final RawAnimation ATTACK_SWING_LEFT = RawAnimation.begin().thenPlay("attack.swing.left");
    public static final RawAnimation ATTACK_SWING_RIGHT = RawAnimation.begin().thenPlay("attack.swing.right");
    public static final RawAnimation HIDE = RawAnimation.begin().thenPlayAndHold("move.hide");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int hideTicks = -1;
    public int hideAnimationTicks = 0;

    public KoopaTroopaEntity(EntityType<? extends KoopaTroopaEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.setPathfindingMalus(PathType.DANGER_FIRE, 16.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 1 + this.level().random.nextInt(1);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.KOOPA_TROOPA_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.KOOPA_TROOPA_DEATH.get();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.KOOPA_TROOPA_AMBIENT.get();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ID_HIDE_FLAGS, (byte)0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.GREEN_KOOPA_TROOPA_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "walk", 5, this::walkAnimation));
        controllers.add(new AnimationController<>(this, "hide_controller", 5, state -> PlayState.STOP)
                .triggerableAnim("hide", HIDE));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, this.isLeftHanded() ? ATTACK_SWING_LEFT : ATTACK_SWING_RIGHT)
                .transitionLength(1));
    }

    protected <E extends GeoAnimatable> PlayState walkAnimation(final AnimationState<E> event) {
        if (!this.isHiding()) {
            if (event.isMoving() || this.getDeltaMovement().horizontalDistance() >= 0.01) {
                event.setAndContinue(WALK);
                return PlayState.CONTINUE;
            } else {
                event.setAndContinue(IDLE);
                return PlayState.CONTINUE;
            }
        } else return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putByte("HideFlags", this.entityData.get(DATA_ID_HIDE_FLAGS));
        tag.putInt("HideTicks", this.hideTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_ID_HIDE_FLAGS, tag.getByte("HideFlags"));
        this.hideTicks = tag.getInt("HideTicks");
    }

    @Override
    public void tick() {
        super.tick();

        if (hideTicks > 0 && this.getDeltaMovement().horizontalDistance() == 0)
            hideTicks--;

        if (!this.level().isClientSide && hideAnimationTicks > 0) {
            hideAnimationTicks--;
            this.spawnKoopaShell(this.getHealth(), this.getHideDuration(), 0, true, true);
        }
    }

    @Override
    public void aiStep() {
        if (!this.isHiding())
            super.aiStep();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypeRegistry.STOMP) || source.is(DamageTypeRegistry.PLAYER_STOMP)) {
            this.hide(Boolean.TRUE);
            this.getNavigation().stop();
            this.setXxa(0.0F);
            this.setSpeed(0.0F);
            this.hideTicks = this.getHideDuration();
            this.hideAnimationTicks = 15;
            this.triggerAnim("hide_controller", "hide");
            this.level().playSound(null, this.blockPosition(), SoundRegistry.KOOPA_TROOPA_STOMP.get(), SoundSource.HOSTILE, 1.0F, 1.0F);
        }
        return super.hurt(source, amount);
    }

    @Override
    protected void triggerOnDeathMobEffects(RemovalReason reason) {
        if (this.level() instanceof ServerLevel && this.getRandom().nextFloat() < 0.25f
                && this.getRemovalReason() == RemovalReason.KILLED)
            this.spawnKoopaShell(this.getMaxHealth(), -1, -1, false, false);

        super.triggerOnDeathMobEffects(reason);
    }

    @Override
    public int getCurrentSwingDuration() {
        if (MobEffectUtil.hasDigSpeed(this)) {
            return 10 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        } else {
            return this.hasEffect(MobEffects.DIG_SLOWDOWN) ? 10 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2 : 10;
        }
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
        if (this.canTakeItem(player.getItemInHand(hand))) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            return InteractionResult.SUCCESS;
        } else return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData) {
        RandomSource random = serverWorld.getRandom();

        if (groupData instanceof KoopaGroupData koopaGroupData) {
            this.populateDefaultEquipmentSlots(random, difficulty);
            this.populateDefaultEquipmentEnchantments(serverWorld, random, difficulty);

            if (koopaGroupData.canSpawnJockey) {
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

        if (random.nextFloat() < 0.25F && this.getItemBySlot(EquipmentSlot.FEET).isEmpty())
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        else if (random.nextFloat() < 0.85F && this.getItemBySlot(EquipmentSlot.FEET).isEmpty())
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(this.getKoopaShoes()));

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
        return checkMonsterSpawnRules(entityType, serverWorld, spawnType, pos, random)
                && (MobSpawnType.isSpawner(spawnType) || serverWorld.canSeeSky(pos));
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

    @Override
    public boolean canBeLeashed() {
        return true;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            @Override
            public void clientTick() {
                if (!KoopaTroopaEntity.this.isHiding()) {
                    super.clientTick();
                }
            }
        };
    }

    public static class KoopaGroupData implements SpawnGroupData {
        public final boolean canSpawnJockey;

        public KoopaGroupData(boolean canSpawnJockey) {
            this.canSpawnJockey = canSpawnJockey;
        }
    }

    public boolean isHiding() {
        return hideTicks > 0;
    }

    public void hide(boolean isSitting) {
        this.setHideFlag(8, isSitting);
    }

    private boolean getHideFlag(int i) {
        return (this.entityData.get(DATA_ID_HIDE_FLAGS) & i) != 0;
    }

    private void setHideFlag(int i, boolean b) {
        byte b0 = this.entityData.get(DATA_ID_HIDE_FLAGS);
        if (b) {
            this.entityData.set(DATA_ID_HIDE_FLAGS, (byte)(b0 | i));
        } else {
            this.entityData.set(DATA_ID_HIDE_FLAGS, (byte)(b0 & ~i));
        }
    }

    @NotNull
    public KoopaShellEntity getKoopaShellEntity() {
        return new KoopaShellEntity(EntityRegistry.GREEN_KOOPA_SHELL.get(), this.level());
    }

    @NotNull
    public Item getKoopaShoes() {
        return ItemRegistry.GREEN_KOOPA_SHOES.get();
    }

    @NotNull
    public Integer getHideDuration() {
        return ConfigRegistry.KOOPA_TROOPA_HIDE_DURATION.get();
    }

    public void spawnKoopaShell(float shellHealth, int hideTicks, int emergeAnimationTicks, boolean saveArmor, boolean savePowerUp) {
        if (hideAnimationTicks == 0) {
            KoopaShellEntity entity = this.getKoopaShellEntity();

            entity.setHideTicks(hideTicks);
            entity.setPos(this.getX(), this.getY(), this.getZ());
            entity.setYRot(this.getYRot());
            entity.setXRot(this.getXRot());
            entity.yBodyRot = this.yBodyRot;
            entity.setYHeadRot(this.getYHeadRot());
            entity.setHealth(shellHealth);
            entity.emergeAnimationTicks = emergeAnimationTicks;

            this.copyAttributeWithModifiers(entity, Attributes.SAFE_FALL_DISTANCE);
            this.copyAttributeWithModifiers(entity, Attributes.SCALE);
            this.copyAttributeWithModifiers(entity, AttributesRegistry.HEIGHT_SCALE);
            this.copyAttributeWithModifiers(entity, AttributesRegistry.WIDTH_SCALE);

            if (saveArmor) {
                for (EquipmentSlot slot : EquipmentSlot.values())
                    entity.setItemSlot(slot, this.getItemBySlot(slot).copy());
            }

            if (savePowerUp) {
                entity.getPersistentData().putBoolean("marioverse:has_fire_flower",
                        this.getPersistentData().getBoolean("marioverse:has_fire_flower"));
                entity.getPersistentData().putBoolean("marioverse:has_ice_flower",
                        this.getPersistentData().getBoolean("marioverse:has_ice_flower"));
                entity.getPersistentData().putBoolean("marioverse:has_mushroom",
                        this.getPersistentData().getBoolean("marioverse:has_mushroom"));
                entity.getPersistentData().putBoolean("marioverse:has_mega_mushroom",
                        this.getPersistentData().getBoolean("marioverse:has_mega_mushroom"));
                entity.getPersistentData().putBoolean("marioverse:has_super_star",
                        this.getPersistentData().getBoolean("marioverse:has_super_star"));
                entity.getPersistentData().putInt("marioverse:super_star_cooldown",
                        this.getPersistentData().getInt("marioverse:super_star_cooldown"));

                AccessoriesCapability capability = AccessoriesCapability.get(this);
                if (capability != null && ConfigRegistry.EQUIP_COSTUMES_MOBS.get()
                        && this.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)) {
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
            }

            this.level().addFreshEntity(entity);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public void copyAttributeWithModifiers(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance fromAttr = this.getAttribute(attribute);
        AttributeInstance toAttr = entity.getAttribute(attribute);

        if (fromAttr != null && toAttr != null) {
            toAttr.setBaseValue(fromAttr.getBaseValue());
            for (AttributeModifier modifier : fromAttr.getModifiers())
                toAttr.addPermanentModifier(modifier);
        }
    }
}
