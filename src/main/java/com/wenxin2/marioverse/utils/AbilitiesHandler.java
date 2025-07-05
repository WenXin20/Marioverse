package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface AbilitiesHandler {
    void mv$clearAllPowerUps();

    boolean mv$hasMushroom();
    void mv$setMushroom(boolean hasMushroom);

    boolean mv$hasMushroomOverride();
    void mv$setMushroomOverride(boolean hasMushroomOverride);

    boolean mv$hasMushroomBoost();
    void mv$setMushroomBoost(boolean hasMushroom);

    boolean mv$hasMegaMushroom();
    void mv$setMegaMushroom(boolean hasMushroom);

    boolean mv$hasFireFlower();
    void mv$setFireFlower(boolean hasFireFlower);

    boolean mv$hasIceFlower();
    void mv$setIceFlower(boolean hasIceFlower);

    boolean mv$hasSuperStar();
    void mv$setSuperStar(boolean hasSuperStar);

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
            return ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get();
        else return ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get();
    }

    @NotNull
    private static Boolean equipCostumes(LivingEntity entity) {
        if (entity instanceof Player)
            return ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get();
        else return ConfigRegistry.EQUIP_COSTUMES_MOBS.get();
    }

    default void applyMushroomPowerUp(Level world, LivingEntity entity) {
        if (!entity.isSpectator() && getDamageShrinksConfig(entity)
                && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_MUSHROOMS) || ConfigRegistry.MUSHROOM_POWERS_ALL_MOBS.get())) {
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            if (!world.isClientSide) {
                if (entity.getHealth() < entity.getMaxHealth())
                    entity.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)) {
                    world.playSound(null, entity, SoundRegistry.PLAYER_POWERS_UP.get(),
                            entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F, 1.0F);
                }
            }
        }
    }

    default void applyOneUpMushroomPowerUp(Level world, ItemStack stack, LivingEntity entity) {
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

            world.playSound(null, entity, SoundRegistry.ONE_UP_COLLECTED.get(),
                    entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F, 1.0F);
            if (world instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);
                ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, entity);
            }
        }
    }

    default void applySuperStarPowerUp(Level world, LivingEntity entity) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS) || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get())
                && entity instanceof AbilitiesHandler handler) {

            handler.mv$setSuperStar(true);
            handler.mv$setSuperStarCooldown(ConfigRegistry.SUPER_STAR_DURATION.get());
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.COIN_GLINT.get(), serverWorld, entity, 10);
            world.playSound(null, entity, SoundRegistry.PLAYER_POWERS_UP.get(),
                    entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    default void applyFireFlowerPowerUp(Level world, LivingEntity entity) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS) || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())
                && entity instanceof AbilitiesHandler handler) {
            AccessoriesCapability capability = AccessoriesCapability.get(entity);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.FIRE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());
            handler.mv$clearAllPowerUps();
            handler.mv$setMushroom(true);
            handler.mv$setFireFlower(true);
            world.playSound(null, entity, SoundRegistry.PLAYER_POWERS_UP.get(),
                    entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F, 1.0F);

            this.applyCostume(entity, capability, ItemRegistry.MARIO_FIRE_HAT.get(), ItemRegistry.MARIO_FIRE_SHIRT.get(),
                    ItemRegistry.MARIO_FIRE_PANTS.get(), ItemRegistry.MARIO_FIRE_SHOES.get());
        }
    }

    default void applyIceFlowerPowerUp(Level world, LivingEntity entity) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS) || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get())
                && entity instanceof AbilitiesHandler handler) {
            AccessoriesCapability capability = AccessoriesCapability.get(entity);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.ICE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.MUSHROOM_HEALTH_HEALED.get().floatValue());
            handler.mv$clearAllPowerUps();
            handler.mv$setMushroom(true);
            handler.mv$setIceFlower(true);
            world.playSound(null, entity, SoundRegistry.PLAYER_POWERS_UP.get(),
                    entity instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F, 1.0F);

            this.applyCostume(entity, capability, ItemRegistry.MARIO_ICE_HAT.get(), ItemRegistry.MARIO_ICE_SHIRT.get(),
                    ItemRegistry.MARIO_ICE_PANTS.get(), ItemRegistry.MARIO_ICE_SHOES.get());
        }
    }

    default void applyCostume(LivingEntity entity, AccessoriesCapability capability, Item hat, Item shirt, Item pants, Item shoes) {
        if (capability != null && equipCostumes(entity)) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            if (containerHat != null && containerHat.getAccessories().getItem(0).getItem() != hat)
                containerHat.getAccessories().setItem(0, new ItemStack(hat));
            if (containerShirt != null && containerShirt.getAccessories().getItem(0).getItem() != shirt)
                containerShirt.getAccessories().setItem(0, new ItemStack(shirt));
            if (containerPants != null && containerPants.getAccessories().getItem(0).getItem() != pants)
                containerPants.getAccessories().setItem(0, new ItemStack(pants));
            if (containerShoes != null && containerShoes.getAccessories().getItem(0).getItem() != shoes)
                containerShoes.getAccessories().setItem(0, new ItemStack(shoes));
        }
    }
}
