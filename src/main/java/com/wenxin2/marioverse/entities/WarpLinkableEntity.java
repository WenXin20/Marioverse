package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.utils.EntityWarpEntityHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public interface WarpLinkableEntity {
    Map<UUID, WarpTarget> WARP_LOCATIONS = new HashMap<>();
    Map<BlockPos, Entity> WARP_ENTITY_LOCATIONS = new HashMap<>();
    Map<Integer, Boolean> WARPED_ENTITIES = new HashMap<>();

    record WarpTarget(BlockPos pos, Direction direction, int width) {};

    boolean mv$isWaxed();

    void mv$setWaxed(boolean isWaxed);

    boolean mv$hasDestinationPos();

    void mv$setDestinationPos(@Nullable BlockPos pos);

    BlockPos mv$getDestinationPos();

    ResourceKey<Level> mv$getDestinationDim();

    void mv$setDestinationDim(@Nullable ResourceKey<Level> dimension);

    boolean mv$isBreakPainting();

    void mv$setBreakPainting(boolean breakPainting);

    boolean mv$getPreventWarp();

    void mv$setPreventWarp(boolean preventWarp);

    UUID mv$getWarpUUID();

    void mv$setWarpUuid(UUID uuid);

    Entity mv$getWarpEntity();

    void mv$setWarpEntity(Entity entity);

    static @Nullable WarpTarget getWarpPos(UUID uuid) {
        return WARP_LOCATIONS.get(uuid);
    }

    static void setWarpPos(UUID uuid, BlockPos pos, Direction direction, int width) {
        WARP_LOCATIONS.put(uuid, new WarpTarget(pos, direction, width));
    }

    static void markEntityTeleported(Entity entity) {
        if (entity != null)
            WARPED_ENTITIES.put(entity.getId(), true);
    }

    static void warp(Entity entity, double x, double y, double z, Level world) {
        Entity passengerEntity = entity.getControllingPassenger();

        if (entity instanceof EntityWarpEntityHandler handler && !handler.mv$doPreventWarp()) {
            if (entity instanceof Player player) {
                entity.teleportTo(x, y, z);
                if (ConfigRegistry.BLINDNESS_EFFECT.get())
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
            } else {
                entity.teleportTo(x, y, z);
                if (passengerEntity instanceof Player player) {
                    if (ConfigRegistry.BLINDNESS_EFFECT.get())
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                    entity.unRide();
                }
            }
        }

        markEntityTeleported(entity);
        world.gameEvent(GameEvent.TELEPORT, BlockPos.containing(x, y, z), GameEvent.Context.of(entity));
        world.playSound(null, BlockPos.containing(x, y, z), SoundRegistry.PAINTING_WARPS.get(), SoundSource.BLOCKS);
    }
}