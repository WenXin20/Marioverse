package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpDoorBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpTrapDoorBlockEntity;
import com.wenxin2.marioverse.event_handlers.TickEventHandlers;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

public interface BlockWarpEntityHandler {
    boolean mv$getBlockWarpTeleportConfig(Entity entity);

    private static boolean getShiftKeyForEntity(Entity entity) {
        return (!entity.isShiftKeyDown() && !(entity instanceof Player))
                || (entity.isShiftKeyDown() && entity instanceof Player);
    }

    default void enterWarp(Entity entity, Level world, BlockPos pos, BlockPos posEmbedded, BlockState state,
                           @Nullable Object object) {
        BlockState stateAboveEntity = state;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockEntity blockEntityAbove = world.getBlockEntity(pos);
        BlockPos warpPos;

        if (ModList.get().isLoaded("sable") && object instanceof SableProvider.SableContext context) {
            state = context.accessor.getBlockState(posEmbedded);
            stateAboveEntity = state;
            blockEntity = context.accessor.getBlockEntity(posEmbedded);
            blockEntityAbove = context.accessor.getBlockEntity(posEmbedded);
            if (world instanceof ServerLevel) {
                state = context.accessor.getServerBlockState(posEmbedded);
                stateAboveEntity = state;
                blockEntity = context.accessor.getServerBlockEntity(posEmbedded);
                blockEntityAbove = context.accessor.getServerBlockEntity(posEmbedded);
            }
        }

        if (blockEntity instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null) {
            warpPos = warpBE.destinationPos;
            int entityId = entity.getId();

            if (BaseWarpBlockEntity.WARPED_ENTITIES.getOrDefault(entityId, true))
                // Reset the teleport status for the entity
                BaseWarpBlockEntity.WARPED_ENTITIES.put(entityId, false);

            if (state.getBlock() instanceof DoorBlock || state.getBlock() instanceof TrapDoorBlock)
                this.enterWarpDoor(entity, world, posEmbedded, warpPos, warpBE);

            if (state.getBlock() instanceof WarpPipeBlock)
                this.enterWarpPipe(entity, world, posEmbedded, warpPos, warpBE, object);
        }

        if (blockEntityAbove instanceof BaseWarpBlockEntity warpBE && warpBE.getLevel() != null) {
            warpPos = warpBE.destinationPos;
            int entityId = entity.getId();

            if (BaseWarpBlockEntity.WARPED_ENTITIES.getOrDefault(entityId, true))
                BaseWarpBlockEntity.WARPED_ENTITIES.put(entityId, false);

            if (stateAboveEntity.getBlock() instanceof WarpPipeBlock)
                this.enterWarpPipeAbove(entity, world, pos, warpPos, warpBE, object);
        }
    }

