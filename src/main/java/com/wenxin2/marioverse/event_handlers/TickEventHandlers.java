package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DamageSourceRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID)
public class TickEventHandlers {
    @SubscribeEvent
    public static void preEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        Level level = entity.level();

        if (ConfigRegistry.ENABLE_STOMPABLE_ENEMIES.get()
                && (entity.getType().is(TagRegistry.CAN_STOMP_ENEMIES) || ConfigRegistry.ALL_MOBS_CAN_STOMP.get()
                || level.getGameRules().getBoolean(Marioverse.ALL_MOBS_CAN_STOMP))
                && (entity.onGround() || entity.isInWaterOrBubble())
                && entity.getData(DataAttachmentRegistry.CONSECUTIVE_BOUNCES) > 0
                && !entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR))
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

        if (entity.hasData(DataAttachmentRegistry.ONE_UPS_COOLDOWN) &&
                entity.getData(DataAttachmentRegistry.ONE_UPS_COOLDOWN) > 0)
            entity.setData(DataAttachmentRegistry.ONE_UPS_COOLDOWN, entity.getData(DataAttachmentRegistry.ONE_UPS_COOLDOWN) - 1);
    }

    @SubscribeEvent
    public static void postEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();
        Vec3 motion = entity.getDeltaMovement();
        int spinningTicks = entity.getPersistentData().getInt("marioverse:spinning_ticks");

        if (entity.isVehicle() && spinningTicks > 0) {
            entity.setYRot(entity.getYRot() + 30);
            entity.getPersistentData().putInt("marioverse:spinning_ticks", spinningTicks - 1);

            for (Entity rider : entity.getPassengers())
                rider.setYHeadRot(rider.getYHeadRot() + 30);
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

            level.destroyBlock(pos, true, entity);

            if (isJumping) {
                BlockPos posAbove = pos.above();
                BlockState stateAbove = level.getBlockState(posAbove);

                if (!stateAbove.isAir() && stateAbove.is(TagRegistry.MEGA_MUSHROOM_CAN_BREAK)
                        && !stateAbove.getCollisionShape(level, posAbove).isEmpty())
                    level.destroyBlock(posAbove, true, entity);
            } else if (isFalling) {
                BlockPos posBelow = pos.below();
                BlockState stateBelow = level.getBlockState(posBelow);

                if (!stateBelow.isAir() && stateBelow.is(TagRegistry.MEGA_MUSHROOM_CAN_BREAK_WHEN_FALLING))
                    level.destroyBlock(posBelow, true, entity);
            }
        }
    }
}
