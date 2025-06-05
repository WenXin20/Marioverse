package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.UUID;
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

    boolean mv$doPreventWarp();
    void mv$setPreventWarp(boolean preventWarp);

    int mv$getPreventWarpCooldown();
    void mv$setPreventWarpCooldown(int preventWarpCooldown);

    int mv$getWarpCooldown();
    void mv$setWarpCooldown(int warpCooldown);

    default void enterWarp(Entity entity, Level world) {
        List<Painting> nearbyPaintings = world.getEntitiesOfClass(Painting.class, entity.getBoundingBox());
        for (Painting painting : nearbyPaintings) {
            if (painting instanceof WarpLinkableEntity linkableEntity && !linkableEntity.mv$getPreventWarp()) {
                int entityId = entity.getId();

                if (WarpLinkableEntity.WARPED_ENTITIES.getOrDefault(entityId, false))
                    WarpLinkableEntity.WARPED_ENTITIES.put(entityId, false);

                this.enterWarpPainting(entity, world, linkableEntity, painting);
            }
            break;
        }
    }

    default void enterWarpPainting(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity, Entity warpEntity) {
        if (!this.mv$doPreventWarp()) {
            if (this.mv$getEntityWarpTeleportConfig() && !entity.getType().is(TagRegistry.CANNOT_WARP)) {
                if (this.mv$getWarpCooldown() == 0 && !entity.isShiftKeyDown()) {
                    this.warp(entity, world, warpLinkableEntity);
                } else if (entity instanceof Player player && warpLinkableEntity.mv$hasDestinationPos())
                    this.displayCooldownMessage(player, warpEntity);
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

                    if (painting instanceof WarpLinkableEntity warpPainting && warpPainting.mv$isBreakPainting())
                        painting.kill();
                    entity.setXRot(direction.toYRot());
                    entity.setYRot(direction.toYRot());
                    entity.setYHeadRot(direction.toYRot());
                } else {
                    BlockPos warpPos = warpEntity.blockPosition();
                    WarpLinkableEntity.warp(entity, warpPos.getX(), warpPos.getY(), warpPos.getZ(), world);
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
                        if (targetEntity.getUUID().equals(warpUUID) && targetEntity instanceof WarpLinkableEntity linkableEntity
                                && linkableEntity.mv$isBreakPainting()) {
                            targetEntity.kill();
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
        if (this.mv$getWarpCooldown() >= 10) {
            if (warpEntity instanceof Painting) {
                if (ConfigRegistry.WARP_COOLDOWN_MESSAGE.get()) {
                    if (ConfigRegistry.WARP_COOLDOWN_MESSAGE_TICKS.get())
                        player.displayClientMessage(Component.translatable("display.marioverse.warp_painting_cooldown.ticks",
                                this.mv$getWarpCooldown()), true);
                    else player.displayClientMessage(Component.translatable("display.marioverse.warp_painting_cooldown"), true);
                }
            }
        }
    }

    default void displayEntityDestinationMissingMessage(Player player) {
        player.displayClientMessage(Component.translatable("display.marioverse.warp_destination_missing"), true);
    }
}
