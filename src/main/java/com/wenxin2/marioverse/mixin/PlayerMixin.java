package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.EntityWarpPlayerHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements BlockWarpPlayerHandler, EntityWarpPlayerHandler {
    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean mv$getBlockWarpTeleportConfig(Entity entity) {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    public boolean mv$getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }
}
