package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.wenxin2.marioverse.mixin.CompositeRenderTypeAccessor;
import com.wenxin2.marioverse.mixin.CompositeStateAccessor;
import com.wenxin2.marioverse.mixin.EmptyTextureStateShardAccessor;
import com.wenxin2.marioverse.registries.ConfigRegistry;
import com.wenxin2.marioverse.registries.DataAttachmentRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Vector3f;

public class SuperStarBufferSource implements MultiBufferSource {
    private final LivingEntity entity;
    private final MultiBufferSource delegate;
    private final float partialTicks;
    private final Map<RenderType, SuperStarVertexCapture> captures = new LinkedHashMap<>();

    public SuperStarBufferSource(LivingEntity entity, MultiBufferSource delegate, float partialTicks) {
        this.entity = entity;
        this.delegate = delegate;
        this.partialTicks = partialTicks;
    }

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        VertexConsumer buffer = this.delegate.getBuffer(renderType);
        SuperStarVertexCapture capture = this.captures.get(renderType);

        if (capture == null) {
            Optional<ResourceLocation> texture = SuperStarBufferSource.getCapturableTexture(renderType);
            if (texture.isEmpty())
                return buffer;
            capture = new SuperStarVertexCapture(texture.get());
            this.captures.put(renderType, capture);
        }
        return VertexMultiConsumer.create(capture, buffer);
    }

    public static boolean hasSuperStarEffect(LivingEntity entity) {
        return entity.getData(DataAttachmentRegistry.HAS_SUPER_STAR)
                || (!ConfigRegistry.DISABLE_JEB_SHADER.get() && entity.hasCustomName()
                        && "jeb_".equals(entity.getName().getString()));
    }

    public static MultiBufferSource wrap(LivingEntity entity, MultiBufferSource bufferSource, float partialTicks) {
        if (bufferSource instanceof SuperStarBufferSource || entity.isInvisible() || !SuperStarBufferSource.hasSuperStarEffect(entity))
            return bufferSource;
        return new SuperStarBufferSource(entity, bufferSource, partialTicks);
    }

    public void flush(LivingEntity renderedEntity, PoseStack poseStack) {
        if (renderedEntity != this.entity || this.captures.isEmpty())
            return;

        if (this.delegate instanceof MultiBufferSource.BufferSource bufferSource)
            this.captures.keySet().forEach(bufferSource::endBatch);

        float scale = poseStack.last().pose().getScale(new Vector3f()).x();
        float time = (this.entity.level().getGameTime() + this.partialTicks) * 0.2F;
        SuperStarRenderType.setUniforms(time, scale > 1.0E-4F ? 1.0F / scale : 1.0F);

        for (SuperStarVertexCapture capture : this.captures.values())
            capture.replay(this.delegate.getBuffer(SuperStarRenderType.superStar(capture.getTexture())));

        if (SuperStarGlowRenderer.isCapturing()) {
            MultiBufferSource glowBuffers = SuperStarGlowRenderer.getGlowBuffers();
            for (SuperStarVertexCapture capture : this.captures.values())
                capture.replay(glowBuffers.getBuffer(SuperStarRenderType.superStarGlow(capture.getTexture())));
        }

        this.captures.clear();
    }

    private static Optional<ResourceLocation> getCapturableTexture(RenderType renderType) {
        if (renderType.format() != DefaultVertexFormat.NEW_ENTITY || renderType.mode() != VertexFormat.Mode.QUADS
                || !(renderType instanceof CompositeRenderTypeAccessor composite))
            return Optional.empty();
        return ((EmptyTextureStateShardAccessor) ((CompositeStateAccessor) (Object) composite.mv$getState())
                .mv$getTextureState()).mv$cutoutTexture();
    }
}
