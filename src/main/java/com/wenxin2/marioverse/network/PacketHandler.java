package com.wenxin2.marioverse.network;

import com.wenxin2.marioverse.network.server_bound.data.ClosePipeButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.FireballShootPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.PipeBubblesSliderPayload;
import com.wenxin2.marioverse.network.server_bound.data.RenamePipePayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutButtonPayload;
import com.wenxin2.marioverse.network.server_bound.data.WaterSpoutSliderPayload;
import com.wenxin2.marioverse.network.server_bound.handler.ClosePipeButtonPacket;
import com.wenxin2.marioverse.network.server_bound.handler.FireballShootPacket;
import com.wenxin2.marioverse.network.server_bound.handler.PipeBubblesButtonPacket;
import com.wenxin2.marioverse.network.server_bound.handler.PipeBubblesSliderPacket;
import com.wenxin2.marioverse.network.server_bound.handler.RenamePipePacket;
import com.wenxin2.marioverse.network.server_bound.handler.WaterSpoutButtonPacket;
import com.wenxin2.marioverse.network.server_bound.handler.WaterSpoutSliderPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PacketHandler {

    @SubscribeEvent
    public static void registerPackets(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("marioverse").versioned("1.0.0");

        // Sends to server
        registrar.playToServer(ClosePipeButtonPayload.CLOSE_STATE_PAYLOAD, ClosePipeButtonPayload.STREAM_CODEC, ClosePipeButtonPacket.get()::handle);
        registrar.playToServer(PipeBubblesSliderPayload.BUBBLES_DISTANCE_PAYLOAD, PipeBubblesSliderPayload.STREAM_CODEC, PipeBubblesSliderPacket.get()::handle);
        registrar.playToServer(PipeBubblesButtonPayload.BUBBLES_STATE_PAYLOAD, PipeBubblesButtonPayload.STREAM_CODEC, PipeBubblesButtonPacket.get()::handle);
        registrar.playToServer(FireballShootPayload.FIREBALL_SHOOT_PAYLOAD, FireballShootPayload.STREAM_CODEC, FireballShootPacket.get()::handle);
        registrar.playToServer(RenamePipePayload.RENAME_PIPE_PAYLOAD, RenamePipePayload.STREAM_CODEC, RenamePipePacket.get()::handle);
        registrar.playToServer(WaterSpoutSliderPayload.SPOUT_HEIGHT_PAYLOAD, WaterSpoutSliderPayload.STREAM_CODEC, WaterSpoutSliderPacket.get()::handle);
        registrar.playToServer(WaterSpoutButtonPayload.SPOUT_STATE_PAYLOAD, WaterSpoutButtonPayload.STREAM_CODEC, WaterSpoutButtonPacket.get()::handle);
    }

    public static <MSG extends CustomPacketPayload> void sendToServer(MSG message) {
        PacketDistributor.sendToServer(message);
    }

    public static <MSG extends CustomPacketPayload> void sentToAllClients(MSG message) {
        PacketDistributor.sendToAllPlayers(message);
    }

    public static <MSG extends CustomPacketPayload> void sendToPlayer(MSG message, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, message);
    }
}
