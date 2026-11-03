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
import org.lwjgl.opengl.GL30;

public class SuperStarGlowRenderer {
    private static final ResourceLocation GLOW_POST_CHAIN = ResourceLocation
            .fromNamespaceAndPath(Marioverse.MOD_ID, "shaders/post/super_star_glow.json");
    private static final MultiBufferSource.BufferSource GLOW_BUFFERS = MultiBufferSource.immediate(new ByteBufferBuilder(262144));
    @Nullable
    private static RenderTarget glowTarget;
    @Nullable
    private static PostChain glowChain;
    private static int silhouetteFramebuffer = -1;
    private static int attachedColorTexture = -1;
    private static int attachedDepthTexture = -1;
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
        if (glowTarget != null && silhouetteFramebuffer >= 0) {
            GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, silhouetteFramebuffer);
            GlStateManager._viewport(0, 0, glowTarget.width, glowTarget.height);
        }
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
        if (silhouetteFramebuffer >= 0)
            GlStateManager._glDeleteFramebuffers(silhouetteFramebuffer);
        silhouetteFramebuffer = -1;
        attachedColorTexture = -1;
        attachedDepthTexture = -1;
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
            glowTarget = new TextureTarget(mainTarget.width, mainTarget.height, false, Minecraft.ON_OSX);
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

        if (!SuperStarGlowRenderer.attachMainDepth(mainTarget)) {
            Marioverse.LOGGER.warn("Failed to attach the world depth buffer to the super star glow target, disabling the glow");
            SuperStarGlowRenderer.close();
            failedToLoad = true;
            mainTarget.bindWrite(false);
            return false;
        }

        glowTarget.clear(Minecraft.ON_OSX);
        mainTarget.bindWrite(false);
        return true;
    }

    private static boolean attachMainDepth(RenderTarget mainTarget) {
        if (glowTarget == null)
            return false;

        int colorTexture = glowTarget.getColorTextureId();
        int depthTexture = mainTarget.getDepthTextureId();
        if (silhouetteFramebuffer >= 0 && colorTexture == attachedColorTexture && depthTexture == attachedDepthTexture)
            return true;

        if (silhouetteFramebuffer < 0)
            silhouetteFramebuffer = GlStateManager.glGenFramebuffers();

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, silhouetteFramebuffer);
        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, colorTexture, 0);
        GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_TEXTURE_2D, depthTexture, 0);
        attachedColorTexture = colorTexture;
        attachedDepthTexture = depthTexture;
        return GlStateManager.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) == GL30.GL_FRAMEBUFFER_COMPLETE;
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
