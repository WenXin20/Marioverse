package com.wenxin2.marioverse.utils;

import com.wenxin2.marioverse.entities.WarpLinkableEntity;
import com.wenxin2.marioverse.integration.fast_paintings_compat.PaintingBlockWarp;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.TagRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

public interface EntityWarpPlayerHandler extends EntityWarpEntityHandler {
    @Override
    default void enterWarpPainting(Entity entity, Level world, WarpLinkableEntity warpLinkableEntity, IAttachmentHolder warpHolder) {
        if (entity instanceof Player player && (!this.mv$getEntityWarpTeleportConfig()
                || player.getType().is(TagRegistry.CANNOT_WARP) || entity.getData(DataAttachmentRegistry.PREVENT_WARP))) {
            this.displayNoTeleportMessage(player, warpLinkableEntity);
        } else EntityWarpEntityHandler.super.enterWarpPainting(entity, world, warpLinkableEntity, warpHolder);
    }

    private void displayNoTeleportMessage(Player player, WarpLinkableEntity warpLinkableEntity) {
        if (BlockWarpEntityHandler.isStillEnteringAfterWarp(player))
            return;

        if (warpLinkableEntity instanceof Painting || warpLinkableEntity instanceof PaintingBlockWarp) {
            if (!this.mv$getEntityWarpTeleportConfig() || player.getType().is(TagRegistry.CANNOT_WARP))
                player.displayClientMessage(Component.translatable("display.marioverse.paintings_cannot_teleport_players"), true);
        }

        if (player.getData(DataAttachmentRegistry.PREVENT_WARP))
            player.displayClientMessage(Component.translatable("display.marioverse.warp_disrupted_player"), true);
    }
}
