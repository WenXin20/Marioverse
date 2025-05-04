package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
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

    boolean marioverse$isWaxed();

    void marioverse$setWaxed(boolean isWaxed);

    boolean marioverse$hasDestinationPos();

    void marioverse$setDestinationPos(@Nullable BlockPos pos);

    BlockPos marioverse$getDestinationPos();

    ResourceKey<Level> marioverse$getDestinationDim();

    void marioverse$setDestinationDim(@Nullable ResourceKey<Level> dimension);

    boolean marioverse$getPreventWarp();

    void marioverse$setPreventWarp(boolean preventWarp);

    UUID marioverse$getWarpUUID();

    void marioverse$setWarpUuid(UUID uuid);

    Entity marioverse$getWarpEntity();

    void marioverse$setWarpEntity(Entity entity);

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

//    static BlockPos findMatchingUUID(UUID uuid) {
//        return WARP_LOCATIONS.getOrDefault(uuid, null);
//    }

    static void warp(Entity entity, double x, double y, double z, Level world) {
        Entity passengerEntity = entity.getControllingPassenger();

        if (entity instanceof Player player && !player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            entity.teleportTo(x, y, z);
            if (ConfigRegistry.BLINDNESS_EFFECT.get())
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
        } else if (!entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            entity.teleportTo(x, y, z);
            if (passengerEntity instanceof Player player && !player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                if (ConfigRegistry.BLINDNESS_EFFECT.get())
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                entity.unRide();
            }
        }

        markEntityTeleported(entity);
        world.gameEvent(GameEvent.TELEPORT, BlockPos.containing(x, y, z), GameEvent.Context.of(entity));
        world.playSound(null, BlockPos.containing(x, y, z), SoundRegistry.PAINTING_WARPS.get(), SoundSource.BLOCKS);
    }
}