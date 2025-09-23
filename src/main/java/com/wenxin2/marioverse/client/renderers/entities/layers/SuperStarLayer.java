package com.wenxin2.marioverse.client.renderers.entities.layers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wenxin2.marioverse.client.renderers.SuperStarRenderType;
import com.wenxin2.marioverse.registries.TextureRegistry;
import com.wenxin2.marioverse.utils.AbilitiesHandler;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import java.awt.Color;
import net.minecraft.Util;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;

public class SuperStarLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private final LivingEntityRenderer<T, M> parentRenderer;

    public SuperStarLayer(LivingEntityRenderer<?, ?> parentRenderer) {
        super((LivingEntityRenderer<T, M>) parentRenderer);
        this.parentRenderer = (LivingEntityRenderer<T, M>) parentRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        ShaderInstance shader = SuperStarRenderType.SUPER_STAR_SHADER;
        if (entity instanceof AbilitiesHandler handler && handler.mv$hasSuperStar()) {
            if (shader != null) {
                float time = (entity.level().getGameTime() + partialTicks) * 0.1F;
                shader.safeGetUniform("Time").set(time);
            }

            ResourceLocation texture = this.getTextureLocation(entity);
            VertexConsumer consumer = bufferSource.getBuffer(SuperStarRenderType.superStar(texture));

            poseStack.pushPose();
                this.getParentModel().renderToBuffer(poseStack, consumer, 0xF000F0,
                        LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
            poseStack.popPose();
        }
    }

    public RenderType get() {
        return RenderType.create("super_star_layer", DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS,
                256, true, true,
                RenderType.CompositeState.builder().setShaderState(RenderType.RENDERTYPE_ENTITY_GLINT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(TextureRegistry.SUPER_STAR_OVERLAY, false, false))
                        .setWriteMaskState(RenderType.COLOR_DEPTH_WRITE)
                        .setCullState(RenderType.NO_CULL).setDepthTestState(RenderType.EQUAL_DEPTH_TEST)
                        .setTransparencyState(RenderType.GLINT_TRANSPARENCY)
                        .setTexturingState(RAINBOW_TEXTURING)
                        .setOverlayState(RenderType.OVERLAY)
                        .createCompositeState(true));
    }

    protected static final RenderStateShard.TexturingStateShard RAINBOW_TEXTURING
            = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> setupRainbowTexturing(1.2F, 4L),
            RenderSystem::resetTextureMatrix);

    private static void setupRainbowTexturing(float in, long time) {
        long i = Util.getMillis() * time;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        Matrix4f matrix4f = (new Matrix4f()).translation(0, f1, 0.0F);
        matrix4f.scale(in);
        RenderSystem.setTextureMatrix(matrix4f);
    }
}
