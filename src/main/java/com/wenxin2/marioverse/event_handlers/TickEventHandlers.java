package com.wenxin2.marioverse.event_handlers;

import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
        if (!(entity instanceof Player)
                && !entity.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING))
            return;

        if (!entity.level().isClientSide && !entity.isSpectator()
                && entity.getData(DataAttachmentRegistry.HAS_MEGA_MUSHROOM)
                && entity.getType().is(TagRegistry.CAN_BREAK_BLOCKS_AS_MEGA)
                && ConfigRegistry.MEGA_MUSHROOM_BREAKS_BLOCKS.get()) {
            TickEventHandlers.breakBlocks(entity);
        }
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

            if (state.isAir() || !state.is(TagRegistry.MEGA_MUSHROOM_CAN_BREAK))
                continue;

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

                if (!stateBelow.isAir() && stateBelow.is(TagRegistry.MEGA_MUSHROOM_CAN_BREAK))
                    level.destroyBlock(posBelow, true, entity);
            }
        }
    }
}
