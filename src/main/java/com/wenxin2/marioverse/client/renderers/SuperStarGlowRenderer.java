package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.wenxin2.marioverse.Marioverse;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

public class SuperStarGlowRenderer {
    private static final ResourceLocation GLOW_POST_CHAIN = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "shaders/post/super_star_glow.json");
    private static final MultiBufferSource.BufferSource GLOW_BUFFERS = MultiBufferSource.immediate(new ByteBufferBuilder(262144));
    @Nullable
    private static RenderTarget glowTarget;
    @Nullable
    private static PostChain glowChain;
    private static boolean failedToLoad;
    private static boolean capturing;
    private static boolean hasGlow;

    public static boolean isCapturing() {
        return capturing && !failedToLoad;
    }

    public static MultiBufferSource getGlowBuffers() {
        hasGlow = true;
        return GLOW_BUFFERS;
    }

    public static void bindGlowTarget() {
        if (glowTarget != null)
            glowTarget.bindWrite(false);
    }

    public static void unbindGlowTarget() {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            capturing = SuperStarGlowRenderer.prepareTarget();
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
            capturing = false;
        } else if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES && hasGlow) {
            hasGlow = false;
            SuperStarGlowRenderer.drawGlow(event.getPartialTick().getGameTimeDeltaTicks());
        }
    }

    public static void close() {
        if (glowChain != null)
            glowChain.close();
        if (glowTarget != null)
            glowTarget.destroyBuffers();
        glowChain = null;
        glowTarget = null;
        failedToLoad = false;
    }

    private static boolean prepareTarget() {
        if (failedToLoad)
            return false;

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();

        if (glowTarget == null) {
            glowTarget = new TextureTarget(mainTarget.width, mainTarget.height, true, Minecraft.ON_OSX);
            glowTarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            try {
                glowChain = new PostChain(minecraft.getTextureManager(), minecraft.getResourceManager(), glowTarget, GLOW_POST_CHAIN);
                glowChain.resize(mainTarget.width, mainTarget.height);
            } catch (IOException | RuntimeException exception) {
                Marioverse.LOGGER.warn("Failed to load super star glow shader {}", GLOW_POST_CHAIN, exception);
                SuperStarGlowRenderer.close();
                failedToLoad = true;
                return false;
            }
        } else if (glowTarget.width != mainTarget.width || glowTarget.height != mainTarget.height) {
            glowTarget.resize(mainTarget.width, mainTarget.height, Minecraft.ON_OSX);
            if (glowChain != null)
                glowChain.resize(mainTarget.width, mainTarget.height);
        }

        glowTarget.clear(Minecraft.ON_OSX);
        glowTarget.copyDepthFrom(mainTarget);
        mainTarget.bindWrite(false);
        return true;
    }

    private static void drawGlow(float partialTicks) {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        GLOW_BUFFERS.endBatch();

        if (glowTarget == null || glowChain == null)
            return;

        glowChain.process(partialTicks);
        mainTarget.bindWrite(false);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
        glowTarget.blitToScreen(mainTarget.width, mainTarget.height, false);
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        mainTarget.bindWrite(true);
    }
}
