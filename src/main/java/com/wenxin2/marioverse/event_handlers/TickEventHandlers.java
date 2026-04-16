package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.SmashableBrickBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.registries.AttributesRegistry;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class TickEventHandlers {
    private static double currentEyeHeightScale = 1.0;
    private static double currentHeightScale = 1.0;
    private static double currentWidthScale = 1.0;

    @SubscribeEvent
    public static void preEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.onGround() || entity.isInWaterOrBubble())
                && entity.getData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES) > 0
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                    || level.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP)))
            entity.setData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES, 0);

        if (!level.isClientSide && !entity.isSpectator() && !entity.isShiftKeyDown()
                && entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)) {
            if (ConfigRegistry.MEGA_MUSHROOM_BREAKS_BLOCKS.get()
                    && (ConfigRegistry.MEGA_MOBS_BREAK_BLOCKS.get() || entity.getType().is(TagRegistry.CAN_BREAK_BLOCKS_AS_MEGA))
                    && (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                        || (entity instanceof Player && entity.getType().is(TagRegistry.CAN_BREAK_BLOCKS_AS_MEGA))))
                TickEventHandlers.breakBlocks(entity);
            TickEventHandlers.collideWithEntity(entity);
        }

        if (entity.hasData(DataAttachmentRegistry.ATTACK_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.ATTACK_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.ATTACK_COOLDOWN, entity.getData(DataAttachmentRegistry.ATTACK_COOLDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN, entity.getData(DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.FIREBALL_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.FIREBALL_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.FIREBALL_COOLDOWN, entity.getData(DataAttachmentRegistry.FIREBALL_COOLDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.FREEZE_IMMUNITY_DURATION) &&
                entity.getData(DataAttachmentRegistry.FREEZE_IMMUNITY_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.FREEZE_IMMUNITY_DURATION, entity.getData(DataAttachmentRegistry.FREEZE_IMMUNITY_DURATION) - 1);

        if (entity.hasData(DataAttachmentRegistry.FROZEN_DURATION) &&
                entity.getData(DataAttachmentRegistry.FROZEN_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.FROZEN_DURATION, entity.getData(DataAttachmentRegistry.FROZEN_DURATION) - 1);

        if (entity.hasData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get())
                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) > 0)
            entity.setData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get(), entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN.get()) - 1);

        if (entity.hasData(DataAttachmentRegistry.HIT_BLOCK_SOUND_COOLDOWN.get())
                && entity.getData(DataAttachmentRegistry.HIT_BLOCK_SOUND_COOLDOWN.get()) > 0)
            entity.setData(DataAttachmentRegistry.HIT_BLOCK_SOUND_COOLDOWN.get(), entity.getData(DataAttachmentRegistry.HIT_BLOCK_SOUND_COOLDOWN.get()) - 1);

        if (entity.hasData(DataAttachmentRegistry.ICE_BALL_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.ICE_BALL_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.ICE_BALL_COOLDOWN, entity.getData(DataAttachmentRegistry.ICE_BALL_COOLDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION) &&
                entity.getData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION, entity.getData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION) - 1);

        if (entity.hasData(DataAttachmentRegistry.ONE_UPS_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.ONE_UPS_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.ONE_UPS_COOLDOWN, entity.getData(DataAttachmentRegistry.ONE_UPS_COOLDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN) &&
                entity.getData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN) > 0)
            entity.setData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN, entity.getData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.SUPER_STAR_DURATION) &&
                entity.getData(DataAttachmentRegistry.SUPER_STAR_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.SUPER_STAR_DURATION, entity.getData(DataAttachmentRegistry.SUPER_STAR_DURATION) - 1);

        if (entity.hasData(DataAttachmentRegistry.WARP_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.WARP_COOLDOWN, entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) - 1);

        if (entity.hasData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN)) {
            int preventWarpCooldown = entity.getData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN);

            if (preventWarpCooldown > 0)
                entity.setData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN, preventWarpCooldown - 1);

            if (preventWarpCooldown == 0
                    && entity.getData(DataAttachmentRegistry.PREVENT_WARP))
                entity.setData(DataAttachmentRegistry.PREVENT_WARP, false);
        }

        if (entity.getData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION) == 0
                && entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)
                && entity instanceof LivingEntity livingEntity) {
            AttributeInstance healthAttribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = livingEntity.getAttribute(Attributes.STEP_HEIGHT);

            entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.PLAYED_MEGA_MUSHROOM_THEME, false);

            AttributesRegistry.updateAttributeModifiers(stepAttribute, AttributesRegistry.AUTO_STEP_HEIGHT, ConfigRegistry.MEGA_MUSHROOM_AUTO_STEP.get(), false, true);
            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM));
        }

        if (entity.getData(DataAttachmentRegistry.SUPER_STAR_DURATION) == 0
                && entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)) {
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, false);
            entity.setData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME, false);
        }
    }

    @SubscribeEvent
    public static void postEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        Vec3 motion = entity.getDeltaMovement();
        int spinningTicks = entity.getPersistentData().getInt("marioverse:spinning_ticks");
        BlockPos pos = entity.blockPosition();

        TickEventHandlers.collideWithBlocks(level, entity);

        if (!level.isClientSide && entity instanceof LivingEntity livingEntity) {
            TickEventHandlers.megaMushroomScale(livingEntity);
            TickEventHandlers.miniMushroomScale(livingEntity);
            TickEventHandlers.superMushroomScale(livingEntity);
        }

        if (entity.isVehicle() && spinningTicks > 0) {
            entity.setYRot(entity.getYRot() + 30);
            entity.getPersistentData().putInt("marioverse:spinning_ticks", spinningTicks - 1);

            for (Entity rider : entity.getPassengers())
                rider.setYHeadRot(rider.getYHeadRot() + 30);
        }

        if (entity instanceof ServerPlayer player && level instanceof ServerLevel
                && player.hasData(DataAttachmentRegistry.VEHICLE_UUID)
                && entity.getData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN) == 0) {
            UUID uuid = player.getData(DataAttachmentRegistry.VEHICLE_UUID);
            ServerLevel serverLevel = level.getServer().getLevel(level.dimension());

            if (serverLevel != null) {
                Entity vehicle = serverLevel.getEntity(uuid);
                if (vehicle != null && !player.isPassenger())
                    player.startRiding(vehicle, true);
                player.removeData(DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN);
                player.removeData(DataAttachmentRegistry.VEHICLE_UUID);
            }
        }

        if (entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                && (entity.isSprinting() || entity.getDeltaMovement().horizontalDistance() >= 0.25D)
                && level.getFluidState(pos).is(FluidTags.WATER) && !level.getFluidState(pos.above()).is(FluidTags.WATER)) {
            if (motion.y <= 0)
                entity.setDeltaMovement(motion.x, 0.0D, motion.z);
            entity.setOnGround(true);
            entity.fallDistance = 0.0F;
        }
    }

    private static void collideWithBlocks(Level level, Entity entity) {
        Vec3 motion = entity.getDeltaMovement();
        boolean canGrief = EventHooks.canEntityGrief(level, entity)
                || (entity instanceof Player player && !player.getAbilities().flying);
        boolean isMovingHorizontal = motion.horizontalDistance() > 0.01;
        CompoundTag hitMap = TickEventHandlers.getHitMap(entity);
        List<String> toRemove = new ArrayList<>();
        CompoundTag data = entity.getPersistentData();

        if (data.contains(HIT_BLOCKS_TAG)) {
            for (String key : hitMap.getAllKeys()) {
                int time = hitMap.getInt(key) - 1;

                if (time <= 0)
                    toRemove.add(key);
                else hitMap.putInt(key, time);
            }

            for (String key : toRemove) {
                hitMap.remove(key);
            }
            if (hitMap.isEmpty()) {
                data.remove(HIT_BLOCKS_TAG);
            }
        }

        if (!level.isClientSide && canGrief && motion.y > 0 && !entity.isSpectator()) {
            AABB aboveBox = entity.getBoundingBox()
                    .deflate(0.1, 0.0, 0.1)
                    .expandTowards(0, motion.y + 0.2, 0);
            BlockPos min = BlockPos.containing(aboveBox.minX, aboveBox.minY, aboveBox.minZ);
            BlockPos max = BlockPos.containing(aboveBox.maxX, aboveBox.maxY, aboveBox.maxZ);

            for (BlockPos posAbove : BlockPos.betweenClosed(min, max)) {
                BlockState stateAbove = level.getBlockState(posAbove);
                long posKey = posAbove.asLong();
                String key = Long.toString(posKey);

                if (stateAbove.isAir())
                    continue;
                if (hitMap.contains(key))
                    continue;
                boolean didHit = false;

                if (!didHit && stateAbove.is(BlockRegistry.ON_OFF_SWITCH)
                        && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES)) {
                    OnOffSwitchBlock.hitSwitchBlock(level, posAbove, entity);
                    didHit = true;
                }

                if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                        && level.getBlockEntity(posAbove) instanceof QuestionBlockEntity questionBE) {
                    QuestionBlock.hitQuestionBlock(level, posAbove, entity, questionBE);
                    didHit = true;
                }

                if (!didHit && stateAbove.is(TagRegistry.SMASHABLE_BLOCKS)
                        && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)) {
                    SmashableBrickBlock.smashBlock(level, posAbove, stateAbove, entity);
                    didHit = true;
                }

                if (!didHit && stateAbove.is(TagRegistry.BONKABLE_BLOCKS)
                        && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)) {
                    if (stateAbove.hasProperty(QuestionBlock.EMPTY) && stateAbove.getValue(QuestionBlock.EMPTY))
                        level.playSound(null, posAbove, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    else level.playSound(null, posAbove, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    didHit = true;
                }

                if (didHit)
                    hitMap.putInt(key, 5);
            }
        }

        if (!level.isClientSide && canGrief && isMovingHorizontal && !entity.isSpectator()) {
            AABB box = entity.getBoundingBox()
                    .deflate(0, 0.2, 0)
                    .inflate(0.1, 0, 0.1);
            BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
            BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);

            for (BlockPos tagertPos : BlockPos.betweenClosed(min, max)) {
                BlockState targetState = level.getBlockState(tagertPos);
                double diffX = tagertPos.getX() + 0.5 - entity.getX();
                double diffZ = tagertPos.getZ() + 0.5 - entity.getZ();
                long posKey = tagertPos.asLong();
                String key = Long.toString(posKey);

                if (targetState.isAir())
                    continue;
                if (tagertPos.getY() >= entity.getBoundingBox().maxY - 0.1)
                    continue;
                if (hitMap.contains(key))
                    continue;
                boolean didHit = false;

                Direction direction;
                if (Math.abs(diffX) > Math.abs(diffZ))
                    direction = diffX > 0 ? Direction.EAST : Direction.WEST;
                else direction = diffZ > 0 ? Direction.SOUTH : Direction.NORTH;

                if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES_FROM_SIDE)) {
                    OnOffSwitchBlock.hitSwitchBlockFromSide(level, tagertPos, entity);
                    didHit = true;
                }

                if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS_FROM_SIDE)) {
                    QuestionBlock.hitQuestionBlockFromSide(level, tagertPos, entity);
                    didHit = true;
                }

                if (!didHit && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS_FROM_SIDE)) {
                    SmashableBrickBlock.smashBlockFromSide(level, tagertPos, targetState, entity, direction);
                    didHit = true;
                }

                if (!didHit && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS_FROM_SIDE)) {
                    StorageBrickBlock.bonkBlockFromSide(level, tagertPos, targetState);
                    didHit = true;
                }

                if (didHit)
                    hitMap.putInt(key, 5);
            }
        }
    }

    public static void collideWithEntity(Entity attackingEntity) {
        AABB boundingBox = attackingEntity.getBoundingBox().inflate(0.15);
        List<Entity> entities = attackingEntity.level().getEntities(attackingEntity, boundingBox, entityList -> entityList != attackingEntity);

        if (!entities.isEmpty()) {
            for (Entity collidedEntity : entities) {
                if (!collidedEntity.level().isClientSide) {
                    if (!(attackingEntity instanceof LivingEntity livingEntity))
                        return;
                    if (!(collidedEntity instanceof LivingEntity collidedLivingEntity))
                        continue;
                    if (attackingEntity.getData(DataAttachmentRegistry.ATTACK_COOLDOWN) > 0)
                        return;
                    if (attackingEntity.isSpectator() || !collidedEntity.isAlive()
                            || collidedEntity.getType().is(TagRegistry.MEGA_MUSHROOM_CANNOT_DAMAGE))
                        continue;
                    if (attackingEntity.getVehicle() == collidedEntity
                            || collidedEntity.getVehicle() == attackingEntity
                            || attackingEntity.isPassengerOfSameVehicle(collidedEntity))
                        continue;
                    if (attackingEntity.isPassenger()) {
                        Entity vehicle = attackingEntity.getVehicle();
                        if (vehicle != null) {
                            Vec3 ridingPos = vehicle.getPassengerRidingPosition(attackingEntity);

                            if (collidedLivingEntity.getBoundingBox().maxY <= ridingPos.y)
                                continue;
                        }
                    }

                    boolean hasNoArmor = true;
                    for (ItemStack armorSlot : collidedLivingEntity.getArmorSlots()) {
                        if (!armorSlot.isEmpty()) {
                            hasNoArmor = false;
                            break;
                        }
                    }

                    if (ConfigRegistry.MEGA_MOBS_DO_DAMAGE.get() || attackingEntity.getType().is(TagRegistry.CAN_DO_DAMAGE_AS_MEGA)) {
                        if (hasNoArmor && attackingEntity.getType().is(TagRegistry.MEGA_MUSHROOM_CAN_INSTAKILL))
                            collidedEntity.hurt(DamageSourceRegistry.megaMushroom(collidedLivingEntity, attackingEntity),
                                    collidedLivingEntity.getHealth());
                        else collidedEntity.hurt(DamageSourceRegistry.megaMushroom(collidedLivingEntity, attackingEntity),
                                    ConfigRegistry.MEGA_MUSHROOM_DAMAGE.get().floatValue());
                    }

                    if (attackingEntity instanceof NeutralMob neutralMob) {
                        neutralMob.isAngryAt(livingEntity);
                        neutralMob.setTarget(livingEntity);
                        neutralMob.setPersistentAngerTarget(attackingEntity.getUUID());
                    }

                    double knockbackStrength = 3.0;
                    Vec3 knockbackDirection = collidedEntity.position().subtract(attackingEntity.position()).normalize();
                    Vec3 knockbackVelocity = knockbackDirection.scale(knockbackStrength).add(0, 1.0, 0);

                    collidedEntity.setDeltaMovement(knockbackVelocity);
                    attackingEntity.setData(DataAttachmentRegistry.ATTACK_COOLDOWN, 1);
                }
                break;
            }
        }
    }

    private static void breakBlocks(Entity entity) {
        Level level = entity.level();
        AABB box = entity.getBoundingBox();
        Vec3 motion = entity.getDeltaMovement();
        double moveX = entity.getX() - entity.xOld;
        double moveZ = entity.getZ() - entity.zOld;

        Direction facing = Direction.getNearest(moveX, 0, moveZ);
        double dx = facing.getStepX();
        double dz = facing.getStepZ();

        if (facing.getAxis().isVertical())
            return;
        if (entity instanceof Mob mob && mob.isNoAi())
            return;
        if (entity.isSpectator())
            return;

        AABB forwardBox = box.move(dx * 0.5D, 0, dz * 0.5D)
                .inflate(Math.max(Math.abs(dx) * 1.15D, 0.25D), 0, Math.max(Math.abs(dz) * 1.15D, 0.25D));

        TickEventHandlers.breakIntersectingBlocks(level, entity, forwardBox, false, false);

        if (motion.y > 0) {
            AABB aboveBox = box.move(0, 1, 0).inflate(0.0D, 0.0D, 0.0D);
            TickEventHandlers.breakIntersectingBlocks(level, entity, aboveBox, true, false);
        }

        if (motion.y < -0.25) {
            AABB belowBox = box.move(0, -1, 0).inflate(0.0D, 0.0D, 0.0D);
            TickEventHandlers.breakIntersectingBlocks(level, entity, belowBox, false, true);
        }
    }

    private static void breakIntersectingBlocks(Level level, Entity entity, AABB box, boolean isJumping, boolean isFalling) {
        BlockPos min = BlockPos.containing(box.minX, box.minY, box.minZ);
        BlockPos max = BlockPos.containing(box.maxX, box.maxY, box.maxZ);

        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            BlockState state = level.getBlockState(pos);

            if (isFalling) {
                if (!state.is(TagRegistry.MEGA_MUSHROOM_CAN_BREAK_WHEN_FALLING))
                    continue;
            } else {
                if (!state.is(TagRegistry.MEGA_MUSHROOM_CAN_BREAK))
                    continue;
                if (entity.isPassenger()) {
                    Entity vehicle = entity.getVehicle();
                    if (vehicle != null) {
                        Vec3 ridingPos = vehicle.getPassengerRidingPosition(entity);

                        if (pos.getY() <= ridingPos.y)
                            continue;
                    }
                }
            }

            if (entity instanceof Player player && !level.mayInteract(player, pos))
                continue;
            if (entity instanceof Player player && !player.mayBuild())
                continue;

            TagKey<Block> breakTag = TagRegistry.MEGA_MUSHROOM_CAN_BREAK;
            if (entity instanceof Player player && player.blockActionRestricted(level, pos, GameType.ADVENTURE))
                breakTag = TagRegistry.MEGA_MUSHROOM_CAN_BREAK_IN_ADVENTURE_MODE;

            if (!state.isAir() && state.is(breakTag)) {
                if (entity instanceof Player player && player.isCreative() || !ConfigRegistry.MEGA_MOBS_DROP_ITEMS.get())
                    level.removeBlock(pos, true);
                else level.destroyBlock(pos, true, entity);
            }

            if (isJumping) {
                BlockPos posAbove = pos.above();
                BlockState stateAbove = level.getBlockState(posAbove);


                if (!stateAbove.isAir() && stateAbove.is(breakTag)
                        && !stateAbove.getCollisionShape(level, posAbove).isEmpty()) {
                    if (entity instanceof Player player && player.isCreative() || !ConfigRegistry.MEGA_MOBS_DROP_ITEMS.get())
                        level.removeBlock(posAbove, true);
                    else level.destroyBlock(posAbove, true, entity);
                }
            } else if (isFalling) {
                BlockPos posBelow = pos.below();
                BlockState stateBelow = level.getBlockState(posBelow);

                TagKey<Block> fallingBreakTag = TagRegistry.MEGA_MUSHROOM_CAN_BREAK_WHEN_FALLING;
                if (entity instanceof Player player && player.blockActionRestricted(level, pos, GameType.ADVENTURE))
                    fallingBreakTag = TagRegistry.MEGA_MUSHROOM_CAN_BREAK_IN_ADVENTURE_MODE;

                if (!stateBelow.isAir() && stateBelow.is(fallingBreakTag)) {
                    if (entity instanceof Player player && player.isCreative() || !ConfigRegistry.MEGA_MOBS_DROP_ITEMS.get())
                        level.removeBlock(posBelow, true);
                    else level.destroyBlock(posBelow, true, entity);
                }
            }
        }
    }

    public static void megaMushroomScale(LivingEntity entity) {
        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        boolean hasMegaMushroom = entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM);
        boolean hasMiniMushroom = entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        boolean hasSuperMushroom = entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM);
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasMegaMushroom ? ConfigRegistry.MEGA_MUSHROOM_HEIGHT_SCALE.get() : 1.0D;
        double targetHeightScale = hasMegaMushroom ? ConfigRegistry.MEGA_MUSHROOM_HEIGHT_SCALE.get() : 1.0D;
        double targetWidthScale = hasMegaMushroom ? ConfigRegistry.MEGA_MUSHROOM_WIDTH_SCALE.get() : 1.0D;

        boolean shouldScale = hasMegaMushroom;
        boolean shouldReset = !hasMegaMushroom && !hasMiniMushroom && hasSuperMushroom;

        TickEventHandlers.updateScale(entity, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
                targetEyeHeightScale, scalingSpeed, heightScale, widthScale, shouldReset);
    }

    public static void miniMushroomScale(LivingEntity entity) {
        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        boolean hasMiniMushroom = entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        boolean hasSuperMushroom = entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM);
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasMiniMushroom ? ConfigRegistry.MINI_MUSHROOM_HEIGHT_SCALE.get()
                : hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetHeightScale = hasMiniMushroom ? ConfigRegistry.MINI_MUSHROOM_HEIGHT_SCALE.get()
                : hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetWidthScale = hasMiniMushroom ? ConfigRegistry.MINI_MUSHROOM_WIDTH_SCALE.get()
                : hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_WIDTH_SCALE.get();

        boolean shouldScale = !hasSuperMushroom && hasMiniMushroom;
        boolean shouldReset = hasSuperMushroom && !hasMiniMushroom;

        TickEventHandlers.updateScale(entity, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
                targetEyeHeightScale, scalingSpeed, heightScale, widthScale, shouldReset);
    }

    public static void superMushroomScale(LivingEntity entity) {
        Level world = entity.level();
        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        boolean hasMegaMushroom = entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM);
        boolean hasMiniMushroom = entity.getData(DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        boolean hasSuperMushroom = entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM);
        boolean hasSuperMushroomOverride = entity.getData(DataAttachmentRegistry.HAS_SUPER_MUSHROOM_OVERRIDE);
        boolean isPlayer = entity instanceof Player;
        float health = entity.getHealth();
        double thresholdHealth = entity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get();
        if (isPlayer)
            thresholdHealth = ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get();
        boolean lowHealth = health <= thresholdHealth;
        boolean healHealth = health > thresholdHealth;
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetHeightScale = hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetWidthScale = hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_WIDTH_SCALE.get();

        boolean shouldScale = !hasSuperMushroom && !hasMegaMushroom && !hasMiniMushroom
                && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK)
                    && ((isPlayer && (hasSuperMushroomOverride
                        || (lowHealth && world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_PLAYERS))))
                    || (!isPlayer && (hasSuperMushroomOverride
                        || (lowHealth && world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_ALL_MOBS)))));

        boolean shouldReset = hasSuperMushroom && !hasMegaMushroom && !hasMiniMushroom
                && ((isPlayer && (hasSuperMushroomOverride
                    || (healHealth && world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_PLAYERS))))
                || (!isPlayer && (hasSuperMushroomOverride
                    || (healHealth && world.getGameRules().getBoolean(Marioverse.DAMAGE_SHRINKS_ALL_MOBS)))));

        TickEventHandlers.updateScale(entity, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
                targetEyeHeightScale, scalingSpeed, heightScale, widthScale, shouldReset);
    }

    private static void updateScale(LivingEntity entity, boolean shouldScale, double targetHeightScale, double targetWidthScale,
                                    AttributeInstance eyeHeightScale, double targetEyeHeightScale, float scalingSpeed,
                                    AttributeInstance heightScale, AttributeInstance widthScale, boolean shouldReset) {
        double currentHeight = heightScale != null ? heightScale.getValue() : 1.0D;
        double currentWidth = widthScale != null ? widthScale.getValue() : 1.0D;

        if (shouldScale && (Math.abs(currentHeight - targetHeightScale) > 1e-4 || Math.abs(currentWidth - targetWidthScale) > 1e-4)) {
            if (entity.getLastDamageSource() != null && entity.isDamageSourceBlocked(entity.getLastDamageSource()))
                return;
            TickEventHandlers.scale(eyeHeightScale, targetEyeHeightScale, scalingSpeed, v -> {});
            TickEventHandlers.scale(heightScale, targetHeightScale, scalingSpeed, v -> {});
            TickEventHandlers.scale(widthScale, targetWidthScale, scalingSpeed, v -> {});
        }

        if (shouldReset && (Math.abs(currentHeight - targetHeightScale) > 1e-4 || Math.abs(currentWidth - targetWidthScale) > 1e-4)) {
            if (eyeHeightScale != null && eyeHeightScale.getValue() != 1.0D)
                TickEventHandlers.scale(eyeHeightScale, targetEyeHeightScale, scalingSpeed, v -> {});

            if (heightScale != null && heightScale.getValue() != 1.0D)
                TickEventHandlers.scale(heightScale, targetHeightScale, scalingSpeed, v -> {});

            if (widthScale != null && widthScale.getValue() != 1.0D)
                TickEventHandlers.scale(widthScale, targetWidthScale, scalingSpeed, v -> {});
        }
    }

    private static void scale(AttributeInstance scaleAttribute, double targetScale, float scalingSpeed, Consumer<Double> setter) {
        ResourceLocation modifier = AttributesRegistry.DAMAGED_SCALE;

        if (scaleAttribute != null) {
            double actualScale = scaleAttribute.getValue();
            double lerpedScale = Mth.lerp(scalingSpeed, actualScale, targetScale);
            double modifierAmount = lerpedScale - 1.0D;

            if (Math.abs(actualScale - targetScale) < 0.0001)
                lerpedScale = targetScale;

            if (scaleAttribute.hasModifier(modifier) && (Math.abs(modifierAmount) < 0.001 || targetScale == 1.0D))
                scaleAttribute.removeModifier(modifier);

            if (lerpedScale != targetScale) {
                scaleAttribute.removeModifier(modifier);
                scaleAttribute.addPermanentModifier(new AttributeModifier(modifier, modifierAmount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

                if (Math.abs(actualScale - targetScale) < 0.01)
                    setter.accept(targetScale);
                else setter.accept(lerpedScale);
            }
        }
    }

    private static final String HIT_BLOCKS_TAG = "marioverse:hit_blocks";

    private static CompoundTag getHitMap(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(HIT_BLOCKS_TAG))
            data.put(HIT_BLOCKS_TAG, new CompoundTag());
        return data.getCompound(HIT_BLOCKS_TAG);
    }
}