package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class PaintingRippleRenderTypes extends RenderType {
    public static ShaderInstance ripplingPaintingShader;
    private static final Set<RenderType> RIPPLING_TYPES = new ReferenceOpenHashSet<>();
    private static final Set<RenderType> TRANSLUCENT_TYPES = new ReferenceOpenHashSet<>();

    private static final Function<ResourceLocation, RenderType> RIPPLING_SOLID = Util.memoize(texture ->
            PaintingRippleRenderTypes.track(false, RenderType.create("marioverse_rippling_painting_solid", DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS, 1536, true, false, RenderType.CompositeState.builder()
                            .setShaderState(new ShaderStateShard(() -> ripplingPaintingShader))
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setLightmapState(LIGHTMAP)
                            .createCompositeState(true))));

    private static final Function<ResourceLocation, RenderType> RIPPLING_TRANSLUCENT = Util.memoize(texture ->
            PaintingRippleRenderTypes.track(true, RenderType.create("marioverse_rippling_painting_translucent", DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS, 1536, true, true, RenderType.CompositeState.builder()
                            .setShaderState(new ShaderStateShard(() -> ripplingPaintingShader))
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                            .setCullState(NO_CULL)
                            .setLightmapState(LIGHTMAP)
                            .createCompositeState(true))));

    private static final Function<ResourceLocation, RenderType> RIPPLING_OVERLAY = Util.memoize(texture ->
            PaintingRippleRenderTypes.track(false, RenderType.create("marioverse_rippling_painting_overlay", DefaultVertexFormat.NEW_ENTITY,
                    VertexFormat.Mode.QUADS, 1536, true, false, RenderType.CompositeState.builder()
                            .setShaderState(new ShaderStateShard(() -> ripplingPaintingShader))
                            .setTextureState(new TextureStateShard(texture, false, false))
                            .setTransparencyState(NO_TRANSPARENCY)
                            .setLightmapState(LIGHTMAP)
                            .setLayeringState(POLYGON_OFFSET_LAYERING)
                            .createCompositeState(true))));

    private PaintingRippleRenderTypes(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling,
                                      boolean sortOnUpload, Runnable setupState, Runnable clearState) {
        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setupState, clearState);
    }

    public static RenderType ripplingPainting(ResourceLocation texture, boolean translucent) {
        return translucent ? RIPPLING_TRANSLUCENT.apply(texture) : RIPPLING_SOLID.apply(texture);
    }

    public static RenderType ripplingPaintingOverlay(ResourceLocation texture) {
        return RIPPLING_OVERLAY.apply(texture);
    }

    public static boolean isRippling(RenderType renderType) {
        return RIPPLING_TYPES.contains(renderType);
    }

    public static boolean isTranslucent(RenderType renderType) {
        return TRANSLUCENT_TYPES.contains(renderType);
    }

    private static RenderType track(boolean translucent, RenderType renderType) {
        RIPPLING_TYPES.add(renderType);
        if (translucent)
            TRANSLUCENT_TYPES.add(renderType);
        return renderType;
    }
}
