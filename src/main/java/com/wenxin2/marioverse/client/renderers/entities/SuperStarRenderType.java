package com.wenxin2.marioverse.client.renderers.entities;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.wenxin2.marioverse.init.TextureRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.client.renderer.RenderType;

public class SuperStarRenderType extends RenderType {
    public SuperStarRenderType(String s, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean b, boolean b1,
                               Runnable runnable, Runnable aSuper) {
        super(s, vertexFormat, mode, i, b, b1, runnable, aSuper);
    }

    private static final Int2ObjectArrayMap<RenderType> TYPES = new Int2ObjectArrayMap<>();

    public static void clear() {
        TYPES.clear();
    }

    public static RenderType get(int width, int height) {
        return TYPES.computeIfAbsent((width << 16) | (height & 0xFFFF), k -> create("slimed",
                DefaultVertexFormat.NEW_ENTITY,
                VertexFormat.Mode.QUADS,
                256,
                false, true,
                CompositeState.builder()
                        .setTextureState(new TextureStateShard(TextureRegistry.SUPER_STAR_OVERLAY, false, false))
                        .setCullState(NO_CULL)
                        .setOverlayState(OVERLAY)
                        .setLightmapState(LIGHTMAP)
                        .setDepthTestState(EQUAL_DEPTH_TEST)
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .createCompositeState(false)
        ));
    }
}
