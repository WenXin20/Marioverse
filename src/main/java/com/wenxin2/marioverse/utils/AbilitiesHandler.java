package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.power_ups.MegaMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.MiniMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.items.OneUpMushroomSpawnEggItem;
import com.wenxin2.marioverse.power_up.PowerUpSource;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import io.wispforest.accessories.api.AccessoriesCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbilitiesHandler extends CostumeHandler {
    @NotNull
    private static Boolean equipCostumes(LivingEntity entity) {
        if (entity instanceof Player)
            return ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get();
        else return ConfigRegistry.EQUIP_COSTUMES_MOBS.get();
    }

    default void mv$clearAllPowerUps(Entity entity) {
        entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, false);
        entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, false);
        entity.removeData(DataAttachmentRegistry.FIRE_FLOWER_DURATION);
        entity.removeData(DataAttachmentRegistry.ICE_FLOWER_DURATION);
    }

    default void applySuperMushroomPowerUp(Level level, LivingEntity entity, @Nullable SuperMushroomEntity powerUp, float healthHealed) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS) || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = entity.getAttribute(Attributes.STEP_HEIGHT);

            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);

            if (level instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            AttributesRegistry.updateAttributeModifiers(stepAttribute, AttributesRegistry.AUTO_STEP_HEIGHT, ConfigRegistry.MEGA_MUSHROOM_AUTO_STEP.get(), false, true);
            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));

            if (!level.isClientSide) {
                if (entity.getHealth() < entity.getMaxHealth() || entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                    entity.heal(healthHealed);
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS))
                    level.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(),
                            SoundSource.AMBIENT, 1.0F, pitch);
            }
            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyMegaMushroomPowerUp(Level level, LivingEntity entity, @Nullable MegaMushroomEntity powerUp) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_MEGA_MUSHROOMS) || ConfigRegistry.MEGA_MUSHROOM_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = entity.getAttribute(Attributes.STEP_HEIGHT);

            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, true);
            entity.setData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION, ConfigRegistry.MEGA_MUSHROOM_DURATION.get());
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);

            if (level instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            AttributesRegistry.updateAttributeModifiers(stepAttribute, AttributesRegistry.AUTO_STEP_HEIGHT, ConfigRegistry.MEGA_MUSHROOM_AUTO_STEP.get(), true, false);
            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    !entity.getType().is(TagRegistry.CANNOT_CHANGE_MAX_HEALTH), !entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM));
            entity.heal(ConfigRegistry.MEGA_MUSHROOM_HEALTH.get().floatValue());

            if (!level.isClientSide) {
                if (!entity.getType().is(TagRegistry.CANNOT_CHANGE_MAX_HEALTH))
                    entity.heal(ConfigRegistry.MEGA_MUSHROOM_HEALTH.get().floatValue());
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS))
                    level.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_MEGA_MUSHROOM.get(),
                            SoundSource.AMBIENT, 1.0F, pitch);
            }
            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyMiniMushroomPowerUp(Level level, LivingEntity entity, @Nullable MiniMushroomEntity powerUp) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_MINI_MUSHROOMS) || ConfigRegistry.MINI_MUSHROOM_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = entity.getAttribute(Attributes.STEP_HEIGHT);

            AttributesRegistry.updateAttributeModifiers(stepAttribute, AttributesRegistry.AUTO_STEP_HEIGHT, ConfigRegistry.MEGA_MUSHROOM_AUTO_STEP.get(), false, true);
            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH,
                    -entity.getMaxHealth() + ConfigRegistry.MINI_MUSHROOM_HEALTH.get(),
                    !entity.getType().is(TagRegistry.CANNOT_CHANGE_MAX_HEALTH) && !entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM),
                    entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));

            if (entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)) {
                entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, false);
                entity.setData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION, 0);
                entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
                entity.setData(DataAttachmentRegistry.PLAYED_MEGA_MUSHROOM_THEME, false);
            } else {
                entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, true);
                entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, false);
            }

            if (level instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            if (!level.isClientSide) {
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS))
                    level.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_MINI_MUSHROOM.get(),
                            SoundSource.AMBIENT, 1.0F, pitch);
            }
            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyOneUpMushroomPowerUp(Level level, ItemStack stack, LivingEntity entity, @Nullable OneUpMushroomEntity powerUp) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS) || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get())) {
            AccessoriesCapability capability = AccessoriesCapability.get(entity);
            ItemStack offhandStack = entity.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get())) {
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            } else if (offhandStack.isEmpty())
                entity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(stack.getItem()));
            else if (offhandStack.getItem() instanceof OneUpMushroomSpawnEggItem) {
                if (offhandStack.getCount() >= offhandStack.getMaxStackSize() && entity instanceof Player player) {
                    player.drop(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()), Boolean.FALSE);
                } else offhandStack.grow(1);
            }

            level.playSound(null, entity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.AMBIENT, 1.0F, pitch);
            if (level instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);
                ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, entity, 1.0);
            }
            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applySuperStarPowerUp(Level level, LivingEntity entity, @Nullable SuperStarEntity powerUp) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_STARS) || ConfigRegistry.SUPER_STAR_POWERS_ALL_MOBS.get())) {
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, true);
            entity.setData(DataAttachmentRegistry.SUPER_STAR_DURATION, ConfigRegistry.SUPER_STAR_DURATION.get());

            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));
            if (level instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.RAINBOW_GLINT.get(), serverWorld, entity, 10);
            level.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_SUPER_STAR.get(),
                    SoundSource.AMBIENT, 1.0F, pitch);

            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyFireFlowerPowerUp(Level level, LivingEntity entity, PowerUpSource source) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS) || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);

            if (level instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.FIRE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            this.mv$clearAllPowerUps(entity);
            entity.setData(DataAttachmentRegistry.FIRE_FLOWER_DURATION, ConfigRegistry.FIRE_FLOWER_DURATION.get());
            entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, true);
            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
            level.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(),
                    SoundSource.AMBIENT, 1.0F, pitch);

            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));

            this.applyCostumeChange(entity, source);
            source.removePowerUpEntity();
        }
    }

    default void applyIceFlowerPowerUp(Level level, LivingEntity entity, PowerUpSource source) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;

        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS) || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);

            if (level instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.ICE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            this.mv$clearAllPowerUps(entity);
            entity.setData(DataAttachmentRegistry.ICE_FLOWER_DURATION, ConfigRegistry.ICE_FLOWER_DURATION.get());
            entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, true);
            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
            level.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(),
                    SoundSource.AMBIENT, 1.0F, pitch);

            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));

            this.applyCostumeChange(entity, source);
            source.removePowerUpEntity();
        }
    }
}
