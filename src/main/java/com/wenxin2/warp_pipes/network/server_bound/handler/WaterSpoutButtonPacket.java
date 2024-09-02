package com.wenxin2.warp_pipes.network.server_bound.handler;

import com.wenxin2.warp_pipes.blocks.WarpPipeBlock;
import com.wenxin2.warp_pipes.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.warp_pipes.network.server_bound.data.WaterSpoutButtonPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class WaterSpoutButtonPacket {
    public static final WaterSpoutButtonPacket INSTANCE = new WaterSpoutButtonPacket();

    public static WaterSpoutButtonPacket get() {
        return INSTANCE;
    }

    public void handle(final WaterSpoutButtonPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (payload.pos() == null)
                    return;
                ServerPlayer player = (ServerPlayer) context.player();
                Level world = player.level();
                BlockEntity blockEntity = world.getBlockEntity(payload.pos());
                if (blockEntity instanceof WarpPipeBlockEntity) {
                    changeState(player, (WarpPipeBlockEntity) blockEntity);
                    ((WarpPipeBlockEntity) blockEntity).sendData();
                    blockEntity.setChanged();
                }
            });
        }
    }

    public void changeState(ServerPlayer player, WarpPipeBlockEntity pipeBlockEntity) {
        Level world = pipeBlockEntity.getLevel();
        if (world == null)
            return;
        BlockPos pos = pipeBlockEntity.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof WarpPipeBlock))
            return;

        pipeBlockEntity.toggleWaterSpout(player);
    }

    public static WaterSpoutButtonPacket waterSpoutOn(BlockPos pos, Boolean hasWaterSpout) {
        WaterSpoutButtonPacket packet = new WaterSpoutButtonPacket();
        hasWaterSpout = false;
        return packet;
    }

    public static WaterSpoutButtonPacket waterSpoutOff(BlockPos pos, Boolean hasWaterSpout) {
        WaterSpoutButtonPacket packet = new WaterSpoutButtonPacket();
        hasWaterSpout = true;
        return packet;
    }
}
