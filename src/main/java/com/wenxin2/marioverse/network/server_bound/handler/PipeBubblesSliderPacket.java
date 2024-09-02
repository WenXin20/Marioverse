package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesSliderPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PipeBubblesSliderPacket {
    public static final PipeBubblesSliderPacket INSTANCE = new PipeBubblesSliderPacket();

    public static PipeBubblesSliderPacket get() {
        return INSTANCE;
    }

    public void handle(final PipeBubblesSliderPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (payload.pos() == null)
                    return;
                ServerPlayer player = (ServerPlayer) context.player();
                Level world = player.level();
                BlockEntity blockEntity = world.getBlockEntity(payload.pos());
                if (blockEntity instanceof WarpPipeBlockEntity pipeBlockEntity) {
                    changeDistance(payload, player, (WarpPipeBlockEntity) blockEntity);
                    pipeBlockEntity.sendData();
                }
            });
        }
    }

    public void changeDistance(final PipeBubblesSliderPayload payload, ServerPlayer player, WarpPipeBlockEntity pipeBlockEntity) {
        Level world = pipeBlockEntity.getLevel();
        if (world == null)
            return;
        BlockPos pos = pipeBlockEntity.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!(state.getBlock() instanceof WarpPipeBlock))
            return;
        pipeBlockEntity.bubblesDistance(player, payload.bubblesDistance()); // Check this
    }
}
