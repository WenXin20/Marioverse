package com.wenxin2.warp_pipes.event_handlers;

import com.wenxin2.warp_pipes.WarpPipes;
import com.wenxin2.warp_pipes.blocks.client.WarpPipeScreen;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = WarpPipes.MODID)
public class WarpEventHandlers {

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinLevelEvent event)
    {
        CompoundTag tag = event.getEntity().getPersistentData();
        if (!(event.getEntity() instanceof LivingEntity) && !(event.getEntity() instanceof Player)) return;

        if (event.getEntity() != null && tag.contains("warp_pipes:can_warp") && tag.getBoolean("warp_pipes:can_warp") == Boolean.FALSE)
            tag.putBoolean("warp_pipes:prevent_warp", true);

        if (event.getEntity() != null && !tag.contains("warp_pipes:prevent_warp"))
            tag.putBoolean("warp_pipes:prevent_warp", false);
    }

    @SubscribeEvent
    public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            BlockPos clickedPos = event.getPos();
            BlockEntity blockEntity = event.getLevel().getBlockEntity(clickedPos);
            if (blockEntity instanceof WarpPipeBlockEntity) {
                // Update the last clicked position
                WarpPipeScreen.lastClickedPos = clickedPos;
            }
        }
    }
}
