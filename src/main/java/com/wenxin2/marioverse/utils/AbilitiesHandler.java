package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.power_ups.AbstractPowerUpEntity;
import com.wenxin2.marioverse.entities.power_ups.MushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.network.server_bound.data.SuperStarThemePayload;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbilitiesHandler extends CostumeHandler {
    void mv$clearAllPowerUps();

    boolean mv$hasSuperMushroom();
    void mv$setSuperMushroom(boolean hasSuperMushroom);

    boolean mv$hasSuperMushroomOverride();
    void mv$setMushroomOverride(boolean hasSuperMushroomOverride);

    boolean mv$hasDashMushroomBoost();
    void mv$setDashMushroomBoost(boolean hasDashMushroom);

    boolean mv$hasMegaMushroom();
    void mv$setMegaMushroom(boolean hasMegaMushroom);

    boolean mv$hasFireFlower();
    void mv$setFireFlower(boolean hasFireFlower);

    boolean mv$hasIceFlower();
    void mv$setIceFlower(boolean hasIceFlower);

    boolean mv$hasSuperStar();
    void mv$setSuperStar(boolean hasSuperStar);

    boolean mv$playedSuperStarTheme();
    void mv$setPlayedSuperStarTheme(boolean playedSuperStarTheme);

    boolean mv$hasSmashedBlock();
    void mv$setSmashedBlock(boolean hasSmashedBlock);


    int mv$getFireballCooldown();
    void mv$setFireballCooldown(int fireballCooldown);

    int mv$getFireballCount();
    void mv$setFireballCount(int fireballCount);

    int mv$getIceBallCooldown();
    void mv$setIceBallCooldown(int iceBallCooldown);

    int mv$getIceBallCount();
    void mv$setIceBallCount(int iceBallCount);

    int mv$getSuperStarCooldown();
    void mv$setSuperStarCooldown(int superStarCooldown);


    int mv$getCheckpointFlagCooldown();
    void mv$setCheckpointFlagCooldown(int checkpointFlagCooldown);

    int mv$getConsecutiveBounces();
    void mv$setConsecutiveBounces(int consecutiveBounces);

    int mv$getFreezeImmunityCooldown();
    void mv$setFreezeImmunityCooldown(int freezeImmunityCooldown);

    int mv$getFrozenCooldown();
    void mv$setFrozenCooldown(int frozenCooldown);

    int mv$getOneUpsRewarded();
    void mv$setOneUpsRewarded(int oneUpsRewarded);

    @NotNull
    private static Boolean getDamageShrinksConfig(LivingEntity entity) {
        if (entity instanceof Player)
            return (ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                    || entity.level().getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_PLAYERS));
        else return (ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                || entity.level().getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_ALL_MOBS));
    }

    @NotNull
    private static Boolean equipCostumes(LivingEntity entity) {
        if (entity instanceof Player)
            return ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get();
        else return ConfigRegistry.EQUIP_COSTUMES_MOBS.get();
    }

    default void applyMushroomPowerUp(Level world, LivingEntity entity, @Nullable MushroomEntity powerUp, float healthHealed) {
        if (!entity.isSpectator() && getDamageShrinksConfig(entity)
                && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS) || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get())) {
            this.mv$setSuperMushroom(true);
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            if (!world.isClientSide) {
                if (entity.getHealth() < entity.getMaxHealth())
                    entity.heal(healthHealed);
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                    world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);
                }
            }
            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyOneUpMushroomPowerUp(Level world, ItemStack stack, LivingEntity entity, OneUpMushroomEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS) || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get())) {
            AccessoriesCapability capability = AccessoriesCapability.get(entity);
            ItemStack offhandStack = entity.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get())) {
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            } else if (offhandStack.isEmpty())
                entity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(stack.getItem()));
            else if (offhandStack.getItem() instanceof OneUpMushroomItem) {
                if (offhandStack.getCount() >= offhandStack.getMaxStackSize() && entity instanceof Player player) {
                    player.drop(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()), Boolean.FALSE);
                } else offhandStack.grow(1);
            }

            world.playSound(null, entity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(), SoundSource.AMBIENT);
            if (world instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);
                ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, entity, 1.0);
            }
            powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applySuperStarPowerUp(Level world, LivingEntity entity, SuperStarEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS) || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get())) {

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.COIN_GLINT.get(), serverWorld, entity, 10);

            this.mv$setSuperStar(true);
            this.mv$setSuperStarCooldown(ConfigRegistry.SUPER_STAR_DURATION.get());
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.COIN_GLINT.get(), serverWorld, entity, 10);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_SUPER_STAR.get(), SoundSource.AMBIENT);
            if (!this.mv$playedSuperStarTheme() && !world.isClientSide()) {
                if (entity instanceof ServerPlayer player)
                    PacketDistributor.sendToPlayer(player, new SuperStarThemePayload(100, player.getId()));
                else PacketDistributor.sendToPlayersTrackingEntity(entity, new SuperStarThemePayload(100, entity.getId()));
            }
            this.mv$setPlayedSuperStarTheme(true);
            powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyFireFlowerPowerUp(Level world, LivingEntity entity, AbstractPowerUpEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS) || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())) {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.FIRE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            this.mv$clearAllPowerUps();
            this.mv$setSuperMushroom(true);
            this.mv$setFireFlower(true);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);

            this.applyCostumeChange(entity, powerUp);
            powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyIceFlowerPowerUp(Level world, LivingEntity entity, AbstractPowerUpEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS) || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get())) {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.ICE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            this.mv$clearAllPowerUps();
            this.mv$setSuperMushroom(true);
            this.mv$setIceFlower(true);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);

            this.applyCostumeChange(entity, powerUp);
            powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
