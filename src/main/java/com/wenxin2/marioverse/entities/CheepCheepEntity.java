package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.FishSwimGoal;
import com.wenxin2.marioverse.entities.ai.goals.JumpOutOfWaterGoal;
import com.wenxin2.marioverse.entities.ai.goals.StopFollowFlockLeaderGoal;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

public class CheepCheepEntity extends AbstractSchoolingFish implements GeoEntity {
    private static final EntityDataAccessor<String> SIZE = SynchedEntityData
            .defineId(CheepCheepEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> VARIANT = SynchedEntityData
            .defineId(CheepCheepEntity.class, EntityDataSerializers.STRING);
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public static final RawAnimation SWIM_FAST = RawAnimation.begin().thenLoop("move.swim_fast");
    public int attackCooldown = 0;

    public CheepCheepEntity(EntityType<? extends CheepCheepEntity> type, Level world) {
        super(type, world);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.xpReward = 2;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 360;
    }

    @NotNull
    @Override
    protected SoundEvent getFlopSound() {
        return SoundEvents.TROPICAL_FISH_FLOP; // TODO
    }

    @Nullable
    protected SoundEvent getJumpSound() {
        return SoundEvents.DOLPHIN_JUMP; // TODO
    }

    @NotNull
    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ItemRegistry.CHEEP_CHEEP_BUCKET.get());
    }

    @NotNull
    public DamageSource getDamageSource(Entity collidingEntity) {
        return DamageSourceRegistry.cheepCheepBite(collidingEntity);
    }

    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.CHEEP_CHEEP_CAN_ATTACK;
    }

    public double getLureRadius() {
        return ConfigRegistry.CHEEP_CHEEP_LURE_RADIUS.get();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new JumpOutOfWaterGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 10, this.getJumpSound()));
        this.goalSelector.addGoal(3, new FishSwimGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 1.0, 20, false));
        this.goalSelector.addGoal(4, new StopFollowFlockLeaderGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "swim_fast", 5, this::swimAnimation));
        controllers.add(DefaultAnimations.genericAttackAnimation(this, DefaultAnimations.ATTACK_BITE).transitionLength(1));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected <E extends GeoAnimatable> PlayState swimAnimation(final AnimationState<E> event) {
        if (this.isInWaterOrBubble()) {
            event.setAndContinue(SWIM_FAST);
            return PlayState.CONTINUE;
        } else return PlayState.CONTINUE;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Size", this.getSize().toString());
        tag.putString("Variant", this.getVariant().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Size"))
            this.setSize(ResourceLocation.parse(tag.getString("Size")));
        if (tag.contains("Variant"))
            this.setVariant(ResourceLocation.parse(tag.getString("Variant")));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, CheepCheepVariants.NORMAL.toString());
        builder.define(VARIANT, CheepCheepVariants.NORMAL.toString());
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack,
                tag -> tag.putString("Variant", this.getVariant().toString()));
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack,
                tag -> tag.putString("Size", this.getSize().toString()));
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        super.loadFromBucketTag(tag);

        if (tag.contains("Size", Tag.TAG_STRING))
            this.setSize(ResourceLocation.parse(tag.getString("Size")));
        if (tag.contains("Variant", Tag.TAG_STRING))
            this.setVariant(ResourceLocation.parse(tag.getString("Variant")));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackCooldown > 0)
            this.attackCooldown--;
    }

    @Override
    public void doPush(Entity entity) {
        super.doPush(entity);

        if (this.isAlive() && !this.isAlliedTo(entity) && this.attackCooldown == 0
                && entity.getType().is(this.getCanAttackTag()) && this.level().getDifficulty() != Difficulty.PEACEFUL) {
            float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if (entity instanceof Creeper)
                entity.hurt(this.getDamageSource(entity), attackDamage);
            else entity.hurt(this.getDamageSource(this), attackDamage);

            this.swing(this.getUsedItemHand());
            this.attackCooldown = 20;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        Holder<Biome> biome = level.getBiome(blockPosition());
        RandomSource random = level.getRandom();
        float chance = random.nextFloat();

        if (biome.is(TagRegistry.HAS_COLD_CHEEP_CHEEP))
            this.setVariant(CheepCheepVariants.COLD);
        else if (biome.is(TagRegistry.HAS_WARM_CHEEP_CHEEP))
            this.setVariant(CheepCheepVariants.WARM);
        else this.setVariant(CheepCheepVariants.NORMAL);

        if (chance < 0.15F)
            this.setSize(CheepCheepVariants.LARGE);
        else if (chance < 0.50F)
            this.setSize(CheepCheepVariants.SMALL);
        else this.setSize(CheepCheepVariants.NORMAL);

        if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            if (random.nextFloat() < 0.01F)
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.TURTLE_HELMET));
            else if (random.nextFloat() < 0.015F)
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            else if (random.nextFloat() < 0.05F)
                this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
        }

        return data;
    }

    public static boolean checkCheepCheepSpawnRules(EntityType<CheepCheepEntity> entityType, LevelAccessor levelAccessor,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getFluidState(pos.below()).is(FluidTags.WATER)
                && levelAccessor.getBlockState(pos.above()).is(Blocks.WATER)
                && WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, spawnType, pos, random);
    }

    public static boolean checkCheepCheepLavaSpawnRules(EntityType<CheepCheepEntity> entityType, LevelAccessor levelAccessor,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getFluidState(pos.below()).is(FluidTags.LAVA)
                && levelAccessor.getBlockState(pos.above()).is(Blocks.LAVA)
                && (levelAccessor.getBiome(pos).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT)
                        || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, spawnType, pos, random));
    }

    public ResourceLocation getSize() {
        return ResourceLocation.parse(this.entityData.get(SIZE));
    }

    public void setSize(ResourceLocation variant) {
        this.entityData.set(SIZE, variant.toString());
    }

    public ResourceLocation getVariant() {
        return ResourceLocation.parse(this.entityData.get(VARIANT));
    }

    public void setVariant(ResourceLocation variant) {
        this.entityData.set(VARIANT, variant.toString());
    }
}