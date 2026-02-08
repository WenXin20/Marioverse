package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.QuicksandBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.entities.power_ups.OneUpMushroomEntity;
import com.wenxin2.marioverse.network.client_bound.data.OneUpPayload;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.utils.BlockWarpEntityHandler;
import com.wenxin2.marioverse.utils.EntityWarpEntityHandler;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements BlockWarpEntityHandler, EntityWarpEntityHandler, AbilitiesHandler {
    @Shadow public abstract void setSpeed(float speed);
    @Shadow public abstract void handleEntityEvent(byte entityEvent);
    @Shadow protected boolean jumping;

    @Unique private static final int MAX_PARTICLE_AMOUNT = 100;
    @Unique protected float mv$appliedEyeHeightScale = 1.0F;
    @Unique protected float mv$appliedHeightScale = 1.0F;
    @Unique protected float mv$appliedWidthScale = 1.0F;

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean mv$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_MOBS.get();
    }

    @Override
    public boolean mv$getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_MOBS.get();
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (tag.contains("marioverse:has_fire_flower"))
            entity.getPersistentData().putBoolean("marioverse:has_fire_flower",
                    tag.getBoolean("marioverse:has_fire_flower"));

        if (tag.contains("marioverse:has_ice_flower"))
            entity.getPersistentData().putBoolean("marioverse:has_ice_flower",
                    tag.getBoolean("marioverse:has_ice_flower"));

        if (tag.contains("marioverse:has_super_mushroom"))
            entity.getPersistentData().putBoolean("marioverse:has_super_mushroom",
                    tag.getBoolean("marioverse:has_super_mushroom"));

        if (tag.contains("marioverse:has_super_mushroom_override"))
            entity.getPersistentData().putBoolean("marioverse:has_super_mushroom_override",
                    tag.getBoolean("marioverse:has_super_mushroom_override"));

        if (tag.contains("marioverse:prevent_warp"))
            entity.getPersistentData().putBoolean("marioverse:prevent_warp",
                    tag.getBoolean("marioverse:prevent_warp"));

        if (tag.contains("marioverse:warp_cooldown"))
            entity.getPersistentData().putInt("marioverse:warp_cooldown",
                    tag.getInt("marioverse:warp_cooldown"));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockPos posNorth = pos.north(Math.round(this.getBbWidth() + 0.1F));
        BlockPos posSouth = pos.south(Math.round(this.getBbWidth() + 0.1F));
        BlockPos posEast = pos.east(Math.round(this.getBbWidth() + 0.1F));
        BlockPos posWest = pos.west(Math.round(this.getBbWidth() + 0.1F));
        BlockState stateAboveEntity = level.getBlockState(posAboveEntity);
        BlockState stateNorth = level.getBlockState(posNorth);
        BlockState stateSouth = level.getBlockState(posSouth);
        BlockState stateEast = level.getBlockState(posEast);
        BlockState stateWest = level.getBlockState(posWest);
        RandomSource rand = RandomSource.create();
        Vec3 motion = entity.getDeltaMovement();

        this.mv$characterAbilities(entity);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                    || level.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                && (entity.fallDistance > 0 || entity.isInWaterOrBubble())
                && !(entity instanceof Player)
                && !entity.isSpectator())
            this.mv$squashEntity(entity);


        if (entity.hasData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get())
                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) > 0)
            entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) - 1);

        if (entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES)
                && (EventHooks.canEntityGrief(level, entity) || entity instanceof Player player && !player.mayFly())
                && !entity.onGround() && entity.getY() > entity.yOld
                && !entity.isSpectator() && !level.isClientSide
                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0)
            OnOffSwitchBlock.hitSwitchBlock(level, posAboveEntity, entity);

        if ((EventHooks.canEntityGrief(level, entity) || entity instanceof Player) && !level.isClientSide
                && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES_FROM_SIDE)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            OnOffSwitchBlock.hitSwitchBlockFromSide(level, posNorth, entity, posSouth, posEast, posWest);

        if (level.getBlockEntity(posAboveEntity) instanceof QuestionBlockEntity questionBlockEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && (EventHooks.canEntityGrief(level, entity) || entity instanceof Player player && !player.mayFly())
                && !entity.onGround() && entity.getY() > entity.yOld
                && !entity.isSpectator() && !level.isClientSide
                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0)
            QuestionBlock.hitQuestionBlock(level, posAboveEntity, entity, questionBlockEntity);

        if ((EventHooks.canEntityGrief(level, entity) || entity instanceof Player) && !level.isClientSide
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS_FROM_SIDE)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            QuestionBlock.hitQuestionBlockFromSide(level, posNorth, entity, posSouth, posEast, posWest);

        if (stateAboveEntity.is(TagRegistry.SMASHABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && (EventHooks.canEntityGrief(level, entity) || entity instanceof Player player && !player.mayFly())
                && !entity.onGround() && entity.getY() > entity.yOld
                && !entity.isSpectator() && !level.isClientSide
                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0) {
            StorageBrickBlock.smashBlock(level, posAboveEntity, stateAboveEntity, entity);
        }

        if ((EventHooks.canEntityGrief(level, entity) || entity instanceof Player) && !level.isClientSide
                && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS_FROM_SIDE)
                && entity.getDeltaMovement().horizontalDistance() > 0.1
                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) == 0)
            StorageBrickBlock.smashBlockFromSide(stateNorth, entity, level, posNorth, stateSouth, posSouth, stateEast, posEast, stateWest, posWest);

        if (stateAboveEntity.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)
                && !entity.onGround() && entity.getY() > entity.yOld
                && !entity.isSpectator() && !level.isClientSide) {
            if (stateAboveEntity.hasProperty(QuestionBlock.EMPTY) && stateAboveEntity.getValue(QuestionBlock.EMPTY))
                level.playSound(null, posAboveEntity, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else level.playSound(null, posAboveEntity, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        if (entity.getType().is(TagRegistry.CAN_BONK_BLOCKS_FROM_SIDE)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            StorageBrickBlock.bonkBlockFromSide(stateNorth, level, posNorth, stateSouth, posSouth, stateEast, posEast, stateWest, posWest);

        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            this.mv$superStarKillEntity(entity);
            if (!entity.isInvisible()) {
                if (level instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.RAINBOW_GLINT.get(), serverWorld, entity);
                ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.RAINBOW_GLINT.get(), entity, true, 10, rand.nextDouble() * entity.getBbHeight(), 0.1);
            }
        }

        if (entity.getData(DataAttachmentRegistry.HAS_DASH_MUSHROOM_BOOST))
            this.mv$boostEntityParticles(entity.getVehicle(), entity);

        float f5 = this.mv$getEyeHeightScale();
        if (f5 != this.mv$appliedEyeHeightScale) {
            this.mv$appliedEyeHeightScale = f5;
            entity.refreshDimensions();
        }

        float f6 = this.mv$getHeightScale();
        if (f6 != this.mv$appliedHeightScale) {
            this.mv$appliedHeightScale = f6;
            entity.refreshDimensions();
        }

        float f7 = this.mv$getWidthScale();
        if (f7 != this.mv$appliedWidthScale) {
            this.mv$appliedWidthScale = f6;
            entity.refreshDimensions();
        }
    }

    @Unique
    private void mv$boostEntityParticles(Entity vehicle, LivingEntity entity) {
        double speed = this.getDeltaMovement().horizontalDistance();
        double minimumBoostSpeed = 0.3;

        if (vehicle != null) {
            speed = vehicle.getDeltaMovement().horizontalDistance();

            if (vehicle instanceof Boat && speed > 0) {
                if (vehicle.level() instanceof ServerLevel serverWorld) {
                    ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.POWERED_UP.get(), serverWorld, vehicle);
                    ServerParticleUtils.spawnParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), serverWorld, vehicle, true, false, 10, 0.1);
                }
                if (vehicle.level().isClientSide) {
                    ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.POWERED_UP.get(), vehicle, true, 5, 0.1, 0.0);
                    ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), vehicle, true, 10, 0.1, 0.0);
                }
            }

            if (speed >= minimumBoostSpeed) {
                if (vehicle.level().isClientSide) {
                    ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.POWERED_UP.get(), vehicle, true, 5, 0.1, 0.0);
                    ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), vehicle, true, 10, 0.1, 0.0);
                } else if (vehicle.level() instanceof ServerLevel serverWorld && !(entity instanceof Player)) {
                    ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.POWERED_UP.get(), serverWorld, vehicle);
                    ServerParticleUtils.spawnParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), serverWorld, vehicle, true, false, 10, 0.1);
                }
            } else entity.setData(DataAttachmentRegistry.HAS_DASH_MUSHROOM_BOOST, false);
        } else if (speed >= minimumBoostSpeed) {
            if (entity.level().isClientSide) {
                ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.POWERED_UP.get(), entity, true, 5, 0.1, 0.0);
                ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), entity, true, 10, 0.1, 0.0);
            } else if (entity.level() instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.POWERED_UP.get(), serverWorld, entity);
                ServerParticleUtils.spawnParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), serverWorld, entity, true, false, 10, 0.1);
            }
        } else entity.setData(DataAttachmentRegistry.HAS_DASH_MUSHROOM_BOOST, false);
    }

    @Unique
    private void mv$shellBonkBlock(BlockState stateNorth, Level world, BlockPos posNorth, BlockState stateSouth, BlockPos posSouth, BlockState stateEast, BlockPos posEast, BlockState stateWest, BlockPos posWest) {
        if (stateNorth.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateNorth.hasProperty(QuestionBlock.EMPTY) && stateNorth.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posNorth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posNorth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateSouth.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateSouth.hasProperty(QuestionBlock.EMPTY) && stateSouth.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posSouth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posSouth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateEast.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateEast.hasProperty(QuestionBlock.EMPTY) && stateEast.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posEast, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posEast, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateWest.is(TagRegistry.BONKABLE_BLOCKS))
            if (stateWest.hasProperty(QuestionBlock.EMPTY) && stateWest.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posWest, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posWest, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Unique
    private void mv$shellHitQuestionBlock(Level world, BlockPos posNorth, LivingEntity entity, BlockPos posSouth, BlockPos posEast, BlockPos posWest) {
        if (world.getBlockEntity(posNorth) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posNorth, entity, questionBlockEntity);

        if (world.getBlockEntity(posSouth) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posSouth, entity, questionBlockEntity);

        if (world.getBlockEntity(posEast) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posEast, entity, questionBlockEntity);

        if (world.getBlockEntity(posWest) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posWest, entity, questionBlockEntity);
    }

    @Unique
    private void mv$characterAbilities(LivingEntity entity) {
        AttributeInstance blockReachAttribute = entity.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityReachAttribute = entity.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        AttributeInstance jumpAttribute = entity.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance safeFallAttribute = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        boolean isMega = entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM);
        boolean isMini = entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        Vec3 motion = entity.getDeltaMovement();
        boolean hasCostume = this.mv$hasMarioCostume(entity)
                || this.mv$hasLuigiCostume(entity)
                || this.mv$hasPeachCostume(entity);

        if (jumpAttribute != null) {
            boolean isRunning = entity.isSprinting();
            double normalJumpBoost = 0.0;
            double runningJumpBoost = 0.0;

            if (hasCostume) {
                if (this.mv$hasPeachCostume(entity)) {
                    normalJumpBoost = 0.3;
                    runningJumpBoost = 0.4;
                } else if (this.mv$hasLuigiCostume(entity)) {
                    normalJumpBoost = 0.6;
                    runningJumpBoost = 0.7;
                } else {
                    normalJumpBoost = 0.5;
                    runningJumpBoost = 0.6;
                }
            }

            if (isMega) {
                if (hasCostume) {
                    normalJumpBoost *= 0.8;
                    runningJumpBoost *= 1.0;
                } else {
                    normalJumpBoost = 0.3;
                    runningJumpBoost = 0.4;
                }
            }

            if (isMini) {
                if (hasCostume) {
                    normalJumpBoost *= 1.2;
                    runningJumpBoost *= 1.4;
                } else {
                    normalJumpBoost = 0.5;
                    runningJumpBoost = 0.6;
                }
            }

            if (!entity.isShiftKeyDown() && (hasCostume || isMini || isMega)) {
                if (isRunning) {
                    mv$setModifier(jumpAttribute, AttributesRegistry.RUNNING_JUMP_BOOST, runningJumpBoost);
                    mv$setModifier(jumpAttribute, AttributesRegistry.JUMP_BOOST, 0);
                } else {
                    mv$setModifier(jumpAttribute, AttributesRegistry.JUMP_BOOST, normalJumpBoost);
                    mv$setModifier(jumpAttribute, AttributesRegistry.RUNNING_JUMP_BOOST, 0);
                }
            } else {
                mv$setModifier(jumpAttribute, AttributesRegistry.JUMP_BOOST, 0);
                mv$setModifier(jumpAttribute, AttributesRegistry.RUNNING_JUMP_BOOST, 0);
            }
        }

        if (safeFallAttribute != null) {
            double safeFallDistance = 0;

            if (isMega) {
                if (hasCostume)
                    safeFallDistance= 7;
                else safeFallDistance = 4;
            } else if (isMini) {
                if (hasCostume)
                    safeFallDistance= 16;
                else safeFallDistance = 14;
            } else if (hasCostume)
                safeFallDistance= 7;

            if (hasCostume || isMega || isMini)
                mv$setModifier(safeFallAttribute, AttributesRegistry.SAFE_FALL_DISTANCE, safeFallDistance);
            else mv$setModifier(safeFallAttribute, AttributesRegistry.SAFE_FALL_DISTANCE, 0);
        }

        if (blockReachAttribute != null) {
            double reachDistance = 0;

            if (isMega)
                reachDistance = ConfigRegistry.MEGA_MUSHROOM_REACH_DISTANCE.get();
            else if (isMini)
                reachDistance = ConfigRegistry.MINI_MUSHROOM_REACH_DISTANCE.get();

            if (isMega || isMini)
                mv$setModifier(blockReachAttribute, AttributesRegistry.BLOCK_REACH_DISTANCE, reachDistance);
            else mv$setModifier(blockReachAttribute, AttributesRegistry.BLOCK_REACH_DISTANCE, 0);
        }

        if (entityReachAttribute != null) {
            double reachDistance = 0;

            if (isMega)
                reachDistance = ConfigRegistry.MEGA_MUSHROOM_REACH_DISTANCE.get();
            else if (isMini)
                reachDistance = ConfigRegistry.MINI_MUSHROOM_REACH_DISTANCE.get();

            if (isMega || isMini)
                mv$setModifier(entityReachAttribute, AttributesRegistry.ENTITY_REACH_DISTANCE, reachDistance);
            else mv$setModifier(entityReachAttribute, AttributesRegistry.ENTITY_REACH_DISTANCE, 0);
        }

        if (this.mv$hasPeachCostume(entity)) {
            if (motion.y < 0)
                entity.setDeltaMovement(motion.x, motion.y * 0.7, motion.z);
        }

        if (isMini) {
            if (motion.y < 0)
                entity.setDeltaMovement(motion.x, motion.y * 0.9, motion.z);
        }
    }

    @Unique
    private static void mv$setModifier(AttributeInstance attribute, ResourceLocation id, double amount) {
        AttributeModifier modifier = attribute.getModifier(id);

        if (amount == 0.0) {
            if (modifier != null)
                attribute.removeModifier(id);
            return;
        }

        if (modifier != null) {
            if (modifier.amount() == amount)
                return;
            attribute.removeModifier(id);
        }

        attribute.addPermanentModifier(new AttributeModifier(id, amount, AttributeModifier.Operation.ADD_VALUE));
    }


    @ModifyReturnValue(method = "getArmorValue", at = @At("RETURN"))
    private int getArmorValue(int original) {
        LivingEntity entity = (LivingEntity) (Object) this;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer[] accessorySlots = {
                    capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat")),
                    capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt")),
                    capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants")),
                    capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"))
            };

            int totalExtraArmor = 0;
            float totalToughness = 0.0F;
            float totalKnockbackResistance = 0.0F;

            for (AccessoriesContainer container : accessorySlots) {
                if (container == null) return original;
                ItemStack stack = container.getAccessories().getItem(0);
                if (!stack.isEmpty() && stack.getItem() instanceof ArmorItem accessoryArmor) {
                    ArmorMaterial material = accessoryArmor.getMaterial().value();
                    totalExtraArmor += material.getDefense(accessoryArmor.getType()) / 2;
                    totalToughness += material.toughness() / 2;
                    totalKnockbackResistance += material.knockbackResistance() / 2;
                }
            }

            AttributeInstance toughnessAttribute = entity.getAttribute(Attributes.ARMOR_TOUGHNESS);
            if (toughnessAttribute != null) {
                boolean hasModifier = toughnessAttribute.getModifier(AttributesRegistry.COSTUME_ARMOR_TOUGHNESS) != null;
                if (totalToughness > 0) {
                    AttributeModifier toughnessModifier = new AttributeModifier(AttributesRegistry.COSTUME_ARMOR_TOUGHNESS,
                            totalToughness, AttributeModifier.Operation.ADD_VALUE);

                    if (!hasModifier)
                        toughnessAttribute.addPermanentModifier(toughnessModifier);
                } else if (hasModifier) toughnessAttribute.removeModifier(AttributesRegistry.COSTUME_ARMOR_TOUGHNESS);
            }

            AttributeInstance knockbackAttribute = entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (knockbackAttribute != null) {
                boolean hasModifier = knockbackAttribute.getModifier(AttributesRegistry.COSTUME_ARMOR_KNOCKBACK_RESISTANCE) != null;
                if (totalKnockbackResistance > 0) {
                    AttributeModifier knockbackModifier = new AttributeModifier(AttributesRegistry.COSTUME_ARMOR_KNOCKBACK_RESISTANCE,
                            totalKnockbackResistance, AttributeModifier.Operation.ADD_VALUE);

                    if (!hasModifier)
                        knockbackAttribute.addPermanentModifier(knockbackModifier);
                } else if (hasModifier)  knockbackAttribute.removeModifier(AttributesRegistry.COSTUME_ARMOR_KNOCKBACK_RESISTANCE);
            }

            return original + totalExtraArmor;
        } else return original;
    }

    @ModifyReturnValue(method = "checkTotemDeathProtection", at = @At("RETURN"))
    private boolean checkTotemDeathProtection(boolean original, DamageSource source) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        SoundSource soundSource = livingEntity instanceof Player ? SoundSource.PLAYERS : SoundSource.NEUTRAL;

        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            ItemStack stack = livingEntity.getOffhandItem();

            for (InteractionHand hand : InteractionHand.values()) {
                ItemStack stackInHand = livingEntity.getItemInHand(hand);
                if (stackInHand.getItem() instanceof OneUpMushroomItem) {
                    stack = stackInHand.copy();
                    stackInHand.shrink(1);
                    break;
                }
            }

            AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
            if (capability != null) {
                AccessoriesContainer containerCharm = capability.getContainer(SlotTypeLoader.getSlotType(livingEntity, "charm"));
                if (containerCharm != null) {
                    ItemStack stackCharm = containerCharm.getAccessories().getItem(0);
                    if (stackCharm.getItem() instanceof OneUpMushroomItem) {
                        this.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(), soundSource, 1.0F, 1.0F);
                        livingEntity.setHealth(ConfigRegistry.ONE_UP_HEALTH_HEALED.get().floatValue());
                        stackCharm.shrink(1);

                        if (livingEntity.level() instanceof ServerLevel serverWorld) {
                            ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, livingEntity, 25);
                            ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, livingEntity, 1.0);
                            if (livingEntity instanceof ServerPlayer player)
                                PacketDistributor.sendToPlayer(player, new OneUpPayload(true));
                        }

                        if (livingEntity instanceof ServerPlayer serverplayer) {
                            serverplayer.awardStat(Stats.ITEM_USED.get(ItemRegistry.ONE_UP_MUSHROOM.get()), 1);
                            CriteriaTriggers.USED_TOTEM.trigger(serverplayer, stack);
                            this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                        }
                        return true;
                    }
                }
            }

            if (!stack.isEmpty() && stack.getItem() instanceof OneUpMushroomItem) {
                this.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(), soundSource, 1.0F, 1.0F);
                livingEntity.setHealth(ConfigRegistry.ONE_UP_HEALTH_HEALED.get().floatValue());
                stack.shrink(1);

                if (livingEntity.level() instanceof ServerLevel serverWorld) {
                    ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, livingEntity, 25);
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, livingEntity, 1.0);
                    if (livingEntity instanceof ServerPlayer player)
                        PacketDistributor.sendToPlayer(player, new OneUpPayload(true));
                }

                if (livingEntity instanceof ServerPlayer serverplayer) {
                    serverplayer.awardStat(Stats.ITEM_USED.get(ItemRegistry.ONE_UP_MUSHROOM.get()), 1);
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayer, stack);
                    this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                }
                return true;
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "isDamageSourceBlocked", at = @At("RETURN"))
    public boolean isDamageSourceBlocked(boolean original, DamageSource source) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (source.is(TagRegistry.SHIELD_BLOCKS) && livingEntity.isBlocking()) {
            Vec3 vec32 = source.getSourcePosition();
            if (vec32 != null) {
                return true;
            }
        }
        return original;
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder createLivingAttributes(AttributeSupplier.Builder original) {
        original.add(AttributesRegistry.EYE_HEIGHT_SCALE);
        original.add(AttributesRegistry.HEIGHT_SCALE);
        original.add(AttributesRegistry.WIDTH_SCALE);

        return original;
    }

    @ModifyReturnValue(method = "handleRelativeFrictionAndCalculateMovement", at = @At("RETURN"))
    private Vec3 mv$handleRelativeFrictionAndCalculateMovement(Vec3 motion) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        BlockState state = livingEntity.level().getBlockState(this.blockPosition());

        if ((this.horizontalCollision || this.jumping) && (state.getBlock() instanceof QuicksandBlock))
            return new Vec3(motion.x, 0.2D, motion.z);
        return motion;
    }

    @ModifyReturnValue(method = "getDimensions", at = @At("TAIL"))
    private EntityDimensions getDimensions(EntityDimensions original, Pose pose) {
        float eyeScale = this.mv$getEyeHeightScale();
        float heightScale = this.mv$getHeightScale();
        float widthScale = this.mv$getWidthScale();

        return EntityDimensions.scalable(original.width()  * widthScale, original.height() * heightScale)
                .withEyeHeight(original.eyeHeight() * eyeScale);
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void jumpFromGround(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Remove the speed modifier when the entity jumps
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(AttributesRegistry.MINI_GOOMBA_SLOWDOWN))
            speedAttribute.removeModifier(AttributesRegistry.MINI_GOOMBA_SLOWDOWN);
    }

    @Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
    private void canFreeze(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            boolean hasFreezeImmunity =
                    (containerHat != null && containerHat.getAccessories().getItem(0).is(ItemTags.FREEZE_IMMUNE_WEARABLES)) ||
                            (containerShirt != null && containerShirt.getAccessories().getItem(0).is(ItemTags.FREEZE_IMMUNE_WEARABLES)) ||
                            (containerPants != null && containerPants.getAccessories().getItem(0).is(ItemTags.FREEZE_IMMUNE_WEARABLES)) ||
                            (containerShoes != null && containerShoes.getAccessories().getItem(0).is(ItemTags.FREEZE_IMMUNE_WEARABLES));

            if (hasFreezeImmunity)
                cir.setReturnValue(false);
        }
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte id, CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        RandomSource random = entity.getRandom();

        if (id == 109) {
            ParticleUtils.spawnParticlesOnBlockFaces(entity.level(),
                    BlockPos.containing(entity.getX() + 0.5D, entity.getY() + entity.getBbHeight(), entity.getZ() + 0.5D),
                    ParticleRegistry.COIN_GLINT.get(), UniformInt.of(2, 3));
        } else if (id == 110) {
            ParticleUtils.spawnParticlesOnBlockFaces(entity.level(),
                    BlockPos.containing(entity.getX() + 0.5D, entity.getY() + entity.getBbHeight(), entity.getZ() + 0.5D),
                    ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        } else if (id == 111) {
            this.level().addParticle(ParticleTypes.CRIT,
                    entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(),
                    0.0, 1.0, 0.0);
        } else if (id == 112) {
            ParticleUtils.spawnParticlesOnBlockFaces(entity.level(), this.blockPosition(), ParticleRegistry.GLOWING_STAR.get(), UniformInt.of(1, 1));
        } else if (id == 113) {
            ParticleUtils.spawnParticlesOnBlockFaces(entity.level(), this.blockPosition(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        } else if (id == 119) {
            this.mv$spawnPowerUpParticles(entity, ParticleRegistry.COIN_GLINT.get(), 15);
        } else if (id == 120) {
            for(int i = 0; i < MAX_PARTICLE_AMOUNT; ++i) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        } else if (id == 123) {
            this.mv$spawnPowerUpParticles(entity, ParticleRegistry.FIRE_POWERED_UP.get(), 15);
        } else if (id == 124) {
            this.mv$spawnPowerUpParticles(entity, ParticleRegistry.POWERED_UP.get(), 25);
        } else if (id == 125) {
            if (this.level().isClientSide) {
                ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.blockPosition().above(Math.round(this.getBbHeight())).above(),
                        ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
            }
        } else if (id == 126) {
            if (this.level().isClientSide) {
                this.level().addParticle(ParticleRegistry.ONE_UP.get(),
                        entity.getX(), entity.getY() + entity.getBbHeight(), entity.getZ(),
                        0.0, 1.0, 0.0);
            }
        } else super.handleEntityEvent(id);
    }

    @Unique
    public float mv$getEyeHeightScale() {
        LivingEntity entity = (LivingEntity) (Object) this;
        AttributeMap attributemap = entity.getAttributes();
        return attributemap == null ? 1.0F : this.mv$sanitizeScales((float) attributemap.getValue(AttributesRegistry.EYE_HEIGHT_SCALE));
    }

    @Unique
    public float mv$getHeightScale() {
        LivingEntity entity = (LivingEntity) (Object) this;
        AttributeMap attributemap = entity.getAttributes();
        return attributemap == null ? 1.0F : this.mv$sanitizeScales((float) attributemap.getValue(AttributesRegistry.HEIGHT_SCALE));
    }

    @Unique
    public float mv$getWidthScale() {
        LivingEntity entity = (LivingEntity) (Object) this;
        AttributeMap attributemap = entity.getAttributes();
        return attributemap == null ? 1.0F : this.mv$sanitizeScales((float) attributemap.getValue(AttributesRegistry.WIDTH_SCALE));
    }

    @Unique
    public float mv$sanitizeScales(float scale) {
        return scale;
    }

    @Unique
    public void mv$spawnPowerUpParticles(Entity entity, ParticleOptions particleType, int avgAmount) {
        if (entity.level().isClientSide) {
            float scaleFactor = entity.getBbWidth();
            int numParticles = (int) (scaleFactor * avgAmount);
            double radius = entity.getBbWidth() / 2;

            for (int i = 0; i < numParticles; i++) {
                // Calculate angle for each particle
                double angle = 2 * Math.PI * i / numParticles;
                // Calculate the X and Z offset using sine and cosine to spread in an ellipse
                double offsetX = Math.cos(angle) * radius;
                double offsetY = entity.getBbHeight();
                double offsetZ = Math.sin(angle) * radius;

                double x = entity.getX() + offsetX;
                double y = entity.getY();
                double z = entity.getZ() + offsetZ;

                this.level().addParticle(particleType, x, y + offsetY - 0.2, z, 0, 1.0, 0);
                this.level().addParticle(particleType, x, y + offsetY / 2, z, 0, 1.0, 0);
                this.level().addParticle(particleType, x, y + 0.2, z, 0, 1.0, 0);
            }
        }
    }

    @Unique
    public void mv$superStarKillEntity(LivingEntity attackingEntity) {
        List<Entity> nearbyEntities = attackingEntity.level().getEntities(attackingEntity, attackingEntity.getBoundingBox());

        if (!nearbyEntities.isEmpty()) {
            for (Entity collidedEntity : nearbyEntities) {
                if (collidedEntity instanceof LivingEntity entity) {
                    if (!entity.getType().is(TagRegistry.SUPER_STAR_IMMUNE)) {
                        if (entity instanceof Player player && player.isCreative() || entity.isSpectator())
                            return;
                        if (collidedEntity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
                            return;

                        Vec3 knockbackDirection = entity.position().subtract(attackingEntity.position()).normalize();
                        double knockbackStrength = 5.0;
                        Vec3 knockbackVelocity = knockbackDirection.scale(knockbackStrength).add(0, 1.0, 0);

                        if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get() && entity.isAlive() && !entity.isInvulnerable())
                            OneUpMushroomEntity.consecutiveReward(attackingEntity, entity, attackingEntity.getData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES));
                        entity.setDeltaMovement(knockbackVelocity);
                        entity.hurt(DamageSourceRegistry.superStar(collidedEntity, attackingEntity), ConfigRegistry.SUPER_STAR_DAMAGE.get().floatValue());
                    }
                }
            }
        }
    }

    @Unique
    public void mv$squashEntity(LivingEntity stompingEntity) {
        List<Entity> nearbyEntities = stompingEntity.level().getEntities(stompingEntity, stompingEntity.getBoundingBox().inflate(0.5, 1.25, 0.5));

        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity damagedEntity && !damagedEntity.isVehicle()
                        && (stompingEntity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                            || stompingEntity.level().getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                        && !damagedEntity.getType().is(TagRegistry.POWER_UP_ENTITIES)
                        && (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED)
                            || damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                            || (ConfigRegistry.STOMP_ALL_MOBS.get() && damagedEntity instanceof LivingEntity)
                            || (damagedEntity.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS)
                                && damagedEntity instanceof LivingEntity))) {

                    if (stompingEntity instanceof Player player && player.getAbilities().flying)
                        return;

                    if (stompingEntity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)
                            || damagedEntity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
                        return;

                    // Check if the colliding entity is above the current entity and falling
                    if (stompingEntity.getY() >= damagedEntity.getY() + damagedEntity.getEyeHeight()
                            && (stompingEntity.fallDistance > 0 || stompingEntity.isInWaterOrBubble())) {
                        double bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT.getAsDouble();
                        double gravity = 0.08; // Approximate Minecraft gravity value
                        double bounceVelocity = Math.sqrt(2 * gravity * bounceBlockHeight);

                        if (damagedEntity.isAlive()) {
                            stompingEntity.setDeltaMovement(stompingEntity.getDeltaMovement().x, bounceVelocity, stompingEntity.getDeltaMovement().z);
                            stompingEntity.hasImpulse = true;
                            if (stompingEntity instanceof ServerPlayer serverPlayer)
                                serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(stompingEntity));
                        }

                        float scaleFactor = damagedEntity.getBbHeight() * damagedEntity.getBbWidth();
                        int numParticles = (int) (scaleFactor * 20);
                        double radius = damagedEntity.getBbWidth() / 2;

                        if (stompingEntity.level() instanceof ServerLevel serverWorld)
                            ServerParticleUtils.spawnParticleRingAboveEntity(ParticleTypes.CRIT, serverWorld, damagedEntity, radius, 0, numParticles);

                        boolean hasNoArmor = true;
                        for (ItemStack armorSlot : damagedEntity.getArmorSlots()) {
                            if (!armorSlot.isEmpty()) {
                                hasNoArmor = false;
                                break;
                            }
                        }

                        if (!stompingEntity.level().isClientSide() && !damagedEntity.isDeadOrDying()) {
                            if (damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED) && hasNoArmor
                                    && !stompingEntity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                                damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), damagedEntity.getHealth());
                            else if (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get()
                                    || stompingEntity.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS)) {
                                if (stompingEntity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                                        || damagedEntity instanceof KoopaTroopaEntity
                                        || damagedEntity instanceof KoopaShellEntity)
                                    damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), 0);
                                else damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                            }
                            if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get() && !stompingEntity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                                OneUpMushroomEntity.consecutiveReward(stompingEntity, damagedEntity, stompingEntity.getData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES));
                            break;
                        }
                    }
                }
            }
        }
    }
}
