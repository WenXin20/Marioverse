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
import com.wenxin2.marioverse.utils.ClearPipeTravel;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
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
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class TickEventHandlers {
    private static final int HIT_COOLDOWN_TICKS = 5;
    private static final int HIT_MAP_PRUNE_THRESHOLD = 64;
    private static double currentEyeHeightScale = 1.0;
    private static double currentHeightScale = 1.0;
    private static double currentWidthScale = 1.0;

    @SubscribeEvent
    public static void preEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (entity.hasData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES)
                && entity.getData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES) > 0
                && ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.onGround() || entity.isInWaterOrBubble())
                && !TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_SUPER_STAR)
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                    || level.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))) {
            entity.setData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES, 0);
            entity.removeData(DataAttachmentRegistry.STOMPED_MOBS);
        }

        if (!level.isClientSide && !entity.isSpectator() && !entity.isShiftKeyDown()
                && TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MEGA_MUSHROOM)) {
            if (ConfigRegistry.MEGA_MUSHROOM_BREAKS_BLOCKS.get()
                    && (ConfigRegistry.MEGA_MOBS_BREAK_BLOCKS.get() || entity.getType().is(TagRegistry.CAN_BREAK_BLOCKS_AS_MEGA))
                    && (level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
                        || (entity instanceof Player && entity.getType().is(TagRegistry.CAN_BREAK_BLOCKS_AS_MEGA))))
                TickEventHandlers.breakBlocks(entity);
            TickEventHandlers.collideWithEntity(entity);
        }

        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.ATTACK_COOLDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.CHECKPOINT_FLAG_COOLDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.FIRE_FLOWER_DURATION);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.FIREBALL_COOLDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.FREEZE_IMMUNITY_DURATION);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.FROZEN_DURATION);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.HIT_BLOCK_COOLDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.HIT_BLOCK_SOUND_COOLDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.ICE_BALL_COOLDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.ICE_FLOWER_DURATION);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.MEGA_MUSHROOM_DURATION);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.ONE_UPS_COOLDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.RIDE_VEHICLE_COUNTDOWN);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.SUPER_STAR_DURATION);
        TickEventHandlers.tickDown(entity, DataAttachmentRegistry.WARP_COOLDOWN);

        if (entity.hasData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN)) {
            int preventWarpCooldown = entity.getData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN);

            if (preventWarpCooldown > 0)
                entity.setData(DataAttachmentRegistry.PREVENT_WARP_COOLDOWN, preventWarpCooldown - 1);

            if (preventWarpCooldown == 0
                    && TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.PREVENT_WARP))
                entity.setData(DataAttachmentRegistry.PREVENT_WARP, false);
        }

        if (TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_FIRE_FLOWER)
                && TickEventHandlers.counter(entity, DataAttachmentRegistry.FIRE_FLOWER_DURATION) == 0) {
            if (entity instanceof LivingEntity livingEntity)
                MaleCostumeItem.resetCostumes(livingEntity);
            entity.setData(DataAttachmentRegistry.HAS_FIRE_FLOWER, false);
            entity.removeData(DataAttachmentRegistry.FIRE_FLOWER_DURATION);
            TickEventHandlers.playDamageTakenSound(level, entity);
        }

        if (TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_ICE_FLOWER)
                && TickEventHandlers.counter(entity, DataAttachmentRegistry.ICE_FLOWER_DURATION) == 0) {
            if (entity instanceof LivingEntity livingEntity)
                MaleCostumeItem.resetCostumes(livingEntity);
            entity.setData(DataAttachmentRegistry.HAS_ICE_FLOWER, false);
            entity.removeData(DataAttachmentRegistry.ICE_FLOWER_DURATION);
            TickEventHandlers.playDamageTakenSound(level, entity);
        }

        if (TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MEGA_MUSHROOM)
                && TickEventHandlers.counter(entity, DataAttachmentRegistry.MEGA_MUSHROOM_DURATION) == 0
                && entity instanceof LivingEntity livingEntity) {
            AttributeInstance healthAttribute = livingEntity.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance stepAttribute = livingEntity.getAttribute(Attributes.STEP_HEIGHT);

            entity.setData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM, false);
            entity.setData(DataAttachmentRegistry.PLAYED_MEGA_MUSHROOM_THEME, false);
            entity.removeData(DataAttachmentRegistry.MEGA_MUSHROOM_DURATION);

            AttributesRegistry.updateAttributeModifiers(stepAttribute, AttributesRegistry.AUTO_STEP_HEIGHT, ConfigRegistry.MEGA_MUSHROOM_AUTO_STEP.get(), false, true);
            AttributesRegistry.updateAttributeModifiers(healthAttribute, AttributesRegistry.MAX_HEATH, ConfigRegistry.MEGA_MUSHROOM_HEALTH.get(),
                    false, !TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MINI_MUSHROOM));
        }

        if (TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_SUPER_STAR)
                && TickEventHandlers.counter(entity, DataAttachmentRegistry.SUPER_STAR_DURATION) == 0) {
            entity.setData(DataAttachmentRegistry.HAS_SUPER_STAR, false);
            entity.setData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME, false);
        }
    }

    @SubscribeEvent
    public static void postEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        Vec3 motion = entity.getDeltaMovement();
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

                if (livingEntity instanceof Mob mob)
                    ClearPipeTravel.tick(mob);
            }
        }

        if (entity.isVehicle()) {
            int spinningTicks = entity.getPersistentData().getInt("marioverse:spinning_ticks");

            if (spinningTicks > 0) {
                entity.setYRot(entity.getYRot() + 30);
                entity.getPersistentData().putInt("marioverse:spinning_ticks", spinningTicks - 1);

                for (Entity rider : entity.getPassengers())
                    rider.setYHeadRot(rider.getYHeadRot() + 30);
            }
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

        if (TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MINI_MUSHROOM)
                && (entity.isSprinting() || entity.getDeltaMovement().horizontalDistance() >= 0.25D)
                && level.getFluidState(pos).is(FluidTags.WATER) && !level.getFluidState(pos.above()).is(FluidTags.WATER)) {
            if (motion.y <= 0)
                entity.setDeltaMovement(motion.x, 0.0D, motion.z);
            entity.setOnGround(true);
            entity.fallDistance = 0.0F;
        }
    }

    public static boolean hasFlag(Entity entity, Supplier<AttachmentType<Boolean>> type) {
        return entity.hasData(type) && entity.getData(type);
    }

    private static int counter(Entity entity, Supplier<AttachmentType<Integer>> type) {
        return entity.hasData(type) ? entity.getData(type) : 0;
    }

    private static void tickDown(Entity entity, Supplier<AttachmentType<Integer>> type) {
        if (!entity.hasData(type))
            return;

        int value = entity.getData(type);
        if (value > 0)
            entity.setData(type, value - 1);
    }

    private static void playDamageTakenSound(Level level, Entity entity) {
        float pitch = 0.9F + level.random.nextFloat() * 0.2F;
        SoundSource soundSource = entity instanceof Player ? SoundSource.PLAYERS : SoundSource.AMBIENT;
        level.playSound(null, entity.blockPosition(), SoundRegistry.DAMAGE_TAKEN.get(), soundSource, 1.0F, pitch);
    }

