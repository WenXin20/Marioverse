package com.wenxin2.marioverse.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.QuicksandBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
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
import java.util.function.Consumer;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DecoratedPotBlock;
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
    @Unique private double mv$currentEyeHeightScale = 1.0;
    @Unique private double mv$currentHeightScale = 1.0;
    @Unique private double mv$currentWidthScale = 1.0;
    @Unique protected float mv$appliedEyeHeightScale = 1.0F;
    @Unique protected float mv$appliedHeightScale = 1.0F;
    @Unique protected float mv$appliedWidthScale = 1.0F;
    @Unique private boolean mv$hasFireFlower;
    @Unique private boolean mv$hasIceFlower;
    @Unique private boolean mv$hasMegaMushroom;
    @Unique private boolean mv$hasSuperMushroom;
    @Unique private boolean mv$hasDashMushroomBoost;
    @Unique private boolean mv$hasSuperMushroomOverride;
    @Unique private boolean mv$preventWarp;
    @Unique private int mv$checkpointFlagCooldown;
    @Unique private int mv$consecutiveBounces;
    @Unique private int mv$fireballCooldown;
    @Unique private int mv$fireballCount;
    @Unique private int mv$freezeImmunityCooldown;
    @Unique private int mv$frozenCooldown;
    @Unique private int mv$iceBallCooldown;
    @Unique private int mv$iceBallCount;
    @Unique private int mv$oneUpsRewarded;
    @Unique private int mv$preventWarpCooldown;
    @Unique private int mv$warpCooldown;

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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        tag.putBoolean("marioverse:has_dash_mushroom_boost", this.mv$hasDashMushroomBoost());
        tag.putBoolean("marioverse:has_fire_flower", this.mv$hasFireFlower());
        tag.putBoolean("marioverse:has_ice_flower", this.mv$hasIceFlower());
        tag.putBoolean("marioverse:has_mega_mushroom", this.mv$hasMegaMushroom());
        tag.putBoolean("marioverse:has_super_mushroom", this.mv$hasSuperMushroom());
        tag.putBoolean("marioverse:has_super_mushroom_override", this.mv$hasSuperMushroomOverride());
        tag.putInt("marioverse:fireball_cooldown", this.mv$getFireballCooldown());
        tag.putInt("marioverse:fireball_count", this.mv$getFireballCount());
        tag.putInt("marioverse:ice_ball_cooldown", this.mv$getIceBallCooldown());
        tag.putInt("marioverse:ice_ball_count", this.mv$getIceBallCount());
        tag.putInt("marioverse:ice_ball_count", this.mv$getIceBallCount());

        if (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS)
                    || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get())) {
            tag.putInt("marioverse:consecutive_bounces", this.mv$getConsecutiveBounces());
            tag.putInt("marioverse:one_ups_rewarded", this.mv$getOneUpsRewarded());
        }

        if (entity.getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS))
            tag.putInt("marioverse:checkpoint_flag_cooldown", this.mv$getCheckpointFlagCooldown());

        if (!entity.getType().is(TagRegistry.ICE_BALL_IMMUNE) && entity instanceof Player) {
            tag.putInt("marioverse:freeze_immunity_cooldown", this.mv$getFreezeImmunityCooldown());
            tag.putInt("marioverse:frozen_cooldown", this.mv$getFrozenCooldown());
        }

        if (!entity.getType().is(TagRegistry.CANNOT_WARP)) {
            if (entity instanceof Player && ConfigRegistry.TELEPORT_PLAYERS.get()) {
                tag.putBoolean("marioverse:prevent_warp", this.mv$doPreventWarp());
                tag.putInt("marioverse:warp_cooldown", this.mv$getWarpCooldown());
            } else if (ConfigRegistry.TELEPORT_MOBS.get()) {
                tag.putBoolean("marioverse:prevent_warp", this.mv$doPreventWarp());
                tag.putInt("marioverse:warp_cooldown", this.mv$getWarpCooldown());
            }

            if (entity instanceof Player)
                tag.putInt("marioverse:prevent_warp_cooldown", this.mv$getPreventWarpCooldown());
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        this.mv$setDashMushroomBoost(tag.getBoolean("marioverse:has_dash_mushroom_boost"));
        this.mv$setFireFlower(tag.getBoolean("marioverse:has_fire_flower"));
        this.mv$setFireballCooldown(tag.getInt("marioverse:fireball_cooldown"));
        this.mv$setFireballCount(tag.getInt("marioverse:fireball_count"));
        this.mv$setIceBallCooldown(tag.getInt("marioverse:ice_ball_cooldown"));
        this.mv$setIceBallCount(tag.getInt("marioverse:ice_ball_count"));
        this.mv$setIceFlower(tag.getBoolean("marioverse:has_ice_flower"));
        this.mv$setMegaMushroom(tag.getBoolean("marioverse:has_mega_mushroom"));
        this.mv$setMushroomOverride(tag.getBoolean("marioverse:has_super_mushroom_override"));
        this.mv$setSuperMushroom(tag.getBoolean("marioverse:has_super_mushroom"));

        if (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES)
                && (entity.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS)
                || ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get())
                && !entity.hasData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)) {
            this.mv$setConsecutiveBounces(tag.getInt("marioverse:consecutive_bounces"));
            this.mv$setOneUpsRewarded(tag.getInt("marioverse:one_ups_rewarded"));
        }

        if (entity.getType().is(TagRegistry.CAN_CLAIM_CHECKPOINT_FLAGS))
            this.mv$setCheckpointFlagCooldown(tag.getInt("marioverse:checkpoint_flag_cooldown"));

        if (!entity.getType().is(TagRegistry.ICE_BALL_IMMUNE) && entity instanceof Player) {
            this.mv$setFreezeImmunityCooldown(tag.getInt("marioverse:freeze_immunity_cooldown"));
            this.mv$setFrozenCooldown(tag.getInt("marioverse:frozen_cooldown"));
        }

        if (!entity.getType().is(TagRegistry.CANNOT_WARP)) {
            if (entity instanceof Player && ConfigRegistry.TELEPORT_PLAYERS.get()) {
                this.mv$setPreventWarp(tag.getBoolean("marioverse:prevent_warp"));
                this.mv$setWarpCooldown(tag.getInt("marioverse:warp_cooldown"));
            } else if (ConfigRegistry.TELEPORT_MOBS.get()) {
                this.mv$setPreventWarp(tag.getBoolean("marioverse:prevent_warp"));
                this.mv$setWarpCooldown(tag.getInt("marioverse:warp_cooldown"));
            }

            if (entity instanceof Player)
                this.mv$setPreventWarpCooldown(tag.getInt("marioverse:prevent_warp_cooldown"));
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockPos posNorth = pos.north(Math.round(this.getBbWidth() + 0.1F));
        BlockPos posSouth = pos.south(Math.round(this.getBbWidth() + 0.1F));
        BlockPos posEast = pos.east(Math.round(this.getBbWidth() + 0.1F));
        BlockPos posWest = pos.west(Math.round(this.getBbWidth() + 0.1F));
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);
        BlockState stateNorth = world.getBlockState(posNorth);
        BlockState stateSouth = world.getBlockState(posSouth);
        BlockState stateEast = world.getBlockState(posEast);
        BlockState stateWest = world.getBlockState(posWest);
        RandomSource rand = RandomSource.create();

        this.mv$characterAbilities(entity);
        this.mv$mushroomScale(entity);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                    || world.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                && !entity.hasData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                && (entity.fallDistance > 0 || entity.isInWaterOrBubble())
                && !(entity instanceof Player)
                && !entity.isSpectator())
            this.mv$squashEntity(entity);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                    || world.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                && (entity.onGround() || entity.isInWaterOrBubble())
                && this.mv$getConsecutiveBounces() > 0
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
            this.mv$setConsecutiveBounces(0);

        double deltaY = entity.getDeltaMovement().y;

        if ((entity.onGround() || entity.isInWaterOrBubble())
                && deltaY <= 0 && entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get()))
            entity.setData(DataAttachmentRegistry.HAS_HIT_BLOCK.get(), false);

        if (stateAboveEntity.is(TagRegistry.SMASHABLE_BLOCKS)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && (EventHooks.canEntityGrief(world, entity) || entity instanceof Player player && !player.mayFly())
                && !entity.onGround() && entity.getY() > entity.yOld
                && !entity.isSpectator() && !world.isClientSide
                && !entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get())) {
            this.mv$smashBlock(world, posAboveEntity, stateAboveEntity, entity);
        }

        if ((EventHooks.canEntityGrief(world, entity) || entity instanceof Player) && !world.isClientSide)
            this.mv$shellSmashBlock(stateNorth, entity, world, posNorth, stateSouth, posSouth, stateEast, posEast, stateWest, posWest);

        if (stateAboveEntity.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)
                && !entity.onGround() && entity.getY() > entity.yOld
                && !entity.isSpectator() && !world.isClientSide) {
            if (stateAboveEntity.hasProperty(QuestionBlock.EMPTY) && stateAboveEntity.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posAboveEntity, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posAboveEntity, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        this.mv$shellBonkBlock(stateNorth, entity, world, posNorth, stateSouth, posSouth, stateEast, posEast, stateWest, posWest);

        if (world.getBlockEntity(posAboveEntity) instanceof QuestionBlockEntity questionBlockEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && (EventHooks.canEntityGrief(world, entity) || entity instanceof Player player && !player.mayFly())
                && !entity.onGround() && entity.getY() > entity.yOld
                && !entity.isSpectator() && !world.isClientSide
                && !entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get()))
            this.mv$hitQuestionBlock(world, posAboveEntity, entity, questionBlockEntity);

        if ((EventHooks.canEntityGrief(world, entity) || entity instanceof Player) && !world.isClientSide)
            this.mv$shellHitQuestionBlock(world, posNorth, entity, posSouth, posEast, posWest);

        if (this.mv$getCheckpointFlagCooldown() > 0)
            this.mv$setCheckpointFlagCooldown(this.mv$getCheckpointFlagCooldown() - 1);

        if (this.mv$getFireballCooldown() > 0)
            this.mv$setFireballCooldown(this.mv$getFireballCooldown() - 1);

        if (this.mv$getIceBallCooldown() > 0)
            this.mv$setIceBallCooldown(this.mv$getIceBallCooldown() - 1);

        if (this.mv$getFreezeImmunityCooldown() > 0)
            this.mv$setFreezeImmunityCooldown(this.mv$getFreezeImmunityCooldown() - 1);

        if (this.mv$getFrozenCooldown() > 0)
            this.mv$setFrozenCooldown(this.mv$getFrozenCooldown() - 1);

        if (entity.getData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN, entity.getData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN) - 1);

        if (entity.getData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN) == 0
                && entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, false);
            entity.setData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME, false);
        }

        if (entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            this.mv$superStarKillEntity(entity);
            if (!entity.isInvisible()) {
                if (this.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.RAINBOW_GLINT.get(), serverWorld, entity);
                ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.RAINBOW_GLINT.get(), entity, true, 10, rand.nextDouble() * entity.getBbHeight(), 0.1);
            }
        }

        if (this.mv$hasDashMushroomBoost())
            this.mv$boostEntityParticles(entity.getVehicle(), entity);

        float f5 = this.mv$getEyeHeightScale();
        if (f5 != this.mv$appliedEyeHeightScale) {
            this.mv$appliedEyeHeightScale = f5;
            this.refreshDimensions();
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

//        if (this.getPersistentData().contains("marioverse:has_mega_mushroom") && this.getPersistentData().getBoolean("marioverse:has_mega_mushroom")) {
//            ScaleTypes.WIDTH.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.HEIGHT.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.JUMP_HEIGHT.getScaleData(this).setTargetScale(20.0F);
//            ScaleTypes.STEP_HEIGHT.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.REACH.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.ATTACK.getScaleData(this).setTargetScale(5.0F);
//        }
    }

    @Override
    public void mv$clearAllPowerUps() {
        mv$setFireFlower(false);
        mv$setIceFlower(false);
    }

    @Override
    public boolean mv$hasSuperMushroom() {
        return this.mv$hasSuperMushroom;
    }

    @Override
    public void mv$setSuperMushroom(boolean hasSuperMushroom) {
        this.mv$hasSuperMushroom = hasSuperMushroom;
    }

    @Override
    public boolean mv$hasSuperMushroomOverride() {
        return this.mv$hasSuperMushroomOverride;
    }

    @Override
    public void mv$setMushroomOverride(boolean hasSuperMushroomOverride) {
        this.mv$hasSuperMushroomOverride = hasSuperMushroomOverride;
    }

    @Override
    public boolean mv$hasDashMushroomBoost() {
        return this.mv$hasDashMushroomBoost;
    }

    @Override
    public void mv$setDashMushroomBoost(boolean hasDashMushroomBoost) {
        this.mv$hasDashMushroomBoost = hasDashMushroomBoost;
    }

    @Override
    public boolean mv$hasMegaMushroom() {
        return this.mv$hasMegaMushroom;
    }

    @Override
    public void mv$setMegaMushroom(boolean hasMegaMushroom) {
        this.mv$hasMegaMushroom = hasMegaMushroom;
    }

    @Override
    public boolean mv$hasFireFlower() {
        return this.mv$hasFireFlower;
    }

    @Override
    public void mv$setFireFlower(boolean hasFireFlower) {
        this.mv$hasFireFlower = hasFireFlower;
    }

    @Override
    public boolean mv$hasIceFlower() {
        return this.mv$hasIceFlower;
    }

    @Override
    public void mv$setIceFlower(boolean hasIceFlower) {
        this.mv$hasIceFlower = hasIceFlower;
    }

    @Override
    public int mv$getFireballCooldown() {
        return this.mv$fireballCooldown;
    }

    @Override
    public void mv$setFireballCooldown(int fireballCooldown) {
        this.mv$fireballCooldown = fireballCooldown;
    }

    @Override
    public int mv$getFireballCount() {
        return this.mv$fireballCount;
    }

    @Override
    public void mv$setFireballCount(int fireballCount) {
        this.mv$fireballCount = fireballCount;
    }

    @Override
    public int mv$getIceBallCooldown() {
        return this.mv$iceBallCooldown;
    }

    @Override
    public void mv$setIceBallCooldown(int iceBallCooldown) {
        this.mv$iceBallCooldown = iceBallCooldown;
    }

    @Override
    public int mv$getIceBallCount() {
        return this.mv$iceBallCount;
    }

    @Override
    public void mv$setIceBallCount(int iceBallCount) {
        this.mv$iceBallCount = iceBallCount;
    }

    @Override
    public int mv$getConsecutiveBounces() {
        return this.mv$consecutiveBounces;
    }

    @Override
    public void mv$setConsecutiveBounces(int consecutiveBounces) {
        this.mv$consecutiveBounces = consecutiveBounces;
    }

    @Override
    public int mv$getOneUpsRewarded() {
        return this.mv$oneUpsRewarded;
    }

    @Override
    public void mv$setOneUpsRewarded(int oneUpsRewarded) {
        this.mv$oneUpsRewarded = oneUpsRewarded;
    }

    @Override
    public boolean mv$doPreventWarp() {
        return this.mv$preventWarp;
    }

    @Override
    public void mv$setPreventWarp(boolean preventWarp) {
        this.mv$preventWarp = preventWarp;
    }

    @Override
    public int mv$getPreventWarpCooldown() {
        return this.mv$preventWarpCooldown;
    }

    @Override
    public void mv$setPreventWarpCooldown(int preventWarpCooldown) {
        this.mv$preventWarpCooldown = preventWarpCooldown;
    }

    @Override
    public int mv$getWarpCooldown() {
        return this.mv$warpCooldown;
    }

    @Override
    public void mv$setWarpCooldown(int warpCooldown) {
        this.mv$warpCooldown = warpCooldown;
    }

    @Override
    public int mv$getCheckpointFlagCooldown() {
        return this.mv$checkpointFlagCooldown;
    }

    @Override
    public void mv$setCheckpointFlagCooldown(int checkpointFlagCooldown) {
        this.mv$checkpointFlagCooldown = checkpointFlagCooldown;
    }

    @Override
    public int mv$getFreezeImmunityCooldown() {
        return this.mv$freezeImmunityCooldown;
    }

    @Override
    public void mv$setFreezeImmunityCooldown(int freezeImmunityCooldown) {
        this.mv$freezeImmunityCooldown = freezeImmunityCooldown;
    }

    @Override
    public int mv$getFrozenCooldown() {
        return this.mv$frozenCooldown;
    }

    @Override
    public void mv$setFrozenCooldown(int frozenCooldown) {
        this.mv$frozenCooldown = frozenCooldown;
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
                    ServerParticleUtils.spawnParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), serverWorld, vehicle, true, 10, 0.1);
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
                    ServerParticleUtils.spawnParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), serverWorld, vehicle, true, 10, 0.1);
                }
            } else this.mv$setDashMushroomBoost(false);
        } else if (speed >= minimumBoostSpeed) {
            if (entity.level().isClientSide) {
                ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.POWERED_UP.get(), entity, true, 5, 0.1, 0.0);
                ServerParticleUtils.spawnClientParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), entity, true, 10, 0.1, 0.0);
            } else if (entity.level() instanceof ServerLevel serverWorld) {
                ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.POWERED_UP.get(), serverWorld, entity);
                ServerParticleUtils.spawnParticleTrail(ParticleRegistry.SUSPENDED_FIRE.get(), serverWorld, entity, true, 10, 0.1);
            }
        } else this.mv$setDashMushroomBoost(false);
    }

    @Unique
    private void mv$shellSmashBlock(BlockState stateNorth, LivingEntity entity, Level world, BlockPos posNorth, BlockState stateSouth, BlockPos posSouth, BlockState stateEast, BlockPos posEast, BlockState stateWest, BlockPos posWest) {
        if ((stateNorth.is(TagRegistry.SMASHABLE_BLOCKS) || stateNorth.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && entity.getDeltaMovement().horizontalDistance() > 0.1
                && !entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get())) {
            this.mv$smashBlock(world, posNorth, stateNorth, entity);
            shell.bounceShell(world, Direction.NORTH);
        }

        if ((stateSouth.is(TagRegistry.SMASHABLE_BLOCKS) || stateSouth.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && entity.getDeltaMovement().horizontalDistance() > 0.1
                && !entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get())) {
            this.mv$smashBlock(world, posSouth, stateSouth, entity);
            shell.bounceShell(world, Direction.SOUTH);
        }

        if ((stateEast.is(TagRegistry.SMASHABLE_BLOCKS) || stateEast.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && entity.getDeltaMovement().horizontalDistance() > 0.1
                && !entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get())) {
            this.mv$smashBlock(world, posEast, stateEast, entity);
            shell.bounceShell(world, Direction.EAST);
        }

        if ((stateWest.is(TagRegistry.SMASHABLE_BLOCKS) || stateWest.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && entity.getDeltaMovement().horizontalDistance() > 0.1
                && !entity.getData(DataAttachmentRegistry.HAS_HIT_BLOCK.get())) {
            this.mv$smashBlock(world, posWest, stateWest, entity);
            shell.bounceShell(world, Direction.WEST);
        }
    }

    @Unique
    private void mv$shellBonkBlock(BlockState stateNorth, LivingEntity entity, Level world, BlockPos posNorth, BlockState stateSouth, BlockPos posSouth, BlockState stateEast, BlockPos posEast, BlockState stateWest, BlockPos posWest) {
        if (stateNorth.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)
                && entity instanceof KoopaShellEntity
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            if (stateNorth.hasProperty(QuestionBlock.EMPTY) && stateNorth.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posNorth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posNorth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateSouth.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)
                && entity instanceof KoopaShellEntity
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            if (stateSouth.hasProperty(QuestionBlock.EMPTY) && stateSouth.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posSouth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posSouth, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateEast.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)
                && entity instanceof KoopaShellEntity
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            if (stateEast.hasProperty(QuestionBlock.EMPTY) && stateEast.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posEast, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posEast, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (stateWest.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)
                && entity instanceof KoopaShellEntity
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
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
        AttributeInstance jumpAttribute = entity.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance safeFallAttribute = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        boolean isMini = entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        Vec3 motion = entity.getDeltaMovement();
        boolean hasCostume = this.mv$hasMarioCostume(entity)
                || this.mv$hasLuigiCostume(entity)
                || this.mv$hasPeachCostume(entity);

        if (jumpAttribute != null) {
            boolean hasJumpModifier = jumpAttribute.getModifier(AttributesRegistry.JUMP_BOOST) != null;
            boolean hasRunningJumpModifier = jumpAttribute.getModifier(AttributesRegistry.RUNNING_JUMP_BOOST) != null;
            boolean isRunning = entity.isSprinting();
            double normalJumpBoost = 0.0;
            double runningJumpBoost = 0.0;

            if (hasCostume) {
                if (this.mv$hasPeachCostume(entity)) {
                    normalJumpBoost = 0.3;
                    runningJumpBoost = 0.4;
                } else {
                    normalJumpBoost = 0.5;
                    runningJumpBoost = 0.6;
                }
            }

            if (isMini) {
                if (hasCostume) {
                    normalJumpBoost *= 1.4;
                    runningJumpBoost *= 1.6;
                } else {
                    normalJumpBoost = 0.5;
                    runningJumpBoost = 0.6;
                }
            }

            if (!entity.isShiftKeyDown() && (hasCostume || isMini)) {
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

            if (isMini) {
                if (hasCostume)
                    safeFallDistance= 16;
                else safeFallDistance = 14;
            } else if (hasCostume)
                safeFallDistance= 7;

            if (hasCostume)
                mv$setModifier(safeFallAttribute, AttributesRegistry.SAFE_FALL_DISTANCE, safeFallDistance);
            else mv$setModifier(safeFallAttribute, AttributesRegistry.SAFE_FALL_DISTANCE, 0);
        }

        if (this.mv$hasPeachCostume(entity)) {
            if (motion.y < 0)
                entity.setDeltaMovement(motion.x, motion.y * 0.7, motion.z);
        }

        if (isMini) {
            if (motion.y < 0)
                entity.setDeltaMovement(motion.x, motion.y * 0.8, motion.z);
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

        if (heightScale > 1) heightScale /= 2;
        if (widthScale > 1) widthScale /= 2;
        if (eyeScale > 1) eyeScale /= 2;

        float newWidth = original.width() * widthScale;
        float newHeight = original.height() * heightScale;
        float newEyeHeight = original.eyeHeight() * eyeScale;

        EntityDimensions resized = original.fixed()
                ? EntityDimensions.fixed(newWidth, newHeight)
                : EntityDimensions.scalable(newWidth, newHeight);

        if (eyeScale == 1.0F && heightScale == 1.0F && widthScale == 1.0F)
            return original;

        if (pose == Pose.SLEEPING)
            return original;

        return resized.withEyeHeight(newEyeHeight);
    }

//    @Inject(method = "getPassengerRidingPosition", at = @At("TAIL"), cancellable = true)
//    private void getPassengerRidingPosition(Entity entity, CallbackInfoReturnable<Vec3> cir) {
//        if (entity instanceof LivingEntity livingEntity)
//            cir.setReturnValue(cir.getReturnValue().add(this.getPassengerAttachmentPoint(entity, entity
//                    .getDimensions(entity.getPose()), this.getHeightScale() * livingEntity.getAgeScale())));
//    }

    @Unique
    private static final ResourceLocation SLOWDOWN_MODIFIER =
            ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "mini_goomba_slowdown");
    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void jumpFromGround(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Remove the speed modifier when the entity jumps
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SLOWDOWN_MODIFIER))
            speedAttribute.removeModifier(SLOWDOWN_MODIFIER);
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
    public void mv$mushroomScale(LivingEntity entity) {
        Level world = entity.level();
        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        boolean hasMiniMushroom = entity.hasData(DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        boolean hasSuperMushroom = this.mv$hasSuperMushroom();
        float health = entity.getHealth();
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasSuperMushroom ? 1.0D : hasMiniMushroom ? 0.235D : 0.485D;
        double targetHeightScale = hasSuperMushroom ? 1.0D : hasMiniMushroom ? 0.25D : 0.5D;
        double targetWidthScale = hasSuperMushroom ? 1.0D : hasMiniMushroom ? 0.35D : 0.75D;

        boolean isPlayer = entity instanceof Player;
        boolean shouldShrink = !hasSuperMushroom
                && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                && (isPlayer && this.mv$hasSuperMushroomOverride()
                    || (isPlayer && health <= ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get()
                        && (ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                            || world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_PLAYERS)))
                || (!isPlayer && this.mv$hasSuperMushroomOverride()
                    || !isPlayer && health <= entity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get()
                    && (ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                        || world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_ALL_MOBS))))
                || !hasSuperMushroom && hasMiniMushroom;

        boolean shouldReset = hasSuperMushroom
                && (isPlayer && this.mv$hasSuperMushroomOverride()
                    || (isPlayer && health > ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get()
                        && (ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get()
                            || world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_PLAYERS)))
                || (!isPlayer && this.mv$hasSuperMushroomOverride()
                    || !isPlayer && health > entity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get()
                    && (ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()
                        || world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_ALL_MOBS))))
                || hasSuperMushroom && !hasMiniMushroom;

        if (shouldShrink && mv$currentHeightScale != targetHeightScale && mv$currentWidthScale != targetWidthScale) {
            if (entity.getLastDamageSource() != null
                    && entity.isDamageSourceBlocked(entity.getLastDamageSource()))
                return;
            mv$updateScale(eyeHeightScale, mv$currentEyeHeightScale,
                    targetEyeHeightScale, scalingSpeed, v -> mv$currentEyeHeightScale = v);
            mv$updateScale(heightScale, mv$currentHeightScale,
                    targetHeightScale, scalingSpeed, v -> mv$currentHeightScale = v);
            mv$updateScale(widthScale, mv$currentWidthScale,
                    targetWidthScale, scalingSpeed, v -> mv$currentWidthScale = v);
        }

        if (shouldReset && mv$currentHeightScale != targetHeightScale && mv$currentWidthScale != targetWidthScale) {
            if (eyeHeightScale != null && eyeHeightScale.getValue() != 1.0D)
                mv$updateScale(eyeHeightScale, mv$currentEyeHeightScale,
                        targetEyeHeightScale, scalingSpeed, v -> mv$currentEyeHeightScale = v);

            if (heightScale != null && heightScale.getValue() != 1.0D)
                mv$updateScale(heightScale, mv$currentHeightScale,
                        targetHeightScale, scalingSpeed, v -> mv$currentHeightScale = v);

            if (widthScale != null && widthScale.getValue() != 1.0D)
                mv$updateScale(widthScale, mv$currentWidthScale,
                        targetWidthScale, scalingSpeed, v -> mv$currentWidthScale = v);
        }
    }

    @Unique
    private void mv$updateScale(AttributeInstance scaleAttribute, double currentScale, double targetScale, float scalingSpeed, Consumer<Double> setter) {
        ResourceLocation modifier = AttributesRegistry.DAMAGED_SCALE;

        if (scaleAttribute != null) {
            double lerpedScale = Mth.lerp(scalingSpeed, currentScale, targetScale);

            if (Math.abs(currentScale - targetScale) < 0.0001)
                lerpedScale = targetScale;

            if (scaleAttribute.hasModifier(modifier) && (Math.abs(lerpedScale - 1.0D) < 0.0001 || targetScale == 1.0D))
                scaleAttribute.removeModifier(modifier);

            if (lerpedScale != targetScale) {
                scaleAttribute.removeModifier(modifier);
                scaleAttribute.addPermanentModifier(new AttributeModifier(modifier, lerpedScale - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

                if (Math.abs(currentScale - targetScale) < 0.01)
                    setter.accept(targetScale);
                else setter.accept(lerpedScale);
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
                            this.mv$consecutiveReward(attackingEntity, entity);
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
                            || ConfigRegistry.STOMP_ALL_MOBS.get()
                            || stompingEntity.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS))) {

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
                                    && !stompingEntity.hasData(DataAttachmentRegistry.HAS_MINI_MUSHROOM))
                                damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), damagedEntity.getHealth());
                            else if (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get()
                                    || stompingEntity.level().getGameRules().getBoolean(Marioverse.STOMP_ALL_MOBS)) {
                                if (stompingEntity.hasData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                                        || damagedEntity instanceof KoopaTroopaEntity
                                        || damagedEntity instanceof KoopaShellEntity)
                                    damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), 0);
                                else damagedEntity.hurt(DamageSourceRegistry.stomp(damagedEntity, stompingEntity), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                            }
                            if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get())
                                this.mv$consecutiveReward(stompingEntity, damagedEntity);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Unique
    public void mv$consecutiveReward(LivingEntity attackingEntity, LivingEntity damagedEntity) {
        int oneUpsRewarded = this.mv$getOneUpsRewarded();
        int consecutiveBounces = this.mv$getConsecutiveBounces();
        this.mv$setConsecutiveBounces(consecutiveBounces + 1);

        if (consecutiveBounces == 0) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GOOD.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.good"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 1) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GREAT.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.great"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 2) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.SUPER.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.super"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 3) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.FANTASTIC.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.fantastic"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 4) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.EXCELLENT.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.excellent"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 5) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.INCREDIBLE.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.incredible"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 6) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.WONDERFUL.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.wonderful"), Boolean.TRUE);
        }
        else if (consecutiveBounces >= 7 && ConfigRegistry.MAX_ONE_UP_BOUNCE_REWARD.get() > oneUpsRewarded) {
            this.mv$setOneUpsRewarded(oneUpsRewarded + 1);
            this.mv$bounceReward(attackingEntity);
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, damagedEntity, 1.0);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.one_up"), Boolean.TRUE);
        }
    }

    @Unique
    public void mv$bounceReward(LivingEntity entity) {
        ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;
        if (entity instanceof LivingEntity livingEntity
                && (ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get() || entity.getType().is(TagRegistry.CAN_CONSUME_ONE_UPS))) {
            AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
            ItemStack offhandStack = livingEntity.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get()))
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            else if (offhandStack.isEmpty())
                livingEntity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
            else if (offhandStack.getItem() instanceof OneUpMushroomItem)
                offhandStack.grow(1);
            this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.NEUTRAL, 1.0F, 1.0F);
        }
    }
}
