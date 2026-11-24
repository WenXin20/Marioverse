package com.wenxin2.marioverse.network.server_bound.handler;

import com.wenxin2.marioverse.entities.HoldableEntity;
import com.wenxin2.marioverse.network.server_bound.data.ThrowHeldShellPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ThrowHeldShellPacket {
    public static final ThrowHeldShellPacket INSTANCE = new ThrowHeldShellPacket();

    public static ThrowHeldShellPacket get() {
        return INSTANCE;
    }

    public void handle(final ThrowHeldShellPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                if (!(context.player() instanceof ServerPlayer player))
                    return;

                HoldableEntity shell = HoldableEntity.getHeldEntity(player);

                if (shell == null || shell.getHoldTicks() < HoldableEntity.THROW_DELAY_TICKS)
                    return;

                if (payload.place()) {
                    if (!player.canInteractWithBlock(payload.placePos(), 1.0)
                            || !shell.placeAt(player, payload.placePos()))
                        return;
                } else shell.throwFrom(player);

                player.swing(InteractionHand.MAIN_HAND, true);
            });
        }
    }
}
