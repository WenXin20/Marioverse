package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.controls.ConfigurableSmoothSwimmingMoveControl;
import com.wenxin2.marioverse.entities.ai.goals.AvoidEntityTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.FishSwimGoal;
import com.wenxin2.marioverse.entities.ai.goals.JumpOutOfWaterGoal;
import com.wenxin2.marioverse.entities.ai.goals.StopFollowFlockLeaderGoal;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public class EepCheepEntity extends CheepCheepEntity implements GeoEntity {
    private static final EntityDataAccessor<String> SIZE = SynchedEntityData
            .defineId(EepCheepEntity.class, EntityDataSerializers.STRING);

    public EepCheepEntity(EntityType<? extends EepCheepEntity> type, Level world) {
        super(type, world);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.moveControl = new ConfigurableSmoothSwimmingMoveControl(this, 85, 10,
                1.5F, 0.1F, 1.2F, true);
        this.xpReward = 2;
    }

    @NotNull
    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ItemRegistry.EEP_CHEEP_BUCKET.get());
    }

    @NotNull
    public DamageSource getDamageSource(Entity collidingEntity) {
        return DamageSourceRegistry.eepCheepBite(collidingEntity);
    }

    @Override
    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.EEP_CHEEP_CAN_ATTACK;
    }

    @Override
    public TagKey<EntityType<?>> getAvoidEntityTag() {
        return TagRegistry.EEP_CHEEP_AVOIDS;
    }

    @Override
    public double getLureRadius() {
        return ConfigRegistry.EEP_CHEEP_LURE_RADIUS.get();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(2, new JumpOutOfWaterGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 10, false, this.getJumpSound()));
        this.goalSelector.addGoal(3, new AvoidEntityTagGoal(this, this.getAvoidEntityTag(),
                6.0F, 1.2, 2.0));
        this.goalSelector.addGoal(4, new FishSwimGoal(this, this.getCanAttackTag(),
                this.getLureRadius(), 1.0, 20, true));
        this.goalSelector.addGoal(5, new StopFollowFlockLeaderGoal(this));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Size", this.getSize());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Size"))
            this.setSize(tag.getString("Size"));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, CheepCheepVariants.REGULAR);
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack,
                tag -> tag.putString("Size", this.getSize()));
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        super.loadFromBucketTag(tag);

        if (tag.contains("Size", Tag.TAG_STRING))
            this.setSize(tag.getString("Size"));
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        RandomSource random = level.getRandom();
        float chance = random.nextFloat();

        if (chance < 0.15F)
            this.setSize(CheepCheepVariants.LARGE);
        else if (chance < 0.50F)
            this.setSize(CheepCheepVariants.SMALL);
        else this.setSize(CheepCheepVariants.REGULAR);

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

    public static boolean checkEepCheepSpawnRules(EntityType<EepCheepEntity> entityType, LevelAccessor levelAccessor,
                                                         MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getFluidState(pos.below()).is(FluidTags.WATER)
                && levelAccessor.getBlockState(pos.above()).is(Blocks.WATER)
                && WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, spawnType, pos, random);
    }

    public String getSize() {
        return this.entityData.get(SIZE);
    }

    public void setSize(String variant) {
        this.entityData.set(SIZE, variant);
    }
}