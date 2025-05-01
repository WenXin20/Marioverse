package com.wenxin2.marioverse.entities;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public interface WarpLinkableEntity {
    public static final Map<Integer, Boolean> WARPED_ENTITIES = new HashMap<>();

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

    static void markEntityTeleported(Entity entity) {
        if (entity != null)
            WARPED_ENTITIES.put(entity.getId(), true);
    }

    static void warp(Entity entity, BlockPos warpPos, Level world) {
        Entity passengerEntity = entity.getControllingPassenger();

        if (entity instanceof Player player && !player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
            if (ConfigRegistry.BLINDNESS_EFFECT.get())
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
        } else if (!entity.getPersistentData().getBoolean("marioverse:prevent_warp")) {
            entity.teleportTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5);
            if (passengerEntity instanceof Player player && !player.getPersistentData().getBoolean("marioverse:prevent_warp")) {
                if (ConfigRegistry.BLINDNESS_EFFECT.get())
                    player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, true, false));
                entity.unRide();
            }
        }

        markEntityTeleported(entity);
        world.gameEvent(GameEvent.TELEPORT, warpPos, GameEvent.Context.of(entity));
    }
}