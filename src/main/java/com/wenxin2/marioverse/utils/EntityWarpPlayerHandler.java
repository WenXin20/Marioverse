package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public interface EntityWarpPlayerHandler extends EntityWarpEntityHandler {
    @NotNull
    @Override
    default Boolean getEntityWarpTeleportConfig() {
        return ConfigRegistry.TELEPORT_PLAYERS.get();
    }

    @Override
    default void enterWarpPainting(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity, Entity warpEntity) {
        if (entity instanceof Player player && (!getEntityWarpTeleportConfig() || player.getType().is(TagRegistry.CANNOT_WARP)
                || player.getPersistentData().getBoolean("marioverse:prevent_warp"))) {
            this.displayNoTeleportMessage(player);
        } else EntityWarpEntityHandler.super.enterWarpPainting(entity, world, warpLinkableEntity, warpEntity);
    }

    private void displayNoTeleportMessage(Player player) {
        if (!getEntityWarpTeleportConfig() || player.getType().is(TagRegistry.CANNOT_WARP))
            player.displayClientMessage(Component.translatable("display.marioverse.players_cannot_teleport"), true);
    }
}
