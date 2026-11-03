package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

public class SuperStarRenderType extends RenderType {
    public static ShaderInstance SUPER_STAR_SHADER;
    public static ShaderInstance SUPER_STAR_GLOW_SHADER;
    private static final OutputStateShard SUPER_STAR_GLOW_TARGET = new OutputStateShard("super_star_glow_target",
            SuperStarGlowRenderer::bindGlowTarget, SuperStarGlowRenderer::unbindGlowTarget);
    private static final Function<ResourceLocation, RenderType> SUPER_STAR;
    private static final Function<ResourceLocation, RenderType> SUPER_STAR_GLOW;

    public SuperStarRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                               boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType superStar(ResourceLocation texture) {
        return SUPER_STAR.apply(texture);
    }

    public static RenderType superStarGlow(ResourceLocation texture) {
        return SUPER_STAR_GLOW.apply(texture);
    }

    public static void setUniforms(float time, float positionScale) {
        for (ShaderInstance shader : new ShaderInstance[]{SUPER_STAR_SHADER, SUPER_STAR_GLOW_SHADER}) {
            if (shader != null) {
                shader.safeGetUniform("Time").set(time);
                shader.safeGetUniform("PositionScale").set(positionScale);
            }
        }
    }

    static {
        SUPER_STAR = Util.memoize(texture -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setCullState(NO_CULL)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .setOutputState(MAIN_TARGET)
                    .setShaderState(new ShaderStateShard(() -> SUPER_STAR_SHADER))
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(RenderType.OutlineProperty.NONE);
            return RenderType.create("super_star_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, state);
        });
        SUPER_STAR_GLOW = Util.memoize(texture -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setCullState(NO_CULL)
                    .setDepthTestState(LEQUAL_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .setOutputState(SUPER_STAR_GLOW_TARGET)
                    .setLayeringState(POLYGON_OFFSET_LAYERING)
                    .setShaderState(new ShaderStateShard(() -> SUPER_STAR_GLOW_SHADER))
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setWriteMaskState(COLOR_WRITE)
                    .createCompositeState(RenderType.OutlineProperty.NONE);
            return RenderType.create("super_star_glow_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true, state);
        });
    }
}
