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
    private static final Function<ResourceLocation, RenderType> SUPER_STAR;

    public SuperStarRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize,
                               boolean affectsCrumbling, boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType superStar(ResourceLocation texture) {
        return SUPER_STAR.apply(texture);
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
                    .setTransparencyState(ADDITIVE_TRANSPARENCY)
                    .createCompositeState(RenderType.OutlineProperty.IS_OUTLINE);
            return RenderType.create("super_star_layer", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, true, true, state);
        });
    }
}
