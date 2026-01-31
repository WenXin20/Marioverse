package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.StarCoinBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.power_ups.AbstractPowerUpEntity;
import com.wenxin2.marioverse.entities.power_ups.MegaMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.MiniMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperMushroomEntity;
import com.wenxin2.marioverse.entities.power_ups.SuperStarEntity;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.sounds.MarioverseSoundTypes;
import io.wispforest.accessories.api.AccessoriesCapability;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface AbilitiesHandler extends CostumeHandler {
    int mv$getFireballCooldown();
    void mv$setFireballCooldown(int fireballCooldown);

    int mv$getFireballCount();
    void mv$setFireballCount(int fireballCount);

    int mv$getIceBallCooldown();
    void mv$setIceBallCooldown(int iceBallCooldown);

    int mv$getIceBallCount();
    void mv$setIceBallCount(int iceBallCount);


    int mv$getFreezeImmunityCooldown();
    void mv$setFreezeImmunityCooldown(int freezeImmunityCooldown);

    int mv$getFrozenCooldown();
    void mv$setFrozenCooldown(int frozenCooldown);

    @NotNull
    private static Boolean equipCostumes(LivingEntity entity) {
        if (entity instanceof Player)
            return ConfigRegistry.EQUIP_COSTUMES_PLAYERS.get();
        else return ConfigRegistry.EQUIP_COSTUMES_MOBS.get();
    }

    default void mv$clearAllPowerUps(Entity entity) {
        entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, false);
        entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, false);
    }

    default void applySuperMushroomPowerUp(Level world, LivingEntity entity, @Nullable SuperMushroomEntity powerUp, float healthHealed) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_SUPER_MUSHROOMS) || ConfigRegistry.SUPER_MUSHROOM_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = entity.getAttribute(Attributes.STEP_HEIGHT);

            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            AttributesRegistry.updateAttributeModifiers(stepAttribute, AttributesRegistry.AUTO_STEP_HEIGHT, ConfigRegistry.MEGA_MUSHROOM_AUTO_STEP.get(), false, true);
            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));

            if (!world.isClientSide) {
                if (entity.getHealth() < entity.getMaxHealth() || entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                    entity.heal(healthHealed);
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS))
                    world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);
            }
            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyMegaMushroomPowerUp(Level world, LivingEntity entity, @Nullable MegaMushroomEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_MEGA_MUSHROOMS) || ConfigRegistry.MEGA_MUSHROOM_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = entity.getAttribute(Attributes.STEP_HEIGHT);

            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, true);
            entity.setData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION, ConfigRegistry.MEGA_MUSHROOM_DURATION.get());
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            AttributesRegistry.updateAttributeModifiers(stepAttribute, AttributesRegistry.AUTO_STEP_HEIGHT, ConfigRegistry.MEGA_MUSHROOM_AUTO_STEP.get(), true, false);
            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    !entity.getType().is(TagRegistry.CANNOT_CHANGE_MAX_HEALTH), !entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM));
            entity.heal(ConfigRegistry.MEGA_MUSHROOM_HEALTH.get().floatValue());

            if (!world.isClientSide) {
                if (!entity.getType().is(TagRegistry.CANNOT_CHANGE_MAX_HEALTH))
                    entity.heal(ConfigRegistry.MEGA_MUSHROOM_HEALTH.get().floatValue());
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS))
                    world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_MEGA_MUSHROOM.get(), SoundSource.AMBIENT);
            }
            if (powerUp != null)
                powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyMiniMushroomPowerUp(Level world, LivingEntity entity, @Nullable MiniMushroomEntity powerUp) {
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
            } else {
                entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, true);
                entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, false);
            }

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, entity, 10);

            if (!world.isClientSide) {
                if (!entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS))
                    world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_MINI_MUSHROOM.get(), SoundSource.AMBIENT);
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
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, true);
            entity.setData(DataAttachmentRegistry.SUPER_STAR_DURATION, ConfigRegistry.SUPER_STAR_DURATION.get());

            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, ConfigRegistry.SUPER_STAR_SPEED_DURATION.get(), 4, true, false));
            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.RAINBOW_GLINT.get(), serverWorld, entity, 10);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP_SUPER_STAR.get(), SoundSource.AMBIENT);

            powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyFireFlowerPowerUp(Level world, LivingEntity entity, AbstractPowerUpEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_FIRE_FLOWERS) || ConfigRegistry.FIRE_FLOWER_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.FIRE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            this.mv$clearAllPowerUps(entity);
            entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, true);
            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);

            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));

            this.applyCostumeChange(entity, powerUp);
            powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void applyIceFlowerPowerUp(Level world, LivingEntity entity, AbstractPowerUpEntity powerUp) {
        if (!entity.isSpectator() && !entity.getType().is(TagRegistry.CANNOT_CONSUME_POWER_UPS)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ICE_FLOWERS) || ConfigRegistry.ICE_FLOWER_POWERS_ALL_MOBS.get())) {
            AttributeInstance healthAttribute = entity.getAttribute(Attributes.MAX_HEALTH);

            if (world instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.ICE_POWERED_UP.get(), serverWorld, entity, 10);

            if (entity.getHealth() < entity.getMaxHealth())
                entity.heal(ConfigRegistry.SUPER_MUSHROOM_HEALTH_HEALED.get().floatValue());
            this.mv$clearAllPowerUps(entity);
            entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, true);
            entity.setData(DataAttachmentRegistry.HAS_MINI_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM, true);
            world.playSound(null, entity.blockPosition(), SoundRegistry.POWERS_UP.get(), SoundSource.AMBIENT);

            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM));

            this.applyCostumeChange(entity, powerUp);
            powerUp.remove(Entity.RemovalReason.DISCARDED);
        }
    }

    default void mv$hitQuestionBlock(Level world, BlockPos pos, Entity entity, QuestionBlockEntity questionBlockEntity) {
        if (world.getBlockState(pos).getBlock() instanceof QuestionBlock questionBlock) {
            ItemStack storedItem = questionBlockEntity.getTheItem();

            if (!world.getBlockState(pos).getValue(QuestionBlock.EMPTY))
                mv$hitEntityAbove(pos, world, entity);

            if (!storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                BlockState stateAbove = world.getBlockState(pos.above());
                ItemStack coinItem = new ItemStack(stateAbove.getBlock().asItem());
                if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                    StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
                else if (stateAbove.getBlock() instanceof CoinBlock)
                    CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);

                if (!world.isClientSide)
                    questionBlock.spawnFromQuestionBlock(world, pos, storedItem, entity, Boolean.FALSE, Boolean.TRUE);

                if (world.getBlockState(pos).is(BlockTags.GUARDED_BY_PIGLINS) && entity instanceof Player player)
                    PiglinAi.angerNearbyPiglins(player, false);

                if (world instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticlesOnBlockFace(ParticleTypes.CRIT, serverWorld, pos, Direction.DOWN,
                            UniformInt.of(3, 4), () -> ServerParticleUtils.getRandomSpeedRanges(world.getRandom()), 0.65D);

                entity.setData(DataAttachmentRegistry.HAS_HIT_BLOCK.get(), true);
                MarioverseSoundTypes.playSounds(world, pos, storedItem);
                questionBlockEntity.splitTheItem(1);
                questionBlockEntity.setChanged();
            }

            if (storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                BlockState currentState = world.getBlockState(pos);
                if (currentState.getBlock() instanceof QuestionBlock)
                    world.setBlock(pos, currentState.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }

            if (world.getBlockState(pos).getBlock() instanceof InvisibleQuestionBlock
                    && world.getBlockState(pos).getValue(InvisibleQuestionBlock.INVISIBLE)) {
                BlockState currentState = world.getBlockState(pos);
                world.setBlock(pos, currentState.setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.FALSE), 3);
                world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    default void mv$smashBlock(Level world, BlockPos pos, BlockState state, Entity entity) {
        BlockState stateAbove = world.getBlockState(pos.above());
        ItemStack coinItem = new ItemStack(stateAbove.getBlock().asItem());

        this.mv$hitEntityAbove(pos, world, entity);

        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM)) {
            if (state.getBlock() instanceof SlabBlock) {
                if (state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                    world.setBlock(pos, state.setValue(SlabBlock.TYPE, SlabType.TOP), 3);
                } else world.destroyBlock(pos, false);
                world.levelEvent(2001, pos, Block.getId(state));
            } else {
                if (state.getBlock() instanceof DecoratedPotBlock) {
                    world.setBlock(pos, state.setValue(DecoratedPotBlock.CRACKED, true), 4);
                    world.destroyBlock(pos, true, entity);
                } else world.destroyBlock(pos, false);
            }

            entity.setData(DataAttachmentRegistry.HAS_HIT_BLOCK.get(), true);
            world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);

            if (state.is(BlockTags.CRYSTAL_SOUND_BLOCKS))
                world.playSound(null, pos, SoundType.AMETHYST.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else if (state.getBlock() instanceof DecoratedPotBlock)
                world.playSound(null, pos, SoundType.DECORATED_POT.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, pos, SoundRegistry.BLOCK_SMASH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
            else if (stateAbove.getBlock() instanceof CoinBlock)
                CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);
        } else {
            world.playSound(null, pos, SoundRegistry.BLOCK_SMASH_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!(state.getBlock() instanceof QuestionBlock)) {
                if (stateAbove.getBlock() instanceof StarCoinBlock starCoin)
                    StarCoinBlock.collectCoin(starCoin, world, stateAbove, pos.above(), entity, coinItem);
                else if (stateAbove.getBlock() instanceof CoinBlock)
                    CoinBlock.collectCoin(world, stateAbove, pos.above(), entity, coinItem);
            }
        }
    }

    default void mv$hitEntityAbove(BlockPos pos, Level world, Entity attackingEntity) {
        AABB boundingBox = new AABB(pos.above()).inflate(0.01);
        List<Entity> entitiesAbove = world.getEntities(null, boundingBox);

        if (!entitiesAbove.isEmpty()) {
            for (Entity entityAbove : entitiesAbove) {
                if (entityAbove instanceof LivingEntity livingEntity && livingEntity.onGround()) {
                    entityAbove.setDeltaMovement(entityAbove.getDeltaMovement().add(0, 0.5, 0));
                    if (world.getBlockState(pos).getBlock() instanceof QuestionBlock) {
                        if (livingEntity instanceof KoopaShellEntity)
                            livingEntity.hurt(DamageSourceRegistry.bonked(livingEntity, attackingEntity), 0.0F);
                        else livingEntity.hurt(DamageSourceRegistry.bonked(livingEntity, attackingEntity), 4.0F);
                    } else {
                        if (livingEntity instanceof KoopaShellEntity)
                            livingEntity.hurt(DamageSourceRegistry.shrapnel(livingEntity, attackingEntity), 0.0F);
                        else livingEntity.hurt(DamageSourceRegistry.shrapnel(livingEntity, attackingEntity), 4.0F);
                    }
                }
            }
        }
    }
}
