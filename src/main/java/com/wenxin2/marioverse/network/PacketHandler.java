package com.wenxin2.marioverse.network;

import com.wenxin2.marioverse.network.client_bound.data.AmericaNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.DisguiseStatePayload;
import com.wenxin2.marioverse.network.client_bound.data.OneUpPayload;
import com.wenxin2.marioverse.network.client_bound.data.WonderNamePayload;
import com.wenxin2.marioverse.network.client_bound.data.SwingHandPayload;
import com.wenxin2.marioverse.network.client_bound.handler.AmericaNamePacket;
import com.wenxin2.marioverse.network.client_bound.handler.DisguiseStatePacket;
import com.wenxin2.marioverse.network.client_bound.handler.OneUpPacket;
import com.wenxin2.marioverse.network.client_bound.handler.WonderNamePacket;
import com.wenxin2.marioverse.network.client_bound.handler.SwingHandPacket;
import com.wenxin2.marioverse.network.server_bound.data.BlockFacePayload;
import com.wenxin2.marioverse.network.server_bound.data.ClosePipeButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import com.wenxin2.marioverse.network.server_bound.data.BouncePayload;
import com.wenxin2.marioverse.network.server_bound.data.IceBallShootPayload;
import com.wenxin2.marioverse.network.server_bound.data.IsInteractablePayload;
import com.wenxin2.marioverse.network.server_bound.data.IsUnbreakablePayload;
import com.wenxin2.marioverse.network.server_bound.data.MenuTypePayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.PiranhaPlantHidePayload;
import com.wenxin2.marioverse.network.server_bound.data.PlacementDirectionPayload;
import com.wenxin2.marioverse.network.server_bound.data.PlacementOffsetPayload;
import com.wenxin2.marioverse.network.server_bound.data.RefillCountdownPayload;
import com.wenxin2.marioverse.network.server_bound.data.RenamePipePayload;
import com.wenxin2.marioverse.network.server_bound.data.SquashEntityPayload;
import com.wenxin2.marioverse.network.server_bound.data.TimeUnitPayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutSliderPayload;
import com.wenxin2.marioverse.network.server_bound.handler.BlockFacePacket;
import com.wenxin2.marioverse.network.server_bound.handler.ClosePipeButtonPacket;
import com.wenxin2.marioverse.network.server_bound.handler.FireballShootPacket;
import com.wenxin2.marioverse.network.server_bound.handler.BouncePacket;
import com.wenxin2.marioverse.network.server_bound.handler.IceBallShootPacket;
import com.wenxin2.marioverse.network.server_bound.handler.IsInteractablePacket;
import com.wenxin2.marioverse.network.server_bound.handler.IsUnbreakablePacket;
import com.wenxin2.marioverse.network.server_bound.handler.MenuTypePacket;
import com.wenxin2.marioverse.network.server_bound.handler.PipeBubblesButtonPacket;
import com.wenxin2.marioverse.network.server_bound.handler.PipeBubblesSliderPacket;
import com.wenxin2.marioverse.network.server_bound.handler.PiranhaPlantHidePacket;
import com.wenxin2.marioverse.network.server_bound.handler.PlacementDirectionPacket;
import com.wenxin2.marioverse.network.server_bound.handler.PlacementOffsetPacket;
import com.wenxin2.marioverse.network.server_bound.handler.RefillCountdownPacket;
import com.wenxin2.marioverse.network.server_bound.handler.RenamePipePacket;
import com.wenxin2.marioverse.network.server_bound.handler.SquashEntityPacket;
import com.wenxin2.marioverse.network.server_bound.handler.TimeUnitPacket;
import com.wenxin2.marioverse.network.server_bound.handler.WaterSpoutButtonPacket;
import com.wenxin2.marioverse.network.server_bound.handler.WaterSpoutSliderPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class PacketHandler {

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("marioverse").versioned("1.0.0");

        // Sends to client
        registrar.playToClient(AmericaNamePayload.PAYLOAD, AmericaNamePayload.STREAM_CODEC, AmericaNamePacket.get()::handle);
        registrar.playToClient(DisguiseStatePayload.PAYLOAD, DisguiseStatePayload.STREAM_CODEC, DisguiseStatePacket.get()::handle);
        registrar.playToClient(OneUpPayload.PAYLOAD, OneUpPayload.STREAM_CODEC, OneUpPacket.get()::handle);
        registrar.playToClient(SwingHandPayload.PAYLOAD, SwingHandPayload.STREAM_CODEC, SwingHandPacket.get()::handle);
        registrar.playToClient(WonderNamePayload.PAYLOAD, WonderNamePayload.STREAM_CODEC, WonderNamePacket.get()::handle);

        // Sends to server
        registrar.playToServer(BlockFacePayload.PAYLOAD, BlockFacePayload.STREAM_CODEC, BlockFacePacket.get()::handle);
        registrar.playToServer(BouncePayload.PAYLOAD, BouncePayload.STREAM_CODEC, BouncePacket.get()::handle);
        registrar.playToServer(ClosePipeButtonPayload.PAYLOAD, ClosePipeButtonPayload.STREAM_CODEC, ClosePipeButtonPacket.get()::handle);
        registrar.playToServer(FireballShootPayload.PAYLOAD, FireballShootPayload.STREAM_CODEC, FireballShootPacket.get()::handle);
        registrar.playToServer(IceBallShootPayload.PAYLOAD, IceBallShootPayload.STREAM_CODEC, IceBallShootPacket.get()::handle);
        registrar.playToServer(IsInteractablePayload.PAYLOAD, IsInteractablePayload.STREAM_CODEC, IsInteractablePacket.get()::handle);
        registrar.playToServer(IsUnbreakablePayload.PAYLOAD, IsUnbreakablePayload.STREAM_CODEC, IsUnbreakablePacket.get()::handle);
        registrar.playToServer(MenuTypePayload.PAYLOAD, MenuTypePayload.STREAM_CODEC, MenuTypePacket.get()::handle);
        registrar.playToServer(PipeBubblesButtonPayload.PAYLOAD, PipeBubblesButtonPayload.STREAM_CODEC, PipeBubblesButtonPacket.get()::handle);
        registrar.playToServer(PipeBubblesSliderPayload.PAYLOAD, PipeBubblesSliderPayload.STREAM_CODEC, PipeBubblesSliderPacket.get()::handle);
        registrar.playToServer(PiranhaPlantHidePayload.PAYLOAD, PiranhaPlantHidePayload.STREAM_CODEC, PiranhaPlantHidePacket.get()::handle);
        registrar.playToServer(PlacementDirectionPayload.PAYLOAD, PlacementDirectionPayload.STREAM_CODEC, PlacementDirectionPacket.get()::handle);
        registrar.playToServer(PlacementOffsetPayload.PAYLOAD, PlacementOffsetPayload.STREAM_CODEC, PlacementOffsetPacket.get()::handle);
        registrar.playToServer(RefillCountdownPayload.PAYLOAD, RefillCountdownPayload.STREAM_CODEC, RefillCountdownPacket.get()::handle);
        registrar.playToServer(RenamePipePayload.PAYLOAD, RenamePipePayload.STREAM_CODEC, RenamePipePacket.get()::handle);
        registrar.playToServer(SquashEntityPayload.PAYLOAD, SquashEntityPayload.STREAM_CODEC, SquashEntityPacket.get()::handle);
        registrar.playToServer(TimeUnitPayload.PAYLOAD, TimeUnitPayload.STREAM_CODEC, TimeUnitPacket.get()::handle);
        registrar.playToServer(WaterSpoutButtonPayload.PAYLOAD, WaterSpoutButtonPayload.STREAM_CODEC, WaterSpoutButtonPacket.get()::handle);
        registrar.playToServer(WaterSpoutSliderPayload.PAYLOAD, WaterSpoutSliderPayload.STREAM_CODEC, WaterSpoutSliderPacket.get()::handle);
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }
}
