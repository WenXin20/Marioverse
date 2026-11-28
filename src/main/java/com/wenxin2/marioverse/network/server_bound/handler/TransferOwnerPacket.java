package com.wenxin2.marioverse.network.server_bound.handler;

import com.mojang.authlib.GameProfile;
import com.wenxin2.marioverse.blocks.entities.WarpPipeBlockEntity;
import com.wenxin2.marioverse.inventory.WarpPipeMenu;
import com.wenxin2.marioverse.network.server_bound.data.TransferOwnerPayload;
import java.nio.file.Files;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TransferOwnerPacket {
    public static final TransferOwnerPacket INSTANCE = new TransferOwnerPacket();

    public static TransferOwnerPacket get() {
        return INSTANCE;
    }

    public void handle(final TransferOwnerPayload payload, IPayloadContext context) {
        if (context.flow().isServerbound()) {
            context.enqueueWork(() -> {
                ServerPlayer player = (ServerPlayer) context.player();
                if (player.containerMenu.containerId != payload.containerId() || !(player.containerMenu instanceof WarpPipeMenu menu))
                    return;

                menu.getAccess().execute((level, pos) -> {
                    if (!(level.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE) || !pipeBE.canManagePayments(player))
                        return;

                    MinecraftServer server = player.server;
                    ServerPlayer onlinePlayer = server.getPlayerList().getPlayerByName(payload.playerName());
                    if (onlinePlayer != null) {
                        TransferOwnerPacket.transferPipe(player, level, pos, onlinePlayer.getGameProfile());
                        return;
                    }

                    GameProfileCache profileCache = server.getProfileCache();
                    if (profileCache == null)
                        return;

                    profileCache.getAsync(payload.playerName()).thenAcceptAsync(profile -> {
                        if (profile.isEmpty() || !TransferOwnerPacket.hasJoined(server, profile.get())) {
                            player.displayClientMessage(Component.translatable("display.marioverse.transfer_pipe.never_joined",
                                    payload.playerName()).withStyle(ChatFormatting.RED), true);
                            return;
                        }

                        SkullBlockEntity.fetchGameProfile(profile.get().getId()).thenAcceptAsync(fullProfile ->
                                TransferOwnerPacket.transferPipe(player, level, pos, fullProfile.orElse(profile.get())), server);
                    }, server);
                });
            });
        }
    }

    private static boolean hasJoined(MinecraftServer server, GameProfile profile) {
        return server.getPlayerList().getPlayer(profile.getId()) != null
                || Files.exists(server.getWorldPath(LevelResource.PLAYER_DATA_DIR).resolve(profile.getId() + ".dat"));
    }

    private static void transferPipe(ServerPlayer player, Level level, BlockPos pos, GameProfile newOwner) {
        if (!(level.getBlockEntity(pos) instanceof WarpPipeBlockEntity pipeBE) || !pipeBE.canManagePayments(player))
            return;

        Component newOwnerName = Component.literal(newOwner.getName());
        if (pipeBE.isPipeOwner(newOwner.getId())) {
            player.displayClientMessage(Component.translatable("display.marioverse.transfer_pipe.already_owner",
                    newOwnerName).withStyle(ChatFormatting.RED), true);
            return;
        }

        pipeBE.setPipeOwner(newOwner);
        player.displayClientMessage(Component.translatable("display.marioverse.transfer_pipe.success",
                newOwnerName).withStyle(ChatFormatting.GREEN), true);

        ServerPlayer onlineOwner = player.server.getPlayerList().getPlayer(newOwner.getId());
        if (onlineOwner != null && onlineOwner != player)
            onlineOwner.displayClientMessage(Component.translatable("display.marioverse.transfer_pipe.received",
                    player.getDisplayName(), pos.getX(), pos.getY(), pos.getZ()).withStyle(ChatFormatting.GREEN), false);

        if (!pipeBE.canManagePayments(player) && player.containerMenu instanceof WarpPipeMenu)
            player.closeContainer();
    }
}