package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.AbilityBlock;
import com.wenxin2.marioverse.blocks.OnOffSwitchBlock;
import com.wenxin2.marioverse.blocks.QuestionBlock;
import com.wenxin2.marioverse.blocks.SmashableBrickBlock;
import com.wenxin2.marioverse.blocks.StorageBrickBlock;
import com.wenxin2.marioverse.blocks.entities.QuestionBlockEntity;
import com.wenxin2.marioverse.entities.KoopaShellEntity;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.items.MaleCostumeItem;
import com.wenxin2.marioverse.items.MegaMushroomItem;
import com.wenxin2.marioverse.items.MiniMushroomItem;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3d;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class TickEventHandlers {
    private static final String HIT_BLOCKS_TAG = "marioverse:hit_blocks";
    private static double currentEyeHeightScale = 1.0;
    private static double currentHeightScale = 1.0;
    private static double currentWidthScale = 1.0;

    @SubscribeEvent
    public static void preEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        SoundSource soundSource = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.AMBIENT;

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

        if (entity.hasData(DataAttachmentRegistry.FIRE_FLOWER_DURATION) &&
                entity.getData(DataAttachmentRegistry.FIRE_FLOWER_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.FIRE_FLOWER_DURATION, entity.getData(DataAttachmentRegistry.FIRE_FLOWER_DURATION) - 1);

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

        if (entity.hasData(DataAttachmentRegistry.ICE_FLOWER_DURATION) &&
                entity.getData(DataAttachmentRegistry.ICE_FLOWER_DURATION) > 0)
            entity.setData(DataAttachmentRegistry.ICE_FLOWER_DURATION, entity.getData(DataAttachmentRegistry.ICE_FLOWER_DURATION) - 1);

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

        if (entity.getData(DataAttachmentRegistry.FIRE_FLOWER_DURATION) == 0
                && entity.getData(DataAttachmentRegistry.HAS_FIRE_FLOWER)) {
            if (entity instanceof LivingEntity livingEntity)
                MaleCostumeItem.resetCostumes(livingEntity);
            entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, false);
            entity.removeData(DataAttachmentRegistry.FIRE_FLOWER_DURATION);
            level.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                    soundSource, 1.0F, pitch);
        }

        if (entity.getData(DataAttachmentRegistry.ICE_FLOWER_DURATION) == 0
                && entity.getData(DataAttachmentRegistry.HAS_ICE_FLOWER)) {
            if (entity instanceof LivingEntity livingEntity)
                MaleCostumeItem.resetCostumes(livingEntity);
            entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, false);
            entity.removeData(DataAttachmentRegistry.ICE_FLOWER_DURATION);
            level.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(),
                    soundSource, 1.0F, pitch);
        }

        if (entity.getData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION) == 0
                && entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)
                && entity instanceof LivingEntity livingEntity) {
            AttributeInstance healthAttribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = livingEntity.getAttribute(Attributes.STEP_HEIGHT);

            entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.PLAYED_MEGA_MUSHROOM_THEME, false);
            entity.removeData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION);

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

        if (entity instanceof LivingEntity livingEntity) {
            AbilityBlock.characterAbility(livingEntity);
            MegaMushroomItem.megaMushroomAbility(livingEntity);
            MiniMushroomItem.miniMushroomAbility(livingEntity);
            AbilityBlock.setAirborneDuration(livingEntity);

            if (!level.isClientSide) {
                TickEventHandlers.megaMushroomScale(livingEntity);
                TickEventHandlers.miniMushroomScale(livingEntity);
                TickEventHandlers.superMushroomScale(livingEntity);
            }
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
//    public static final List<AABB> DEBUG_BOXES = new ArrayList<>();

    private static void collideWithBlocks(Level level, Entity entity) {
        Vec3 motion = entity.getDeltaMovement();
        float pitch = 0.8F + level.random.nextFloat() * 0.2F;
        boolean canGrief = EventHooks.canEntityGrief(level, entity)
                || (entity instanceof Player player && !player.getAbilities().flying);
        List<String> toRemove = new ArrayList<>();
        CompoundTag hitMap = TickEventHandlers.getHitMap(entity);
        CompoundTag data = entity.getPersistentData();

        if (hitMap != null) {
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
                hitMap = null;
            }
        }

        Object object = null;
        if (ModList.get().isLoaded("sable"))
            object = SableProvider.getContext(entity.level(), entity);

        boolean isMovingHorizontal = motion.horizontalDistance() > 0.01;

        if (canGrief && motion.y > 0 && !entity.isSpectator()) {
            AABB hitBox = entity.getBoundingBox().deflate(0.05, 0.0, 0.05)
                    .expandTowards(0, motion.y + 0.02, 0);
            BlockPos min = BlockPos.containing(hitBox.minX, hitBox.minY, hitBox.minZ);
            BlockPos max = BlockPos.containing(hitBox.maxX, hitBox.maxY, hitBox.maxZ);

            if (object instanceof SableProvider.SableContext context) {
                Quaterniondc rotation = context.subLevel.logicalPose().orientation();
                min = context.posEmbedded.offset(-2, -2, -2);
                max = context.posEmbedded.offset(2, 2, 2);

                Vector3d c1 = rotation.transform(new Vector3d(hitBox.minX - entity.getX(), hitBox.minY - entity.getY(), hitBox.minZ - entity.getZ()));
                Vector3d c2 = rotation.transform(new Vector3d(hitBox.minX - entity.getX(), hitBox.minY - entity.getY(), hitBox.maxZ - entity.getZ()));
                Vector3d c3 = rotation.transform(new Vector3d(hitBox.minX - entity.getX(), hitBox.maxY - entity.getY(), hitBox.minZ - entity.getZ()));
                Vector3d c4 = rotation.transform(new Vector3d(hitBox.minX - entity.getX(), hitBox.maxY - entity.getY(), hitBox.maxZ - entity.getZ()));
                Vector3d c5 = rotation.transform(new Vector3d(hitBox.maxX - entity.getX(), hitBox.minY - entity.getY(), hitBox.minZ - entity.getZ()));
                Vector3d c6 = rotation.transform(new Vector3d(hitBox.maxX - entity.getX(), hitBox.minY - entity.getY(), hitBox.maxZ - entity.getZ()));
                Vector3d c7 = rotation.transform(new Vector3d(hitBox.maxX - entity.getX(), hitBox.maxY - entity.getY(), hitBox.minZ - entity.getZ()));
                Vector3d c8 = rotation.transform(new Vector3d(hitBox.maxX - entity.getX(), hitBox.maxY - entity.getY(), hitBox.maxZ - entity.getZ()));

                double minWX = Math.min(Math.min(Math.min(c1.x, c2.x), Math.min(c3.x, c4.x)),
                        Math.min(Math.min(c5.x, c6.x), Math.min(c7.x, c8.x)));
                double minWY = Math.min(Math.min(Math.min(c1.y, c2.y), Math.min(c3.y, c4.y)),
                        Math.min(Math.min(c5.y, c6.y), Math.min(c7.y, c8.y)));
                double minWZ = Math.min(Math.min(Math.min(c1.z, c2.z), Math.min(c3.z, c4.z)),
                        Math.min(Math.min(c5.z, c6.z), Math.min(c7.z, c8.z)));
                double maxWX = Math.max(Math.max(Math.max(c1.x, c2.x), Math.max(c3.x, c4.x)),
                        Math.max(Math.max(c5.x, c6.x), Math.max(c7.x, c8.x)));
                double maxWY = Math.max(Math.max(Math.max(c1.y, c2.y), Math.max(c3.y, c4.y)),
                        Math.max(Math.max(c5.y, c6.y), Math.max(c7.y, c8.y)));
                double maxWZ = Math.max(Math.max(Math.max(c1.z, c2.z), Math.max(c3.z, c4.z)),
                        Math.max(Math.max(c5.z, c6.z), Math.max(c7.z, c8.z)));

                Vec3 relativePlayerPos = new Vec3(entity.getX() - Math.floor(entity.getX()),
                        entity.getY() - Math.floor(entity.getY()), entity.getZ() - Math.floor(entity.getZ()));
                Vector3d rotatedPlayerOffset = rotation.transform(new Vector3d(relativePlayerPos.x, relativePlayerPos.y, relativePlayerPos.z));
                double centerX = context.posWorld.getX() + rotatedPlayerOffset.x;
                double centerY = context.posWorld.getY() + rotatedPlayerOffset.y;
                double centerZ = context.posWorld.getZ() + rotatedPlayerOffset.z;

                AABB transformedHitBox = new AABB(centerX + minWX, centerY + minWY, centerZ + minWZ,
                        centerX + maxWX, centerY + maxWY, centerZ + maxWZ);

                for (BlockPos posAbove : BlockPos.betweenClosed(min, max)) {
                    Vector3d rotated = rotation.transform(new Vector3d(posAbove.getX() - context.posEmbedded.getX(),
                            posAbove.getY() - context.posEmbedded.getY(), posAbove.getZ() - context.posEmbedded.getZ()));
                    Vec3 worldVec = new Vec3(context.posWorld.getX() + rotated.x + 0.5,
                            context.posWorld.getY() + rotated.y + 0.5, context.posWorld.getZ() + rotated.z + 0.5);
                    BlockPos worldPos = BlockPos.containing(worldVec);
                    AABB blockBox = new AABB(worldPos);

//                    DEBUG_BOXES.add(blockBox);

                    if (!transformedHitBox.intersects(blockBox))
                        continue;

                    BlockState stateAbove = context.accessor.getBlockState(posAbove);
                    BlockEntity blockEntityAbove = context.accessor.getBlockEntity(posAbove);
                    long posKey = worldPos.asLong();

                    if (level instanceof ServerLevel) {
                        stateAbove = context.accessor.getServerBlockState(worldPos);
                        blockEntityAbove = context.accessor.getServerBlockEntity(worldPos);
                    }

                    String key = Long.toString(posKey);
                    if (hitMap != null && hitMap.contains(key))
                        continue;
                    boolean didHit = false;

                    if (!didHit && stateAbove.is(BlockRegistry.ON_OFF_SWITCH)
                            && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES)) {
                        OnOffSwitchBlock.hitSwitchBlock(level, BlockPos.of(posKey), entity);
                        didHit = true;
                    }

                    if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                            && blockEntityAbove instanceof QuestionBlockEntity questionBE
                            && !(stateAbove.hasProperty(QuestionBlock.EMPTY) && stateAbove.getValue(QuestionBlock.EMPTY))) {
                        QuestionBlock.hitQuestionBlock(level, BlockPos.of(posKey), entity, questionBE);
                        didHit = true;
                    }

                    if (!didHit && stateAbove.is(TagRegistry.SMASHABLE_BLOCKS)
                            && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)) {
                        SmashableBrickBlock.smashBlock(level, BlockPos.of(posKey), stateAbove, entity);
                        didHit = true;
                    }

                    if (!didHit && stateAbove.is(TagRegistry.BONKABLE_BLOCKS)
                            && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)) {
                        level.playSound(null, BlockPos.of(posKey), SoundRegistry.BLOCK_BONK.get(),
                                SoundSource.BLOCKS, 1.0F, pitch);
                        didHit = true;
                    }

                    if (didHit) {
                        hitMap = getOrCreateHitMap(entity);
                        hitMap.putInt(key, 5);
                    }
                }
            } else {
                for (BlockPos posAbove : BlockPos.betweenClosed(min, max)) {
                    BlockState stateAbove = level.getBlockState(posAbove);
                    BlockEntity blockEntityAbove = level.getBlockEntity(posAbove);
                    long posKey = posAbove.asLong();

                    String key = Long.toString(posKey);
                    if (hitMap != null && hitMap.contains(key))
                        continue;
                    boolean didHit = false;

                    if (!didHit && stateAbove.is(BlockRegistry.ON_OFF_SWITCH)
                            && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES)) {
                        OnOffSwitchBlock.hitSwitchBlock(level, BlockPos.of(posKey), entity);
                        didHit = true;
                    }

                    if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                            && blockEntityAbove instanceof QuestionBlockEntity questionBE
                            && !(stateAbove.hasProperty(QuestionBlock.EMPTY) && stateAbove.getValue(QuestionBlock.EMPTY))) {
                        QuestionBlock.hitQuestionBlock(level, BlockPos.of(posKey), entity, questionBE);
                        didHit = true;
                    }

                    if (!didHit && stateAbove.is(TagRegistry.SMASHABLE_BLOCKS)
                            && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)) {
                        SmashableBrickBlock.smashBlock(level, BlockPos.of(posKey), stateAbove, entity);
                        didHit = true;
                    }

                    if (!didHit && entity instanceof LivingEntity livingEntity
                            && stateAbove.is(TagRegistry.ABILITY_BLOCKS)
                            && entity.getType().is(TagRegistry.CAN_HIT_ABILITY_BLOCKS)) {
                        AbilityBlock.hitAbilityBlock(level, BlockPos.of(posKey), stateAbove, livingEntity);
                        didHit = true;
                    }

                    if (!didHit && stateAbove.is(TagRegistry.BONKABLE_BLOCKS)
                            && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)) {
                        if (stateAbove.hasProperty(QuestionBlock.EMPTY) && stateAbove.getValue(QuestionBlock.EMPTY))
                            level.playSound(null, BlockPos.of(posKey), SoundRegistry.BLOCK_BONK.get(),
                                    SoundSource.BLOCKS, 1.0F, pitch);
                        else
                            level.playSound(null, BlockPos.of(posKey), SoundRegistry.BLOCK_BONK.get(),
                                    SoundSource.BLOCKS, 1.0F, pitch);
                        didHit = true;
                    }

                    if (didHit) {
                        hitMap = getOrCreateHitMap(entity);
                        hitMap.putInt(key, 5);
                    }
                }
            }
        }

        if (!level.isClientSide && canGrief && isMovingHorizontal && !entity.isSpectator()) {
            AABB box = entity.getBoundingBox()
                    .inflate(0.1, 0, 0.1);
            int minX = Mth.floor(box.minX);
            int minY = Mth.floor(box.minY);
            int minZ = Mth.floor(box.minZ);
            int maxX = Mth.ceil(box.maxX) - 1;
            int maxY = Mth.ceil(box.maxY) - 1;
            int maxZ = Mth.ceil(box.maxZ) - 1;
            BlockPos min = new BlockPos(minX, minY, minZ);
            BlockPos max = new BlockPos(maxX, maxY, maxZ);

            for (BlockPos posTarget : BlockPos.betweenClosed(min, max)) {
                BlockState targetState = level.getBlockState(posTarget);
                BlockEntity blockEntity = level.getBlockEntity(posTarget);
                double diffX = posTarget.getX() + 0.5 - entity.getX();
                double diffZ = posTarget.getZ() + 0.5 - entity.getZ();
                long posKey = posTarget.asLong();

                if (ModList.get().isLoaded("sable")) {
                    SableProvider.SableContext context = SableProvider.getContext(level, entity);

                    if (context != null) {
                        BlockPos entityBase = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
                        int baseX = entityBase.getX();
                        int baseY = entityBase.getY();
                        int baseZ = entityBase.getZ();
                        BlockPos posEmbedded = context.posEmbedded
                                .offset(-(posTarget.getX() - baseX),
                                        posTarget.getY() - baseY,
                                        posTarget.getZ() - baseZ);
                        targetState = context.accessor.getBlockState(posEmbedded);
                        blockEntity = context.accessor.getBlockEntity(posEmbedded);
                        if (level instanceof ServerLevel) {
                            posEmbedded = context.posWorld
                                    .offset(-(posTarget.getX() - baseX),
                                            posTarget.getY() - baseY,
                                            posTarget.getZ() - baseZ);
                            targetState = context.accessor.getServerBlockState(posEmbedded);
                            blockEntity = context.accessor.getServerBlockEntity(posEmbedded);
                        }
                        diffX = posEmbedded.getX() + 0.5 - entity.getX();
                        diffZ = posEmbedded.getZ() + 0.5 - entity.getZ();
                        posKey = posEmbedded.asLong();
                    }
                }

                String key = Long.toString(posKey);
                if (hitMap != null && hitMap.contains(key))
                    continue;
                boolean didHit = false;

                Direction direction;
                if (Math.abs(diffX) > Math.abs(diffZ))
                    direction = diffX > 0 ? Direction.EAST : Direction.WEST;
                else direction = diffZ > 0 ? Direction.SOUTH : Direction.NORTH;
                Vec3 hitPos = Vec3.atCenterOf(posTarget).subtract(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.5));
                BlockHitResult hitResult = new BlockHitResult(hitPos, direction, posTarget, false);

                if (!didHit && targetState.is(BlockRegistry.ON_OFF_SWITCH)
                        && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES_FROM_SIDE)) {
                    if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                        shell.bounceShell(level, hitResult);
                    OnOffSwitchBlock.hitSwitchBlockFromSide(level, BlockPos.of(posKey), entity);
                    didHit = true;
                }

                if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS_FROM_SIDE)
                        && blockEntity instanceof QuestionBlockEntity
                        && !(targetState.hasProperty(QuestionBlock.EMPTY) && targetState.getValue(QuestionBlock.EMPTY))) {
                    if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                        shell.bounceShell(level, hitResult);
                    QuestionBlock.hitQuestionBlockFromSide(level, BlockPos.of(posKey), entity);
                    didHit = true;
                }

                if (!didHit && targetState.is(TagRegistry.SMASHABLE_BLOCKS)
                        && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS_FROM_SIDE)) {
                    if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                        shell.bounceShell(level, hitResult);
                    SmashableBrickBlock.smashBlock(level, BlockPos.of(posKey), targetState, entity);
                    didHit = true;
                }

                if (!didHit && entity instanceof LivingEntity livingEntity
                        && targetState.is(TagRegistry.ABILITY_BLOCKS)
                        && entity.getType().is(TagRegistry.CAN_HIT_ABILITY_BLOCKS_FROM_SIDE)) {
                    AbilityBlock.hitAbilityBlockFromSide(level, BlockPos.of(posKey), targetState, livingEntity);
                    didHit = true;
                }

                if (!didHit && targetState.is(TagRegistry.BONKABLE_BLOCKS)
                        && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS_FROM_SIDE)) {
                    if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                        shell.bounceShell(level, hitResult);
                    StorageBrickBlock.bonkBlockFromSide(level, BlockPos.of(posKey), targetState);
                    didHit = true;
                }

                if (didHit) {
                    hitMap = getOrCreateHitMap(entity);
                    hitMap.putInt(key, 5);
                }
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

        TickEventHandlers.updateScale(entity, AttributesRegistry.DAMAGED_SCALE, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
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

        TickEventHandlers.updateScale(entity, AttributesRegistry.DAMAGED_SCALE, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
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

        TickEventHandlers.updateScale(entity, AttributesRegistry.DAMAGED_SCALE, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
                targetEyeHeightScale, scalingSpeed, heightScale, widthScale, shouldReset);
    }

    public static void updateScale(LivingEntity entity, ResourceLocation modifierID, boolean shouldScale, double targetHeightScale, double targetWidthScale,
                                   AttributeInstance eyeHeightScale, double targetEyeHeightScale, float scalingSpeed,
                                   AttributeInstance heightScale, AttributeInstance widthScale, boolean shouldReset) {
        double currentHeight = TickEventHandlers.modifierScale(heightScale, modifierID);
        double currentWidth = TickEventHandlers.modifierScale(widthScale, modifierID);

        if (shouldScale && (Math.abs(currentHeight - targetHeightScale) > 1e-4 || Math.abs(currentWidth - targetWidthScale) > 1e-4)) {
            if (entity.getLastDamageSource() != null && entity.isDamageSourceBlocked(entity.getLastDamageSource()))
                return;
            TickEventHandlers.scale(eyeHeightScale, modifierID, targetEyeHeightScale, scalingSpeed, v -> {});
            TickEventHandlers.scale(heightScale, modifierID, targetHeightScale, scalingSpeed, v -> {});
            TickEventHandlers.scale(widthScale, modifierID, targetWidthScale, scalingSpeed, v -> {});
        }

        if (shouldReset && (Math.abs(currentHeight - targetHeightScale) > 1e-4 || Math.abs(currentWidth - targetWidthScale) > 1e-4)) {
            if (TickEventHandlers.modifierScale(eyeHeightScale, modifierID) != 1.0D)
                TickEventHandlers.scale(eyeHeightScale, modifierID, targetEyeHeightScale, scalingSpeed, v -> {});

            if (TickEventHandlers.modifierScale(heightScale, modifierID) != 1.0D)
                TickEventHandlers.scale(heightScale, modifierID, targetHeightScale, scalingSpeed, v -> {});

            if (TickEventHandlers.modifierScale(widthScale, modifierID) != 1.0D)
                TickEventHandlers.scale(widthScale, modifierID, targetWidthScale, scalingSpeed, v -> {});
        }
    }

    private static void scale(AttributeInstance scaleAttribute, ResourceLocation modifierID, double targetScale,
                              float scalingSpeed, Consumer<Double> setter) {
        if (scaleAttribute != null) {
            AttributeModifier existing = scaleAttribute.getModifier(modifierID);
            double modifierScale = existing != null ? existing.amount() + 1.0D : 1.0D;
            double lerpedScale = Mth.lerp(scalingSpeed, modifierScale, targetScale);
            double modifierAmount = lerpedScale - 1.0D;

            if (Math.abs(modifierScale - targetScale) < 0.0001)
                lerpedScale = targetScale;

            if (existing != null && (Math.abs(modifierAmount) < 0.001 || targetScale == 1.0D))
                scaleAttribute.removeModifier(modifierID);

            if (lerpedScale != targetScale) {
                scaleAttribute.removeModifier(modifierID);
                scaleAttribute.addPermanentModifier(new AttributeModifier(modifierID, modifierAmount, AttributeModifier.Operation.ADD_MULTIPLIED_BASE));

                if (Math.abs(modifierScale - targetScale) < 0.01)
                    setter.accept(targetScale);
                else setter.accept(lerpedScale);
            }
        }
    }

    private static double modifierScale(AttributeInstance attribute, ResourceLocation modifierId) {
        if (attribute == null)
            return 1.0D;

        AttributeModifier modifier = attribute.getModifier(modifierId);
        return modifier != null ? modifier.amount() + 1.0D : 1.0D;
    }

    @Nullable
    private static CompoundTag getHitMap(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        return data.contains(HIT_BLOCKS_TAG)
                ? data.getCompound(HIT_BLOCKS_TAG)
                : null;
    }

    private static CompoundTag getOrCreateHitMap(Entity entity) {
        CompoundTag data = entity.getPersistentData();
        if (!data.contains(HIT_BLOCKS_TAG)) {
            CompoundTag tag = new CompoundTag();
            data.put(HIT_BLOCKS_TAG, tag);
            return tag;
        }
        return data.getCompound(HIT_BLOCKS_TAG);
    }
}