    default void enterWarpDoor(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        BlockState state = world.getBlockState(pos);

        if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (this.mv$getBlockWarpTeleportConfig(entity) && !entity.getType().is(TagRegistry.CANNOT_WARP)) {
                if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0 && !entity.isShiftKeyDown())
                    this.warp(entity, world, pos, state, warpPos, warpBE);
                else if (warpBE.preventWarp && entity instanceof Player player)
                    this.displayWarpDisruptedMessage(player, state);
            }
        }
    }

    default void enterWarpPipe(Entity entity, Level world, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE,
                               @Nullable Object object) {
        BlockState state = world.getBlockState(pos);

        double entityX = entity.getX();
        double entityY = entity.getY();
        double entityZ = entity.getZ();
        int blockX = pos.getX();
        int blockY = pos.getY();
        int blockZ = pos.getZ();

        if (ModList.get().isLoaded("sable") && object instanceof SableProvider.SableContext context) {
            state = context.accessor.getBlockState(pos);
            if (world instanceof ServerLevel)
                state = context.accessor.getServerBlockState(pos);
            entityX = context.posLocal.x;
            entityY = context.posLocal.y;
            entityZ = context.posLocal.z;
            blockX = context.posWorld.getX();
            blockY = context.posWorld.getY();
            blockZ = context.posWorld.getZ();
        }

        if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (this.mv$getBlockWarpTeleportConfig(entity) && !entity.getType().is(TagRegistry.CANNOT_WARP) && state.hasProperty(WarpPipeBlock.FACING)) {
                if (state.getValue(WarpPipeBlock.FACING) == Direction.UP && getShiftKeyForEntity(entity) && (entityY + entity.getBbHeight() >= blockY - 1)
                        && (entityX < blockX + 1 && entityX > blockX) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player, state);
                    }
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.NORTH && !entity.isShiftKeyDown()
                        && (entity.verticalCollision || entity.onGround() || entity.isInWaterOrBubble()
                            || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                            || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ)) {
                    if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player, state);
                    }
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.SOUTH && !entity.isShiftKeyDown()
                        && (entity.verticalCollision || entity.onGround() || entity.isInWaterOrBubble()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                        || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX < blockX + 1 && entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ > blockZ + 0.25)) {
                    if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player, state);
                    }
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.EAST && !entity.isShiftKeyDown()
                        && (entity.verticalCollision || entity.onGround() || entity.isInWaterOrBubble()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                        || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX > blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player, state);
                    }
                }
                if (state.getValue(WarpPipeBlock.FACING) == Direction.WEST && !entity.isShiftKeyDown()
                        && (entity.verticalCollision || entity.onGround() || entity.isInWaterOrBubble()
                        || (entity instanceof LivingEntity livingEntity && livingEntity.isFallFlying())
                        || (entity instanceof Player player && player.getAbilities().flying))
                        && (entityX < blockX) && (entityY >= blockY && entityY < blockY + 0.75) && (entityZ < blockZ + 1 && entityZ > blockZ)) {
                    if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                        this.warp(entity, world, pos, state, warpPos, warpBE);
                    else if (entity instanceof Player player) {
                        if (warpBE.preventWarp)
                            this.displayWarpDisruptedMessage(player, state);
                        else if (warpBE.hasDestinationPos())
                            this.displayCooldownMessage(player, state);
                    }
                }
            }
        }
    }

    default void enterWarpPipeAbove(Entity entity, Level level, BlockPos pos, BlockPos warpPos, BaseWarpBlockEntity warpBE,
                                    @Nullable Object object) {
        Vec3 motion = entity.getDeltaMovement();
        AABB aboveBox = entity.getBoundingBox()
                .deflate(0.1, 0.0, 0.1)
                .expandTowards(0, motion.y + 0.2, 0);
        BlockPos min = BlockPos.containing(aboveBox.minX, aboveBox.minY, aboveBox.minZ);
        BlockPos max = BlockPos.containing(aboveBox.maxX, aboveBox.maxY, aboveBox.maxZ);

        for (BlockPos posAbove : BlockPos.betweenClosed(min, max)) {
            BlockState stateAbove = level.getBlockState(posAbove);

            if (ModList.get().isLoaded("sable") && object instanceof SableProvider.SableContext context) {
                BlockPos posEmbedded = context.posEmbedded.above(Math.round(entity.getBbHeight()))
                        .offset(posAbove.getX() - min.getX(), posAbove.getY() - max.getY(), posAbove.getZ() - min.getZ());
                stateAbove = context.accessor.getBlockState(posEmbedded);
                if (level instanceof ServerLevel) {
                    posEmbedded = context.posWorld.above(Math.round(entity.getBbHeight()))
                            .offset(posAbove.getX() - min.getX(), posAbove.getY() - max.getY(), posAbove.getZ() - min.getZ());
                    stateAbove = context.accessor.getServerBlockState(posEmbedded);
                }
            }

            if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
                if (this.mv$getBlockWarpTeleportConfig(entity) && !entity.getType().is(TagRegistry.CANNOT_WARP) && stateAbove.hasProperty(WarpPipeBlock.FACING)) {
                    if (stateAbove.getValue(WarpPipeBlock.FACING) == Direction.DOWN) {
                        if (!warpBE.preventWarp && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0)
                            this.warp(entity, level, pos, stateAbove, warpPos, warpBE);
                        else if (entity instanceof Player player) {
                            if (warpBE.preventWarp)
                                this.displayWarpDisruptedMessage(player, stateAbove);
                            else if (warpBE.hasDestinationPos())
                                this.displayCooldownMessage(player, stateAbove);
                        }
                    }
                }
            }
        }
    }

    default void warp(Entity entity, Level level, BlockPos pos, BlockState state, BlockPos warpPos, BaseWarpBlockEntity warpBE) {
        if (warpPos != null && !(level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity)
                && entity instanceof Player player)
            BlockWarpEntityHandler.displayDestinationMissingMessage(player);

        if (warpPos != null && level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity) {
            BlockState warpState = level.getBlockState(warpPos);

            if (warpState.getBlock() instanceof DoorBlock doorblock)
                WarpDoorBlockEntity.warp(entity, warpPos, level, warpState, doorblock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(entity, warpPos, level, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, level, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                level.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
            this.updateDoorState(level, pos, state, warpPos, warpState);
        } else if (warpBE.getUUID() != null && warpBE.getWarpUuid() != null
                && BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID()) != null) {
            warpPos = BaseWarpBlockEntity.findMatchingUUID(warpBE.getUUID());
            BlockState warpState = level.getBlockState(warpPos);

            if (warpState.getBlock() instanceof DoorBlock doorblock)
                WarpDoorBlockEntity.warp(entity, warpPos, level, warpState, doorblock, warpBE);
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                WarpTrapDoorBlockEntity.warp(entity, warpPos, level, warpState, trapdoorBlock, warpBE);
            if (warpState.getBlock() instanceof WarpPipeBlock)
                WarpPipeBlockEntity.warp(entity, warpPos, level, warpState);
            if (state.getBlock() instanceof WarpPipeBlock)
                level.playSound(null, pos, SoundRegistry.PIPE_WARPS.get(), SoundSource.BLOCKS);
            this.updateDoorState(level, pos, state, warpPos, warpState);

            warpBE.setDestinationPos(warpPos);
            if (level.getBlockEntity(warpPos) instanceof BaseWarpBlockEntity destBE)
                destBE.setDestinationPos(pos);
        }
    }

    private void updateDoorState(Level level, BlockPos pos, BlockState state, BlockPos warpPos, BlockState warpState) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        BlockEntity warpBE = level.getBlockEntity(warpPos);

        if (!level.isClientSide) {
            if (warpBE instanceof WarpDoorBlockEntity warpDoorBE && warpDoorBE.breakDoor)
                WarpDoorBlockEntity.breakDoor(warpPos, level);
            if (warpBE instanceof WarpTrapDoorBlockEntity warpTrapdoorBE && warpTrapdoorBE.breakTrapdoor)
                WarpTrapDoorBlockEntity.breakTrapdoor(warpPos, level);

            if (state.getBlock() instanceof DoorBlock)
                level.setBlock(pos, state.setValue(DoorBlock.OPEN, Boolean.FALSE)
                        .setValue(DoorBlock.FACING, state.getValue(DoorBlock.FACING)), 10);
            if (state.getBlock() instanceof TrapDoorBlock)
                level.setBlock(pos, state.setValue(TrapDoorBlock.OPEN, Boolean.FALSE)
                        .setValue(TrapDoorBlock.FACING, state.getValue(TrapDoorBlock.FACING)), 10);

            if (warpBE instanceof WarpDoorBlockEntity warpDoorBE && !warpDoorBE.breakDoor)
                level.setBlock(warpPos, warpState.setValue(DoorBlock.OPEN, Boolean.TRUE)
                        .setValue(DoorBlock.FACING, warpState.getValue(DoorBlock.FACING)), 10);
            if (warpBE instanceof WarpTrapDoorBlockEntity warpDoorBE && !warpDoorBE.breakTrapdoor)
                level.setBlock(warpPos, warpState.setValue(TrapDoorBlock.OPEN, Boolean.TRUE)
                        .setValue(TrapDoorBlock.FACING, warpState.getValue(TrapDoorBlock.FACING)), 10);
        }

        if (blockEntity instanceof BaseWarpBlockEntity warpDoorBE) {
            if (state.getBlock() instanceof DoorBlock doorBlock)
                warpDoorBE.playDoorSounds(null, level, pos, state.getValue(DoorBlock.OPEN), doorBlock.type());
            if (warpState.getBlock() instanceof DoorBlock doorBlock)
                warpDoorBE.playDoorSounds(null, level, warpPos, warpState.getValue(DoorBlock.OPEN), doorBlock.type());

            if (state.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                warpDoorBE.playDoorSounds(null, level, pos, state.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
            if (warpState.getBlock() instanceof TrapDoorBlock trapdoorBlock)
                warpDoorBE.playDoorSounds(null, level, warpPos, warpState.getValue(TrapDoorBlock.OPEN), trapdoorBlock.getType());
        }
    }

    default void displayCooldownMessage(Player player, BlockState state) {
        if (player.getData(DataAttachmentRegistry.WARP_COOLDOWN) >= 10) {
            if (state.getBlock() instanceof WarpPipeBlock) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        player.displayClientMessage(Component.translatable("display.marioverse.warp_pipe_cooldown.ticks",
                                player.getData(DataAttachmentRegistry.WARP_COOLDOWN)), true);
                    else player.displayClientMessage(Component.translatable("display.marioverse.warp_pipe_cooldown"), true);
                }
            } else if (state.getBlock() instanceof DoorBlock) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        player.displayClientMessage(Component.translatable("display.marioverse.warp_door_cooldown.ticks",
                                player.getData(DataAttachmentRegistry.WARP_COOLDOWN)), true);
                    else player.displayClientMessage(Component.translatable("display.marioverse.warp_door_cooldown"), true);
                }
            } else if (state.getBlock() instanceof TrapDoorBlock) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        player.displayClientMessage(Component.translatable("display.marioverse.warp_trapdoor_cooldown.ticks",
                                player.getData(DataAttachmentRegistry.WARP_COOLDOWN)), true);
                    else player.displayClientMessage(Component.translatable("display.marioverse.warp_trapdoor_cooldown"), true);
                }
            }
        }
    }

    default void displayWarpDisruptedMessage(Player player, BlockState state) {
        player.displayClientMessage(Component.translatable("display.marioverse.warp_disrupted",
                state.getBlock().getName()).withStyle(ChatFormatting.RED), true);
    }

    static void displayDestinationMissingMessage(Player player) {
        player.displayClientMessage(Component.translatable("display.marioverse.warp_destination_missing"), true);
    }
}