package com.wenxin2.marioverse.mixin;

import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.utils.BlockWarpPlayerHandler;
import com.wenxin2.marioverse.utils.EntityWarpPlayerHandler;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Player.class)
public abstract class PlayerMixin extends Entity implements BlockWarpPlayerHandler, EntityWarpPlayerHandler {
    @Shadow protected abstract float getBlockSpeedFactor();

    public PlayerMixin(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public boolean marioverse$getBlockWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    public boolean marioverse$getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    public void baseTick() {
        super.baseTick();

        int preventWarpCooldown = this.mv$getPreventWarpCooldown();
        if (preventWarpCooldown > 0)
            this.mv$setPreventWarpCooldown(this.mv$getPreventWarpCooldown() - 1);

        if (preventWarpCooldown == 0 && this.mv$doPreventWarp())
            this.mv$setPreventWarp(false);
    }
}
