package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutSliderPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class WaterSpoutSliderPacket {
    public static final WaterSpoutSliderPacket INSTANCE = new WaterSpoutSliderPacket();

    public static WaterSpoutSliderPacket get() {
        return INSTANCE;
    }

    public void handle(final WaterSpoutSliderPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId == payload.containerId()
                        && player.containerMenu instanceof WarpPipeMenu menu) {
                    menu.getAccess().execute((level, pos) -> {
                        BlockEntity blockEntity = level.getBlockEntity(pos);

                        if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                            changeHeight(payload, player, (WarpPipeBlockEntity) blockEntity);
                            pipeBlockEntity.sendData();
                        }
                    });
                }
            });
        }
    }

    public void changeHeight(final WaterSpoutSliderPayload payload, ServerPlayer player, WarpPipeBlockEntity pipeBlockEntity) {
        Level world = pipeBlockEntity.getLevel();
        if (world == null)
            return;
        BlockPos pos = pipeBlockEntity.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof WarpPipeBlock))
            return;
        pipeBlockEntity.waterSpoutHeight(player, payload.waterSpoutHeight()); // Check this
    }
}
