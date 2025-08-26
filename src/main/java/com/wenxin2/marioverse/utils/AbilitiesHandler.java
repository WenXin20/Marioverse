package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.entities.power_ups.AbstractPowerUpEntity;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.sounds.FadingSoundInstance;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface AbilitiesHandler {
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

    default void applyMushroomPowerUp(Level world, LivingEntity entity, float healthHealed) {
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

            world.playSound(null, entity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(), SoundSource.AMBIENT);
            if (world instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);
                ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, entity, 1.0);
            }
        }
    }

    default void applySuperStarPowerUp(Level world, LivingEntity entity) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS) || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get())
                && entity instanceof AbilitiesHandler handler) {

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.COIN_GLINT.get(), serverWorld, entity, 10);

            handler.mv$setSuperStar(true);
            handler.mv$setSuperStarCooldown(ConfigRegistry.SUPER_STAR_DURATION.get());
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.COIN_GLINT.get(), serverWorld, entity, 10);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_SUPER_STAR.get(), SoundSource.AMBIENT);
            if (!handler.mv$playedSuperStarTheme())
                Minecraft.getInstance().getSoundManager().play(new FadingSoundInstance(entity, SoundRegistry.SUPER_STAR_THEME.get(),
                        SoundSource.AMBIENT, entity.getRandom(), handler.mv$getSuperStarCooldown(), 100));
            handler.mv$setPlayedSuperStarTheme(true);
        }
    }

    default void applyFireFlowerPowerUp(Level world, LivingEntity entity, AbstractPowerUpEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS) || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())
                && entity instanceof AbilitiesHandler handler) {
            AccessoriesCapability capability = AccessoriesCapability.get(entity);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.FIRE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            handler.mv$clearAllPowerUps();
            handler.mv$setSuperMushroom(true);
            handler.mv$setFireFlower(true);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);

            this.applyCostumeChange(entity, powerUp, capability);
        }
    }

    default void applyIceFlowerPowerUp(Level world, LivingEntity entity, AbstractPowerUpEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS) || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get())
                && entity instanceof AbilitiesHandler handler) {
            AccessoriesCapability capability = AccessoriesCapability.get(entity);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.ICE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            handler.mv$clearAllPowerUps();
            handler.mv$setSuperMushroom(true);
            handler.mv$setIceFlower(true);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);

            this.applyCostumeChange(entity, powerUp, capability);
        }
    }

    default void applyCostumeChange(LivingEntity entity, AbstractPowerUpEntity powerUp, AccessoriesCapability capability) {
        if (capability != null) {
            if (entity instanceof Player && ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get())
                this.updateCostume(entity, powerUp, capability);
            else if (!(entity instanceof Player) && ConfigRegistry.EQUIP_COSTUMES_MOBS.get())
                this.updateCostume(entity, powerUp, capability);
        }
    }

    default void updateCostume(LivingEntity entity, AbstractPowerUpEntity powerUp, AccessoriesCapability capability) {
        AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
        AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
        AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
        AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

        int randomIndex = (int) (Math.random() * powerUp.getHatItems().size());

        if (entity.getType().is(TagRegistry.EQUIP_COSTUMES_IN_ARMOR_SLOTS)) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (!slot.isArmor()) continue;
                ItemStack currentStack = entity.getItemBySlot(slot);

                switch (slot) {
                    case HEAD -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.HEAD);
                        ItemStack newStack = powerUp.getHatItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getHatItems(), newStack, currentStack);
                    }
                    case CHEST -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.CHEST);
                        ItemStack newStack = powerUp.getShirtItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getShirtItems(), newStack, currentStack);
                    }
                    case LEGS -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.LEGS);
                        ItemStack newStack = powerUp.getPantsItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getPantsItems(), newStack, currentStack);
                    }
                    case FEET -> {
                        ItemStack stackArmor = entity.getItemBySlot(EquipmentSlot.FEET);
                        ItemStack newStack = powerUp.getShoesItems().get(randomIndex);
                        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES))
                            this.equipCostumesInArmorSlots(entity, powerUp, slot, stackArmor, powerUp.getShoesItems(), newStack, currentStack);
                    }
                }
            }
        }

        if (containerHat != null && !containerHat.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerHat.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.HEAD) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getHatItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getHatItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerHat.getAccessories().setItem(0, newStack);
        }

        if (containerShirt != null && !containerShirt.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerShirt.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.BODY) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getShirtItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getShirtItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerShirt.getAccessories().setItem(0, newStack);
        }

        if (containerPants != null && !containerPants.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerPants.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.LEGS) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getPantsItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getPantsItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerPants.getAccessories().setItem(0, newStack);
        }

        if (containerShoes != null && !containerShoes.getAccessories().getItem(0).is(powerUp.getPowerUpCostumeTag())) {
            ItemStack stack = containerShoes.getAccessories().getItem(0);
            ItemStack stackArmor = !(entity instanceof Player)
                    ? entity.getItemBySlot(EquipmentSlot.FEET) : stack;
            ItemStack newStack = !(entity instanceof Player)
                    ? powerUp.getShoesItems().get(randomIndex) : stack;

            for (ItemStack item : powerUp.getShoesItems())
                newStack = this.equipCostumesInAccessorySlots(powerUp, item, stackArmor, newStack, stack);

            newStack.applyComponents(stack.getComponents());
            containerShoes.getAccessories().setItem(0, newStack);
        }
    }

    default ItemStack equipCostumesInAccessorySlots(AbstractPowerUpEntity powerUp, ItemStack item, ItemStack stackArmor, ItemStack newStack, ItemStack stack) {
        if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stack.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stack.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        } else if (stack.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
            if (item.is(powerUp.getPowerUpCostumeTag()))
                newStack = item.copy();
        }
        return newStack;
    }

    default void equipCostumesInArmorSlots(LivingEntity entity, AbstractPowerUpEntity powerUp, EquipmentSlot slot, ItemStack stackArmor, List<ItemStack> costumeList, ItemStack newStack, ItemStack currentStack) {
        if (stackArmor.isEmpty() || stackArmor.is(TagRegistry.COSTUMES)) {
            for (ItemStack item : costumeList) {
                if (stackArmor.is(TagRegistry.MARIO_COSTUMES) && item.is(TagRegistry.MARIO_COSTUMES)) {
                    if (item.is(powerUp.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.LUIGI_COSTUMES) && item.is(TagRegistry.LUIGI_COSTUMES)) {
                    if (item.is(powerUp.getPowerUpCostumeTag()))
                        newStack = item.copy();
                } else if (stackArmor.is(TagRegistry.PEACH_COSTUMES) && item.is(TagRegistry.PEACH_COSTUMES)) {
                    if (item.is(powerUp.getPowerUpCostumeTag()))
                        newStack = item.copy();
                }
            }

            newStack.applyComponents(currentStack.getComponents());
            entity.setItemSlot(slot, newStack);
        }
    }
}
