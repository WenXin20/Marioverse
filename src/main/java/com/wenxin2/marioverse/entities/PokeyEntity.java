package com.wenxin2.marioverse.entities;

import com.mojang.authlib.GameProfile;
import com.wenxin2.marioverse.entities.ai.goals.LookAtTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
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

public class PokeyEntity extends Monster implements GeoEntity, NeutralMob {
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    public static final RawAnimation WALK_INVERSE = RawAnimation.begin().thenLoop("move.walk_inverse");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable private UUID persistentAngerTarget;
    private RawAnimation currentAnimation = null;
    public int attackCooldown = 0;

    public PokeyEntity(EntityType<? extends PokeyEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.xpReward = 2;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.BOO_LAUGH.get();
    } // TODO

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.BOO_HURT.get();
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.BOO_DEATH.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 360;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(2, new LookAtTagGoal(this, TagRegistry.GREEN_KOOPA_TROOPA_CAN_ATTACK, 8.0F, 1.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.GREEN_KOOPA_TROOPA_CAN_ATTACK, true)); // TODO
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkController));
    }

    protected <E extends GeoAnimatable> PlayState walkController(final AnimationState<E> event) {
        LivingEntity bottomPokey = this.getBottomSegment();
        boolean isBottom = (bottomPokey == this);

        if (isBottom) {
            if (this.getDeltaMovement().horizontalDistance() > 0.01) {
                event.setAndContinue(WALK);
                this.setCurrentAnimation(WALK);
                return PlayState.CONTINUE;
            } else this.setCurrentAnimation(null);
            return PlayState.STOP;
        }

        if (this.getVehicle() instanceof PokeyEntity pokeyVehicle
                && bottomPokey.getDeltaMovement().horizontalDistance() > 0.01) {
            if (pokeyVehicle.getCurrentAnimation() == WALK) {
                this.setCurrentAnimation(WALK_INVERSE);
                event.setAndContinue(WALK_INVERSE);
                return PlayState.CONTINUE;
            } else if (pokeyVehicle.getCurrentAnimation() == WALK_INVERSE) {
                this.setCurrentAnimation(WALK);
                event.setAndContinue(WALK);
                return PlayState.CONTINUE;
            }
        } else this.setCurrentAnimation(null);

        this.setCurrentAnimation(null);
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public RawAnimation getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(RawAnimation anim) {
        this.currentAnimation = anim;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.addPersistentAngerSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.readPersistentAngerSaveData(this.level(), tag);
    }

    @Override
    public void tick() {
        super.tick();
        this.pokeEntity();

        if (this.attackCooldown > 0)
            this.attackCooldown--;

        LivingEntity head = this.getHeadSegment();
        if (this.isPassenger() && this == head) {
            LivingEntity bottom = this.getBottomSegment();

            this.setYHeadRot(bottom.getYHeadRot());
            this.yHeadRotO = bottom.yHeadRotO;
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide() && super.hurt(source, amount)) {
            Entity attacker = source.getEntity();

            if (attacker instanceof LivingEntity livingEntity) {
                LivingEntity headEntity = this.getHeadSegment();

                if (headEntity != this && headEntity instanceof NeutralMob neutral) {
                    neutral.setPersistentAngerTarget(livingEntity.getUUID());
                    neutral.startPersistentAngerTimer();
                    neutral.setTarget(livingEntity);
                }
            }
        }
        return super.hurt(source, amount);
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);

        Entity vehicle = this.getVehicle();

        while (vehicle instanceof PokeyEntity) {
            Entity next = vehicle.getVehicle();
            vehicle.kill();
            vehicle = next;
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
        Entity bottom = this.getBottomSegment();

        if (bottom == this) {
            super.knockback(strength, x, z);
            return;
        }

        if (bottom instanceof LivingEntity livingEntity)
            livingEntity.knockback(strength, x, z);
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.isInWater() // Copied from FloatGoal
                && this.getFluidHeight(FluidTags.WATER) > this.getFluidJumpThreshold()) {
            if (this.getRandom().nextFloat() < 0.8F)
                this.getJumpControl().jump();
            this.getNavigation().setCanFloat(true);
        }
        super.travel(travelVector);
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setRemainingPersistentAngerTime(int angerTime) {
        this.remainingPersistentAngerTime = angerTime;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID angerTarget) {
        this.persistentAngerTarget = angerTarget;
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Nullable
    @Override
    public ItemStack getPickedResult(@NotNull HitResult target) {
        return new ItemStack(ItemRegistry.POKEY_SPAWN_EGG.get());
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

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);

        if (!(this instanceof PokeyBodyEntity)) {
            if (random.nextFloat() < 0.015F && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            else if (random.nextFloat() < 0.05F && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData groupData) {
        RandomSource random = serverWorld.getRandom();
        this.populateDefaultEquipmentSlots(random, difficulty);
        this.populateDefaultEquipmentEnchantments(serverWorld, random, difficulty);

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int day = localDate.getDayOfMonth();
            int month = localDate.getMonth().getValue();
            List<ServerPlayer> players = serverWorld.getLevel().players();

            boolean isHalloween = (month == 10 && day >= 30 && !ConfigRegistry.DISABLE_BOO_MASKS.get()); // TODO
            boolean forceMasks = ConfigRegistry.FORCE_BOO_MASKS.get();

            Optional<Item> randomMask = BuiltInRegistries.ITEM
                    .getTag(TagRegistry.HALLOWEEN_MASKS)
                    .flatMap(tag -> tag.getRandomElement(random))
                    .map(Holder::value);

            if (isHalloween || forceMasks) {
                boolean appliedMask = false;

                if (random.nextFloat() < 0.25F) {
                    randomMask.ifPresent(item -> this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(item)));
                    appliedMask = this.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.HALLOWEEN_MASKS);
                }

                if (random.nextFloat() < 0.15F) {
                    List<ItemStack> skulls = new ArrayList<>();
                    serverWorld.registryAccess().registryOrThrow(Registries.ITEM)
                            .getTagOrEmpty(ItemTags.SKULLS)
                            .forEach(holder -> {
                                Item item = holder.value();
                                skulls.add(new ItemStack(item));
                            });

                    if (!skulls.isEmpty()) {
                        ItemStack randomSkull = skulls.get(random.nextInt(skulls.size()));
                        if (randomSkull.getItem() instanceof PlayerHeadItem && !players.isEmpty()) {
                            ServerPlayer randomPlayer = players.get(random.nextInt(players.size()));
                            GameProfile playerProfile = randomPlayer.getGameProfile();
                            ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD);
                            playerHead.set(DataComponents.PROFILE, new ResolvableProfile(playerProfile));
                            this.setItemSlot(EquipmentSlot.HEAD, playerHead);
                        } else {
                            this.setItemSlot(EquipmentSlot.HEAD, randomSkull);
                        }
                        appliedMask = true;
                    }
                }

                if (random.nextFloat() < 0.1F) {
                    if (!players.isEmpty()) {
                        ServerPlayer randomPlayer = players.get(random.nextInt(players.size()));
                        GameProfile playerProfile = randomPlayer.getGameProfile();
                        SkullBlockEntity.fetchGameProfile(randomPlayer.getUUID());
                        ItemStack playerHeadItem = new ItemStack(Items.PLAYER_HEAD);

                        playerHeadItem.set(DataComponents.PROFILE, new ResolvableProfile(playerProfile));

                        this.setItemSlot(EquipmentSlot.HEAD, playerHeadItem);
                        appliedMask = true;
                    }
                }

                if (random.nextFloat() < 0.05F) {
                    List<ItemStack> skulls = new ArrayList<>();
                    serverWorld.registryAccess().registryOrThrow(Registries.BLOCK)
                            .getTagOrEmpty(CompatRegistry.TF_TROPHIES)
                            .forEach(holder -> skulls.add(new ItemStack(holder.value())));

                    if (!skulls.isEmpty()) {
                        ItemStack randomTrophy = skulls.get(random.nextInt(skulls.size()));
                        this.setItemSlot(EquipmentSlot.HEAD, randomTrophy);
                        appliedMask = true;
                    }
                }

                if (appliedMask)
                    this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
            }
        }
        return super.finalizeSpawn(serverWorld, difficulty, spawnType, groupData);
    }

    public static boolean checkPokeySpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    public void pokeEntity() {
        if (this.attackCooldown > 0)
            return;

        List<Entity> nearbyEntities = this.level().getEntities(this,
                this.getBoundingBox().inflate(0.01, 0.0, 0.01), entity -> !entity.isSpectator()
                        && entity instanceof LivingEntity && !(entity instanceof PiranhaPlantEntity)
                        && !this.level().isClientSide());

        if (!nearbyEntities.isEmpty()) {
            for (Entity collidingEntity : nearbyEntities) {
                if (collidingEntity instanceof PokeyEntity)
                    continue;

                if (collidingEntity.getType().is(EntityTypeTags.SENSITIVE_TO_IMPALING)) // TODO
                    continue;

                if (collidingEntity.isSpectator() || collidingEntity instanceof Player player && player.isCreative()) // TODO
                    continue;


                float attackDamage = this.isBaby() ? (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2
                        : (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

                if (collidingEntity instanceof Creeper)
                    collidingEntity.hurt(DamageSourceRegistry.piranhaChomp(null, collidingEntity), attackDamage); // TODO
                else collidingEntity.hurt(DamageSourceRegistry.piranhaChomp(null, this), attackDamage);

                if (collidingEntity instanceof NeutralMob neutralMob) {
                    neutralMob.isAngryAt(this);
                    neutralMob.setTarget(this);
                    neutralMob.setPersistentAngerTarget(this.getUUID());
                }

                this.swing(InteractionHand.MAIN_HAND);
                this.playSound(SoundRegistry.PIRANHA_PLANT_CHOMP.get(), 1.0F, 1.0F); // TODO
                this.attackCooldown = 20;
                break;
            }
        }
    }

    @Nullable
    public LivingEntity getHeadSegment() {
        LivingEntity current = this;

        while (current.getFirstPassenger() instanceof LivingEntity livingEntity)
            current = livingEntity;
        if (current instanceof PokeyBodyEntity)
            return null;
        return current;
    }

    public LivingEntity getBottomSegment() {
        LivingEntity current = this;

        while (current.getVehicle() instanceof LivingEntity livingEntity)
            current = livingEntity;
        return current;
    }

    public List<PokeyEntity> getEntireStack() {
        List<PokeyEntity> result = new ArrayList<>();
        PokeyEntity bottom = this;

        while (bottom.getVehicle() instanceof PokeyEntity pe)
            bottom = pe;

        PokeyEntity current = bottom;
        while (current != null) {
            result.add(current);
            current = current.getPassengers().stream()
                    .filter(p -> p instanceof PokeyEntity)
                    .map(p -> (PokeyEntity) p)
                    .findFirst()
                    .orElse(null);
        }
        return result;
    }

}