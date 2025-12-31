package com.wenxin2.marioverse.event_handlers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.wenxin2.marioverse.Marioverse;
import com.wenxin2.marioverse.blocks.ClearWarpPipeBlock;
import com.wenxin2.marioverse.blocks.QuicksandBlock;
import com.wenxin2.marioverse.client.QuicksandOverlay;
import com.wenxin2.marioverse.client.RedQuicksandOverlay;
import com.wenxin2.marioverse.client.renderers.SuperStarRenderType;
import com.wenxin2.marioverse.registries.BlockRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import com.wenxin2.marioverse.registries.SoundRegistry;
import com.wenxin2.marioverse.sounds.FadeInAndOutSoundInstance;
import com.wenxin2.marioverse.sounds.FadingSoundInstance;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = Marioverse.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandlers {
    public static final Map<UUID, FadeInAndOutSoundInstance> ACTIVE_PIPE_SOUNDS = new HashMap<>();
    public static final ResourceLocation QUICKSAND_OVERLAY = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "textures/misc/quicksand_overlay.png");
    public static final ResourceLocation RED_QUICKSAND_OVERLAY = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "textures/misc/red_quicksand_overlay.png");
    public static final ResourceLocation SPLUNKIN_OVERLAY = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "textures/misc/splunkin_pumpkin_blur.png");

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                ResourceLocation.fromNamespaceAndPath(Marioverse.MOD_ID, "super_star_shader"),
                DefaultVertexFormat.POSITION_TEX_COLOR),
                shader -> SuperStarRenderType.SUPER_STAR_SHADER = shader);
    }

    @SubscribeEvent
    public static void onClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(
                new IClientItemExtensions() {
                    @Override
                    public void renderHelmetOverlay(ItemStack stack, Player player, GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
                        renderCustomOverlay(guiGraphics, SPLUNKIN_OVERLAY, 1.0F);
                    }
                }, BlockRegistry.SPLUNKIN_CARVED_PUMPKIN.get().asItem()
        );
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        QuicksandOverlay.clientTick(Minecraft.getInstance());
        RedQuicksandOverlay.clientTick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiLayerEvent.Pre event) {
        GuiGraphics gui = event.getGuiGraphics();
        int w = gui.guiWidth();
        int h = gui.guiHeight();

        float alpha = QuicksandOverlay.getOverlayProgress();
        if (alpha > 0.001F) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1F, 1F, 1F, alpha);

            gui.blit(QUICKSAND_OVERLAY, 0, 0, 0, 0, w, h, w, h);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.disableBlend();
        }

        float redAlpha = RedQuicksandOverlay.getOverlayProgress();
        if (redAlpha > 0.001F) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1F, 1F, 1F, redAlpha);

            gui.blit(RED_QUICKSAND_OVERLAY, 0, 0, 0, 0, w, h, w, h);

            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            RenderSystem.disableBlend();
        }
    }

    private static boolean isInQuicksand(Camera camera) {
        BlockPos pos = BlockPos.containing(camera.getPosition());
        return camera.getEntity().level().getBlockState(pos).getBlock() instanceof QuicksandBlock;
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (!isInQuicksand(event.getCamera())) return;

        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(3.0F);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        Level level = camera.getEntity().level();

        BlockPos pos = BlockPos.containing(camera.getPosition());
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof QuicksandBlock quicksand)) return;

        int rgba = quicksand.getDustColor(state, level, pos);

        event.setRed(((rgba >> 16) & 0xFF) / 255F);
        event.setGreen(((rgba >> 8) & 0xFF) / 255F);
        event.setBlue((rgba & 0xFF) / 255F);
    }

    @SubscribeEvent
    public static void onEntityRemoved(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        UUID uuid = entity.getUUID();
        if (!(event.getLevel() instanceof ClientLevel))
            return;

        if (ACTIVE_PIPE_SOUNDS.get(uuid) != null) {
            ACTIVE_PIPE_SOUNDS.get(uuid).startFadeOut();
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, false);
        }
    }

    @SubscribeEvent
    public static void postEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();

        UUID uuid = entity.getUUID();
        Level world = entity.level();
        BlockPos pos = entity.blockPosition();
        BlockState state = world.getBlockState(pos);
        boolean inClearPipe = state.getBlock() instanceof ClearWarpPipeBlock;
        boolean isEntrance = state.hasProperty(ClearWarpPipeBlock.ENTRANCE) && state.getValue(ClearWarpPipeBlock.ENTRANCE);

        if (!(entity.level() instanceof ClientLevel))
            return;

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
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_ENTER_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_EXIT_PIPE_SOUND)
                && !entity.getData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND) && inClearPipe) {
            FadeInAndOutSoundInstance insideSound = new FadeInAndOutSoundInstance(entity, SoundRegistry.CLEAR_PIPE_INSIDE.get(),
                    SoundSource.BLOCKS, 20, 10);

            ACTIVE_PIPE_SOUNDS.put(uuid, insideSound);
            Minecraft.getInstance().getSoundManager().play(insideSound);
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, true);
        }

        if (entity.getData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND) && !inClearPipe) {
            FadeInAndOutSoundInstance active = ACTIVE_PIPE_SOUNDS.get(uuid);

            if (active != null)
                active.startFadeOut();
            ACTIVE_PIPE_SOUNDS.remove(uuid);
            entity.setData(DataAttachmentRegistry.PLAYED_INSIDE_PIPE_SOUND, false);
        }
    }

    private static void renderCustomOverlay(GuiGraphics guiGraphics, ResourceLocation texture, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        guiGraphics.blit(texture, 0, 0, -90, 0.0F, 0.0F,
                guiGraphics.guiWidth(), guiGraphics.guiHeight(),
                guiGraphics.guiWidth(), guiGraphics.guiHeight());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
