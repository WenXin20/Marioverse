package com.wenxin2.marioverse.entities;

import com.mojang.authlib.GameProfile;
import com.wenxin2.marioverse.entities.ai.goals.LookAtEntityTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.EntityRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.pathfinder.PathType;
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
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

public class DryBonesEntity extends Monster implements GeoEntity {
    public static final RawAnimation ATTACK_SWING_LEFT = RawAnimation.begin().thenPlay("attack.swing.left");
    public static final RawAnimation ATTACK_SWING_RIGHT = RawAnimation.begin().thenPlay("attack.swing.right");
    public static final RawAnimation HIDE = RawAnimation.begin().thenPlayAndHold("move.hide");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("misc.idle");
    public static final RawAnimation SIT = RawAnimation.begin().thenLoop("misc.sit");
    public static final RawAnimation WALK = RawAnimation.begin().thenLoop("move.walk");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public DryBonesEntity(EntityType<? extends DryBonesEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.xpReward = 8;
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

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.DRY_BONES_AMBIENT.get();
    }

    @Override
    public int getAmbientSoundInterval() {
        return 360;
    }

    @NotNull
    public Item getKoopaShoes() {
        return ItemRegistry.WHITE_KOOPA_SHOES.get();
    }

    @NotNull
    public SimpleParticleType getShatterParticle() {
        return ParticleTypes.POOF;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 0.6D, false));
        this.goalSelector.addGoal(2, new LookAtEntityTagGoal(this, TagRegistry.DRY_BONES_CAN_ATTACK, 8.0F, 1.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.DRY_BONES_CAN_ATTACK, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
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
        if ((this.isPassenger() && !(this.getVehicle() instanceof LivingEntity))) {
            event.setAndContinue(SIT);
            return PlayState.CONTINUE;
        }

        if (event.isMoving() || this.getDeltaMovement().horizontalDistance() >= 0.01) {
            event.setAndContinue(WALK);
            return PlayState.CONTINUE;
        } else {
            event.setAndContinue(IDLE);
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void die(DamageSource source) {
        Level level = this.level();
        Entity attacker = source.getEntity();
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (attacker instanceof LivingEntity entity) {
            ItemStack weapon = entity.getMainHandItem();
            ItemEnchantments enchantments = weapon.get(DataComponents.ENCHANTMENTS);
            if (enchantments != null) {
                for (var entry : enchantments.entrySet()) {
                    Holder<Enchantment> holder = entry.getKey();
                    if (holder.is(TagRegistry.BYPASSES_BOO_INVULNERABILITY_ENCHANTS)) {
                        int enchantLevel = entry.getIntValue();
                        if (enchantLevel > 0) {
                            super.die(source);
                            return;
                        }
                    }
                }
            }
        }

        if (!this.level().isClientSide && !this.isNoAi()
                && !source.is(TagRegistry.PREVENTS_DRY_BONES_RESURRECTION)) {
            this.spawnDryBonesPart(new DryBonesPartEntity(EntityRegistry.DRY_BONES_HEAD.get(), level),
                    "head", true, true);
            this.spawnDryBonesPart(new DryBonesPartEntity(EntityRegistry.DRY_BONES_SHELL.get(), level),
                    "shell", true, true);
            this.spawnDryBonesPart(new DryBonesPartEntity(EntityRegistry.DRY_BONES_LEFT_ARM.get(), level),
                    "left_arm", true, true);
            this.spawnDryBonesPart(new DryBonesPartEntity(EntityRegistry.DRY_BONES_LEFT_LEG.get(), level),
                    "left_leg", true, true);
            this.spawnDryBonesPart(new DryBonesPartEntity(EntityRegistry.DRY_BONES_RIGHT_ARM.get(), level),
                    "right_arm", true, true);
            this.spawnDryBonesPart(new DryBonesPartEntity(EntityRegistry.DRY_BONES_RIGHT_LEG.get(), level),
                    "right_leg", true, true);
            this.spawnDryBonesPart(new DryBonesPartEntity(EntityRegistry.DRY_BONES_TAIL.get(), level),
                    "tail", true, true);
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticlesOnEntityRandomly(this.getShatterParticle(), serverWorld,
                        this, 0.0, 15);
            if (this.getDeathSound() != null)
                this.playSound(this.getDeathSound(), 1.0F, pitch);
            this.discard();
        } else super.die(source);
    }

    @Override
    public boolean shouldDropExperience() {
        DamageSource source = this.getLastDamageSource();
        if (source != null && source.is(TagRegistry.PREVENTS_DRY_BONES_RESURRECTION))
            return true;
        return false;
    }

    @Override
    protected void dropEquipment() {
        DamageSource source = this.getLastDamageSource();
        if (source != null && source.is(TagRegistry.PREVENTS_DRY_BONES_RESURRECTION))
            return;
        super.dropEquipment();
    }

    @Override
    protected void dropFromLootTable(DamageSource source, boolean hitByPlayer) {
        if (source.is(TagRegistry.PREVENTS_DRY_BONES_RESURRECTION))
            super.dropFromLootTable(source, hitByPlayer);
    }

    @Override
    public int getCurrentSwingDuration() {
        if (MobEffectUtil.hasDigSpeed(this))
            return 10 - (1 + MobEffectUtil.getDigSpeedAmplification(this));
        else return this.hasEffect(MobEffects.DIG_SLOWDOWN) ? 10 + (1 + this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) * 2 : 10;
    }

    @NotNull
    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float height) {
        if (entity instanceof KoopaTroopaEntity || entity instanceof DryBonesEntity)
            return new Vec3(0.0D, this.getBbHeight() - 0.5D, 0.0D);
        return super.getPassengerAttachmentPoint(entity, dimensions, height);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);

        if (random.nextFloat() < 0.005F && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.TURTLE_HELMET));
        if (random.nextFloat() < 0.15F && this.getItemBySlot(EquipmentSlot.FEET).isEmpty())
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
        else if (random.nextFloat() < 0.85F && this.getItemBySlot(EquipmentSlot.FEET).isEmpty())
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(this.getKoopaShoes()));

        if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int randomPowerUpInt = random.nextInt(6);
            int randomCharacterInt = random.nextInt(1);
            if (randomPowerUpInt == 0) {
                if (randomCharacterInt == 0)
                    this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.MARIO_FIRE_HAT.get()));
                else this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.LUIGI_FIRE_HAT.get()));
                this.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, true);
            } else if (randomPowerUpInt == 1) {
                if (randomCharacterInt == 0)
                    this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.MARIO_ICE_HAT.get()));
                else this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.LUIGI_ICE_HAT.get()));
                this.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, true);
            } else {
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            }
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

            boolean isChristmas = ((month == 12 && day >= 24) || (month == 1 && day <= 6)) && !ConfigRegistry.DISABLE_CHRISTMAS_HATS.get();
            boolean forceHats = ConfigRegistry.FORCE_CHRISTMAS_HATS.get();

            if (isChristmas || forceHats) {
                boolean appliedHat = false;

                if (random.nextFloat() < 0.40F) {
                    ItemStack hat = new ItemStack(ItemRegistry.CHRISTMAS_HAT.get());
                    this.setItemSlot(EquipmentSlot.HEAD, hat);
                    appliedHat = true;
                }

                if (appliedHat)
                    this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.25F;
            }
        }

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int day = localDate.getDayOfMonth();
            int month = localDate.getMonth().getValue();
            List<ServerPlayer> players = serverWorld.getLevel().players();

            boolean isHalloween = (month == 10 && day >= 30 && !ConfigRegistry.DISABLE_MOB_MASKS.get());
            boolean forceMasks = ConfigRegistry.FORCE_MOB_MASKS.get();

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

    public static boolean checkDryBonesSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return !serverWorld.getBlockState(pos.below()).is(Blocks.NETHER_WART_BLOCK)
                && serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    public void spawnDryBonesPart(DryBonesPartEntity entity, String type, boolean saveArmor, boolean savePowerUp) {
        float randomRotation = this.random.nextFloat() * 360.0F;
        double upwardMotion = 0.1 + this.random.nextDouble() * 0.2;
        double angle = this.random.nextDouble() * Math.PI * 2;
        float width = this.getDimensions(this.getPose()).width() / 2.0F;
        double xOffset = Math.cos(angle) * width * 0.25;
        double zOffset = Math.sin(angle) * width * 0.25;
        if (type.equals("head")) {
            xOffset = xOffset * 4.0;
            zOffset = zOffset * 4.0;
        } else if (type.equals("shell")) {
            xOffset = 0;
            zOffset = 0;
        }

        entity.setOwnerUUID(this.getUUID());
        entity.setPos(this.getX(), this.getY(), this.getZ());
        entity.setYRot(randomRotation);
        entity.yBodyRot = randomRotation;
        entity.setYHeadRot(randomRotation);
        entity.setNoAi(this.isNoAi());
        entity.setInvulnerable(this.isInvulnerable());
        entity.setCustomName(this.getCustomName());
        entity.setData(DataAttachmentRegistry.DEATH_DURATION.get(), ConfigRegistry.DRY_BONES_DEATH_DURATION.get());
        entity.setPartType(type);

        entity.moveTo(this.getX(), this.getY() + 0.5, this.getZ(), randomRotation, 0.0F);
        entity.setDeltaMovement(xOffset, upwardMotion, zOffset);
        entity.move(MoverType.SELF, entity.getDeltaMovement());

        if (this.isPersistenceRequired())
            entity.setPersistenceRequired();

        this.copyAttributeWithModifiers(entity, Attributes.MAX_HEALTH);
        this.copyAttributeWithModifiers(entity, Attributes.SAFE_FALL_DISTANCE);
        this.copyAttributeWithModifiers(entity, Attributes.SCALE);
        this.copyAttributeWithModifiers(entity, AttributesRegistry.EYE_HEIGHT_SCALE);
        this.copyAttributeWithModifiers(entity, AttributesRegistry.HEIGHT_SCALE);
        this.copyAttributeWithModifiers(entity, AttributesRegistry.WIDTH_SCALE);

        if (saveArmor) {
            for (EquipmentSlot slot : EquipmentSlot.values()){
                ItemStack stack = this.getItemBySlot(slot);
                if (!stack.isEmpty() && matchesArmorPart(type, slot))
                    entity.setItemSlot(slot, stack.copy());
            }
        }

        if (savePowerUp) {
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM));
            entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, this.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER));
            entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, this.getData(DataAttachmentRegistry.HAS_ICE_FLOWER));
            entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));
            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, this.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM));
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, this.getData(DataAttachmentRegistry.HAS_SUPER_STAR));
            entity.setData(DataAttachmentRegistry.SUPER_STAR_DURATION, this.getData(DataAttachmentRegistry.SUPER_STAR_DURATION));

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
    }

    public void copyAttributeWithModifiers(LivingEntity entity, Holder<Attribute> attribute) {
        AttributeInstance originalAttribute = this.getAttribute(attribute);
        AttributeInstance newAttribute = entity.getAttribute(attribute);

        if (originalAttribute != null && newAttribute != null) {
            newAttribute.setBaseValue(originalAttribute.getBaseValue());
            for (AttributeModifier modifier : originalAttribute.getModifiers())
                newAttribute.addPermanentModifier(modifier);
        }
    }

    private boolean matchesArmorPart(String type, EquipmentSlot slot) {
        return switch (type) {
            case "head" -> slot == EquipmentSlot.HEAD;
            case "left_leg", "right_leg" -> slot == EquipmentSlot.LEGS || slot == EquipmentSlot.FEET;
            case "left_arm", "right_arm" -> slot == EquipmentSlot.OFFHAND || slot == EquipmentSlot.MAINHAND
                    || slot == EquipmentSlot.CHEST || slot == EquipmentSlot.BODY;
            default -> false;
        };
    }
}
