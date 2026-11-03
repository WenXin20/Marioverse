package com.wenxin2.marioverse.client.renderers;

import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

public class SuperStarVertexCapture implements VertexConsumer {
    private static final int STRIDE = 8;
    private final ResourceLocation texture;
    private final RenderStateShard.LayeringStateShard layering;
    private final FloatArrayList vertices = new FloatArrayList();
    private final IntArrayList colors = new IntArrayList();
    private int currentVertex = -1;

    public SuperStarVertexCapture(ResourceLocation texture, RenderStateShard.LayeringStateShard layering) {
        this.texture = texture;
        this.layering = layering;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public RenderStateShard.LayeringStateShard getLayering() {
        return this.layering;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z) {
        this.currentVertex = this.vertices.size();
        this.vertices.add(x);
        this.vertices.add(y);
        this.vertices.add(z);
        this.colors.add(-1);
        this.vertices.add(0.0F);
        this.vertices.add(0.0F);
        this.vertices.add(0.0F);
        this.vertices.add(1.0F);
        this.vertices.add(0.0F);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha) {
        if (this.currentVertex >= 0)
            this.colors.set(this.currentVertex / STRIDE, FastColor.ARGB32.color(alpha, red, green, blue));
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v) {
        if (this.currentVertex >= 0) {
            this.vertices.set(this.currentVertex + 3, u);
            this.vertices.set(this.currentVertex + 4, v);
        }
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v) {
        return this;
    }

    @Override
    public VertexConsumer setNormal(float normalX, float normalY, float normalZ) {
        if (this.currentVertex >= 0) {
            this.vertices.set(this.currentVertex + 5, normalX);
            this.vertices.set(this.currentVertex + 6, normalY);
            this.vertices.set(this.currentVertex + 7, normalZ);
        }
        return this;
    }

    public void replay(VertexConsumer consumer) {
        for (int i = 0; i + STRIDE <= this.vertices.size(); i += STRIDE) {
            consumer.addVertex(this.vertices.getFloat(i), this.vertices.getFloat(i + 1), this.vertices.getFloat(i + 2),
                    this.colors.getInt(i / STRIDE), this.vertices.getFloat(i + 3), this.vertices.getFloat(i + 4),
                    OverlayTexture.NO_OVERLAY, LightTexture.FULL_BRIGHT,
                    this.vertices.getFloat(i + 5), this.vertices.getFloat(i + 6), this.vertices.getFloat(i + 7));
        }
    }
}
