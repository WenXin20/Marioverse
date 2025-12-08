package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;

public class SnowPokeyEntity extends PokeyEntity implements GeoEntity, NeutralMob {
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

    @Override
    public TagKey<EntityType<?>> getCanAttackTag() {
        return TagRegistry.SNOW_POKEY_CAN_ATTACK;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Walk", 5, this::walkController));
    }

    @Nullable
    @Override
    public ItemStack getPickedResult(@NotNull HitResult target) {
        return new ItemStack(ItemRegistry.SNOW_POKEY_SPAWN_EGG.get());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getData(DataAttachmentRegistry.IS_BLOOMING))
            this.setData(DataAttachmentRegistry.IS_BLOOMING, false);
    }

    @Override
    public void triggerBloom() {
    }

    public static boolean checkSnowPokeySpawnRules(EntityType<? extends Monster> entityType, ServerLevelAccessor serverWorld,
                                                MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return serverWorld.getDifficulty() != Difficulty.PEACEFUL
                && (MobSpawnType.ignoresLightRequirements(spawnType) || isDarkEnoughToSpawn(serverWorld, pos, random))
                && checkMobSpawnRules(entityType, serverWorld, spawnType, pos, random);
    }
}