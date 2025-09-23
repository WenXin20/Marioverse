package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.awt.*;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class SuperStarRenderType extends RenderType {
    public static ShaderInstance SUPER_STAR_SHADER;
    private static final Function<ResourceLocation, RenderType> SUPER_STAR;

    public SuperStarRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                               boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

//    protected static final RenderStateShard.TexturingStateShard RAINBOW_TEXTURING
//            = new RenderStateShard.TexturingStateShard("entity_glint_texturing", () -> setupRainbowTexturing(1.2F, 4L),
//            RenderSystem::resetTextureMatrix);

    private static void setupRainbowTexturing(float in, long time, float r, float g, float b, float a) {
        long i = Util.getMillis() * time;
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        Matrix4f matrix4f = (new Matrix4f()).translation(0, f1, 0.0F);
        matrix4f.scale(in);
        RenderSystem.setTextureMatrix(matrix4f);
        RenderSystem.setShaderColor(r, g, b, a);
    }

    private static Vector3f hsbToRgb(float hue, float saturation, float brightness) {
        int rgb = Color.HSBtoRGB(hue, saturation, brightness);
        return new Vector3f(
                ((rgb >> 16) & 0xFF) / 255f,
                ((rgb >> 8) & 0xFF) / 255f,
                (rgb & 0xFF) / 255f
        );
    }

    public static RenderType superStar(ResourceLocation texture) {
        return SUPER_STAR.apply(texture);
    }

//    public static RenderType get() {
//        return create("super_star_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS,
//                256, true, true,
//                RenderType.CompositeState.builder().setShaderState(new ShaderStateShard(() -> SUPER_STAR_SHADER))
////                        .setTextureState(new RenderStateShard.TextureStateShard(TextureRegistry.SUPER_STAR_OVERLAY, false, false))
//                        .setWriteMaskState(COLOR_DEPTH_WRITE)
//                        .setCullState(NO_CULL).setDepthTestState(EQUAL_DEPTH_TEST)
//                        .setTransparencyState(GLINT_TRANSPARENCY)
//                        .setOverlayState(OVERLAY)
//                        .createCompositeState(true));
//    }

    static {
        SUPER_STAR = Util.memoize(texture -> {
            RenderType.CompositeState state = RenderType.CompositeState.builder()
                    .setCullState(NO_CULL)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .setOutputState(OUTLINE_TARGET)
                    .setShaderState(new ShaderStateShard(() -> SUPER_STAR_SHADER))
                    .setTextureState(new TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .createCompositeState(RenderType.OutlineProperty.IS_OUTLINE);
            return RenderType.create("super_star_layer", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, true, true, state);
        });
    }
}
