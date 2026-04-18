package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.FishSwimGoal;
import com.wenxin2.marioverse.entities.ai.goals.JumpOutOfWaterGoal;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowFlockLeaderGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TryFindWaterGoal;
import net.minecraft.world.entity.animal.AbstractSchoolingFish;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CheepCheepEntity extends AbstractSchoolingFish implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public int attackCooldown = 0;

    public CheepCheepEntity(EntityType<? extends CheepCheepEntity> type, Level world) {
        super(type, world);
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
        return new ItemStack(Items.TROPICAL_FISH_BUCKET); // TODO
    }

    @NotNull
    public DamageSource getDamageSource(Entity collidingEntity) {
        return DamageSourceRegistry.cheepCheepBite(collidingEntity);
    }

    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.CHEEP_CHEEP_CAN_ATTACK;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PanicGoal(this, 1.25));
        this.goalSelector.addGoal(0, new TryFindWaterGoal(this));
        this.goalSelector.addGoal(1, new JumpOutOfWaterGoal(this, this.getCanAttackTag(),
                10.0, 5, this.getJumpSound()));
        this.goalSelector.addGoal(2, new FishSwimGoal(this, this.getCanAttackTag(),
                10.0, 4.0, 1.0, 40, false, true));
        this.goalSelector.addGoal(3, new FishSwimGoal(this, this.getCanAttackTag(),
                10.0, 4.0, 1.0, 40, true, false));
        this.goalSelector.addGoal(5, new FollowFlockLeaderGoal(this));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.attackCooldown > 0)
            this.attackCooldown--;
    }

    @Override
    public void push(Entity entity) {
        super.push(entity);

        if (this.isAlive() && this.attackCooldown == 0
                && entity.getType().is(TagRegistry.CHEEP_CHEEP_CAN_ATTACK)) {
            float attackDamage = (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE);

            if (entity instanceof Creeper)
                entity.hurt(this.getDamageSource(entity), attackDamage);
            else entity.hurt(this.getDamageSource(this), attackDamage);

            this.swing(this.getUsedItemHand());
            this.attackCooldown = 20;
        }
    }

    public static boolean checkCheepCheepSpawnRules(EntityType<CheepCheepEntity> entityType, LevelAccessor levelAccessor,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getFluidState(pos.below()).is(FluidTags.WATER)
                && levelAccessor.getBlockState(pos.above()).is(Blocks.WATER)
                && (levelAccessor.getBiome(pos).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT)
                        || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, spawnType, pos, random));
    }

    public static boolean checkCheepCheepLavaSpawnRules(EntityType<CheepCheepEntity> entityType, LevelAccessor levelAccessor,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getFluidState(pos.below()).is(FluidTags.LAVA)
                && levelAccessor.getBlockState(pos.above()).is(Blocks.LAVA)
                && (levelAccessor.getBiome(pos).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT)
                        || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, spawnType, pos, random));
    }
}