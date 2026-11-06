package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.blocks.entities.BaseWarpBlockEntity;
import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.integration.CompatRegistry;
import com.wenxin2.marioverse.integration.immersive_paintings_compat.PaintingEntityProvider;
import com.wenxin2.marioverse.integration.sable_compat.SableProvider;
import com.wenxin2.marioverse.integration.twilightforest_compat.MagicPaintingEntityProvider;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

public interface EntityWarpEntityHandler {
    double PAINTING_EXIT_DISTANCE = 0.6;

    boolean mv$getEntityWarpTeleportConfig();

    default void enterWarp(Entity entity, Level world) {
        if (entity.isSpectator())
            return;

        List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(0.1, 0, 0.1));
        for (Entity warpEntity : nearbyEntities) {
            if (warpEntity != entity && warpEntity.isAlive() && warpEntity instanceof WarpLinkableEntity linkableEntity
                    && linkableEntity.mv$getWarpUUID() != null
                    && !warpEntity.getData(DataAttachmentRegistry.PREVENT_WARP.get())) {
                int entityId = entity.getId();

                if (WarpLinkableEntity.WARPED_ENTITIES.getOrDefault(entityId, false))
                    WarpLinkableEntity.WARPED_ENTITIES.put(entityId, false);

                this.enterWarpPainting(entity, world, linkableEntity, warpEntity);
                break;
            }
        }
    }

    default void enterWarpPainting(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity, Entity warpEntity) {
        if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (this.mv$getEntityWarpTeleportConfig() && !entity.getType().is(TagRegistry.CANNOT_WARP)) {
                if (!warpEntity.getData(DataAttachmentRegistry.PREVENT_WARP.get()) && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0
                        && !entity.isShiftKeyDown()) {
                    this.warp(entity, world, warpLinkableEntity);
                    BlockWarpEntityHandler.AWAITING_RELEASE.put(entity, entity.level().getGameTime());
                } else if (entity instanceof Player player && !BlockWarpEntityHandler.isStillEnteringAfterWarp(entity)) {
                    if (warpEntity.getData(DataAttachmentRegistry.PREVENT_WARP.get()) && warpLinkableEntity instanceof Painting warpPainting)
                        this.displayWarpDisruptedMessage(player, warpPainting);
                    else if (warpLinkableEntity.mv$hasDestinationPos())
                        this.displayCooldownMessage(player, warpEntity);
                }
            }
        }
    }

    default void warp(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity) {

        if (world instanceof ServerLevel serverWorld && warpLinkableEntity.mv$getWarpUUID() != null) {
            UUID warpUUID = warpLinkableEntity.mv$getWarpUUID();
            Entity warpEntity = serverWorld.getEntity(warpLinkableEntity.mv$getWarpUUID());
            BlockPos xpDestination = null;

            if (warpEntity != null)
                xpDestination = warpEntity.blockPosition();
            else if (warpLinkableEntity.mv$getWarpTarget() != null)
                xpDestination = warpLinkableEntity.mv$getWarpTarget().pos();
            else if (WarpLinkableEntity.getWarpPos(warpUUID) != null)
                xpDestination = WarpLinkableEntity.getWarpPos(warpUUID).pos();
            else if (warpLinkableEntity.mv$hasDestinationPos())
                xpDestination = warpLinkableEntity.mv$getDestinationPos();

            Vec3 enteredPos = warpLinkableEntity instanceof Entity enteredWarp ? enteredWarp.position() : entity.position();
            boolean isWarpPainting = warpLinkableEntity instanceof Painting
                    || (warpLinkableEntity instanceof Entity enteredEntity && (enteredEntity.getType() == CompatRegistry.IMMERSIVE_GLOW_GRAFFITI.get()
                    || enteredEntity.getType() == CompatRegistry.IMMERSIVE_GLOW_PAINTING.get()
                    || enteredEntity.getType() == CompatRegistry.IMMERSIVE_GRAFFITI.get()
                    || enteredEntity.getType() == CompatRegistry.IMMERSIVE_PAINTING.get()
                    || enteredEntity.getType() == CompatRegistry.MAGIC_PAINTING.get()));
            WarpCost.Cost warpCost = WarpCost.getEntityCost(world, BlockPos.containing(enteredPos), xpDestination, isWarpPainting);
            Vec3 startPos = entity.position();

            if (!WarpCost.canAfford(entity, warpCost))
                return;

            if (warpEntity != null) {
                if (SableProvider.isLoaded())
                    SableProvider.recordEntityAnchor(warpEntity);

                boolean isImmersivePainting = warpEntity.getType() == CompatRegistry.IMMERSIVE_GLOW_GRAFFITI.get()
                        || warpEntity.getType() == CompatRegistry.IMMERSIVE_GLOW_PAINTING.get()
                        || warpEntity.getType() == CompatRegistry.IMMERSIVE_GRAFFITI.get()
                        || warpEntity.getType() == CompatRegistry.IMMERSIVE_PAINTING.get();
                boolean isMagicPainting = warpEntity.getType() == CompatRegistry.MAGIC_PAINTING.get();

                if (warpEntity instanceof Painting painting) {
                    int width = painting.getVariant().value().width();
                    Direction direction = warpEntity.getDirection();
                    BlockPos basePos = painting.getPos();

                    if (!EntityWarpEntityHandler.warpPaintingDirection(warpEntity, basePos, direction, width, entity, world))
                        return;
                    warpLinkableEntity.mv$setWarpTarget(new WarpLinkableEntity.WarpTarget(basePos, direction, width));
                    WarpLinkableEntity.updateWarpSubLevel(world, warpLinkableEntity, basePos);

                    if (painting.getData(DataAttachmentRegistry.BREAK.get()))
                        painting.discard();
                } else if (ModList.get().isLoaded("immersive_paintings")
                        && isImmersivePainting && warpEntity instanceof BlockAttachedEntity blockAttachedEntity) {
                    int width = PaintingEntityProvider.getPaintingWidth(warpEntity);
                    Direction direction = warpEntity.getDirection();
                    BlockPos basePos = blockAttachedEntity.getPos();

                    if (!EntityWarpEntityHandler.warpPaintingDirection(warpEntity, basePos, direction, width, entity, world))
                        return;
                    warpLinkableEntity.mv$setWarpTarget(new WarpLinkableEntity.WarpTarget(basePos, direction, width));
                    WarpLinkableEntity.updateWarpSubLevel(world, warpLinkableEntity, basePos);

                    if (warpEntity.getData(DataAttachmentRegistry.BREAK.get()))
                        warpEntity.discard();
                } else if (ModList.get().isLoaded("twilightforest")
                        && isMagicPainting && warpEntity instanceof BlockAttachedEntity blockAttachedEntity) {
                    int width = MagicPaintingEntityProvider.getPaintingWidth(warpEntity);
                    Direction direction = warpEntity.getDirection();
                    BlockPos basePos = blockAttachedEntity.getPos();

                    if (!EntityWarpEntityHandler.warpPaintingDirection(warpEntity, basePos, direction, width, entity, world))
                        return;
                    warpLinkableEntity.mv$setWarpTarget(new WarpLinkableEntity.WarpTarget(basePos, direction, width));
                    WarpLinkableEntity.updateWarpSubLevel(world, warpLinkableEntity, basePos);

                    if (warpEntity.getData(DataAttachmentRegistry.BREAK.get()))
                        warpEntity.discard();
                } else {
                    BlockPos warpPos = warpEntity.blockPosition();
                    Vec3 worldWarpPos = SableProvider.isLoaded()
                            ? SableProvider.toWorldVec(world, Vec3.atLowerCornerOf(warpPos)) : Vec3.atLowerCornerOf(warpPos);

                    if (worldWarpPos == null) {
                        if (entity instanceof Player player)
                            BlockWarpEntityHandler.displayDestinationMissingMessage(player);
                        return;
                    }

                    WarpLinkableEntity.warp(world, entity, worldWarpPos.x, worldWarpPos.y, worldWarpPos.z);
                    WarpLinkableEntity.updateWarpSubLevel(world, warpLinkableEntity, warpPos);
                    if (warpEntity.getData(DataAttachmentRegistry.BREAK.get()))
                        warpEntity.discard();
                }
            } else if (EntityWarpEntityHandler.getLinkedBlock(serverWorld, warpLinkableEntity, warpUUID) != null) {
                BlockWarpEntityHandler.warpIntoBlock(serverWorld, EntityWarpEntityHandler.getLinkedBlock(serverWorld, warpLinkableEntity, warpUUID), entity);
            } else {
                UUID warpSubLevel = warpLinkableEntity.mv$getWarpSubLevel();
                BlockPos warpSubLevelPos = warpLinkableEntity.mv$getWarpSubLevelPos();

                if (SableProvider.isLoaded() && (warpSubLevel == null || warpSubLevelPos == null)) {
                    SableProvider.SubLevelAnchor recordedAnchor = SableProvider.getEntityAnchor(world, warpUUID);

                    if (recordedAnchor != null) {
                        warpSubLevel = recordedAnchor.subLevel();
                        warpSubLevelPos = recordedAnchor.localPos();
                        warpLinkableEntity.mv$setWarpSubLevel(warpSubLevel, warpSubLevelPos);
                    }
                }
                BlockPos subLevelPos = SableProvider.isLoaded() && warpSubLevel != null && warpSubLevelPos != null
                        ? SableProvider.resolveSubLevelPos(world, warpSubLevel, warpSubLevelPos) : null;

                if (SableProvider.isLoaded() && warpSubLevel != null && warpSubLevelPos != null && subLevelPos == null) {
                    if (SableProvider.queueSubLevelWarp(serverWorld, warpSubLevel, warpSubLevelPos, entity,
                            loadedPos -> serverWorld.getEntity(warpUUID) != null,
                            () -> this.warp(entity, world, warpLinkableEntity))) {
                        if (entity instanceof Player player)
                            player.displayClientMessage(Component.translatable("display.marioverse.warp_destination_loading"), true);
                    } else if (entity instanceof Player player)
                        BlockWarpEntityHandler.displayDestinationMissingMessage(player);
                    return;
                }

                WarpLinkableEntity.WarpTarget savedTarget = warpLinkableEntity.mv$getWarpTarget();
                if (savedTarget == null)
                    savedTarget = WarpLinkableEntity.getWarpPos(warpUUID);

                if (savedTarget != null) {
                    BlockPos basePos = subLevelPos != null ? subLevelPos : savedTarget.pos();

                    if (serverWorld.areEntitiesLoaded(ChunkPos.asLong(basePos))) {
                        if (entity instanceof Player player)
                            BlockWarpEntityHandler.displayDestinationMissingMessage(player);
                        return;
                    }
                    Direction direction = savedTarget.direction();
                    int width = savedTarget.width();

                    if (!EntityWarpEntityHandler.warpPaintingDirection(null, basePos, direction, width, entity, world))
                        return;

                    List<Entity> entitiesAtPos = world.getEntities(null, new AABB(basePos));
                    for (Entity targetEntity : entitiesAtPos) {
                        if (targetEntity.getUUID().equals(warpUUID)
                                && targetEntity.getData(DataAttachmentRegistry.BREAK.get())) {
                            targetEntity.discard();
                            break;
                        }
                    }
                }
            }

            WarpCost.chargeIfWarped(entity, startPos, warpCost);

            if (!entity.position().equals(startPos)) {
                if (warpLinkableEntity instanceof Entity enteredWarp)
                    PaintingRipples.sendRipple(enteredWarp);
                PaintingRipples.sendRipple(serverWorld.getEntity(warpUUID));
            }
        }
    }

    private static boolean warpPaintingDirection(@Nullable Entity destination, BlockPos basePos, Direction direction, double width,
                                          Entity entity, Level world) {
        Vec3 paintingCenter;
        double footY;

        if (destination != null) {
            AABB bounds = destination.getBoundingBox();
            paintingCenter = bounds.getCenter();
            footY = bounds.minY;
        } else {
            Direction alongPainting = direction.getCounterClockWise();
            double offset = width % 2 == 0 ? 0.5 : 0.0;
            paintingCenter = Vec3.atCenterOf(basePos).add(direction.getStepX() * -0.46875, 0.0, direction.getStepZ() * -0.46875)
                    .add(alongPainting.getStepX() * offset, 0.0, alongPainting.getStepZ() * offset);
            footY = basePos.getY();
        }

        Vec3 center = new Vec3(paintingCenter.x + direction.getStepX() * PAINTING_EXIT_DISTANCE, footY,
                paintingCenter.z + direction.getStepZ() * PAINTING_EXIT_DISTANCE);
        Vec3 worldCenter = center;
        float yaw = direction.toYRot();

        if (SableProvider.isLoaded()) {
            worldCenter = SableProvider.toWorldVec(world, center);
            Vec3 worldFacing = SableProvider.toWorldVec(world, center.add(direction.getStepX(), 0.0, direction.getStepZ()));

            if (worldCenter == null || worldFacing == null) {
                if (entity instanceof Player player)
                    BlockWarpEntityHandler.displayDestinationMissingMessage(player);
                return false;
            }

            Vec3 facing = worldFacing.subtract(worldCenter);
            if (facing.horizontalDistanceSqr() > 1.0E-6)
                yaw = (float) (Mth.atan2(-facing.x, facing.z) * Mth.RAD_TO_DEG);
        }

        WarpLinkableEntity.warp(world, entity, worldCenter.x, worldCenter.y, worldCenter.z, yaw);
        return true;
    }

    private void displayCooldownMessage(Player player, Entity warpEntity) {
        if (player.getData(DataAttachmentRegistry.WARP_COOLDOWN) >= 10) {
            if (warpEntity instanceof Painting) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        player.displayClientMessage(Component.translatable("display.marioverse.warp_painting_cooldown.ticks",
                                player.getData(DataAttachmentRegistry.WARP_COOLDOWN)), true);
                    else player.displayClientMessage(Component.translatable("display.marioverse.warp_painting_cooldown"), true);
                }
            }
        }
    }

    default void displayWarpDisruptedMessage(Player player, Painting warpPainting) {
        if (warpPainting.getVariant().getKey() != null) {
            player.displayClientMessage(Component.translatable("display.marioverse.painting_warp_disrupted",
                    Component.translatable(warpPainting.getVariant().getKey().location().toLanguageKey("painting", "title")),
                    warpPainting.getName()).withStyle(ChatFormatting.RED), true);
        }
    }

    static boolean warpToEntity(Entity entity, Level world, Entity destination) {
        int width = EntityWarpEntityHandler.getPaintingWidth(destination);

        if (width > 0 && destination instanceof BlockAttachedEntity attached) {
            if (!EntityWarpEntityHandler.warpPaintingDirection(destination, attached.getPos(), destination.getDirection(), width, entity, world))
                return false;
        } else {
            Vec3 worldPos = SableProvider.isLoaded() ? SableProvider.toWorldVec(world, destination.position()) : destination.position();

            if (worldPos == null) {
                if (entity instanceof Player player)
                    BlockWarpEntityHandler.displayDestinationMissingMessage(player);
                return false;
            }
            WarpLinkableEntity.warp(world, entity, worldPos.x, worldPos.y, worldPos.z);
        }

        PaintingRipples.sendRipple(destination);
        if (destination.getData(DataAttachmentRegistry.BREAK.get()))
            destination.discard();
        return true;
    }

    static boolean warpToSavedTarget(Entity entity, Level world, BlockPos basePos, Direction direction, int width) {
        if (width > 0)
            return EntityWarpEntityHandler.warpPaintingDirection(null, basePos, direction, width, entity, world);

        Vec3 bottomCenter = Vec3.atBottomCenterOf(basePos);
        Vec3 worldPos = SableProvider.isLoaded() ? SableProvider.toWorldVec(world, bottomCenter) : bottomCenter;

        if (worldPos == null) {
            if (entity instanceof Player player)
                BlockWarpEntityHandler.displayDestinationMissingMessage(player);
            return false;
        }
        WarpLinkableEntity.warp(world, entity, worldPos.x, worldPos.y, worldPos.z);
        return true;
    }

    @Nullable
    private static BlockPos getLinkedBlock(ServerLevel level, WarpLinkableEntity warpLinkableEntity, UUID warpUUID) {
        BlockPos destination = warpLinkableEntity.mv$getDestinationPos();

        if (destination != null && level.getBlockEntity(destination) instanceof BaseWarpBlockEntity warpBE && warpUUID.equals(warpBE.getUUID()))
            return destination;
        return null;
    }

    static int getPaintingWidth(Entity entity) {
        if (entity instanceof Painting painting)
            return painting.getVariant().value().width();
        if (ModList.get().isLoaded("immersive_paintings") && (entity.getType() == CompatRegistry.IMMERSIVE_GLOW_GRAFFITI.get()
                || entity.getType() == CompatRegistry.IMMERSIVE_GLOW_PAINTING.get()
                || entity.getType() == CompatRegistry.IMMERSIVE_GRAFFITI.get()
                || entity.getType() == CompatRegistry.IMMERSIVE_PAINTING.get()))
            return PaintingEntityProvider.getPaintingWidth(entity);
        if (ModList.get().isLoaded("twilightforest") && entity.getType() == CompatRegistry.MAGIC_PAINTING.get())
            return MagicPaintingEntityProvider.getPaintingWidth(entity);
        return 0;
    }
}
