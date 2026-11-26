package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.entities.BlockSpawnerBlockEntity;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.BlockSpawnerMenu;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.MenuTypePayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MenuTypePacket {
    public static final MenuTypePacket INSTANCE = new MenuTypePacket();

    public static MenuTypePacket get() {
        return INSTANCE;
    }

    public void handle(final MenuTypePayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();

                if (player.containerMenu.containerId != payload.containerId())
                    return;

                if (player.containerMenu instanceof BlockSpawnerMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);
                        if (blockEntity instanceof BlockSpawnerBlockEntity spawnerBE) {
                            spawnerBE.setMenuType(payload.menuTypeID());
                            spawnerBE.setChanged();
                        }
                    });
                } else if (player.containerMenu instanceof WarpPipeMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        if (level.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE) {
                            int menuType = Mth.clamp(payload.menuTypeID(), WarpPipeMenu.WARP_PIPE_TAB, WarpPipeMenu.PAYMENTS_TAB);

                            if (menuType != WarpPipeMenu.PAYMENTS_TAB || pipeBE.canManagePayments(player))
                                pipeBE.setMenuType(menuType);
                        }
                    });
                }
            });
        }
    }
}