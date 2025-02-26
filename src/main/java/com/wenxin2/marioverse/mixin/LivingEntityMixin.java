package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.CoinBlock;
import com.wenxin2.marioverse.blocks.InvisibleQuestionBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.init.AttributesRegistry;
import com.wenxin2.marioverse.init.ConfigRegistry;
import com.wenxin2.marioverse.init.DamageTypeRegistry;
import com.wenxin2.marioverse.init.ItemRegistry;
import com.wenxin2.marioverse.init.ParticleRegistry;
import com.wenxin2.marioverse.init.SoundRegistry;
import com.wenxin2.marioverse.init.TagRegistry;
import com.wenxin2.marioverse.items.OneUpMushroomItem;
import io.wispforest.accessories.api.AccessoriesCapability;
import io.wispforest.accessories.api.AccessoriesContainer;
import io.wispforest.accessories.data.SlotTypeLoader;
import java.util.List;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique private static final int MAX_PARTICLE_AMOUNT = 100;
    @Unique private int marioverse$warpCooldown;
    @Unique private int marioverse$consecutiveBounces;
    @Unique private int marioverse$oneUpsRewarded;
    @Unique private boolean marioverse$playedDamagedSound;

    public LivingEntityMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "baseTick")
    public void baseTick(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockPos posAboveEntity = pos.above(Math.round(entity.getBbHeight()));
        BlockPos posInBlock = pos.above(Math.round(entity.getBbHeight()) - 1);
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(posAboveEntity);
        BlockState stateInBlock = world.getBlockState(posInBlock);

        int fireballCooldown = this.getPersistentData().getInt("marioverse:fireball_cooldown");
        int superStarCooldown = this.getPersistentData().getInt("marioverse:super_star_cooldown");
        boolean hasSuperStar = this.getPersistentData().getBoolean("marioverse:has_super_star");

        for (Direction facing : Direction.values()) {
            BlockPos offsetPos = pos.relative(facing);
            BlockState offsetState = world.getBlockState(offsetPos);

            if (!entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                if (offsetState.getBlock() instanceof WarpPipeBlock && !offsetState.getValue(WarpPipeBlock.CLOSED))
                    this.marioverse$enterWarp(offsetPos);
                if (state.getBlock() instanceof WarpPipeBlock && !state.getValue(WarpPipeBlock.CLOSED))
                    this.marioverse$enterWarp(pos);
            }
        }

        if (stateAboveEntity.getBlock() instanceof WarpPipeBlock && !stateAboveEntity.getValue(WarpPipeBlock.CLOSED)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_DOORS.get()
                && world.getBlockEntity(pos) instanceof WarpDoorBlockEntity
                && state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)
                && state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && world.getBlockEntity(pos) instanceof WarpTrapDoorBlockEntity
                && state.getBlock() instanceof TrapDoorBlock && state.getValue(TrapDoorBlock.OPEN)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(pos);

        if (!ConfigRegistry.DISABLE_WARP_TRAPDOORS.get()
                && world.getBlockEntity(posInBlock) instanceof WarpTrapDoorBlockEntity
                && stateInBlock.getBlock() instanceof TrapDoorBlock && stateInBlock.getValue(TrapDoorBlock.OPEN)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp"))
            this.marioverse$enterWarp(posInBlock);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                && (entity.fallDistance > 0 || entity.isInWaterOrBubble()))
            this.marioverse$squashEntity(entity);

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                && (entity.onGround() || entity.isInWaterOrBubble())
                && (marioverse$consecutiveBounces > 0 || marioverse$oneUpsRewarded > 0)
                && !hasSuperStar) {
            marioverse$consecutiveBounces = 0;
            marioverse$oneUpsRewarded = 0;
        }

        if (this.marioverse$warpCooldown > 0)
            --this.marioverse$warpCooldown;

        if (entity.onGround() && entity.getDeltaMovement().y <= 0
                && entity.getPersistentData().getBoolean("marioverse:has_smashed_block"))
            entity.getPersistentData().putBoolean("marioverse:has_smashed_block", false);

        if (stateAboveEntity.is(TagRegistry.SMASHABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS) && entity.getDeltaMovement().y > 0
                && !entity.getPersistentData().getBoolean("marioverse:has_smashed_block")) {
            if (entity.getPersistentData().getBoolean("marioverse:has_mushroom")) {
                if (stateAboveEntity.getBlock() instanceof SlabBlock) {
                    if (stateAboveEntity.getValue(SlabBlock.TYPE) == SlabType.DOUBLE) {
                        world.setBlock(posAboveEntity, stateAboveEntity.setValue(SlabBlock.TYPE, SlabType.TOP), 3);
                        entity.getPersistentData().putBoolean("marioverse:has_smashed_block", true);
                    } else world.destroyBlock(posAboveEntity, false);
                    world.levelEvent(2001, posAboveEntity, Block.getId(stateAboveEntity));
                } else world.destroyBlock(posAboveEntity, false);
                entity.getPersistentData().putBoolean("marioverse:has_smashed_block", true);
                world.gameEvent(this, GameEvent.BLOCK_CHANGE, posAboveEntity);
                if (stateAboveEntity.is(BlockTags.CRYSTAL_SOUND_BLOCKS))
                    world.playSound(null, posAboveEntity, SoundType.AMETHYST.getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                else world.playSound(null, posAboveEntity, SoundRegistry.BLOCK_SMASH.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                this.marioverse$dropCoin(world, posAboveEntity, this);
            } else {
                world.playSound(null, posAboveEntity, SoundRegistry.BLOCK_SMASH_FAIL.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                this.marioverse$dropCoin(world, posAboveEntity, this);
            }
        }

        if (stateAboveEntity.is(TagRegistry.BONKABLE_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS) && this.getDeltaMovement().y > 0)
            if (stateAboveEntity.hasProperty(QuestionBlock.EMPTY) && stateAboveEntity.getValue(QuestionBlock.EMPTY))
                world.playSound(null, pos, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            else world.playSound(null, pos, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);

        if (world.getBlockEntity(posAboveEntity) instanceof QuestionBlockEntity questionBlockEntity
                && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS) && this.getDeltaMovement().y > 0)
            this.marioverse$hitQuestionBlock(world, posAboveEntity, questionBlockEntity);

        if (fireballCooldown > 0)
            entity.getPersistentData().putInt("marioverse:fireball_cooldown", fireballCooldown - 1);

        if (superStarCooldown > 0)
            entity.getPersistentData().putInt("marioverse:super_star_cooldown", superStarCooldown - 1);

        if (superStarCooldown == 0 && hasSuperStar)
            entity.getPersistentData().putBoolean("marioverse:has_super_star", Boolean.FALSE);

        if (hasSuperStar) {
            this.marioverse$superStarKillEntity(entity);
            this.level().broadcastEntityEvent(entity, (byte) 114);
            this.marioverse$playSuperStarTheme();
        } else if (!hasSuperStar && this.marioverse$playedStarTheme)
            this.marioverse$playedStarTheme = false;

        this.marioverse$entityScale(entity);

//        if (this.getPersistentData().contains("marioverse:has_mega_mushroom") && this.getPersistentData().getBoolean("marioverse:has_mega_mushroom")) {
//            ScaleTypes.WIDTH.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.HEIGHT.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.JUMP_HEIGHT.getScaleData(this).setTargetScale(20.0F);
//            ScaleTypes.STEP_HEIGHT.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.REACH.getScaleData(this).setTargetScale(5.0F);
//            ScaleTypes.ATTACK.getScaleData(this).setTargetScale(5.0F);
//        }
    }

    @Inject(method = "checkTotemDeathProtection", at = @At("RETURN"), cancellable = true)
    private void checkTotemDeathProtection(DamageSource source, CallbackInfoReturnable<Boolean> info) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

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
                        this.level().broadcastEntityEvent(this, (byte) 124); // Mushroom Transform particle
                        this.level().broadcastEntityEvent(this, (byte) 115); // 1-Up Pop Up
                        this.level().broadcastEntityEvent(this, (byte) 126); // 1-Up Particle
                        float scaleFactor = livingEntity.getBbHeight() * livingEntity.getBbWidth();
                        int numParticles = (int) (scaleFactor * 20);
                        double radius = livingEntity.getBbWidth() / 2;

                        for (int i = 0; i < numParticles; i++) {
                            // Calculate angle for each particle
                            double angle = 2 * Math.PI * i / numParticles;
                            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
                            double offsetX = Math.cos(angle) * radius;
                            double offsetY = livingEntity.getBbHeight() / 2;
                            double offsetZ = Math.sin(angle) * radius;

                            double x = livingEntity.getX() + offsetX;
                            double y = livingEntity.getY() + offsetY;
                            double z = livingEntity.getZ() + offsetZ;

                            this.level().addParticle(ParticleRegistry.POWERED_UP.get(), x, y, z, 0, 1.0, 0);
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
                this.level().broadcastEntityEvent(livingEntity, (byte) 124); // Mushroom Transform particle
                this.level().broadcastEntityEvent(this, (byte) 115); // 1-Up Pop Up
                this.level().broadcastEntityEvent(this, (byte) 126); // 1-Up Particle

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
        LivingEntity entity = (LivingEntity) (Object) this;

        if (pose != Pose.SLEEPING) {
            float eyeHeightScale = (float) entity.getAttributeValue(AttributesRegistry.EYE_HEIGHT_SCALE);
            float heightScale = (float) entity.getAttributeValue(AttributesRegistry.HEIGHT_SCALE);

            float scaledHeight;
            if (heightScale <= 1)
                scaledHeight = cir.getReturnValue().height();
            else scaledHeight = cir.getReturnValue().height() * heightScale;

            float adjustedEyeHeight = cir.getReturnValue().eyeHeight() * eyeHeightScale * (scaledHeight / cir.getReturnValue().height());

            EntityDimensions customDimensions = EntityDimensions.scalable(
                    cir.getReturnValue().width(),
                    cir.getReturnValue().height()
            ).withEyeHeight(adjustedEyeHeight);

            cir.setReturnValue(customDimensions);
        }
    }

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

    @Inject(method = "handleEntityEvent", at = @At("HEAD"))
    private void handleEntityEvent(byte id, CallbackInfo info) {
        LivingEntity entity = (LivingEntity) (Object) this;
        RandomSource random = entity.getRandom();

        if (id == 113) {
            ParticleUtils.spawnParticlesOnBlockFaces(entity.level(), this.blockPosition(), ParticleRegistry.COIN_GLINT.get(), UniformInt.of(1, 1));
        } else if (id == 114) {
            this.marioverse$starParticles(entity, ParticleRegistry.COIN_GLINT.get());
        } else if (id == 115) {
            if (this.level().isClientSide) {
                if (entity == Minecraft.getInstance().player)
                    Minecraft.getInstance().gameRenderer.displayItemActivation(ItemRegistry.ONE_UP_MUSHROOM.get().getDefaultInstance());
            }
        } else if (id == 119) {
            this.marioverse$spawnPowerUpParticles(entity, ParticleRegistry.COIN_GLINT.get(), 15);
        } else if (id == 120) {
            for(int i = 0; i < MAX_PARTICLE_AMOUNT; ++i) {
                this.level().addParticle(ParticleTypes.ENCHANT,
                        entity.getRandomX(0.5D), entity.getRandomY(), entity.getRandomZ(0.5D),
                        (random.nextDouble() - 0.5D) * 2.0D, -random.nextDouble(),
                        (random.nextDouble() - 0.5D) * 2.0D);
            }
        } else if (id == 123) {
            this.marioverse$spawnPowerUpParticles(entity, ParticleRegistry.FIRE_POWERED_UP.get(), 15);
        } else if (id == 124) {
            this.marioverse$spawnPowerUpParticles(entity, ParticleRegistry.POWERED_UP.get(), 25);
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
    public void marioverse$rewardParticles(LivingEntity entity, ParticleOptions particleType) {
        if (entity.level() instanceof ServerLevel serverWorld)
            serverWorld.sendParticles(particleType, entity.getX(),
                    entity.getY() + entity.getBbHeight() + 1.0,
                    entity.getZ(), 1, 0, 1.0, 0, 0.5);
    }

    @Unique
    public void marioverse$starParticles(LivingEntity entity, ParticleOptions particleType) {
        RandomSource rand = RandomSource.create();
        double offsetX = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);
        double offsetY = rand.nextDouble() * entity.getBbHeight();
        double offsetZ = rand.nextDouble() * entity.getBbWidth() - (entity.getBbWidth() / 2.0);

        this.level().addParticle(particleType,
                entity.getX() + offsetX, entity.getY() + offsetY, entity.getZ() + offsetZ,
                0, 0, 0);
    }

    @Unique
    public void marioverse$spawnPowerUpParticles(Entity entity, ParticleOptions particleType, int avgAmount) {
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
    public void marioverse$entityScale(LivingEntity entity) {
        Level world = entity.level();
        CompoundTag tag = entity.getPersistentData();
        boolean hasMushroom = tag.getBoolean("marioverse:has_mushroom");
        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        float health = entity.getHealth();
        float scalingSpeed = 0.05F;

        double targetEyeHeightScale = hasMushroom ? 1.0D : 0.5D;
        double targetHeightScale = hasMushroom ? 1.0D : 0.5D;
        double targetWidthScale = hasMushroom ? 1.0D : 0.75D;

        boolean isPlayer = entity instanceof Player;
        boolean shouldShrink = entity.getLastDamageSource() != null
                && !entity.isDamageSourceBlocked(entity.getLastDamageSource())
                && !hasMushroom
                && !entity.getType().is(TagRegistry.CANNOT_LOSE_POWER_UP)
                && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                && ((isPlayer && health <= ConfigRegistry.HEALTH_SHRINK_PLAYERS.get() && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get())
                || (!isPlayer && health <= entity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get() && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get()));

        boolean shouldReset = (isPlayer && health > ConfigRegistry.HEALTH_SHRINK_PLAYERS.get() && ConfigRegistry.DAMAGE_SHRINKS_PLAYERS.get())
                || (!isPlayer && health > entity.getMaxHealth() * ConfigRegistry.HEALTH_SHRINK_MOBS.get() && ConfigRegistry.DAMAGE_SHRINKS_ALL_MOBS.get());

        if (shouldShrink) {
            marioverse$updateScale(eyeHeightScale, targetEyeHeightScale, scalingSpeed);
            marioverse$updateScale(heightScale, targetHeightScale, scalingSpeed);
            marioverse$updateScale(widthScale, targetWidthScale, scalingSpeed);

            if (!marioverse$playedDamagedSound) {
                marioverse$playedDamagedSound = true;
                SoundSource soundSource = isPlayer ? SoundSource.PLAYERS : SoundSource.NEUTRAL;
                world.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(), soundSource, 1.0F, 1.0F);
            }
        }

        if (shouldReset) {
            marioverse$playedDamagedSound = false;
            marioverse$resetScale(eyeHeightScale, scalingSpeed);
            marioverse$resetScale(heightScale, scalingSpeed);
            marioverse$resetScale(widthScale, scalingSpeed);
        }
    }

    @Unique
    private void marioverse$resetScale(AttributeInstance scaleAttribute, float scalingSpeed) {
        marioverse$updateScale(scaleAttribute, 1.0D, scalingSpeed);
    }

    @Unique
    private void marioverse$updateScale(AttributeInstance scaleAttribute, double targetScale, float scalingSpeed) {
        if (scaleAttribute != null) {
            double currentScale = Mth.lerp(scalingSpeed, scaleAttribute.getBaseValue(), targetScale);
            if (scaleAttribute.getBaseValue() != currentScale) {
                scaleAttribute.setBaseValue(currentScale);
            }
        }
    }

    @Unique
    public void marioverse$dropCoin(Level world, BlockPos pos, Entity entity) {
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
    public void marioverse$hitQuestionBlock(Level world, BlockPos pos, QuestionBlockEntity questionBlockEntity) {
        LivingEntity entity = (LivingEntity) (Object) this;

        if (world.getBlockState(pos).getBlock() instanceof QuestionBlock questionBlock) {
            ItemStack storedItem = questionBlockEntity.getTheItem();

//            if (questionBlockEntity.getLootTable() != null)
//                questionBlock.unpackLootTable(this, questionBlockEntity);

            if (!storedItem.isEmpty() && !world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                this.marioverse$dropCoin(world, pos, this);

                if (!world.isClientSide)
                    questionBlock.spawnFromQuestionBlock(world, pos, storedItem, entity, Boolean.FALSE, Boolean.TRUE);


                if (world.getBlockState(pos).is(BlockTags.GUARDED_BY_PIGLINS) && entity instanceof Player player)
                    PiglinAi.angerNearbyPiglins(player, false);

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

            if (!world.getBlockState(pos).getValue(QuestionBlock.EMPTY)) {
                AABB boundingBox = new AABB(pos.above()).inflate(0.5);
                List<Entity> entitiesAbove = world.getEntities(null, boundingBox);

                if (!entitiesAbove.isEmpty()) {
                    for (Entity entityAbove : entitiesAbove) {
                        if (entityAbove instanceof LivingEntity livingEntity) {
                            // TODO: Add custom damage source
                            livingEntity.hurt(world.damageSources().generic(), 4.0F);
                        }
                    }
                }
            }
        }
    }

    @Unique
    public void marioverse$superStarKillEntity(LivingEntity attackingEntity) {
        List<Entity> nearbyEntities = attackingEntity.level().getEntities(attackingEntity, attackingEntity.getBoundingBox());

        if (!nearbyEntities.isEmpty()) {
            for (Entity collidedEntity : nearbyEntities) {
                if (collidedEntity instanceof LivingEntity entity) {
                    if (!entity.getType().is(TagRegistry.SUPER_STAR_IMMUNE)
                            && !collidedEntity.getPersistentData().getBoolean("marioverse:has_super_star")) {

                        if (entity instanceof Player player && (player.isCreative() || player.isSpectator()))
                            return;

                        Vec3 knockbackDirection = entity.position().subtract(attackingEntity.position()).normalize();
                        double knockbackStrength = 5.0;
                        Vec3 knockbackVelocity = knockbackDirection.scale(knockbackStrength).add(0, 1.0, 0);

                        if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get() && entity.isAlive() && !entity.isInvulnerable())
                            this.marioverse$consecutiveReward(attackingEntity, entity);
                        entity.setDeltaMovement(knockbackVelocity);
                        entity.hurt(DamageTypeRegistry.superStar(collidedEntity, attackingEntity), ConfigRegistry.SUPER_STAR_DAMAGE.get().floatValue());
                    }
                }
            }
        }
    }

    @Unique
    private boolean marioverse$playedStarTheme = false;

    @Unique
    private void marioverse$playSuperStarTheme() {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();

        if ((world.getGameTime() % 262L == 0L) || !marioverse$playedStarTheme) {
            world.playSound(null, entity.blockPosition(), SoundRegistry.SUPER_STAR_THEME.get(), SoundSource.PLAYERS, 1.0F, 1.0f);
            marioverse$playedStarTheme = true;
        }
    }

    @Unique
    public void marioverse$squashEntity(LivingEntity stompingEntity) {
        List<Entity> nearbyEntities = stompingEntity.level().getEntities(stompingEntity, stompingEntity.getBoundingBox().inflate(0, 0.5, 0));

        if (!nearbyEntities.isEmpty()) {
            for (Entity entity : nearbyEntities) {
                if (entity instanceof LivingEntity damagedEntity && !damagedEntity.isVehicle()
                        && (stompingEntity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get())
                        && !stompingEntity.getPersistentData().getBoolean("marioverse:has_super_star")
                        && !damagedEntity.getType().is(TagRegistry.POWER_UP_ENTITIES)
                        && (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED)
                        || damagedEntity.getType().is(TagRegistry.CAN_BE_INSTAKILL_STOMPED)
                        || ConfigRegistry.STOMP_ALL_MOBS.get())
                        && !damagedEntity.getPersistentData().getBoolean("marioverse:has_super_star")) {

                    if (stompingEntity instanceof Player player && player.getAbilities().flying)
                        return;

                    // Check if the colliding entity is above the current entity and falling
                    if (stompingEntity.getY() >= damagedEntity.getY() + damagedEntity.getEyeHeight()
                            && (stompingEntity.fallDistance > 0 || stompingEntity.isInWaterOrBubble())) {
                        double bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT.getAsDouble();
                        if (stompingEntity instanceof Player)
                            if (Minecraft.getInstance().options.keyJump.isDown())
                                bounceBlockHeight = ConfigRegistry.STOMP_BOUNCE_HEIGHT_JUMP.getAsDouble();
                        double gravity = 0.08; // Approximate Minecraft gravity value
                        double bounceVelocity = Math.sqrt(2 * gravity * bounceBlockHeight);

                        if (damagedEntity.isAlive()) {
                            stompingEntity.setDeltaMovement(stompingEntity.getDeltaMovement().x, bounceVelocity, stompingEntity.getDeltaMovement().z);
                            stompingEntity.fallDistance = 0; // Reset fall damage
                        }

                        float scaleFactor = damagedEntity.getBbHeight() * damagedEntity.getBbWidth();
                        int numParticles = (int) (scaleFactor * 20);
                        double radius = damagedEntity.getBbWidth() / 2;

                        for (int i = 0; i < numParticles; i++) {
                            // Calculate angle for each particle
                            double angle = 2 * Math.PI * i / numParticles;
                            // Calculate the X and Z offset using sine and cosine to spread in an ellipse
                            double offsetX = Math.cos(angle) * radius;
                            double offsetY = damagedEntity.getBbHeight();
                            double offsetZ = Math.sin(angle) * radius;

                            double x = damagedEntity.getX() + offsetX;
                            double y = damagedEntity.getY() + offsetY;
                            double z = damagedEntity.getZ() + offsetZ;

                            this.level().addParticle(ParticleTypes.CRIT, x, y, z, 0, 1.0, 0);
                        }

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
                            else if (damagedEntity.getType().is(TagRegistry.CAN_BE_STOMPED) || ConfigRegistry.STOMP_ALL_MOBS.get())
                                damagedEntity.hurt(DamageTypeRegistry.stomp(damagedEntity, stompingEntity), ConfigRegistry.STOMP_DAMAGE.get().floatValue());
                            if (!ConfigRegistry.DISABLE_CONSECUTIVE_BOUNCING.get())
                                this.marioverse$consecutiveReward(stompingEntity, damagedEntity);
                        }
                    }
                }
            }
        }
    }

    @Unique
    public void marioverse$consecutiveReward(LivingEntity attackingEntity, LivingEntity damagedEntity) {
        marioverse$consecutiveBounces++;

        if (marioverse$consecutiveBounces == 1) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.GOOD.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.good"), Boolean.TRUE);
        }
        else if (marioverse$consecutiveBounces == 2) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.GREAT.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.great"), Boolean.TRUE);
        }
        else if (marioverse$consecutiveBounces == 3) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.SUPER.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.super"), Boolean.TRUE);
        }
        else if (marioverse$consecutiveBounces == 4) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.FANTASTIC.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.fantastic"), Boolean.TRUE);
        }
        else if (marioverse$consecutiveBounces == 5) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.EXCELLENT.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.excellent"), Boolean.TRUE);
        }
        else if (marioverse$consecutiveBounces == 6) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.INCREDIBLE.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.incredible"), Boolean.TRUE);
        }
        else if (marioverse$consecutiveBounces == 7) {
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.WONDERFUL.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.wonderful"), Boolean.TRUE);
        }
        else if (marioverse$consecutiveBounces >= 8 && ConfigRegistry.MAX_ONE_UP_BOUNCE_REWARD.get() > marioverse$oneUpsRewarded) {
            marioverse$oneUpsRewarded++;
            this.marioverse$bounceReward(attackingEntity);
            if (!ConfigRegistry.DISABLE_REWARD_PARTICLES.get())
                this.marioverse$rewardParticles(damagedEntity, ParticleRegistry.ONE_UP.get());
            else if (attackingEntity instanceof Player player)
                player.displayClientMessage(Component.translatable("display.marioverse.consecutive_bounce.one_up"), Boolean.TRUE);
        }
    }

    @Unique
    public void marioverse$bounceReward(LivingEntity entity) {
        ItemLike item = ItemRegistry.ONE_UP_MUSHROOM;
        if (entity instanceof Player player && !player.isSpectator()) {
            AccessoriesCapability capability = AccessoriesCapability.get(player);
            ItemStack offhandStack = player.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get()))
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            else if (offhandStack.isEmpty())
                player.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
            else if (offhandStack.getCount() >= 1)
                player.addItem(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);

        } else if (entity instanceof LivingEntity livingEntity && ConfigRegistry.ONE_UP_HEALS_ALL_MOBS.get()) {
            AccessoriesCapability capability = AccessoriesCapability.get(livingEntity);
            ItemStack offhandStack = livingEntity.getOffhandItem();

            if (capability != null && !capability.isEquipped(ItemRegistry.ONE_UP_MUSHROOM.get()))
                capability.attemptToEquipAccessory(new ItemStack(ItemRegistry.ONE_UP_MUSHROOM.get()));
            else if (offhandStack.isEmpty())
                livingEntity.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(item));
            else if (offhandStack.getItem() instanceof OneUpMushroomItem)
                offhandStack.grow(1);
            this.level().playSound(null, this.blockPosition(), SoundRegistry.ONE_UP_COLLECTED.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Unique
    public int marioverse$getWarpCooldown() {
        return marioverse$warpCooldown;
    }

    @Unique
    public void marioverse$setWarpCooldown(int cooldown) {
        this.marioverse$warpCooldown = cooldown;
    }

    @Unique
    public void marioverse$enterWarp(BlockPos pos) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();
        BlockState state = world.getBlockState(pos);
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(this.getBbHeight())));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos.above(Math.round(this.getBbHeight())));
        BlockPos warpPos;

        if (blockEntity instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null
                && !warpBE.preventWarp) {
            warpPos = warpBE.destinationPos;
            int entityId = this.getId();

            if (BaseWarpBlockEntity.teleportedEntities.getOrDefault(entityId, false))
                // Reset the teleport status for the entity
                BaseWarpBlockEntity.teleportedEntities.put(entityId, false);

            if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock)
                this.marioverse$enterWarpDoor(pos, warpPos, warpBE);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipe(pos, warpPos, warpBE);
        }

        if (blockEntityAbove instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null
                && !warpBE.preventWarp) {
            warpPos = warpBE.destinationPos;
            int entityId = this.getId();

            if (BaseWarpBlockEntity.teleportedEntities.getOrDefault(entityId, true))
                BaseWarpBlockEntity.teleportedEntities.put(entityId, false);

            if (stateAboveEntity.getBlock() instanceof WarpPipeBlock)
                this.marioverse$enterWarpPipeAbove(pos, warpPos, warpBE);
        }
    }

    @Unique
    public void marioverse$warp(BlockPos pos, BlockState state, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();

        if (warpPos != null && world.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity) {
            BlockState warpState = world.getBlockState(warpPos);

            if (warpState.getBlock() instanceof DoorBlock doorblock)
                WarpDoorBlockEntity.warp(entity, warpPos, world, warpState, doorblock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(entity, warpPos, world, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, world, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
            this.marioverse$updateDoor(pos, state, warpPos, warpState);
        } else if (warpBE.getUuid() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUuid(), world, pos) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUuid(), world, pos);
            BlockState warpState = world.getBlockState(warpPos);

            if (warpState.getBlock() instanceof DoorBlock doorblock)
                WarpDoorBlockEntity.warp(entity, warpPos, world, warpState, doorblock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(entity, warpPos, world, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, world, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                world.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
            this.marioverse$updateDoor(pos, state, warpPos, warpState);
        }
    }

    @Unique
    public void marioverse$enterWarpDoor(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();
        BlockState state = world.getBlockState(pos);

        if (ConfigRegistry.TELEPORT_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (this.marioverse$getWarpCooldown() == 0 && !entity.isShiftKeyDown()) {
                this.marioverse$warp(pos, state, warpPos, warpBE);
                if (state.getBlock() instanceof DoorBlock)
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_DOOR_COOLDOWN.get());
                else this.marioverse$setWarpCooldown(ConfigRegistry.WARP_TRAPDOOR_COOLDOWN.get());
            }
        }
    }

    @Unique
    public void marioverse$enterWarpPipe(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();
        BlockState state = world.getBlockState(pos);

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (ConfigRegistry.TELEPORT_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && !entity.isShiftKeyDown() && (entityY + entity.getBbHeight() >= blockY - 1)
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !entity.isShiftKeyDown()
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !entity.isShiftKeyDown()
                    && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !entity.isShiftKeyDown()
                    && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
            if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !entity.isShiftKeyDown()
                    && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, state, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
        }
    }

    @Unique
    public void marioverse$enterWarpPipeAbove(BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();
        BlockState stateAboveEntity = world.getBlockState(pos.above(Math.round(entity.getBbHeight())));

        double entityX = this.getX();
        double entityZ = this.getZ();
        int blockX = pos.getX();
        int blockZ = pos.getZ();

        if (ConfigRegistry.TELEPORT_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (stateAboveEntity.getValue(WarpPipeBlock.FACING) == Direction.DOWN
                    && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                if (this.marioverse$getWarpCooldown() == 0) {
                    this.marioverse$warp(pos, stateAboveEntity, warpPos, warpBE);
                    this.marioverse$setWarpCooldown(ConfigRegistry.WARP_PIPE_COOLDOWN.get());
                }
            }
        }
    }

    @Unique
    public void marioverse$updateDoor(BlockPos pos, BlockState state, BlockPos warpPos, BlockState warpState) {
        LivingEntity entity = (LivingEntity) (Object) this;
        Level world = entity.level();
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity warpBE = world.getBlockEntity(warpPos);

        if (!world.isClientSide) {
            if (warpBE instanceof WarpDoorBlockEntity warpDoorBE && warpDoorBE.breakDoor)
                WarpDoorBlockEntity.breakDoor(warpPos, world);
            if (warpBE instanceof WarpTrapDoorBlockEntity warpTrapdoorBE && warpTrapdoorBE.breakTrapdoor)
                WarpTrapDoorBlockEntity.breakTrapdoor(warpPos, world);

            if (state.getBlock() instanceof DoorBlock)
                world.setBlock(pos, state.setValue(DoorBlock.OPEN, Boolean.FALSE)
                        .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING)), 10);
            if (state.getBlock() instanceof TrapDoorBlock)
                world.setBlock(pos, state.setValue(TrapDoorBlock.OPEN, Boolean.FALSE)
                        .setValue(TrapDoorBlock.FACING, state.getValue(TrapDoorBlock.FACING)), 10);

            if (warpBE instanceof WarpDoorBlockEntity warpDoorBE && !warpDoorBE.breakDoor)
                world.setBlock(warpPos, warpState.setValue(DoorBlock.OPEN, Boolean.TRUE)
                        .setValue(DoorBlock.FACING, warpState.getValue(DoorBlock.FACING)), 10);
            if (warpBE instanceof WarpTrapDoorBlockEntity warpDoorBE && !warpDoorBE.breakTrapdoor)
                world.setBlock(warpPos, warpState.setValue(TrapDoorBlock.OPEN, Boolean.TRUE)
                        .setValue(TrapDoorBlock.FACING, warpState.getValue(TrapDoorBlock.FACING)), 10);
        }

        if (blockEntity instanceof BaseWarpBlockEntity warpDoorBE) {
            if (state.getBlock() instanceof DoorBlock doorBlock)
                warpDoorBE.playDoorSounds(null, world, pos, state.getValue(DoorBlock.OPEN), doorBlock.type());
            if (warpState.getBlock() instanceof DoorBlock doorBlock)
                warpDoorBE.playDoorSounds(null, world, warpPos, warpState.getValue(DoorBlock.OPEN), doorBlock.type());

            if (state.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                warpDoorBE.playDoorSounds(null, world, pos, state.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                warpDoorBE.playDoorSounds(null, world, warpPos, warpState.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
        }
    }
}
