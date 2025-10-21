package com.wenxin2.marioverse.entities;

import com.mojang.authlib.GameProfile;
import com.wenxin2.marioverse.entities.ai.controls.FloatMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.ChargeAttackGoal;
import com.wenxin2.marioverse.entities.ai.goals.FreezeWhenLookedAt;
import com.wenxin2.marioverse.entities.ai.goals.LookAtTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.RandomMoveGoal;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
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

public class BooEntity extends Monster implements GeoEntity {
    public static final RawAnimation ATTACK_SWING_LEFT = RawAnimation.begin().thenPlay("attack.swing.left");
    public static final RawAnimation ATTACK_SWING_RIGHT = RawAnimation.begin().thenPlay("attack.swing.right");
    public static final RawAnimation CHARGE = RawAnimation.begin().thenLoop("boo.charge");
    public static final RawAnimation HIDE = RawAnimation.begin().thenLoop("boo.hide");
    public static final RawAnimation IDLE = RawAnimation.begin().thenLoop("boo.idle");
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable private BlockPos boundOrigin;

    public BooEntity(EntityType<? extends BooEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.moveControl = new FloatMoveControl(this);
    }

    @Override
    protected int getBaseExperienceReward() {
        return 2 + this.level().random.nextInt(1);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundRegistry.SPLUNKIN_CRACKS.get();
    } // TODO

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.SPLUNKIN_DEATH.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FreezeWhenLookedAt(this, TagRegistry.BOO_CAN_ATTACK));
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ChargeAttackGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(4, new RandomMoveGoal(this));
        this.goalSelector.addGoal(5, new LookAtTagGoal(this, TagRegistry.BOO_CAN_ATTACK, 16.0F, 1.0F));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, TagRegistry.BOO_CAN_ATTACK, false));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Idle", 5, this::animController));
        controllers.add(new AnimationController<>(this, "Run", 5, this::animController));
        controllers.add(new AnimationController<>(this, "Swim", 15, this::animController));
        controllers.add(new AnimationController<>(this, "Walk", 5, this::animController));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, this.getRandom().nextFloat() < 0.25
                        ? DefaultAnimations.ATTACK_BITE : this.isLeftHanded() ? ATTACK_SWING_LEFT : ATTACK_SWING_RIGHT).transitionLength(1));
        controllers.add(DefaultAnimations.genericWalkController(this));
    }

    protected <E extends GeoAnimatable> PlayState animController(final AnimationState<E> event) {
        if (this.getData(DataAttachmentRegistry.IS_HIDING.get())) {
            event.setAndContinue(HIDE);
            return PlayState.CONTINUE;
        } else if (this.getData(DataAttachmentRegistry.IS_ATTACKING.get())
                || this.getData(DataAttachmentRegistry.IS_CHARGING.get())) {
            event.setAndContinue(CHARGE);
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
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("BoundX"))
            this.boundOrigin = new BlockPos(tag.getInt("BoundX"), tag.getInt("BoundY"), tag.getInt("BoundZ"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (this.boundOrigin != null) {
            tag.putInt("BoundX", this.boundOrigin.getX());
            tag.putInt("BoundY", this.boundOrigin.getY());
            tag.putInt("BoundZ", this.boundOrigin.getZ());
        }
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if (this.isInWaterOrBubble())
            this.ejectPassengers();
        if (!this.level().isClientSide && !this.isNoAi() && !this.getData(DataAttachmentRegistry.HAS_SUPER_STAR.get())) {
            if (this.level().getBrightness(LightLayer.SKY, this.blockPosition()) >= 8 && this.level().isDay()) {
                this.playDeathAnimation(this);
                this.discard();
            }

            if (this.level().getBrightness(LightLayer.BLOCK, this.blockPosition()) >= 8) {
                this.playDeathAnimation(this);
                this.kill();
            }
        }
    }

    @Override
    public void baseTick() {
        int i = this.getAirSupply();

        super.baseTick();
        this.handleAirSupply(i);
    }

    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isControlledByLocalInstance() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), travelVector);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9));
        } else super.travel(travelVector);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(TagRegistry.BYPASSES_BOO_INVULNERABILITY))
            super.hurt(source, amount);
        return false;
    }

    @Override
    public void die(DamageSource source) {
        this.playDeathAnimation(this);
        super.die(source);
    }

    @Override
    public boolean canTakeItem(ItemStack stack) {
        EquipmentSlot equipmentslot = this.getEquipmentSlotForItem(stack);
        return this.getItemBySlot(equipmentslot).isEmpty();
    }

    @NotNull // TODO: remove
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

        if (random.nextFloat() < 0.05F && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
        else if (random.nextFloat() < 0.15F && this.getItemBySlot(EquipmentSlot.HEAD).isEmpty())
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));


        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int day = localDate.getDayOfMonth();
            int month = localDate.getMonth().getValue();
            List<ServerPlayer> players = serverWorld.getLevel().players();

            if ((month == 10 && day == 31 && !ConfigRegistry.DISABLE_BOO_MASKS.get())
                    || ConfigRegistry.FORCE_BOO_MASKS.get()) {
                if (random.nextFloat() < 0.25F)
                    this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F
                            ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                //TODO: add tag for extra blocks

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

    public static boolean checkBooSpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    @Nullable
    public BlockPos getBoundOrigin() {
        return this.boundOrigin;
    }

    public void setBoundOrigin(@Nullable BlockPos pos) {
        this.boundOrigin = pos;
    }

    public boolean isLookingAtMe(LivingEntity entity) {
        Vec3 vec3 = entity.getViewVector(1.0F).normalize();
        Vec3 vec31 = new Vec3(this.getX() - entity.getX(),
                this.getEyeY() - entity.getEyeY(), this.getZ() - entity.getZ());
        double d0 = vec31.length();
        vec31 = vec31.normalize();
        double d1 = vec3.dot(vec31);

        return d1 > 1.0 - 0.025 / d0 ? entity.hasLineOfSight(this) : false;
    }

    protected void handleAirSupply(int airSupplyAmount) {
        if (this.isAlive() && this.isInWaterOrBubble())
            this.setAirSupply(airSupplyAmount);
    }

    @NotNull
    public ParticleOptions getDeathParticle() {
        return ParticleTypes.POOF;
    }

    public void playDeathAnimation(Entity entity) {
        if (entity.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnParticlesOnEntityRandomly(this.getDeathParticle(), serverWorld, entity, 0.0, 15);
    }
}