//    public static final List<AABB> DEBUG_BOXES = new ArrayList<>();

    private static boolean shapeIntersects(Level level, BlockPos pos, BlockState state, AABB box) {
        VoxelShape shape = state.getCollisionShape(level, pos);
        if (shape.isEmpty())
            shape = state.getShape(level, pos);

        for (AABB part : shape.toAabbs()) {
            if (part.move(pos).intersects(box))
                return true;
        }
        return false;
    }

    private static void collideWithBlocks(Level level, Entity entity) {
        if (entity instanceof AbstractWindCharge)
            return;

        EntityType<?> type = entity.getType();
        boolean canHitAbove = type.is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES) || type.is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                || type.is(TagRegistry.CAN_SMASH_BLOCKS) || type.is(TagRegistry.CAN_HIT_ABILITY_BLOCKS)
                || type.is(TagRegistry.CAN_BONK_BLOCKS);
        boolean canHitSide = type.is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES_FROM_SIDE) || type.is(TagRegistry.CAN_HIT_QUESTION_BLOCKS_FROM_SIDE)
                || type.is(TagRegistry.CAN_SMASH_BLOCKS_FROM_SIDE) || type.is(TagRegistry.CAN_HIT_ABILITY_BLOCKS_FROM_SIDE)
                || type.is(TagRegistry.CAN_BONK_BLOCKS_FROM_SIDE);
        if (!canHitAbove && !canHitSide)
            return;

        Vec3 motion = entity instanceof Projectile
                ? entity.position().subtract(entity.xOld, entity.yOld, entity.zOld) : entity.getDeltaMovement();
        float pitch = 0.8F + level.random.nextFloat() * 0.2F;
        boolean canGrief = EventHooks.canEntityGrief(level, entity)
                || (entity instanceof Player player && !player.getAbilities().flying);
        long currentTick = level.getGameTime();
        Long2LongOpenHashMap hitMap = entity.getExistingDataOrNull(DataAttachmentRegistry.HIT_BLOCK_TIMESTAMPS);

        if (hitMap != null && hitMap.size() > HIT_MAP_PRUNE_THRESHOLD) {
            ObjectIterator<Long2LongMap.Entry> it = hitMap.long2LongEntrySet().fastIterator();
            while (it.hasNext()) {
                if (currentTick - it.next().getLongValue() >= HIT_COOLDOWN_TICKS)
                    it.remove();
            }
        }

        double moveX = entity.getX() - entity.xOld;
        double moveZ = entity.getZ() - entity.zOld;
        boolean isAscending = motion.y > 0 || entity.getY() > entity.yOld;
        boolean isMovingHorizontal = motion.horizontalDistance() > 0.01 || moveX * moveX + moveZ * moveZ > 1.0E-4;

        if (level.isClientSide || !canGrief || entity.isSpectator())
            return;

        if (canHitAbove && isAscending) {
            AABB entityBox = entity.getBoundingBox();
            AABB hitBox = new AABB(entityBox.minX, entityBox.maxY, entityBox.minZ,
                    entityBox.maxX, entityBox.maxY + Math.max(motion.y, 0) + 0.02, entityBox.maxZ);
            TickEventHandlers.hitBlocksFromBelow(level, entity, hitBox, currentTick, pitch);
        }

        if (canHitSide && isMovingHorizontal)
            TickEventHandlers.hitBlocksFromSide(level, entity, entity.getBoundingBox().inflate(0.1, 0, 0.1),
                    entity.getX(), entity.getZ(), currentTick);

        if (SableProvider.isLoaded() && SableProvider.getContext(level, entity) instanceof SableProvider.SableContext context) {
            BlockPos plotPos = context.posWorld;
            Vec3 localCenter = SableProvider.toLocalVec(level, plotPos, entity.getBoundingBox().getCenter());
            if (localCenter == null)
                return;

            Vec3 localMotion = SableProvider.toLocalDirection(level, plotPos, motion);
            double halfWidth = entity.getBbWidth() * 0.5;
            double halfHeight = entity.getBbHeight() * 0.5;
            AABB localBox = new AABB(localCenter.x - halfWidth, localCenter.y - halfHeight, localCenter.z - halfWidth,
                    localCenter.x + halfWidth, localCenter.y + halfHeight, localCenter.z + halfWidth);

            if (canHitAbove && localMotion.y > 0) {
                AABB hitBox = new AABB(localBox.minX, localBox.maxY, localBox.minZ,
                        localBox.maxX, localBox.maxY + localMotion.y + 0.02, localBox.maxZ);
                TickEventHandlers.hitBlocksFromBelow(level, entity, hitBox, currentTick, pitch);
            }

            if (canHitSide && localMotion.horizontalDistanceSqr() > 1.0E-4)
                TickEventHandlers.hitBlocksFromSide(level, entity, localBox.inflate(0.1, 0, 0.1), localCenter.x, localCenter.z, currentTick);
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
        boolean hasMegaMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MEGA_MUSHROOM);
        boolean hasMiniMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        boolean hasSuperMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_SUPER_MUSHROOM);

        boolean shouldScale = hasMegaMushroom;
        boolean shouldReset = !hasMegaMushroom && !hasMiniMushroom && hasSuperMushroom;
        if (!shouldScale && !shouldReset)
            return;

        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasMegaMushroom ? ConfigRegistry.MEGA_MUSHROOM_HEIGHT_SCALE.get() : 1.0D;
        double targetHeightScale = hasMegaMushroom ? ConfigRegistry.MEGA_MUSHROOM_HEIGHT_SCALE.get() : 1.0D;
        double targetWidthScale = hasMegaMushroom ? ConfigRegistry.MEGA_MUSHROOM_WIDTH_SCALE.get() : 1.0D;

        TickEventHandlers.updateScale(entity, AttributesRegistry.DAMAGED_SCALE, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
                targetEyeHeightScale, scalingSpeed, heightScale, widthScale, shouldReset);
    }

    public static void miniMushroomScale(LivingEntity entity) {
        boolean hasMiniMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        boolean hasSuperMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_SUPER_MUSHROOM);

        boolean shouldScale = !hasSuperMushroom && hasMiniMushroom;
        boolean shouldReset = hasSuperMushroom && !hasMiniMushroom;
        if (!shouldScale && !shouldReset)
            return;

        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasMiniMushroom ? ConfigRegistry.MINI_MUSHROOM_HEIGHT_SCALE.get()
                : hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetHeightScale = hasMiniMushroom ? ConfigRegistry.MINI_MUSHROOM_HEIGHT_SCALE.get()
                : hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetWidthScale = hasMiniMushroom ? ConfigRegistry.MINI_MUSHROOM_WIDTH_SCALE.get()
                : hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_WIDTH_SCALE.get();

        TickEventHandlers.updateScale(entity, AttributesRegistry.DAMAGED_SCALE, shouldScale, targetHeightScale, targetWidthScale, eyeHeightScale,
                targetEyeHeightScale, scalingSpeed, heightScale, widthScale, shouldReset);
    }

    public static void superMushroomScale(LivingEntity entity) {
        boolean hasMegaMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MEGA_MUSHROOM);
        boolean hasMiniMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_MINI_MUSHROOM);
        if (hasMegaMushroom || hasMiniMushroom)
            return;

        Level world = entity.level();
        boolean hasSuperMushroom = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_SUPER_MUSHROOM);
        boolean hasSuperMushroomOverride = TickEventHandlers.hasFlag(entity, DataAttachmentRegistry.HAS_SUPER_MUSHROOM_OVERRIDE);
        boolean isPlayer = entity instanceof Player;
        GameRules.Key<GameRules.BooleanValue> shrinkRule = isPlayer
                ? Marioverse.DAMAGE_SHRINKS_PLAYERS : Marioverse.DAMAGE_SHRINKS_ALL_MOBS;

        boolean shouldScale = false;
        boolean shouldReset = false;

        if (hasSuperMushroomOverride) {
            shouldScale = !hasSuperMushroom && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK);
            shouldReset = hasSuperMushroom;
        } else if (world.getGameRules().getBoolean(shrinkRule)) {
            float health = entity.getHealth();
            double thresholdHealth = isPlayer ? ConfigRegistry.SHRINK_PLAYERS_AT_HEALTH.get()
                    : entity.getMaxHealth() * ConfigRegistry.SHRINK_MOBS_AT_HEALTH.get();

            if (hasSuperMushroom)
                shouldReset = health > thresholdHealth;
            else shouldScale = health <= thresholdHealth && !entity.getType().is(TagRegistry.DAMAGE_CANNOT_SHRINK);
        }

        if (!shouldScale && !shouldReset)
            return;

        AttributeInstance eyeHeightScale = entity.getAttribute(AttributesRegistry.EYE_HEIGHT_SCALE);
        AttributeInstance heightScale = entity.getAttribute(AttributesRegistry.HEIGHT_SCALE);
        AttributeInstance widthScale = entity.getAttribute(AttributesRegistry.WIDTH_SCALE);
        float scalingSpeed = 0.1F;

        double targetEyeHeightScale = hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetHeightScale = hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_HEIGHT_SCALE.get();
        double targetWidthScale = hasSuperMushroom ? 1.0D : ConfigRegistry.SHRINK_WIDTH_SCALE.get();

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

    private static void hitBlocksFromBelow(Level level, Entity entity, AABB hitBox, long currentTick, float pitch) {
        for (BlockPos posAbove : BlockPos.betweenClosed(BlockPos.containing(hitBox.minX, hitBox.minY, hitBox.minZ),
                BlockPos.containing(hitBox.maxX, hitBox.maxY, hitBox.maxZ))) {
            BlockState stateAbove = level.getBlockState(posAbove);
            if (!stateAbove.isAir() && TickEventHandlers.shapeIntersects(level, posAbove, stateAbove, hitBox))
                TickEventHandlers.hitBlockFromBelow(level, entity, posAbove.immutable(), stateAbove, currentTick, pitch);
        }
    }

    private static void hitBlocksFromSide(Level level, Entity entity, AABB box, double centerX, double centerZ, long currentTick) {
        BlockPos min = new BlockPos(Mth.floor(box.minX), Mth.floor(box.minY), Mth.floor(box.minZ));
        BlockPos max = new BlockPos(Mth.ceil(box.maxX) - 1, Mth.ceil(box.maxY) - 1, Mth.ceil(box.maxZ) - 1);

        for (BlockPos posTarget : BlockPos.betweenClosed(min, max)) {
            BlockState targetState = level.getBlockState(posTarget);
            if (!targetState.isAir())
                TickEventHandlers.hitBlockFromSide(level, entity, posTarget.immutable(), targetState,
                        posTarget.getX() + 0.5 - centerX, posTarget.getZ() + 0.5 - centerZ, currentTick);
        }
    }

    private static void hitBlockFromBelow(Level level, Entity entity, BlockPos pos, BlockState state, long currentTick, float pitch) {
        Long2LongOpenHashMap hitMap = entity.getExistingDataOrNull(DataAttachmentRegistry.HIT_BLOCK_TIMESTAMPS);
        long posKey = pos.asLong();

        if (hitMap != null && currentTick - hitMap.get(posKey) < HIT_COOLDOWN_TICKS)
            return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean didHit = false;

        if (state.is(BlockRegistry.ON_OFF_SWITCH) && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES)) {
            OnOffSwitchBlock.hitSwitchBlock(level, pos, entity);
            didHit = true;
        }

        if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS)
                && blockEntity instanceof QuestionBlockEntity questionBE
                && !(state.hasProperty(QuestionBlock.EMPTY) && state.getValue(QuestionBlock.EMPTY))) {
            QuestionBlock.hitQuestionBlock(level, pos, entity, questionBE);
            didHit = true;
        }

        if (!didHit && state.is(TagRegistry.SMASHABLE_BLOCKS) && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS)) {
            SmashableBrickBlock.smashBlock(level, pos, state, entity);
            didHit = true;
        }

        if (!didHit && entity instanceof LivingEntity livingEntity && state.is(TagRegistry.ABILITY_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_HIT_ABILITY_BLOCKS)) {
            AbilityBlock.hitAbilityBlock(level, pos, state, livingEntity);
            didHit = true;
        }

        if (!didHit && state.is(TagRegistry.BONKABLE_BLOCKS) && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS)) {
            level.playSound(null, pos, SoundRegistry.BLOCK_BONK.get(), SoundSource.BLOCKS, 1.0F, pitch);
            didHit = true;
        }

        if (didHit)
            entity.getData(DataAttachmentRegistry.HIT_BLOCK_TIMESTAMPS).put(posKey, currentTick);
    }

    private static void hitBlockFromSide(Level level, Entity entity, BlockPos pos, BlockState state, double diffX, double diffZ, long currentTick) {
        Long2LongOpenHashMap hitMap = entity.getExistingDataOrNull(DataAttachmentRegistry.HIT_BLOCK_TIMESTAMPS);
        long posKey = pos.asLong();

        if (hitMap != null && currentTick - hitMap.get(posKey) < HIT_COOLDOWN_TICKS)
            return;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean didHit = false;
        Direction direction;

        if (Math.abs(diffX) > Math.abs(diffZ))
            direction = diffX > 0 ? Direction.EAST : Direction.WEST;
        else direction = diffZ > 0 ? Direction.SOUTH : Direction.NORTH;
        Vec3 hitPos = Vec3.atCenterOf(pos).subtract(Vec3.atLowerCornerOf(direction.getNormal()).scale(0.5));
        BlockHitResult hitResult = new BlockHitResult(hitPos, direction, pos, false);

        if (state.is(BlockRegistry.ON_OFF_SWITCH) && entity.getType().is(TagRegistry.CAN_HIT_ON_OFF_SWITCHES_FROM_SIDE)) {
            if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                shell.bounceShell(level, hitResult);
            OnOffSwitchBlock.hitSwitchBlockFromSide(level, pos, entity);
            didHit = true;
        }

        if (!didHit && entity.getType().is(TagRegistry.CAN_HIT_QUESTION_BLOCKS_FROM_SIDE)
                && blockEntity instanceof QuestionBlockEntity
                && !(state.hasProperty(QuestionBlock.EMPTY) && state.getValue(QuestionBlock.EMPTY))) {
            if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                shell.bounceShell(level, hitResult);
            QuestionBlock.hitQuestionBlockFromSide(level, pos, entity);
            didHit = true;
        }

        if (!didHit && state.is(TagRegistry.SMASHABLE_BLOCKS) && entity.getType().is(TagRegistry.CAN_SMASH_BLOCKS_FROM_SIDE)) {
            if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                shell.bounceShell(level, hitResult);
            SmashableBrickBlock.smashBlock(level, pos, state, entity);
            didHit = true;
        }

        if (!didHit && entity instanceof LivingEntity livingEntity && state.is(TagRegistry.ABILITY_BLOCKS)
                && entity.getType().is(TagRegistry.CAN_HIT_ABILITY_BLOCKS_FROM_SIDE)) {
            AbilityBlock.hitAbilityBlockFromSide(level, pos, state, livingEntity);
            didHit = true;
        }

        if (!didHit && state.is(TagRegistry.BONKABLE_BLOCKS) && entity.getType().is(TagRegistry.CAN_BONK_BLOCKS_FROM_SIDE)) {
            if (entity instanceof KoopaShellEntity shell && entity.getData(DataAttachmentRegistry.HIT_BLOCK_COOLDOWN) == 0)
                shell.bounceShell(level, hitResult);
            StorageBrickBlock.bonkBlockFromSide(level, pos, state);
            didHit = true;
        }

        if (didHit)
            entity.getData(DataAttachmentRegistry.HIT_BLOCK_TIMESTAMPS).put(posKey, currentTick);
    }
}
