package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public interface EntityWarpEntityHandler {
    boolean mv$getEntityWarpTeleportConfig();

    default void enterWarp(Entity entity, Level world) {
        List<Entity> nearbyEntities = world.getEntitiesOfClass(Entity.class, entity.getBoundingBox().inflate(0.1, 0, 0.1));
        for (Entity warpEntity : nearbyEntities) {
            if (warpEntity != entity && warpEntity.isAlive() && warpEntity instanceof WarpLinkableEntity linkableEntity
                    && !warpEntity.getData(DataAttachmentRegistry.PREVENT_WARP.get())) {
                int entityId = entity.getId();

                if (WarpLinkableEntity.WARPED_ENTITIES.getOrDefault(entityId, false))
                    WarpLinkableEntity.WARPED_ENTITIES.put(entityId, false);

                this.enterWarpPainting(entity, world, linkableEntity, warpEntity);
            }
            break;
        }
    }

    default void enterWarpPainting(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity, Entity warpEntity) {
        if (!entity.getData(DataAttachmentRegistry.PREVENT_WARP)) {
            if (this.mv$getEntityWarpTeleportConfig() && !entity.getType().is(TagRegistry.CANNOT_WARP)) {
                if (!warpEntity.getData(DataAttachmentRegistry.PREVENT_WARP.get()) && entity.getData(DataAttachmentRegistry.WARP_COOLDOWN) == 0
                        && !entity.isShiftKeyDown())
                    this.warp(entity, world, warpLinkableEntity);
                else if (entity instanceof Player player) {
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
            if (warpEntity != null) {
                if (warpEntity instanceof Painting painting) {
                    int width = painting.getVariant().value().width();
                    Direction direction = painting.getDirection();
                    BlockPos basePos = painting.getPos();

                    this.warpPaintingDirection(basePos, direction, width, entity, warpEntity, world);

                    if (painting.getData(DataAttachmentRegistry.BREAK.get()))
                        painting.discard();
                    entity.setXRot(direction.toYRot());
                    entity.setYRot(direction.toYRot());
                    entity.setYHeadRot(direction.toYRot());
                } else {
                    BlockPos warpPos = warpEntity.blockPosition();
                    WarpLinkableEntity.warp(entity, warpPos.getX(), warpPos.getY(), warpPos.getZ(), world);
                    if (warpEntity.getData(DataAttachmentRegistry.BREAK.get()))
                        warpEntity.discard();
                }
            } else {
                WarpLinkableEntity.WarpTarget savedTarget = WarpLinkableEntity.getWarpPos(warpUUID);

                if (savedTarget != null) {
                    BlockPos basePos = savedTarget.pos();
                    Direction direction = savedTarget.direction();
                    int width = savedTarget.width();

                    this.warpPaintingDirection(basePos, direction, width, entity, warpEntity, world);
                    entity.setXRot(direction.toYRot());
                    entity.setYRot(direction.toYRot());
                    entity.setYHeadRot(direction.toYRot());

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
        }
    }

    private void warpPaintingDirection(BlockPos basePos, Direction direction, double width, Entity entity, Entity warpEntity, Level world) {
        double centerX = basePos.getX();
        double centerY = basePos.getY();
        double centerZ = basePos.getZ();

        switch (direction) {
            case NORTH -> centerZ += 0.5;
            case SOUTH -> { centerX += width / 2; centerZ += 0.5; }
            case EAST  -> centerX += 0.5;
            case WEST  -> { centerZ += width / 2; centerX += 0.5; }
        }

        WarpLinkableEntity.warp(entity, centerX, centerY, centerZ, world);
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
}
