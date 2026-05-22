package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.entities.ai.goals.MeleeAttackTagGoal;
import com.wenxin2.marioverse.entities.ai.goals.NearestAttackableTagGoal;
import com.wenxin2.marioverse.entities.variants.CheepCheepVariants;
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
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;

public class SpinyCheepCheepEntity extends CheepCheepEntity implements GeoEntity {
    private static final EntityDataAccessor<String> SIZE = SynchedEntityData
            .defineId(SpinyCheepCheepEntity.class, EntityDataSerializers.STRING);
    public int attackCooldown = 0;

    public SpinyCheepCheepEntity(EntityType<? extends SpinyCheepCheepEntity> type, Level world) {
        super(type, world);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.xpReward = 2;
    }

    @NotNull
    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ItemRegistry.SPINY_CHEEP_CHEEP_BUCKET.get());
    }

    @NotNull
    public DamageSource getDamageSource(Entity collidingEntity) {
        return DamageSourceRegistry.cheepCheepBite(collidingEntity); //TODO
    }

    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.CHEEP_CHEEP_CAN_ATTACK;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(6, new MeleeAttackTagGoal(this, this.getCanAttackTag(), 1.2F, true));
        this.targetSelector.addGoal(0, new NearestAttackableTagGoal(this, this.getCanAttackTag(), true));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("Size", this.getSize().toString());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("Size"))
            this.setSize(ResourceLocation.parse(tag.getString("Size")));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SIZE, CheepCheepVariants.REGULAR.toString());
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        super.saveToBucketTag(stack);
        CustomData.update(DataComponents.BUCKET_ENTITY_DATA, stack,
                tag -> tag.putString("Size", this.getSize().toString()));
    }

    @Override
    public void loadFromBucketTag(CompoundTag tag) {
        super.loadFromBucketTag(tag);

        if (tag.contains("Size", Tag.TAG_STRING))
            this.setSize(ResourceLocation.parse(tag.getString("Size")));
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

            if (entity instanceof LivingEntity livingEntity && !entity.level().isClientSide) { // TODO Config
                if (entity instanceof Player player && !player.isCreative())
                    livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));
                else livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));
            }

            this.swing(this.getUsedItemHand());
            this.attackCooldown = 20;
        }
    }

    @Override // TODO REMOVE
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
                || blockItem.getBlock() instanceof EquipableCarvedPumpkinBlock)))) {
            this.equipItemIfPossible(player.getItemInHand(hand));
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        RandomSource random = level.getRandom();
        float chance = random.nextFloat();

        if (chance < 0.25F)
            this.setSize(CheepCheepVariants.LARGE);
        else if (chance < 0.50F)
            this.setSize(CheepCheepVariants.SMALL);
        else this.setSize(CheepCheepVariants.REGULAR);

        return data;
    }

    public static boolean checkSpinyCheepCheepSpawnRules(EntityType<SpinyCheepCheepEntity> entityType, LevelAccessor levelAccessor,
                                                    MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return levelAccessor.getFluidState(pos.below()).is(FluidTags.WATER)
                && levelAccessor.getBlockState(pos.above()).is(Blocks.WATER)
                && (levelAccessor.getBiome(pos).is(BiomeTags.ALLOWS_TROPICAL_FISH_SPAWNS_AT_ANY_HEIGHT)
                        || WaterAnimal.checkSurfaceWaterAnimalSpawnRules(entityType, levelAccessor, spawnType, pos, random));
    }

    public ResourceLocation getSize() {
        return ResourceLocation.parse(this.entityData.get(SIZE));
    }

    public void setSize(ResourceLocation variant) {
        this.entityData.set(SIZE, variant.toString());
    }
}