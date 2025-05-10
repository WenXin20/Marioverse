package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Unique;

public class EntityWarpEntityHandler {
    public static int getWarpCooldown(Entity entity) {
        return entity.getPersistentData().getInt("marioverse:warp_cooldown");
    }

    public static void setWarpCooldown(Entity entity, int cooldown) {
        entity.getPersistentData().putInt("marioverse:warp_cooldown", cooldown);
    }

    public static void enterWarp(Entity entity, Level world) {
        List<Painting> nearbyPaintings = world.getEntitiesOfClass(Painting.class, entity.getBoundingBox());
        for (Painting painting : nearbyPaintings) {
            if (painting instanceof WarpLinkableEntity linkableEntity && !linkableEntity.marioverse$getPreventWarp()) {
                int entityId = entity.getId();

                if (WarpLinkableEntity.WARPED_ENTITIES.getOrDefault(entityId, false))
                    WarpLinkableEntity.WARPED_ENTITIES.put(entityId, false);

                enterWarpPainting(entity, world, linkableEntity);
            }
            break;
        }
    }

    @Unique
    public static void enterWarpPainting(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity) {
        if (ConfigRegistry.TELEPORT_NON_MOBS.get() && !entity.getType().is(TagRegistry.CANNOT_WARP)
                && !entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            if (getWarpCooldown(entity) == 0 && !entity.isShiftKeyDown()) {
                warp(entity, world, warpLinkableEntity);
                setWarpCooldown(entity, ConfigRegistry.WARP_PAINTING_COOLDOWN.get());
            }
        }
    }

    @Unique
    public static void warp(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity) {
        if (world instanceof ServerLevel serverWorld && warpLinkableEntity.marioverse$getWarpUUID() != null) {
            UUID warpUUID = warpLinkableEntity.marioverse$getWarpUUID();
            Entity warpEntity = serverWorld.getEntity(warpLinkableEntity.marioverse$getWarpUUID());
            if (warpEntity != null) {
                if (warpEntity instanceof Painting painting) {
                    int width = painting.getVariant().value().width();
                    Direction direction = painting.getDirection();
                    BlockPos basePos = painting.getPos();

                    warpPaintingDirection(basePos, direction, width, entity, world);

                    if (painting instanceof WarpLinkableEntity warpPainting && warpPainting.marioverse$isBreakPainting())
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

                    warpPaintingDirection(basePos, direction, width, entity, world);
                    entity.setXRot(direction.toYRot());
                    entity.setYRot(direction.toYRot());
                    entity.setYHeadRot(direction.toYRot());

                    List<Entity> entitiesAtPos = world.getEntities(null, new AABB(basePos));
                    for (Entity targetEntity : entitiesAtPos) {
                        if (targetEntity.getUUID().equals(warpUUID) && targetEntity instanceof WarpLinkableEntity linkableEntity
                                && linkableEntity.marioverse$isBreakPainting()) {
                            targetEntity.kill();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Unique
    private static void warpPaintingDirection(BlockPos basePos, Direction direction, double width, Entity entity, Level world) {
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
}
