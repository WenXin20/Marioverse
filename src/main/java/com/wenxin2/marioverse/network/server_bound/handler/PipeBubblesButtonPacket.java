package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.blocks.WarpPipeBlock;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesButtonPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PipeBubblesButtonPacket {

    public static final PipeBubblesButtonPacket INSTANCE = new PipeBubblesButtonPacket();

    public static PipeBubblesButtonPacket get() {
        return INSTANCE;
    }

    public void handle(final PipeBubblesButtonPayload payload, IPayloadContext context) {
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

        pipeBlockEntity.togglePipeBubbles(player);
    }

    public static PipeBubblesButtonPacket pipeBubblesOn(BlockPos pos, Boolean hasPipeBubbles) {
        PipeBubblesButtonPacket packet = new PipeBubblesButtonPacket();
        hasPipeBubbles = false;
        return packet;
    }

    public static PipeBubblesButtonPacket pipeBubblesOff(BlockPos pos, Boolean hasPipeBubbles) {
        PipeBubblesButtonPacket packet = new PipeBubblesButtonPacket();
        hasPipeBubbles = true;
        return packet;
    }
}
