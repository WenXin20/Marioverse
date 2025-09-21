package com.wenxin2.marioverse.client.sounds;

import com.wenxin2.marioverse.network.client_bound.handler.SuperStarThemePacket;
import com.wenxin2.marioverse.network.server_bound.data.SuperStarThemePayload;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.sounds.FadeInAndOutSoundInstance;
import com.wenxin2.marioverse.sounds.FadingSoundInstance;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PlayClientSound {
    public static final Map<UUID, FadeInAndOutSoundInstance> ACTIVE_PIPE_SOUNDS = new HashMap<>();

    private static void withLevelDo(Consumer<Level> action) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null)
            action.accept(level);
    }

    public static void playSuperStarSound(SuperStarThemePayload payload, int fadeDuration) {
        withLevelDo((l) -> {
            Entity entity = l.getEntity(payload.entityID());
            if (entity instanceof LivingEntity livingEntity && entity instanceof AbilitiesHandler handler)
                Minecraft.getInstance().getSoundManager().play(new FadingSoundInstance(livingEntity, SoundRegistry.SUPER_STAR_THEME.get(),
                        SoundSource.AMBIENT, entity.getRandom(), handler.mv$getSuperStarCooldown(), fadeDuration));
        });
    }

    public static void playClearPipeSound(Entity entity, int fadeInDuration, int fadeOutDuration, boolean inClearPipe) {
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockState state = world.getBlockState(pos);
        UUID uuid = entity.getUUID();

        FadeInAndOutSoundInstance soundInstance = ACTIVE_PIPE_SOUNDS.get(uuid);

        if (inClearPipe) {
            if (soundInstance == null) {
                FadeInAndOutSoundInstance insideSound = new FadeInAndOutSoundInstance(entity, SoundRegistry.CLEAR_PIPE_INSIDE.get(),
                        SoundSource.BLOCKS, fadeInDuration, fadeOutDuration);

                Minecraft.getInstance().getSoundManager().play(insideSound);
                ACTIVE_PIPE_SOUNDS.put(uuid, insideSound);
            }
        } else {
            if (soundInstance != null)
                soundInstance.startFadeOut();
        }
    }
}
