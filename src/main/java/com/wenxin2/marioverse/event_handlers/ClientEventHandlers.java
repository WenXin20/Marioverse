package com.wenxin2.marioverse.event_handlers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.client.renderers.SuperStarRenderType;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.sounds.FadeInAndOutSoundInstance;
import com.wenxin2.marioverse.sounds.FadingSoundInstance;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandlers {
    public static final Map<UUID, FadeInAndOutSoundInstance> ACTIVE_PIPE_SOUNDS = new HashMap<>();

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "super_star_shader"),
                DefaultVertexFormat.POSITION_TEX_COLOR
        ), shader -> SuperStarRenderType.SUPER_STAR_SHADER = shader);
    }

    @SubscribeEvent
    public static void postEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUUID();

        if (MarioverseEventHandlers.PENDING_EXIT_SOUND.remove(uuid) != null) {
            entity.playSound(SoundRegistry.CLEAR_PIPE_EXIT.get(), 1.0F, 1.0F);
            ACTIVE_PIPE_SOUNDS.remove(uuid);
        }
    }

    @SubscribeEvent
    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUUID();

        if (ACTIVE_PIPE_SOUNDS.get(uuid) != null)
            ACTIVE_PIPE_SOUNDS.get(uuid).startFadeOut();
    }

    @SubscribeEvent
    public static void preEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUUID();
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockState state = world.getBlockState(pos);
        boolean inClearPipe = state.getBlock() instanceof ClearWarpPipeBlock;
        boolean isEntrance = state.hasProperty(ClearWarpPipeBlock.ENTRANCE) && state.getValue(ClearWarpPipeBlock.ENTRANCE);

        FadeInAndOutSoundInstance insideSound = new FadeInAndOutSoundInstance(entity, SoundRegistry.CLEAR_PIPE_INSIDE.get(),
                SoundSource.BLOCKS, 20, 10);

        if (entity instanceof LivingEntity livingEntity && entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)
                && !entity.getData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME)) {
            Minecraft.getInstance().getSoundManager().play(new FadingSoundInstance(livingEntity, SoundRegistry.SUPER_STAR_THEME.get(),
                    SoundSource.AMBIENT, entity.getRandom(), entity.getData(DataAttachmentRegistry.SUPER_STAR_COOLDOWN), 100));
            entity.setData(DataAttachmentRegistry.PLAYED_SUPER_STAR_THEME, true);
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND) && inClearPipe && isEntrance) {
            entity.playSound(SoundRegistry.CLEAR_PIPE_ENTER.get(), 1.0F, 1.0F);
            entity.setData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND, true);
            entity.setData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND, false);
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND) && !inClearPipe) {
            entity.playSound(SoundRegistry.CLEAR_PIPE_EXIT.get(), 1.0F, 1.0F);
            entity.setData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND, true);
            entity.setData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND, false);
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, fa);
            insideSound.startFadeOut();
            if (ACTIVE_PIPE_SOUNDS.get(uuid) != null)
                ACTIVE_PIPE_SOUNDS.get(uuid).startFadeOut();
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND) && inClearPipe) {
            ACTIVE_PIPE_SOUNDS.put(uuid, insideSound);
            Minecraft.getInstance().getSoundManager().play(insideSound);
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, true);
        }
    }
}
