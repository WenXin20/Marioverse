package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.entities.KoopaTroopaEntity;
import com.wenxin2.marioverse.network.client_bound.data.OneUpPayload;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageTypeRegistry;
import com.wenxin2.marioverse.registries.ItemRegistry;
import com.wenxin2.marioverse.registries.ParticleRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import com.wenxin2.marioverse.utils.BlockWarpEntityHandler;
import com.wenxin2.marioverse.utils.EntityWarpEntityHandler;
import com.wenxin2.marioverse.utils.PowerUpHandler;
import com.wenxin2.marioverse.utils.ServerParticleUtils;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
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
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DecoratedPotBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements BlockWarpEntityHandler, EntityWarpEntityHandler, PowerUpHandler {
    @Shadow public abstract void setSpeed(float p_21320_);

    @Unique private static final int MAX_PARTICLE_AMOUNT = 100;
    @Unique private boolean mv$playedDamagedSound;
    @Unique protected float mv$appliedEyeHeightScale = 1.0F;
    @Unique protected float mv$appliedHeightScale = 1.0F;
    @Unique protected float mv$appliedWidthScale = 1.0F;
    @Unique private boolean mv$hasFireFlower;
    @Unique private boolean mv$hasIceFlower;
    @Unique private boolean mv$hasMegaMushroom;
    @Unique private boolean mv$hasMushroom;
    @Unique private boolean mv$hasSuperStar;
    @Unique private boolean mv$preventWarp;
    @Unique private int mv$oneUpsRewarded;
    @Unique private int mv$preventWarpCooldown;
    @Unique private int mv$superStarCooldown;
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
        tag.putBoolean("marioverse:has_fire_flower", this.mv$hasFireFlower());
        tag.putBoolean("marioverse:has_ice_flower", this.mv$hasIceFlower());
        tag.putBoolean("marioverse:has_mega_mushroom", this.mv$hasMegaMushroom());
        tag.putBoolean("marioverse:has_mushroom", this.mv$hasMushroom());
        tag.putBoolean("marioverse:has_super_star", this.mv$hasSuperStar());
        tag.putBoolean("marioverse:prevent_warp", this.mv$doPreventWarp());

        tag.putInt("marioverse:one_ups_rewarded", this.mv$getOneUpsRewarded());
        tag.putInt("marioverse:prevent_warp_cooldown", this.mv$getPreventWarpCooldown());
        tag.putInt("marioverse:super_star_cooldown", this.mv$getSuperStarCooldown());
        tag.putInt("marioverse:warp_cooldown", this.mv$getWarpCooldown());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        this.mv$setFireFlower(tag.getBoolean("marioverse:has_fire_flower"));
        this.mv$setIceFlower(tag.getBoolean("marioverse:has_ice_flower"));
        this.mv$setMegaMushroom(tag.getBoolean("marioverse:has_mega_mushroom"));
        this.mv$setMushroom(tag.getBoolean("marioverse:has_mushroom"));
        this.mv$setSuperStar(tag.getBoolean("marioverse:has_super_star"));
        this.mv$setPreventWarp(tag.getBoolean("marioverse:prevent_warp"));

        this.mv$setOneUpsRewarded(tag.getInt("marioverse:one_ups_rewarded"));
        this.mv$setPreventWarpCooldown(tag.getInt("marioverse:prevent_warp_cooldown"));
        this.mv$setSuperStarCooldown(tag.getInt("marioverse:super_star_cooldown"));
        this.mv$setWarpCooldown(tag.getInt("marioverse:warp_cooldown"));
    }

    @Inject( method = "tick", at = @At("TAIL"))
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

        int checkpointCooldown = entity.getPersistentData().getInt("marioverse:claimed_checkpoint_flag_cooldown");
        int fireballCooldown = entity.getPersistentData().getInt("marioverse:fireball_cooldown");
        int iceBallCooldown = entity.getPersistentData().getInt("marioverse:ice_ball_cooldown");
        int iceCubeCooldown = entity.getPersistentData().getInt("marioverse:frozen_in_ice_cube_cooldown");
        int consecutiveBounces = entity.getPersistentData().getInt("marioverse:consecutive_bounces");

        this.mv$characterAbilities(entity);
        this.mv$entityScale(entity);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                && (entity.fallDistance > 0 || entity.isInWaterOrBubble())
                && !(entity instanceof Player)
                && !entity.isSpectator())
            this.mv$squashEntity(entity);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                && (entity.onGround() || entity.isInWaterOrBubble())
                && (consecutiveBounces > 0 || this.mv$getOneUpsRewarded() > 0)
                && !this.mv$hasSuperStar()) {
            entity.getPersistentData().putInt("marioverse:consecutive_bounces", 0);
            this.mv$setOneUpsRewarded(0);
        }

        if (entity.onGround() && entity.getDeltaMovement().y <= 0
                && entity.getPersistentData().getBoolean("marioverse:has_smashed_block"))
            entity.getPersistentData().putBoolean("marioverse:has_smashed_block", false);

        double deltaY = entity.getDeltaMovement().y;
        if (stateAboveEntity.is(TagRegistry.SMASHABLE_BLOCKS)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && !entity.getPersistentData().getBoolean("marioverse:has_smashed_block")
                && !entity.onGround() && deltaY > -0.079
                && !entity.isSpectator()) {
            this.mv$smashBlock(world, posAboveEntity, stateAboveEntity, entity);
        }

        this.mv$shellSmashBlock(stateNorth, entity, world, posNorth, stateSouth, posSouth, stateEast, posEast, stateWest, posWest);

        if (stateAboveEntity.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)
                && !entity.onGround() && deltaY > -0.079
                && !entity.isSpectator())
            if (stateAboveEntity.hasProperty(QuestionBlock.EMPTY) && stateAboveEntity.getValue(QuestionBlock.EMPTY))
                world.playSound(null, posAboveEntity, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, posAboveEntity, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        this.mv$shellBonkBlock(stateNorth, entity, world, posNorth, stateSouth, posSouth, stateEast, posEast, stateWest, posWest);

        if (world.getBlockEntity(posAboveEntity) instanceof QuestionBlockEntity questionBlockEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && !entity.onGround() && deltaY > -0.079
                && !entity.isSpectator())
            this.mv$hitQuestionBlock(world, posAboveEntity, questionBlockEntity);

        this.marioiverse$shellHitQuestionBlock(world, posNorth, entity, posSouth, posEast, posWest);

        if (checkpointCooldown > 0)
            entity.getPersistentData().putInt("marioverse:claimed_checkpoint_flag_cooldown", checkpointCooldown - 1);

        if (fireballCooldown > 0)
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", fireballCooldown - 1);

        if (iceBallCooldown > 0)
            entity.getPersistentData().putInt("marioverse:ice_ball_cooldown", iceBallCooldown - 1);

        if (iceCubeCooldown > 0)
            entity.getPersistentData().putInt("marioverse:frozen_in_ice_cube_cooldown", iceCubeCooldown - 1);

        if (this.mv$getSuperStarCooldown() > 0)
            this.mv$setSuperStarCooldown(this.mv$getSuperStarCooldown() - 1);

        if (this.mv$getSuperStarCooldown() == 0 && this.mv$hasSuperStar())
            this.mv$setSuperStar(false);

        if (this.mv$hasSuperStar()) {
            this.mv$superStarKillEntity(entity);
            if (this.level() instanceof ServerLevel serverWorld)
                ServerParticleUtils.spawnSingleParticleOnEntityRandomly(ParticleRegistry.COIN_GLINT.get(), serverWorld, this);
            this.mv$playSuperStarTheme();
        } else if (!this.mv$hasSuperStar() && this.mv$playedStarTheme)
            this.mv$playedStarTheme = false;

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
    public boolean mv$hasMushroom() {
        return this.mv$hasMushroom;
    }

    @Override
    public void mv$setMushroom(boolean hasMushroom) {
        this.mv$hasMushroom = hasMushroom;
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
    public boolean mv$hasSuperStar() {
        return this.mv$hasSuperStar;
    }

    @Override
    public void mv$setSuperStar(boolean hasSuperStar) {
        this.mv$hasSuperStar = hasSuperStar;
    }

    @Override
    public int mv$getSuperStarCooldown() {
        return this.mv$superStarCooldown;
    }

    @Override
    public void mv$setSuperStarCooldown(int superStarCooldown) {
        this.mv$superStarCooldown = superStarCooldown;
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

    @Unique
    private void mv$shellSmashBlock(BlockState stateNorth, LivingEntity entity, Level world, BlockPos posNorth, BlockState stateSouth, BlockPos posSouth, BlockState stateEast, BlockPos posEast, BlockState stateWest, BlockPos posWest) {
        if ((stateNorth.is(TagRegistry.SMASHABLE_BLOCKS) || stateNorth.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && !entity.getPersistentData().getBoolean("marioverse:has_smashed_block")
                && entity.getDeltaMovement().horizontalDistance() > 0.1) {
            mv$smashBlock(world, posNorth, stateNorth, entity);
            shell.bounceShell(world, Direction.NORTH);
        }

        if ((stateSouth.is(TagRegistry.SMASHABLE_BLOCKS) || stateSouth.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && !entity.getPersistentData().getBoolean("marioverse:has_smashed_block")
                && entity.getDeltaMovement().horizontalDistance() > 0.1) {
            mv$smashBlock(world, posSouth, stateSouth, entity);
            shell.bounceShell(world, Direction.SOUTH);
        }

        if ((stateEast.is(TagRegistry.SMASHABLE_BLOCKS) || stateEast.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && !entity.getPersistentData().getBoolean("marioverse:has_smashed_block")
                && entity.getDeltaMovement().horizontalDistance() > 0.1) {
            mv$smashBlock(world, posEast, stateEast, entity);
            shell.bounceShell(world, Direction.EAST);
        }

        if ((stateWest.is(TagRegistry.SMASHABLE_BLOCKS) || stateWest.getBlock() instanceof DecoratedPotBlock)
                 && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)
                && entity instanceof KoopaShellEntity shell
                && !entity.getPersistentData().getBoolean("marioverse:has_smashed_block")
                && entity.getDeltaMovement().horizontalDistance() > 0.1) {
            mv$smashBlock(world, posWest, stateWest, entity);
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
    private void marioiverse$shellHitQuestionBlock(Level world, BlockPos posNorth, LivingEntity entity, BlockPos posSouth, BlockPos posEast, BlockPos posWest) {
        if (world.getBlockEntity(posNorth) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posNorth, questionBlockEntity);

        if (world.getBlockEntity(posSouth) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posSouth, questionBlockEntity);

        if (world.getBlockEntity(posEast) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posEast, questionBlockEntity);

        if (world.getBlockEntity(posWest) instanceof QuestionBlockEntity questionBlockEntity
                && entity instanceof KoopaShellEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && entity.getDeltaMovement().horizontalDistance() > 0.1)
            this.mv$hitQuestionBlock(world, posWest, questionBlockEntity);
    }

    @Unique
    private void mv$characterAbilities(LivingEntity entity) {
        AttributeInstance jumpAttribute = entity.getAttribute(Attributes.JUMP_STRENGTH);
        AttributeInstance safeFallAttribute = entity.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        AttributeInstance gravityAttribute = entity.getAttribute(Attributes.GRAVITY);

        if (jumpAttribute != null) {
//            Minecraft minecraft = Minecraft.getInstance();
//            KeyMapping sprintKey = minecraft.options.keySprint;
            double normalJumpBoost = 0.4;
            double runningJumpBoost = 0.5;
            boolean hasJumpModifier = jumpAttribute.getModifier(AttributesRegistry.JUMP_BOOST) != null;
            boolean hasRunningJumpModifier = jumpAttribute.getModifier(AttributesRegistry.RUNNING_JUMP_BOOST) != null;
            boolean isRunning = entity.isSprinting();

            if (this.mv$hasLuigiCostume(entity)) {
                normalJumpBoost = 0.5;
                runningJumpBoost = 0.6;
            }

            if (this.mv$hasMarioCostume(entity)
                    || this.mv$hasLuigiCostume(entity)
                    || this.mv$hasPeachCostume(entity)) {
                if (isRunning) {
                    if (!hasRunningJumpModifier)
                        jumpAttribute.addPermanentModifier(new AttributeModifier(AttributesRegistry.RUNNING_JUMP_BOOST, runningJumpBoost, AttributeModifier.Operation.ADD_VALUE));
                    if (hasJumpModifier)
                        jumpAttribute.removeModifier(AttributesRegistry.JUMP_BOOST);
                } else {
                    if (!hasJumpModifier)
                        jumpAttribute.addPermanentModifier(new AttributeModifier(AttributesRegistry.JUMP_BOOST, normalJumpBoost, AttributeModifier.Operation.ADD_VALUE));
                    if (hasRunningJumpModifier)
                        jumpAttribute.removeModifier(AttributesRegistry.RUNNING_JUMP_BOOST);
                }
            } else {
                if (hasRunningJumpModifier)
                    jumpAttribute.removeModifier(AttributesRegistry.RUNNING_JUMP_BOOST);
                if (hasJumpModifier)
                    jumpAttribute.removeModifier(AttributesRegistry.JUMP_BOOST);
            }
        }

        if (safeFallAttribute != null) {
            if (this.mv$hasMarioCostume(entity)
                    || this.mv$hasLuigiCostume(entity)
                    || this.mv$hasPeachCostume(entity)) {
                boolean hasSafeFallModifier = safeFallAttribute.getModifier(AttributesRegistry.SAFE_FALL_DISTANCE) != null;
                if (!hasSafeFallModifier)
                    safeFallAttribute.addPermanentModifier(new AttributeModifier(AttributesRegistry.SAFE_FALL_DISTANCE, 7, AttributeModifier.Operation.ADD_VALUE));
            }
            else safeFallAttribute.removeModifier(AttributesRegistry.SAFE_FALL_DISTANCE);
        }

        if (this.mv$hasPeachCostume(entity)) {
            Vec3 motion = entity.getDeltaMovement();
            if (motion.y < 0)
                entity.setDeltaMovement(motion.x, motion.y * 0.7, motion.z);
        }
    }

    @Unique
    private boolean mv$hasMarioCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.MARIO_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.MARIO_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.MARIO_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.MARIO_COSTUMES))
            return true;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            if (containerHat != null && containerShirt != null && containerPants != null && containerShoes != null) {
                ItemStack stackHat = containerHat.getAccessories().getItem(0);
                ItemStack stackShirt = containerShirt.getAccessories().getItem(0);
                ItemStack stackPants = containerPants.getAccessories().getItem(0);
                ItemStack stackShoes = containerShoes.getAccessories().getItem(0);
                return stackHat.is(TagRegistry.MARIO_COSTUMES) && stackShirt.is(TagRegistry.MARIO_COSTUMES)
                        && stackPants.is(TagRegistry.MARIO_COSTUMES) && stackShoes.is(TagRegistry.MARIO_COSTUMES);
            }
        }
        return false;
    }

    @Unique
    private boolean mv$hasLuigiCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.LUIGI_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.LUIGI_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.LUIGI_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.LUIGI_COSTUMES))
            return true;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            if (containerHat != null && containerShirt != null && containerPants != null && containerShoes != null) {
                ItemStack stackHat = containerHat.getAccessories().getItem(0);
                ItemStack stackShirt = containerShirt.getAccessories().getItem(0);
                ItemStack stackPants = containerPants.getAccessories().getItem(0);
                ItemStack stackShoes = containerShoes.getAccessories().getItem(0);
                return stackHat.is(TagRegistry.LUIGI_COSTUMES) && stackShirt.is(TagRegistry.LUIGI_COSTUMES)
                        && stackPants.is(TagRegistry.LUIGI_COSTUMES) && stackShoes.is(TagRegistry.LUIGI_COSTUMES);
            }
        }
        return false;
    }

    @Unique
    private boolean mv$hasPeachCostume(LivingEntity entity) {
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(TagRegistry.PEACH_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.CHEST).is(TagRegistry.PEACH_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.LEGS).is(TagRegistry.PEACH_COSTUMES)
                && entity.getItemBySlot(EquipmentSlot.FEET).is(TagRegistry.PEACH_COSTUMES))
            return true;

        AccessoriesCapability capability = AccessoriesCapability.get(entity);
        if (capability != null) {
            AccessoriesContainer containerHat = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_hat"));
            AccessoriesContainer containerShirt = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shirt"));
            AccessoriesContainer containerPants = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_pants"));
            AccessoriesContainer containerShoes = capability.getContainer(SlotTypeLoader.getSlotType(entity, "costume_shoes"));

            if (containerHat != null && containerShirt != null && containerPants != null && containerShoes != null) {
                ItemStack stackHat = containerHat.getAccessories().getItem(0);
                ItemStack stackShirt = containerShirt.getAccessories().getItem(0);
                ItemStack stackPants = containerPants.getAccessories().getItem(0);
                ItemStack stackShoes = containerShoes.getAccessories().getItem(0);
                return stackHat.is(TagRegistry.PEACH_COSTUMES) && stackShirt.is(TagRegistry.PEACH_COSTUMES)
                        && stackPants.is(TagRegistry.PEACH_COSTUMES) && stackShoes.is(TagRegistry.PEACH_COSTUMES);
            }
        }
        return false;
    }

    @Inject(method = "getArmorValue", at = @At("RETURN"), cancellable = true)
    private void getArmorValue(CallbackInfoReturnable<Integer> info) {
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
                if (container == null) return;
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

            int newArmorValue = info.getReturnValue() + totalExtraArmor;
            info.setReturnValue(newArmorValue);
        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return;
        } else {
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
                        info.setReturnValue(true);
                        this.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                                SoundSource.PLAYERS, 1.0F, 1.0F);
                        livingEntity.setHealth(1.0F);
                        livingEntity.heal(ConfigRegistry.ONE_UP_HEALTH_HEALED.get().floatValue());
                        stackCharm.shrink(1);

                        if (livingEntity.level() instanceof ServerLevel serverWorld) {
                            ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, livingEntity, 25);
                            ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, livingEntity);
                            if (livingEntity instanceof ServerPlayer player)
                                PacketDistributor.sendToPlayer(player, new OneUpPayload(true));
                        }

                        if (livingEntity instanceof ServerPlayer serverplayer) {
                            serverplayer.awardStat(Stats.ITEM_USED.get(ItemRegistry.ONE_UP_MUSHROOM.get()), 1);
                            CriteriaTriggers.USED_TOTEM.trigger(serverplayer, stack);
                            this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                        }
                    }
                }
            }

            if (!stack.isEmpty() && stack.getItem() instanceof OneUpMushroomItem) {
                info.setReturnValue(true);
                this.level().playSound(null, livingEntity.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                livingEntity.setHealth(1.0F);
                livingEntity.heal(ConfigRegistry.ONE_UP_HEALTH_HEALED.get().floatValue());
                stack.shrink(1);

                if (livingEntity.level() instanceof ServerLevel serverWorld) {
                    ServerParticleUtils.spawnPoweredUpParticles(ParticleRegistry.POWERED_UP.get(), serverWorld, livingEntity, 25);
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, livingEntity);
                    if (livingEntity instanceof ServerPlayer player)
                        PacketDistributor.sendToPlayer(player, new OneUpPayload(true));
                }

                if (livingEntity instanceof ServerPlayer serverplayer) {
                    serverplayer.awardStat(Stats.ITEM_USED.get(ItemRegistry.ONE_UP_MUSHROOM.get()), 1);
                    CriteriaTriggers.USED_TOTEM.trigger(serverplayer, stack);
                    this.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
                }
            }
        }
    }

    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    public void isDamageSourceBlocked(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        if (source.is(TagRegistry.SHIELD_BLOCKS) && livingEntity.isBlocking()) {
            Vec3 vec32 = source.getSourcePosition();
            if (vec32 != null) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
    private static void addCustomAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        AttributeSupplier.Builder builder = cir.getReturnValue();

        builder.add(AttributesRegistry.EYE_HEIGHT_SCALE);
        builder.add(AttributesRegistry.HEIGHT_SCALE);
        builder.add(AttributesRegistry.WIDTH_SCALE);

        cir.setReturnValue(builder);
    }

    @Inject(method = "getDimensions", at = @At("TAIL"), cancellable = true)
    private void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        float eyeHeightScale;
        float heightScale;
        float widthScale;

        if (this.mv$getHeightScale() > 1)
            heightScale = this.mv$getHeightScale() / 2;
        else if (this.mv$getHeightScale() == 1)
            heightScale = this.mv$getHeightScale();
        else heightScale = this.mv$getHeightScale();

        if (this.mv$getEyeHeightScale() > 1)
            eyeHeightScale = cir.getReturnValue().eyeHeight() * this.mv$getEyeHeightScale() / 2;
        else if (this.mv$getEyeHeightScale() == 1)
            eyeHeightScale = cir.getReturnValue().eyeHeight() * this.mv$getEyeHeightScale();
        else eyeHeightScale = cir.getReturnValue().eyeHeight() * this.mv$getEyeHeightScale();

        if (this.mv$getWidthScale() > 1)
            widthScale = this.mv$getWidthScale() / 2;
        else if (this.mv$getWidthScale() == 1)
            widthScale = this.mv$getWidthScale();
        else widthScale = this.mv$getWidthScale();

        if (pose != Pose.SLEEPING) {
            EntityDimensions customDimensions = cir.getReturnValue()
                    .scale(widthScale, heightScale)
                    .withEyeHeight(eyeHeightScale);

            cir.setReturnValue(customDimensions);
        }
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
    private void onJumpFromGround(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Remove the speed modifier when the entity jumps
        AttributeInstance speedAttribute = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttribute != null && speedAttribute.hasModifier(SLOWDOWN_MODIFIER)) {
            speedAttribute.removeModifier(SLOWDOWN_MODIFIER);
        }
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
    public void mv$rewardParticles(LivingEntity entity, ParticleOptions particleType) {
        if (entity.level() instanceof ServerLevel serverWorld)
            serverWorld.sendParticles(particleType, entity.getX(),
                    entity.getY() + entity.getBbHeight() + 1.0,
                    entity.getZ(), 1, 0, 1.0, 0, 0.5);
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
    public void mv$entityScale(LivingEntity entity) {
        Level world = entity.level();
        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        boolean hasMushroom = this.mv$hasMushroom();
        float health = entity.getHealth();
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasMushroom ? 1.0D : 0.5D;
        double targetHeightScale = hasMushroom ? 1.0D : 0.5D;
        double targetWidthScale = hasMushroom ? 1.0D : 0.75D;

        boolean isPlayer = entity instanceof Player;
        boolean shouldShrink =  !hasMushroom
                && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)
                && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                && ((isPlayer && health <= ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get() && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get())
                || (!isPlayer && health <= entity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get() && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()));

        boolean shouldReset = (isPlayer && health > ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get() && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get())
                || (!isPlayer && health > entity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get() && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get());

        if (shouldShrink) {
            if (entity.getLastDamageSource() != null
                    && entity.isDamageSourceBlocked(entity.getLastDamageSource()))
                return;
            mv$updateScale(eyeHeightScale, targetEyeHeightScale, scalingSpeed);
            mv$updateScale(heightScale, targetHeightScale, scalingSpeed);
            mv$updateScale(widthScale, targetWidthScale, scalingSpeed);

            if (!mv$playedDamagedSound) {
                mv$playedDamagedSound = true;
                SoundSource soundSource = isPlayer ? SoundSource.PLAYERS : SoundSource.NEUTRAL;
                world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(), soundSource, 1.0F, 1.0F);
            }
        }

        if (shouldReset) {
            mv$playedDamagedSound = false;
            mv$updateScale(eyeHeightScale, targetEyeHeightScale, scalingSpeed);
            mv$updateScale(heightScale, targetHeightScale, scalingSpeed);
            mv$updateScale(widthScale, targetWidthScale, scalingSpeed);
        }
    }

    @Unique
    private void mv$updateScale(AttributeInstance scaleAttribute, double targetScale, float scalingSpeed) {
        if (scaleAttribute != null) {
            ResourceLocation modifier = AttributesRegistry.DAMAGED_SCALE;
            double currentScale = scaleAttribute.getValue();
            double lerpedScale = Mth.lerp(scalingSpeed, currentScale, targetScale);

            if (Math.abs(currentScale - targetScale) < 0.001)
                lerpedScale = targetScale;

            if (scaleAttribute.hasModifier(modifier) && lerpedScale != targetScale || targetScale == 1.0)
                scaleAttribute.removeModifier(modifier);
            if (lerpedScale != targetScale)
                scaleAttribute.addPermanentModifier(new AttributeModifier(modifier, lerpedScale - 1.0D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));
        }
    }

    @Unique
    public void mv$dropCoin(Level world, BlockPos pos, Entity entity) {
        if (world.getBlockState(pos.above()).getBlock() instanceof CoinBlock) {
            ItemStack coinItem = new ItemStack(world.getBlockState(pos.above()).getBlock());

            this.level().broadcastEntityEvent(entity, (byte) 125); // Coin Glint particle
            world.playSound(null, pos.above(), SoundRegistry.COIN_PICKUP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

            if (entity instanceof Player player) {
                world.removeBlock(pos.above(), false);
                player.getInventory().add(coinItem);

                if (!player.getInventory().add(coinItem)) {
                    player.drop(coinItem, false);
                }
            } else world.removeBlock(pos.above(), true);
        }
    }

    @Unique
    public void mv$hitQuestionBlock(Level world, BlockPos pos, QuestionBlockEntity questionBlockEntity) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (world.getBlockState(pos).getBlock() instanceof QuestionBlock questionBlock) {
            ItemStack storedItem = questionBlockEntity.getTheItem();

            if (!world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                mv$hitEntityAbove(pos, world, entity);
            }

            if (!storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                this.mv$dropCoin(world, pos, this);

                if (!world.isClientSide)
                    questionBlock.spawnFromQuestionBlock(world, pos, storedItem, entity, Boolean.FALSE, Boolean.TRUE);

                if (world.getBlockState(pos).is(BlockTags.GUARDED_BY_PIGLINS) && entity instanceof Player player)
                    PiglinAi.angerNearbyPiglins(player, false);

                if (world instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnParticlesOnBlockFace(ParticleTypes.CRIT, serverWorld, pos, Direction.DOWN,
                            UniformInt.of(3, 4), () -> ServerParticleUtils.getRandomSpeedRanges(world.getRandom()), 0.65D);

                questionBlock.playSounds(world, pos, storedItem);
                questionBlockEntity.splitTheItem(1);
                questionBlockEntity.setChanged();
            }

            if (storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                BlockState currentState = world.getBlockState(pos);
                if (currentState.getBlock() instanceof QuestionBlock)
                    world.setBlock(pos, currentState.setValue(QuestionBlock.EMPTY, Boolean.TRUE), 3);
                world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }

            if (world.getBlockState(pos).getBlock() instanceof InvisibleQuestionBlock && world.getBlockState(pos).getValue(InvisibleQuestionBlock.INVISIBLE)) {
                BlockState currentState = world.getBlockState(pos);
                world.setBlock(pos, currentState.setValue(InvisibleQuestionBlock.INVISIBLE, Boolean.FALSE), 3);
                world.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
            }
        }
    }

    @Unique
    private void mv$smashBlock(Level world, BlockPos pos, BlockState state, LivingEntity entity) {

        mv$hitEntityAbove(pos, world, entity);

        if (entity instanceof PowerUpHandler handler && handler.mv$hasMushroom()) {
            if (state.getBlock() instanceof SlabBlock) {
                if (state.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                    world.setBlock(pos, state.setValue(SlabBlock.TYPE, SlabType.TOP), 3);
                    entity.getPersistentData().putBoolean("marioverse:has_smashed_block", true);
                } else world.destroyBlock(pos, false);
                world.levelEvent(2001, pos, Block.getId(state));
            } else if (state.getBlock() instanceof DecoratedPotBlock) {
                world.setBlock(pos, state.setValue(DecoratedPotBlock.CRACKED, true), 4);
                world.destroyBlock(pos, true, entity);
            } else world.destroyBlock(pos, false);

            entity.getPersistentData().putBoolean("marioverse:has_smashed_block", true);
            world.gameEvent(this, GameEvent.BLOCK_CHANGE, pos);

            if (state.is(BlockTags.CRYSTAL_SOUND_BLOCKS))
                world.playSound(null, pos, SoundType.AMETHYST.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else if (state.getBlock() instanceof DecoratedPotBlock)
                world.playSound(null, pos, SoundType.DECORATED_POT.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, pos, SoundRegistry.BLOCK_SMASH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            this.mv$dropCoin(world, pos, this);
        } else {
            world.playSound(null, pos, SoundRegistry.BLOCK_SMASH_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            this.mv$dropCoin(world, pos, this);
        }
    }

    @Unique
    private static void mv$hitEntityAbove(BlockPos pos, Level world, LivingEntity attackingEntity) {
        AABB boundingBox = new AABB(pos.above()).inflate(0.01);
        List<Entity> entitiesAbove = world.getEntities(null, boundingBox);

        if (!entitiesAbove.isEmpty()) {
            for (Entity entityAbove : entitiesAbove) {
                if (entityAbove instanceof LivingEntity livingEntity && livingEntity.onGround()) {
                    entityAbove.setDeltaMovement(entityAbove.getDeltaMovement().add(0, 0.5, 0));
                    if (world.getBlockState(pos).getBlock() instanceof QuestionBlock) {
                        if (livingEntity instanceof KoopaShellEntity)
                            livingEntity.hurt(DamageTypeRegistry.bonked(livingEntity, attackingEntity), 0.0F);
                        else livingEntity.hurt(DamageTypeRegistry.bonked(livingEntity, attackingEntity), 4.0F);
                    } else {
                        if (livingEntity instanceof KoopaShellEntity)
                            livingEntity.hurt(DamageTypeRegistry.shrapnel(livingEntity, attackingEntity), 0.0F);
                        else livingEntity.hurt(DamageTypeRegistry.shrapnel(livingEntity, attackingEntity), 4.0F);
                    }
                }
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
                        if (collidedEntity instanceof PowerUpHandler handler && handler.mv$hasSuperStar())
                            return;

                        Vec3 knockbackDirection = entity.position().subtract(attackingEntity.position()).normalize();
                        double knockbackStrength = 5.0;
                        Vec3 knockbackVelocity = knockbackDirection.scale(knockbackStrength).add(0, 1.0, 0);

                        if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get() && entity.isAlive() && !entity.isInvulnerable())
                            this.mv$consecutiveReward(attackingEntity, entity);
                        entity.setDeltaMovement(knockbackVelocity);
                        entity.hurt(DamageTypeRegistry.superStar(collidedEntity, attackingEntity), ConfigRegistry.SUPER_STAR_DAMAGE.get().floatValue());
                    }
                }
            }
        }
    }

    @Unique
    private boolean mv$playedStarTheme = false;

    @Unique
    private void mv$playSuperStarTheme() {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();

        if ((world.getGameTime() % 262L == 0L) || !mv$playedStarTheme) {
            world.playSound(null, entity.blockPosition(), SoundRegistry.SUPER_STAR_THEME.get(), SoundSource.PLAYERS, 1.0F, 1.0f);
            mv$playedStarTheme = true;
        }
    }

    @Unique
    public void mv$squashEntity(LivingEntity stompingEntity) {
        List<Entity> nearbyEntities = stompingEntity.level().getEntities(stompingEntity, stompingEntity.getBoundingBox().inflate(0, 0.5, 0));

        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity damagedEntity && !damagedEntity.isVehicle()
                        && (stompingEntity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                        && !damagedEntity.getType().is(TagRegistry.POWER_UP_ENTITIES)
                        && (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED)
                            || damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                            || ConfigRegistry.STOMP_ALL_MOBS.get())) {

                    if (stompingEntity instanceof Player player && player.getAbilities().flying)
                        return;

                    if (stompingEntity instanceof PowerUpHandler handler && handler.mv$hasSuperStar())
                        return;

                    if (damagedEntity instanceof PowerUpHandler handler && handler.mv$hasSuperStar())
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
                            if (damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED) && hasNoArmor)
                                damagedEntity.hurt(DamageTypeRegistry.stomp(damagedEntity, stompingEntity), damagedEntity.getHealth());
                            else if (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get()) {
                                if (damagedEntity instanceof KoopaTroopaEntity
                                        || damagedEntity instanceof KoopaShellEntity)
                                    damagedEntity.hurt(DamageTypeRegistry.stomp(damagedEntity, stompingEntity), 0);
                                else damagedEntity.hurt(DamageTypeRegistry.stomp(damagedEntity, stompingEntity), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                            }
                            if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get())
                                this.mv$consecutiveReward(stompingEntity, damagedEntity);
                        }
                    }
                }
            }
        }
    }

    @Unique
    public void mv$consecutiveReward(LivingEntity attackingEntity, LivingEntity damagedEntity) {
        int consecutiveBounces = attackingEntity.getPersistentData().getInt("marioverse:consecutive_bounces");
        int oneUpsRewarded = this.mv$getOneUpsRewarded();
        attackingEntity.getPersistentData().putInt("marioverse:consecutive_bounces", consecutiveBounces + 1);

        if (consecutiveBounces == 0) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GOOD.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.good"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 1) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.GREAT.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.great"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 2) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.SUPER.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.super"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 3) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.FANTASTIC.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.fantastic"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 4) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.EXCELLENT.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.excellent"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 5) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.INCREDIBLE.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.incredible"), Boolean.TRUE);
        }
        else if (consecutiveBounces == 6) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.WONDERFUL.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.wonderful"), Boolean.TRUE);
        }
        else if (consecutiveBounces >= 7 && ConfigRegistry.MAX_ONE_UP_BOUNCE_REWARD.get() > oneUpsRewarded) {
            this.mv$setOneUpsRewarded(oneUpsRewarded + 1);
            this.mv$bounceReward(attackingEntity);
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get()) {
                if (damagedEntity.level() instanceof ServerLevel serverWorld)
                    ServerParticleUtils.spawnRewardParticle(ParticleRegistry.ONE_UP.get(), serverWorld, damagedEntity);
            } else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.one_up"), Boolean.TRUE);
        }
    }

    @Unique
    public void mv$bounceReward(LivingEntity entity) {
        ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;
        if (entity instanceof LivingEntity livingEntity && ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get()) {
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
