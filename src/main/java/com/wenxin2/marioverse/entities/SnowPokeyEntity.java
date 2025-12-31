package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.projectiles.LargeSnowballProjectile;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
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
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

public class SnowPokeyEntity extends PokeyEntity implements GeoEntity, NeutralMob, IShearable {
    public SnowPokeyEntity(EntityType<? extends SnowPokeyEntity> type, Level world) {
        super(type, world);
        this.setPathfindingMalus(PathType.DOOR_OPEN, 1.0F);
        this.xpReward = 2;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SNOW_GOLEM_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNOW_GOLEM_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SNOW_GOLEM_AMBIENT;
    }

    protected Holder<SoundEvent> getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    @Override
    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.SNOW_POKEY_CAN_ATTACK;
    }

    @Nullable
    @Override
    public ItemStack getPickedResult(@NotNull HitResult target) {
        return new ItemStack(ItemRegistry.SNOW_POKEY_SPAWN_EGG.get());
    }

    @NotNull
    @Override
    public DamageSource getDamageSource(Entity collidingEntity) {
        return DamageSourceRegistry.snowPokeyThorns(collidingEntity);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkController));
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.hasData(DataAttachmentRegistry.HAS_CARROT))
            this.setData(DataAttachmentRegistry.HAS_CARROT, true);

        if (!this.hasData(DataAttachmentRegistry.HAS_GOLDEN_CARROT))
            this.setData(DataAttachmentRegistry.HAS_GOLDEN_CARROT, false);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide && this.isAlive()) {
            if (!EventHooks.canEntityGrief(this.level(), this) || !ConfigRegistry.SNOW_POKEY_TRAIL.get())
                return;

            BlockState blockstate = Blocks.SNOW.defaultBlockState();

            for (int i = 0; i < 4; i++) {
                int j = Mth.floor(this.getX() + (double)((float)(i % 2 * 2 - 1) * 0.25F));
                int k = Mth.floor(this.getY());
                int l = Mth.floor(this.getZ() + (double)((float)(i / 2 % 2 * 2 - 1) * 0.25F));
                BlockPos blockpos = new BlockPos(j, k, l);
                if (this.level().getBlockState(blockpos).isAir() && blockstate.canSurvive(this.level(), blockpos)) {
                    this.level().setBlockAndUpdate(blockpos, blockstate);
                    this.level().gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(this, blockstate));
                }
            }
        }
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        Level world = this.level();
        ItemStack stack = player.getItemInHand(hand);

        if (stack.is(Tags.Items.TOOLS_SHEAR) && this.isShearable(player, stack, world, this.blockPosition())) {
            this.gameEvent(GameEvent.SHEAR, player);
            this.onSheared(player, stack, world, this.blockPosition());

            if (!world.isClientSide)
                stack.hurtAndBreak(1, player, getSlotForHand(hand));

            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (stack.is(Tags.Items.CROPS_CARROT) && !this.getData(DataAttachmentRegistry.HAS_CARROT)
                && !this.getData(DataAttachmentRegistry.HAS_GOLDEN_CARROT)) {
            world.playSound(null, this, SoundEvents.SNOW_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
            this.setData(DataAttachmentRegistry.HAS_CARROT, true);
            this.gameEvent(GameEvent.EQUIP, player);
            stack.consume(1, player);

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, this.blockPosition(), stack);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else if (stack.is(Items.GOLDEN_CARROT) && !this.getData(DataAttachmentRegistry.HAS_GOLDEN_CARROT)
                && !this.getData(DataAttachmentRegistry.HAS_CARROT)) {
            world.playSound(null, this, SoundEvents.SNOW_PLACE, SoundSource.PLAYERS, 1.0F, 1.0F);
            this.setData(DataAttachmentRegistry.HAS_GOLDEN_CARROT, true);
            this.gameEvent(GameEvent.EQUIP, player);
            stack.consume(1, player);

            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, this.blockPosition(), stack);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
            }
            return InteractionResult.sidedSuccess(world.isClientSide);
        } else return InteractionResult.PASS;
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 20 && !this.level().isClientSide() && !this.isRemoved()) {
            this.remove(Entity.RemovalReason.KILLED);

            if (this.getLastDamageSource() != null
                    && !this.getLastDamageSource().is(DamageTypeTags.BYPASSES_INVULNERABILITY)
                    && ConfigRegistry.SNOW_POKEY_DROPS_SNOWBALL.get()) {
                LargeSnowballProjectile snowball = new LargeSnowballProjectile(this.level(), this);
                snowball.setYRot(this.getYRot());
                this.level().addFreshEntity(snowball);
            }

            if (this.level() instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleTypes.SNOWFLAKE, serverWorld,
                        this, 0.0, 35);
                ServerParticleUtils.spawnParticlesOnEntityRandomly(ParticleRegistry.ICE_STAR.get(), serverWorld,
                        this, 0.0, 15);
            }
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            if (random.nextFloat() < 0.60F)
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemRegistry.PLASTIC_BUCKET.get()));
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverWorld, DifficultyInstance difficulty, MobSpawnType spawnType,
                                        @Nullable SpawnGroupData groupData) {
        float roll = this.getRandom().nextFloat();

        if (roll < 0.10F) {
            this.setData(DataAttachmentRegistry.HAS_GOLDEN_CARROT, true);
            this.setData(DataAttachmentRegistry.HAS_CARROT, false);
        } else if (roll < 0.8F) {
            this.setData(DataAttachmentRegistry.HAS_CARROT, true);
            this.setData(DataAttachmentRegistry.HAS_GOLDEN_CARROT, false);
        } else {
            this.setData(DataAttachmentRegistry.HAS_CARROT, false);
            this.setData(DataAttachmentRegistry.HAS_GOLDEN_CARROT, false);
        }

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
        return super.finalizeSpawn(serverWorld, difficulty, spawnType, groupData);
    }

    @Override
    public void triggerBloom() {
    }

    @Override
    public void pokeEntity() {
    }

    public static boolean checkSnowPokeySpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }

    @NotNull
    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack stack, Level world, BlockPos pos) {
        List<ItemStack> defaultDrops = super.onSheared(player, stack, world, pos);
        List<ItemStack> finalDrops = new ArrayList<>(defaultDrops);

        if (!world.isClientSide() && this.getData(DataAttachmentRegistry.HAS_CARROT)) {
            world.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
            this.setData(DataAttachmentRegistry.HAS_CARROT, false);
            this.spawnShearedDrop(world, pos, new ItemStack(Items.CARROT));
            finalDrops.add(new ItemStack(Items.CARROT));
        }

        if (!world.isClientSide() && this.getData(DataAttachmentRegistry.HAS_GOLDEN_CARROT)) {
            world.playSound(null, this, SoundEvents.SNOW_GOLEM_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
            this.setData(DataAttachmentRegistry.HAS_GOLDEN_CARROT, false);
            this.spawnShearedDrop(world, pos, new ItemStack(Items.GOLDEN_CARROT));
            finalDrops.add(new ItemStack(Items.GOLDEN_CARROT));
        }
        return finalDrops;
    }

    @Override
    public boolean isShearable(@Nullable Player player, ItemStack stack, Level world, BlockPos pos) {
        return this.isAlive()
                && (this.getData(DataAttachmentRegistry.HAS_CARROT)|| this.getData(DataAttachmentRegistry.HAS_GOLDEN_CARROT));
    }
}