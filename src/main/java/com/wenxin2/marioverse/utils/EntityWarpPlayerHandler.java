package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface EntityWarpPlayerHandler extends EntityWarpEntityHandler {
    @Override
    default void enterWarpPainting(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity, Entity warpEntity) {
        if (entity instanceof Player player && (!this.mv$getEntityWarpTeleportConfig()
                || player.getType().is(TagRegistry.CANNOT_WARP) || this.mv$doPreventWarp())) {
            this.displayNoTeleportMessage(player, warpEntity);
        } else EntityWarpEntityHandler.super.enterWarpPainting(entity, world, warpLinkableEntity, warpEntity);
    }

    private void displayNoTeleportMessage(Player player, Entity warpEntity) {
        if (warpEntity instanceof Painting) {
            if (!this.mv$getEntityWarpTeleportConfig() || player.getType().is(TagRegistry.CANNOT_WARP))
                player.displayClientMessage(Component.translatable("display.marioverse.paintings_cannot_teleport_players"), true);
        }

        if (this.mv$doPreventWarp())
            player.displayClientMessage(Component.translatable("display.marioverse.warp_disruptor_prevented_warp"), true);
    }
}
